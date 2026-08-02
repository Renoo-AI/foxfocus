import React, { useState } from 'react';
import { 
  auth, 
  googleProvider, 
  signInWithPopup, 
  signInWithEmailAndPassword, 
  createUserWithEmailAndPassword, 
  signInAnonymously,
  syncUserProfile 
} from '../firebase';
import { X, Sparkles, Mail, Lock, UserCheck, ShieldCheck } from 'lucide-react';
import { audioEngine } from '../utils/AudioEngine';

export default function AuthModal({ isOpen, onClose, promptMessage }) {
  const [isRegister, setIsRegister] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (!isOpen) return null;

  const handleGoogleSignIn = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await signInWithPopup(auth, googleProvider);
      await syncUserProfile(res.user);
      audioEngine.playSfx('sfx_game_win.wav');
      onClose();
    } catch (err) {
      console.error(err);
      setError(err.message || 'Google Sign-In failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleEmailAuth = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      let userCredential;
      if (isRegister) {
        userCredential = await createUserWithEmailAndPassword(auth, email, password);
        await syncUserProfile(userCredential.user, { displayName });
      } else {
        userCredential = await signInWithEmailAndPassword(auth, email, password);
        await syncUserProfile(userCredential.user);
      }
      audioEngine.playSfx('sfx_game_win.wav');
      onClose();
    } catch (err) {
      console.error(err);
      setError(err.message.replace('Firebase: ', ''));
    } finally {
      setLoading(false);
    }
  };

  const handleGuestSignIn = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await signInAnonymously(auth);
      await syncUserProfile(res.user, { displayName: 'Guest Fox' });
      audioEngine.playSfx('sfx_coin_claim.wav');
      onClose();
    } catch (err) {
      console.error(err);
      setError('Guest mode login failed.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="glass-panel w-full max-w-md p-6 relative border-amber-500/40 animate-flame">
        
        {/* Close Button */}
        <button 
          onClick={onClose}
          className="absolute top-4 right-4 p-2 rounded-xl bg-amber-900/40 text-amber-200 hover:bg-amber-800/60 transition-colors"
        >
          <X className="w-5 h-5" />
        </button>

        {/* Header */}
        <div className="text-center mb-6">
          <img src="/medias/app-logo.webp" alt="FoxFocus" className="w-14 h-14 mx-auto mb-2 rounded-2xl shadow-lg" />
          <h2 className="text-2xl font-extrabold text-amber-400">Welcome to FoxFocus</h2>
          <p className="text-xs text-amber-200/70 font-semibold mt-1">
            {isRegister ? 'Create your FoxFocus account' : 'Sign in to sync streaks & profile'}
          </p>

          {/* Free Premium Banner */}
          <div className="mt-3 p-2.5 rounded-xl bg-amber-950/60 border border-amber-500/40 text-xs font-bold text-amber-300 flex items-center justify-center gap-2">
            <Sparkles className="w-4 h-4 text-amber-400" />
            <span>🎁 PREMIUM PLAN IS FREE FOR ALL USERS!</span>
          </div>

          {promptMessage && (
            <div className="mt-2 p-2.5 rounded-xl bg-orange-950/80 border border-orange-500/60 text-xs text-orange-200 text-left">
              🔒 {promptMessage}
            </div>
          )}
        </div>

        {error && (
          <div className="mb-4 p-3 rounded-xl bg-red-950/80 border border-red-500/60 text-xs text-red-200 text-center font-bold">
            ⚠️ {error}
          </div>
        )}

        {/* Google Sign-In Button */}
        <button
          onClick={handleGoogleSignIn}
          disabled={loading}
          className="w-full py-3 px-4 rounded-xl bg-white hover:bg-amber-50 text-slate-900 font-extrabold text-sm shadow-md flex items-center justify-center gap-3 transition-all mb-4"
        >
          <img src="/medias/google.png" alt="Google" className="w-5 h-5" />
          <span>Continue with Google</span>
        </button>

        <div className="flex items-center my-4">
          <div className="flex-1 border-t border-amber-900/60"></div>
          <span className="px-3 text-xs font-bold text-amber-400/60 uppercase">OR</span>
          <div className="flex-1 border-t border-amber-900/60"></div>
        </div>

        {/* Email & Password Form */}
        <form onSubmit={handleEmailAuth} className="space-y-3">
          {isRegister && (
            <div>
              <label className="block text-xs font-bold text-amber-200/80 mb-1">Display Name</label>
              <input
                type="text"
                required
                placeholder="e.g. Alex Mercer"
                value={displayName}
                onChange={(e) => setDisplayName(e.target.value)}
                className="input-base"
              />
            </div>
          )}

          <div>
            <label className="block text-xs font-bold text-amber-200/80 mb-1">Email Address</label>
            <input
              type="email"
              required
              placeholder="you@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="input-base"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-amber-200/80 mb-1">Password</label>
            <input
              type="password"
              required
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="input-base"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="btn-primary w-full py-3 mt-2"
          >
            {loading ? 'Processing...' : (isRegister ? '🚀 Create Free Account' : '🔑 Sign In')}
          </button>
        </form>

        {/* Guest Sign-In Option */}
        <div className="mt-4 pt-4 border-t border-amber-900/40 text-center">
          <button
            onClick={handleGuestSignIn}
            disabled={loading}
            className="w-full py-2.5 rounded-xl bg-amber-900/30 hover:bg-amber-900/60 border border-amber-800/40 text-xs font-bold text-amber-200 flex items-center justify-center gap-2 transition-all"
          >
            <UserCheck className="w-4 h-4 text-amber-400" />
            <span>Continue as Guest Mode (Limited Backup)</span>
          </button>
        </div>

        {/* Toggle Register / Sign In */}
        <p className="mt-4 text-center text-xs font-medium text-amber-200/60">
          {isRegister ? 'Already have an account?' : "Don't have an account yet?"}{' '}
          <button
            onClick={() => setIsRegister(!isRegister)}
            className="text-amber-400 font-extrabold underline hover:text-amber-300 ml-1"
          >
            {isRegister ? 'Sign In' : 'Sign Up Free'}
          </button>
        </p>

      </div>
    </div>
  );
}
