// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.mapper

import android.graphics.BitmapFactory
import com.iproov.kmp.api.IProovOptions
import com.iproov.kmp.api.LineDrawingStyle
import com.iproov.kmp.api.NaturalStyle
import com.iproov.kmp.util.assignIfNotNull
import com.iproov.sdk.api.IProov


fun IProovOptions.toIProov(): IProov.Options {
    val options = IProov.Options()

    this.title.assignIfNotNull { options.title = it }
    this.titleTextColor.assignIfNotNull { options.titleTextColor = it }
    this.headerBackgroundColor.assignIfNotNull { options.headerBackgroundColor = it }

    this.filter.assignIfNotNull { options.filter = it.toIProov() }

    this.promptTextColor.assignIfNotNull { options.promptTextColor = it }
    this.promptBackgroundColor.assignIfNotNull { options.promptBackgroundColor = it }
    this.promptRoundedCorners.assignIfNotNull { options.promptRoundedCorners = it }

    this.disableExteriorEffects.assignIfNotNull { options.disableExteriorEffects = it }
    this.enableScreenshots.assignIfNotNull { options.enableScreenshots = it }
    this.timeoutSecs.assignIfNotNull { options.timeoutSecs = it }
    this.surroundColor.assignIfNotNull { options.surroundColor = it }

    this.certificates.assignIfNotNull { options.certificates = it.toIProov() }
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

        gpa.accessibility.assignIfNotNull { accessibility ->
            accessibility.isColorEnabled.assignIfNotNull { options.genuinePresenceAssurance.accessibility.isColorEnabled = it }
            accessibility.isSoundEnabled.assignIfNotNull { options.genuinePresenceAssurance.accessibility.isSoundEnabled = it }
            accessibility.isVibrationEnabled.assignIfNotNull { options.genuinePresenceAssurance.accessibility.isVibrationEnabled = it }
        }
    }

    // LA ----
    this.livenessAssurance.assignIfNotNull { la ->
        la.ovalStrokeColor.assignIfNotNull { options.livenessAssurance.ovalStrokeColor = it }
        la.completedOvalStrokeColor.assignIfNotNull { options.livenessAssurance.completedOvalStrokeColor = it }
    }

    return options
}

private fun IProovOptions.Filter.toIProov(): IProov.Options.Filter {
    return when (val kmpFilter = this) {
        is IProovOptions.Filter.LineDrawingFilter -> IProov.Options.Filter.LineDrawingFilter().apply {
            kmpFilter.style.assignIfNotNull { style = it.toIProov() }
            kmpFilter.foregroundColor.assignIfNotNull { foregroundColor = it }
            kmpFilter.backgroundColor.assignIfNotNull { backgroundColor = it }
        }

        is IProovOptions.Filter.NaturalFilter -> IProov.Options.Filter.NaturalFilter().apply {
            kmpFilter.style.assignIfNotNull { style = it.toIProov() }
        }
    }
}

private fun LineDrawingStyle.toIProov(): IProov.LineDrawingStyle {
    return when (this) {
        LineDrawingStyle.SHADED -> IProov.LineDrawingStyle.SHADED
        LineDrawingStyle.CLASSIC -> IProov.LineDrawingStyle.CLASSIC
        LineDrawingStyle.VIBRANT -> IProov.LineDrawingStyle.VIBRANT
    }
}

private fun NaturalStyle.toIProov(): IProov.NaturalStyle {
    return when (this) {
        NaturalStyle.BLUR -> IProov.NaturalStyle.BLUR
        NaturalStyle.CLEAR -> IProov.NaturalStyle.CLEAR
    }
}

private fun List<IProovOptions.Certificate>.toIProov(): List<IProov.Options.Certificate> {
    return this.map { IProov.Options.Certificate(it.spki) }
}