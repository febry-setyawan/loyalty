package com.example.loyalty.common.exceptions;

/**
 * Exception thrown when input validation fails
 */
public class ValidationException extends LoyaltyException {
    private final String field;

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", 400);
        this.field = null;
    }

    public ValidationException(String message, String field) {
        super(message, "VALIDATION_ERROR", 400);
        this.field = field;
    }

    public ValidationException(String message, String field, Throwable cause) {
        super(message, "VALIDATION_ERROR", 400, cause);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}