package com.example.loyalty.users.interfaces.controllers;

import com.example.loyalty.common.response.ApiResponse;
import com.example.loyalty.users.application.dto.*;
import com.example.loyalty.users.application.usecases.*;
import com.example.loyalty.users.infrastructure.security.RateLimit;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user authentication operations
 */
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*") // Configure properly for production
public class AuthController {
  
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  
  private final RegisterUserUseCase registerUserUseCase;
  private final LoginUserUseCase loginUserUseCase;
  private final VerifyUserUseCase verifyUserUseCase;
  private final PasswordResetUseCase passwordResetUseCase;
  private final PasswordResetConfirmUseCase passwordResetConfirmUseCase;
  
  public AuthController(
      RegisterUserUseCase registerUserUseCase,
      LoginUserUseCase loginUserUseCase,
      VerifyUserUseCase verifyUserUseCase,
      PasswordResetUseCase passwordResetUseCase,
      PasswordResetConfirmUseCase passwordResetConfirmUseCase) {
    this.registerUserUseCase = registerUserUseCase;
    this.loginUserUseCase = loginUserUseCase;
    this.verifyUserUseCase = verifyUserUseCase;
    this.passwordResetUseCase = passwordResetUseCase;
    this.passwordResetConfirmUseCase = passwordResetConfirmUseCase;
  }
  
  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterUserRequest request) {
    logger.info("User registration request for email: {}", request.getEmail());
    
    UserResponse response = registerUserUseCase.execute(request);
    
    logger.info("User registered successfully with ID: {}", response.getId());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(response, "User registered successfully. Please check your email/SMS for verification."));
  }
  
  @PostMapping("/login")
  @RateLimit(limit = 5, window = 300) // 5 attempts per 5 minutes
  public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
    logger.info("User login request for email: {}", request.getEmail());
    
    LoginResponse response = loginUserUseCase.execute(request);
    
    logger.info("User logged in successfully: {}", request.getEmail());
    return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
  }
  
  @PostMapping("/verify")
  public ResponseEntity<ApiResponse<UserResponse>> verify(@Valid @RequestBody VerifyUserRequest request) {
    logger.info("User verification request with token: {}", request.getToken());
    
    UserResponse response = verifyUserUseCase.execute(request);
    
    logger.info("User verified successfully: {}", response.getId());
    return ResponseEntity.ok(ApiResponse.success(response, "Account verified successfully"));
  }
  
  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<String>> logout() {
    // For JWT-based authentication, logout is typically handled client-side by removing the token
    // In a more sophisticated implementation, you might maintain a blacklist of tokens
    logger.info("User logout request");
    
    return ResponseEntity.ok(ApiResponse.success("Token should be removed from client", "Logout successful"));
  }
  
  @PostMapping("/password-reset")
  @RateLimit(limit = 3, window = 300) // 3 reset requests per 5 minutes
  public ResponseEntity<ApiResponse<String>> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
    logger.info("Password reset request for email: {}", request.getEmail());
    
    passwordResetUseCase.execute(request);
    
    logger.info("Password reset email sent to: {}", request.getEmail());
    return ResponseEntity.ok(ApiResponse.success("Password reset instructions sent to your email", "Password reset initiated"));
  }
  
  @PostMapping("/password-reset/confirm")
  public ResponseEntity<ApiResponse<UserResponse>> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmRequest request) {
    logger.info("Password reset confirmation request");
    
    UserResponse response = passwordResetConfirmUseCase.execute(request);
    
    logger.info("Password reset successfully for user: {}", response.getId());
    return ResponseEntity.ok(ApiResponse.success(response, "Password reset successfully"));
  }
}