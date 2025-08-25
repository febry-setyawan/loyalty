package com.example.loyalty.users.application.usecases;

import com.example.loyalty.common.exceptions.NotFoundException;
import com.example.loyalty.common.exceptions.ValidationException;
import com.example.loyalty.users.application.dto.PasswordResetConfirmRequest;
import com.example.loyalty.users.application.dto.UserResponse;
import com.example.loyalty.users.application.services.PasswordService;
import com.example.loyalty.users.domain.entities.User;
import com.example.loyalty.users.domain.entities.UserAuth;
import com.example.loyalty.users.domain.repositories.UserRepository;
import com.example.loyalty.users.domain.repositories.UserAuthRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for confirming password reset
 */
@Service
public class PasswordResetConfirmUseCase {
  
  private final UserRepository userRepository;
  private final UserAuthRepository userAuthRepository;
  private final PasswordService passwordService;
  
  public PasswordResetConfirmUseCase(
      UserRepository userRepository,
      UserAuthRepository userAuthRepository,
      PasswordService passwordService) {
    this.userRepository = userRepository;
    this.userAuthRepository = userAuthRepository;
    this.passwordService = passwordService;
  }
  
  @Transactional
  public UserResponse execute(PasswordResetConfirmRequest request) {
    // Find user auth by reset token
    UserAuth userAuth = userAuthRepository.findByPasswordResetToken(request.getToken())
        .orElseThrow(() -> new NotFoundException("Invalid password reset token"));
    
    // Validate token
    if (!userAuth.isPasswordResetTokenValid(request.getToken())) {
      throw new ValidationException("Password reset token has expired");
    }
    
    // Get user
    User user = userAuth.getUser();
    
    // Hash new password and update
    String newPasswordHash = passwordService.hashPassword(request.getNewPassword());
    userAuth.updatePassword(newPasswordHash);
    
    // Save auth record
    userAuthRepository.save(userAuth);
    
    // Return response
    return new UserResponse(
        user.getId().toString(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getPhoneNumber(),
        user.getStatus(),
        user.getTier(),
        user.isActive()
    );
  }
}