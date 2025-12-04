// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.mapper

import com.iproov.kmp.api.IproovOptions
import com.iproov.kmp.api.LineDrawingStyle
import com.iproov.kmp.api.NaturalStyle
import com.iproov.kmp.util.assignIfNotNull
import com.iproov.sdk.IPFilterProtocol
import com.iproov.sdk.IPGenuinePresenceAssuranceOptions
import com.iproov.sdk.IPLineDrawingFilter
import com.iproov.sdk.IPLineDrawingFilterStyle
import com.iproov.sdk.IPLineDrawingFilterStyleClassic
import com.iproov.sdk.IPLineDrawingFilterStyleShaded
import com.iproov.sdk.IPLineDrawingFilterStyleVibrant
import com.iproov.sdk.IPLivenessAssuranceOptions
import com.iproov.sdk.IPNaturalFilter
import com.iproov.sdk.IPNaturalFilterStyle
import com.iproov.sdk.IPNaturalFilterStyleBlur
import com.iproov.sdk.IPNaturalFilterStyleClear
import com.iproov.sdk.IPOptions
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
fun IproovOptions.toIproov(): IPOptions {
    val iosOptions = IPOptions()

    this.title.assignIfNotNull { iosOptions.setTitle(it) }
    this.titleTextColor.assignIfNotNull { iosOptions.setTitleTextColor(it.toUIColor()) }
    this.headerBackgroundColor.assignIfNotNull { iosOptions.setHeaderBackgroundColor(it.toUIColor()) }

    this.filter.assignIfNotNull { iosOptions.setFilter(it.toIproov()) }

    this.promptTextColor.assignIfNotNull { iosOptions.setPromptTextColor(it.toUIColor()) }
    this.promptBackgroundColor.assignIfNotNull { iosOptions.setPromptBackgroundColor(it.toUIColor()) }
    this.promptRoundedCorners.assignIfNotNull { iosOptions.setPromptRoundedCorners(it) }

    this.disableExteriorEffects.assignIfNotNull { iosOptions.setDisableExteriorEffects(it) }

    this.timeoutSecs.assignIfNotNull { iosOptions.setTimeout(it.toDouble()) }

    this.surroundColor.assignIfNotNull { iosOptions.setSurroundColor(it.toUIColor()) }
    this.certificates.assignIfNotNull { iosOptions.setCertificates(it.map { it.spki }) }

    this.logo.assignIfNotNull { iosOptions.setLogoImage(byteArrayToUIImage(it)) }

    this.fontPath.assignIfNotNull { iosOptions.setFont(it) }

    // Close button ----
    this.closeButton.assignIfNotNull { cb ->
        cb.colorTint.assignIfNotNull { iosOptions.setCloseButtonTintColor(it.toUIColor()) }
        cb.icon.assignIfNotNull { iosOptions.setCloseButtonImage(byteArrayToUIImage(it)!!) }
    }

    // GPA ----
    this.genuinePresenceAssurance.assignIfNotNull { gpa ->
        iosOptions.setGenuinePresenceAssurance(
            IPGenuinePresenceAssuranceOptions().apply {
                gpa.readyOvalStrokeColor.assignIfNotNull { setReadyOvalStrokeColor(it.toUIColor()) }
                gpa.notReadyOvalStrokeColor.assignIfNotNull { setNotReadyOvalStrokeColor(it.toUIColor()) }
                gpa.scanningPrompts.assignIfNotNull { setScanningPrompts(it) }
                gpa.controlXPosition.assignIfNotNull { setControlXPosition(it) }
                gpa.controlYPosition.assignIfNotNull { setControlYPosition(it) }
            }
        )
    }

    // LA ----
    this.livenessAssurance.assignIfNotNull { la ->
        iosOptions.setLivenessAssurance(
            IPLivenessAssuranceOptions().apply {
                la.ovalStrokeColor.assignIfNotNull { setOvalStrokeColor(it.toUIColor()) }
                la.completedOvalStrokeColor.assignIfNotNull { setCompletedOvalStrokeColor(it.toUIColor()) }
            }
        )
    }

    return iosOptions
}

@OptIn(ExperimentalForeignApi::class)
fun IproovOptions.Filter.toIproov(): IPFilterProtocol {
    return when (val kmpFilter = this) {
        is IproovOptions.Filter.LineDrawingFilter -> IPLineDrawingFilter().apply {
            kmpFilter.style.assignIfNotNull { setStyle(it.toIproov()) }
            kmpFilter.foregroundColor.assignIfNotNull { setForegroundColor(it.toUIColor()) }
            kmpFilter.backgroundColor.assignIfNotNull { setBackgroundColor(it.toUIColor()) }
        }

        is IproovOptions.Filter.NaturalFilter ->
            IPNaturalFilter().apply {
                kmpFilter.style.assignIfNotNull { setStyle(it.toIproov()) }
            }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun LineDrawingStyle.toIproov(): IPLineDrawingFilterStyle {
    return when (this) {
        LineDrawingStyle.SHADED -> IPLineDrawingFilterStyleShaded
        LineDrawingStyle.CLASSIC -> IPLineDrawingFilterStyleClassic
        LineDrawingStyle.VIBRANT -> IPLineDrawingFilterStyleVibrant
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NaturalStyle.toIproov(): IPNaturalFilterStyle {
    return when (this) {
        NaturalStyle.BLUR -> IPNaturalFilterStyleBlur
        NaturalStyle.CLEAR -> IPNaturalFilterStyleClear
    }
}
