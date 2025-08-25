package com.example.loyalty.users.interfaces.controllers;

import com.example.loyalty.common.response.ApiResponse;
import com.example.loyalty.users.application.dto.ProfileResponse;
import com.example.loyalty.users.application.dto.PrivacySettingsResponse;
import com.example.loyalty.users.application.dto.UpdatePrivacyRequest;
import com.example.loyalty.users.application.dto.UpdateProfileRequest;
import com.example.loyalty.users.application.usecases.GetUserProfileUseCase;
import com.example.loyalty.users.application.usecases.UpdatePrivacySettingsUseCase;
import com.example.loyalty.users.application.usecases.UpdateUserProfileUseCase;
import com.example.loyalty.users.application.usecases.UploadAvatarUseCase;
import com.example.loyalty.users.domain.entities.User;
import com.example.loyalty.users.domain.valueobjects.PrivacySettings;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

/**
 * REST controller for user profile management operations
 */
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*") // Configure properly for production
public class ProfileController {

  private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

  private final GetUserProfileUseCase getUserProfileUseCase;
  private final UpdateUserProfileUseCase updateUserProfileUseCase;
  private final UpdatePrivacySettingsUseCase updatePrivacySettingsUseCase;
  private final UploadAvatarUseCase uploadAvatarUseCase;

  public ProfileController(
      GetUserProfileUseCase getUserProfileUseCase,
      UpdateUserProfileUseCase updateUserProfileUseCase,
      UpdatePrivacySettingsUseCase updatePrivacySettingsUseCase,
      UploadAvatarUseCase uploadAvatarUseCase) {
    this.getUserProfileUseCase = getUserProfileUseCase;
    this.updateUserProfileUseCase = updateUserProfileUseCase;
    this.updatePrivacySettingsUseCase = updatePrivacySettingsUseCase;
    this.uploadAvatarUseCase = uploadAvatarUseCase;
  }

  @GetMapping("/profile")
  public ResponseEntity<ApiResponse<ProfileResponse>> getProfile() {
    logger.info("Getting user profile");
    
    // TODO: In production, get userId from JWT token/security context
    UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"); // Placeholder
    
    User user = getUserProfileUseCase.execute(userId);
    ProfileResponse response = ProfileResponse.from(user);
    
    return ResponseEntity.ok(ApiResponse.success(response, "Profile retrieved successfully"));
  }

  @PutMapping("/profile")
  public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
    logger.info("Updating user profile");
    
    // TODO: In production, get userId from JWT token/security context
    UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"); // Placeholder
    
    User user = updateUserProfileUseCase.execute(userId, request);
    ProfileResponse response = ProfileResponse.from(user);
    
    return ResponseEntity.ok(ApiResponse.success(response, "Profile updated successfully"));
  }

  @PostMapping("/profile/avatar")
  public ResponseEntity<ApiResponse<ProfileResponse>> uploadAvatar(@RequestParam("file") MultipartFile file) {
    logger.info("Uploading user avatar");
    
    // TODO: In production, get userId from JWT token/security context
    UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"); // Placeholder
    
    User user = uploadAvatarUseCase.execute(userId, file);
    ProfileResponse response = ProfileResponse.from(user);
    
    return ResponseEntity.ok(ApiResponse.success(response, "Avatar uploaded successfully"));
  }

  @GetMapping("/profile/privacy")
  public ResponseEntity<ApiResponse<PrivacySettingsResponse>> getPrivacySettings() {
    logger.info("Getting user privacy settings");
    
    // TODO: In production, get userId from JWT token/security context
    UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"); // Placeholder
    
    User user = getUserProfileUseCase.execute(userId);
    PrivacySettings privacy = user.getPrivacySettings();
    
    // Handle case where privacy settings might be null for existing users
    if (privacy == null) {
      privacy = PrivacySettings.defaultSettings();
    }
    
    PrivacySettingsResponse response = new PrivacySettingsResponse(
      privacy.getEmailVisible(),
      privacy.getPhoneVisible(),
      privacy.getDateOfBirthVisible(),
      privacy.getProfilePictureVisible()
    );
    
    return ResponseEntity.ok(ApiResponse.success(response, "Privacy settings retrieved successfully"));
  }

  @PutMapping("/profile/privacy")
  public ResponseEntity<ApiResponse<PrivacySettingsResponse>> updatePrivacySettings(@Valid @RequestBody UpdatePrivacyRequest request) {
    logger.info("Updating user privacy settings");
    
    // TODO: In production, get userId from JWT token/security context
    UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"); // Placeholder
    
    User user = updatePrivacySettingsUseCase.execute(userId, request);
    PrivacySettings privacy = user.getPrivacySettings();
    
    PrivacySettingsResponse response = new PrivacySettingsResponse(
      privacy.getEmailVisible(),
      privacy.getPhoneVisible(),
      privacy.getDateOfBirthVisible(),
      privacy.getProfilePictureVisible()
    );
    
    return ResponseEntity.ok(ApiResponse.success(response, "Privacy settings updated successfully"));
  }
}