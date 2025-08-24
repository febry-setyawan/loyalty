package com.example.loyalty.common.exceptions;

import java.time.LocalDateTime;

/**
 * Base exception class for all loyalty system errors Provides consistent error structure across all
 * services
 */
public abstract class LoyaltyException extends RuntimeException {
  private final String code;
  private final int statusCode;
  private final LocalDateTime timestamp;

  protected LoyaltyException(String message, String code, int statusCode) {
    super(message);
    this.code = code;
    this.statusCode = statusCode;
    this.timestamp = LocalDateTime.now();
  }

  protected LoyaltyException(String message, String code, int statusCode, Throwable cause) {
    super(message, cause);
    this.code = code;
    this.statusCode = statusCode;
    this.timestamp = LocalDateTime.now();
  }

  public String getCode() {
    return code;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }
}
