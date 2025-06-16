// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.mapper

import android.graphics.Bitmap
import com.iproov.sdk.api.IProov
import com.iproov.kmp.api.CameraException
import com.iproov.kmp.api.CameraPermissionException
import com.iproov.kmp.api.Canceler
import com.iproov.kmp.api.CaptureAlreadyActiveException
import com.iproov.kmp.api.FaceDetectorException
import com.iproov.kmp.api.FailureResult
import com.iproov.kmp.api.IProovException
import com.iproov.kmp.api.InvalidOptionsException
import com.iproov.kmp.api.IproovState
import com.iproov.kmp.api.MultiWindowUnsupportedException
import com.iproov.kmp.api.NetworkException
import com.iproov.kmp.api.ServerException
import com.iproov.kmp.api.SuccessResult
import com.iproov.kmp.api.UnexpectedErrorException
import com.iproov.kmp.api.UnsupportedDeviceException
import java.io.ByteArrayOutputStream


fun IProov.State.toIproovState(): IproovState {
    return when (this) {
        IProov.State.Starting -> IproovState.Starting
        IProov.State.Connecting -> IproovState.Connecting
        IProov.State.Connected -> IproovState.Connected
        is IProov.State.Processing -> IproovState.Processing(this.progress, this.message)
        is IProov.State.Success -> {
            val frame = if (this.successResult.frame == null) null else bitmapToByteArray(this.successResult.frame!!)
            IproovState.Success(SuccessResult(frame))
        }

        is IProov.State.Failure -> {
            val frame = if (this.failureResult.frame == null) null else bitmapToByteArray(this.failureResult.frame!!)
            val reason = this.failureResult.reason

            IproovState.Failure(FailureResult(reason.name, reason.feedbackCode, frame))
        }

        is IProov.State.Canceled -> {
            val canceler = if (this.canceler.name.lowercase() == "user") Canceler.USER else Canceler.INTEGRATION

            IproovState.Canceled(canceler)
        }

        is IProov.State.Error -> {
            IproovState.Error(this.exception.toIproovException())
        }
    }
}

private fun com.iproov.sdk.api.exception.IProovException.toIproovException(): IProovException {
    return when (this) {
        is com.iproov.sdk.api.exception.CaptureAlreadyActiveException -> CaptureAlreadyActiveException(this.reason)
        is com.iproov.sdk.api.exception.NetworkException -> NetworkException(this.reason, this.message)
        is com.iproov.sdk.api.exception.CameraPermissionException -> CameraPermissionException(this.reason)
        is com.iproov.sdk.api.exception.ServerException -> ServerException(this.reason, this.message)
        is com.iproov.sdk.api.exception.CameraException -> CameraException(this.reason, this.message)
        is com.iproov.sdk.api.exception.MultiWindowUnsupportedException -> MultiWindowUnsupportedException(this.reason)
        is com.iproov.sdk.api.exception.FaceDetectorException -> FaceDetectorException(this.reason, this.message)
        is com.iproov.sdk.api.exception.UnsupportedDeviceException -> UnsupportedDeviceException(this.reason)
        is com.iproov.sdk.api.exception.InvalidOptionsException -> InvalidOptionsException(this.reason)
        else -> UnexpectedErrorException(this.reason, this.message)
    }
}

private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}