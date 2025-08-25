package com.example.loyalty.users.application.dto;

import com.example.loyalty.users.domain.entities.UserStatus;
import com.example.loyalty.users.domain.entities.UserTier;

/**
 * User response DTO for API responses
 */
public class UserResponse {
  
  private String id;
  private String email;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private UserStatus status;
  private UserTier tier;
  private boolean isVerified;

  // Default constructor
  public UserResponse() {}

  // Constructor
  public UserResponse(String id, String email, String firstName, String lastName, 
                     String phoneNumber, UserStatus status, UserTier tier, boolean isVerified) {
    this.id = id;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.phoneNumber = phoneNumber;
    this.status = status;
    this.tier = tier;
    this.isVerified = isVerified;
  }

  // Getters and setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public UserTier getTier() {
    return tier;
  }

  public void setTier(UserTier tier) {
    this.tier = tier;
  }

  public boolean isVerified() {
    return isVerified;
  }

  public void setVerified(boolean verified) {
    isVerified = verified;
  }
}