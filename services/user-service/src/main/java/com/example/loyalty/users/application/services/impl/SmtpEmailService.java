package com.example.loyalty.users.application.services.impl;

import com.example.loyalty.users.application.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * SMTP-based email service implementation
 * Uses Spring Boot's JavaMailSender for sending emails
 */
@Service
@ConditionalOnProperty(name = "app.email.smtp.host")
public class SmtpEmailService implements EmailService {

  private static final Logger logger = LoggerFactory.getLogger(SmtpEmailService.class);

  private final JavaMailSender mailSender;
  
  @Value("${app.email.smtp.from}")
  private String fromEmail;
  
  @Value("${app.email.smtp.from-name:Loyalty System}")
  private String fromName;

  public SmtpEmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Override
  public void sendVerificationEmail(String email, String firstName, String verificationToken) {
    logger.info("Sending verification email to: {}", email);
    
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setFrom(fromEmail);
    message.setSubject("Welcome to Loyalty System - Verify Your Account");
    
    String emailBody = String.format(
        "Hi %s,\n\n" +
        "Welcome to Loyalty System! Please verify your account by using the verification code below:\n\n" +
        "Verification Code: %s\n\n" +
        "If you didn't create an account with us, please ignore this email.\n\n" +
        "Best regards,\n" +
        "%s Team",
        firstName != null ? firstName : "there",
        verificationToken,
        fromName
    );
    
    message.setText(emailBody);
    
    try {
      mailSender.send(message);
      logger.info("Verification email sent successfully to: {}", email);
    } catch (Exception e) {
      logger.error("Failed to send verification email to: {}", email, e);
      throw new RuntimeException("Failed to send verification email", e);
    }
  }

  @Override
  public void sendPasswordResetEmail(String email, String firstName, String resetToken) {
    logger.info("Sending password reset email to: {}", email);
    
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setFrom(fromEmail);
    message.setSubject("Password Reset Request - Loyalty System");
    
    String emailBody = String.format(
        "Hi %s,\n\n" +
        "You requested to reset your password. Please use the reset code below:\n\n" +
        "Reset Code: %s\n\n" +
        "This code will expire in 15 minutes. If you didn't request this reset, please ignore this email.\n\n" +
        "Best regards,\n" +
        "%s Team",
        firstName != null ? firstName : "there",
        resetToken,
        fromName
    );
    
    message.setText(emailBody);
    
    try {
      mailSender.send(message);
      logger.info("Password reset email sent successfully to: {}", email);
    } catch (Exception e) {
      logger.error("Failed to send password reset email to: {}", email, e);
      throw new RuntimeException("Failed to send password reset email", e);
    }
  }

  @Override
  public void sendWelcomeEmail(String email, String firstName) {
    logger.info("Sending welcome email to: {}", email);
    
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setFrom(fromEmail);
    message.setSubject("Welcome to Loyalty System!");
    
    String emailBody = String.format(
        "Hi %s,\n\n" +
        "Welcome to Loyalty System! Your account has been successfully verified.\n\n" +
        "You can now start earning and redeeming loyalty points. Log in to your account to get started.\n\n" +
        "Thank you for joining us!\n\n" +
        "Best regards,\n" +
        "%s Team",
        firstName != null ? firstName : "there",
        fromName
    );
    
    message.setText(emailBody);
    
    try {
      mailSender.send(message);
      logger.info("Welcome email sent successfully to: {}", email);
    } catch (Exception e) {
      logger.error("Failed to send welcome email to: {}", email, e);
      throw new RuntimeException("Failed to send welcome email", e);
    }
  }
}