package com.example.loyalty.common.exceptions;

/** Exception thrown when authentication fails */
public class AuthenticationException extends LoyaltyException {

  public AuthenticationException(String message) {
    super(message, "AUTHENTICATION_ERROR", 401);
  }

  public AuthenticationException(String message, Throwable cause) {
    super(message, "AUTHENTICATION_ERROR", 401, cause);
  }
}
