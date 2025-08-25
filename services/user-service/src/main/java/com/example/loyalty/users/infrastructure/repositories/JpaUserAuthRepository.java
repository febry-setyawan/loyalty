package com.example.loyalty.users.infrastructure.repositories;

import com.example.loyalty.users.domain.entities.UserAuth;
import com.example.loyalty.users.domain.repositories.UserAuthRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of UserAuthRepository
 */
@Repository
public interface JpaUserAuthRepository extends JpaRepository<UserAuth, UUID>, UserAuthRepository {
  
  Optional<UserAuth> findByUserId(UUID userId);
  
  Optional<UserAuth> findByVerificationToken(String token);
  
  Optional<UserAuth> findByPasswordResetToken(String token);
  
  void deleteByUserId(UUID userId);
}