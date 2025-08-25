package com.example.loyalty.users.domain.repositories;

import com.example.loyalty.users.domain.entities.UserAuth;
import java.util.Optional;
import java.util.UUID;

/**
 * User authentication repository interface - Domain layer contract
 * Implementation will be provided by infrastructure layer
 */
public interface UserAuthRepository {
  
  UserAuth save(UserAuth userAuth);
  
  Optional<UserAuth> findByUserId(UUID userId);
  
  Optional<UserAuth> findByVerificationToken(String token);
  
  Optional<UserAuth> findByPasswordResetToken(String token);
  
  void deleteByUserId(UUID userId);
}