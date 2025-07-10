![iProov: Flexible authentication for identity assurance](https://github.com/iProov/kotlin-multiplatform/blob/main/banner.jpg)
# iProov Biometrics Kotlin Multiplatform SDK

## Introduction

The iProov Biometrics Kotlin Multiplatform SDK wraps iProov's native [iOS](https://github.com/iProov/ios) (Swift) and [Android](https://github.com/iProov/android) (Java) SDKs behind a Kotlin Multiplatform interface for use from within your Kotlin Multiplatform iOS or Android app.

We also provide an API Client written in Kotlin Multiplatform to call our [REST API v2](https://eu.rp.secure.iproov.me/docs.html) from the Kotlin Multiplatform Example app, which can be used to request tokens directly from the iProov API (note that this is not a secure way of getting tokens, and should only be used for demo/debugging purposes).

### Requirements

- Kotlin Multiplatform SDK 2.1.21 and above
- iOS 13 and above
- Android API Level 26 (Android 8 Oreo) and above

## Repository contents

The iProov Kotlin Multiplatform SDK is provided via this repository, which contains the following:

- **README.md** - This document
- **sdk/commonMain/api** - iProov Kotlin Multiplatform SDK Plugin.
- **sdk/commonMain/api_client** - The Kotlin Multiplatform iProov API Client
- **ExampleApp/commonMain** - The Kotlin Multiplatform demonstration Example App.

## IOS Configuration

1. Select `File` → `Add Packages…` in the Xcode menu bar.

2. Search for the iProov SDK package using the following URL:

   ```
   https://github.com/iProov/ios
   ```

3. Set the _version_ to be as same iOS SDK version used in the Kotlin Multiplatform SDK.

4. Click _Add Package_ to add the iProov SDK to your Xcode project and then click again to confirm.

5. You must also add a `NSCameraUsageDescription` to your iOS app's Info.plist, with the reason why your app requires camera access (e.g. “To iProov you in order to verify your identity.”).

## Android Configuration

1. Open the `settings.gradle` file in your Android Studio.

2. Add maven to the `repositories` section in your `settings.gradle` file:

    ```groovy
    repositories {
        maven { url 'https://raw.githubusercontent.com/iProov/android/master/maven/' }
        maven { url 'https://raw.githubusercontent.com/iProov/kotlin-multiplatform/main/maven/' }
    }
    ```

## Kotlin Multiplatform SDK Installation

1. Add the Kotlin Multiplatform SDK version to the `commonMain.dependencies` section in your `build.gradle` file:

    ```kotlin
    commonMain.dependencies {
        implementation('com.iproov.kmp:sdk:1.0.1')
    }
    ```

2. Build your project

## Get Started

Once you have a valid token (obtained via the Kotlin Multiplatform API client or your own backend-to-backend call), you can `launch` an iProov capture and handle the state events as they arrive in the `Iproov.sessionState`.

```kotlin
class IproovViewModel : ViewModel() {

    init {
       collectIproovState()
    }

    private fun startScan(token: String) {
        viewModelScope.launch {
            // baseUrl - is the address of the server (SP) you are using
            // token - the single-use claim token you acquired in the prior step
            // options - are optional and referenced later in this document (they control the look and feel of the scan's UI among other aspects)

            Iproov.launchSession(baseUrl, token, IproovOptions())
        }
    }

    private fun collectIproovState() {
        viewModelScope.launch {
            Iproov.sessionState.collect { state ->
                withContext(Dispatchers.Main) {
                    when (state) {
                        is IproovState.Starting -> {
                            // The SDK is starting (Android Only)
                        }

                        is IproovState.Connecting -> {
                            // The SDK is connecting to the server. You should provide an indeterminate progress indicator
                            // to let the user know that the connection is taking place.
                        }

                        is IproovState.Connected -> {
                            // The SDK has connected, and the iProov user interface will now be displayed. You should hide
                            // any progress indication at this point.
                        }

                        is IproovState.Processing -> {
                            // The SDK will update your app with the progress of streaming to the server and authenticating
                            // the user. This will be called multiple times as the progress updates.
                            val progress = state.progress // Progress between 0.0 and 1.0
                            val message = state.message // Message to be displayed to the user
                        }

                        is IproovState.Success -> {
                            // The user was successfully verified/enrolled and the token has been validated.
                            // You can access the following properties:
                            val frame = state.successResult.frame // An optional image containing a single frame of the user, if enabled for your service provider
                        }

                        is IproovState.Failure -> {
                            // The user was not successfully verified/enrolled, as their identity could not be verified,
                            // or there was another issue with their verification/enrollment. A reason (as a string)
                            // is provided as to why the claim failed, along with a feedback code from the back-end.
                            val feedbackCode = state.failureResult.feedbackCode
                            val reason = state.failureResult.reason
                            val frame = state.failureResult.frame // An optional image containing a single frame of the user, if enabled for your service provider
                        }

                        is IproovState.Error -> {
                            // The user was not successfully verified/enrolled due to an error (e.g. lost internet connection).
                            // You will be provided with an Exception (see below).
                            // It will be called once, or never.
                            val error = state.exception // IProovException provided by the SDK
                        }

                        is IproovState.Canceled -> {
                            // The user canceled iProov, either by pressing the close button at the top of the screen, or sending
                            // the app to the background. (event.canceler == Canceler.user)
                            // Or, the app canceled (event.canceler == Canceler.app) by canceling the session to the IProov.cancelSession().
                            // You should use this to determine the next step in your flow.
                            val canceler = state.canceler
                        }

                        null -> {
                            // First initial value before launch IProov.launchSession() or when cancel the session IProov.cancelSession()
                        }
                    }
                }
            }
        }
    }
}
```

👉 You should now familiarize yourself with the following resources:

-  [iProov Biometrics iOS SDK documentation](https://github.com/iProov/ios)
-  [Android Biometrics Android SDK documentation](https://github.com/iProov/android)

These repositories provide comprehensive documentation about the available customization options and other important details regarding the SDK usage.

### Canceling the SDK

Under normal circumstances, the user will be in control of the completion of the iProov scan, i.e. they will either complete the scan, or use the close button to cancel. In some cases, you (the integrator) may wish to cancel the iProov scan programmatically, for example in response to a timeout or change of conditions in your app.

The scan can now be closed doing `IProov.cancelSession()`.

Example:

```kotlin
Iproov.cancelSession()
```

## Options

The `Options` class allows iProov to be customized in various ways. These can be specified by passing the optional `options:` named parameter in `IProov.launchSession()`.

Most of these options are common to both Android and iOS, however, some are Android-only.

For full documentation, please read the respective [iOS](https://github.com/iProov/ios#options) and [Android](https://github.com/iProov/android#customize-the-user-experience) native SDK documentation.

A summary of the support for the various SDK options in Kotlin Multiplatform is provided below. Any options not set will default to their platform-defined default value.

| Option | Type                                             | iOS | Android |
|---|--------------------------------------------------|---|---|
| `filter` | `Filter` [(See filter options)](#filter-options) | ✅ | ✅ |
| `titleTextColor` | `Int`                                            | ✅ | ✅ |
| `promptTextColor` | `Int`                                            | ✅ | ✅ |
| `closeButtonTintColor` | `Int`                                            | ✅ | ✅ |
| `closeButtonImage` | `ByteArray?`                                     | ✅ | ✅ |
| `title` | `String`                                         | ✅ | ✅ |
| `fontPath` | `String?`                                        | ✅  | ✅ |
| `logoImage` | `ByteArray?`                                     | ✅ | ✅ |
| `promptBackgroundColor` | `Int`                                            | ✅ | ✅ |
| `promptRoundedCorners` | `Boolean`                                        | ✅ | ✅ |
| `surroundColor` | `Int`                                            | ✅ | ✅ |
| `certificates` | `List<Certificate>`                              | ✅ | ✅ |
| `timeout` | `Int`                                            | ✅ | ✅ |
| `enableScreenshots` | `Boolean`                                        |  | ✅ |
| `orientation` | `Orientation`                                    |  | ✅ |
| `camera` | `Camera`                                         |  | ✅ |
| `headerBackgroundColor` | `Int`                                            | ✅ | ✅ |
| `disableExteriorEffects` | `Boolean`                                        | ✅ | ✅ |
|**`genuinePresenceAssurance`** | `GenuinePresenceAssurance`                       |  |  |
| ↳ `readyOvalStrokeColor` | `Int`                                            | ✅ | ✅ |
| ↳ `notReadyOvalStrokeColor` | `Int`                                            | ✅ | ✅ |
| ↳ `scanningPrompts` | `Boolean`                                        | ✅ | ✅ |
|**`livenessAssurance`** | `LivenessAssurance`                              |  |  |
| ↳ `ovalStrokeColor` | `Int`                                            | ✅ | ✅ |
| ↳ `completedOvalStrokeColor` | `Int`                                            | ✅ | ✅ |

### Filter Options

The SDK supports two different camera filters:

#### `LineDrawingFilter`

`LineDrawingFilter` is iProov's traditional "canny" filter, which is available in 3 styles: `.SHADED` (default), `.CLASSIC` and `.VIBRANT`.

The `foregroundColor` and `backgroundColor` can also be customized.

Example:

```kotlin
val options = IproovOptions()

options.filter = IproovOptions.Filter.LineDrawingFilter(
   style = LineDrawingStyle.VIBRANT,
   foregroundColor = Color.Black.toArgb(),
   backgroundColor = Color.White.toArgb()
)
```

#### `NaturalFilter`

`NaturalFilter` provides a more direct visualization of the user's face and is available in 2 styles: `.CLEAR` (default) and `.BLUR`.

Example:

```kotlin
val options = IproovOptions()

options.filter = IproovOptions.Filter.NaturalFilter(
   style = NaturalStyle.CLEAR
)
```

> **Note**: `NaturalFilter` is available for Liveness Assurance claims only. Attempts to use `NaturalFilter` for Genuine Presence Assurance claims will result in an error.

## Handling errors

All errors from the native SDKs are re-mapped to Kotlin Multiplatform exceptions:

| Exception                         | iOS | Android | Description                                                                                                                      |
| --------------------------------- | --- | ------- | -------------------------------------------------------------------------------------------------------------------------------- |
| `CaptureAlreadyActiveException`   | ✅   | ✅       | An existing iProov capture is already in progress. Wait until the current capture completes before starting a new one.           |
| `NetworkException`                    | ✅   | ✅       | An error occurred with the video streaming process. Consult the `message` value for more information.                            |
| `CameraPermissionException`           | ✅   | ✅       | The user disallowed access to the camera when prompted. You should direct the user to re-enable camera access.                   |
| `ServerException`                 | ✅   | ✅       | A server-side error/token invalidation occurred. The associated `message` will contain further information about the error.      |
| `UnexpectedErrorException`        | ✅   | ✅       | An unexpected and unrecoverable error has occurred. These errors should be reported to iProov for further investigation.         |
| `UnsupportedDeviceException`         |✅   | ✅         | Device is not supported.|
| `ListenerNotRegisteredException`  |     | ✅       | The SDK was launched before a listener was registered.                                                                           |
| `MultiWindowUnsupportedException` |     | ✅       | The user attempted to iProov in split-screen/multi-screen mode, which is not supported.                                          |
| `CameraException`                 |     | ✅       | An error occurred acquiring or using the camera. This could happen when a non-phone is used with/without an external/USB camera. |
| `FaceDetectorException`           |     | ✅       | An error occurred with the face detector.                                                                                        |
| `InvalidOptionsException`         |     | ✅       | An error occurred when trying to apply your options.|
| `UserTimeoutException`         |✅   |          | The user has taken too long to complete the claim.|

## API Client

The Kotlin Multiplatform API Client provides a convenient wrapper to call iProov's REST API v2 from your Kotlin Multiplatform app. It is a useful tool to assist with testing, debugging and demos, but should not be used in production mobile apps. You can also use this code as a reference for your back-end implementation to perform server-to-server calls.

The Kotlin Multiplatform API client package can be found in the `api_client` folder.

> **Warning**
>
> Use of the Kotlin Multiplatform API Client requires providing it with your API secret. **You should never embed your API secret within a production app.**

### Registration

You can obtain API credentials by registering on [iPortal](https://portal.iproov.com).

### Functionality

The Kotlin Multiplatform API Client supports the following functionality:

- `getToken()` - Get an enrol/verify token.

### Getting a token

The most basic thing you can do with the API Client is get a token to either enrol or verify a user, using either iProov's Genuine Presence Assurance or Liveness Assurance.

This is achieved as follows:

```kotlin
import com.iproov.kmp.api_client.AssuranceType
import com.iproov.kmp.api_client.ClaimType
import com.iproov.kmp.api_client.impl.ApiClientImpl

class IproovViewModel : ViewModel() {

    private val apiClient = ApiClientImpl(
        baseUrl = Credentials.FUEL_URL,
        apiKey = Credentials.API_KEY,
        secret = Credentials.SECRET
    )

    fun launchIProov() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = apiClient.getToken(
                    assuranceType = assuranceType,
                    claimType = claimType,
                    userID = username,
                    resource = null
                )

                // You can then launch the iProov SDK with this token.
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}
```

## Sample code

For a simple iProov experience that is ready to run out-of-the-box, check out the Kotlin Multiplatform example project which also makes use of the Kotlin Multiplatform API Client.

In the example app folder, check the `Credentials.kt` file and add your credentials obtained from the [iProov portal](https://portal.iproov.com/).

> NOTE: iProov is not supported on the iOS or Android simulator, you must use a physical device in order to iProov.

## Help & support

You may find your question is answered in the documentation of our native SDKs:

- iOS - [Documentation](https://github.com/iProov/ios), [FAQs](https://github.com/iProov/ios/wiki/Frequently-Asked-Questions)
- Android - [Documentation](https://github.com/iProov/android), [FAQs](https://github.com/iProov/android/wiki/Frequently-Asked-Questions)

For further help with integrating the SDK, please contact [support@iproov.com](mailto:support@iproov.com).
