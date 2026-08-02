# 🦊 FoxFocus — Gamified Habit & App Blocker System

FoxFocus is a high-grade gamified habit tracking, focus enforcement, and app blocking platform built for both **Mobile (Android)** and **Web (Showcase & PC Client)**.

---

## 📁 Repository Structure (`/app` & `/web`)

The project is structured into two primary folders inside the repository:

```
Fox/
├── 📱 app/                  # Android Mobile Application (Kotlin + Jetpack Compose)
│   ├── src/main/java/      # Auth, Firestore Sync, App Blocker, Games & UI Screens
│   ├── src/main/res/       # Layouts, Drawables, SFX Wavs, Focus Audio MP3s & Strings
│   └── build.gradle.kts    # Android Dependencies & Build Config
│
├── 🌐 web/                  # Web Application & Showcase Portal (React + Vite + Firebase)
│   ├── src/components/     # Navbar, AuthModal, ProfileModal, QrPairingModal, ApkDownloadModal
│   ├── src/pages/          # LandingPage (/web), AppPage (/app), LeaderboardPage, DesignSystemPage
│   ├── public/medias/      # Sound effects (.wav), Focus Music (.mp3), Mascot Artworks (.webp)
│   └── package.json        # Web Dependencies & Scripts
│
├── 📜 firestore.rules      # Firestore Database Security Rules
├── 📜 google-services.json  # Firebase Android Configuration
└── 📦 foxfocus-v1.0.0.apk  # Compiled Release Android APK
```

---

## 🚀 Part 1: Android Mobile Application (`/app`)

The Android application is built natively in Kotlin using Jetpack Compose, Room Database, Firebase Auth, and system-level Accessibility & Overlay services for real-time app blocking.

### Core Features
* **Real Firebase Auth**: Google Sign-In, Email & Password, Anonymous Guest Mode.
* **App Blocker Guard**: Uses `ACCESSIBILITY_SERVICE` & `SYSTEM_ALERT_WINDOW` to detect foreground launches of distracting apps (Instagram, TikTok, YouTube, Snapchat, Games) and pop up the FoxFocus blocking overlay with sound alerts.
* **Mascot Leveling & Streaks**: Finn the Flame Fox levels up with XP, earns streak flames 🔥, and uses streak freeze shields 🛡️.
* **Full Profile & Settings**: Custom display name, bio, avatar selection, sound volume controls, focus audio player, and student `.edu` verification.

### Building the Android APK
```bash
# Build Debug APK
./gradlew assembleDebug

# Output APK path
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🌐 Part 2: Web Application & Showcase Portal (`/web`)

The Web portal serves as both the official showcase website (`/web`) and the desktop focus web application (`/app`).

### Web Routes & Portals
1. **`🌐 /web` (Showcase Landing Page)**: Showcase site with Finn mascot hero, live interactive App Blocker sound simulator, QR PC connect guide, and direct APK download button.
2. **`📱 /app` (Web Focus Application)**: Full web focus dashboard with ambient Lofi audio engine, focus timer, daily habit missions, and Fox Coins (`🪙 FC`) wallet.
3. **`🏆 /leaderboard` (Global Champions)**: Live rankings displaying real global focus champions backed by Firestore.
4. **`🧩 /design` (Warm UI Component System)**: Interactive catalog featuring 12 mobile phone screen previews and 100+ atomic UI components.
5. **`💻 PC Link (QR Code)`**: WhatsApp & Telegram style 2-second QR pairing connecting Phone sessions to PC screens in real-time.

### Running & Building the Web App
```bash
# Navigate to web directory
cd web

# Install dependencies
npm install

# Start local dev server (port 3000)
npm run dev

# Build production bundle
npm run build
```

---

## 🎁 Free Premium Status
All accounts (Web & Mobile) are automatically granted **Free Premium Status** (`isPremium = true`). Users enjoy unlimited focus sessions, cloud sync, custom mascot avatars, and advanced analytics with zero paywalls.

---

## 🔗 GitHub Repository
* **Repository**: [https://github.com/Renoo-AI/foxfocus.git](https://github.com/Renoo-AI/foxfocus.git)
* **Direct APK Download**: Available at `foxfocus-v1.0.0.apk` and inside `web/public/foxfocus-v1.0.0.apk`.
