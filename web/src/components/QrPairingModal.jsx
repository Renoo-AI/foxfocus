import React, { useState, useEffect } from 'react';
import { QRCodeSVG } from 'qrcode.react';
import { db, doc, setDoc, onSnapshot, serverTimestamp } from '../firebase';
import { X, QrCode, Smartphone, CheckCircle, RefreshCw, ShieldCheck } from 'lucide-react';
import { audioEngine } from '../utils/AudioEngine';

export default function QrPairingModal({ isOpen, onClose, user }) {
  const [pairingCode, setPairingCode] = useState('');
  const [status, setStatus] = useState('pending'); // pending, approved, revoked
  const [ownerInfo, setOwnerInfo] = useState(null);

  const generateNewPairing = async () => {
    const code = Math.floor(100000 + Math.random() * 900000).toString();
    setPairingCode(code);
    setStatus('pending');
    setOwnerInfo(null);

    if (user?.uid) {
      try {
        await setDoc(doc(db, 'pairings', code), {
          viewerUid: user.uid,
          ownerUid: null,
          status: 'pending',
          createdAt: serverTimestamp()
        });
      } catch (e) {
        console.warn('Pairing doc init error:', e);
      }
    }
  };

  useEffect(() => {
    if (isOpen) {
      generateNewPairing();
    }
  }, [isOpen]);

  // Listen for pairing document update
  useEffect(() => {
    if (!pairingCode) return;
    const unsub = onSnapshot(doc(db, 'pairings', pairingCode), (docSnap) => {
      if (docSnap.exists()) {
        const data = docSnap.data();
        if (data.status === 'approved') {
          setStatus('approved');
          setOwnerInfo(data);
          audioEngine.playSfx('sfx_badge_unlocked.wav');
        }
      }
    });
    return () => unsub();
  }, [pairingCode]);

  if (!isOpen) return null;

  const qrPayload = JSON.stringify({
    action: 'foxfocus_pair',
    code: pairingCode,
    timestamp: Date.now()
  });

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
          <div className="w-12 h-12 rounded-2xl bg-amber-500/20 border border-amber-500/40 flex items-center justify-center mx-auto mb-2">
            <QrCode className="w-6 h-6 text-amber-400" />
          </div>
          <h2 className="text-xl font-extrabold text-amber-400">FoxFocus PC Web Connect</h2>
          <p className="text-xs text-amber-200/70 font-semibold mt-0.5">
            WhatsApp & Telegram Style QR Code Desktop Linkage
          </p>
        </div>

        {status === 'approved' ? (
          <div className="py-8 space-y-4 animate-flame">
            <div className="w-16 h-16 rounded-full bg-emerald-500/20 border border-emerald-500 flex items-center justify-center mx-auto text-emerald-400">
              <CheckCircle className="w-10 h-10" />
            </div>
            <div>
              <h3 className="text-lg font-extrabold text-emerald-400">Device Successfully Synced!</h3>
              <p className="text-xs text-amber-200/80 mt-1">
                Your PC session is now securely connected to your Mobile FoxFocus account.
              </p>
            </div>
            <div className="p-3 rounded-xl bg-amber-950/60 border border-amber-900/40 text-xs text-amber-300 font-bold">
              🔒 End-to-End Session Mirror Active
            </div>
            <button onClick={onClose} className="btn-primary w-full py-2.5 text-xs">
              Done & Launch PC Dashboard
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            
            {/* QR Display Frame */}
            <div className="p-4 rounded-2xl bg-white border-2 border-amber-400/80 inline-block shadow-xl my-2">
              <QRCodeSVG 
                value={qrPayload} 
                size={180} 
                bgColor="#FFFFFF"
                fgColor="#241D18"
                level="H"
              />
            </div>

            {/* PIN Fallback */}
            <div className="p-3 rounded-xl bg-amber-950/60 border border-amber-900/40">
              <span className="block text-[10px] font-bold text-amber-400/80 uppercase">Or enter pairing PIN code on phone</span>
              <span className="text-2xl font-black tracking-widest text-amber-400 font-mono">{pairingCode}</span>
            </div>

            {/* Instructions */}
            <div className="text-left space-y-2 bg-amber-950/30 p-3 rounded-xl border border-amber-900/30 text-xs text-amber-200/80">
              <div className="flex items-center gap-2 font-bold text-amber-300">
                <Smartphone className="w-4 h-4 text-amber-400" />
                <span>How to Link:</span>
              </div>
              <ol className="list-decimal list-inside space-y-1 pl-1 text-[11px]">
                <li>Open <strong>FoxFocus App</strong> on your phone</li>
                <li>Go to <strong>Profile / Settings</strong> & tap <strong>PC Link (QR)</strong></li>
                <li>Scan this QR code or type the 6-digit PIN code</li>
              </ol>
            </div>

            {/* Refresh Button */}
            <button
              onClick={generateNewPairing}
              className="text-xs font-bold text-amber-400 hover:text-amber-300 flex items-center justify-center gap-1.5 mx-auto py-1"
            >
              <RefreshCw className="w-3.5 h-3.5" /> Regenerate QR Code
            </button>

          </div>
        )}

      </div>
    </div>
  );
}
