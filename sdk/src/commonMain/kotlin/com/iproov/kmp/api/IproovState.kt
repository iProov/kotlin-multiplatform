// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.api


data class SuccessResult(
    val frame: ByteArray?,
)

data class FailureResult(
    val reason: String,
    val feedbackCode: String,
    val frame: ByteArray?,
)

enum class Canceler { USER, INTEGRATION }

sealed class IproovState(val isFinal: Boolean) {
    object Starting : IproovState(false)
    object Connecting : IproovState(false)
    object Connected : IproovState(false)
    data class Processing(val progress: Double, val message: String) : IproovState(false) {
        override fun toString(): String =
            "${super.toString()} $progress $message"
    }

    data class Success(val successResult: SuccessResult) : IproovState(true) {
        override fun toString(): String = super.toString()
    }

    data class Failure(val failureResult: FailureResult) : IproovState(true) {
        override fun toString(): String = "${super.toString()} $failureResult"
    }

    data class Canceled(val canceler: Canceler) : IproovState(true)
    data class Error(val exception: IProovException) : IproovState(true) {
        override fun toString(): String = "${super.toString()} $exception"
    }

    override fun toString(): String {
        return "IProovState [${this::class.simpleName}]"
    }
}