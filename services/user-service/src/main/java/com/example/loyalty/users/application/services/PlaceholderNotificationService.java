package com.example.loyalty.users.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Placeholder notification service implementation
 * In production, this would integrate with email/SMS providers like AWS SES, Twilio, etc.
 */
@Service
public class PlaceholderNotificationService implements NotificationService {
  
  private static final Logger logger = LoggerFactory.getLogger(PlaceholderNotificationService.class);
  
  @Override
  public void sendVerificationEmail(String email, String firstName, String verificationToken) {
    logger.info("Sending verification email to: {} with token: {}", email, verificationToken);
    // TODO: Integrate with email provider (AWS SES, SendGrid, etc.)
    // For now, just log the action for demo purposes
  }
  
  @Override
  public void sendVerificationSMS(String phoneNumber, String firstName, String verificationToken) {
    logger.info("Sending verification SMS to: {} with token: {}", phoneNumber, verificationToken);
    // TODO: Integrate with SMS provider (Twilio, AWS SNS, etc.)
    // For now, just log the action for demo purposes
  }
  
  @Override
  public void sendPasswordResetEmail(String email, String firstName, String resetToken) {
    logger.info("Sending password reset email to: {} with token: {}", email, resetToken);
    // TODO: Integrate with email provider
  }
  
  @Override
  public void sendPasswordResetSMS(String phoneNumber, String firstName, String resetToken) {
    logger.info("Sending password reset SMS to: {} with token: {}", phoneNumber, resetToken);
    // TODO: Integrate with SMS provider
  }
  
  @Override
  public void sendWelcomeEmail(String email, String firstName) {
    logger.info("Sending welcome email to: {}", email);
    // TODO: Integrate with email provider
  }
}