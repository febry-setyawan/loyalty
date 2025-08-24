package com.example.loyalty.common.validation;

import com.example.loyalty.common.exceptions.ValidationException;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Common validation utilities for all services
 * Provides consistent validation logic across the system
 */
@Component
public class ValidationUtils {
    
    private final Validator validator;
    
    // Common validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+[1-9]\\d{1,14}$"
    );
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
        Pattern.CASE_INSENSITIVE
    );

    public ValidationUtils(Validator validator) {
        this.validator = validator;
    }

    /**
     * Validate object using Bean Validation annotations
     */
    public <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        
        if (!violations.isEmpty()) {
            List<String> errors = violations.stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());
            
            throw new ValidationException("Validation failed: " + String.join(", ", errors));
        }
    }

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate phone number format (international format)
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validate password strength
     */
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Validate UUID format
     */
    public static boolean isValidUuid(String uuid) {
        return uuid != null && UUID_PATTERN.matcher(uuid).matches();
    }

    /**
     * Require non-null value
     */
    public static <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " cannot be null", fieldName);
        }
        return value;
    }

    /**
     * Require non-empty string
     */
    public static String requireNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty", fieldName);
        }
        return value.trim();
    }

    /**
     * Validate string length
     */
    public static String requireLength(String value, String fieldName, int minLength, int maxLength) {
        requireNonEmpty(value, fieldName);
        
        if (value.length() < minLength || value.length() > maxLength) {
            throw new ValidationException(
                String.format("%s must be between %d and %d characters", fieldName, minLength, maxLength),
                fieldName
            );
        }
        
        return value;
    }

    /**
     * Validate positive number
     */
    public static <T extends Number> T requirePositive(T value, String fieldName) {
        requireNonNull(value, fieldName);
        
        if (value.doubleValue() <= 0) {
            throw new ValidationException(fieldName + " must be positive", fieldName);
        }
        
        return value;
    }

    /**
     * Validate email and throw exception if invalid
     */
    public static String requireValidEmail(String email) {
        requireNonEmpty(email, "email");
        
        String trimmedEmail = email.trim();
        if (!isValidEmail(trimmedEmail)) {
            throw new ValidationException("Invalid email format", "email");
        }
        
        return trimmedEmail.toLowerCase();
    }

    /**
     * Validate phone and throw exception if invalid
     */
    public static String requireValidPhone(String phone) {
        if (phone != null && !phone.trim().isEmpty() && !isValidPhone(phone)) {
            throw new ValidationException("Invalid phone format. Use international format (+1234567890)", "phone");
        }
        
        return phone != null ? phone.trim() : null;
    }

    /**
     * Validate password and throw exception if invalid
     */
    public static String requireValidPassword(String password) {
        requireNonEmpty(password, "password");
        
        if (!isValidPassword(password)) {
            throw new ValidationException(
                "Password must contain at least 8 characters with uppercase, lowercase, number and special character",
                "password"
            );
        }
        
        return password;
    }
}