package com.example.loyalty.users.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Password reset confirmation request DTO
 */
public class PasswordResetConfirmRequest {
  
  @NotBlank(message = "Token is required")
  private String token;
  
  @NotBlank(message = "New password is required")
  @Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
    message = "Password must be at least 8 characters with uppercase, lowercase, digit and special character"
  )
  private String newPassword;

  // Default constructor
  public PasswordResetConfirmRequest() {}

  // Constructor
  public PasswordResetConfirmRequest(String token, String newPassword) {
    this.token = token;
    this.newPassword = newPassword;
  }

  // Getters and setters
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}