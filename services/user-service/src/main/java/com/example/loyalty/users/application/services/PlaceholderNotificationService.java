package com.example.loyalty.users.application.services;

import com.example.loyalty.users.application.services.EmailService;
import com.example.loyalty.users.application.services.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Notification service implementation that delegates to email and SMS services
 * This service orchestrates email and SMS notifications for user events
 */
@Service
public class PlaceholderNotificationService implements NotificationService {
  
  private static final Logger logger = LoggerFactory.getLogger(PlaceholderNotificationService.class);
  
  private final EmailService emailService;
  private final SmsService smsService;
  
  public PlaceholderNotificationService(EmailService emailService, SmsService smsService) {
    this.emailService = emailService;
    this.smsService = smsService;
  }
  
  @Override
  public void sendVerificationEmail(String email, String firstName, String verificationToken) {
    logger.info("Sending verification email to: {}", email);
    emailService.sendVerificationEmail(email, firstName, verificationToken);
  }
  
  @Override
  public void sendVerificationSMS(String phoneNumber, String firstName, String verificationToken) {
    logger.info("Sending verification SMS to: {}", phoneNumber);
    smsService.sendVerificationSMS(phoneNumber, firstName, verificationToken);
  }
  
  @Override
  public void sendPasswordResetEmail(String email, String firstName, String resetToken) {
    logger.info("Sending password reset email to: {}", email);
    emailService.sendPasswordResetEmail(email, firstName, resetToken);
  }
  
  @Override
  public void sendPasswordResetSMS(String phoneNumber, String firstName, String resetToken) {
    logger.info("Sending password reset SMS to: {}", phoneNumber);
    smsService.sendPasswordResetSMS(phoneNumber, firstName, resetToken);
  }
  
  @Override
  public void sendWelcomeEmail(String email, String firstName) {
    logger.info("Sending welcome email to: {}", email);
    emailService.sendWelcomeEmail(email, firstName);
  }
}