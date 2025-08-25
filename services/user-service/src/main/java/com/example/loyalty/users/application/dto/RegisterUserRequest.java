package com.example.loyalty.users.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * User registration request DTO with validation
 */
public class RegisterUserRequest {
  
  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  @Size(max = 255, message = "Email too long")
  private String email;
  
  @NotBlank(message = "Password is required")
  @Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
    message = "Password must be at least 8 characters with uppercase, lowercase, digit and special character"
  )
  private String password;
  
  @NotBlank(message = "First name is required")
  @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
  private String firstName;
  
  @NotBlank(message = "Last name is required")
  @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
  private String lastName;
  
  @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
  private String phoneNumber;

  // Default constructor
  public RegisterUserRequest() {}

  // Constructor
  public RegisterUserRequest(String email, String password, String firstName, String lastName, String phoneNumber) {
    this.email = email;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.phoneNumber = phoneNumber;
  }

  // Getters and setters
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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
}