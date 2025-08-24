package com.example.loyalty.common.security;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Data masking service for PII protection in logs and API responses
 * Provides various masking strategies for different data types
 */
@Service
public class DataMaskingService {

    /**
     * Mask credit card number showing only last 4 digits
     */
    public String maskCreditCard(String creditCard) {
        if (!StringUtils.hasText(creditCard) || creditCard.length() < 4) {
            return creditCard;
        }
        
        String lastFour = creditCard.substring(creditCard.length() - 4);
        return "**** **** **** " + lastFour;
    }

    /**
     * Mask email address showing first and last character of username
     */
    public String maskEmail(String email) {
        if (!StringUtils.hasText(email) || !email.contains("@")) {
            return email;
        }
        
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        String maskedUsername = username.charAt(0) + 
            "*".repeat(Math.max(0, username.length() - 2)) +
            (username.length() > 1 ? username.charAt(username.length() - 1) : "");
            
        return maskedUsername + "@" + domain;
    }

    /**
     * Mask phone number showing only last 4 digits
     */
    public String maskPhoneNumber(String phone) {
        if (!StringUtils.hasText(phone) || phone.length() < 4) {
            return phone;
        }
        
        return "*".repeat(phone.length() - 4) + phone.substring(phone.length() - 4);
    }
    
    /**
     * Mask name showing only first character and last initial
     */
    public String maskName(String name) {
        if (!StringUtils.hasText(name)) {
            return name;
        }
        
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].charAt(0) + "*".repeat(Math.max(0, parts[0].length() - 1));
        }
        
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) masked.append(" ");
            
            if (i == 0) {
                // First name - show first character
                masked.append(parts[i].charAt(0));
                if (parts[i].length() > 1) {
                    masked.append("*".repeat(parts[i].length() - 1));
                }
            } else if (i == parts.length - 1) {
                // Last name - show first character only
                masked.append(parts[i].charAt(0)).append(".");
            } else {
                // Middle names - show first character only
                masked.append(parts[i].charAt(0)).append(".");
            }
        }
        
        return masked.toString();
    }

    /**
     * Mask social security number showing only last 4 digits
     */
    public String maskSSN(String ssn) {
        if (!StringUtils.hasText(ssn) || ssn.length() < 4) {
            return ssn;
        }
        
        String lastFour = ssn.substring(ssn.length() - 4);
        return "***-**-" + lastFour;
    }

    /**
     * Mask address showing only street number and city
     */
    public String maskAddress(String address) {
        if (!StringUtils.hasText(address)) {
            return address;
        }
        
        // Simple masking - show first part (likely street number) and mask rest
        String[] parts = address.split(",");
        if (parts.length > 1) {
            String streetPart = parts[0].trim();
            String[] streetWords = streetPart.split("\\s+");
            if (streetWords.length > 0) {
                return streetWords[0] + " *** ****, " + parts[parts.length - 1].trim();
            }
        }
        
        return "*** *** ***";
    }
    
    /**
     * Generic masking that shows first and last character
     */
    public String maskGeneric(String data, int visibleStart, int visibleEnd) {
        if (!StringUtils.hasText(data) || data.length() <= visibleStart + visibleEnd) {
            return data;
        }
        
        String start = data.substring(0, visibleStart);
        String end = data.substring(data.length() - visibleEnd);
        int maskedLength = data.length() - visibleStart - visibleEnd;
        
        return start + "*".repeat(maskedLength) + end;
    }
}