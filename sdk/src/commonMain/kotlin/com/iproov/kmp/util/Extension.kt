// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.util

inline val String.endingWithSlash: String
    get() = if (endsWith("/")) this else "$this/"

inline val String.saferUrl: String
    get() = if (endingWithSlash.endsWith("api/v2/")) endingWithSlash else "${endingWithSlash}api/v2/"

inline fun <T> T?.assignIfNotNull(action: (T) -> Unit) {
    if (this != null) {
        action(this)
    }
}
