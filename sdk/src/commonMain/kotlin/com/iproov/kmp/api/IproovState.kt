// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.api


data class FailureReason(
    val feedbackCode: String,
    val description: String
)

enum class Canceler { USER, INTEGRATION }

sealed class IProovState(val isFinal: Boolean) {
    object Starting : IProovState(false)
    object Connecting : IProovState(false)
    object Connected : IProovState(false)
    data class Processing(val progress: Double, val message: String) : IProovState(false) {
        override fun toString(): String =
            "${super.toString()} $progress $message"
    }

    object Success : IProovState(true)

    data class Failure(val reasons: List<FailureReason>) : IProovState(true) {
        override fun toString(): String = "${super.toString()} $reasons"
    }

    data class Canceled(val canceler: Canceler) : IProovState(true)
    data class Error(val exception: IProovException) : IProovState(true) {
        override fun toString(): String = "${super.toString()} $exception"
    }

    override fun toString(): String {
        return "IProovState [${this::class.simpleName}]"
    }
}

sealed class IProovUIState {
    object NotStarted : IProovUIState()
    object Started : IProovUIState()
    object Ended : IProovUIState()

    override fun toString(): String {
        return "IProovUIState [${this::class.simpleName}]"
    }
}