package com.example.loyalty.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * User Service Application for Loyalty System Handles user management, authentication, and
 * user-related operations
 */
@SpringBootApplication(scanBasePackages = {
    "com.example.loyalty.users",
    "com.example.loyalty.common"
})
public class UserServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(UserServiceApplication.class, args);
  }
}
