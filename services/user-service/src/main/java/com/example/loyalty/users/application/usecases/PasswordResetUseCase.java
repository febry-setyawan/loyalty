package com.example.loyalty.users.application.usecases;

import com.example.loyalty.common.exceptions.NotFoundException;
import com.example.loyalty.users.application.dto.PasswordResetRequest;
import com.example.loyalty.users.application.services.NotificationService;
import com.example.loyalty.users.domain.entities.User;
import com.example.loyalty.users.domain.entities.UserAuth;
import com.example.loyalty.users.domain.repositories.UserRepository;
import com.example.loyalty.users.domain.repositories.UserAuthRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Use case for initiating password reset
 */
@Service
public class PasswordResetUseCase {
  
  private final UserRepository userRepository;
  private final UserAuthRepository userAuthRepository;
  private final NotificationService notificationService;
  private final SecureRandom secureRandom;
  
  public PasswordResetUseCase(
      UserRepository userRepository,
      UserAuthRepository userAuthRepository,
      NotificationService notificationService) {
    this.userRepository = userRepository;
    this.userAuthRepository = userAuthRepository;
    this.notificationService = notificationService;
    this.secureRandom = new SecureRandom();
  }
  
  @Transactional
  public void execute(PasswordResetRequest request) {
    // Find user by email
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new NotFoundException("User not found with this email"));
    
    // Get user auth
    UserAuth userAuth = userAuthRepository.findByUserId(user.getId())
        .orElseThrow(() -> new NotFoundException("User authentication data not found"));
    
    // Generate reset token
    String resetToken = generateToken();
    userAuth.setPasswordResetToken(resetToken, 1); // 1 hour expiration
    
    // Save auth record
    userAuthRepository.save(userAuth);
    
    // Send password reset email
    notificationService.sendPasswordResetEmail(
        user.getEmail(), 
        user.getFirstName(), 
        resetToken
    );
    
    // Send password reset SMS if phone number available
    if (user.getPhoneNumber() != null) {
      notificationService.sendPasswordResetSMS(
          user.getPhoneNumber(), 
          user.getFirstName(), 
          resetToken
      );
    }
  }
  
  private String generateToken() {
    byte[] bytes = new byte[32];
    secureRandom.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }
}