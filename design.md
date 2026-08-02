```markdown
# System Design Document (`design.md`)

* **System Name:** Warm UI — Gamified Habit & Focus Application Framework
* **Target Audience:** Cross-platform Mobile & Web Systems
* **Design Philosophy:** Tactile Warmth, High Contrast Scannability, Low Cognitive Load

---

## 1. Executive Summary & Core Context

**Warm UI** is a specialized design system and front-end architecture designed for gamified habit tracking, focus enforcement, and time-management mobile applications. 

The architecture bridges playful gamification (mascot leveling, XP gains, flame streaks, coin rewards) with precise, high-utility app-blocking and habit analytics tools. The design system uses an organic, tactile visual palette with heavy drop shadows (`4px` tactile pressed states), high-contrast warm color tokens, and explicit support for bidirectional layouts (LTR and RTL).

---

## 2. Design System Tokens & Foundations

### 2.1 Color Tokens & Palette Architecture

```css
:root {
    /* Brand & Accent Tokens */
    --primary: #F0813F;               /* Primary Action / Warm Orange */
    --primary-pressed: #D8672A;       /* Tactile Button Shadow / Active State */
    --hero-gradient-top: #FFD98A;     /* Warm Hero Top */
    --hero-gradient-bottom: #F5A623;  /* Warm Hero Bottom / Gold Highlight */
    --coin-gold: #F5A623;             /* Currency Token */
    --streak-flame: #FF7A29;          /* Flame / Streak Highlight */

    /* Status & Feedback Tokens */
    --success: #6FA84B;              /* Positive Action / Complete */
    --success-bg: #E9F3E1;           /* Soft Success Container */
    --danger: #D8443C;               /* Destructive / App Blocked Alert */
    --danger-bg: #FDE8E7;            /* Soft Danger Container */

    /* Surface & Background Tokens */
    --background: #FFFBF5;           /* Soft Warm Canvas */
    --surface: #FFFFFF;              /* Primary Elevation Layer */
    --border: #EDE6D8;               /* High-contrast Warm Border */
    --locked-bg: #F1EFE8;            /* Inactive / Disabled Component Surface */

    /* Typography Tokens */
    --text-primary: #3D2817;         /* Deep Warm Brown (Primary High Contrast) */
    --text-secondary: #8A7A68;       /* Muted Brown (Secondary Labels) */
    --text-muted: #B5A997;           /* Subdued Text / Micro-captions */

    /* Category Specific Accents */
    --category-body-bg: #FFF3E4;     --category-body-icon: #F0813F;
    --category-mind-bg: #F0EAF7;     --category-mind-icon: #9B6FC7;
    --category-habit-bg: #E9F3E1;    --category-habit-icon: #6FA84B;

    /* Elevation & Shadows */
    --shadow-sm: 0 2px 8px rgba(61, 40, 23, 0.04);
    --shadow-md: 0 8px 24px rgba(61, 40, 23, 0.07);
    --shadow-lg: 0 16px 36px rgba(61, 40, 23, 0.12);
}

```

### 2.2 Typography Hierarchy

* **Primary Font Family:** `Plus Jakarta Sans`, `Cairo` (RTL Fallback), `-apple-system`, `sans-serif`
* **Weights:** `400` (Regular), `500` (Medium), `600` (Semi-Bold), `700` (Bold), `800` (Extra-Bold / Micro-Headers)

| Level | Size | Weight | Line Height / Style | Target Usage |
| --- | --- | --- | --- | --- |
| `Heading-XL` | 32px | 800 | Uppercase / Tight | Screen Titles / Main Metric Display |
| `Heading-LG` | 22px / 28px | 800 | Default | Hero Headers & Modal Headlines |
| `Heading-MD` | 18px | 800 | Default | Card Titles / Step Headers |
| `Subheading` | 14px / 16px | 700 / 800 | Default | Interactive Section Headers |
| `Body-Primary` | 13px / 14px | 600 / 700 | Standard | Form Labels, Input Values, Card Details |
| `Caption / Badge` | 10px / 11px | 800 | Uppercase (Letter spacing `0.8px`) | Metadata, Micro Steps, Badges |

---

## 3. Atomic Component Specifications

### 3.1 Buttons & Interactive Elements

```
+------------------------------------+   +------------------------------------+
|         btn-primary                |   |          btn-secondary             |
|   Height: 48px | Radius: 14px      |   |   Height: 46px | Radius: 14px      |
|   BG: --primary (#F0813F)          |   |   BG: --surface (#FFFFFF)          |
|   Shadow: 0 4px 0 --primary-pressed|   |   Border: 1.5px solid --border     |
+------------------------------------+   +------------------------------------+

```

1. **Tactile Primary Button (`.btn-primary`)**
* **Dimensions:** Height: `48px`, Width: `100%`, Border Radius: `14px`.
* **Visuals:** Background: `var(--primary)`, Text: `#FFFFFF`, Weight: `700`.
* **Tactile Effect:** `box-shadow: 0 4px 0 var(--primary-pressed)`.
* **Active Interaction:** `transform: translateY(3px); box-shadow: 0 1px 0 var(--primary-pressed);`.


2. **Surface Secondary Button (`.btn-secondary`)**
* **Dimensions:** Height: `46px`, Width: `100%`, Border Radius: `14px`.
* **Visuals:** Background: `var(--surface)`, Border: `1.5px solid var(--border)`, Color: `var(--text-primary)`.
* **Tactile Effect:** `box-shadow: 0 2px 0 var(--border)`.


3. **Soft Pill Action (`.btn-pill`)**
* **Dimensions:** Height: `38px / 44px`, Padding: `0 16px / 24px`, Border Radius: `99px`.
* **Visuals:** Background: `var(--category-body-bg)`, Color: `var(--primary)`.


4. **Destructive & Success Action Pills (`.btn-danger`, `.btn-success`)**
* **Danger:** Background: `var(--danger-bg)`, Text: `var(--danger)`.
* **Success:** Background: `var(--success-bg)`, Text: `var(--success)`.



### 3.2 Form Controls & Selection Inputs

* **Base Text Input (`.input-base`)**
* Height: `44px` / `46px`, Border Radius: `12px`, Border: `1.5px solid var(--border)`, Background: `var(--background)`.
* **Focus State:** Border-color: `var(--primary)`, Box-shadow: `0 0 0 3px rgba(240,129,63,0.12)`.


* **OTP 4-Digit Security Grid (`.otp-input`)**
* Width: `46px`, Height: `50px` / `56px`, Text Alignment: `Center`, Font Size: `20px`, Weight: `800`.


* **Selection Chips (`.chip-option`)**
* Padding: `8px 12px`, Border Radius: `99px`, Border: `1.5px solid var(--border)`.
* **Selected State (`.chip-option.selected`):** Background: `var(--category-body-bg)`, Border-color: `var(--primary)`, Color: `var(--primary)`.


* **Tactile Toggle Switch (`.toggle-switch`)**
* Width: `48px`, Height: `26px`, Track Radius: `30px`. Thumb: `20px x 20px` circle with `2px` elevation.



### 3.3 Navigation & Container Structures

* **Bottom Navigation Bar (`.bottom-nav`)**
* Fixed Position / Elevation at bottom, Height: `62px` / `64px`, Surface: `var(--surface)`, Top Border: `1px solid var(--border)`.
* Grid: 5 equal column tabs (`Home`, `History`, `Leaderboard`, `Shop`, `Profile`). Active tab highlighted with `var(--category-body-bg)` soft pill and `var(--primary)` accent.


* **Card Container (`.card-box`)**
* Background: `var(--surface)`, Border: `1px solid var(--border)`, Border Radius: `18px`, Padding: `14px` / `16px`, Elevation: `var(--shadow-sm)`.


* **Progress Bar (`.progress-bar`)**
* Height: `8px`, Track: `var(--border)`, Fill: `var(--primary)` / `var(--success)` with pill edge radius (`99px`).



---

## 4. Application Screen Architecture (14 Screens)

```
[ 01. Onboarding: Lang ] ---> [ 02. Onboarding: Age ] ---> [ 03. Onboarding: Goal ]
                                                                     |
[ 06. Auth: Login ] <------- [ 05. Onboarding: Base ] <--- [ 04. Onboarding: Apps ]
        |
        +---> [ 07. Auth: OTP ] ---> [ 08. Auth: Reset ]
        |
        +---> [ 09. Home Dashboard ] <===> [ 10. History & Analytics ]
                     ||              <===> [ 11. Global Leaderboard ]
                     ||              <===> [ 12. Store & Paywall Pass ]
                     ||              <===> [ 13. User Profile Screen ]
                     ||              <===> [ 14. Full App Settings ]

```

### 4.1 Onboarding Flow (5 Screens)

1. **`01. Onboarding — Language & Locales`**
* **Components:** Step indicator (`1/5`), linear progress (`20%`), language selection buttons (LTR / RTL toggle).
* **State Change:** Toggling `العربية (RTL)` dynamically sets frame attribute `dir="rtl"`.


2. **`02. Onboarding — Age & Tier Segmentation`**
* **Components:** Step indicator (`2/5`), selection chip options for age bands: `<18` (Student offer trigger), `18-24`, `25-34`, `35+`.


3. **`03. Onboarding — Primary Focus Goal`**
* **Components:** Selection chips for core user intents (`Study & Work Focus`, `Better Sleep`, `Stop Doomscrolling`, `Family Time`).


4. **`04. Onboarding — App Blocklist Selection`**
* **Components:** Multi-select app target chips (`Instagram`, `TikTok`, `YouTube`, `Snapchat`, `Twitter/X`, `Games`).


5. **`05. Onboarding — Screen Time Baseline`**
* **Components:** Dynamic interactive range slider (`1h` to `10h`), real-time baseline metric reader (`4.5 Hours / day`).



### 4.2 Authentication Flow (3 Screens)

6. **`06. Auth — Standard Login`**
* **Components:** Email field, password field with mask control, "Remember me" checkbox, Forgot password trigger, "Continue as Guest" secondary button.


7. **`07. Auth — OTP Code Verification`**
* **Components:** 4-digit isolated PIN input fields, security code resend timer (`00:45`), verification button.


8. **`08. Auth — Password Reset`**
* **Components:** New password input, live visual password strength meter (`85% Strong`, `var(--success)` track).



### 4.3 Core Application Suite (6 Screens)

9. **`09. Home Dashboard`**
* **Top Bar:** User greeting, Gold Coin Wallet status badge (`🪙 1,250`).
* **Mascot Hero Banner:** Gradient container showcasing mascot state (`Finn the Flame Fox`), Streak Counter (`18 Days Flame 🔥`).
* **Today's Habits List:** Habit cards with dual action states (`✓ Done` state vs. `Claim +30🪙` primary trigger).


10. **`10. Activity History & Analytics`**
* **Metrics Banner:** Baseline savings comparison metric (`2.1 Hours Saved Daily` vs. `4.5h Baseline`, `↓ 53% Saved`).
* **Timeline Log:** Scrollable daily activity event stream displaying app block events vs. habit completion timestamps.


11. **`11. Global Leaderboard & XP Stats`**
* **User Rank Banner:** Highlighted personal rank card (`#4 Alex Mercer`, `2,850 XP`).
* **Leaderboard Stream:** Card-based list ranking global users by level (`Lvl 18`), streak days, and total XP earned.


12. **`12. Store & Paywall Pass`**
* **Header:** Dark wallet balance card (`🪙 1,250`).
* **Item Marketplace:** Purchase options for power-ups (e.g., `Streak Freeze Shield 🛡️` for `200🪙`).
* **Dynamic Paywall:** Age-tailored subscription promo (`Warm Pro Pass` - Student Tier: `$1.99 / Mo`).


13. **`13. Full Profile Screen`**
* **Profile Card:** Avatar badge (`Pro Pass`), user handle, Level/XP progress bar (`Level 14 • 2,850 / 3,000 XP`).
* **3-Column Metric Grid:** Quick-read counters for `Streak Days`, `Total Coins`, and `Gems`.
* **Lifetime Stats Container:** Summary grid tracking total blocked events, completed missions, and hours saved.


14. **`14. Full App Settings Screen`**
* **Account & Auth:** Email modification controls.
* **Blocked Apps Management:** Active blocklist monitor (`2/2 Free Used`) with removal triggers.
* **System Permissions Status:** OS-level service toggles (`Overlay Access: Granted ✓`, `Accessibility Service: Enable ⚠️`).
* **Notifications & Haptics:** Toggle switches for daily reminders, streak alerts, and sound effects.
* **Danger Zone:** Destructive triggers (`Reset Stats`, `Sign Out`).



---

## 5. Technical Architecture & Constraints

### 5.1 Native Accessibility & OS Overlay Requirements

* **Android Accessibility Service & Overlay Permissions:** To enforce real-time app blocking, the application architecture relies on system-level `SYSTEM_ALERT_WINDOW` (draw over apps) and `ACCESSIBILITY_SERVICE` to detect targeted app package launches and render the Warm UI blocking overlay.
* **RTL Layout Engine:** The interface strictly utilizes bidirectional CSS rules and logical properties (`margin-inline-start`, `flex-direction`) to accommodate Arabic (`ar`) locales without layout breakage.

### 5.2 Responsive & Mockup Constraints

* **Fixed Frame Dimensions:** Mobile viewport mockups are calibrated to an aspect ratio of `360px x 760px` within an outer hardware frame enclosure (`border: 10px solid #2B1D11`, `radius: 44px`).
* **Zero External Framework Overhead:** Core tokens and utility classes render seamlessly using pure CSS native variables and inline structural layouts.

```