![iProov: Flexible authentication for identity assurance](https://github.com/iProov/kotlin-multiplatform/blob/main/banner.jpg)

# iProov Face Kotlin Multiplatform SDK v3.0.0

## Introduction

This guide describes how to integrate iProov biometric assurance technologies into your Kotlin Multiplatform app for iOS and Android.

The iProov Face Kotlin Multiplatform SDK wraps iProov's native [iOS](https://github.com/iProov/ios) (Swift) and [Android](https://github.com/iProov/android) (Kotlin) SDKs behind a single Kotlin Multiplatform interface.

iProov offers two powerful authentication solutions:

* **Dynamic Liveness™** technology (our **Genuine Presence Assurance™** solution) verifies that an online remote user is the right person, a real person, and that they are authenticating right now, for purposes of access control and security.
* **Express Liveness™** technology (our **Liveness Assurance™** solution) verifies that a remote online user is the right person and a real person for access control and security.

### Software Download & Installation

The SDK can be downloaded and installed directly into your Kotlin Multiplatform application, or incorporated into your own SDK.

Our downloadable software components implementing **Dynamic Liveness™** and **Express Liveness™** technologies integrate seamlessly with your existing user journeys.

### Web Services & API Access

In addition to our downloadable software components, iProov provides identity authentication services through our secure API and web-based administration portal.
These services enable organizations to implement **Dynamic Liveness™** and **Express Liveness™** verification capabilities across their digital platforms.

iProov also supports [iOS](https://github.com/iproov/ios), [Android](https://github.com/iproov/android), [Flutter](https://github.com/iproov/flutter), [MAUI](https://github.com/iProov/dotnet-maui), [React Native](https://github.com/iproov/react-native), and [Web](https://github.com/iProov/web).

### Requirements

- Kotlin 2.4.21 or above
- iOS 16.0 and above
- Xcode 26.0 and above
- Android minSdk API Level 29 (Android 10) and above

This version wraps the following native SDKs:

- iOS SDK [14.0.0](https://github.com/iProov/ios/releases/tag/14.0.0)
- Android SDK [12.0.0](https://github.com/iProov/android/releases/tag/v12.0.0)

### Firebase Cloud Firestore

On Android, Firestore versions before 27.0.0 depend on `protolite-well-known-types`, which contains its own copy of `com.google.protobuf.DescriptorProtos`. `protobuf-javalite` 4.27.0 and later contain the same class, so the build fails with `Duplicate class com.google.protobuf.DescriptorProtos`. To fix this, upgrade to Firestore 27.0.0 or later, or force `protobuf-javalite` 3.25.8, which the Android SDK also supports.

## Repository Contents

The iProov Kotlin Multiplatform SDK is provided via this repository, which contains the following:

- **README.md** - This document
- **maven** - A Maven repository for the SDK
- **sdk** - The iProov Kotlin Multiplatform SDK, including the API Client (`com.iproov.kmp.api_client`)
- **exampleApp** - The shared Kotlin Multiplatform code (Compose UI) for the Example App
- **exampleApp/androidApp** - The Android application for the Example App
- **exampleApp/iosApp** - The iOS (Xcode) application for the Example App

## Upgrading from Earlier Versions

See the [release notes](https://github.com/iProov/kotlin-multiplatform/releases) for the changes in each version. Version 3.0.0 contains breaking changes:

- Classes are renamed to match the native SDKs: `Iproov` is now `IProov`, `IproovOptions` is now `IProovOptions`, `IproovState` is now `IProovState`, and `IproovUIState` is now `IProovUIState`. `IProovException` and its subclasses are unchanged
- `IProovState.Success` is now an object, and `IProovState.Failure` exposes `reasons` directly. `SuccessResult`, `FailureResult` and the success and failure frames have been removed. Replace `state.failureResult.reasons` with `state.reasons`
- The `orientation` option and `Orientation` enum have been removed
- The minimum iOS version is now iOS 16
- New errors: `UnsupportedOSVersionException` and `UnsupportedSDKVersionException`, and `UnsupportedDeviceException` is now also raised on iOS. See [Errors](#errors)
- `com.iproov.kmp:sdk-android-debug` is no longer published. Depend on `com.iproov.kmp:sdk`

The native SDK upgrade guides also apply: [iOS](https://github.com/iProov/ios/wiki/Upgrade-Guide) and [Android](https://github.com/iProov/android/wiki/Upgrade-Guide).

## Registration

You can obtain API credentials by registering on [iPortal](https://portal.iproov.com/).

## Installation

### Kotlin Multiplatform

1. Add the iProov and iProov Kotlin Multiplatform Maven repositories to the `repositories` section of your `settings.gradle.kts` file:

    ```kotlin
    dependencyResolutionManagement {
        repositories {
            maven("https://raw.githubusercontent.com/iProov/android/master/maven/")
            maven("https://raw.githubusercontent.com/iProov/kotlin-multiplatform/main/maven/")
        }
    }
    ```

2. Add the SDK to the `commonMain.dependencies` section of your shared module's `build.gradle.kts` file:

    ```kotlin
    commonMain.dependencies {
        implementation("com.iproov.kmp:sdk:3.0.0")
    }
    ```

3. Build your project

### iOS

The iOS app also needs the native iOS SDK, at the same version this SDK wraps:

1. Select `File` → `Add Packages…` in the Xcode menu bar.

2. Search for the iProov SDK package using the following URL:

   ```
   https://github.com/iProov/ios-spm
   ```

3. Set the _version_ to `14.0.0`. **Always use the iOS SDK version listed in [Requirements](#requirements)**.

4. Click _Add Package_ to add the iProov SDK to your Xcode project and then click again to confirm.

5. Add an `NSCameraUsageDescription` to your iOS app's Info.plist, with the reason why your app requires camera access (e.g. "To iProov you in order to verify your identity.").

### Android

No extra steps are needed: the native Android SDK is a dependency of `com.iproov.kmp:sdk`. The camera and internet permissions are declared by the SDK, and the camera permission is requested when needed.

## Get Started

The overall process is as follows:

1. Get a token
2. Launch a session with `IProov.launchSession()`, passing any `IProovOptions`
3. Collect `IProov.sessionState` to follow the progress of the scan and its result

### Get a Token

In production, tokens should always be obtained by a secure server-to-server call. For development and testing without a server, you can use the [API Client](#api-client).

### Launch a Session

Once you have a valid token, `launchSession` the iProov capture and handle the states as they arrive in `IProov.sessionState`:

```kotlin
class IProovViewModel : ViewModel() {

    init {
        collectIProovState()
    }

    private fun startScan(token: String) {
        viewModelScope.launch {
            // baseUrl - the address of the server (SP) you are using
            // token - the single-use claim token you acquired in the prior step
            // iproovOptions - controls the look and feel of the scan's UI, among other aspects. See Options below

            IProov.launchSession(baseUrl, token, IProovOptions())
        }
    }

    private fun collectIProovState() {
        viewModelScope.launch {
            IProov.sessionState.collect { state ->
                withContext(Dispatchers.Main) {
                    when (state) {
                        is IProovState.Starting -> {
                            // The SDK is starting (Android only)
                        }

                        is IProovState.Connecting -> {
                            // The SDK is connecting to the server. You should provide an indeterminate progress indicator
                            // to let the user know that the connection is taking place.
                        }

                        is IProovState.Connected -> {
                            // The SDK has connected, and the iProov user interface will now be displayed. You should hide
                            // any progress indication at this point.
                        }

                        is IProovState.Processing -> {
                            // The SDK will update your app with the progress of streaming to the server and authenticating
                            // the user. This will be called multiple times as the progress updates.
                            val progress = state.progress // Progress between 0.0 and 1.0
                            val message = state.message // Message to be displayed to the user
                        }

                        is IProovState.Success -> {
                            // The user was successfully verified/enrolled and the token has been validated.
                        }

                        is IProovState.Failure -> {
                            // The user was not successfully verified/enrolled, as their identity could not be verified.
                            // See Failures below.
                            val reasons = state.reasons
                        }

                        is IProovState.Error -> {
                            // The user was not successfully verified/enrolled due to an error (e.g. lost internet connection).
                            // See Errors below.
                            val exception = state.exception
                        }

                        is IProovState.Canceled -> {
                            // The user canceled iProov (Canceler.USER), or your app canceled it with IProov.cancelSession()
                            // (Canceler.INTEGRATION). On iOS, both are reported as Canceler.USER.
                            val canceler = state.canceler
                        }

                        null -> {
                            // The initial value, before IProov.launchSession() is called
                        }
                    }
                }
            }
        }
    }
}
```

`Success`, `Failure`, `Canceled` and `Error` are final states: `state.isFinal` is `true` for them.

### UI States (optional)

To follow when the iProov UI is shown and dismissed, collect `IProov.uiState`. It emits `IProovUIState.NotStarted`, `IProovUIState.Started` and `IProovUIState.Ended`.

### Canceling a Session

Under normal circumstances, the user will be in control of the completion of the iProov scan, i.e. they will either complete the scan, or use the close button to cancel. In some cases, you (the integrator) may wish to cancel the iProov scan programmatically, for example in response to a timeout or change of conditions in your app.

`cancelSession` is a suspend function:

```kotlin
viewModelScope.launch {
    IProov.cancelSession()
}
```

## Options

You can customize the iProov session by passing an `IProovOptions` to `IProov.launchSession()`. Any option you don't set keeps the native SDK's default, as documented for [iOS](https://github.com/iProov/ios#options) and [Android](https://github.com/iProov/android#customize-the-user-experience).

> **Note**: The defaults support accessibility requirements and have been verified to [comply with WCAG 2.1 AA guidelines](https://www.iproov.com/blog/biometric-authentication-liveness-accessibility-inclusivity-wcag-regulations). Changing any of these could invalidate compliance.

Colors are ARGB `Int` values, for example `0xFF01AC41.toInt()`. Images are `ByteArray`s holding encoded image data, such as a PNG.

| Option | Type | Description | iOS | Android |
|---|---|---|---|---|
| `filter` | `Filter` | The filter applied to the camera preview. [See filter options](#filter-options) | ✅ | ✅ |
| `title` | `String` | The title displayed during a claim scan. | ✅ | ✅ |
| `titleTextColor` | `Int` | The color of the text in the title. | ✅ | ✅ |
| `headerBackgroundColor` | `Int` | The background color of the header bar. | ✅ | ✅ |
| `fontPath` | `String` | The custom font for the title and prompt: a font name on iOS, a font file path on Android. | ✅ | ✅ |
| `logo` | `ByteArray` | The custom logo in the header. | ✅ | ✅ |
| `closeButton` | `CloseButton` | The close button's `icon` (`ByteArray`) and `colorTint` (`Int`). | ✅ | ✅ |
| `promptTextColor` | `Int` | The color of the text in the prompt. | ✅ | ✅ |
| `promptBackgroundColor` | `Int` | The color of the prompt. | ✅ | ✅ |
| `promptRoundedCorners` | `Boolean` | Whether the prompt has rounded corners. | ✅ | ✅ |
| `surroundColor` | `Int` | The color of the area outside the oval. | ✅ | ✅ |
| `disableExteriorEffects` | `Boolean` | Whether to disable the blur and vignette outside the oval. | ✅ | ✅ |
| `certificates` | `List<Certificate>` | Certificates to pin to. [See certificate pinning](#certificate-pinning) | ✅ | ✅ |
| `timeoutSecs` | `Int` | The WebSocket streaming timeout in seconds. | ✅ | ✅ |
| `enableScreenshots` | `Boolean` | Whether screenshots are allowed during the scan. | | ✅ |
| `genuinePresenceAssurance` | `GenuinePresenceAssurance` | [See Genuine Presence Assurance options](#genuine-presence-assurance-options) | ✅ | ✅ |
| `livenessAssurance` | `LivenessAssurance` | [See Liveness Assurance options](#liveness-assurance-options) | ✅ | ✅ |

### Filter Options

The SDK supports two different camera filters:

#### `LineDrawingFilter`

`LineDrawingFilter` is iProov's traditional "canny" filter, which is available in 3 styles: `SHADED` (default), `CLASSIC` and `VIBRANT`.

The `foregroundColor` and `backgroundColor` can also be customized.

Example:

```kotlin
val options = IProovOptions()

options.filter = IProovOptions.Filter.LineDrawingFilter(
    style = LineDrawingStyle.VIBRANT,
    foregroundColor = 0xFF000000.toInt(),
    backgroundColor = 0xFFFFFFFF.toInt()
)
```

#### `NaturalFilter`

`NaturalFilter` provides a more direct visualization of the user's face and is available in 2 styles: `CLEAR` (default) and `BLUR`. `NaturalFilter` is the default filter, for both Genuine Presence Assurance and Liveness Assurance claims.

Example:

```kotlin
val options = IProovOptions()

options.filter = IProovOptions.Filter.NaturalFilter(
    style = NaturalStyle.CLEAR
)
```

> **Note**: On iOS, if `disableExteriorEffects` is set to `true`, `filter` can not be set to `NaturalFilter` with `NaturalStyle.BLUR`. This combination of options will result in an error.

### Certificate Pinning

Certificate pinning is not recommended for most use cases. If you are using your own reverse-proxy, you will need to use this option to pin to your own certificates.

Certificates are passed as the base64-encoded SHA-256 hash of a certificate's Subject Public Key Info:

```kotlin
options.certificates = listOf(
    IProovOptions.Certificate("O8qZKEXWWkMPISIpvB7DUow++JzIW2g+k9z3U/l5V94="),
    IProovOptions.Certificate("Sc7wL4FfOw8ued5w3JOSzu5MzB471PfqdyN4hnMYbX4=")
)
```

To get Subject Public Key Info from a `.crt` file, use the following command:

```sh
$ openssl x509  -in cert.crt -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64
```

To get Subject Public Key Info from a `.der` file, use the following command:

```sh
$ openssl x509 -inform der -in cert.der -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64
```

When multiple certificates are passed, as long as the server matches **any** of the certificates, the connection will be allowed.

### Genuine Presence Assurance Options

Genuine Presence Assurance options are set under `IProovOptions.genuinePresenceAssurance`:

| Option | Type | Description | iOS | Android |
|---|---|---|---|---|
| `readyOvalStrokeColor` | `Int` | The color of the oval stroke in the GPA "ready" state. | ✅ | ✅ |
| `notReadyOvalStrokeColor` | `Int` | The color of the oval stroke in the GPA "not ready" state. | ✅ | ✅ |
| `controlXPosition` | `Boolean` | Whether to control the x position of the face, by showing prompts. | ✅ | ✅ |
| `controlYPosition` | `Boolean` | Whether to control the y position of the face, by showing prompts. | ✅ | ✅ |
| `scanningPrompts` | `Boolean` | Show a "Scanning" prompt during the scan, and "Scan Completed" when it completes. | ✅ | ✅ |
| `accessibility` | `Accessibility` | [See accessibility options](#accessibility-options) | ✅ | ✅ |

### Accessibility Options

Accessibility options provide additional guidance for users with low vision, to help them align their face for a scan to start. They are set under `IProovOptions.genuinePresenceAssurance.accessibility`, apply to Genuine Presence Assurance only, can be used in any combination, and are all disabled by default:

| Option | Type | Description | iOS | Android |
|---|---|---|---|---|
| `isColorEnabled` | `Boolean` | Adds progressive color to the experience. | ✅ | ✅ |
| `isSoundEnabled` | `Boolean` | Adds progressive sound to the experience. | ✅ | ✅ |
| `isVibrationEnabled` | `Boolean` | Adds progressive vibration to the experience. | ✅ | ✅ |

```kotlin
options.genuinePresenceAssurance = IProovOptions.GenuinePresenceAssurance(
    accessibility = IProovOptions.GenuinePresenceAssurance.Accessibility(
        isColorEnabled = true,
        isSoundEnabled = true,
        isVibrationEnabled = true
    )
)
```

Enabling any of them changes the capture: for example, `controlXPosition` and `controlYPosition` are treated as enabled, and the instruction prompt moves. See the [iOS](https://github.com/iProov/ios#accessibility-options) and [Android](https://github.com/iProov/android#accessibility-options) documentation for the details on each platform.

### Liveness Assurance Options

Liveness Assurance options are set under `IProovOptions.livenessAssurance`:

| Option | Type | Description | iOS | Android |
|---|---|---|---|---|
| `ovalStrokeColor` | `Int` | The color of the oval stroke during the scan. | ✅ | ✅ |
| `completedOvalStrokeColor` | `Int` | The color of the oval stroke after the scan completes. | ✅ | ✅ |

## Localization

The SDK ships with support for the following languages:

| Language | Code |
|---|---|
| English | `en` |
| Dutch | `nl` |
| French | `fr` |
| German | `de` |
| Italian | `it` |
| Portuguese | `pt` |
| Portuguese (Brazil) | `pt-BR` |
| Spanish | `es` |
| Spanish (Colombia) | `es-CO` |
| Welsh | `cy-GB` |

To customize the strings or add a language, follow each native SDK's instructions:

- **Android**: all strings are prefixed with `iproov__`, and you can override them in your app's `strings.xml`. See the [Android documentation](https://github.com/iProov/android#string-localization--customization).
- **iOS**: see the [Localization Guide](https://github.com/iProov/ios/wiki/Localization).

## Handling Failures & Errors

### Failures

A **failure** occurs when iProov successfully processes a claim but the user's identity cannot be verified. This differs from an error, where the capture itself could not be completed.

Failures arrive as `IProovState.Failure`, whose `reasons` holds up to three `FailureReason`s, each with:

- `feedbackCode` - A string representation of the feedback code, as reported by the native SDK.
- `description` - A localized hint for the user. You should present this to the user, as it may help them to iProov successfully next time.

The available failure reasons are as follows:

| Reason | `description` (English) |
|---|---|
| System error | System error. |
| Lighting issues | Make sure your face is well lit and free from glare. |
| Face not found | Align your face in the oval and then try to keep still. |
| Timeout | System timeout. |
| Eyewear | Remove your eyewear. |
| Device issue | Try a different device. |
| Rejected | Transaction could not be completed. |
| Background issue | Move to a different location with a neutral background. |
| Motion issue | Try a different device. |
| Device restart | Please restart your device and try again. |
| Frames blurry | Align your face in the oval and then try to keep still. Please try again. |
| User not found | Transaction could not be completed. |
| Processing fault | Please try again. |

Legacy reasons may still be returned in some scenarios (for example, features not yet enabled for your service provider). For these, and for the exact `feedbackCode` values, see the [iOS](https://github.com/iProov/ios#failures) and [Android](https://github.com/iProov/android#failures) documentation.

### Errors

An **error** occurs when the capture could not be completed, due to a technical problem or a user action. Errors arrive as `IProovState.Error`, whose `exception` is one of the following `IProovException` subclasses:

| Exception | iOS | Android | Description |
|---|---|---|---|
| `CaptureAlreadyActiveException` | ✅ | ✅ | An existing iProov capture is already in progress. Wait until the current capture completes before starting a new one. |
| `NetworkException` | ✅ | ✅ | An error occurred with communications to the server, for example the internet connection was lost. Consult the `message` value for more information. |
| `CameraPermissionException` | ✅ | ✅ | The user did not allow access to the camera when prompted. Direct the user to re-enable camera access in Settings. |
| `ServerException` | ✅ | ✅ | The token was invalidated server-side, or another server error occurred. Consult the `message` value for more information. |
| `UnexpectedErrorException` | ✅ | ✅ | An unexpected and unrecoverable error has occurred. These errors should be reported to iProov for further investigation. |
| `UnsupportedDeviceException` | ✅ | ✅ | The device is not supported, for example it does not have a front-facing camera. Do not retry. |
| `UnsupportedOSVersionException` | ✅ | ✅ | The operating system version is not supported. Do not retry. |
| `UnsupportedSDKVersionException` | ✅ | ✅ | This version of the SDK is no longer supported. Do not retry. |
| `UserTimeoutException` | ✅ | | The user has taken too long to complete the claim. |
| `MultiWindowUnsupportedException` | | ✅ | The user attempted to iProov in split-screen/multi-window mode, which is not supported. |
| `CameraException` | | ✅ | An error occurred acquiring or using the camera, for example with an external camera. |
| `InvalidOptionsException` | | ✅ | An error occurred when trying to apply your options. |

## API Client

The Kotlin Multiplatform API Client provides a convenient wrapper to call iProov's [REST API v2](https://eu.rp.secure.iproov.me/docs.html) from your Kotlin Multiplatform app. It is a useful tool to assist with testing, debugging and demos, but should not be used in production mobile apps. You can also use this code as a reference for your back-end implementation to perform server-to-server calls.

The API Client is part of the SDK, in the `com.iproov.kmp.api_client` package.

> **Warning**
>
> Use of the Kotlin Multiplatform API Client requires providing it with your API secret. **You should never embed your API secret within a production app.**

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

class IProovViewModel : ViewModel() {

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

## Example Project

For a simple iProov experience that is ready to run out-of-the-box and uses the API Client, check out the Example project:

1. Add your API Key and Secret, obtained from [iPortal](https://portal.iproov.com/), to `exampleApp/src/commonMain/kotlin/com/iproov/example/util/Credentials.kt`.

2. For Android, open the project in Android Studio and run the `androidApp` configuration.

3. For iOS, open `exampleApp/iosApp/iosApp.xcodeproj` in Xcode, set your team and bundle ID in `exampleApp/iosApp/Configuration/Config.xcconfig`, then build and run.

> **Note**: The SDK builds for the iOS simulator, but you can only iProov on a physical device.

> **Warning**: The Example project uses the API Client to fetch tokens directly on-device, which is inherently insecure. Production implementations of iProov should always obtain tokens securely from a server-to-server call.

## Help & Support

You may find your question answered in the documentation of our native SDKs:

- iOS - [Documentation](https://github.com/iProov/ios), [FAQs](https://github.com/iProov/ios/wiki/Frequently-Asked-Questions)
- Android - [Documentation](https://github.com/iProov/android), [FAQs](https://github.com/iProov/android/wiki/Frequently-Asked-Questions)

Consult the [Documentation Center](https://docs.iproov.com/) for the detailed implementation guide, glossary, and API reference.

For further help with integrating the SDK, please contact [support@iproov.com](mailto:support@iproov.com).
