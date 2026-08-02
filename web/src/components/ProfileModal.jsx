import React, { useState, useEffect } from 'react';
import { db, doc, updateDoc, setDoc, auth, signOut } from '../firebase';
import { X, Save, Sparkles, Trophy, Shield, Flame, Check, User, LogOut } from 'lucide-react';
import { audioEngine } from '../utils/AudioEngine';

const AVATAR_OPTIONS = [
  { id: 'finn_crown', name: 'Royal Crown Finn', img: '/medias/fox-happy-wear-crown.webp' },
  { id: 'finn_meditating', name: 'Zen Meditating', img: '/medias/meditating-fox-close-eyes.webp' },
  { id: 'finn_pushup', name: 'Athletic Pushup', img: '/medias/pushup-fox.webp' },
  { id: 'finn_reading', name: 'Scholar Reader', img: '/medias/reading-book-foxy-stories-green-book.webp' },
  { id: 'finn_walking_human', name: 'Humanoid Executive', img: '/medias/fox-human-walking.webp' },
  { id: 'finn_angry', name: 'Focus Demon', img: '/medias/angry-fox.webp' },
  { id: 'finn_happy_coins', name: 'Coin Master', img: '/medias/happy-fox-coins.png' }
];

const AVAILABLE_APPS = [
  { name: 'Instagram', icon: '/medias/instagram.png' },
  { name: 'TikTok', icon: '/medias/tiktok.png' },
  { name: 'YouTube', icon: '/medias/youtube.png' },
  { name: 'Snapchat', icon: '/medias/snapchat.png' },
  { name: 'Reddit', icon: '/medias/reddit.png' },
  { name: 'X / Twitter', icon: '/medias/x-black-logo.png' },
  { name: 'Facebook', icon: '/medias/facebook.png' }
];

export default function ProfileModal({ isOpen, onClose, user, userProfile, onRequireAuth }) {
  const [displayName, setDisplayName] = useState(userProfile?.displayName || '');
  const [bio, setBio] = useState(userProfile?.bio || '');
  const [avatarId, setAvatarId] = useState(userProfile?.avatarId || 'finn_crown');
  const [targetApps, setTargetApps] = useState(userProfile?.targetApps || ['Instagram', 'TikTok', 'YouTube']);
  const [dailyGoalMinutes, setDailyGoalMinutes] = useState(userProfile?.dailyGoalMinutes || 60);
  const [saving, setSaving] = useState(false);
  const [successMsg, setSuccessMsg] = useState('');

  useEffect(() => {
    if (userProfile) {
      setDisplayName(userProfile.displayName || '');
      setBio(userProfile.bio || '');
      setAvatarId(userProfile.avatarId || 'finn_crown');
      setTargetApps(userProfile.targetApps || ['Instagram', 'TikTok', 'YouTube']);
      setDailyGoalMinutes(userProfile.dailyGoalMinutes || 60);
    }
  }, [userProfile]);

  if (!isOpen) return null;

  const toggleApp = (appName) => {
    audioEngine.playSfx('sfx_coin_claim.wav');
    if (targetApps.includes(appName)) {
      setTargetApps(targetApps.filter(a => a !== appName));
    } else {
      setTargetApps([...targetApps, appName]);
    }
  };

  const handleSave = async () => {
    if (!user || user.isAnonymous) {
      onRequireAuth('Please sign in with Google or Email to customize and save your profile identity to the cloud!');
      return;
    }

    setSaving(true);
    try {
      const userRef = doc(db, 'users', user.uid);
      const updateData = {
        displayName,
        bio,
        avatarId,
        targetApps,
        dailyGoalMinutes,
        updatedAt: new Date()
      };
      await updateDoc(userRef, updateData);

      // Also update public leaderboard snapshot
      await setDoc(doc(db, 'leaderboard', user.uid), {
        uid: user.uid,
        displayName,
        avatarId,
        level: userProfile?.level || 5,
        xp: userProfile?.xp || 1420,
        streakDays: userProfile?.streakDays || 12,
        isPremium: true
      }, { merge: true });

      audioEngine.playSfx('sfx_badge_unlocked.wav');
      setSuccessMsg('Profile updated successfully! 🦊✨');
      setTimeout(() => {
        setSuccessMsg('');
        onClose();
      }, 1200);
    } catch (err) {
      console.error(err);
      alert('Failed to update profile. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="glass-panel w-full max-w-xl p-6 relative border-amber-500/50 max-h-[90vh] overflow-y-auto">
        
        {/* Close Button */}
        <button 
          onClick={onClose}
          className="absolute top-4 right-4 p-2 rounded-xl bg-amber-900/40 text-amber-200 hover:bg-amber-800/60 transition-colors"
        >
          <X className="w-5 h-5" />
        </button>

        {/* Header */}
        <div className="flex items-center gap-3 mb-6 border-b border-amber-900/40 pb-4">
          <div className="p-3 rounded-2xl bg-amber-500/20 border border-amber-500/40">
            <User className="w-6 h-6 text-amber-400" />
          </div>
          <div>
            <h2 className="text-xl font-extrabold text-amber-400">Customize Profile & Focus Identity</h2>
            <p className="text-xs text-amber-200/70">Tailor your mascot avatar, display bio, and app blocklist</p>
          </div>
        </div>

        {successMsg && (
          <div className="mb-4 p-3 rounded-xl bg-emerald-950/80 border border-emerald-500/60 text-xs text-emerald-200 font-extrabold text-center">
            {successMsg}
          </div>
        )}

        <div className="space-y-5">
          
          {/* Avatar Selector */}
          <div>
            <label className="block text-xs font-bold text-amber-300 uppercase tracking-wider mb-2">
              Select Finn Mascot Avatar
            </label>
            <div className="grid grid-cols-4 sm:grid-cols-7 gap-2">
              {AVATAR_OPTIONS.map((av) => {
                const selected = avatarId === av.id;
                return (
                  <button
                    key={av.id}
                    onClick={() => { audioEngine.playSfx('sfx_coin_claim.wav'); setAvatarId(av.id); }}
                    className={`p-2 rounded-2xl border flex flex-col items-center gap-1 transition-all ${
                      selected 
                        ? 'bg-amber-500/30 border-amber-400 ring-2 ring-amber-400/50 scale-105' 
                        : 'bg-amber-950/30 border-amber-900/50 hover:border-amber-700/60'
                    }`}
                    title={av.name}
                  >
                    <img src={av.img} alt={av.name} className="w-10 h-10 object-contain rounded-lg" />
                  </button>
                );
              })}
            </div>
          </div>

          {/* Display Name */}
          <div>
            <label className="block text-xs font-bold text-amber-300 uppercase tracking-wider mb-1">
              Display Name
            </label>
            <input
              type="text"
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              placeholder="e.g. Alex Mercer"
              className="input-base"
            />
          </div>

          {/* Bio */}
          <div>
            <label className="block text-xs font-bold text-amber-300 uppercase tracking-wider mb-1">
              Personal Focus Bio
            </label>
            <input
              type="text"
              value={bio}
              onChange={(e) => setBio(e.target.value)}
              placeholder="e.g. 100 Days Streak Goal • Software Engineer"
              className="input-base"
            />
          </div>

          {/* Apps to Block Selector */}
          <div>
            <label className="block text-xs font-bold text-amber-300 uppercase tracking-wider mb-2">
              Target Apps to Lock During Focus
            </label>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
              {AVAILABLE_APPS.map((app) => {
                const isBlocked = targetApps.includes(app.name);
                return (
                  <button
                    key={app.name}
                    type="button"
                    onClick={() => toggleApp(app.name)}
                    className={`p-2.5 rounded-xl border font-bold text-xs flex items-center justify-between transition-all ${
                      isBlocked
                        ? 'bg-orange-950/70 border-orange-500 text-orange-200'
                        : 'bg-amber-950/20 border-amber-900/40 text-amber-200/60 hover:text-amber-200'
                    }`}
                  >
                    <div className="flex items-center gap-2">
                      <img src={app.icon} alt={app.name} className="w-4 h-4 object-contain" />
                      <span>{app.name}</span>
                    </div>
                    {isBlocked && <Check className="w-3.5 h-3.5 text-orange-400" />}
                  </button>
                );
              })}
            </div>
          </div>

          {/* Daily Goal Selector */}
          <div>
            <label className="block text-xs font-bold text-amber-300 uppercase tracking-wider mb-2">
              Daily Target Focus Goal
            </label>
            <div className="grid grid-cols-4 gap-2">
              {[30, 60, 120, 180].map((mins) => (
                <button
                  key={mins}
                  type="button"
                  onClick={() => { audioEngine.playSfx('sfx_coin_claim.wav'); setDailyGoalMinutes(mins); }}
                  className={`py-2 rounded-xl border font-extrabold text-xs transition-all ${
                    dailyGoalMinutes === mins
                      ? 'bg-amber-500 text-slate-950 border-amber-400 shadow-md'
                      : 'bg-amber-950/30 border-amber-900/40 text-amber-200/70 hover:text-amber-200'
                  }`}
                >
                  {mins >= 60 ? `${mins / 60}h Goal` : `${mins}m Goal`}
                </button>
              ))}
            </div>
          </div>

        </div>

        {/* Buttons */}
        <div className="mt-6 pt-4 border-t border-amber-900/40 flex items-center justify-between gap-3">
          {user && (
            <button
              onClick={() => { signOut(auth); onClose(); }}
              className="py-2.5 px-4 rounded-xl bg-red-950/60 hover:bg-red-900/80 border border-red-800/40 text-xs font-bold text-red-200 flex items-center gap-2 transition-all"
            >
              <LogOut className="w-4 h-4" /> Sign Out
            </button>
          )}

          <div className="flex items-center gap-2 ml-auto">
            <button onClick={onClose} className="btn-secondary text-xs py-2.5">
              Cancel
            </button>
            <button onClick={handleSave} disabled={saving} className="btn-primary text-xs py-2.5">
              <Save className="w-4 h-4" /> {saving ? 'Saving...' : 'Save Profile Changes'}
            </button>
          </div>
        </div>

      </div>
    </div>
  );
}
