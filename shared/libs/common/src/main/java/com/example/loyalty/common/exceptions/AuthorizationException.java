package com.example.loyalty.common.exceptions;

/**
 * Exception thrown when authorization fails
 */
public class AuthorizationException extends LoyaltyException {
    
    public AuthorizationException(String message) {
        super(message, "AUTHORIZATION_ERROR", 403);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, "AUTHORIZATION_ERROR", 403, cause);
    }
}