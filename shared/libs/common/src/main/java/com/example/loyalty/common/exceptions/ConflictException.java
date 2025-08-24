package com.example.loyalty.common.exceptions;

/**
 * Exception thrown when there's a conflict (e.g., duplicate email)
 */
public class ConflictException extends LoyaltyException {
    
    public ConflictException(String message) {
        super(message, "CONFLICT_ERROR", 409);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, "CONFLICT_ERROR", 409, cause);
    }
}