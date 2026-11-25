# LumaPlay 2.0 — independent Android video companion

Original native Java implementation inspired by the requested navigation and a glass-style visual system. This is a separate app, not an in-place update or decompilation of YouTube Pro. No code, branding assets, or signing keys from the uploaded APK are included.

## Features
- Glass-inspired translucent panels, rounded controls, light/dark/AMOLED themes and independent vector app icon. These are Android-native approximations, not Apple's actual Liquid Glass rendering technology.
- Home, Shorts, Library, Settings; local search history (clearable), topic shortcuts, and validated YouTube HTTPS links.
- YouTube searches and Shorts open in the official YouTube application or an external browser. No native YouTube feed, account integration, ad blocking, downloads, background playback or custom YouTube playback is implemented.
- Native VideoView local media player, standard seek/pause controls, error state, and manual PiP when supported by device and settings. Codec support depends on Android hardware/software.
- Android document picker for selected videos only. No INTERNET permission, broad storage access, advertising SDK, overlay permission, analytics, or backup. External services may have their own data processing. Only appearance and up to 20 recent search terms are persisted in app-private preferences.
- No third-party runtime libraries; performance has not been benchmarked.

## Build on your computer
1. Extract this archive. Open the `LumaPlay` project directory in Android Studio (a version supporting AGP 8.13). Use JDK 17, Android SDK Platform 36, Build Tools 36.0.0, and Gradle 8.13. Internet access is necessary to fetch the Android Gradle plugin.
2. Use a locally installed Gradle 8.13: `gradle :app:assembleDebug :app:lintDebug`. Alternatively generate a wrapper via `gradle wrapper --gradle-version 8.13`, then use `./gradlew :app:assembleDebug` (Windows: `gradlew.bat :app:assembleDebug`). No Gradle wrapper binary is bundled because network access was unavailable.
3. APK output: `app/build/outputs/apk/debug/app-debug.apk`. Install via Android Studio or `adb install -r app/build/outputs/apk/debug/app-debug.apk` on a test device. Debug signing is only for testing.
4. For a release, use Android Studio > Build > Generate Signed App Bundle or APK and your own private signing key. The application ID is `com.targetmedia.lumaplay` so the original APK can remain installed separately.

### Alternative: GitHub Actions
Every push runs **Build Android debug APK**. You can also start it from the Actions tab. The workflow builds the debug app, runs Android lint, and verifies the APK signature. Open a successful run and download the `LumaPlay-debug` artifact. On your Android 8.0 or newer phone, extract the ZIP, open `LumaPlay-debug.apk`, and tap **Install**. If Android asks, allow your file manager to install the app.

## Device validation checklist (not yet executed)
- Android 8 (API 26), Android 15 (35), Android 16 (36), Android 17 (37): cold launch, screen rotation, system bars, navigation, theme persistence and font scaling.
- Home search and external handoff with/without official YouTube installed; Shorts and valid YouTube links; invalid links rejected; history deletion.
- Select local MP4, WebM, vertical clip, unavailable/unsupported file; play, pause, seek, rotate, background/foreground, back, and playback completion.
- Enter and exit PiP; verify playback and controls; test disabled PiP and close the floating window.
- Test gesture and three-button navigation, display cutouts, tablet and foldable layouts. Inspect manifest permissions and check for crashes/ANRs. Measure cold start, memory and battery before making any performance claims.

## Current verification status
A clean local build of version 2.0.0 passed `:app:assembleDebug` and `:app:lintDebug` using JDK 17, Gradle 8.13, and Android SDK 36. Android lint reported eight warnings and zero errors. Android SDK Build Tools verified the debug APK signature and alignment. Device testing remains pending.

References: https://developer.android.com/about/versions/16/setup-sdk ; https://developer.android.com/about/versions/17/setup-sdk ; https://developers.google.com/youtube/terms/developer-policies
