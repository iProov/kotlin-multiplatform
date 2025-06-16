// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.api

import com.iproov.sdk.IPErrorCode
import com.iproov.sdk.IPSession
import com.iproov.sdk.IProov
import com.iproov.sdk.buildNumber
import com.iproov.sdk.launchWithStreamingURL
import com.iproov.sdk.versionStr
import com.iproov.kmp.mapper.convertUIImageToByteArray
import com.iproov.kmp.mapper.toIproov
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import platform.Foundation.NSURL


@OptIn(ExperimentalForeignApi::class)
actual object Iproov {

    actual val sdkVersion: String = IProov.versionStr()
    actual val buildNumber: String = IProov.buildNumber().toString()

    actual val sessionState: MutableStateFlow<IproovState?> = MutableStateFlow(null)

    private var session: IPSession? = null

    actual suspend fun launchSession(baseUrl: String, token: String, iproovOptions: IproovOptions) {

        val url = NSURL(string = baseUrl)
        val options = iproovOptions.toIproov()

        session = IProov.launchWithStreamingURL(
            streamingURL = url,
            token = token,
            options = options,
            connecting = {
                sessionState.tryEmit(IproovState.Connecting)
            },
            connected = {
                sessionState.tryEmit(IproovState.Connected)
            },
            processing = { progress, message ->
                sessionState.tryEmit(IproovState.Processing(progress, message ?: ""))
            },
            success = {
                val frame = if (it?.frame() == null) null else convertUIImageToByteArray(it.frame()!!)
                sessionState.tryEmit(IproovState.Success(SuccessResult(frame)))
            },
            canceled = {
                sessionState.tryEmit(IproovState.Canceled(Canceler.USER))
            },
            failure = {
                val frame = if (it?.frame() == null) null else convertUIImageToByteArray(it.frame()!!)
                val reason = it?.reason()
                val feedbackCode = it?.localizedDescription() ?: ""

                sessionState.tryEmit(IproovState.Failure(FailureResult(reason.toString(), feedbackCode, frame)))
            },
            error = {
                val description = it?.localizedDescription() ?: ""
                val result = when (it?.code) {
                    IPErrorCode.captureAlreadyActive() -> CaptureAlreadyActiveException(description)
                    IPErrorCode.networkError() -> NetworkException("", description)
                    IPErrorCode.cameraPermissionDenied() -> CameraPermissionException(description)
                    IPErrorCode.serverError() -> ServerException("", description)
                    IPErrorCode.userTimeout() -> UserTimeoutException(description)
                    IPErrorCode.notSupported() -> UnsupportedDeviceException(description)
                    else -> UnexpectedErrorException("", description)
                }

                sessionState.tryEmit(IproovState.Error(result))
            }
        )
    }

    actual suspend fun cancelSession() {
        session?.cancel()
    }
}