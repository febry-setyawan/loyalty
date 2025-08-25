package com.example.loyalty.users.application.usecases;

import com.example.loyalty.common.exceptions.ValidationException;
import com.example.loyalty.common.exceptions.NotFoundException;
import com.example.loyalty.users.application.dto.VerifyUserRequest;
import com.example.loyalty.users.application.dto.UserResponse;
import com.example.loyalty.users.application.services.NotificationService;
import com.example.loyalty.users.domain.entities.User;
import com.example.loyalty.users.domain.entities.UserAuth;
import com.example.loyalty.users.domain.repositories.UserRepository;
import com.example.loyalty.users.domain.repositories.UserAuthRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for user verification (email/SMS)
 */
@Service
public class VerifyUserUseCase {
  
  private final UserRepository userRepository;
  private final UserAuthRepository userAuthRepository;
  private final NotificationService notificationService;
  
  public VerifyUserUseCase(
      UserRepository userRepository,
      UserAuthRepository userAuthRepository,
      NotificationService notificationService) {
    this.userRepository = userRepository;
    this.userAuthRepository = userAuthRepository;
    this.notificationService = notificationService;
  }
  
  @Transactional
  public UserResponse execute(VerifyUserRequest request) {
    // Find user auth by verification token
    UserAuth userAuth = userAuthRepository.findByVerificationToken(request.getToken())
        .orElseThrow(() -> new NotFoundException("Invalid verification token"));
    
    // Validate token
    if (!userAuth.isVerificationTokenValid(request.getToken())) {
      throw new ValidationException("Verification token has expired");
    }
    
    // Get user
    User user = userAuth.getUser();
    
    // Activate user account
    user.activate();
    userRepository.save(user);
    
    // Clear verification token
    userAuth.clearVerificationToken();
    userAuthRepository.save(userAuth);
    
    // Send welcome email
    notificationService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
    
    // Return response
    return new UserResponse(
        user.getId().toString(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getPhoneNumber(),
        user.getStatus(),
        user.getTier(),
        true // Now verified
    );
  }
}