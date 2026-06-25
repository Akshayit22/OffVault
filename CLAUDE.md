# OffVault

Android application built with Kotlin and Jetpack Compose.

## Build Setup

- **Language**: Kotlin 2.0.21
- **UI**: Jetpack Compose (BOM 2024.09.00)
- **AGP**: 8.10.0 (Android Studio compatibility ceiling)
- **Gradle**: 8.11.1
- **compileSdk**: 35 | **minSdk**: 26 | **targetSdk**: 35

## Key Constraints

Android Studio supports AGP up to 8.10.0 — do not upgrade AGP beyond this without first upgrading Android Studio.

Library versions must stay compatible with compileSdk 35:
- `core-ktx`: 1.15.0
- `lifecycle-runtime-ktx`: 2.8.7
- `activity-compose`: 1.10.0

## Common Commands

```bash
./gradlew assembleDebug        # build debug APK
./gradlew test                 # run unit tests
./gradlew connectedAndroidTest # run instrumented tests
```

## Project Structure

```
app/src/main/java/com/aks/offvault/
├── MainActivity.kt
└── ui/theme/
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt
```