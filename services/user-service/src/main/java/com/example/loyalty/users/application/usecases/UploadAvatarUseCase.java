package com.example.loyalty.users.application.usecases;

import com.example.loyalty.common.exceptions.NotFoundException;
import com.example.loyalty.common.exceptions.ValidationException;
import com.example.loyalty.users.domain.entities.User;
import com.example.loyalty.users.domain.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

/**
 * Use case for uploading user avatar/profile picture
 */
@Service
@Transactional
public class UploadAvatarUseCase {

  private static final Logger logger = LoggerFactory.getLogger(UploadAvatarUseCase.class);
  
  private final UserRepository userRepository;

  // Simplified - in production this would be configurable
  private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
  private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/gif"};

  public UploadAvatarUseCase(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User execute(UUID userId, MultipartFile file) {
    // Validate file
    validateFile(file);

    // Find user
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

    // Generate avatar URL (simplified - in production would upload to S3)
    String avatarUrl = generateAvatarUrl(userId, file);

    // Update user avatar
    user.updateAvatarUrl(avatarUrl);

    // Save user
    User updatedUser = userRepository.save(user);

    logger.info("Avatar uploaded for user: {}, URL: {}", userId, avatarUrl);

    return updatedUser;
  }

  private void validateFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new ValidationException("Avatar file is required", "file");
    }

    if (file.getSize() > MAX_FILE_SIZE) {
      throw new ValidationException("Avatar file size must be less than 5MB", "file");
    }

    String contentType = file.getContentType();
    boolean isValidType = false;
    for (String allowedType : ALLOWED_TYPES) {
      if (allowedType.equals(contentType)) {
        isValidType = true;
        break;
      }
    }

    if (!isValidType) {
      throw new ValidationException("Avatar must be JPEG, PNG or GIF image", "file");
    }
  }

  private String generateAvatarUrl(UUID userId, MultipartFile file) {
    // Simplified - in production would upload to S3 and return actual URL
    // For now, generate a placeholder URL
    String fileExtension = getFileExtension(file.getOriginalFilename());
    return String.format("/avatars/%s.%s", userId, fileExtension);
  }

  private String getFileExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
      return "jpg";
    }
    return filename.substring(filename.lastIndexOf(".") + 1);
  }
}