package com.example.loyalty.users.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Verification request DTO
 */
public class VerifyUserRequest {
  
  @NotBlank(message = "Verification token is required")
  private String token;

  // Default constructor
  public VerifyUserRequest() {}

  // Constructor
  public VerifyUserRequest(String token) {
    this.token = token;
  }

  // Getters and setters
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}