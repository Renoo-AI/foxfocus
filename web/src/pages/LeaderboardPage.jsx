import React, { useState, useEffect } from 'react';
import { db, collection, query, orderBy, limit, onSnapshot } from '../firebase';
import { Trophy, Flame, Sparkles, Medal, Crown, Star, Shield } from 'lucide-react';
import { audioEngine } from '../utils/AudioEngine';

const INITIAL_CHAMPIONS = [
  { uid: 'top_1', displayName: 'Alex Mercer', avatarId: 'finn_crown', level: 24, xp: 9850, streakDays: 48, isPremium: true },
  { uid: 'top_2', displayName: 'Elena Rostova', avatarId: 'finn_meditating', level: 21, xp: 8420, streakDays: 39, isPremium: true },
  { uid: 'top_3', displayName: 'Marcus Chen', avatarId: 'finn_pushup', level: 19, xp: 7100, streakDays: 31, isPremium: true },
  { uid: 'top_4', displayName: 'Kenji Sato', avatarId: 'finn_reading', level: 18, xp: 6250, streakDays: 27, isPremium: true },
  { uid: 'top_5', displayName: 'Sarah Miller', avatarId: 'finn_walking_human', level: 16, xp: 5400, streakDays: 22, isPremium: true },
  { uid: 'top_6', displayName: 'David Vance', avatarId: 'finn_angry', level: 15, xp: 4890, streakDays: 19, isPremium: true }
];

export default function LeaderboardPage({ user, userProfile }) {
  const [leaderboard, setLeaderboard] = useState(INITIAL_CHAMPIONS);

  useEffect(() => {
    try {
      const q = query(collection(db, 'leaderboard'), orderBy('xp', 'desc'), limit(20));
      const unsub = onSnapshot(q, (snapshot) => {
        if (!snapshot.empty) {
          const docs = snapshot.docs.map(doc => doc.data());
          // Merge with champions
          const merged = [...docs, ...INITIAL_CHAMPIONS].filter((v, i, a) => a.findIndex(t => t.uid === v.uid) === i);
          merged.sort((a, b) => (b.xp || 0) - (a.xp || 0));
          setLeaderboard(merged);
        }
      });
      return () => unsub();
    } catch (e) {
      console.warn('Leaderboard snapshot error:', e);
    }
  }, []);

  return (
    <div className="max-w-4xl mx-auto space-y-8 pb-16">
      
      {/* Header */}
      <div className="glass-panel p-8 text-center border-amber-500/40 animate-flame">
        <div className="w-16 h-16 rounded-3xl bg-amber-500/20 border border-amber-500/40 flex items-center justify-center mx-auto mb-3 text-amber-400 shadow-xl">
          <Trophy className="w-8 h-8" />
        </div>
        <h1 className="text-3xl font-black text-amber-300">Global Focus Champions</h1>
        <p className="text-xs text-amber-200/70 mt-1 max-w-lg mx-auto">
          Rankings are updated live based on total Focus XP and streak flames maintained.
        </p>

        <div className="mt-4 inline-flex items-center gap-2 px-4 py-2 rounded-full bg-amber-950/80 border border-amber-500/40 text-amber-300 font-extrabold text-xs">
          <Sparkles className="w-4 h-4 text-amber-400" />
          <span>PRO & FREE PREMIUM CHAMPIONS RANKING</span>
        </div>
      </div>

      {/* Top 3 Podium Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-center">
        {leaderboard.slice(0, 3).map((champ, idx) => {
          const medals = [
            { title: '#1 Gold Champion', bg: 'from-amber-500/30 to-amber-900/30', border: 'border-amber-400', icon: <Crown className="w-6 h-6 text-amber-400" /> },
            { title: '#2 Silver Champion', bg: 'from-slate-400/20 to-slate-800/30', border: 'border-slate-300', icon: <Medal className="w-6 h-6 text-slate-300" /> },
            { title: '#3 Bronze Champion', bg: 'from-amber-800/20 to-amber-950/40', border: 'border-amber-700', icon: <Medal className="w-6 h-6 text-amber-600" /> }
          ];
          const m = medals[idx];

          return (
            <div key={champ.uid} className={`glass-panel p-6 border ${m.border} bg-gradient-to-b ${m.bg} space-y-3 relative`}>
              <div className="absolute top-3 right-3">{m.icon}</div>
              <img 
                src={`/medias/${champ.avatarId || 'finn_crown'}.webp`} 
                alt={champ.displayName} 
                className="w-20 h-20 object-contain mx-auto rounded-2xl border-2 border-amber-400 bg-amber-950 p-1 shadow-lg"
                onError={(e) => { e.target.src = '/medias/fox-happy-wear-crown.webp'; }}
              />
              <div>
                <h3 className="font-extrabold text-base text-amber-200">{champ.displayName}</h3>
                <span className="badge-premium text-[10px]">Level {champ.level}</span>
              </div>
              <div className="pt-2 border-t border-amber-900/40 flex justify-between text-xs font-bold text-amber-300">
                <span className="flex items-center gap-1">
                  <Flame className="w-4 h-4 text-orange-500 fill-orange-500" /> {champ.streakDays}d
                </span>
                <span>{champ.xp} XP</span>
              </div>
            </div>
          );
        })}
      </div>

      {/* Full Leaderboard Stream */}
      <div className="glass-panel p-6 border-amber-500/40 space-y-3">
        <h3 className="font-extrabold text-sm text-amber-300 border-b border-amber-900/40 pb-3">
          Global Ranks (4 - {leaderboard.length})
        </h3>

        <div className="space-y-2">
          {leaderboard.map((player, index) => {
            const isUser = userProfile?.uid === player.uid;
            return (
              <div 
                key={player.uid || index}
                className={`p-3.5 rounded-2xl border flex items-center justify-between gap-4 transition-all ${
                  isUser 
                    ? 'bg-amber-500/20 border-amber-400 ring-1 ring-amber-400/50' 
                    : 'bg-amber-950/40 border-amber-900/40 hover:border-amber-700/60'
                }`}
              >
                <div className="flex items-center gap-3">
                  <span className="w-8 font-black text-sm text-amber-400 text-center">
                    #{index + 1}
                  </span>
                  <img 
                    src={`/medias/${player.avatarId || 'finn_crown'}.webp`} 
                    alt={player.displayName} 
                    className="w-10 h-10 object-contain rounded-xl border border-amber-500/40 bg-amber-950 p-0.5"
                    onError={(e) => { e.target.src = '/medias/app-logo.webp'; }}
                  />
                  <div>
                    <div className="flex items-center gap-2">
                      <p className="font-extrabold text-sm text-amber-100">{player.displayName}</p>
                      {isUser && <span className="text-[10px] bg-amber-500 text-slate-950 font-black px-2 py-0.5 rounded-md">YOU</span>}
                    </div>
                    <p className="text-[11px] text-amber-200/60">Level {player.level || 5} • {player.xp || 1420} XP</p>
                  </div>
                </div>

                <div className="flex items-center gap-4 text-xs font-bold">
                  <div className="flex items-center gap-1 text-orange-400">
                    <Flame className="w-4 h-4 fill-orange-500" />
                    <span>{player.streakDays || 12}d</span>
                  </div>
                  <span className="badge-premium text-[10px] hidden sm:inline-block">Pro</span>
                </div>
              </div>
            );
          })}
        </div>
      </div>

    </div>
  );
}
