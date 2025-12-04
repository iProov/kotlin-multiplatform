// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.kmp.api

open class IProovException(
    var title: String,
    var message: String? = null
)

class CaptureAlreadyActiveException(title: String) : IProovException(title)

class NetworkException(title: String, message: String?) : IProovException(title, message)

class CameraPermissionException(title: String) : IProovException(title)

class ServerException(title: String, message: String?) : IProovException(title, message)

class UnexpectedErrorException(title: String, message: String?) : IProovException(title, message)

// iOS only
class UserTimeoutException(title: String) : IProovException(title)

// Android only
class MultiWindowUnsupportedException(title: String) : IProovException(title)

class CameraException(title: String, message: String?) : IProovException(title, message)

class UnsupportedDeviceException(title: String) : IProovException(title)

class InvalidOptionsException(title: String) : IProovException(title)