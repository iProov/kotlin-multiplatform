// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.mapper

import com.iproov.kmp.AppContext
import com.iproov.kmp.api.CameraException
import com.iproov.kmp.api.CameraPermissionException
import com.iproov.kmp.api.Canceler
import com.iproov.kmp.api.CaptureAlreadyActiveException
import com.iproov.kmp.api.FailureReason
import com.iproov.kmp.api.IProovException
import com.iproov.kmp.api.InvalidOptionsException
import com.iproov.kmp.api.IProovState
import com.iproov.kmp.api.IProovUIState
import com.iproov.kmp.api.MultiWindowUnsupportedException
import com.iproov.kmp.api.NetworkException
import com.iproov.kmp.api.ServerException
import com.iproov.kmp.api.UnexpectedErrorException
import com.iproov.kmp.api.UnsupportedDeviceException
import com.iproov.kmp.api.UnsupportedOSVersionException
import com.iproov.kmp.api.UnsupportedSDKVersionException
import com.iproov.sdk.api.IProov
import com.iproov.sdk.api.IProov.UIState


fun IProov.State.toIProovState(): IProovState {
    return when (this) {
        IProov.State.Starting -> IProovState.Starting
        IProov.State.Connecting -> IProovState.Connecting
        IProov.State.Connected -> IProovState.Connected
        is IProov.State.Processing -> IProovState.Processing(this.progress, this.message)
        IProov.State.Success -> IProovState.Success

        is IProov.State.Failure -> {
            val context = AppContext.get()
            val reasons = this.reasons.map { FailureReason(it.feedbackCode, context.getString(it.description)) }

            IProovState.Failure(reasons)
        }

        is IProov.State.Canceled -> {
            val canceler = if (this.canceler.name.lowercase() == "user") Canceler.USER else Canceler.INTEGRATION

            IProovState.Canceled(canceler)
        }

        is IProov.State.Error -> {
            IProovState.Error(this.exception.toIProovException())
        }
    }
}

fun UIState.toIProovUIState(): IProovUIState {
    return when (this) {
        UIState.NotStarted -> IProovUIState.NotStarted
        UIState.Started -> IProovUIState.Started
        UIState.Ended -> IProovUIState.Ended
    }
}


private fun com.iproov.sdk.api.exception.IProovException.toIProovException(): IProovException {
    return when (this) {
        is com.iproov.sdk.api.exception.CaptureAlreadyActiveException -> CaptureAlreadyActiveException(this.reason)
        is com.iproov.sdk.api.exception.NetworkException -> NetworkException(this.reason, this.message)
        is com.iproov.sdk.api.exception.CameraPermissionException -> CameraPermissionException(this.reason)
        is com.iproov.sdk.api.exception.ServerException -> ServerException(this.reason, this.message)
        is com.iproov.sdk.api.exception.CameraException -> CameraException(this.reason, this.message)
        is com.iproov.sdk.api.exception.MultiWindowUnsupportedException -> MultiWindowUnsupportedException(this.reason)
        is com.iproov.sdk.api.exception.UnsupportedDeviceException -> UnsupportedDeviceException(this.reason)
        is com.iproov.sdk.api.exception.UnsupportedOSVersionException -> UnsupportedOSVersionException(this.reason)
        is com.iproov.sdk.api.exception.UnsupportedSDKVersionException -> UnsupportedSDKVersionException(this.reason)
        is com.iproov.sdk.api.exception.InvalidOptionsException -> InvalidOptionsException(this.reason)
        else -> UnexpectedErrorException(this.reason, this.message)
    }
}