package com.example.loyalty.users.domain.entities;

import com.example.loyalty.common.database.BaseEntity;
import com.example.loyalty.common.security.SensitiveData;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * User domain entity representing core user information
 * Follows DDD principles with business logic encapsulation
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity {

  // ID is inherited from BaseEntity

  @Column(name = "email", nullable = false, unique = true)
  @Email(message = "Invalid email format")
  @NotBlank(message = "Email is required")
  @Size(max = 255, message = "Email too long")
  @SensitiveData
  private String email;

  @Column(name = "first_name", nullable = false)
  @NotBlank(message = "First name is required")
  @Size(max = 100, message = "First name too long")
  private String firstName;

  @Column(name = "last_name", nullable = false)
  @NotBlank(message = "Last name is required")
  @Size(max = 100, message = "Last name too long")
  private String lastName;

  @Column(name = "phone_number")
  @Size(max = 20, message = "Phone number too long")
  @SensitiveData
  private String phoneNumber;

  @Column(name = "date_of_birth")
  @SensitiveData
  private LocalDate dateOfBirth;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private UserStatus status = UserStatus.ACTIVE;

  @Enumerated(EnumType.STRING)
  @Column(name = "tier", nullable = false)
  private UserTier tier = UserTier.BRONZE;

  @Column(name = "preferences", columnDefinition = "jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, Object> preferences;

  // Default constructor for JPA
  protected User() {}

  // Constructor for creating new users
  public User(String email, String firstName, String lastName) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.status = UserStatus.PENDING_VERIFICATION;
  }

  // Business methods
  public boolean isActive() {
    return this.status == UserStatus.ACTIVE;
  }

  public boolean isPendingVerification() {
    return this.status == UserStatus.PENDING_VERIFICATION;
  }

  public void activate() {
    this.status = UserStatus.ACTIVE;
  }

  public void suspend() {
    this.status = UserStatus.SUSPENDED;
  }

  public void deactivate() {
    this.status = UserStatus.INACTIVE;
  }

  public void updateProfile(String firstName, String lastName, String phoneNumber, LocalDate dateOfBirth) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.phoneNumber = phoneNumber;
    this.dateOfBirth = dateOfBirth;
  }

  public String getFullName() {
    return firstName + " " + lastName;
  }

  // Getters and setters
  // getId() inherited from BaseEntity

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
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

  public UserStatus getStatus() {
    return status;
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
}