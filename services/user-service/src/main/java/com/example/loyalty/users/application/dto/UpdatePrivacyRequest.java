package com.example.loyalty.users.application.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating user privacy settings
 */
public class UpdatePrivacyRequest {

  @NotNull(message = "Email visibility setting is required")
  private Boolean emailVisible = true;

  @NotNull(message = "Phone visibility setting is required")
  private Boolean phoneVisible = true;

  @NotNull(message = "Date of birth visibility setting is required")
  private Boolean dateOfBirthVisible = false;

  @NotNull(message = "Profile picture visibility setting is required")
  private Boolean profilePictureVisible = true;

  // Default constructor
  public UpdatePrivacyRequest() {}

  // Constructor
  public UpdatePrivacyRequest(Boolean emailVisible, Boolean phoneVisible, Boolean dateOfBirthVisible, Boolean profilePictureVisible) {
    this.emailVisible = emailVisible;
    this.phoneVisible = phoneVisible;
    this.dateOfBirthVisible = dateOfBirthVisible;
    this.profilePictureVisible = profilePictureVisible;
  }

  // Getters and setters
  public Boolean getEmailVisible() {
    return emailVisible;
  }

  public void setEmailVisible(Boolean emailVisible) {
    this.emailVisible = emailVisible;
  }

  public Boolean getPhoneVisible() {
    return phoneVisible;
  }

  public void setPhoneVisible(Boolean phoneVisible) {
    this.phoneVisible = phoneVisible;
  }

  public Boolean getDateOfBirthVisible() {
    return dateOfBirthVisible;
  }

  public void setDateOfBirthVisible(Boolean dateOfBirthVisible) {
    this.dateOfBirthVisible = dateOfBirthVisible;
  }

  public Boolean getProfilePictureVisible() {
    return profilePictureVisible;
  }

  public void setProfilePictureVisible(Boolean profilePictureVisible) {
    this.profilePictureVisible = profilePictureVisible;
  }
}