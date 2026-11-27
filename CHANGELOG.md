# iProov Face Kotlin-Multiplatform SDK

## 3.1.0

iProov Face Kotlin-Multiplatform SDK v3.1.0 includes the following:

- Security enhancements from the native SDKs
- Fixed a crash at the start of the first session on Android in apps built with Android Gradle Plugin 9.1 or later and R8
- A motion issue failure now tells the user to align their face in the oval and keep still, instead of suggesting a different device. On Android, an app that overrides `iproov__failure_motion_issue` still shows its own text
- On iPhone Duo, the capture pauses while the phone is open and asks the user to close it (new iOS localization key `IProov_PromptCloseDevice`)
- On iOS, strings customised with a custom strings bundle or table now also apply to failure messages and error titles, and a string missing from it shows the SDK's default text instead of the key name

### iOS

* SDK version [v14.1.0](https://github.com/iProov/ios/releases/tag/14.1.0).

### Android

* SDK version [v12.1.1](https://github.com/iProov/android/releases/tag/v12.1.1).

## 3.0.0

iProov Face Kotlin-Multiplatform SDK v3.0.0 includes the following:

- **Breaking:** Classes are renamed to match the company name and the native SDKs: `Iproov` is now `IProov`, `IproovOptions` is now `IProovOptions`, `IproovState` is now `IProovState`, and `IproovUIState` is now `IProovUIState`
- **Breaking:** Success and failure frames have been removed. `IProovState.Success` is now an object, and `IProovState.Failure` exposes `reasons` directly. `SuccessResult` and `FailureResult` have been removed
- **Breaking:** The `orientation` option and `Orientation` enum have been removed
- **Breaking:** Minimum iOS version is now iOS 16
- Added `accessibility` options to `GenuinePresenceAssurance` (`isColorEnabled`, `isSoundEnabled`, `isVibrationEnabled`)
- Added `UnsupportedOSVersionException` and `UnsupportedSDKVersionException`
- `UnsupportedDeviceException` is now also raised on iOS for the new `unsupportedDevice` error code

### iOS

* SDK version [v14.0.0](https://github.com/iProov/ios/releases/tag/14.0.0).

### Android

* SDK version [v12.0.0](https://github.com/iProov/android/releases/tag/v12.0.0).

## 2.0.1

iProov Face Kotlin-Multiplatform SDK v2.0.1 includes the following:

- Remove internal dependency on Compose
- Support for iOS Simulator

### iOS

* SDK version [v13.1.1](https://github.com/iProov/ios/releases/tag/13.1.1).

### Android

* SDK version [v11.1.1](https://github.com/iProov/android/releases/tag/v11.1.1).

## 2.0.0

iProov Face Kotlin-Multiplatform SDK v2.0.0 includes the following:

- Adding the new Options in GenuinePresenceAssurance(controlYPosition, controlXPosition, and scanningPrompts)
- New UI state API

### iOS

* SDK version [v13.1.0](https://github.com/iProov/ios/releases/tag/13.1.0).

### Android

* SDK version [v11.1.0](https://github.com/iProov/android/releases/tag/v11.1.0).


## 1.0.1

iProov Face Kotlin-Multiplatform SDK v1.0.1 includes the following:

### iOS

* SDK version [v12.4.1](https://github.com/iProov/ios/releases/tag/12.4.1).

### Android

* SDK version [v10.3.2](https://github.com/iProov/android/releases/tag/v10.3.2).


## 1.0.0

iProov Face Kotlin-Multiplatform SDK v1.0.0 includes the following:

### iOS

* SDK version [v12.3.1](https://github.com/iProov/ios/releases/tag/12.3.1).

### Android

* SDK version [v10.3.2](https://github.com/iProov/android/releases/tag/v10.3.2).