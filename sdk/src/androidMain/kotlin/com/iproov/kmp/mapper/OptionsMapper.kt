// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.mapper

import android.graphics.BitmapFactory
import com.iproov.kmp.api.IproovOptions
import com.iproov.kmp.api.LineDrawingStyle
import com.iproov.kmp.api.NaturalStyle
import com.iproov.kmp.api.Orientation
import com.iproov.kmp.util.assignIfNotNull
import com.iproov.sdk.api.IProov


fun IproovOptions.toIproov(): IProov.Options {
    val options = IProov.Options()

    this.title.assignIfNotNull { options.title = it }
    this.titleTextColor.assignIfNotNull { options.titleTextColor = it }
    this.headerBackgroundColor.assignIfNotNull { options.headerBackgroundColor = it }

    this.filter.assignIfNotNull { options.filter = it.toIproov() }

    this.promptTextColor.assignIfNotNull { options.promptTextColor = it }
    this.promptBackgroundColor.assignIfNotNull { options.promptBackgroundColor = it }
    this.promptRoundedCorners.assignIfNotNull { options.promptRoundedCorners = it }

    this.disableExteriorEffects.assignIfNotNull { options.disableExteriorEffects = it }
    this.orientation.assignIfNotNull { options.orientation = it.toIproov() }
    this.enableScreenshots.assignIfNotNull { options.enableScreenshots = it }
    this.timeoutSecs.assignIfNotNull { options.timeoutSecs = it }
    this.surroundColor.assignIfNotNull { options.surroundColor = it }

    this.certificates.assignIfNotNull { options.certificates = it.toIproov() }
    this.fontPath.assignIfNotNull { options.font = IProov.Options.Font.PathFont(it) }

    this.logo.assignIfNotNull { logo ->
        options.logo = IProov.Options.Icon.BitmapIcon(
            BitmapFactory.decodeByteArray(logo, 0, logo.size)
        )
    }

    // Close button ----
    this.closeButton?.icon?.let {
        options.closeButton.icon = IProov.Options.Icon.BitmapIcon(
            BitmapFactory.decodeByteArray(it, 0, it.size)
        )
    }

    this.closeButton?.colorTint.assignIfNotNull { options.closeButton.colorTint = it }

    // GPA ----
    this.genuinePresenceAssurance.assignIfNotNull { gpa ->
        gpa.notReadyOvalStrokeColor.assignIfNotNull { options.genuinePresenceAssurance.notReadyOvalStrokeColor = it }
        gpa.readyOvalStrokeColor.assignIfNotNull { options.genuinePresenceAssurance.readyOvalStrokeColor = it }
        gpa.controlYPosition.assignIfNotNull { options.genuinePresenceAssurance.controlYPosition = it }
        gpa.controlXPosition.assignIfNotNull { options.genuinePresenceAssurance.controlXPosition = it }
        gpa.scanningPrompts.assignIfNotNull { options.genuinePresenceAssurance.scanningPrompts = it }
    }

    // LA ----
    this.livenessAssurance.assignIfNotNull { la ->
        la.ovalStrokeColor.assignIfNotNull { options.livenessAssurance.ovalStrokeColor = it }
        la.completedOvalStrokeColor.assignIfNotNull { options.livenessAssurance.completedOvalStrokeColor = it }
    }

    return options
}

private fun IproovOptions.Filter.toIproov(): IProov.Options.Filter {
    return when (val kmpFilter = this) {
        is IproovOptions.Filter.LineDrawingFilter -> IProov.Options.Filter.LineDrawingFilter().apply {
            kmpFilter.style.assignIfNotNull { style = it.toIproov() }
            kmpFilter.foregroundColor.assignIfNotNull { foregroundColor = it }
            kmpFilter.backgroundColor.assignIfNotNull { backgroundColor = it }
        }

        is IproovOptions.Filter.NaturalFilter -> IProov.Options.Filter.NaturalFilter().apply {
            kmpFilter.style.assignIfNotNull { style = it.toIproov() }
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

private fun List<IproovOptions.Certificate>.toIproov(): List<IProov.Options.Certificate> {
    return this.map { IProov.Options.Certificate(it.spki) }
}