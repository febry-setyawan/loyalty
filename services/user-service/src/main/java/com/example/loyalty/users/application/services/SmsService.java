package com.example.loyalty.users.application.services;

/**
 * SMS service interface for sending various types of SMS messages
 */
public interface SmsService {
  
  /**
   * Send verification SMS to new user
   */
  void sendVerificationSMS(String phoneNumber, String firstName, String verificationToken);
  
  /**
   * Send password reset SMS
   */
  void sendPasswordResetSMS(String phoneNumber, String firstName, String resetToken);
}