package com.example.loyalty.users.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Request DTO for updating user profile information
 */
public class UpdateProfileRequest {

  @NotBlank(message = "First name is required")
  @Size(max = 100, message = "First name too long")
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 100, message = "Last name too long")
  private String lastName;

  @Size(max = 20, message = "Phone number too long")
  private String phoneNumber;

  @Past(message = "Date of birth must be in the past")
  private LocalDate dateOfBirth;

  // Default constructor
  public UpdateProfileRequest() {}

  // Constructor
  public UpdateProfileRequest(String firstName, String lastName, String phoneNumber, LocalDate dateOfBirth) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.phoneNumber = phoneNumber;
    this.dateOfBirth = dateOfBirth;
  }

  // Getters and setters
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
}