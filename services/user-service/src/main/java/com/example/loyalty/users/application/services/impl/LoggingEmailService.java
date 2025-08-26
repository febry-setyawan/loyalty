package com.example.loyalty.users.application.services.impl;

import com.example.loyalty.users.application.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Logging-based email service implementation
 * Used when SMTP is not configured - logs emails instead of sending them
 */
@Service
@ConditionalOnMissingBean(JavaMailSender.class)
public class LoggingEmailService implements EmailService {

  private static final Logger logger = LoggerFactory.getLogger(LoggingEmailService.class);

  @Override
  public void sendVerificationEmail(String email, String firstName, String verificationToken) {
    logger.info("=== EMAIL SIMULATION ===");
    logger.info("To: {}", email);
    logger.info("Subject: Welcome to Loyalty System - Verify Your Account");
    logger.info("Body: Hi {},\n\n" +
        "Welcome to Loyalty System! Please verify your account by using the verification code below:\n\n" +
        "Verification Code: {}\n\n" +
        "If you didn't create an account with us, please ignore this email.\n\n" +
        "Best regards,\n" +
        "Loyalty System Team", 
        firstName != null ? firstName : "there", 
        verificationToken);
    logger.info("========================");
  }

  @Override
  public void sendPasswordResetEmail(String email, String firstName, String resetToken) {
    logger.info("=== EMAIL SIMULATION ===");
    logger.info("To: {}", email);
    logger.info("Subject: Password Reset Request - Loyalty System");
    logger.info("Body: Hi {},\n\n" +
        "You requested to reset your password. Please use the reset code below:\n\n" +
        "Reset Code: {}\n\n" +
        "This code will expire in 15 minutes. If you didn't request this reset, please ignore this email.\n\n" +
        "Best regards,\n" +
        "Loyalty System Team", 
        firstName != null ? firstName : "there", 
        resetToken);
    logger.info("========================");
  }

  @Override
  public void sendWelcomeEmail(String email, String firstName) {
    logger.info("=== EMAIL SIMULATION ===");
    logger.info("To: {}", email);
    logger.info("Subject: Welcome to Loyalty System!");
    logger.info("Body: Hi {},\n\n" +
        "Welcome to Loyalty System! Your account has been successfully verified.\n\n" +
        "You can now start earning and redeeming loyalty points. Log in to your account to get started.\n\n" +
        "Thank you for joining us!\n\n" +
        "Best regards,\n" +
        "Loyalty System Team", 
        firstName != null ? firstName : "there");
    logger.info("========================");
  }
}