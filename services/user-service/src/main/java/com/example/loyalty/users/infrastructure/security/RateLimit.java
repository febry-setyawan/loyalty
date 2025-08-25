package com.example.loyalty.users.infrastructure.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rate limiting annotation for API endpoints
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
  
  /**
   * Number of requests allowed within the time window
   */
  int limit() default 10;
  
  /**
   * Time window in seconds
   */
  int window() default 60;
  
  /**
   * Key for rate limiting (default uses IP address)
   */
  String key() default "ip";
}