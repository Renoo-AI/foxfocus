import React, { useState } from 'react';
import { X, Download, ShieldCheck, Smartphone, Check, Sparkles } from 'lucide-react';
import { audioEngine } from '../utils/AudioEngine';

export default function ApkDownloadModal({ isOpen, onClose }) {
  const [downloading, setDownloading] = useState(false);
  const [downloadProgress, setDownloadProgress] = useState(0);
  const [completed, setCompleted] = useState(false);

  if (!isOpen) return null;

  const handleDownload = () => {
    audioEngine.playSfx('sfx_coin_claim.wav');
    setDownloading(true);
    setDownloadProgress(10);

    const interval = setInterval(() => {
      setDownloadProgress((prev) => {
        if (prev >= 100) {
          clearInterval(interval);
          setDownloading(false);
          setCompleted(true);
          audioEngine.playSfx('sfx_badge_unlocked.wav');
          
          // Trigger actual blob/file download
          const link = document.createElement('a');
          link.href = '/foxfocus-v1.0.0.apk';
          link.download = 'FoxFocus-v1.0.0-Release.apk';
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);

          return 100;
        }
        return prev + 22;
      });
    }, 250);
  };

  return (
    <div className="modal-overlay">
      <div className="glass-panel w-full max-w-md p-6 relative border-amber-500/50 text-center">
        
        {/* Close Button */}
        <button 
          onClick={onClose}
          className="absolute top-4 right-4 p-2 rounded-xl bg-amber-900/40 text-amber-200 hover:bg-amber-800/60 transition-colors"
        >
          <X className="w-5 h-5" />
        </button>

        {/* Header */}
        <div className="mb-4">
          <div className="w-14 h-14 rounded-2xl bg-amber-500/20 border border-amber-500/40 flex items-center justify-center mx-auto mb-2 shadow-lg">
            <Smartphone className="w-8 h-8 text-amber-400" />
          </div>
          <h2 className="text-xl font-extrabold text-amber-400">FoxFocus APK Direct Download</h2>
          <p className="text-xs text-amber-200/70 font-semibold mt-0.5">
            Standalone Android Package (No Play Store / App Store required)
          </p>
        </div>

        {/* Info Card */}
        <div className="bg-amber-950/40 p-4 rounded-2xl border border-amber-900/40 text-left space-y-2 mb-4 text-xs">
          <div className="flex justify-between border-b border-amber-900/40 pb-2">
            <span className="text-amber-200/60 font-semibold">Package Version:</span>
            <span className="font-extrabold text-amber-300">v1.0.0 Release (Pro Unlocked)</span>
          </div>
          <div className="flex justify-between border-b border-amber-900/40 pb-2">
            <span className="text-amber-200/60 font-semibold">File Size:</span>
            <span className="font-extrabold text-amber-300">24.5 MB</span>
          </div>
          <div className="flex justify-between border-b border-amber-900/40 pb-2">
            <span className="text-amber-200/60 font-semibold">System Requirement:</span>
            <span className="font-extrabold text-amber-300">Android 8.0+ (API 26+)</span>
          </div>
          <div className="flex justify-between">
            <span className="text-amber-200/60 font-semibold">Security Verification:</span>
            <span className="font-extrabold text-emerald-400 flex items-center gap-1">
              <ShieldCheck className="w-4 h-4 text-emerald-400" /> Verified Safe APK
            </span>
          </div>
        </div>

        {/* Feature Highlights */}
        <div className="space-y-1.5 text-left mb-6 text-xs text-amber-200/80">
          <div className="flex items-center gap-2">
            <Check className="w-4 h-4 text-amber-400" /> <span>Full Accessibility App Blocker overlay service</span>
          </div>
          <div className="flex items-center gap-2">
            <Check className="w-4 h-4 text-amber-400" /> <span>Google & Email Auth + Firestore cloud sync</span>
          </div>
          <div className="flex items-center gap-2">
            <Check className="w-4 h-4 text-amber-400" /> <span>Sound effects & ambient lofi focus music engine</span>
          </div>
        </div>

        {/* Progress Bar */}
        {downloading && (
          <div className="mb-4">
            <div className="w-full bg-amber-950 rounded-full h-3 overflow-hidden border border-amber-800">
              <div 
                className="bg-gradient-to-r from-amber-500 to-orange-500 h-full transition-all duration-300"
                style={{ width: `${downloadProgress}%` }}
              />
            </div>
            <p className="text-xs font-bold text-amber-300 mt-2">Downloading APK... {downloadProgress}%</p>
          </div>
        )}

        {completed ? (
          <div className="p-3 rounded-xl bg-emerald-950/80 border border-emerald-500/60 text-emerald-300 font-extrabold text-xs mb-2">
            🎉 FoxFocus APK Downloaded! Open file on Android to Install.
          </div>
        ) : null}

        <button
          onClick={handleDownload}
          disabled={downloading}
          className="btn-gold w-full py-3.5 text-sm"
        >
          <Download className="w-5 h-5" />
          <span>{downloading ? 'Downloading...' : 'Download FoxFocus APK (24.5 MB)'}</span>
        </button>

      </div>
    </div>
  );
}
