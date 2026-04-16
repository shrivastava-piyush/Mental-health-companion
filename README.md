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

## Prerequisites

| Tool | Minimum version | How to verify |
|------|----------------|---------------|
| **JDK** | 17 | `java -version` |
| **Android Studio** | Hedgehog (2023.1.1) or newer | *Help → About* |
| **Android SDK** | API 34 (compile), API 26+ (min) | SDK Manager |
| **Gradle** | 8.9 (bundled via wrapper) | `./gradlew --version` |

> You do **not** need to install Gradle globally — the wrapper downloads the
> correct version on first run. You **do** need the JDK and the Android SDK
> (Android Studio installs both).

---

## Building & running on macOS

### Option A — Android Studio (recommended)

1. **Install Android Studio**

   Download from <https://developer.android.com/studio> and run the `.dmg`
   installer. On first launch, let the setup wizard install the default SDK
   (API 34) and accept all licences.

2. **Install JDK 17** (if not bundled)

   Android Studio ships a JBR (JetBrains Runtime) based on JDK 17. If you
   need a standalone JDK:
   ```bash
   brew install openjdk@17
   export JAVA_HOME=$(/usr/libexec/java_home -v 17)
   ```

3. **Clone and open**
   ```bash
   git clone https://github.com/shrivastava-piyush/Mental-health-companion.git
   cd Mental-health-companion
   ```
   In Android Studio: *File → Open…* → select the `Mental-health-companion`
   folder. Gradle sync starts automatically.

4. **Generate the Gradle wrapper** (first time only, if `gradle-wrapper.jar`
   is missing)
   ```bash
   # Using the Gradle installed by Android Studio:
   /Applications/Android\ Studio.app/Contents/jbr/Contents/Home/bin/java \
     -version  # confirm JDK works

   # Or install gradle via Homebrew and run:
   brew install gradle
   gradle wrapper
   ```

5. **Build**
   ```bash
   ./gradlew :app:assembleDebug
   ```
   The unsigned debug APK is written to
   `app/build/outputs/apk/debug/app-debug.apk`.

6. **Run on a device / emulator**

   *Via Android Studio:* click the green **Run ▶** button. Select a connected
   device or create an emulator (API 26+, x86_64 image recommended).

   *Via command line:*
   ```bash
   # List connected devices
   adb devices

   # Install and launch
   adb install app/build/outputs/apk/debug/app-debug.apk
   adb shell am start -n com.wellness.companion/.MainActivity
   ```

7. **Run tests**
   ```bash
   ./gradlew :app:testDebugUnitTest          # JUnit (JVM)
   ./gradlew :app:connectedDebugAndroidTest  # Espresso (needs device)
   ```

### Option B — terminal only (no Android Studio)

```bash
# 1. Install prerequisites
brew install openjdk@17 gradle
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"

# 2. Install SDK components (if not already present)
sdkmanager "platforms;android-34" "build-tools;34.0.0" "platform-tools"
sdkmanager --licenses  # accept all

# 3. Clone, wrap, build
git clone https://github.com/shrivastava-piyush/Mental-health-companion.git
cd Mental-health-companion
gradle wrapper
./gradlew :app:assembleDebug

# 4. Install on device
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## Building & running on Windows

### Option A — Android Studio (recommended)

1. **Install Android Studio**

   Download the `.exe` installer from <https://developer.android.com/studio>.
   Run the setup wizard — it installs the JBR (JDK 17), the Android SDK
   (API 34), and the emulator.

2. **Set environment variables** (the installer usually does this, verify in
   *System Properties → Environment Variables*):
   ```
   JAVA_HOME  = C:\Program Files\Android\Android Studio\jbr
   ANDROID_HOME = C:\Users\<you>\AppData\Local\Android\Sdk
   PATH += %ANDROID_HOME%\platform-tools
   ```

3. **Clone and open**
   ```powershell
   git clone https://github.com/shrivastava-piyush/Mental-health-companion.git
   cd Mental-health-companion
   ```
   In Android Studio: *File → Open…* → select the `Mental-health-companion`
   folder. Wait for Gradle sync to finish.

4. **Generate the Gradle wrapper** (first time only, if `gradle-wrapper.jar`
   is missing)

   Open the Android Studio terminal (*View → Tool Windows → Terminal*) and
   run:
   ```powershell
   # If gradle is on PATH (e.g. via scoop/chocolatey):
   gradle wrapper

   # Or use the bundled JDK to run the wrapper task from Android Studio's
   # built-in terminal — the IDE sets JAVA_HOME automatically.
   ```

   Alternatively, install Gradle via [Scoop](https://scoop.sh):
   ```powershell
   scoop install gradle
   gradle wrapper
   ```

5. **Build**
   ```powershell
   .\gradlew.bat :app:assembleDebug
   ```
   Output APK: `app\build\outputs\apk\debug\app-debug.apk`

6. **Run on a device / emulator**

   *Via Android Studio:* click **Run ▶**, pick a connected device or create
   an emulator (API 26+, x86_64 / x86 recommended for speed).

   *Via command line:*
   ```powershell
   adb devices
   adb install app\build\outputs\apk\debug\app-debug.apk
   adb shell am start -n com.wellness.companion/.MainActivity
   ```

7. **Run tests**
   ```powershell
   .\gradlew.bat :app:testDebugUnitTest
   .\gradlew.bat :app:connectedDebugAndroidTest
   ```

### Option B — terminal only (PowerShell / cmd, no Android Studio)

```powershell
# 1. Install JDK 17 (e.g. via winget or scoop)
winget install EclipseAdoptium.Temurin.17.JDK
# Or: scoop install temurin17-jdk

# 2. Install Android command-line tools
#    Download from https://developer.android.com/studio#command-line-tools-only
#    Unzip to C:\android-sdk\cmdline-tools\latest

$env:ANDROID_HOME = "C:\android-sdk"
$env:PATH += ";$env:ANDROID_HOME\cmdline-tools\latest\bin;$env:ANDROID_HOME\platform-tools"

sdkmanager "platforms;android-34" "build-tools;34.0.0" "platform-tools"
sdkmanager --licenses

# 3. Clone, wrap, build
git clone https://github.com/shrivastava-piyush/Mental-health-companion.git
cd Mental-health-companion

scoop install gradle   # if not already installed
gradle wrapper

.\gradlew.bat :app:assembleDebug

# 4. Install on device
adb install app\build\outputs\apk\debug\app-debug.apk
```

---

## Signing a release APK

Debug builds are auto-signed. For release:

```bash
# 1. Create a keystore (once)
keytool -genkey -v -keystore wellness.keystore \
  -alias wellness -keyalg RSA -keysize 2048 -validity 10000

# 2. Build signed APK
./gradlew :app:assembleRelease \
  -Pandroid.injected.signing.store.file=$PWD/wellness.keystore \
  -Pandroid.injected.signing.store.password=<password> \
  -Pandroid.injected.signing.key.alias=wellness \
  -Pandroid.injected.signing.key.password=<password>
```

> **Do not** commit `wellness.keystore` to version control.

---

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| `SDK location not found` | Set `ANDROID_HOME` env var or create `local.properties` with `sdk.dir=/path/to/sdk` |
| `Could not determine java version from '21'` | Set `JAVA_HOME` to a JDK 17 installation |
| `gradle-wrapper.jar not found` | Run `gradle wrapper` (needs Gradle installed globally once) |
| Emulator runs but app crashes on launch | Ensure emulator image is API 26+; check `adb logcat \| grep -i fatal` |
| `license for package … not accepted` | Run `sdkmanager --licenses` and accept all |

Minimum SDK 26 (Android 8.0); target SDK 34.

## License

Project code is released under the Apache License 2.0. See `NOTICE` for
third-party attributions.
