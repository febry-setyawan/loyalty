package com.example.loyalty.users.application.dto;

/**
 * Response DTO for user privacy settings
 */
public class PrivacySettingsResponse {

  private Boolean emailVisible;
  private Boolean phoneVisible;
  private Boolean dateOfBirthVisible;
  private Boolean profilePictureVisible;

  // Default constructor
  public PrivacySettingsResponse() {}

  // Constructor
  public PrivacySettingsResponse(Boolean emailVisible, Boolean phoneVisible, Boolean dateOfBirthVisible, Boolean profilePictureVisible) {
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