import React, { useState, useEffect } from 'react';
import { auth, db, onAuthStateChanged, doc, onSnapshot, syncUserProfile } from './firebase';

import Navbar from './components/Navbar';
import AuthModal from './components/AuthModal';
import ProfileModal from './components/ProfileModal';
import QrPairingModal from './components/QrPairingModal';
import ApkDownloadModal from './components/ApkDownloadModal';

import LandingPage from './pages/LandingPage';
import AppPage from './pages/AppPage';
import LeaderboardPage from './pages/LeaderboardPage';
import DesignSystemPage from './pages/DesignSystemPage';

export default function App() {
  const [currentTab, setCurrentTab] = useState('web');
  const [user, setUser] = useState(null);
  const [userProfile, setUserProfile] = useState(null);

  // Modals state
  const [isAuthOpen, setIsAuthOpen] = useState(false);
  const [authPromptMessage, setAuthPromptMessage] = useState('');
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [isPairingOpen, setIsPairingOpen] = useState(false);
  const [isApkOpen, setIsApkOpen] = useState(false);

  // Handle URL hash or path routing
  useEffect(() => {
    const handleLocation = () => {
      const path = window.location.pathname;
      const hash = window.location.hash;
      if (path === '/app' || hash === '#/app') {
        setCurrentTab('app');
      } else if (path === '/leaderboard' || hash === '#/leaderboard') {
        setCurrentTab('leaderboard');
      } else if (path === '/design' || hash === '#/design' || hash === '#/components') {
        setCurrentTab('design');
      } else if (path === '/web' || hash === '#/web') {
        setCurrentTab('web');
      }
    };
    handleLocation();
    window.addEventListener('popstate', handleLocation);
    return () => window.removeEventListener('popstate', handleLocation);
  }, []);

  // Listen to Auth State
  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, async (currentUser) => {
      setUser(currentUser);
      if (currentUser) {
        const prof = await syncUserProfile(currentUser);
        setUserProfile(prof);

        // Listen to Firestore profile changes
        const profUnsub = onSnapshot(doc(db, 'users', currentUser.uid), (docSnap) => {
          if (docSnap.exists()) {
            setUserProfile({ ...docSnap.data(), isPremium: true });
          }
        });
        return () => profUnsub();
      } else {
        setUserProfile(null);
      }
    });
    return () => unsubscribe();
  }, []);

  const openAuthWithPrompt = (msg = '') => {
    setAuthPromptMessage(msg);
    setIsAuthOpen(true);
  };

  return (
    <div className="min-h-screen flex flex-col bg-background text-primary">
      
      {/* Navigation Bar */}
      <Navbar
        currentTab={currentTab}
        setCurrentTab={(tab) => {
          setCurrentTab(tab);
          window.location.hash = `#/${tab}`;
        }}
        user={user}
        userProfile={userProfile}
        onOpenAuth={() => openAuthWithPrompt('')}
        onOpenProfile={() => setIsProfileOpen(true)}
        onOpenPairing={() => setIsPairingOpen(true)}
        onOpenApkModal={() => setIsApkOpen(true)}
      />

      {/* Main Page View Container */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 pt-2">
        {currentTab === 'web' && (
          <LandingPage
            onLaunchApp={() => { setCurrentTab('app'); window.location.hash = '#/app'; }}
            onOpenApk={() => setIsApkOpen(true)}
            onOpenPairing={() => setIsPairingOpen(true)}
            onOpenAuth={() => openAuthWithPrompt('')}
          />
        )}

        {currentTab === 'app' && (
          <AppPage
            user={user}
            userProfile={userProfile}
            onOpenProfile={() => setIsProfileOpen(true)}
            onOpenAuth={() => openAuthWithPrompt('')}
          />
        )}

        {currentTab === 'leaderboard' && (
          <LeaderboardPage
            user={user}
            userProfile={userProfile}
          />
        )}

        {currentTab === 'design' && (
          <DesignSystemPage />
        )}
      </main>

      {/* Global Modals */}
      <AuthModal
        isOpen={isAuthOpen}
        onClose={() => setIsAuthOpen(false)}
        promptMessage={authPromptMessage}
      />

      <ProfileModal
        isOpen={isProfileOpen}
        onClose={() => setIsProfileOpen(false)}
        user={user}
        userProfile={userProfile}
        onRequireAuth={(msg) => {
          setIsProfileOpen(false);
          openAuthWithPrompt(msg);
        }}
      />

      <QrPairingModal
        isOpen={isPairingOpen}
        onClose={() => setIsPairingOpen(false)}
        user={user}
      />

      <ApkDownloadModal
        isOpen={isApkOpen}
        onClose={() => setIsApkOpen(false)}
      />

      {/* Global Footer */}
      <footer className="glass-panel rounded-none border-x-0 border-b-0 py-6 px-4 text-center border-amber-900/40 text-xs text-amber-200/60 mt-12">
        <div className="max-w-7xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-2">
            <img src="/medias/app-logo.webp" alt="FoxFocus" className="w-6 h-6 rounded-md" />
            <span className="font-extrabold text-amber-400">FoxFocus Warm Framework v1.0.0</span>
          </div>
          <p>© 2026 FoxFocus Inc. All rights reserved. 300+ Warm UI Components Integrated.</p>
          <div className="flex items-center gap-4 font-bold text-amber-300">
            <button onClick={() => setCurrentTab('web')} className="hover:underline">/web</button>
            <button onClick={() => setCurrentTab('app')} className="hover:underline">/app</button>
            <button onClick={() => setCurrentTab('design')} className="hover:underline">Warm UI Kit (300+)</button>
            <button onClick={() => setIsApkOpen(true)} className="hover:underline">Direct APK Download</button>
          </div>
        </div>
      </footer>

    </div>
  );
}
