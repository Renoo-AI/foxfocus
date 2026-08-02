import React from 'react';
import { Shield, Trophy, QrCode, Download, User, LogIn, Flame, Sparkles, Volume2, VolumeX, Layers } from 'lucide-react';
import { audioEngine } from '../utils/AudioEngine';

export default function Navbar({ 
  currentTab, 
  setCurrentTab, 
  user, 
  userProfile, 
  onOpenAuth, 
  onOpenProfile, 
  onOpenPairing,
  onOpenApkModal
}) {
  const [muted, setMuted] = React.useState(audioEngine.soundMuted);

  const toggleSound = () => {
    const isMuted = audioEngine.toggleSfx();
    setMuted(isMuted);
  };

  return (
    <header className="sticky top-0 z-50 glass-panel rounded-none border-x-0 border-t-0 px-4 py-3 mb-6">
      <div className="max-w-7xl mx-auto flex items-center justify-between gap-4">
        
        {/* Brand Logo & Title */}
        <div 
          className="flex items-center gap-3 cursor-pointer group"
          onClick={() => setCurrentTab('web')}
        >
          <div className="relative">
            <img 
              src="/medias/app-logo.webp" 
              alt="FoxFocus Logo" 
              className="w-10 h-10 rounded-xl object-cover shadow-md group-hover:scale-105 transition-transform" 
            />
            <div className="absolute -top-1 -right-1 bg-amber-500 rounded-full w-3.5 h-3.5 border-2 border-slate-900 animate-pulse" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="font-extrabold text-xl tracking-tight text-amber-500">FoxFocus</span>
              <span className="badge-premium">
                <Sparkles className="w-3 h-3 text-amber-300" /> Free Premium
              </span>
            </div>
            <p className="text-xs text-amber-200/60 font-medium hidden sm:block">Gamified App Blocker & Focus System</p>
          </div>
        </div>

        {/* Navigation Tabs */}
        <nav className="hidden md:flex items-center gap-1 bg-amber-950/40 p-1.5 rounded-2xl border border-amber-900/40">
          <button
            onClick={() => { audioEngine.playSfx('sfx_coin_claim.wav'); setCurrentTab('web'); }}
            className={`px-3.5 py-2 rounded-xl font-bold text-sm transition-all flex items-center gap-2 ${
              currentTab === 'web' 
                ? 'bg-amber-500 text-slate-950 shadow-md scale-[1.02]' 
                : 'text-amber-100/70 hover:text-amber-100 hover:bg-amber-900/30'
            }`}
          >
            🌐 Showcase /web
          </button>
          
          <button
            onClick={() => { audioEngine.playSfx('sfx_coin_claim.wav'); setCurrentTab('app'); }}
            className={`px-3.5 py-2 rounded-xl font-bold text-sm transition-all flex items-center gap-2 ${
              currentTab === 'app' 
                ? 'bg-amber-500 text-slate-950 shadow-md scale-[1.02]' 
                : 'text-amber-100/70 hover:text-amber-100 hover:bg-amber-900/30'
            }`}
          >
            📱 Web App /app
          </button>

          <button
            onClick={() => { audioEngine.playSfx('sfx_coin_claim.wav'); setCurrentTab('leaderboard'); }}
            className={`px-3.5 py-2 rounded-xl font-bold text-sm transition-all flex items-center gap-2 ${
              currentTab === 'leaderboard' 
                ? 'bg-amber-500 text-slate-950 shadow-md scale-[1.02]' 
                : 'text-amber-100/70 hover:text-amber-100 hover:bg-amber-900/30'
            }`}
          >
            <Trophy className="w-4 h-4" /> Leaderboard
          </button>

          <button
            onClick={() => { audioEngine.playSfx('sfx_coin_claim.wav'); setCurrentTab('design'); }}
            className={`px-3.5 py-2 rounded-xl font-bold text-sm transition-all flex items-center gap-2 ${
              currentTab === 'design' 
                ? 'bg-amber-500 text-slate-950 shadow-md scale-[1.02]' 
                : 'text-amber-100/70 hover:text-amber-100 hover:bg-amber-900/30'
            }`}
          >
            <Layers className="w-4 h-4" /> Warm UI Kit (300+)
          </button>
        </nav>

        {/* Action Controls & Profile / Auth */}
        <div className="flex items-center gap-3">
          
          {/* Sound Toggle */}
          <button 
            onClick={toggleSound}
            className="p-2.5 rounded-xl bg-amber-900/30 hover:bg-amber-900/60 border border-amber-800/40 text-amber-200 transition-colors"
            title="Toggle Sound Effects"
          >
            {muted ? <VolumeX className="w-5 h-5 text-red-400" /> : <Volume2 className="w-5 h-5 text-emerald-400" />}
          </button>

          {/* PC Link QR Code */}
          <button
            onClick={() => { audioEngine.playSfx('sfx_coin_claim.wav'); onOpenPairing(); }}
            className="btn-secondary text-sm hidden sm:inline-flex"
            title="Connect Phone with PC QR Code"
          >
            <QrCode className="w-4 h-4 text-amber-400" /> PC Connect
          </button>

          {/* Download APK Button */}
          <button
            onClick={() => { audioEngine.playSfx('sfx_coin_claim.wav'); onOpenApkModal(); }}
            className="btn-gold text-sm"
          >
            <Download className="w-4 h-4" /> Download APK
          </button>

          {/* User Auth / Profile Badge */}
          {user ? (
            <button
              onClick={() => { audioEngine.playSfx('sfx_coin_claim.wav'); onOpenProfile(); }}
              className="flex items-center gap-2 p-1.5 pr-3 rounded-2xl bg-amber-900/40 hover:bg-amber-900/70 border border-amber-500/40 transition-all cursor-pointer group"
            >
              <img 
                src={`/medias/${userProfile?.avatarId || 'finn_crown'}.webp`} 
                alt="Avatar" 
                className="w-8 h-8 rounded-xl object-cover bg-amber-950 p-0.5 border border-amber-400/50"
                onError={(e) => { e.target.src = '/medias/app-logo.webp'; }}
              />
              <div className="text-left hidden sm:block">
                <p className="text-xs font-bold text-amber-100 group-hover:text-amber-400 transition-colors">
                  {userProfile?.displayName || 'Fox User'}
                </p>
                <p className="text-[10px] text-amber-400 font-semibold flex items-center gap-1">
                  <Flame className="w-3 h-3 text-orange-500 fill-orange-500" /> {userProfile?.streakDays || 12}d Flame
                </p>
              </div>
            </button>
          ) : (
            <button
              onClick={() => { audioEngine.playSfx('sfx_coin_claim.wav'); onOpenAuth(); }}
              className="btn-primary text-sm"
            >
              <LogIn className="w-4 h-4" /> Sign In / Guest
            </button>
          )}

        </div>

      </div>

      {/* Mobile Nav Bar */}
      <div className="flex md:hidden items-center justify-around mt-3 pt-2 border-t border-amber-900/30">
        <button
          onClick={() => setCurrentTab('web')}
          className={`text-xs font-bold px-3 py-1.5 rounded-lg ${currentTab === 'web' ? 'bg-amber-500 text-slate-950' : 'text-amber-200/70'}`}
        >
          🌐 Showcase
        </button>
        <button
          onClick={() => setCurrentTab('app')}
          className={`text-xs font-bold px-3 py-1.5 rounded-lg ${currentTab === 'app' ? 'bg-amber-500 text-slate-950' : 'text-amber-200/70'}`}
        >
          📱 Web App
        </button>
        <button
          onClick={() => setCurrentTab('leaderboard')}
          className={`text-xs font-bold px-3 py-1.5 rounded-lg ${currentTab === 'leaderboard' ? 'bg-amber-500 text-slate-950' : 'text-amber-200/70'}`}
        >
          🏆 Ranks
        </button>
        <button
          onClick={() => setCurrentTab('design')}
          className={`text-xs font-bold px-3 py-1.5 rounded-lg ${currentTab === 'design' ? 'bg-amber-500 text-slate-950' : 'text-amber-200/70'}`}
        >
          🧩 UI Kit
        </button>
      </div>
    </header>
  );
}
