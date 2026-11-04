// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.example.viewmodel


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iproov.example.util.Credentials
import com.iproov.kmp.api.IProov
import com.iproov.kmp.api.IProovOptions
import com.iproov.kmp.api.IProovState
import com.iproov.kmp.api_client.AssuranceType
import com.iproov.kmp.api_client.ClaimType
import com.iproov.kmp.api_client.impl.ApiClientImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClaimViewModel : ViewModel() {

    private val apiClient = ApiClientImpl(
        baseUrl = Credentials.FUEL_URL,
        apiKey = Credentials.API_KEY,
        secret = Credentials.SECRET
    )

    private val _iProovState: MutableState<IProovState?> = mutableStateOf(null)
    val iProovState: State<IProovState?> = _iProovState

    private val _message: MutableState<String?> = mutableStateOf(null)
    val message: State<String?> = _message

    init {
        collectIProovState()
    }

    fun launchIProov(claimType: ClaimType, username: String, assuranceType: AssuranceType) {

        if (Credentials.API_KEY.isEmpty() || Credentials.SECRET.isEmpty()) {
            _message.value = "You must set the API_KEY and SECRET values in the Credentials.kt file!"
            return
        }

        _message.value = "Fetching token..."

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = apiClient.getToken(
                    assuranceType = assuranceType,
                    claimType = claimType,
                    userID = username,
                    resource = null
                )

                startScan(token)

                _message.value = null
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    ex.printStackTrace()
                    _message.value = "Failed to get token"
                }
            }
        }
    }

    fun resetState() {
        _iProovState.value = null
    }

    fun sdkVersion(): String = IProov.sdkVersion

    private fun collectIProovState() {
        viewModelScope.launch {
            IProov.sessionState.collect {
                _iProovState.value = it
            }
        }
    }

    private fun startScan(token: String) {
        viewModelScope.launch {
            val options = IProovOptions()

            IProov.launchSession(Credentials.BASE_URL, token, options)
        }
    }
}

