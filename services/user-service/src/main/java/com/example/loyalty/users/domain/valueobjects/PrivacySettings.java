package com.example.loyalty.users.domain.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Privacy settings value object for user profile data visibility
 */
@Embeddable
public class PrivacySettings {

  private Boolean emailVisible = true;
  private Boolean phoneVisible = true;
  private Boolean dateOfBirthVisible = false;
  private Boolean profilePictureVisible = true;

  // Default constructor for JPA
  protected PrivacySettings() {}

  // Constructor
  public PrivacySettings(Boolean emailVisible, Boolean phoneVisible, Boolean dateOfBirthVisible, Boolean profilePictureVisible) {
    this.emailVisible = emailVisible;
    this.phoneVisible = phoneVisible;
    this.dateOfBirthVisible = dateOfBirthVisible;
    this.profilePictureVisible = profilePictureVisible;
  }

  // Static factory method with defaults
  public static PrivacySettings defaultSettings() {
    return new PrivacySettings(true, true, false, true);
  }

  // Business methods
  public boolean canShowEmail() {
    return Boolean.TRUE.equals(emailVisible);
  }

  public boolean canShowPhone() {
    return Boolean.TRUE.equals(phoneVisible);
  }

  public boolean canShowDateOfBirth() {
    return Boolean.TRUE.equals(dateOfBirthVisible);
  }

  public boolean canShowProfilePicture() {
    return Boolean.TRUE.equals(profilePictureVisible);
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