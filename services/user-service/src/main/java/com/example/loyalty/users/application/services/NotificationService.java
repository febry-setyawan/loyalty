package com.example.loyalty.users.application.services;

/**
 * Notification service interface for sending emails and SMS
 * Implementation will be provided based on configured providers
 */
public interface NotificationService {
  
  void sendVerificationEmail(String email, String firstName, String verificationToken);
  
  void sendVerificationSMS(String phoneNumber, String firstName, String verificationToken);
  
  void sendPasswordResetEmail(String email, String firstName, String resetToken);
  
  void sendPasswordResetSMS(String phoneNumber, String firstName, String resetToken);
  
  void sendWelcomeEmail(String email, String firstName);
}