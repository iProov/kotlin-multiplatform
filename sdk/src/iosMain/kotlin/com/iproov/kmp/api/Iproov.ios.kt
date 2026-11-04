// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.api

import com.iproov.kmp.mapper.toIProov
import com.iproov.sdk.IPErrorCode
import com.iproov.sdk.IPSession
import com.iproov.sdk.IProov as NativeIProov
import com.iproov.sdk.IProovViewDelegateProtocol
import com.iproov.sdk.buildNumber
import com.iproov.sdk.launchWithStreamingURL
import com.iproov.sdk.versionStr
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import platform.Foundation.NSURL
import platform.Foundation.NSUserDefaults
import platform.darwin.NSObject


@OptIn(ExperimentalForeignApi::class)
actual object IProov {

    actual val sdkVersion: String = NativeIProov.versionStr()
    actual val buildNumber: String = NativeIProov.buildNumber().toString()

    actual val sessionState: MutableStateFlow<IProovState?> = MutableStateFlow(null)
    actual val uiState: MutableStateFlow<IProovUIState?> = MutableStateFlow(null)

    private var session: IPSession? = null

    init {
        setEnvironment()
    }

    actual suspend fun launchSession(baseUrl: String, token: String, iproovOptions: IProovOptions) {
        val url = NSURL(string = baseUrl)
        val options = iproovOptions.toIProov()

        options.setViewDelegate(delegate)

        session = NativeIProov.launchWithStreamingURL(
            streamingURL = url,
            token = token,
            options = options,
            connecting = {
                sessionState.tryEmit(IProovState.Connecting)
            },
            connected = {
                sessionState.tryEmit(IProovState.Connected)
            },
            processing = { progress, message ->
                sessionState.tryEmit(IProovState.Processing(progress, message ?: ""))
            },
            success = {
                sessionState.tryEmit(IProovState.Success)
            },
            canceled = {
                sessionState.tryEmit(IProovState.Canceled(Canceler.USER))
            },
            failure = { feedbackCodeList, descriptionList ->
                val failureReasons = mutableListOf<FailureReason>()
                feedbackCodeList?.forEachIndexed { index, feedbackCode ->
                    failureReasons.add(FailureReason(feedbackCode.toString(), descriptionList?.get(index)?.toString() ?: ""))
                }

                sessionState.tryEmit(IProovState.Failure(failureReasons))
            },
            error = {
                val description = it?.localizedDescription() ?: ""
                val result = when (it?.code) {
                    IPErrorCode.captureAlreadyActive() -> CaptureAlreadyActiveException(description)
                    IPErrorCode.networkError() -> NetworkException("", description)
                    IPErrorCode.cameraPermissionDenied() -> CameraPermissionException(description)
                    IPErrorCode.serverError() -> ServerException("", description)
                    IPErrorCode.userTimeout() -> UserTimeoutException(description)
                    IPErrorCode.notSupported(),
                    IPErrorCode.unsupportedDevice() -> UnsupportedDeviceException(description)
                    IPErrorCode.unsupportedOSVersion() -> UnsupportedOSVersionException(description)
                    IPErrorCode.unsupportedSDKVersion() -> UnsupportedSDKVersionException(description)
                    else -> UnexpectedErrorException("", description)
                }

                sessionState.tryEmit(IProovState.Error(result))
            }
        )
    }


    actual suspend fun cancelSession() {
        session?.cancel()
    }

    private val delegate = object : NSObject(), IProovViewDelegateProtocol {
        override fun willPresentIProovView() {
            uiState.value = IProovUIState.NotStarted
        }

        override fun didPresentIProovView() {
            uiState.value = IProovUIState.Started
        }

        override fun didDismissIProovView() {
            uiState.value = IProovUIState.Ended
        }
    }

    private fun setEnvironment() {
        val userDefaults = NSUserDefaults(suiteName = "iproov_environment_prefs")
        userDefaults.setObject("kotlin_multiplatform", "environment")
    }
}