package com.example.loyalty.users.application.usecases;

import com.example.loyalty.common.exceptions.ConflictException;
import com.example.loyalty.users.application.dto.RegisterUserRequest;
import com.example.loyalty.users.application.dto.UserResponse;
import com.example.loyalty.users.application.services.NotificationService;
import com.example.loyalty.users.application.services.PasswordService;
import com.example.loyalty.users.domain.entities.User;
import com.example.loyalty.users.domain.entities.UserAuth;
import com.example.loyalty.users.domain.entities.UserStatus;
import com.example.loyalty.users.domain.repositories.UserRepository;
import com.example.loyalty.users.domain.repositories.UserAuthRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Use case for user registration
 */
@Service
public class RegisterUserUseCase {
  
  private final UserRepository userRepository;
  private final UserAuthRepository userAuthRepository;
  private final PasswordService passwordService;
  private final NotificationService notificationService;
  private final SecureRandom secureRandom;
  
  public RegisterUserUseCase(
      UserRepository userRepository, 
      UserAuthRepository userAuthRepository,
      PasswordService passwordService,
      NotificationService notificationService) {
    this.userRepository = userRepository;
    this.userAuthRepository = userAuthRepository;
    this.passwordService = passwordService;
    this.notificationService = notificationService;
    this.secureRandom = new SecureRandom();
  }
  
  @Transactional
  public UserResponse execute(RegisterUserRequest request) {
    // Check if user already exists
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new ConflictException("User already exists with this email");
    }
    
    if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
      throw new ConflictException("User already exists with this phone number");
    }
    
    // Create user entity
    User user = new User(request.getEmail(), request.getFirstName(), request.getLastName());
    user.setPhoneNumber(request.getPhoneNumber());
    
    // Save user
    User savedUser = userRepository.save(user);
    
    // Hash password and create auth record
    String passwordHash = passwordService.hashPassword(request.getPassword());
    UserAuth userAuth = new UserAuth(savedUser, passwordHash);
    
    // Generate verification token
    String verificationToken = generateToken();
    userAuth.setVerificationToken(verificationToken, 24); // 24 hours expiration
    
    // Save auth record
    userAuthRepository.save(userAuth);
    
    // Send verification email
    notificationService.sendVerificationEmail(
        savedUser.getEmail(), 
        savedUser.getFirstName(), 
        verificationToken
    );
    
    // Send verification SMS if phone number provided
    if (savedUser.getPhoneNumber() != null) {
      notificationService.sendVerificationSMS(
          savedUser.getPhoneNumber(), 
          savedUser.getFirstName(), 
          verificationToken
      );
    }
    
    // Return response
    return new UserResponse(
        savedUser.getId().toString(),
        savedUser.getEmail(),
        savedUser.getFirstName(),
        savedUser.getLastName(),
        savedUser.getPhoneNumber(),
        savedUser.getStatus(),
        savedUser.getTier(),
        false // Not yet verified
    );
  }
  
  private String generateToken() {
    byte[] bytes = new byte[32];
    secureRandom.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }
}