package com.example.loyalty.users.application.usecases;

import com.example.loyalty.common.exceptions.NotFoundException;
import com.example.loyalty.common.validation.ValidationUtils;
import com.example.loyalty.users.application.dto.UpdatePrivacyRequest;
import com.example.loyalty.users.domain.entities.User;
import com.example.loyalty.users.domain.repositories.UserRepository;
import com.example.loyalty.users.domain.valueobjects.PrivacySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

/**
 * Use case for updating user privacy settings
 */
@Service
@Transactional
public class UpdatePrivacySettingsUseCase {

  private static final Logger logger = LoggerFactory.getLogger(UpdatePrivacySettingsUseCase.class);

  private final UserRepository userRepository;
  private final ValidationUtils validationUtils;

  public UpdatePrivacySettingsUseCase(UserRepository userRepository, ValidationUtils validationUtils) {
    this.userRepository = userRepository;
    this.validationUtils = validationUtils;
  }

  public User execute(UUID userId, UpdatePrivacyRequest request) {
    // Validate request
    validationUtils.validate(request);

    // Find user
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

    // Create new privacy settings
    PrivacySettings privacySettings = new PrivacySettings(
      request.getEmailVisible(),
      request.getPhoneVisible(),
      request.getDateOfBirthVisible(),
      request.getProfilePictureVisible()
    );

    // Update user privacy settings
    user.updatePrivacySettings(privacySettings);

    // Save user
    User updatedUser = userRepository.save(user);

    logger.info("Privacy settings updated for user: {}", userId);

    return updatedUser;
  }
}