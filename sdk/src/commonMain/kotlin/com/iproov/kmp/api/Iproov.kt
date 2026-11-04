// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.api

import kotlinx.coroutines.flow.MutableStateFlow

expect object IProov {
    val sdkVersion: String
    val buildNumber: String

    val sessionState: MutableStateFlow<IProovState?>
    val uiState: MutableStateFlow<IProovUIState?>

    suspend fun launchSession(baseUrl: String, token: String, iproovOptions: IProovOptions)
    suspend fun cancelSession()
}