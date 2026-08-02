# 📱 FoxFocus Android Mobile Application (/app)

This is the self-contained Android Native application for **FoxFocus**, built with Kotlin, Jetpack Compose, Room Database, Firebase Auth, and Accessibility App Blocker Overlay Service.

---

## 📁 Directory Structure
```
app/
├── build.gradle.kts        # Root Gradle build script
├── settings.gradle.kts     # Gradle settings
├── gradle.properties       # Gradle properties
├── gradlew / gradlew.bat   # Gradle wrappers
├── google-services.json    # Firebase Android credentials
├── firestore.rules         # Cloud Firestore security rules
├── medias/                 # Sound effects & mascot artwork assets
└── app/                    # Android Application Module
    ├── build.gradle.kts    # Module dependencies & SDK config
    └── src/main/java/      # Kotlin source code (Auth, Blocker, Screens, Games)
```

---

## 🚀 Building the APK
```bash
# Build Android Debug APK
./gradlew assembleDebug

# Compiled APK location
app/build/outputs/apk/debug/app-debug.apk
```
