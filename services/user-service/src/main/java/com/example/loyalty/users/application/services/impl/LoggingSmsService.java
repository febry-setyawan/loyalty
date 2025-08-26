package com.example.loyalty.users.application.services.impl;

import com.example.loyalty.users.application.services.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Logging-based SMS service implementation
 * Logs SMS messages instead of sending them - used for development/testing
 */
@Service
@ConditionalOnProperty(name = "app.sms.provider", havingValue = "log", matchIfMissing = true)
public class LoggingSmsService implements SmsService {

  private static final Logger logger = LoggerFactory.getLogger(LoggingSmsService.class);

  @Override
  public void sendVerificationSMS(String phoneNumber, String firstName, String verificationToken) {
    logger.info("=== SMS SIMULATION ===");
    logger.info("To: {}", phoneNumber);
    logger.info("Message: Hi {}! Your Loyalty System verification code is: {}. Don't share this code with anyone.", 
        firstName != null ? firstName : "there", 
        verificationToken);
    logger.info("======================");
  }

  @Override
  public void sendPasswordResetSMS(String phoneNumber, String firstName, String resetToken) {
    logger.info("=== SMS SIMULATION ===");
    logger.info("To: {}", phoneNumber);
    logger.info("Message: Hi {}! Your Loyalty System password reset code is: {}. This code expires in 15 minutes.", 
        firstName != null ? firstName : "there", 
        resetToken);
    logger.info("======================");
  }
}