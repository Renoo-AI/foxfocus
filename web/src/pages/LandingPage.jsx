import React, { useState } from 'react';
import { 
  Sparkles, 
  Shield, 
  Flame, 
  Trophy, 
  Smartphone, 
  QrCode, 
  Download, 
  Play, 
  Pause, 
  CheckCircle, 
  Lock, 
  Star,
  Zap,
  Volume2
} from 'lucide-react';
import { audioEngine } from '../utils/AudioEngine';

export default function LandingPage({ onLaunchApp, onOpenApk, onOpenPairing, onOpenAuth }) {
  const [demoFocusing, setDemoFocusing] = useState(false);
  const [demoTimeLeft, setDemoTimeLeft] = useState(1500); // 25:00
  const [blockedApp, setBlockedApp] = useState(null);

  const toggleDemoTimer = () => {
    if (!demoFocusing) {
      audioEngine.playSfx('sfx_coin_claim.wav');
      audioEngine.playBgMusic('music_lofi_1.mp3');
      setDemoFocusing(true);
    } else {
      audioEngine.stopBgMusic();
      setDemoFocusing(false);
    }
  };

  const simulateAppBlock = (appName) => {
    setBlockedApp(appName);
    audioEngine.playSfx('sfx_app_blocked.wav');
  };

  const formatTime = (secs) => {
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  return (
    <div className="space-y-16 pb-16">
      
      {/* 🚀 Hero Section */}
      <section className="relative overflow-hidden glass-panel p-8 sm:p-12 border-amber-500/40">
        <div className="absolute top-0 right-0 -mt-12 -mr-12 w-96 h-96 bg-amber-500/10 rounded-full blur-3xl pointer-events-none" />
        
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
          
          <div className="lg:col-span-7 space-y-6 text-left">
            
            <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-amber-950/80 border border-amber-500/50 text-amber-300 font-extrabold text-xs uppercase tracking-wider">
              <Sparkles className="w-4 h-4 text-amber-400" />
              <span>Next-Gen $8M Gamified Focus Framework</span>
            </div>

            <h1 className="text-4xl sm:text-6xl font-black text-amber-100 tracking-tight leading-tight">
              Master Your Focus. <br />
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-amber-400 via-orange-400 to-amber-500">
                Destroy Distraction.
              </span>
            </h1>

            <p className="text-base sm:text-lg text-amber-200/80 font-medium max-w-2xl leading-relaxed">
              FoxFocus locks addicting apps with instant sound alerts, levels up your mascot Finn the Flame Fox, and syncs seamlessly between Android Mobile and PC Web via QR Code.
            </p>

            {/* Free Premium Callout */}
            <div className="p-3.5 rounded-2xl bg-amber-950/60 border border-amber-500/40 text-amber-300 font-extrabold text-sm flex items-center gap-3">
              <Star className="w-5 h-5 text-amber-400 fill-amber-400" />
              <span>🎉 ALL PREMIUM FEATURES ARE FREE FOR ALL USERS TODAY!</span>
            </div>

            {/* CTA Buttons */}
            <div className="flex flex-wrap items-center gap-4 pt-2">
              <button
                onClick={() => { audioEngine.playSfx('sfx_coin_claim.wav'); onLaunchApp(); }}
                className="btn-gold text-base py-4 px-8 shadow-xl"
              >
                <Zap className="w-5 h-5" /> Launch Web App /app
              </button>

              <button
                onClick={() => { audioEngine.playSfx('sfx_coin_claim.wav'); onOpenApk(); }}
                className="btn-primary text-base py-4 px-6 shadow-xl"
              >
                <Download className="w-5 h-5" /> Download APK (Direct)
              </button>

              <button
                onClick={() => { audioEngine.playSfx('sfx_coin_claim.wav'); onOpenPairing(); }}
                className="btn-secondary text-base py-4 px-6"
              >
                <QrCode className="w-5 h-5 text-amber-400" /> PC QR Connect
              </button>
            </div>

            {/* Stats row */}
            <div className="pt-6 border-t border-amber-900/40 grid grid-cols-3 gap-4 text-center sm:text-left">
              <div>
                <span className="block text-2xl font-black text-amber-400">2.1 Hours</span>
                <span className="text-xs text-amber-200/60 font-semibold">Avg Daily Savings</span>
              </div>
              <div>
                <span className="block text-2xl font-black text-amber-400">100% Free</span>
                <span className="text-xs text-amber-200/60 font-semibold">Premium Unlocked</span>
              </div>
              <div>
                <span className="block text-2xl font-black text-amber-400">4.9 ★★★★★</span>
                <span className="text-xs text-amber-200/60 font-semibold">Focus Rating</span>
              </div>
            </div>

          </div>

          {/* Hero Mascot & Live Interactive Preview */}
          <div className="lg:col-span-5 relative">
            <div className="glass-panel p-6 border-amber-400/50 animate-flame text-center relative overflow-hidden">
              <div className="absolute top-3 left-3">
                <span className="badge-premium">🔥 Active Session</span>
              </div>

              <img 
                src={demoFocusing ? '/medias/meditating-fox-close-eyes.webp' : '/medias/fox-happy-wear-crown.webp'} 
                alt="Finn Mascot" 
                className="w-48 h-48 object-contain mx-auto my-4 transition-all hover:scale-105"
              />

              <h3 className="text-2xl font-black text-amber-300">
                {demoFocusing ? 'Finn is Meditating...' : 'Finn the Flame Fox'}
              </h3>
              <p className="text-xs text-amber-200/70 mb-4">
                {demoFocusing ? 'Ambient Lofi music active • Distractions locked' : 'Level 14 • 18 Days Streak Flame'}
              </p>

              {/* Timer Display */}
              <div className="bg-amber-950/80 p-4 rounded-2xl border border-amber-900/60 mb-4 inline-block px-8">
                <span className="text-4xl font-black font-mono text-amber-400 tracking-wider">
                  {formatTime(demoTimeLeft)}
                </span>
              </div>

              {/* Controls */}
              <div className="flex justify-center gap-3">
                <button
                  onClick={toggleDemoTimer}
                  className={`btn-primary text-xs py-2.5 px-6 ${demoFocusing ? 'bg-orange-600' : ''}`}
                >
                  {demoFocusing ? <Pause className="w-4 h-4" /> : <Play className="w-4 h-4" />}
                  <span>{demoFocusing ? 'Pause Session' : 'Start Focus Session'}</span>
                </button>
              </div>

            </div>
          </div>

        </div>
      </section>

      {/* 🎯 Interactive App Blocker Simulator Section */}
      <section className="glass-panel p-8 border-amber-500/40 text-center">
        <div className="max-w-3xl mx-auto space-y-4 mb-8">
          <span className="px-3 py-1 rounded-full bg-orange-950/80 border border-orange-500/50 text-orange-400 font-extrabold text-xs uppercase tracking-wider">
            ⚡ Real-Time App Blocking Service
          </span>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-amber-300">
            Test the App Blocker Sound & Guard
          </h2>
          <p className="text-sm text-amber-200/70">
            Click any app below to simulate what happens when FoxFocus catches you opening distracting apps during focus mode!
          </p>
        </div>

        {/* App Trigger Grid */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 max-w-4xl mx-auto mb-8">
          {[
            { name: 'Instagram', icon: '/medias/instagram.png' },
            { name: 'TikTok', icon: '/medias/tiktok.png' },
            { name: 'YouTube', icon: '/medias/youtube.png' },
            { name: 'Snapchat', icon: '/medias/snapchat.png' }
          ].map((app) => (
            <button
              key={app.name}
              onClick={() => simulateAppBlock(app.name)}
              className="p-4 rounded-2xl bg-amber-950/40 hover:bg-amber-950/80 border border-amber-900/50 hover:border-orange-500/60 transition-all group text-center space-y-2"
            >
              <img src={app.icon} alt={app.name} className="w-10 h-10 object-contain mx-auto group-hover:scale-110 transition-transform" />
              <span className="block font-extrabold text-xs text-amber-200 group-hover:text-amber-400">
                Try Opening {app.name}
              </span>
            </button>
          ))}
        </div>

        {/* Blocked App Overlay Modal Mock */}
        {blockedApp && (
          <div className="modal-overlay">
            <div className="glass-panel w-full max-w-md p-6 border-red-500/60 text-center animate-flame">
              <img src="/medias/angry-fox.webp" alt="Angry Devil Fox" className="w-32 h-32 object-contain mx-auto mb-2" />
              <div className="p-2 rounded-full bg-red-950/80 border border-red-500/60 text-xs font-extrabold text-red-300 inline-block mb-2 px-4">
                🚫 {blockedApp} IS BLOCKED BY FOXFOCUS
              </div>
              <h3 className="text-xl font-extrabold text-red-400">Stop Doomscrolling!</h3>
              <p className="text-xs text-amber-200/80 mt-1 mb-4">
                Finn caught you attempting to launch {blockedApp}. Return to your focus mission to keep your streak flame alive!
              </p>
              <button
                onClick={() => { audioEngine.playSfx('sfx_coin_claim.wav'); setBlockedApp(null); }}
                className="btn-primary w-full py-2.5 text-xs bg-red-600 hover:bg-red-700"
              >
                Back to Deep Focus
              </button>
            </div>
          </div>
        )}

      </section>

      {/* 💻 QR Code Desktop Linkage Feature */}
      <section className="grid grid-cols-1 md:grid-cols-2 gap-8 items-center glass-panel p-8 border-amber-500/40">
        <div className="space-y-4 text-left">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-amber-950/80 border border-amber-500/40 text-amber-300 text-xs font-bold uppercase">
            <QrCode className="w-4 h-4 text-amber-400" /> WhatsApp / Telegram Web Linkage
          </div>
          <h2 className="text-3xl font-extrabold text-amber-300">
            Connect PC & Mobile Instantly via QR Code
          </h2>
          <p className="text-sm text-amber-200/80 leading-relaxed">
            No password typing required on PC! Open FoxFocus on your PC browser, scan the generated QR code with your Android phone, and instantly mirror your focus stats and session state.
          </p>
          <ul className="space-y-2 text-xs font-bold text-amber-200">
            <li className="flex items-center gap-2"><CheckCircle className="w-4 h-4 text-emerald-400" /> Instant 2-second QR pairing</li>
            <li className="flex items-center gap-2"><CheckCircle className="w-4 h-4 text-emerald-400" /> Real-time Firestore document mirroring</li>
            <li className="flex items-center gap-2"><CheckCircle className="w-4 h-4 text-emerald-400" /> Zero passwords exposed on public PC screens</li>
          </ul>
          <button onClick={onOpenPairing} className="btn-gold text-xs py-3 px-6">
            Test QR Pairing Now
          </button>
        </div>

        <div className="bg-amber-950/60 p-6 rounded-2xl border border-amber-900/50 text-center">
          <img src="/medias/cold-fox-16-9-wedget.jpeg" alt="PC Sync Preview" className="w-full h-48 object-cover rounded-xl border border-amber-500/30 mb-3" />
          <p className="text-xs font-extrabold text-amber-400">Desktop Web Sync Engine Active</p>
        </div>
      </section>

      {/* 🏆 Feature Grid */}
      <section className="grid grid-cols-1 sm:grid-cols-3 gap-6">
        
        <div className="glass-panel p-6 border-amber-500/30 text-left space-y-3">
          <div className="w-12 h-12 rounded-2xl bg-amber-500/20 border border-amber-500/40 flex items-center justify-center text-amber-400">
            <Flame className="w-6 h-6" />
          </div>
          <h3 className="text-lg font-extrabold text-amber-300">Streak Flame Engine</h3>
          <p className="text-xs text-amber-200/70">
            Build consecutive daily focus streaks. Earn streak shields, unlock crowns, and avoid frozen flame states.
          </p>
        </div>

        <div className="glass-panel p-6 border-amber-500/30 text-left space-y-3">
          <div className="w-12 h-12 rounded-2xl bg-amber-500/20 border border-amber-500/40 flex items-center justify-center text-amber-400">
            <Trophy className="w-6 h-6" />
          </div>
          <h3 className="text-lg font-extrabold text-amber-300">Global Leaderboard</h3>
          <p className="text-xs text-amber-200/70">
            Compete against real focus champions worldwide. Earn XP, level up your mascot, and rank #1 globally.
          </p>
        </div>

        <div className="glass-panel p-6 border-amber-500/30 text-left space-y-3">
          <div className="w-12 h-12 rounded-2xl bg-amber-500/20 border border-amber-500/40 flex items-center justify-center text-amber-400">
            <Volume2 className="w-6 h-6" />
          </div>
          <h3 className="text-lg font-extrabold text-amber-300">Ambient Lofi Soundscapes</h3>
          <p className="text-xs text-amber-200/70">
            High-definition lofi beat loops and calm piano tracks designed to supercharge your brain state during work sessions.
          </p>
        </div>

      </section>

    </div>
  );
}
