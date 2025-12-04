// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.api

import android.content.Context
import androidx.core.content.edit
import com.iproov.kmp.AppContext
import com.iproov.kmp.mapper.toIproov
import com.iproov.kmp.mapper.toIproovState
import com.iproov.kmp.mapper.toIproovUIState
import com.iproov.sdk.api.IProov
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

actual object Iproov {

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    private var sessionStateJob: Job? = null
    private var sessionUIStateJob: Job? = null

    actual val sdkVersion: String = IProov.sdkVersion
    actual val buildNumber: String = IProov.buildNumber

    actual val sessionState: MutableStateFlow<IproovState?> = MutableStateFlow(null)
    actual val uiState: MutableStateFlow<IproovUIState?> = MutableStateFlow(null)

    init {
        setEnvironment(AppContext.get())
    }

    actual suspend fun launchSession(baseUrl: String, token: String, iproovOptions: IproovOptions) {
        val session = IProov.createSession(AppContext.get(), baseUrl, token, iproovOptions.toIproov())
        // Observe first, then start
        observeSessionState(session) { session.start() }
        observeSessionUIState(session)
    }

    actual suspend fun cancelSession() {
        IProov.session?.cancel()
        sessionStateJob?.cancel()
        sessionState.value = null
    }

    private fun observeSessionUIState(session: IProov.Session) {
        sessionUIStateJob?.cancel()
        sessionUIStateJob = coroutineScope.launch(Dispatchers.IO) {
            session.uiState
                .collect { sessionUIState ->
                    if (sessionUIStateJob?.isActive == true) {
                        withContext(Dispatchers.Main) {
                            uiState.value = sessionUIState.toIproovUIState()
                        }
                    }
                }
        }
    }

    private fun observeSessionState(session: IProov.Session, whenReady: (() -> Unit)? = null) {
        sessionStateJob?.cancel()
        sessionStateJob = coroutineScope.launch {
            session.state
                .onSubscription { whenReady?.invoke() }
                .collect { state ->
                    if (sessionStateJob?.isActive == true) {
                        withContext(Dispatchers.Main) {
                            sessionState.value = state.toIproovState()
                        }
                    }
                }
        }
    }

    private fun setEnvironment(context: Context) {
        val sharedPref = context.getSharedPreferences("iproov_environment_prefs", Context.MODE_PRIVATE)
        sharedPref.edit {
            putString("environment", "kotlin_multiplatform")
        }
    }
}

