# WWF Project Management System – Android

Android app for tracking WWF conservation projects, tasks and milestones.

## Compatibility
- **minSdk 24** (Android 7.0+, ~98% of active devices)
- **compileSdk / targetSdk 36** – always tracks the latest platform so the app behaves correctly on new devices
- Edge-to-edge, predictive back, SplashScreen compat, adaptive + monochrome launcher icons, Material You dynamic colour (API 31+) with a brand fallback palette
- Core-library desugaring so `java.time` etc. work on older devices
- Adaptive theming: light/dark follows the system on every API level

## Stack
- Kotlin 2.2, AGP 8.13, Gradle 8.14
- **Jetpack Compose (Material 3)** for new UI
- **XML Views + ViewBinding** (AppCompat / Material Components) where needed – XML layouts are embedded in Compose via `AndroidViewBinding` (see `ui/HomeScreen.kt` + `res/layout/view_info_card.xml`)

## Build
Requires JDK 17–24 (Gradle 8.14 does not yet run on JDK 25).

```sh
./gradlew assembleDebug          # debug APK
./gradlew testDebugUnitTest      # unit tests
./gradlew connectedDebugAndroidTest  # UI tests on a device/emulator
./gradlew lintDebug              # lint
./gradlew bundleRelease          # Play Store AAB (per-ABI/density/language splits enabled)
```
