package com.example.loyalty.users.application.usecases;

import com.example.loyalty.common.exceptions.NotFoundException;
import com.example.loyalty.common.validation.ValidationUtils;
import com.example.loyalty.users.application.dto.UpdateProfileRequest;
import com.example.loyalty.users.domain.entities.User;
import com.example.loyalty.users.domain.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

/**
 * Use case for updating user profile information
 */
@Service
@Transactional
public class UpdateUserProfileUseCase {

  private static final Logger logger = LoggerFactory.getLogger(UpdateUserProfileUseCase.class);

  private final UserRepository userRepository;
  private final ValidationUtils validationUtils;

  public UpdateUserProfileUseCase(UserRepository userRepository, ValidationUtils validationUtils) {
    this.userRepository = userRepository;
    this.validationUtils = validationUtils;
  }

  public User execute(UUID userId, UpdateProfileRequest request) {
    // Validate request
    validationUtils.validate(request);

    // Find user
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

    // Update profile
    user.updateProfile(
      request.getFirstName(),
      request.getLastName(),
      request.getPhoneNumber(),
      request.getDateOfBirth()
    );

    // Save user
    User updatedUser = userRepository.save(user);

    logger.info("Profile updated for user: {}", userId);

    return updatedUser;
  }
}