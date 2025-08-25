package com.example.loyalty.users.application.usecases;

import com.example.loyalty.common.exceptions.AuthenticationException;
import com.example.loyalty.common.security.JwtTokenService;
import com.example.loyalty.users.application.dto.LoginRequest;
import com.example.loyalty.users.application.dto.LoginResponse;
import com.example.loyalty.users.application.dto.UserResponse;
import com.example.loyalty.users.application.services.PasswordService;
import com.example.loyalty.users.domain.entities.User;
import com.example.loyalty.users.domain.entities.UserAuth;
import com.example.loyalty.users.domain.repositories.UserRepository;
import com.example.loyalty.users.domain.repositories.UserAuthRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Use case for user authentication/login
 */
@Service
public class LoginUserUseCase {
  
  private final UserRepository userRepository;
  private final UserAuthRepository userAuthRepository;
  private final PasswordService passwordService;
  private final JwtTokenService jwtTokenService;
  
  public LoginUserUseCase(
      UserRepository userRepository,
      UserAuthRepository userAuthRepository,
      PasswordService passwordService,
      JwtTokenService jwtTokenService) {
    this.userRepository = userRepository;
    this.userAuthRepository = userAuthRepository;
    this.passwordService = passwordService;
    this.jwtTokenService = jwtTokenService;
  }
  
  @Transactional
  public LoginResponse execute(LoginRequest request) {
    // Find user by email
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new AuthenticationException("Invalid email or password"));
    
    // Get user auth
    UserAuth userAuth = userAuthRepository.findByUserId(user.getId())
        .orElseThrow(() -> new AuthenticationException("Invalid email or password"));
    
    // Check if account is locked
    if (userAuth.isAccountLocked()) {
      throw new AuthenticationException("Account is temporarily locked due to too many failed login attempts");
    }
    
    // Verify password
    if (!passwordService.verifyPassword(request.getPassword(), userAuth.getPasswordHash())) {
      userAuth.recordFailedLogin();
      userAuthRepository.save(userAuth);
      throw new AuthenticationException("Invalid email or password");
    }
    
    // Check if user account is active
    if (!user.isActive()) {
      throw new AuthenticationException("Account is not active");
    }
    
    // Record successful login
    userAuth.recordSuccessfulLogin();
    userAuthRepository.save(userAuth);
    
    // Generate JWT tokens
    List<String> roles = List.of("USER"); // Basic user role
    String accessToken = jwtTokenService.generateAccessToken(user.getId().toString(), user.getEmail(), roles);
    String refreshToken = jwtTokenService.generateRefreshToken(user.getId().toString());
    
    // Create user response
    UserResponse userResponse = new UserResponse(
        user.getId().toString(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getPhoneNumber(),
        user.getStatus(),
        user.getTier(),
        user.isActive() // User is verified if active
    );
    
    // Return login response with tokens
    return new LoginResponse(
        accessToken,
        refreshToken,
        15 * 60, // 15 minutes in seconds
        userResponse
    );
  }
}