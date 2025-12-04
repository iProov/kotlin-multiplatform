// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.api

import kotlinx.coroutines.flow.MutableStateFlow

expect object Iproov {
    val sdkVersion: String
    val buildNumber: String

    val sessionState: MutableStateFlow<IproovState?>
    val uiState: MutableStateFlow<IproovUIState?>

    suspend fun launchSession(baseUrl: String, token: String, iproovOptions: IproovOptions)
    suspend fun cancelSession()
}