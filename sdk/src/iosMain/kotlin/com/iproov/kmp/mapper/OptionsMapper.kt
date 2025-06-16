// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.mapper

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
import com.iproov.kmp.api.IproovOptions
import com.iproov.kmp.api.LineDrawingStyle
import com.iproov.kmp.api.NaturalStyle
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
fun IproovOptions.toIproov(): IPOptions {
    val iosOptions = IPOptions()

    iosOptions.setTitle(this.title)
    iosOptions.setTitleTextColor(this.titleTextColor.toUIColor())
    iosOptions.setHeaderBackgroundColor(this.headerBackgroundColor.toUIColor())

    iosOptions.setFilter(this.filter.toIproov())

    iosOptions.setPromptTextColor(this.promptTextColor.toUIColor())
    iosOptions.setPromptBackgroundColor(this.promptBackgroundColor.toUIColor())
    iosOptions.setPromptRoundedCorners(this.promptRoundedCorners)

    iosOptions.setDisableExteriorEffects(this.disableExteriorEffects)

    iosOptions.setTimeout(this.timeoutSecs.toDouble())

    iosOptions.setSurroundColor(this.surroundColor.toUIColor())

    iosOptions.setCertificates(this.certificates.map { it.spki })

    if (this.logo != null)
        iosOptions.setLogoImage(byteArrayToUIImage(this.logo!!))

    if (this.fontPath != null)
        iosOptions.setFont(this.fontPath!!)

    // Close button ----
    iosOptions.setCloseButtonTintColor(this.closeButton.colorTint.toUIColor())
    if (this.closeButton.icon != null)
        iosOptions.setCloseButtonImage(byteArrayToUIImage(this.closeButton.icon!!)!!)

    // GPA ----
    val ipGenuinePresenceAssuranceOptions = this.genuinePresenceAssurance.readyOvalStrokeColor.toUIColor()
    val ipNotReadyOvalStrokeColor = this.genuinePresenceAssurance.notReadyOvalStrokeColor.toUIColor()
    val scanningPrompts = this.genuinePresenceAssurance.scanningPrompts
    iosOptions.setGenuinePresenceAssurance(
        IPGenuinePresenceAssuranceOptions().apply {
            setReadyOvalStrokeColor(ipGenuinePresenceAssuranceOptions)
            setNotReadyOvalStrokeColor(ipNotReadyOvalStrokeColor)
            setScanningPrompts(scanningPrompts)
        }
    )

    // LA ----
    val ipOvalStrokeColor = this.livenessAssurance.ovalStrokeColor.toUIColor()
    val ipCompletedOvalStrokeColor = this.livenessAssurance.completedOvalStrokeColor.toUIColor()
    iosOptions.setLivenessAssurance(
        IPLivenessAssuranceOptions().apply {
            setOvalStrokeColor(ipOvalStrokeColor)
            setCompletedOvalStrokeColor(ipCompletedOvalStrokeColor)
        }
    )

    return iosOptions
}

@OptIn(ExperimentalForeignApi::class)
fun IproovOptions.Filter.toIproov(): IPFilterProtocol {
    return when (val kmpFilter = this) {
        is IproovOptions.Filter.LineDrawingFilter ->
            IPLineDrawingFilter(
                style = kmpFilter.style.toIproov(),
                foregroundColor = kmpFilter.foregroundColor.toUIColor(),
                backgroundColor = kmpFilter.backgroundColor.toUIColor()
            )

        is IproovOptions.Filter.NaturalFilter ->
            IPNaturalFilter(
                style = kmpFilter.style.toIproov()
            )
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
