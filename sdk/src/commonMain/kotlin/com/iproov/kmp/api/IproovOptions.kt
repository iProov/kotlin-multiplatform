// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.api

// IproovOptions ----

enum class LineDrawingStyle { CLASSIC, SHADED, VIBRANT }
enum class NaturalStyle { BLUR, CLEAR }
enum class Orientation { PORTRAIT, LANDSCAPE, REVERSE_PORTRAIT, REVERSE_LANDSCAPE }

// Mutable IproovOptions with defaults
class IproovOptions {
    var title: String? = null
    var titleTextColor: Int? = null
    var headerBackgroundColor: Int? = null
    var filter: Filter? = null
    var surroundColor: Int? = null
    var fontPath: String?? = null
    var logo: ByteArray? = null
    var enableScreenshots: Boolean? = null
    var closeButton: CloseButton? = null
    var promptTextColor: Int? = null
    var promptBackgroundColor: Int? = null
    var promptRoundedCorners: Boolean? = null
    var disableExteriorEffects: Boolean? = null
    var certificates: List<Certificate>? = null
    var timeoutSecs: Int? = null
    var orientation: Orientation? = null
    var genuinePresenceAssurance: GenuinePresenceAssurance? = null
    var livenessAssurance: LivenessAssurance? = null

    data class Certificate(val spki: String)

    data class CloseButton(
        var icon: ByteArray? = null,
        var colorTint: Int? = null,
    )

    sealed class Filter {
        data class LineDrawingFilter(
            var style: LineDrawingStyle? = null,
            var foregroundColor: Int? = null,
            var backgroundColor: Int? = null,
        ) : Filter()

        data class NaturalFilter(
            var style: NaturalStyle? = null,
        ) : Filter()
    }

    data class LivenessAssurance(
        var ovalStrokeColor: Int? = null,
        var completedOvalStrokeColor: Int? = null,
    )

    data class GenuinePresenceAssurance(
        var readyOvalStrokeColor: Int? = null,
        var notReadyOvalStrokeColor: Int? = null,
        var controlYPosition: Boolean? = null,
        var controlXPosition: Boolean? = null,
        var scanningPrompts: Boolean? = null,
    )
}