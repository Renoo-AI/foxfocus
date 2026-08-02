import React, { useState } from 'react';
import { 
  Search, 
  Sparkles, 
  Smartphone, 
  Layers, 
  Flame, 
  Coins, 
  ShieldCheck, 
  CheckCircle2, 
  AlertCircle, 
  Bell, 
  User, 
  Settings, 
  Lock, 
  QrCode, 
  Plus, 
  Minus, 
  Check, 
  X, 
  Download,
  RotateCcw,
  Trophy
} from 'lucide-react';
import { audioEngine } from '../utils/AudioEngine';

export default function DesignSystemPage() {
  const [activeMainView, setActiveMainView] = useState('pages'); // 'pages' or 'components'
  const [searchQuery, setSearchQuery] = useState('');
  
  // Interactive Component State Demos
  const [otpVal, setOtpVal] = useState(['4', '8', '2', '']);
  const [stepperVal, setStepperVal] = useState(3);
  const [toggle1, setToggle1] = useState(true);
  const [toggle2, setToggle2] = useState(false);
  const [rangeVal, setRangeVal] = useState(65);

  const filteredPages = [
    { id: 'page-login', num: '01', title: 'Login & Auth', tag: 'Auth' },
    { id: 'page-onboarding', num: '02', title: 'Onboarding & Goals', tag: 'Flow' },
    { id: 'page-otp', num: '03', title: 'OTP Verification', tag: 'Auth' },
    { id: 'page-dashboard', num: '04', title: 'Home Dashboard', tag: 'Core' },
    { id: 'page-quest', num: '05', title: 'Focus Quest & Blocker', tag: 'Core' },
    { id: 'page-analytics', num: '06', title: 'Analytics & Streaks', tag: 'Stats' },
    { id: 'page-wallet', num: '07', title: 'Wallet & Marketplace', tag: 'Store' },
    { id: 'page-profile', num: '08', title: 'User Profile & Mascot', tag: 'User' },
    { id: 'page-settings', num: '09', title: 'App Settings & Overlays', tag: 'System' },
    { id: 'page-notifications', num: '10', title: 'Smart Nudges', tag: 'Alert' },
    { id: 'page-checkout', num: '11', title: 'Free Premium Pass', tag: 'Store' },
    { id: 'page-empty', num: '12', title: 'Empty & PC Link', tag: 'System' }
  ].filter(p => p.title.toLowerCase().includes(searchQuery.toLowerCase()) || p.tag.toLowerCase().includes(searchQuery.toLowerCase()));

  return (
    <div className="flex flex-col lg:flex-row gap-8 pb-16">
      
      {/* 📍 Left Sidebar Catalog Navigation */}
      <aside className="w-full lg:w-72 flex-shrink-0 space-y-5">
        <div className="glass-panel p-5 space-y-4 border-amber-500/40">
          
          <div className="flex items-center justify-between">
            <span className="font-extrabold text-base text-amber-400">Warm UI System</span>
            <span className="badge-premium text-[10px]">300+ Kits</span>
          </div>

          {/* View Switcher Tabs */}
          <div className="flex bg-amber-950/60 p-1 rounded-xl border border-amber-900/40 gap-1">
            <button
              onClick={() => { audioEngine.playSfx('sfx_coin_claim.wav'); setActiveMainView('pages'); }}
              className={`flex-1 py-1.5 rounded-lg text-xs font-bold transition-all ${
                activeMainView === 'pages' 
                  ? 'bg-amber-500 text-slate-950 shadow-md' 
                  : 'text-amber-200/70 hover:text-amber-100'
              }`}
            >
              📱 12 Pages
            </button>
            <button
              onClick={() => { audioEngine.playSfx('sfx_coin_claim.wav'); setActiveMainView('components'); }}
              className={`flex-1 py-1.5 rounded-lg text-xs font-bold transition-all ${
                activeMainView === 'components' 
                  ? 'bg-amber-500 text-slate-950 shadow-md' 
                  : 'text-amber-200/70 hover:text-amber-100'
              }`}
            >
              🧩 Atomic UI
            </button>
          </div>

          {/* Search Box */}
          <div className="relative">
            <Search className="w-4 h-4 text-amber-400 absolute left-3 top-3" />
            <input
              type="text"
              placeholder="Search components..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="input-base text-xs pl-9 py-2"
            />
          </div>

          {/* Page Links List */}
          <div className="space-y-1 pt-2 border-t border-amber-900/40">
            <span className="block text-[10px] font-extrabold text-amber-400/70 uppercase tracking-wider mb-2">
              Default App Screens
            </span>
            {filteredPages.map((page) => (
              <a
                key={page.id}
                href={`#${page.id}`}
                className="p-2 rounded-xl text-xs font-bold text-amber-200/80 hover:text-amber-300 hover:bg-amber-900/40 flex items-center justify-between transition-all"
              >
                <span>{page.num}. {page.title}</span>
                <span className="text-[10px] bg-amber-950 px-2 py-0.5 rounded-md text-amber-400">{page.tag}</span>
              </a>
            ))}
          </div>

        </div>
      </aside>

      {/* 🚀 Main Content View Area */}
      <main className="flex-1 space-y-8">
        
        {/* Header Section */}
        <div className="glass-panel p-6 border-amber-500/40 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div>
            <h1 className="text-2xl font-black text-amber-300">
              {activeMainView === 'pages' ? '📱 12 Default Mobile Application Screens' : '🧩 Atomic UI Component Library'}
            </h1>
            <p className="text-xs text-amber-200/70 mt-1">
              Built strictly with exact XML color tokens (`primary: #F0813F`, `surface: #FFFFFF`, `coin-gold: #F5A623`).
            </p>
          </div>
          <div className="flex items-center gap-2">
            <span className="badge-premium">
              <Sparkles className="w-3.5 h-3.5 text-amber-300" /> Free Premium Framework
            </span>
          </div>
        </div>

        {/* VIEW 1: 12 DEFAULT MOBILE APP SCREENS */}
        {activeMainView === 'pages' && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            
            {/* 1. Login Screen */}
            <div className="phone-frame" id="page-login">
              <div className="notch-bar"><div class="notch"></div></div>
              <div className="page-body">
                <div className="page-title-badge">01. Login Screen</div>
                <div className="text-center my-4">
                  <div className="w-14 h-14 bg-orange-500/20 text-orange-400 rounded-2xl inline-flex items-center justify-center text-2xl font-black mb-2 border border-orange-500/40">🔥</div>
                  <h2 className="text-lg font-black text-amber-300">Welcome Back</h2>
                  <p className="text-xs text-amber-200/60 mt-1">Sign in to continue your focus quest</p>
                </div>
                <div className="space-y-3 mt-4">
                  <div className="input-group">
                    <label className="input-label">Email Address</label>
                    <input type="email" className="input-base" defaultValue="alex.dev@warm.app" />
                  </div>
                  <div className="input-group">
                    <label className="input-label">Password</label>
                    <input type="password" className="input-base" defaultValue="••••••••••••" />
                  </div>
                  <button className="btn-primary w-full mt-2 text-xs">Sign In to Dashboard</button>
                  <button className="btn-secondary w-full text-xs">Sign In with Google</button>
                </div>
              </div>
            </div>

            {/* 2. Onboarding Screen */}
            <div className="phone-frame" id="page-onboarding">
              <div className="notch-bar"><div class="notch"></div></div>
              <div className="page-body">
                <div className="page-title-badge">02. Onboarding Screen</div>
                <div className="text-center my-2">
                  <img src="/medias/fox-human-walking.webp" alt="Finn Onboarding" className="w-28 h-28 object-contain mx-auto" />
                  <h2 className="text-lg font-black text-amber-300">Select Focus Goals</h2>
                  <p className="text-xs text-amber-200/60">What do you want to accomplish?</p>
                </div>
                <div className="space-y-2 mt-2">
                  {['Stop Doomscrolling', 'Study & Work Focus', 'Better Sleep', 'Family Time'].map((goal, idx) => (
                    <button key={goal} className={`btn-secondary w-full text-xs justify-between ${idx === 0 ? 'border-orange-500 bg-orange-950/40 text-orange-200' : ''}`}>
                      <span>{goal}</span>
                      {idx === 0 && <Check className="w-4 h-4 text-orange-400" />}
                    </button>
                  ))}
                </div>
                <button className="btn-primary w-full mt-auto text-xs">Continue (Step 3/5)</button>
              </div>
            </div>

            {/* 3. OTP Verification */}
            <div className="phone-frame" id="page-otp">
              <div className="notch-bar"><div class="notch"></div></div>
              <div className="page-body text-center">
                <div className="page-title-badge">03. OTP Verification</div>
                <div className="w-12 h-12 bg-amber-500/20 text-amber-400 rounded-2xl flex items-center justify-center mx-auto my-3 border border-amber-500/40">
                  <Lock className="w-6 h-6" />
                </div>
                <h2 className="text-lg font-black text-amber-300">Enter Security PIN</h2>
                <p className="text-xs text-amber-200/60 mt-1">We sent a 4-digit code to your email</p>
                
                <div className="flex justify-center gap-2 my-6">
                  {['4', '8', '2', '-'].map((digit, i) => (
                    <input key={i} type="text" className="otp-input" value={digit} readOnly />
                  ))}
                </div>
                <button className="btn-primary w-full text-xs">Verify & Link Account</button>
                <p className="text-[11px] text-amber-400 font-bold mt-4">Resend code in 00:45</p>
              </div>
            </div>

            {/* 4. Home Dashboard */}
            <div className="phone-frame" id="page-dashboard">
              <div className="notch-bar"><div class="notch"></div></div>
              <div className="page-body space-y-4">
                <div className="page-title-badge">04. Home Dashboard</div>
                
                <div className="hero-banner">
                  <div>
                    <span className="block text-sm font-black">Day 7 Streak Active</span>
                    <span className="text-[11px] opacity-90">Double coin reward multiplier</span>
                  </div>
                  <span className="text-lg font-black">🪙 1,250</span>
                </div>

                <div className="text-center bg-amber-950/60 p-4 rounded-2xl border border-amber-900/50">
                  <img src="/medias/fox-happy-wear-crown.webp" alt="Finn Mascot" className="w-28 h-28 object-contain mx-auto mb-2" />
                  <span className="block font-black text-sm text-amber-300">Finn Level 14 • Flame Crown</span>
                  <div className="progress-bar mt-2">
                    <div className="progress-fill" style={{ width: '75%' }}></div>
                  </div>
                </div>

                <div className="bottom-nav rounded-2xl">
                  <div className="nav-item active"><Flame className="w-5 h-5 text-orange-500" /><span>Home</span></div>
                  <div className="nav-item"><Trophy className="w-5 h-5" /><span>Ranks</span></div>
                  <div className="nav-item"><User className="w-5 h-5" /><span>Profile</span></div>
                </div>
              </div>
            </div>

            {/* 5. Focus Quest & Blocker */}
            <div className="phone-frame" id="page-quest">
              <div className="notch-bar"><div class="notch"></div></div>
              <div className="page-body text-center space-y-3">
                <div className="page-title-badge">05. Focus Quest & Blocker</div>
                <img src="/medias/meditating-fox-close-eyes.webp" alt="Zen Mascot" className="w-32 h-32 object-contain mx-auto" />
                <span className="badge-premium">Deep Work Timer</span>
                <div className="text-4xl font-black font-mono text-amber-300">24:59</div>
                <div className="p-3 bg-red-950/80 border border-red-500/60 rounded-2xl text-xs text-red-200 text-left">
                  🚫 Instagram & TikTok blocked during focus mode
                </div>
                <button className="btn-gold w-full text-xs">Pause Session</button>
              </div>
            </div>

            {/* 6. Analytics & Streaks */}
            <div className="phone-frame" id="page-analytics">
              <div className="notch-bar"><div class="notch"></div></div>
              <div className="page-body space-y-3">
                <div className="page-title-badge">06. Analytics & Streaks</div>
                <div className="stat-card">
                  <span className="stat-lbl">Hours Saved Today</span>
                  <span className="stat-val">2.4 Hours</span>
                </div>
                <div className="stat-card">
                  <span className="stat-lbl">Current Streak Flame</span>
                  <span className="stat-val text-orange-500">18 Days 🔥</span>
                </div>
                <div className="stat-card">
                  <span className="stat-lbl">Distractions Blocked</span>
                  <span className="stat-val text-emerald-400">42 Apps</span>
                </div>
              </div>
            </div>

            {/* 7. Wallet & Marketplace */}
            <div className="phone-frame" id="page-wallet">
              <div className="notch-bar"><div class="notch"></div></div>
              <div className="page-body space-y-3">
                <div className="page-title-badge">07. Wallet & Marketplace</div>
                <div className="hero-banner">
                  <span>Coins Balance</span>
                  <span className="text-lg font-black">🪙 1,250 FC</span>
                </div>
                <div className="card-box">
                  <span className="font-extrabold text-xs text-amber-300">Streak Freeze Shield 🛡️</span>
                  <p className="text-[11px] text-amber-200/70">Protects your flame streak for 1 day if you miss a session.</p>
                  <button className="btn-gold w-full text-xs">Buy for 200 🪙</button>
                </div>
              </div>
            </div>

            {/* 8. User Profile */}
            <div className="phone-frame" id="page-profile">
              <div className="notch-bar"><div class="notch"></div></div>
              <div className="page-body space-y-3 text-center">
                <div className="page-title-badge">08. User Profile</div>
                <img src="/medias/fox-happy-wear-crown.webp" alt="Avatar" className="w-20 h-20 object-contain mx-auto rounded-full border-2 border-amber-400 bg-amber-950 p-1" />
                <h3 className="font-black text-base text-amber-300">Alex Mercer</h3>
                <span className="badge-premium">👑 Free Premium Active</span>
                <button className="btn-secondary w-full text-xs">Edit Profile Identity</button>
              </div>
            </div>

            {/* 9. App Settings */}
            <div className="phone-frame" id="page-settings">
              <div className="notch-bar"><div class="notch"></div></div>
              <div className="page-body space-y-3">
                <div className="page-title-badge">09. App Settings</div>
                <div className="card-box space-y-2">
                  <div className="flex justify-between items-center text-xs">
                    <span>Draw Over Apps Overlay</span>
                    <span className="text-emerald-400 font-bold">Granted ✓</span>
                  </div>
                  <div className="flex justify-between items-center text-xs">
                    <span>Accessibility Blocker</span>
                    <span className="text-emerald-400 font-bold">Enabled ✓</span>
                  </div>
                </div>
              </div>
            </div>

            {/* 10. Smart Nudges */}
            <div className="phone-frame" id="page-notifications">
              <div className="notch-bar"><div class="notch"></div></div>
              <div className="page-body space-y-3">
                <div className="page-title-badge">10. Smart Nudges</div>
                <div className="alert-box alert-warning">
                  <span>🔔 Time to start your 25m Focus Session!</span>
                </div>
              </div>
            </div>

            {/* 11. Free Premium Pass */}
            <div className="phone-frame" id="page-checkout">
              <div className="notch-bar"><div class="notch"></div></div>
              <div className="page-body text-center space-y-3">
                <div className="page-title-badge">11. Free Premium Pass</div>
                <h2 className="text-lg font-black text-amber-300">Warm Pro Pass Unlocked</h2>
                <div className="p-4 bg-amber-500/20 border border-amber-400 rounded-2xl text-xs text-amber-200 font-bold">
                  🎁 Premium features are 100% free for all accounts!
                </div>
              </div>
            </div>

            {/* 12. Empty / Offline & PC Link */}
            <div className="phone-frame" id="page-empty">
              <div className="notch-bar"><div class="notch"></div></div>
              <div className="page-body text-center space-y-3">
                <div className="page-title-badge">12. PC Web Connect</div>
                <QrCode className="w-12 h-12 text-amber-400 mx-auto" />
                <h3 className="text-base font-black text-amber-300">Scan QR to Link PC</h3>
                <p className="text-xs text-amber-200/70">Connects desktop browser with phone session in 2 seconds.</p>
              </div>
            </div>

          </div>
        )}

        {/* VIEW 2: ATOMIC UI COMPONENTS SHOWCASE (100+ / 300+ COMPONENTS) */}
        {activeMainView === 'components' && (
          <div className="space-y-12">
            
            {/* 1. Buttons & Actions */}
            <section className="space-y-4">
              <h2 className="text-lg font-extrabold text-amber-300 border-b border-amber-900/40 pb-2">
                1. Buttons & Actions (Tactile & Soft Variants)
              </h2>

              <div className="comp-grid">
                
                <div className="comp-card">
                  <div className="comp-title">Primary Tactile Button</div>
                  <div className="comp-preview">
                    <button className="btn-primary text-xs">Continue Mission</button>
                  </div>
                </div>

                <div className="comp-card">
                  <div className="comp-title">Secondary Surface Button</div>
                  <div className="comp-preview">
                    <button className="btn-secondary text-xs">Cancel Step</button>
                  </div>
                </div>

                <div className="comp-card">
                  <div className="comp-title">Soft Pill Button</div>
                  <div className="comp-preview">
                    <button className="btn-pill text-xs">Explore Category</button>
                  </div>
                </div>

                <div className="comp-card">
                  <div className="comp-title">Destructive Soft Button</div>
                  <div className="comp-preview">
                    <button className="btn-danger text-xs">Delete Quest</button>
                  </div>
                </div>

                <div className="comp-card">
                  <div className="comp-title">Success Soft Button</div>
                  <div className="comp-preview">
                    <button className="btn-success text-xs">Claim +50 Coins</button>
                  </div>
                </div>

                <div className="comp-card">
                  <div className="comp-title">Gold Gradient Button</div>
                  <div className="comp-preview">
                    <button className="btn-gold text-xs">Unlock Pro Feature</button>
                  </div>
                </div>

                <div className="comp-card">
                  <div className="comp-title">Square Action Icon</div>
                  <div className="comp-preview">
                    <button className="btn-icon"><Plus className="w-5 h-5" /></button>
                  </div>
                </div>

                <div className="comp-card">
                  <div className="comp-title">Floating Action (FAB)</div>
                  <div className="comp-preview">
                    <button className="btn-fab">+</button>
                  </div>
                </div>

                <div className="comp-card">
                  <div className="comp-title">Loading Spinner State</div>
                  <div className="comp-preview">
                    <button className="btn-loading text-xs">
                      <div className="spinner-sm"></div> Saving...
                    </button>
                  </div>
                </div>

                <div className="comp-card">
                  <div className="comp-title">Ghost Minimal Text</div>
                  <div className="comp-preview">
                    <button className="btn-ghost text-xs">Skip Intro</button>
                  </div>
                </div>

              </div>
            </section>

            {/* 2. Inputs & Form Controls */}
            <section className="space-y-4">
              <h2 className="text-lg font-extrabold text-amber-300 border-b border-amber-900/40 pb-2">
                2. Inputs & Form Controls
              </h2>

              <div className="comp-grid">
                
                <div className="comp-card">
                  <div className="comp-title">Standard Input Field</div>
                  <div className="comp-preview">
                    <input type="text" className="input-base text-xs" placeholder="Enter full name..." />
                  </div>
                </div>

                <div className="comp-card">
                  <div className="comp-title">OTP Pin Code Grid</div>
                  <div className="comp-preview">
                    <div className="otp-group">
                      {otpVal.map((v, i) => (
                        <input key={i} type="text" className="otp-input" value={v} readOnly />
                      ))}
                    </div>
                  </div>
                </div>

                <div className="comp-card">
                  <div className="comp-title">Quantity Stepper Counter</div>
                  <div className="comp-preview">
                    <div className="stepper">
                      <button onClick={() => setStepperVal(s => Math.max(1, s - 1))} className="stepper-btn">-</button>
                      <span className="stepper-val">{stepperVal}</span>
                      <button onClick={() => setStepperVal(s => s + 1)} className="stepper-btn">+</button>
                    </div>
                  </div>
                </div>

                <div className="comp-card">
                  <div className="comp-title">Tactile Toggle Switches</div>
                  <div className="comp-preview flex gap-4">
                    <label className="toggle-switch">
                      <input type="checkbox" checked={toggle1} onChange={() => setToggle1(!toggle1)} />
                      <span className="slider"></span>
                    </label>
                    <label className="toggle-switch">
                      <input type="checkbox" checked={toggle2} onChange={() => setToggle2(!toggle2)} />
                      <span className="slider"></span>
                    </label>
                  </div>
                </div>

                <div className="comp-card">
                  <div className="comp-title">Custom Range Slider ({rangeVal}%)</div>
                  <div className="comp-preview">
                    <input 
                      type="range" 
                      className="range-slider" 
                      min="0" 
                      max="100" 
                      value={rangeVal}
                      onChange={(e) => setRangeVal(e.target.value)} 
                    />
                  </div>
                </div>

              </div>
            </section>

            {/* 3. Alerts & Toasts */}
            <section className="space-y-4">
              <h2 className="text-lg font-extrabold text-amber-300 border-b border-amber-900/40 pb-2">
                3. Alerts, Toasts & Banners
              </h2>

              <div className="comp-grid">
                
                <div className="comp-card">
                  <div className="comp-title">Success Alert Banner</div>
                  <div className="comp-preview">
                    <div className="alert-box alert-success text-xs">
                      <span>✓ Goal complete (+100 XP)</span>
                    </div>
                  </div>
                </div>

                <div className="comp-card">
                  <div className="comp-title">Danger Alert Error Box</div>
                  <div className="comp-preview">
                    <div className="alert-box alert-danger text-xs">
                      <span>✕ App launch blocked by guard</span>
                    </div>
                  </div>
                </div>

                <div className="comp-card">
                  <div className="comp-title">Warning Banner</div>
                  <div className="comp-preview">
                    <div className="alert-box alert-warning text-xs">
                      <span>⚠️ 30 Minutes remaining in session</span>
                    </div>
                  </div>
                </div>

                <div className="comp-card">
                  <div className="comp-title">Floating Dark Toast</div>
                  <div className="comp-preview">
                    <div className="toast-float text-xs">
                      ⚡ Streak updated (+12 Days)!
                    </div>
                  </div>
                </div>

              </div>
            </section>

          </div>
        )}

      </main>

    </div>
  );
}
