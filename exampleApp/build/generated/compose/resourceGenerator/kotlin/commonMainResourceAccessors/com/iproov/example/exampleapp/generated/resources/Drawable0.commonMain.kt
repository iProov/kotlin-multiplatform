@file:OptIn(org.jetbrains.compose.resources.InternalResourceApi::class)

package com.iproov.example.exampleapp.generated.resources

import kotlin.OptIn
import org.jetbrains.compose.resources.DrawableResource

private object CommonMainDrawable0 {
  public val iproov_logo: DrawableResource by 
      lazy { init_iproov_logo() }
}

internal val Res.drawable.iproov_logo: DrawableResource
  get() = CommonMainDrawable0.iproov_logo

private fun init_iproov_logo(): DrawableResource = org.jetbrains.compose.resources.DrawableResource(
  "drawable:iproov_logo",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/com.iproov.example.exampleapp.generated.resources/drawable/iproov_logo.png", -1, -1),
    )
)
