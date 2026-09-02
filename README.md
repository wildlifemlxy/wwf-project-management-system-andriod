# WWF Project Management System – Android

Android app for tracking WWF conservation projects, tasks and milestones.

## Compatibility
- **minSdk 24** (Android 7.0+, ~98% of active devices)
- **compileSdk / targetSdk 36** – always tracks the latest platform so the app behaves correctly on new devices
- Edge-to-edge, predictive back, SplashScreen compat, adaptive + monochrome launcher icons, Material You dynamic colour (API 31+) with a brand fallback palette
- Core-library desugaring so `java.time` etc. work on older devices
- Adaptive theming: light/dark follows the system on every API level

## Stack
- Kotlin 2.2 (built into AGP 9), AGP 9.2, Gradle 9.4
- **Jetpack Compose (Material 3)** for new UI
- **XML Views + ViewBinding** (AppCompat / Material Components) where needed – XML layouts are embedded in Compose via `AndroidViewBinding` (see `ui/HomeScreen.kt` + `res/layout/view_info_card.xml`)

## Build
Works with Android Studio 2026.1+ and its bundled JDK (Java 25); command line needs JDK 17+.

```sh
./gradlew assembleDebug          # debug APK
./gradlew testDebugUnitTest      # unit tests
./gradlew connectedDebugAndroidTest  # UI tests on a device/emulator
./gradlew lintDebug              # lint
./gradlew bundleRelease          # Play Store AAB (per-ABI/density/language splits enabled)
```

## Running in Android Studio
1. **File → Open** the project root (the folder with `settings.gradle.kts`) and let Gradle sync finish.
2. Pick the **`app`** run configuration and a device/emulator (e.g. *Medium Phone API 36*), then press ▶.

If Studio shows *Add Configuration…*, it means Gradle sync hasn't completed — use **File → Sync Project with Gradle Files**.
