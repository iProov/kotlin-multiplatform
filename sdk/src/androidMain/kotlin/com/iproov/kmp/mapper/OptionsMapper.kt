// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.mapper

import android.graphics.BitmapFactory
import com.iproov.sdk.api.IProov
import com.iproov.kmp.api.Camera
import com.iproov.kmp.api.IproovOptions
import com.iproov.kmp.api.LineDrawingStyle
import com.iproov.kmp.api.NaturalStyle
import com.iproov.kmp.api.Orientation


fun IproovOptions.toIproov(): IProov.Options {
    val options = IProov.Options()

    options.title = this.title
    options.titleTextColor = this.titleTextColor
    options.headerBackgroundColor = this.headerBackgroundColor

    options.filter = this.filter.toIproov()

    options.promptTextColor = this.promptTextColor
    options.promptBackgroundColor = this.promptBackgroundColor
    options.promptRoundedCorners = this.promptRoundedCorners

    options.disableExteriorEffects = this.disableExteriorEffects
    options.orientation = this.orientation.toIproov()
    options.enableScreenshots = this.enableScreenshots
    options.timeoutSecs = this.timeoutSecs
    options.camera = this.camera.toIproov()
    options.surroundColor = this.surroundColor

    options.certificates = this.certificates.toIproov()

    this.fontPath?.let {
        options.font = IProov.Options.Font.PathFont( it)
    }

    if (this.logo != null)
        options.logo = IProov.Options.Icon.BitmapIcon(
            BitmapFactory.decodeByteArray(this.logo, 0, this.logo!!.size)
        )

    // Close button ----
    this.closeButton.icon?.let {
        options.closeButton.icon = IProov.Options.Icon.BitmapIcon(
            BitmapFactory.decodeByteArray(it, 0, it.size)
        )
    }
    options.closeButton.colorTint = this.closeButton.colorTint

    // GPA ----
    options.genuinePresenceAssurance.notReadyOvalStrokeColor = this.genuinePresenceAssurance.notReadyOvalStrokeColor
    options.genuinePresenceAssurance.readyOvalStrokeColor = this.genuinePresenceAssurance.readyOvalStrokeColor
    options.genuinePresenceAssurance.scanningPrompts = this.genuinePresenceAssurance.scanningPrompts

    // LA ----
    options.livenessAssurance.ovalStrokeColor = this.livenessAssurance.ovalStrokeColor
    options.livenessAssurance.completedOvalStrokeColor = this.livenessAssurance.completedOvalStrokeColor

    return options
}

private fun IproovOptions.Filter.toIproov(): IProov.Options.Filter {
    return when (val kmpFilter = this) {
        is IproovOptions.Filter.LineDrawingFilter -> IProov.Options.Filter.LineDrawingFilter().apply {
            style = kmpFilter.style.toIproov()
            foregroundColor = kmpFilter.foregroundColor
            backgroundColor = kmpFilter.backgroundColor
        }

        is IproovOptions.Filter.NaturalFilter -> IProov.Options.Filter.NaturalFilter().apply {
            style = kmpFilter.style.toIproov()
        }
    }
}

private fun LineDrawingStyle.toIproov(): IProov.LineDrawingStyle {
    return when (this) {
        LineDrawingStyle.SHADED -> IProov.LineDrawingStyle.SHADED
        LineDrawingStyle.CLASSIC -> IProov.LineDrawingStyle.CLASSIC
        LineDrawingStyle.VIBRANT -> IProov.LineDrawingStyle.VIBRANT
    }
}

private fun NaturalStyle.toIproov(): IProov.NaturalStyle {
    return when (this) {
        NaturalStyle.BLUR -> IProov.NaturalStyle.BLUR
        NaturalStyle.CLEAR -> IProov.NaturalStyle.CLEAR
    }
}

private fun Orientation.toIproov(): IProov.Orientation {
    return when (this) {
        Orientation.PORTRAIT -> IProov.Orientation.PORTRAIT
        Orientation.LANDSCAPE -> IProov.Orientation.LANDSCAPE
        Orientation.REVERSE_PORTRAIT -> IProov.Orientation.REVERSE_PORTRAIT
        Orientation.REVERSE_LANDSCAPE -> IProov.Orientation.REVERSE_LANDSCAPE
    }
}

private fun Camera.toIproov(): IProov.Camera {
    return when (this) {
        Camera.FRONT -> IProov.Camera.FRONT
        Camera.EXTERNAL -> IProov.Camera.EXTERNAL
    }
}

private fun List<IproovOptions.Certificate>.toIproov(): List<IProov.Options.Certificate> {
    return this.map { IProov.Options.Certificate(it.spki) }
}