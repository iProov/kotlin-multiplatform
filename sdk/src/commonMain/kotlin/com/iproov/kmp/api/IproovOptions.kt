// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.api

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

// IproovOptions ----

enum class LineDrawingStyle { CLASSIC, SHADED, VIBRANT }
enum class NaturalStyle { BLUR, CLEAR }
enum class Orientation { PORTRAIT, LANDSCAPE, REVERSE_PORTRAIT, REVERSE_LANDSCAPE }
enum class Camera { FRONT, EXTERNAL }

// Mutable IproovOptions with defaults
class IproovOptions {
    var title: String = Defaults.title
    var titleTextColor: Int = Defaults.titleTextColor
    var headerBackgroundColor: Int = Defaults.headerBackgroundColor
    var filter: Filter = Defaults.filter
    var surroundColor: Int = Defaults.surroundColor
    var fontPath: String? = Defaults.font
    var logo: ByteArray? = Defaults.logo
    var enableScreenshots: Boolean = Defaults.enableScreenshots
    var closeButton: CloseButton = Defaults.closeButton
    var promptTextColor: Int = Defaults.promptTextColor
    var promptBackgroundColor: Int = Defaults.promptBackgroundColor
    var promptRoundedCorners: Boolean = Defaults.promptRoundedCorners
    var disableExteriorEffects: Boolean = Defaults.disableExteriorEffects
    var certificates: List<Certificate> = Defaults.certificates
    var timeoutSecs: Int = Defaults.timeoutSecs
    var orientation: Orientation = Defaults.orientation
    var camera: Camera = Defaults.camera
    var genuinePresenceAssurance: GenuinePresenceAssurance = Defaults.genuinePresenceAssurance
    var livenessAssurance: LivenessAssurance = Defaults.livenessAssurance

    data class Certificate(val spki: String)

    object Defaults {
        const val title = ""

        val titleTextColor = Color.White.toArgb()

        val headerBackgroundColor = Color(0x00FFFFFF).toArgb()
        val filter = Filter.LineDrawingFilter()

        val surroundColor = Color(0x66000000).toArgb()
        val font: String? = null
        val logo: ByteArray? = null
        const val enableScreenshots = false
        val closeButton: CloseButton = CloseButton()

        val promptTextColor = Color.White.toArgb()

        val promptBackgroundColor = Color(0xCC000000).toArgb()
        const val promptRoundedCorners = true
        val certificates: List<Certificate> = listOf(
            Certificate("BbrVIhEYvvBL6FiyC7nzVKLLDU3GPYdqHWAfk0ev/80="), // AlphaSSL Intermediate (G4)
            Certificate("Sc7wL4FfOw8ued5w3JOSzu5MzB471PfqdyN4hnMYbX4="), // Global Sign Intermediate (R3)
            Certificate("JdFERRONSeokpPRwHKoZgZPPGO+7YwoMHGHoe1BAq3c="), // Global Sign GCC AlphaSSL CA 2023 (R6)
        )
        const val timeoutSecs = 10
        val orientation: Orientation = Orientation.PORTRAIT
        val camera: Camera = Camera.FRONT
        val genuinePresenceAssurance: GenuinePresenceAssurance = GenuinePresenceAssurance()
        val livenessAssurance: LivenessAssurance = LivenessAssurance()
        val disableExteriorEffects: Boolean = false
    }

    data class CloseButton(
        var icon: ByteArray? = Defaults.icon,
        var colorTint: Int = Defaults.colorTint,
    ) {
        object Defaults {
            val icon: ByteArray? = null
            val colorTint: Int = Color.White.toArgb()
        }
    }

    sealed class Filter {
        data class LineDrawingFilter(
            var style: LineDrawingStyle = Defaults.style,
            var foregroundColor: Int = Defaults.foregroundColor,
            var backgroundColor: Int = Defaults.backgroundColor,
        ) : Filter() {
            object Defaults {
                val style = LineDrawingStyle.SHADED
                val foregroundColor = Color(0xFF404040).toArgb()
                val backgroundColor = Color(0xFFFAFAFA).toArgb()
            }
        }

        data class NaturalFilter(
            var style: NaturalStyle = Defaults.style,
        ) : Filter() {
            object Defaults {
                val style = NaturalStyle.CLEAR
            }
        }
    }

    data class LivenessAssurance(
        var ovalStrokeColor: Int = Defaults.ovalStrokeColor,
        var completedOvalStrokeColor: Int = Defaults.completedOvalStrokeColor,
    ) {
        object Defaults {
            val ovalStrokeColor = Color.White.toArgb()
            val completedOvalStrokeColor = Color(0xFF01AC41).toArgb()
        }
    }

    data class GenuinePresenceAssurance(
        var readyOvalStrokeColor: Int = Defaults.readyOvalStrokeColor,
        var notReadyOvalStrokeColor: Int = Defaults.notReadyOvalStrokeColor,
        var scanningPrompts: Boolean = Defaults.scanningPrompts,
    ) {
        object Defaults {
            val readyOvalStrokeColor = Color(0xFF01AC41).toArgb()
            val notReadyOvalStrokeColor = Color.White.toArgb()
            val scanningPrompts: Boolean = false
        }
    }
}