package com.example.loyalty.users.infrastructure.repositories;

import com.example.loyalty.users.domain.entities.User;
import com.example.loyalty.users.domain.entities.UserStatus;
import com.example.loyalty.users.domain.repositories.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

/**
 * JPA implementation of UserRepository
 */
@Repository
public interface JpaUserRepository extends JpaRepository<User, UUID>, UserRepository {
  
  Optional<User> findByEmail(String email);
  
  Optional<User> findByPhoneNumber(String phoneNumber);
  
  boolean existsByEmail(String email);
  
  boolean existsByPhoneNumber(String phoneNumber);
  
  List<User> findByStatus(UserStatus status);
  
  long countByStatus(UserStatus status);
}