package com.example.loyalty.users.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Password reset request DTO
 */
public class PasswordResetRequest {
  
  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;

  // Default constructor
  public PasswordResetRequest() {}

  // Constructor
  public PasswordResetRequest(String email) {
    this.email = email;
  }

  // Getters and setters
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}