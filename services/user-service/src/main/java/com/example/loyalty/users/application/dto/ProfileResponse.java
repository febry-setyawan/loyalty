package com.example.loyalty.users.application.dto;

import com.example.loyalty.users.domain.entities.User;
import com.example.loyalty.users.domain.entities.UserTier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for user profile information
 */
public class ProfileResponse {

  private UUID id;
  private String email;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private LocalDate dateOfBirth;
  private UserTier tier;
  private Map<String, Object> preferences;
  private String avatarUrl;
  private LocalDateTime lastUpdated;

  // Default constructor
  public ProfileResponse() {}

  // Static factory method to create from User entity
  public static ProfileResponse from(User user) {
    ProfileResponse response = new ProfileResponse();
    response.id = user.getId();
    response.email = user.getEmail();
    response.firstName = user.getFirstName();
    response.lastName = user.getLastName();
    response.phoneNumber = user.getPhoneNumber();
    response.dateOfBirth = user.getDateOfBirth();
    response.tier = user.getTier();
    response.preferences = user.getPreferences();
    response.avatarUrl = user.getAvatarUrl();
    response.lastUpdated = user.getUpdatedAt();
    return response;
  }

  // Getters and setters
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
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

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public UserTier getTier() {
    return tier;
  }

  public void setTier(UserTier tier) {
    this.tier = tier;
  }

  public Map<String, Object> getPreferences() {
    return preferences;
  }

  public void setPreferences(Map<String, Object> preferences) {
    this.preferences = preferences;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public LocalDateTime getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(LocalDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
  }
}