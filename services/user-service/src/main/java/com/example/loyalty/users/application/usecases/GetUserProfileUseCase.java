package com.example.loyalty.users.application.usecases;

import com.example.loyalty.common.exceptions.NotFoundException;
import com.example.loyalty.users.domain.entities.User;
import com.example.loyalty.users.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

/**
 * Use case for retrieving user profile information
 */
@Service
@Transactional(readOnly = true)
public class GetUserProfileUseCase {

  private final UserRepository userRepository;

  public GetUserProfileUseCase(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User execute(UUID userId) {
    return userRepository.findById(userId)
      .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
  }
}