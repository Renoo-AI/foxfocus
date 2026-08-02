# 🌐 FoxFocus Web Application & Showcase Portal (/web)

This is the self-contained Web Application and Showcase Portal for **FoxFocus**, built with React 18, Vite, Tailwind CSS tokens, and Firebase JS SDK.

---

## 📁 Directory Structure
```
web/
├── index.html              # Main HTML entry point
├── package.json            # Dependencies & scripts
├── vite.config.js          # Vite configuration
├── src/                    # React Source Code
│   ├── components/         # Navbar, AuthModal, ProfileModal, QrPairingModal, ApkDownloadModal
│   ├── pages/              # LandingPage (/web), AppPage (/app), LeaderboardPage, DesignSystemPage
│   ├── utils/              # AudioEngine for sound effects and focus music
│   ├── firebase.js         # Firebase Auth & Firestore integration
│   ├── index.css           # Warm UI CSS tokens & glassmorphism
│   ├── App.jsx             # Main Router App
│   └── main.jsx            # Entry point
└── public/                 # Static Assets
    ├── medias/             # Sound effects (.wav), Focus Music (.mp3), Mascot Artworks (.webp)
    └── foxfocus-v1.0.0.apk # Direct download Android APK
```

---

## 🚀 Running & Building
```bash
# Install dependencies
npm install

# Run local development server
npm run dev

# Build production bundle (output to web/dist)
npm run build
```
