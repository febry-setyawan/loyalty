# Wireframes & User Flow Diagrams
# Loyalty System - Interactive Flow Documentation

**Versi:** 1.0  
**Tanggal:** Desember 2024  
**Dokumen:** Wireframes & User Flow Diagrams  
**Project:** Loyalty System Development  
**Companion to:** UI/UX Design Specification

---

## 📋 Overview

Dokumen ini menyediakan detailed wireframes dan user flow diagrams yang mengilustrasikan interaksi user dengan sistem loyalty. Setiap flow dirancang untuk memberikan customer experience yang optimal dengan minimal friction.

---

## 🔄 Critical User Flows

### 1. User Registration & Onboarding Flow

```
START: App Launch
    ↓
┌─────────────────┐
│ Splash Screen   │ (2 seconds)
│                 │
│ [Logo + Brand]  │
└─────────┬───────┘
          ↓
┌─────────────────┐
│ Welcome Screen  │
│                 │
│ [Get Started]   │ ────┐
│ [I have account]│     │
└─────────┬───────┘     │
          ↓             │
┌─────────────────┐     │
│ Onboarding      │     │
│ Slides (3)      │     │
│                 │     │
│ Skip → ────────────────┤
│ Next → Continue │     │
└─────────┬───────┘     │
          ↓             │
┌─────────────────┐     │
│ Registration    │ ←───┘
│ Form            │
│                 │
│ [Create Account]│
└─────────┬───────┘
          ↓
┌─────────────────┐
│ Email/SMS       │
│ Verification    │
│                 │
│ Enter Code      │
└─────────┬───────┘
          ↓
┌─────────────────┐
│ Success &       │
│ Welcome Bonus   │
│                 │
│ +100 points     │
└─────────┬───────┘
          ↓
┌─────────────────┐
│ Main Dashboard  │
│ (First Time)    │
│                 │
│ [Tutorial Tour] │
└─────────────────┘
          ↓
       END
```

### 2. Point Earning Flow (QR Code Scan)

```
START: Main Dashboard
    ↓
┌─────────────────┐
│ Dashboard       │
│                 │
│ [Earn Points]───┼──→ Earn Button Pressed
└─────────────────┘
          ↓
┌─────────────────┐
│ Earning Options │
│                 │
│ [📱 Scan QR]    │ ←── Primary Method
│ [✏️ Manual]     │
│ [🏪 In-Store]   │
└─────────┬───────┘
          ↓
┌─────────────────┐
│ QR Scanner      │
│                 │
│ [Viewfinder]    │
│ Camera Active   │
│                 │
│ [Manual Entry]  │ ←── Fallback Option
│ [Flash Toggle]  │
└─────────┬───────┘
          ↓ (QR Detected)
┌─────────────────┐
│ Transaction     │
│ Confirmation    │
│                 │
│ Store: ABC Shop │
│ Amount: Rp 50K  │
│ Points: +50     │
│                 │
│ [Confirm]       │
│ [Cancel]        │
└─────────┬───────┘
          ↓ (Confirmed)
┌─────────────────┐
│ Processing...   │
│                 │
│ [Spinner]       │ ←── 2-3 seconds
│ "Earning points"│
└─────────┬───────┘
          ↓
┌─────────────────┐
│ Success Screen  │
│                 │
│ ✅ +50 Points   │
│ New Balance: 150│
│                 │
│ [View History]  │
│ [Earn More]     │
│ [Done]          │
└─────────┬───────┘
          ↓ (Auto after 3s or Done pressed)
┌─────────────────┐
│ Updated         │
│ Dashboard       │
│                 │
│ Balance: 150    │
└─────────────────┘
          ↓
       END
```

### 3. Reward Redemption Flow

```
START: Main Dashboard
    ↓
┌─────────────────┐
│ Dashboard       │
│                 │
│ [Rewards Tab]───┼──→ Bottom Nav
└─────────────────┘
          ↓
┌─────────────────┐
│ Rewards Catalog │
│                 │
│ [Categories]    │
│ [Search]        │
│ [Filter]        │
│                 │
│ Reward List     │
│ Available       │ ←── Show only affordable
│ Unavailable     │ ←── Grayed out
└─────────┬───────┘
          ↓ (Reward Selected)
┌─────────────────┐
│ Reward Details  │
│                 │
│ [Image Gallery] │
│ Description     │
│ Terms & Conds   │
│ Point Cost      │
│ Validity        │
│                 │
│ [Redeem Now]    │
│ [Add to Wishlist│ ←── Future feature
└─────────┬───────┘
          ↓ (Redeem Pressed)
┌─────────────────┐
│ Confirmation    │
│                 │
│ "Are you sure?" │
│ Current: 150 pts│
│ Cost: 100 pts   │
│ Remaining: 50   │
│                 │
│ [Yes, Redeem]   │
│ [Cancel]        │
└─────────┬───────┘
          ↓ (Confirmed)
┌─────────────────┐
│ Processing...   │
│                 │
│ [Spinner]       │ ←── 2-3 seconds
│ "Redeeming..."  │
└─────────┬───────┘
          ↓
┌─────────────────┐
│ Success Screen  │
│                 │
│ ✅ Redeemed!    │
│ Voucher Code:   │
│ ABC123XYZ       │
│                 │
│ [Use Now]       │ ←── External app/link
│ [Save to Wallet]│
│ [Email Me]      │
│ [Done]          │
└─────────┬───────┘
          ↓
┌─────────────────┐
│ Updated         │
│ Dashboard       │
│                 │
│ Balance: 50     │
│ [My Vouchers]   │ ←── New section
└─────────────────┘
          ↓
       END
```

### 4. Tier Progression Flow

```
TRIGGER: Points Threshold Reached
    ↓
┌─────────────────┐
│ Background      │
│ Process         │
│                 │
│ Points >= 1000  │ ←── Silver Tier
│ Trigger Upgrade │
└─────────┬───────┘
          ↓
┌─────────────────┐
│ Push            │
│ Notification    │
│                 │
│ "Congratulations│
│ You're now      │
│ Silver!"        │
└─────────┬───────┘
          ↓ (User opens app)
┌─────────────────┐
│ Upgrade Modal   │
│                 │
│ 🎉 SILVER TIER  │
│                 │
│ Unlocked:       │
│ • 2x Weekend    │
│ • Birthday Bonus│
│ • Early Access  │
│                 │
│ [Explore Benefits│
│ [Continue]      │
└─────────┬───────┘
          ↓ (Explore pressed)
┌─────────────────┐
│ Tier Benefits   │
│ Overview        │
│                 │
│ Current: Silver │
│ Progress to Gold│ ─── Next Goal
│ ██░░░░░░ 25%    │
│                 │
│ All Tiers:      │
│ Bronze          │ ←── Completed
│ Silver ✓        │ ←── Current  
│ Gold            │ ←── Next
│ Platinum        │ ←── Future
└─────────┬───────┘
          ↓
┌─────────────────┐
│ Updated         │
│ Dashboard       │
│                 │
│ SILVER Badge    │ ←── Visual indicator
│ Progress to Gold│
└─────────────────┘
          ↓
       END
```

---

## 📱 Screen-by-Screen Wireframes

### Authentication Screens

#### Welcome Screen
```
┌─────────────────────────────┐
│                             │
│                             │
│        [APP LOGO]           │ ←── Large, memorable
│         120x120             │
│                             │
│      LoyaltyRewards         │ ←── 24px, bold
│                             │
│    Earn points with         │ ←── 16px, light
│    every purchase           │
│                             │
│                             │
│  ┌─────────────────────┐    │
│  │   GET STARTED       │    │ ←── Primary CTA
│  └─────────────────────┘    │     48px height
│                             │
│        Have account?        │ ←── 14px, link
│         [Sign In]           │
│                             │
│                             │
│      Terms • Privacy        │ ←── Legal links
│                             │
└─────────────────────────────┘
```

#### Registration Form
```
┌─────────────────────────────┐
│ [←] Create Account          │ ←── Header with back
├─────────────────────────────┤
│                             │
│  Step 1 of 2                │ ←── Progress indicator
│  ●●○                        │
│                             │
│  Personal Information       │ ←── Section title
│                             │
│  ┌─────────────────────┐    │
│  │ Full Name           │    │ ←── 48px height inputs
│  └─────────────────────┘    │     for touch-friendly
│                             │
│  ┌─────────────────────┐    │
│  │ Email Address       │    │
│  └─────────────────────┘    │
│                             │
│  ┌─────────────────────┐    │
│  │ Phone Number   [🇮🇩] │    │ ←── Country selector
│  └─────────────────────┘    │
│                             │
│  ┌─────────────────────┐    │
│  │ Create Password [👁] │    │ ←── Show/hide toggle
│  └─────────────────────┘    │
│                             │
│  Password must contain:     │ ←── Real-time validation
│  ✓ 8+ characters           │
│  ✓ 1 uppercase             │
│  ○ 1 number                │
│                             │
│  ┌─────────────────────┐    │
│  │      CONTINUE       │    │ ←── Disabled until valid
│  └─────────────────────┘    │
│                             │
└─────────────────────────────┘
```

### Main Dashboard Wireframe

#### Dashboard - First Time User
```
┌─────────────────────────────┐
│ [☰] LoyaltyRewards    [🔔]  │ ←── Header with menu & notifications
├─────────────────────────────┤
│                             │
│ 👋 Welcome, Sarah!          │ ←── Personalized greeting
│ Let's get you started       │     18px, friendly tone
│                             │
│ ┌─────────────────────────┐ │
│ │      Your Points        │ │ ←── Main points card
│ │         100             │ │     Large, prominent
│ │    Welcome Bonus!       │ │     48px for number
│ │                         │ │
│ │  Next Goal: 400 more    │ │ ←── Clear next step
│ │  to reach Silver        │ │     Motivational
│ │  ██░░░░░░░░ 20%         │ │     Progress bar
│ └─────────────────────────┘ │
│                             │
│ How to earn points          │ ←── Educational section
│ ┌─────────────────────────┐ │
│ │ 📱 Scan QR codes        │ │ ←── Primary method
│ │ 🛍️ Shop at partners     │ │     Clear icons + text
│ │ 🎂 Birthday & bonuses   │ │     16px text
│ └─────────────────────────┘ │
│                             │
│      [START EARNING]        │ ←── Large CTA button
│                             │     Primary action
│ Featured Rewards            │ ←── Teaser content
│ ┌──┬──┬──┐                  │     3-column grid
│ │[]│[]│[]│                  │     120px width each
│ └──┴──┴──┘                  │
│ [Browse All Rewards]        │ ←── Secondary action
│                             │
├─────────────────────────────┤
│ [🏠][💰][🎁][👤][⚙️]        │ ←── Bottom navigation
└─────────────────────────────┘     48px height
```

#### Dashboard - Returning User
```
┌─────────────────────────────┐
│ [☰] LoyaltyRewards    [🔔•] │ ←── Notification badge
├─────────────────────────────┤
│                             │
│ Good morning, Sarah! ☀️     │ ←── Time-based greeting
│ SILVER Member               │     Tier badge
│                             │
│ ┌─────────────────────────┐ │
│ │     Your Points         │ │
│ │       2,450             │ │ ←── Current balance
│ │  [Earn] [Redeem]        │ │     Action buttons
│ │                         │ │     inline
│ │   Progress to Gold      │ │
│ │   ████████░░ 80%        │ │ ←── Tier progress
│ │   550 points needed     │ │     Clear target
│ └─────────────────────────┘ │
│                             │
│ Recent Activity             │ ←── Activity feed
│ • +125 pts • Coffee Shop   │     Recent transactions
│ • Redeemed • Movie Ticket  │     With timestamps
│   2 hours ago              │
│                             │
│ Quick Actions               │ ←── Shortcut grid
│ ┌───┬───┬───┬───┐           │     4-column layout
│ │📱 │📊 │🏪 │🎁│           │     64px squares
│ │Scan│Hist│Store│Rewards│   │     12px labels
│ └───┴───┴───┴───┘           │
│                             │
│ Recommended for You         │ ←── Personalized content
│ ┌─────────────────────────┐ │
│ │ [IMG] Coffee Voucher    │ │ ←── 280px wide card
│ │       500 Points        │ │     Swipeable carousel
│ │     [Redeem Now]        │ │
│ └─────────────────────────┘ │
│                             │
├─────────────────────────────┤
│ [🏠][💰][🎁][👤][⚙️]        │
└─────────────────────────────┘
```

### QR Code Scanning Interface

#### Scanner Active State
```
┌─────────────────────────────┐
│ [←] Scan QR Code       [💡] │ ←── Back button + flash toggle
├─────────────────────────────┤
│                             │ ←── Full screen camera
│  ┌─────────────────────┐    │     Dark overlay with
│  │                     │    │     cutout for viewfinder
│  │   [CAMERA PREVIEW]  │    │
│  │                     │    │     280x280px square
│  │   ┌─────────────┐   │    │     Animated corners
│  │   │ ╭─╮     ╭─╮ │   │    │     to show active area
│  │   │ ╰─╯ ▒▒▒ ╰─╯ │   │    │
│  │   │ ▒▒▒ ▒▒▒ ▒▒▒ │   │    │ ←── QR pattern visual
│  │   │ ╭─╮ ▒▒▒ ╭─╮ │   │    │     for demonstration
│  │   │ ╰─╯     ╰─╯ │   │    │
│  │   └─────────────┘   │    │
│  │                     │    │
│  └─────────────────────┘    │
│                             │
│    Align QR code within     │ ←── Instruction text
│         the frame           │     16px, white text
│                             │     with shadow/bg
│                             │
│      Having trouble?        │ ←── Help option
│      [Manual Entry]         │     Secondary action
│                             │
│                             │
│         [🏪 Find Stores]     │ ←── Tertiary action
│                             │     Help users find
│                             │     participating locations
└─────────────────────────────┘
```

#### QR Detected State
```
┌─────────────────────────────┐
│ [←] Scan QR Code       [💡] │
├─────────────────────────────┤
│                             │
│  ┌─────────────────────┐    │
│  │                     │    │
│  │   [CAMERA PREVIEW]  │    │
│  │                     │    │
│  │   ┌─────────────┐   │    │ ←── Green border when
│  │   │ ╭─╮     ╭─╮ │   │    │     QR code detected
│  │   │ ╰─╯ ▒▒▒ ╰─╯ │   │    │     Success state
│  │   │ ▒▒▒ ▒▒▒ ▒▒▒ │   │    │
│  │   │ ╭─╮ ▒▒▒ ╭─╮ │   │    │
│  │   │ ╰─╯     ╰─╯ │   │    │
│  │   └─────────────┘   │    │
│  │                     │    │
│  └─────────────────────┘    │
│                             │
│         ✓ QR Code           │ ←── Success feedback
│          Detected           │     Green text
│                             │
│        Processing...        │ ←── Loading state
│         ●○○○○               │     Animation
│                             │
└─────────────────────────────┘
```

### Rewards Catalog Wireframes

#### Rewards Browse Screen
```
┌─────────────────────────────┐
│ [←] Rewards          [🔍]   │ ←── Search icon
├─────────────────────────────┤
│                             │
│ Your Points: 2,575          │ ←── Current balance
│ Available Rewards: 24       │     Prominent display
│                             │
│ Categories                  │ ←── Filter chips
│ [All] [Food] [Shopping]     │     Horizontal scroll
│ [Travel] [Entertainment]    │     Active state styling
│                             │
│ Sort by: [Newest ▼]         │ ←── Sort dropdown
│                             │
│ ┌─────────────────────────┐ │ ←── Reward cards
│ │ [IMG]  Coffee Voucher   │ │     Left: 80x80 image
│ │        Starbucks        │ │     Right: Info
│ │        500 Points       │ │     
│ │        ✓ Available      │ │ ←── Status indicator
│ │        [Redeem]         │ │     Call-to-action
│ └─────────────────────────┘ │
│                             │
│ ┌─────────────────────────┐ │
│ │ [IMG]  Movie Tickets    │ │
│ │        Cinema XXI       │ │
│ │        1,200 Points     │ │
│ │        ✓ Available      │ │
│ │        [Redeem]         │ │
│ └─────────────────────────┘ │
│                             │
│ ┌─────────────────────────┐ │
│ │ [IMG]  Dinner Voucher   │ │
│ │        Restaurant ABC   │ │
│ │        3,000 Points     │ │ ←── Too expensive
│ │        🔒 Gold Only      │ │     Tier restriction
│ │        [View]           │ │     Different action
│ └─────────────────────────┘ │
│                             │
├─────────────────────────────┤
│ [🏠][💰][🎁][👤][⚙️]        │
└─────────────────────────────┘
```

#### Individual Reward Detail
```
┌─────────────────────────────┐
│ [←] Coffee Voucher     [❤]  │ ←── Favorite/wishlist
├─────────────────────────────┤
│                             │
│    [LARGE REWARD IMAGE]     │ ←── Hero image
│         280x200px           │     High quality photo
│                             │
│ ┌─────────────────────────┐ │
│ │ Starbucks Coffee        │ │ ←── Reward title
│ │ Voucher                 │ │     18px, bold
│ │                         │ │
│ │ 500 Points              │ │ ←── Point cost
│ │                         │ │     Large, prominent
│ │ You have: 2,575         │ │     User's balance
│ │ Remaining: 2,075        │ │     After redemption
│ │                         │ │
│ │ ✓ You can redeem this   │ │ ←── Eligibility status
│ └─────────────────────────┘ │
│                             │
│ Details                     │ ←── Expandable sections
│ • Valid for: 6 months       │
│ • Category: Food & Drink    │
│ • Partner: Starbucks        │
│ • Locations: All outlets    │
│                             │
│ Description                 │
│ Enjoy any menu item up to   │ ←── Full description
│ Rp 75,000 at participating  │     Wrap text
│ Starbucks locations.        │
│                             │
│ Terms & Conditions          │ ←── Legal text
│ [Show More ▼]               │     Collapsible
│                             │
│  ┌─────────────────────┐    │
│  │    REDEEM NOW       │    │ ←── Primary CTA
│  └─────────────────────┘    │     Prominent button
│                             │
│      [Add to Wishlist]      │ ←── Secondary action
│                             │
└─────────────────────────────┘
```

### Profile & Settings Wireframes

#### User Profile Screen
```
┌─────────────────────────────┐
│ [←] Profile           [⚙️]  │ ←── Settings gear icon
├─────────────────────────────┤
│                             │
│     [PROFILE PICTURE]       │ ←── 120x120px circle
│         [Edit Photo]        │     Tap to change
│                             │
│      Sarah Johnson          │ ←── Display name
│      sarah@email.com        │     18px, centered
│      +62 812-3456-7890      │     Secondary info
│      Member since Dec 2023  │     14px, gray
│                             │
│ ┌─────────────────────────┐ │
│ │      SILVER TIER        │ │ ←── Tier status card
│ │        [TIER ICON]      │ │     Visually prominent
│ │                         │ │
│ │    Current Points:      │ │
│ │        2,575            │ │ ←── Large number
│ │                         │ │     32px font
│ │   Next Tier Progress:   │ │
│ │   ████████░░ 80%        │ │ ←── Progress bar
│ │   425 points to Gold    │ │     Clear target
│ └─────────────────────────┘ │
│                             │
│ Tier Benefits               │ ←── Current benefits
│ ✓ 2x points on weekends     │     Checkmarks
│ ✓ Birthday bonus (100 pts)  │     Green icons
│ ✓ Exclusive weekend offers  │     List format
│ ✓ Priority customer service │
│                             │
│ Quick Stats                 │ ←── Achievements
│ Points earned this month: 650│     Gamification
│ Rewards redeemed: 12        │     elements
│ Favorite category: Food     │
│                             │
│      [View All Tiers]       │ ←── Secondary action
│      [Edit Profile]         │     Account management
│                             │
├─────────────────────────────┤
│ [🏠][💰][🎁][👤][⚙️]        │
└─────────────────────────────┘
```

---

## 🖥️ Admin Dashboard Wireframes

### Admin Dashboard Overview
```
┌─────────────────────────────────────────────────────────────────┐
│ LoyaltyAdmin                              [👤 Admin] [🚪 Logout] │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│ ┌─────────────┐                                                │
│ │ DASHBOARD   │ Members  Rewards  Points  Reports  Settings    │ ←── Tab navigation
│ └─────────────┘                                                │     Clear active state
│                                                                 │
│ Welcome back, Admin! Last login: Dec 15, 2:30 PM               │ ←── Personal greeting
│                                                                 │
│ Key Performance Indicators                   [Last 30 Days ▼]  │ ←── Time filter
│ ┌───────────────┬───────────────┬───────────────┬─────────────┐ │
│ │ Total Members │ Active Points │ Redeemed      │ Revenue     │ │ ←── KPI cards
│ │               │               │ This Month    │ Impact      │ │     4-column grid
│ │   25,847      │   2.1M        │   450K        │ Rp 125M     │ │     Large numbers
│ │   ⬆ +12%     │   ⬆ +8%      │   ⬆ +15%     │ ⬆ +18%     │ │     Trend indicators
│ │   vs last     │   vs last     │   vs last     │ vs last     │ │     Growth metrics
│ │   month       │   month       │   month       │ month       │ │
│ └───────────────┴───────────────┴───────────────┴─────────────┘ │
│                                                                 │
│ Activity Overview                       Member Distribution     │
│ ┌─────────────────────────────────────┐ ┌───────────────────┐   │
│ │        [LINE CHART]                 │ │   [DONUT CHART]   │   │ ←── Data visualizations
│ │                                     │ │                   │   │     Charts with
│ │  Points  •••••                      │ │  Bronze   45%     │   │     clear labels
│ │  150K   •     •••                   │ │  Silver   30%     │   │
│ │  100K  •         ••                 │ │  Gold     20%     │   │
│ │   50K•             •                │ │  Platinum  5%     │   │
│ │     ────────────────────             │ │                   │   │
│ │     Week 1  Week 2  Week 3          │ └───────────────────┘   │
│ └─────────────────────────────────────┘                         │
│                                                                 │
│ Recent Activity                        System Health            │
│ ┌─────────────────────────────────────┐ ┌───────────────────┐   │
│ │ • New member: John Smith            │ │ API Response      │   │ ←── Real-time feeds
│ │   2 minutes ago                     │ │ ●●●○ 250ms        │   │     Status indicators
│ │ • 500 points redeemed               │ │                   │   │
│ │   5 minutes ago                     │ │ Database          │   │
│ │ • New reward: Coffee Voucher        │ │ ●●●● 15ms         │   │
│ │   1 hour ago                        │ │                   │   │
│ │ • Member tier upgraded: Gold        │ │ Server Load       │   │
│ │   2 hours ago                       │ │ ●●○○ 65%          │   │
│ │                                     │ └───────────────────┘   │
│ │         [View All Activity]         │                         │
│ └─────────────────────────────────────┘                         │
│                                                                 │
│ Quick Actions                                                   │
│ [+ Add Member] [+ Add Reward] [📊 Generate Report] [⚙️ Settings] │ ←── Action buttons
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Member Management Interface
```
┌─────────────────────────────────────────────────────────────────┐
│ LoyaltyAdmin                              [👤 Admin] [🚪 Logout] │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│ Dashboard  ┌─────────────┐  Rewards  Points  Reports  Settings  │
│            │   MEMBERS   │                                      │
│            └─────────────┘                                      │
│                                                                 │
│ Member Management                           Total: 25,847 users │ ←── Count display
│                                                                 │
│ ┌───────────────────────────────────────────────────────────┐   │
│ │ [🔍 Search by name, email, phone...]  [Advanced Filter ▼]│   │ ←── Search & filter bar
│ │                                                           │   │     Full-width input
│ │ Status: [All ▼]  Tier: [All ▼]  Date: [This Month ▼]    │   │     Multiple filters
│ │                                        [+ Add Member]     │   │     Primary action
│ └───────────────────────────────────────────────────────────┘   │
│                                                                 │
│ Bulk Actions                                                    │
│ [ ] Select All  (0 selected)                                   │ ←── Bulk operations
│ [📧 Send Email] [📤 Export] [🚫 Suspend] [🗑️ Delete]            │     Disabled when none
│                                                                 │     selected
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │[ ]│Name         │Email            │Tier  │Points│Status │⚙️ │ │ ←── Data table
│ ├───┼─────────────┼─────────────────┼──────┼──────┼───────┼───┤ │     Sortable columns
│ │[ ]│Sarah Johnson│sarah@email.com  │Silver│2,575 │Active │⋮ │ │     Action menu
│ │[ ]│John Doe     │john@email.com   │Gold  │3,250 │Active │⋮ │ │     Checkbox select
│ │[ ]│Mary Smith   │mary@email.com   │Bronze│1,125 │Active │⋮ │ │
│ │[ ]│David Wilson │david@email.com  │Silver│2,100 │Suspend│⋮ │ │ ←── Status indicators
│ │[ ]│Lisa Brown   │lisa@email.com   │Gold  │4,750 │Active │⋮ │ │     Color coding
│ │[ ]│Mike Johnson │mike@email.com   │Bronze│  825 │Active │⋮ │ │
│ │[ ]│Anna Davis   │anna@email.com   │Platinum│8,500│Active │⋮ │ │
│ └─────────────────────────────────────────────────────────────┘ │
│                                                                 │
│ [← Previous]  Page 1 of 524  [Next →]              50 per page │ ←── Pagination
│                                                                 │     Page size option
│ Export Options                                                  │
│ [📊 CSV Export] [📋 Excel Export] [📧 Email Report]             │ ←── Export actions
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Member Detail Modal/Page
```
┌─────────────────────────────────────────────────────────────────┐
│ [←] Sarah Johnson                      [Edit] [Suspend] [Delete] │ ←── Action buttons
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│ Member Profile                                                  │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │     [PROFILE PHOTO]    Sarah Johnson                       │ │ ←── Personal info
│ │        120x120          sarah@email.com                    │ │     section
│ │                         +62 812-3456-7890                 │ │
│ │                         Joined: Dec 15, 2023              │ │
│ │                         Last Login: 2 hours ago           │ │
│ │                                                           │ │
│ │ SILVER TIER            Points Balance: 2,575              │ │ ←── Key metrics
│ │ Progress to Gold       Lifetime Points: 5,200             │ │     prominently
│ │ ████████░░ 80%         Points Redeemed: 2,625             │ │     displayed
│ └─────────────────────────────────────────────────────────────┘ │
│                                                                 │
│ Activity Summary                                                │
│ ┌──────────────────────┬──────────────────────┬───────────────┐ │
│ │ Points Earned        │ Rewards Redeemed     │ Avg Monthly   │ │ ←── Statistics cards
│ │ This Month: 650      │ This Month: 3        │ Activity: 12  │ │
│ │ Last Month: 480      │ Last Month: 2        │ transactions  │ │
│ │ ⬆ +35% increase      │ ⬆ +50% increase      │ ⬆ +15% trend │ │
│ └──────────────────────┴──────────────────────┴───────────────┘ │
│                                                                 │
│ Recent Transactions                                   [View All]│ ←── Transaction history
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ Dec 15  +125 pts  Coffee Shop Purchase  Rp 125,000         │ │     Scrollable list
│ │ Dec 14  -500 pts  Coffee Voucher Redemption                │ │     with details
│ │ Dec 13  +200 pts  Restaurant ABC Purchase  Rp 200,000      │ │
│ │ Dec 12  +50 pts   Bonus Weekend Multiplier (2x)            │ │
│ │ Dec 11  -1200 pts Movie Tickets Redemption                 │ │
│ └─────────────────────────────────────────────────────────────┘ │
│                                                                 │
│ Account Actions                                                 │
│ [📧 Send Email] [🔄 Reset Password] [⚙️ Adjust Points] [📊 Report]│ ←── Management actions
│                                                                 │
│ Audit Log                                                       │
│ • Profile updated by Admin on Dec 14, 3:45 PM                  │ ←── Admin activity log
│ • Points adjusted (+100) by System on Dec 10, 12:00 PM         │     for compliance
│ • Tier upgraded to Silver on Dec 8, 6:20 PM                    │     and tracking
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔄 State Management & Interactions

### Loading States
```
1. Initial App Load
   ┌─────────────────────────────┐
   │          [LOGO]             │ ←── Splash screen
   │        Loading...           │     2-3 seconds
   │         ●○○○○               │     Progress indicator
   └─────────────────────────────┘

2. Authentication Loading
   ┌─────────────────────────────┐
   │      [LOGIN FORM]           │ ←── Form remains visible
   │                             │     Button shows loading
   │    [Signing In...] ⟳        │     Spinner in button
   └─────────────────────────────┘

3. Data Fetching
   ┌─────────────────────────────┐
   │ Your Rewards                │ ←── Section title visible
   │                             │     Content skeleton
   │ ┌─────┐ ┌─────┐ ┌─────┐     │     Placeholder cards
   │ │ ░░░ │ │ ░░░ │ │ ░░░ │     │     Gray blocks
   │ │ ░░░ │ │ ░░░ │ │ ░░░ │     │     Maintain layout
   │ └─────┘ └─────┘ └─────┘     │
   └─────────────────────────────┘

4. Action Processing
   ┌─────────────────────────────┐
   │      Processing...          │ ←── Full screen overlay
   │         ⟳                   │     Large spinner
   │    Earning points...        │     Descriptive text
   │                             │     Semi-transparent
   └─────────────────────────────┘     background
```

### Error States
```
1. Network Error
   ┌─────────────────────────────┐
   │         📶 ✗               │ ←── Network icon with X
   │                             │
   │    Connection Problem       │     Clear error message
   │                             │
   │  Please check your internet │     Helpful explanation
   │  connection and try again   │
   │                             │
   │       [Try Again]           │ ←── Clear action
   │                             │     Retry mechanism
   └─────────────────────────────┘

2. Insufficient Points
   ┌─────────────────────────────┐
   │          ⚠️                 │ ←── Warning icon
   │                             │
   │   Not Enough Points         │     Clear title
   │                             │
   │  You need 500 points but    │     Specific details
   │  only have 250 points       │     Current vs required
   │                             │
   │   [Earn More Points]        │ ←── Constructive action
   │        [Cancel]             │     Alternative option
   └─────────────────────────────┘

3. Server Error
   ┌─────────────────────────────┐
   │          🔧                 │ ←── Technical icon
   │                             │
   │  Something went wrong       │     User-friendly message
   │                             │
   │  We're working to fix this  │     Reassuring explanation
   │  Please try again later     │
   │                             │
   │    [Contact Support]        │ ←── Help option
   │       [Try Again]           │     Retry option
   └─────────────────────────────┘
```

### Empty States
```
1. No Transaction History
   ┌─────────────────────────────┐
   │         📊                  │ ←── Relevant icon
   │                             │
   │    No transactions yet      │     Clear empty state
   │                             │     message
   │   Start earning points by   │     Guidance text
   │   shopping at partner       │     Next steps
   │         stores              │
   │                             │
   │     [Find Stores]           │ ←── Action to resolve
   │      [Scan QR]              │     Multiple options
   └─────────────────────────────┘

2. No Rewards Available
   ┌─────────────────────────────┐
   │         🎁                  │
   │                             │
   │  No rewards in this         │
   │      category               │
   │                             │
   │  Try browsing other         │
   │  categories or check        │
   │  back later                 │
   │                             │
   │    [Browse All]             │
   └─────────────────────────────┘

3. Search No Results
   ┌─────────────────────────────┐
   │         🔍                  │
   │                             │
   │   No results found for      │
   │      "coffee voucher"       │     Show search term
   │                             │
   │  Try different keywords     │     Suggestions
   │  or browse categories       │
   │                             │
   │   [Clear Search]            │ ←── Reset action
   │   [Browse Categories]       │     Alternative path
   └─────────────────────────────┘
```

---

## 📊 Success & Feedback States

### Success Confirmations
```
1. Points Earned Success
   ┌─────────────────────────────┐
   │          ✅                 │ ←── Large success icon
   │                             │     Green color
   │      +125 Points            │     Prominent number
   │       Earned!               │     48px font size
   │                             │
   │   Coffee Shop Purchase      │ ←── Transaction details
   │   Amount: Rp 125,000        │     Context information
   │   Date: Dec 15, 2024        │
   │                             │
   │  New Balance: 2,575 points  │ ←── Updated balance
   │                             │
   │      [View History]         │ ←── Secondary action
   │         [Done]              │     Primary action
   └─────────────────────────────┘

2. Redemption Success
   ┌─────────────────────────────┐
   │          🎉                 │ ←── Celebration icon
   │                             │
   │    Reward Redeemed!         │     Success message
   │                             │
   │     Coffee Voucher          │ ←── Reward name
   │                             │
   │    Your voucher code:       │     Important info
   │      ABC123XYZ              │     Copy-able code
   │                             │     Large, selectable
   │  Valid until: Jun 15, 2025  │     Expiry date
   │                             │
   │     [Copy Code]             │ ←── Useful action
   │     [Email Me]              │     Additional options
   │     [Done]                  │     Continue flow
   └─────────────────────────────┘

3. Profile Update Success
   ┌─────────────────────────────┐
   │          ✓                  │ ←── Simple checkmark
   │                             │
   │  Profile Updated            │     Brief confirmation
   │   Successfully              │     Non-intrusive
   │                             │
   │    Auto-dismiss in 3s       │ ←── Temporary toast
   └─────────────────────────────┘     notification style
```

### Tier Upgrade Celebration
```
┌─────────────────────────────┐
│                             │ ←── Full screen modal
│        🥈 ✨                │     Celebratory graphics
│                             │     Animation on entry
│   CONGRATULATIONS!          │
│                             │
│    You're now SILVER!       │ ←── Big announcement
│                             │     24px bold text
│                             │
│  ┌─────────────────────────┐ │
│  │    Unlocked Benefits    │ │ ←── Benefits showcase
│  │                         │ │
│  │ ✓ 2x Weekend Points     │ │     Feature highlights
│  │ ✓ Birthday Bonus        │ │     with checkmarks
│  │ ✓ Exclusive Offers      │ │
│  │ ✓ Priority Support      │ │
│  └─────────────────────────┘ │
│                             │
│   Next Goal: Gold Tier      │ ←── Future motivation
│   2,000 more points needed  │     Clear target
│                             │
│    [Explore Benefits]       │ ←── Learn more action
│       [Continue]            │     Main CTA
│                             │
└─────────────────────────────┘
```

---

## 🎯 Design Guidelines Summary

### Mobile-First Principles
1. **Touch-Friendly Sizing:** Minimum 44px touch targets
2. **Thumb Navigation:** Key actions within thumb reach
3. **Progressive Disclosure:** Show essential info first
4. **Offline Capability:** Core functions work offline
5. **Performance First:** <3 second load times

### Visual Hierarchy
1. **F-Pattern Layout:** Users scan left-to-right, top-to-bottom
2. **Size & Color:** Larger, darker elements get attention first
3. **White Space:** Breathing room around important elements
4. **Grouping:** Related items visually connected
5. **Contrast:** 4.5:1 minimum for accessibility

### User Experience Flow
1. **Onboarding:** 3 screens max, clear value proposition
2. **Registration:** Single screen, social login options
3. **Core Actions:** 2 taps maximum to main functions
4. **Feedback:** Immediate response to all interactions
5. **Recovery:** Clear error states with resolution paths

### Consistency Standards
1. **Component Reuse:** Same elements across screens
2. **Interaction Patterns:** Consistent tap/swipe behaviors
3. **Visual Language:** Unified colors, typography, spacing
4. **Copy Tone:** Friendly, encouraging, clear
5. **Icon Style:** Consistent line weight and style

---

**📍 Document Status:** Ready for Developer Implementation  
**🔄 Version:** 1.0 - Initial Wireframes & Flows  
**👤 Next Review:** Product Owner + Technical Lead  
**📅 Implementation Start:** Phase 1 Development Kickoff