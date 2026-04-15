# Wellness Companion

A private, offline-first "Wellness and Mental Health Companion" for Android,
built with **Kotlin + Jetpack Compose (Material 3)** and engineered to scale
to 100k+ local records per user.

> See [`SCALING.md`](./SCALING.md) for the full scaling write-up.

## Features

- **Mood & Metric Tracker** — interactive Canvas "Wellness Wheel" that
  captures both valence (angle) and arousal (radius) in a single gesture.
- **Autonomous Journaling** — minimalist, on-device-only editor with word-
  count indexing and Paging 3 for instant scroll at any volume.
- **Data Visualization** — fully custom `Canvas` charts (smoothed trend
  line, metric bars). No third-party charting libraries.
- **Biometric Privacy Gate** — `androidx.biometric` unlock on cold start,
  graceful fallback on devices without biometrics.
- **Responsive Navigation** — bottom bar on phones, navigation rail on
  tablets / unfolded foldables (600 dp breakpoint).
- **Soft, organic aesthetic** — pastel sage / rose / lavender Material-3
  palette, spring animations, ample whitespace.

## Architecture

Clean Architecture with MVVM-flavoured MVI for the Mood screen:

```
ui/        Composables + screen ViewModels (state = StateFlow<UiState>)
domain/    Pure-Kotlin helpers (no Android imports)
data/
  db/      Room entities, DAOs, migrations, tuning PRAGMAs
  repository/ Thin facades exposed to ViewModels
di/        Lightweight manual container (no Hilt/Dagger)
```

## Tech stack (everything is Apache 2.0 / EPL)

| Concern            | Library                                   | License   |
| ------------------ | ----------------------------------------- | --------- |
| Language           | Kotlin stdlib, Coroutines, Flow           | Apache 2.0 |
| UI                 | Jetpack Compose (BOM), Material 3         | Apache 2.0 |
| Icons              | `material-icons-extended`                 | Apache 2.0 |
| Navigation         | `androidx.navigation:navigation-compose`  | Apache 2.0 |
| Lifecycle / VM     | `androidx.lifecycle:*`                    | Apache 2.0 |
| Local DB           | Room + KSP                                | Apache 2.0 |
| Paging             | Paging 3 (`androidx.paging:*`)            | Apache 2.0 |
| Biometrics         | `androidx.biometric`                      | Apache 2.0 |
| Prefs              | `androidx.datastore:datastore-preferences`| Apache 2.0 |
| Foldables          | `androidx.window`                         | Apache 2.0 |
| Tests              | JUnit 4                                   | EPL 1.0   |
| Android test tools | Espresso, AndroidX Test                   | Apache 2.0 |

**No Google-Play-policy-sensitive dependencies are used.** There is no
Firebase, no GMS, no Play Services, no analytics, no ads, no cloud sync,
and no network code whatsoever. The app does not declare the `INTERNET`
permission. User data never leaves the device.

## Building

This project uses Gradle 8.9, AGP 8.5.2, Kotlin 2.0.20, and Compose via BOM
2024.09.02.

```
# first time only — generates gradle-wrapper.jar
gradle wrapper

./gradlew :app:assembleDebug
```

Minimum SDK 26 (Android 8.0); target SDK 34.

## License

Project code is released under the Apache License 2.0. See `NOTICE` for
third-party attributions.
