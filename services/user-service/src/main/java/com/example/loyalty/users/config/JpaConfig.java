package com.example.loyalty.users.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA configuration for the user service
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}