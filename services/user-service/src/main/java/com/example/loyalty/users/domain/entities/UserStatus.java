package com.example.loyalty.users.domain.entities;

/**
 * User status enumeration
 * Represents the current state of a user account
 */
public enum UserStatus {
  /** User account is active and can perform all operations */
  ACTIVE,
  
  /** User account is newly created but not yet verified */
  PENDING_VERIFICATION,
  
  /** User account has been temporarily suspended */
  SUSPENDED,
  
  /** User account has been deactivated (soft delete) */
  INACTIVE
}