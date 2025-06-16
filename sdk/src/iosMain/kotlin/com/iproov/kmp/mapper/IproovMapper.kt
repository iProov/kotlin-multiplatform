// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.mapper

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIColor
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation


@OptIn(ExperimentalForeignApi::class)
fun convertUIImageToByteArray(image: UIImage): ByteArray? {
    val imageData: NSData? = UIImageJPEGRepresentation(image, 1.0)
    val bytes = imageData?.bytes?.readBytes(imageData.length.toInt())
    return bytes
}

fun Int.toUIColor(): UIColor {
    val red = ((this shr 16) and 0xFF) / 255.0
    val green = ((this shr 8) and 0xFF) / 255.0
    val blue = (this and 0xFF) / 255.0
    val alpha = ((this shr 24) and 0xFF) / 255.0

    return UIColor(red = red, green = green, blue = blue, alpha = alpha)
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun byteArrayToUIImage(byteArray: ByteArray): UIImage? {
    return memScoped {
        val data = byteArray.usePinned {
            NSData.create(bytes = it.addressOf(0), length = byteArray.size.toULong())
        }
        UIImage.imageWithData(data)
    }
}