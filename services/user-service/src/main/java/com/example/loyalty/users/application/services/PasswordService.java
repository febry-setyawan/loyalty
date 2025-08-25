package com.example.loyalty.users.application.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Password service for handling password operations
 */
@Service
public class PasswordService {
  
  private final PasswordEncoder passwordEncoder;
  
  public PasswordService() {
    this.passwordEncoder = new BCryptPasswordEncoder(12);
  }
  
  public String hashPassword(String password) {
    return passwordEncoder.encode(password);
  }
  
  public boolean verifyPassword(String password, String hashedPassword) {
    return passwordEncoder.matches(password, hashedPassword);
  }
}