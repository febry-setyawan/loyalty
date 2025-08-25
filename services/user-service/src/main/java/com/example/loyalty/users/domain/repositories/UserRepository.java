package com.example.loyalty.users.domain.repositories;

import com.example.loyalty.users.domain.entities.User;
import com.example.loyalty.users.domain.entities.UserStatus;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

/**
 * User repository interface - Domain layer contract
 * Implementation will be provided by infrastructure layer
 */
public interface UserRepository {
  
  User save(User user);
  
  Optional<User> findById(UUID id);
  
  Optional<User> findByEmail(String email);
  
  Optional<User> findByPhoneNumber(String phoneNumber);
  
  boolean existsByEmail(String email);
  
  boolean existsByPhoneNumber(String phoneNumber);
  
  List<User> findByStatus(UserStatus status);
  
  void deleteById(UUID id);
  
  long countByStatus(UserStatus status);
}