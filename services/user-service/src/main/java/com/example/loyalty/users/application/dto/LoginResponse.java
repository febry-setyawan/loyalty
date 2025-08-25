package com.example.loyalty.users.application.dto;

/**
 * Login response DTO containing JWT tokens
 */
public class LoginResponse {
  
  private String accessToken;
  private String refreshToken;
  private long expiresIn;
  private UserResponse user;

  // Default constructor
  public LoginResponse() {}

  // Constructor
  public LoginResponse(String accessToken, String refreshToken, long expiresIn, UserResponse user) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.expiresIn = expiresIn;
    this.user = user;
  }

  // Getters and setters
  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public long getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(long expiresIn) {
    this.expiresIn = expiresIn;
  }

  public UserResponse getUser() {
    return user;
  }

  public void setUser(UserResponse user) {
    this.user = user;
  }
}