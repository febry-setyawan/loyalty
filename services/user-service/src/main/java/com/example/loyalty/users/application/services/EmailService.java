package com.example.loyalty.users.application.services;

/**
 * Email service interface for sending various types of emails
 */
public interface EmailService {
  
  /**
   * Send verification email to new user
   */
  void sendVerificationEmail(String email, String firstName, String verificationToken);
  
  /**
   * Send password reset email 
   */
  void sendPasswordResetEmail(String email, String firstName, String resetToken);
  
  /**
   * Send welcome email to verified user
   */
  void sendWelcomeEmail(String email, String firstName);
}