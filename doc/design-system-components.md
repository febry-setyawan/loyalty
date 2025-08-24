# Design System & Component Library
# Loyalty System - UI Component Specifications

**Version:** 1.0  
**Date:** December 2024  
**Document:** Design System & Component Library  
**Project:** Loyalty System Development  
**Target:** Frontend Development Team

---

## 📋 Overview

This document provides complete specifications for UI components that will be used in the loyalty system. This design system ensures visual and interaction consistency throughout the application, both mobile and web admin.

---

## 🎨 Foundation Design Tokens

### Color System
```scss
// Brand Colors
$primary-blue: #1E40AF;       // Main brand color
$primary-gold: #F59E0B;       // Points/rewards accent
$primary-green: #10B981;      // Success states
$primary-purple: #8B5CF6;     // Premium tier

// Semantic Colors
$success: #10B981;            // Success messages
$warning: #F59E0B;            // Warning states
$error: #EF4444;              // Error states
$info: #3B82F6;               // Information

// Neutral Palette
$white: #FFFFFF;
$gray-50: #F9FAFB;            // Light backgrounds
$gray-100: #F3F4F6;          // Card backgrounds
$gray-200: #E5E7EB;          // Borders light
$gray-300: #D1D5DB;          // Borders
$gray-400: #9CA3AF;          // Disabled text
$gray-500: #6B7280;          // Secondary text
$gray-600: #4B5563;          // Body text
$gray-700: #374151;          // Headings
$gray-800: #1F2937;          // Dark text
$gray-900: #111827;          // Highest contrast

// Alpha Variants (for overlays, shadows)
$black-alpha-10: rgba(0, 0, 0, 0.1);
$black-alpha-25: rgba(0, 0, 0, 0.25);
$black-alpha-50: rgba(0, 0, 0, 0.5);
$white-alpha-90: rgba(255, 255, 255, 0.9);

// Tier Colors
$tier-bronze: #CD7F32;
$tier-silver: #C0C0C0;
$tier-gold: #FFD700;
$tier-platinum: #E5E4E2;
```

### Typography Scale
```scss
// Font Families
$font-primary: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
$font-display: 'Poppins', sans-serif;  // For headings and emphasis
$font-mono: 'Fira Code', 'Monaco', monospace;  // For codes and technical text

// Font Weights
$font-thin: 100;
$font-light: 300;
$font-normal: 400;
$font-medium: 500;
$font-semibold: 600;
$font-bold: 700;
$font-extrabold: 800;

// Font Sizes (Mobile First)
$text-xs: 0.75rem;     // 12px - Small labels, captions
$text-sm: 0.875rem;    // 14px - Secondary text
$text-base: 1rem;      // 16px - Base body text
$text-lg: 1.125rem;    // 18px - Large body text
$text-xl: 1.25rem;     // 20px - Small headings
$text-2xl: 1.5rem;     // 24px - Section headings
$text-3xl: 1.875rem;   // 30px - Page headings
$text-4xl: 2.25rem;    // 36px - Hero text
$text-5xl: 3rem;       // 48px - Display text

// Line Heights
$leading-tight: 1.25;   // Headlines
$leading-normal: 1.5;   // Body text
$leading-relaxed: 1.625; // Large text blocks
```

### Spacing System
```scss
// Base unit: 4px (0.25rem)
$space-px: 1px;
$space-0: 0;
$space-1: 0.25rem;     // 4px
$space-2: 0.5rem;      // 8px
$space-3: 0.75rem;     // 12px
$space-4: 1rem;        // 16px
$space-5: 1.25rem;     // 20px
$space-6: 1.5rem;      // 24px
$space-8: 2rem;        // 32px
$space-10: 2.5rem;     // 40px
$space-12: 3rem;       // 48px
$space-16: 4rem;       // 64px
$space-20: 5rem;       // 80px
$space-24: 6rem;       // 96px

// Semantic Spacing (for consistency)
$gap-xs: $space-2;     // Small gaps
$gap-sm: $space-4;     // Medium gaps
$gap-md: $space-6;     // Large gaps
$gap-lg: $space-8;     // Extra large gaps
$gap-xl: $space-12;    // Section gaps

$padding-xs: $space-2; // Button padding small
$padding-sm: $space-3; // Input padding
$padding-md: $space-4; // Card padding
$padding-lg: $space-6; // Section padding
$padding-xl: $space-8; // Page padding
```

### Border Radius
```scss
$radius-none: 0;
$radius-sm: 0.125rem;   // 2px
$radius-base: 0.25rem;  // 4px
$radius-md: 0.375rem;   // 6px
$radius-lg: 0.5rem;     // 8px
$radius-xl: 0.75rem;    // 12px
$radius-2xl: 1rem;      // 16px
$radius-full: 9999px;   // Perfect circle

// Semantic Radius
$radius-button: $radius-lg;    // Buttons
$radius-card: $radius-xl;      // Cards
$radius-input: $radius-md;     // Form inputs
$radius-badge: $radius-full;   // Badges, pills
```

### Shadows
```scss
$shadow-xs: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
$shadow-sm: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
$shadow-base: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
$shadow-md: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
$shadow-lg: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
$shadow-xl: 0 25px 50px -12px rgba(0, 0, 0, 0.25);

// Colored Shadows
$shadow-primary: 0 4px 14px 0 rgba(30, 64, 175, 0.15);
$shadow-success: 0 4px 14px 0 rgba(16, 185, 129, 0.15);
$shadow-warning: 0 4px 14px 0 rgba(245, 158, 11, 0.15);
$shadow-error: 0 4px 14px 0 rgba(239, 68, 68, 0.15);
```

---

## 🧩 Core Components

### 1. Buttons

#### Primary Button
```scss
.btn-primary {
  // Layout
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: $space-3 $space-6;
  min-height: 48px; // Touch-friendly
  
  // Typography
  font-family: $font-primary;
  font-size: $text-base;
  font-weight: $font-semibold;
  line-height: $leading-tight;
  text-decoration: none;
  
  // Visual
  background: $primary-blue;
  color: $white;
  border: none;
  border-radius: $radius-button;
  box-shadow: $shadow-sm;
  
  // Interaction
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  user-select: none;
  
  // States
  &:hover {
    background: darken($primary-blue, 8%);
    box-shadow: $shadow-md;
    transform: translateY(-1px);
  }
  
  &:active {
    background: darken($primary-blue, 12%);
    box-shadow: $shadow-xs;
    transform: translateY(0);
  }
  
  &:focus {
    outline: none;
    box-shadow: 0 0 0 3px rgba(30, 64, 175, 0.2);
  }
  
  &:disabled {
    background: $gray-300;
    color: $gray-500;
    cursor: not-allowed;
    box-shadow: none;
    transform: none;
  }
  
  // Loading state
  &.loading {
    position: relative;
    color: transparent;
    
    &::after {
      content: '';
      position: absolute;
      width: 20px;
      height: 20px;
      border: 2px solid transparent;
      border-top: 2px solid currentColor;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }
  }
}

// Size variants
.btn-sm {
  padding: $space-2 $space-4;
  min-height: 36px;
  font-size: $text-sm;
}

.btn-lg {
  padding: $space-4 $space-8;
  min-height: 56px;
  font-size: $text-lg;
}

// Style variants
.btn-secondary {
  background: transparent;
  color: $primary-blue;
  border: 2px solid $primary-blue;
  
  &:hover {
    background: $primary-blue;
    color: $white;
  }
}

.btn-success {
  background: $success;
  
  &:hover {
    background: darken($success, 8%);
  }
}

.btn-danger {
  background: $error;
  
  &:hover {
    background: darken($error, 8%);
  }
}

.btn-ghost {
  background: transparent;
  color: $gray-600;
  box-shadow: none;
  
  &:hover {
    background: $gray-100;
  }
}
```

#### React Component Example
```jsx
// Button.jsx
import React from 'react';
import classNames from 'classnames';

const Button = ({
  children,
  variant = 'primary',
  size = 'md',
  loading = false,
  disabled = false,
  onClick,
  type = 'button',
  className,
  ...props
}) => {
  const buttonClasses = classNames(
    'btn',
    `btn-${variant}`,
    `btn-${size}`,
    {
      'loading': loading,
    },
    className
  );

  return (
    <button
      type={type}
      className={buttonClasses}
      onClick={onClick}
      disabled={disabled || loading}
      {...props}
    >
      {children}
    </button>
  );
};

// Usage Examples
<Button variant="primary">Earn Points</Button>
<Button variant="secondary" size="sm">Cancel</Button>
<Button variant="success" loading>Redeeming...</Button>
<Button variant="ghost" disabled>Out of Stock</Button>
```

### 2. Form Inputs

#### Text Input
```scss
.input {
  // Layout
  display: block;
  width: 100%;
  padding: $space-3 $space-4;
  min-height: 48px;
  
  // Typography
  font-family: $font-primary;
  font-size: 16px; // Prevent zoom on iOS
  font-weight: $font-normal;
  line-height: $leading-normal;
  
  // Visual
  background: $white;
  color: $gray-700;
  border: 2px solid $gray-200;
  border-radius: $radius-input;
  box-shadow: $shadow-xs;
  
  // Interaction
  transition: all 0.2s ease-in-out;
  
  // Placeholder
  &::placeholder {
    color: $gray-400;
    font-weight: $font-normal;
  }
  
  // States
  &:focus {
    outline: none;
    border-color: $primary-blue;
    box-shadow: 0 0 0 3px rgba(30, 64, 175, 0.1);
  }
  
  &:invalid {
    border-color: $error;
    box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
  }
  
  &:disabled {
    background: $gray-100;
    color: $gray-500;
    cursor: not-allowed;
  }
  
  // With icon
  &.input-with-icon {
    padding-left: $space-12;
    
    + .input-icon {
      position: absolute;
      left: $space-4;
      top: 50%;
      transform: translateY(-50%);
      color: $gray-400;
      pointer-events: none;
    }
  }
}

// Input Group
.input-group {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: $space-2;
}

.input-label {
  font-size: $text-sm;
  font-weight: $font-medium;
  color: $gray-700;
  margin-bottom: $space-1;
}

.input-error {
  font-size: $text-sm;
  color: $error;
  margin-top: $space-1;
  
  &::before {
    content: "⚠ ";
  }
}

.input-help {
  font-size: $text-xs;
  color: $gray-500;
  margin-top: $space-1;
}
```

#### React Input Component
```jsx
// Input.jsx
import React, { useState } from 'react';
import classNames from 'classnames';

const Input = ({
  label,
  type = 'text',
  placeholder,
  value,
  onChange,
  error,
  helpText,
  icon,
  disabled = false,
  required = false,
  className,
  ...props
}) => {
  const [focused, setFocused] = useState(false);

  const inputClasses = classNames(
    'input',
    {
      'input-with-icon': icon,
      'input-error': error,
    },
    className
  );

  return (
    <div className="input-group">
      {label && (
        <label className="input-label">
          {label}
          {required && <span className="text-error">*</span>}
        </label>
      )}
      
      <div className="relative">
        <input
          type={type}
          className={inputClasses}
          placeholder={placeholder}
          value={value}
          onChange={onChange}
          onFocus={() => setFocused(true)}
          onBlur={() => setFocused(false)}
          disabled={disabled}
          required={required}
          {...props}
        />
        
        {icon && (
          <div className="input-icon">
            {icon}
          </div>
        )}
      </div>
      
      {error && (
        <div className="input-error">
          {error}
        </div>
      )}
      
      {helpText && !error && (
        <div className="input-help">
          {helpText}
        </div>
      )}
    </div>
  );
};

// Usage Examples
<Input 
  label="Email Address"
  type="email"
  placeholder="Enter your email"
  icon={<EmailIcon />}
  required
/>

<Input 
  label="Phone Number"
  type="tel"
  error="Please enter a valid phone number"
/>
```

### 3. Cards

#### Base Card Component
```scss
.card {
  // Layout
  display: block;
  padding: $space-6;
  
  // Visual
  background: $white;
  border: 1px solid $gray-200;
  border-radius: $radius-card;
  box-shadow: $shadow-sm;
  
  // Interaction
  transition: all 0.2s ease-in-out;
  
  // Hover state (for interactive cards)
  &.card-interactive {
    cursor: pointer;
    
    &:hover {
      box-shadow: $shadow-md;
      transform: translateY(-2px);
      border-color: $gray-300;
    }
  }
  
  // Card variants
  &.card-flat {
    box-shadow: none;
    border: 1px solid $gray-200;
  }
  
  &.card-elevated {
    box-shadow: $shadow-lg;
    border: none;
  }
  
  // Size variants
  &.card-sm {
    padding: $space-4;
  }
  
  &.card-lg {
    padding: $space-8;
  }
}

// Card Header/Footer
.card-header {
  margin-bottom: $space-4;
  padding-bottom: $space-4;
  border-bottom: 1px solid $gray-200;
}

.card-footer {
  margin-top: $space-4;
  padding-top: $space-4;
  border-top: 1px solid $gray-200;
}

// Special Cards
.points-card {
  background: linear-gradient(135deg, $primary-blue 0%, darken($primary-blue, 10%) 100%);
  color: $white;
  border: none;
  text-align: center;
  
  .points-amount {
    font-size: $text-4xl;
    font-weight: $font-bold;
    margin: $space-2 0;
  }
  
  .points-label {
    font-size: $text-sm;
    opacity: 0.9;
  }
}

.reward-card {
  position: relative;
  overflow: hidden;
  
  .reward-image {
    width: 100%;
    height: 120px;
    object-fit: cover;
    border-radius: $radius-md;
    margin-bottom: $space-3;
  }
  
  .reward-title {
    font-size: $text-lg;
    font-weight: $font-semibold;
    color: $gray-800;
    margin-bottom: $space-1;
  }
  
  .reward-points {
    font-size: $text-xl;
    font-weight: $font-bold;
    color: $primary-gold;
    margin: $space-2 0;
  }
  
  .reward-status {
    position: absolute;
    top: $space-3;
    right: $space-3;
    padding: $space-1 $space-2;
    background: $success;
    color: $white;
    font-size: $text-xs;
    font-weight: $font-medium;
    border-radius: $radius-badge;
  }
}
```

#### React Card Component
```jsx
// Card.jsx
import React from 'react';
import classNames from 'classnames';

const Card = ({
  children,
  variant = 'default',
  size = 'md',
  interactive = false,
  className,
  onClick,
  ...props
}) => {
  const cardClasses = classNames(
    'card',
    `card-${size}`,
    {
      'card-interactive': interactive,
      'card-flat': variant === 'flat',
      'card-elevated': variant === 'elevated',
    },
    className
  );

  return (
    <div
      className={cardClasses}
      onClick={onClick}
      {...props}
    >
      {children}
    </div>
  );
};

// Specialized Card Components
const PointsCard = ({ points, label, children }) => (
  <Card className="points-card">
    <div className="points-label">{label}</div>
    <div className="points-amount">{points?.toLocaleString()}</div>
    {children}
  </Card>
);

const RewardCard = ({ 
  title, 
  points, 
  image, 
  status, 
  available, 
  onClick 
}) => (
  <Card 
    className="reward-card" 
    interactive 
    onClick={onClick}
  >
    {status && <div className="reward-status">{status}</div>}
    {image && (
      <img 
        src={image} 
        alt={title} 
        className="reward-image" 
      />
    )}
    <h3 className="reward-title">{title}</h3>
    <div className="reward-points">{points} points</div>
    <div className={`reward-availability ${available ? 'available' : 'unavailable'}`}>
      {available ? 'Available' : 'Out of Stock'}
    </div>
  </Card>
);

// Usage Examples
<Card>
  <h2>Welcome!</h2>
  <p>Start earning points today</p>
</Card>

<PointsCard 
  points={2575} 
  label="Your Points"
>
  <Button>Redeem Now</Button>
</PointsCard>

<RewardCard
  title="Coffee Voucher"
  points={500}
  image="/images/coffee.jpg"
  status="Popular"
  available={true}
  onClick={() => handleRewardClick()}
/>
```

### 4. Navigation Components

#### Bottom Navigation (Mobile)
```scss
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  
  // Layout
  display: flex;
  padding: $space-2 0;
  min-height: 60px;
  
  // Visual
  background: $white;
  border-top: 1px solid $gray-200;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.08);
  
  // Safe area for iPhone X+
  padding-bottom: max(#{$space-2}, env(safe-area-inset-bottom));
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $space-2;
  text-decoration: none;
  color: $gray-500;
  font-size: $text-xs;
  font-weight: $font-medium;
  transition: color 0.2s ease-in-out;
  
  .nav-icon {
    font-size: 24px;
    margin-bottom: $space-1;
    transition: transform 0.2s ease-in-out;
  }
  
  .nav-label {
    font-size: $text-xs;
    line-height: 1;
  }
  
  // Active state
  &.active {
    color: $primary-blue;
    
    .nav-icon {
      transform: scale(1.1);
    }
  }
  
  // Badge for notifications
  .nav-badge {
    position: absolute;
    top: $space-1;
    right: 50%;
    transform: translateX(50%);
    
    min-width: 16px;
    height: 16px;
    padding: 0 4px;
    
    background: $error;
    color: $white;
    font-size: 10px;
    font-weight: $font-bold;
    line-height: 16px;
    text-align: center;
    border-radius: $radius-full;
  }
}
```

#### React Bottom Navigation
```jsx
// BottomNavigation.jsx
import React from 'react';
import { useLocation, Link } from 'react-router-dom';
import classNames from 'classnames';

const BottomNavigation = () => {
  const location = useLocation();
  
  const navItems = [
    {
      path: '/',
      icon: '🏠',
      label: 'Home',
      badge: null
    },
    {
      path: '/earn',
      icon: '💰',
      label: 'Earn',
      badge: null
    },
    {
      path: '/rewards',
      icon: '🎁',
      label: 'Rewards',
      badge: null
    },
    {
      path: '/profile',
      icon: '👤',
      label: 'Profile',
      badge: null
    },
    {
      path: '/settings',
      icon: '⚙️',
      label: 'Settings',
      badge: null
    }
  ];

  return (
    <nav className="bottom-nav">
      {navItems.map((item) => {
        const isActive = location.pathname === item.path;
        
        return (
          <Link
            key={item.path}
            to={item.path}
            className={classNames('nav-item', { active: isActive })}
          >
            <div className="nav-icon">
              {item.icon}
              {item.badge && (
                <span className="nav-badge">{item.badge}</span>
              )}
            </div>
            <span className="nav-label">{item.label}</span>
          </Link>
        );
      })}
    </nav>
  );
};
```

### 5. Modal & Overlay Components

#### Modal Base
```scss
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $space-4;
  
  background: $black-alpha-50;
  backdrop-filter: blur(4px);
  
  // Animation
  opacity: 0;
  animation: fadeIn 0.2s ease-out forwards;
  
  @keyframes fadeIn {
    to { opacity: 1; }
  }
}

.modal {
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
  width: 100%;
  
  background: $white;
  border-radius: $radius-2xl;
  box-shadow: $shadow-xl;
  
  // Animation
  transform: scale(0.95);
  animation: modalSlideIn 0.2s ease-out forwards;
  
  @keyframes modalSlideIn {
    to {
      transform: scale(1);
    }
  }
  
  // Responsive sizing
  @media (min-width: 640px) {
    width: auto;
    min-width: 400px;
  }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $space-6 $space-6 0;
  margin-bottom: $space-4;
}

.modal-title {
  font-size: $text-xl;
  font-weight: $font-semibold;
  color: $gray-800;
  margin: 0;
}

.modal-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  
  background: transparent;
  border: none;
  border-radius: $radius-full;
  color: $gray-400;
  cursor: pointer;
  
  transition: all 0.2s ease-in-out;
  
  &:hover {
    background: $gray-100;
    color: $gray-600;
  }
}

.modal-body {
  padding: 0 $space-6;
  margin-bottom: $space-6;
  overflow-y: auto;
}

.modal-footer {
  display: flex;
  gap: $space-3;
  padding: 0 $space-6 $space-6;
  justify-content: flex-end;
}
```

#### React Modal Component
```jsx
// Modal.jsx
import React, { useEffect } from 'react';
import { createPortal } from 'react-dom';

const Modal = ({
  isOpen,
  onClose,
  title,
  children,
  footer,
  size = 'md',
  closeOnOverlayClick = true
}) => {
  // Handle ESC key
  useEffect(() => {
    const handleEsc = (e) => {
      if (e.keyCode === 27) onClose();
    };
    
    if (isOpen) {
      document.addEventListener('keydown', handleEsc);
      document.body.style.overflow = 'hidden';
    }
    
    return () => {
      document.removeEventListener('keydown', handleEsc);
      document.body.style.overflow = 'unset';
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return createPortal(
    <div 
      className="modal-overlay"
      onClick={closeOnOverlayClick ? onClose : undefined}
    >
      <div 
        className={`modal modal-${size}`}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="modal-header">
          <h2 className="modal-title">{title}</h2>
          <button 
            className="modal-close"
            onClick={onClose}
            aria-label="Close modal"
          >
            ✕
          </button>
        </div>
        
        <div className="modal-body">
          {children}
        </div>
        
        {footer && (
          <div className="modal-footer">
            {footer}
          </div>
        )}
      </div>
    </div>,
    document.body
  );
};

// Confirmation Modal
const ConfirmModal = ({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  confirmText = "Confirm",
  cancelText = "Cancel",
  variant = "primary"
}) => (
  <Modal
    isOpen={isOpen}
    onClose={onClose}
    title={title}
    footer={
      <>
        <Button variant="ghost" onClick={onClose}>
          {cancelText}
        </Button>
        <Button variant={variant} onClick={onConfirm}>
          {confirmText}
        </Button>
      </>
    }
  >
    <p>{message}</p>
  </Modal>
);

// Usage Examples
<Modal
  isOpen={showModal}
  onClose={() => setShowModal(false)}
  title="Reward Details"
>
  <RewardDetails reward={selectedReward} />
</Modal>

<ConfirmModal
  isOpen={showConfirm}
  onClose={() => setShowConfirm(false)}
  onConfirm={handleRedeem}
  title="Redeem Reward"
  message="Are you sure you want to redeem this reward for 500 points?"
  confirmText="Yes, Redeem"
  variant="success"
/>
```

---

## 📊 Data Display Components

### 1. Progress Bars

#### Progress Component
```scss
.progress {
  width: 100%;
  height: 8px;
  background: $gray-200;
  border-radius: $radius-full;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, $primary-blue, lighten($primary-blue, 10%));
  border-radius: $radius-full;
  transition: width 0.6s ease-in-out;
  position: relative;
  
  // Animated shine effect
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(
      90deg,
      transparent,
      rgba(255, 255, 255, 0.4),
      transparent
    );
    animation: shine 2s infinite;
  }
  
  @keyframes shine {
    0% { left: -100%; }
    100% { left: 100%; }
  }
}

// Size variants
.progress-sm {
  height: 4px;
}

.progress-lg {
  height: 12px;
}

// Color variants
.progress-success .progress-bar {
  background: linear-gradient(90deg, $success, lighten($success, 10%));
}

.progress-warning .progress-bar {
  background: linear-gradient(90deg, $warning, lighten($warning, 10%));
}

.progress-error .progress-bar {
  background: linear-gradient(90deg, $error, lighten($error, 10%));
}
```

#### React Progress Component
```jsx
// Progress.jsx
import React from 'react';
import classNames from 'classnames';

const Progress = ({
  value,
  max = 100,
  size = 'md',
  variant = 'primary',
  animated = false,
  showLabel = false,
  label,
  className
}) => {
  const percentage = Math.min(100, Math.max(0, (value / max) * 100));
  
  const progressClasses = classNames(
    'progress',
    `progress-${size}`,
    `progress-${variant}`,
    className
  );
  
  const barClasses = classNames(
    'progress-bar',
    {
      'progress-animated': animated
    }
  );

  return (
    <div className="progress-container">
      {(showLabel || label) && (
        <div className="progress-label">
          {label || `${Math.round(percentage)}%`}
        </div>
      )}
      <div className={progressClasses}>
        <div 
          className={barClasses}
          style={{ width: `${percentage}%` }}
          role="progressbar"
          aria-valuenow={value}
          aria-valuemin={0}
          aria-valuemax={max}
        />
      </div>
    </div>
  );
};

// Tier Progress Component
const TierProgress = ({ 
  currentPoints, 
  nextTierPoints, 
  currentTier, 
  nextTier 
}) => {
  const progress = (currentPoints / nextTierPoints) * 100;
  const remaining = nextTierPoints - currentPoints;

  return (
    <div className="tier-progress">
      <div className="tier-progress-header">
        <span className="current-tier">{currentTier}</span>
        <span className="next-tier">{nextTier}</span>
      </div>
      
      <Progress 
        value={currentPoints} 
        max={nextTierPoints}
        size="lg"
        variant="primary"
        animated
      />
      
      <div className="tier-progress-footer">
        <span>{remaining.toLocaleString()} points to {nextTier}</span>
        <span>{Math.round(progress)}%</span>
      </div>
    </div>
  );
};

// Usage Examples
<Progress value={75} showLabel />

<TierProgress
  currentPoints={2575}
  nextTierPoints={3000}
  currentTier="Silver"
  nextTier="Gold"
/>
```

### 2. Badges & Status Indicators

#### Badge Component
```scss
.badge {
  display: inline-flex;
  align-items: center;
  padding: $space-1 $space-3;
  font-size: $text-xs;
  font-weight: $font-semibold;
  line-height: 1;
  border-radius: $radius-badge;
  text-transform: uppercase;
  letter-spacing: 0.025em;
  
  // Default style
  background: $gray-100;
  color: $gray-600;
  
  // Size variants
  &.badge-sm {
    padding: 2px $space-2;
    font-size: 10px;
  }
  
  &.badge-lg {
    padding: $space-2 $space-4;
    font-size: $text-sm;
  }
  
  // Color variants
  &.badge-primary {
    background: $primary-blue;
    color: $white;
  }
  
  &.badge-success {
    background: $success;
    color: $white;
  }
  
  &.badge-warning {
    background: $warning;
    color: $white;
  }
  
  &.badge-error {
    background: $error;
    color: $white;
  }
  
  &.badge-bronze {
    background: $tier-bronze;
    color: $white;
  }
  
  &.badge-silver {
    background: $tier-silver;
    color: $gray-800;
  }
  
  &.badge-gold {
    background: $tier-gold;
    color: $gray-800;
  }
  
  &.badge-platinum {
    background: $tier-platinum;
    color: $gray-800;
  }
  
  // With icon
  .badge-icon {
    margin-right: $space-1;
    font-size: 1em;
  }
  
  // Dot indicator
  .badge-dot {
    width: 6px;
    height: 6px;
    background: currentColor;
    border-radius: 50%;
    margin-right: $space-2;
  }
}

// Notification badge (for counters)
.notification-badge {
  position: relative;
  
  &::after {
    content: attr(data-count);
    position: absolute;
    top: -8px;
    right: -8px;
    
    min-width: 16px;
    height: 16px;
    padding: 0 4px;
    
    background: $error;
    color: $white;
    font-size: 10px;
    font-weight: $font-bold;
    line-height: 16px;
    text-align: center;
    border-radius: $radius-full;
    
    // Hide if count is 0
    &[data-count="0"],
    &[data-count=""] {
      display: none;
    }
  }
}
```

#### React Badge Component
```jsx
// Badge.jsx
import React from 'react';
import classNames from 'classnames';

const Badge = ({
  children,
  variant = 'default',
  size = 'md',
  icon,
  dot = false,
  className
}) => {
  const badgeClasses = classNames(
    'badge',
    `badge-${variant}`,
    `badge-${size}`,
    className
  );

  return (
    <span className={badgeClasses}>
      {dot && <span className="badge-dot" />}
      {icon && <span className="badge-icon">{icon}</span>}
      {children}
    </span>
  );
};

// Tier Badge Component
const TierBadge = ({ tier }) => {
  const tierConfig = {
    bronze: { variant: 'bronze', icon: '🥉' },
    silver: { variant: 'silver', icon: '🥈' },
    gold: { variant: 'gold', icon: '🥇' },
    platinum: { variant: 'platinum', icon: '💎' }
  };

  const config = tierConfig[tier.toLowerCase()] || tierConfig.bronze;

  return (
    <Badge 
      variant={config.variant} 
      icon={config.icon}
      size="lg"
    >
      {tier}
    </Badge>
  );
};

// Status Badge Component
const StatusBadge = ({ status }) => {
  const statusConfig = {
    active: { variant: 'success', text: 'Active', dot: true },
    inactive: { variant: 'error', text: 'Inactive', dot: true },
    pending: { variant: 'warning', text: 'Pending', dot: true },
    expired: { variant: 'default', text: 'Expired', dot: true }
  };

  const config = statusConfig[status] || statusConfig.active;

  return (
    <Badge 
      variant={config.variant}
      dot={config.dot}
      size="sm"
    >
      {config.text}
    </Badge>
  );
};

// Usage Examples
<Badge variant="primary">New</Badge>
<Badge variant="success" icon="✓">Completed</Badge>
<Badge dot variant="warning">Pending</Badge>

<TierBadge tier="gold" />
<StatusBadge status="active" />
```

---

## 📱 Mobile-Specific Components

### 1. Pull-to-Refresh
```scss
.pull-to-refresh {
  position: relative;
  overflow: hidden;
  
  .pull-indicator {
    position: absolute;
    top: -60px;
    left: 50%;
    transform: translateX(-50%);
    
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    
    background: $white;
    border-radius: $radius-full;
    box-shadow: $shadow-md;
    
    transition: all 0.3s ease-out;
    
    &.pulling {
      top: 20px;
    }
    
    &.refreshing {
      top: 20px;
      animation: spin 1s linear infinite;
    }
  }
}
```

### 2. Touch-Friendly Lists
```scss
.touch-list {
  .list-item {
    display: flex;
    align-items: center;
    padding: $space-4;
    min-height: 56px; // Touch-friendly height
    
    border-bottom: 1px solid $gray-100;
    
    // Touch feedback
    &:active {
      background: $gray-50;
    }
    
    .item-avatar {
      width: 40px;
      height: 40px;
      margin-right: $space-4;
      border-radius: $radius-full;
      flex-shrink: 0;
    }
    
    .item-content {
      flex: 1;
      min-width: 0; // Allow text truncation
      
      .item-title {
        font-weight: $font-medium;
        color: $gray-800;
        margin-bottom: $space-1;
        
        // Truncate long text
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      
      .item-subtitle {
        font-size: $text-sm;
        color: $gray-500;
        
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
    
    .item-action {
      margin-left: $space-3;
      flex-shrink: 0;
    }
  }
}
```

---

## 🎯 Implementation Guidelines

### CSS Architecture
```scss
// Use BEM methodology for class naming
.component {}
.component__element {}
.component--modifier {}

// Example:
.card {}                    // Block
.card__header {}           // Element
.card--elevated {}         // Modifier
```

### Responsive Design Approach
```scss
// Mobile-first breakpoints
$mobile: 320px;
$tablet: 768px;
$desktop: 1024px;
$wide: 1200px;

// Media query mixins
@mixin mobile-up {
  @media (min-width: $mobile) { @content; }
}

@mixin tablet-up {
  @media (min-width: $tablet) { @content; }
}

@mixin desktop-up {
  @media (min-width: $desktop) { @content; }
}

// Example usage
.component {
  // Mobile styles (default)
  padding: $space-4;
  
  @include tablet-up {
    padding: $space-6;
  }
  
  @include desktop-up {
    padding: $space-8;
  }
}
```

### Accessibility Guidelines
```scss
// Focus styles for keyboard navigation
*:focus {
  outline: 2px solid $primary-blue;
  outline-offset: 2px;
}

// High contrast support
@media (prefers-contrast: high) {
  .card {
    border: 2px solid $gray-800;
  }
}

// Reduced motion support
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}
```

### Dark Mode Support (Future)
```scss
// CSS Custom Properties for theming
:root {
  --bg-primary: #{$white};
  --text-primary: #{$gray-900};
  --border-color: #{$gray-200};
}

[data-theme="dark"] {
  --bg-primary: #{$gray-900};
  --text-primary: #{$white};
  --border-color: #{$gray-700};
}

// Component using theme variables
.card {
  background: var(--bg-primary);
  color: var(--text-primary);
  border-color: var(--border-color);
}
```

---

## 📋 Component Checklist

### Before Implementation
- [ ] Design tokens defined and documented
- [ ] Component specifications reviewed with design team
- [ ] Accessibility requirements understood
- [ ] Responsive behavior defined for all breakpoints
- [ ] Animation and interaction specifications documented

### During Development
- [ ] Components built with mobile-first approach
- [ ] Proper semantic HTML structure used
- [ ] ARIA attributes implemented where needed
- [ ] Touch-friendly sizing (44px minimum) maintained
- [ ] Cross-browser compatibility tested

### Before Delivery
- [ ] Component documentation updated
- [ ] Storybook stories created for all variants
- [ ] Accessibility audit completed (WCAG 2.1 AA)
- [ ] Performance impact assessed
- [ ] Design review and approval obtained

---

**📍 Document Status:** Ready for Frontend Implementation  
**🔄 Version:** 1.0 - Complete Component Library  
**👤 Owner:** UI/UX Designer + Frontend Team Lead  
**📅 Next Review:** After Phase 1 Component Implementation