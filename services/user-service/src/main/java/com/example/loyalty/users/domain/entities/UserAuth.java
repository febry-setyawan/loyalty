package com.example.loyalty.users.domain.entities;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User authentication entity containing authentication-related data
 * Separated from User entity for security and performance reasons
 */
@Entity
@Table(name = "user_auth")
@EntityListeners(AuditingEntityListener.class)
public class UserAuth {

  @Id
  @Column(name = "user_id")
  private UUID userId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @MapsId
  private User user;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "last_login")
  private LocalDateTime lastLogin;

  @Column(name = "failed_login_attempts", nullable = false)
  private Integer failedLoginAttempts = 0;

  @Column(name = "account_locked_until")
  private LocalDateTime accountLockedUntil;

  @Column(name = "password_changed_at", nullable = false)
  private LocalDateTime passwordChangedAt;

  @Column(name = "verification_token")
  private String verificationToken;

  @Column(name = "verification_token_expires_at")
  private LocalDateTime verificationTokenExpiresAt;

  @Column(name = "password_reset_token")
  private String passwordResetToken;

  @Column(name = "password_reset_token_expires_at")
  private LocalDateTime passwordResetTokenExpiresAt;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  // Default constructor for JPA
  protected UserAuth() {}

  // Constructor
  public UserAuth(User user, String passwordHash) {
    this.user = user;
    this.userId = user.getId();
    this.passwordHash = passwordHash;
    this.passwordChangedAt = LocalDateTime.now();
  }

  // Business methods
  public boolean isAccountLocked() {
    return accountLockedUntil != null && accountLockedUntil.isAfter(LocalDateTime.now());
  }

  public void recordFailedLogin() {
    this.failedLoginAttempts++;
    if (this.failedLoginAttempts >= 5) {
      lockAccount(30); // Lock for 30 minutes
    }
  }

  public void recordSuccessfulLogin() {
    this.failedLoginAttempts = 0;
    this.accountLockedUntil = null;
    this.lastLogin = LocalDateTime.now();
  }

  public void lockAccount(int lockDurationMinutes) {
    this.accountLockedUntil = LocalDateTime.now().plusMinutes(lockDurationMinutes);
  }

  public void unlockAccount() {
    this.accountLockedUntil = null;
    this.failedLoginAttempts = 0;
  }

  public void setVerificationToken(String token, int expirationHours) {
    this.verificationToken = token;
    this.verificationTokenExpiresAt = LocalDateTime.now().plusHours(expirationHours);
  }

  public boolean isVerificationTokenValid(String token) {
    return this.verificationToken != null 
        && this.verificationToken.equals(token)
        && this.verificationTokenExpiresAt != null
        && this.verificationTokenExpiresAt.isAfter(LocalDateTime.now());
  }

  public void clearVerificationToken() {
    this.verificationToken = null;
    this.verificationTokenExpiresAt = null;
  }

  public void setPasswordResetToken(String token, int expirationHours) {
    this.passwordResetToken = token;
    this.passwordResetTokenExpiresAt = LocalDateTime.now().plusHours(expirationHours);
  }

  public boolean isPasswordResetTokenValid(String token) {
    return this.passwordResetToken != null 
        && this.passwordResetToken.equals(token)
        && this.passwordResetTokenExpiresAt != null
        && this.passwordResetTokenExpiresAt.isAfter(LocalDateTime.now());
  }

  public void clearPasswordResetToken() {
    this.passwordResetToken = null;
    this.passwordResetTokenExpiresAt = null;
  }

  public void updatePassword(String newPasswordHash) {
    this.passwordHash = newPasswordHash;
    this.passwordChangedAt = LocalDateTime.now();
    clearPasswordResetToken();
  }

  // Getters and setters
  public UUID getUserId() {
    return userId;
  }

  public User getUser() {
    return user;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public LocalDateTime getLastLogin() {
    return lastLogin;
  }

  public Integer getFailedLoginAttempts() {
    return failedLoginAttempts;
  }

  public LocalDateTime getAccountLockedUntil() {
    return accountLockedUntil;
  }

  public LocalDateTime getPasswordChangedAt() {
    return passwordChangedAt;
  }

  public String getVerificationToken() {
    return verificationToken;
  }

  public LocalDateTime getVerificationTokenExpiresAt() {
    return verificationTokenExpiresAt;
  }

  public String getPasswordResetToken() {
    return passwordResetToken;
  }

  public LocalDateTime getPasswordResetTokenExpiresAt() {
    return passwordResetTokenExpiresAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}