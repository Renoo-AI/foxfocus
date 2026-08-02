import React, { useState, useEffect } from 'react';
import { 
  Flame, 
  Sparkles, 
  Play, 
  Pause, 
  RotateCcw, 
  CheckCircle2, 
  Circle, 
  Coins, 
  Gem, 
  ShieldCheck, 
  Volume2, 
  VolumeX, 
  Lock, 
  Sliders, 
  Edit3,
  User,
  Music
} from 'lucide-react';
import { audioEngine } from '../utils/AudioEngine';

const MUSIC_TRACKS = [
  { id: 'lo-fi-1.mp3', name: 'Lofi Chill Beats' },
  { id: 'lo-fi-2.mp3', name: 'Deep Work Synth' },
  { id: 'lo-fi-3.mp3', name: 'Rainy Night Lofi' },
  { id: 'piano-music.mp3', name: 'Calm Piano Study' },
  { id: 'piano-powerful-music-8min.mp3', name: 'Powerful Piano Flow' }
];

export default function AppPage({ user, userProfile, onOpenProfile, onOpenAuth }) {
  const [timerActive, setTimerActive] = useState(false);
  const [timeLeft, setTimeLeft] = useState(1500); // 25 min
  const [initialTime, setInitialTime] = useState(1500);
  const [selectedMusic, setSelectedMusic] = useState('lo-fi-1.mp3');
  const [isMusicPlaying, setIsMusicPlaying] = useState(false);

  // Habits State
  const [habits, setHabits] = useState([
    { id: 1, title: '25 Min Deep Work Session', rewardCoins: 50, completed: false, category: 'Work' },
    { id: 2, title: 'No Instagram Before 12 PM', rewardCoins: 30, completed: true, category: 'Block' },
    { id: 3, title: 'Read 15 Pages of Book', rewardCoins: 40, completed: false, category: 'Mind' },
    { id: 4, title: 'Zero Doomscrolling Night', rewardCoins: 60, completed: false, category: 'Sleep' }
  ]);

  const [coins, setCoins] = useState(userProfile?.coinBalance || 1250);
  const [streakDays, setStreakDays] = useState(userProfile?.streakDays || 12);

  // Timer interval
  useEffect(() => {
    let interval = null;
    if (timerActive && timeLeft > 0) {
      interval = setInterval(() => {
        setTimeLeft((prev) => prev - 1);
      }, 1000);
    } else if (timeLeft === 0 && timerActive) {
      setTimerActive(false);
      audioEngine.stopBgMusic();
      setIsMusicPlaying(false);
      audioEngine.playSfx('sfx_badge_unlocked.wav');
      setCoins((c) => c + 100);
      alert('🎉 Focus Session Completed! You earned +100 Fox Coins! 🪙');
    }
    return () => clearInterval(interval);
  }, [timerActive, timeLeft]);

  const toggleTimer = () => {
    if (!timerActive) {
      audioEngine.playSfx('sfx_coin_claim.wav');
      audioEngine.playBgMusic(selectedMusic);
      setIsMusicPlaying(true);
      setTimerActive(true);
    } else {
      audioEngine.stopBgMusic();
      setIsMusicPlaying(false);
      setTimerActive(false);
    }
  };

  const resetTimer = (newMins = 25) => {
    audioEngine.playSfx('sfx_coin_claim.wav');
    audioEngine.stopBgMusic();
    setIsMusicPlaying(false);
    setTimerActive(false);
    setInitialTime(newMins * 60);
    setTimeLeft(newMins * 60);
  };

  const claimHabit = (id, reward) => {
    audioEngine.playSfx('sfx_coin_claim.wav');
    setHabits(habits.map(h => h.id === id ? { ...h, completed: true } : h));
    setCoins(c => c + reward);
  };

  const formatTime = (secs) => {
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  return (
    <div className="space-y-6 max-w-6xl mx-auto pb-16">
      
      {/* Top Banner Greeting & Wallet */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 glass-panel p-6 border-amber-500/40">
        <div className="flex items-center gap-3">
          <img 
            src={`/medias/${userProfile?.avatarId || 'finn_crown'}.webp`} 
            alt="Avatar" 
            className="w-14 h-14 rounded-2xl object-cover border-2 border-amber-400 bg-amber-950 p-1 shadow-lg"
            onError={(e) => { e.target.src = '/medias/app-logo.webp'; }}
          />
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-xl font-black text-amber-300">
                Welcome back, {userProfile?.displayName || 'Fox Master'}!
              </h1>
              <span className="badge-premium">
                <Sparkles className="w-3 h-3 text-amber-300" /> Free Premium
              </span>
            </div>
            <p className="text-xs text-amber-200/70">
              {userProfile?.bio || 'Crushing distraction & mastering daily focus 🦊🔥'}
            </p>
          </div>
        </div>

        {/* Currency Pill */}
        <div className="flex items-center gap-3">
          <div className="flex items-center gap-2 bg-amber-950/80 px-4 py-2 rounded-2xl border border-amber-500/40">
            <Flame className="w-5 h-5 text-orange-500 fill-orange-500" />
            <span className="font-black text-sm text-orange-400">{streakDays}d Flame</span>
          </div>
          <div className="flex items-center gap-2 bg-amber-950/80 px-4 py-2 rounded-2xl border border-amber-500/40">
            <Coins className="w-5 h-5 text-amber-400" />
            <span className="font-black text-sm text-amber-300">{coins} FC</span>
          </div>
          <button onClick={onOpenProfile} className="btn-secondary p-2.5 text-xs">
            <Edit3 className="w-4 h-4 text-amber-400" />
          </button>
        </div>
      </div>

      {/* Guest Mode Warning Banner */}
      {(!user || user.isAnonymous) && (
        <div className="p-4 rounded-2xl bg-amber-950/80 border border-amber-500/60 flex flex-col sm:flex-row items-center justify-between gap-3 text-xs">
          <div className="flex items-center gap-3 text-amber-200">
            <Lock className="w-5 h-5 text-amber-400 flex-shrink-0" />
            <span>
              <strong>Guest Mode Active:</strong> Your session is local. Sign in with Google or Email to back up progress and customize your global leaderboard rank!
            </span>
          </div>
          <button onClick={onOpenAuth} className="btn-primary text-xs py-2 px-4 whitespace-nowrap">
            Sign In Free
          </button>
        </div>
      )}

      {/* Main Focus Control Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        
        {/* Left Column: Mascot & Timer */}
        <div className="lg:col-span-7 glass-panel p-8 border-amber-500/40 text-center space-y-6 animate-flame">
          
          <div className="flex items-center justify-between border-b border-amber-900/40 pb-4">
            <span className="text-xs font-bold text-amber-400 uppercase tracking-wider">
              Focus Mode & Ambient Engine
            </span>
            <span className="text-xs font-extrabold text-amber-300">
              Level {userProfile?.level || 5} • {userProfile?.xp || 1420} XP
            </span>
          </div>

          {/* Mascot Artwork */}
          <div className="relative inline-block">
            <img 
              src={timerActive ? '/medias/meditating-fox-close-eyes.webp' : `/medias/${userProfile?.avatarId || 'finn_crown'}.webp`} 
              alt="Finn Mascot" 
              className="w-48 h-48 object-contain mx-auto transition-all transform hover:scale-105"
              onError={(e) => { e.target.src = '/medias/fox-happy-wear-crown.webp'; }}
            />
            {timerActive && (
              <div className="absolute -bottom-2 left-1/2 -translate-x-1/2 bg-orange-500 text-slate-950 font-black text-[10px] uppercase px-3 py-0.5 rounded-full shadow-lg">
                Deep Work Meditating
              </div>
            )}
          </div>

          {/* Digital Clock */}
          <div>
            <div className="text-6xl font-black font-mono text-amber-300 tracking-wider my-2">
              {formatTime(timeLeft)}
            </div>
            <p className="text-xs text-amber-200/60 font-semibold">
              {timerActive ? '🎧 Focus audio playing • Stay on task' : 'Select target time & hit start'}
            </p>
          </div>

          {/* Preset Time Buttons */}
          <div className="flex justify-center gap-2">
            {[15, 25, 45, 60].map((mins) => (
              <button
                key={mins}
                onClick={() => resetTimer(mins)}
                className={`px-3 py-1.5 rounded-xl border text-xs font-extrabold transition-all ${
                  initialTime === mins * 60 
                    ? 'bg-amber-500 text-slate-950 border-amber-400' 
                    : 'bg-amber-950/40 border-amber-900/50 text-amber-200/70 hover:text-amber-200'
                }`}
              >
                {mins}m
              </button>
            ))}
          </div>

          {/* Audio Track Selector */}
          <div className="bg-amber-950/50 p-3 rounded-2xl border border-amber-900/40 text-left space-y-2">
            <label className="block text-[11px] font-bold text-amber-300 uppercase tracking-wider flex items-center gap-2">
              <Music className="w-3.5 h-3.5 text-amber-400" /> Ambient Focus Track
            </label>
            <select
              value={selectedMusic}
              onChange={(e) => {
                setSelectedMusic(e.target.value);
                if (timerActive) {
                  audioEngine.playBgMusic(e.target.value);
                }
              }}
              className="input-base text-xs py-2"
            >
              {MUSIC_TRACKS.map((t) => (
                <option key={t.id} value={t.id} className="bg-slate-900 text-amber-100">
                  🎵 {t.name}
                </option>
              ))}
            </select>
          </div>

          {/* Main Controls */}
          <div className="flex justify-center items-center gap-4 pt-2">
            <button onClick={toggleTimer} className="btn-gold text-base py-4 px-10 shadow-xl">
              {timerActive ? <Pause className="w-5 h-5" /> : <Play className="w-5 h-5" />}
              <span>{timerActive ? 'Pause Session' : 'Start Focus Session'}</span>
            </button>
            <button onClick={() => resetTimer(25)} className="btn-secondary p-4">
              <RotateCcw className="w-5 h-5 text-amber-400" />
            </button>
          </div>

        </div>

        {/* Right Column: Daily Habits & App Locklist */}
        <div className="lg:col-span-5 space-y-6">
          
          {/* Today's Gamified Missions */}
          <div className="glass-panel p-6 border-amber-500/40 space-y-4">
            <div className="flex items-center justify-between border-b border-amber-900/40 pb-3">
              <h3 className="font-extrabold text-sm text-amber-300">Today's Focus Missions</h3>
              <span className="text-xs text-amber-400 font-bold">
                {habits.filter(h => h.completed).length} / {habits.length} Done
              </span>
            </div>

            <div className="space-y-3">
              {habits.map((habit) => (
                <div 
                  key={habit.id}
                  className={`p-3.5 rounded-2xl border flex items-center justify-between gap-3 transition-all ${
                    habit.completed 
                      ? 'bg-amber-950/20 border-emerald-500/40 opacity-80' 
                      : 'bg-amber-950/50 border-amber-900/50 hover:border-amber-700/60'
                  }`}
                >
                  <div className="flex items-center gap-3">
                    {habit.completed ? (
                      <CheckCircle2 className="w-5 h-5 text-emerald-400 flex-shrink-0" />
                    ) : (
                      <Circle className="w-5 h-5 text-amber-500/60 flex-shrink-0" />
                    )}
                    <div>
                      <p className={`text-xs font-bold ${habit.completed ? 'line-through text-amber-200/50' : 'text-amber-100'}`}>
                        {habit.title}
                      </p>
                      <span className="text-[10px] text-amber-400/80 font-semibold">+{habit.rewardCoins} Fox Coins</span>
                    </div>
                  </div>

                  {!habit.completed && (
                    <button
                      onClick={() => claimHabit(habit.id, habit.rewardCoins)}
                      className="btn-primary text-[11px] py-1.5 px-3"
                    >
                      Claim
                    </button>
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* Active Target App Blocklist */}
          <div className="glass-panel p-6 border-amber-500/40 space-y-4">
            <div className="flex items-center justify-between border-b border-amber-900/40 pb-3">
              <h3 className="font-extrabold text-sm text-amber-300">Target Blocklist Guard</h3>
              <button onClick={onOpenProfile} className="text-xs text-amber-400 font-bold hover:underline">
                Edit List
              </button>
            </div>

            <div className="grid grid-cols-2 gap-2">
              {(userProfile?.targetApps || ['Instagram', 'TikTok', 'YouTube', 'Games']).map((appName) => (
                <div key={appName} className="p-2.5 rounded-xl bg-orange-950/60 border border-orange-500/40 flex items-center justify-between">
                  <span className="text-xs font-bold text-orange-200">{appName}</span>
                  <Lock className="w-3.5 h-3.5 text-orange-400" />
                </div>
              ))}
            </div>
          </div>

        </div>

      </div>

    </div>
  );
}
