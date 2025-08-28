package com.example.loyalty.points.application.dto;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for referral point requests
 */
public class ReferralRequest {
    
    @NotNull
    private UUID userId;
    
    @Positive
    private BigDecimal points = BigDecimal.valueOf(500); // Default referral points
    
    private UUID referrerId;
    private String referralCode;

    // Constructors
    public ReferralRequest() {}
    
    public ReferralRequest(UUID userId) {
        this.userId = userId;
    }
    
    public ReferralRequest(UUID userId, BigDecimal points) {
        this.userId = userId;
        this.points = points;
    }

    // Getters and Setters
    public UUID getUserId() { 
        return userId; 
    }
    
    public void setUserId(UUID userId) { 
        this.userId = userId; 
    }
    
    public BigDecimal getPoints() { 
        return points; 
    }
    
    public void setPoints(BigDecimal points) { 
        this.points = points; 
    }
    
    public UUID getReferrerId() { 
        return referrerId; 
    }
    
    public void setReferrerId(UUID referrerId) { 
        this.referrerId = referrerId; 
    }
    
    public String getReferralCode() { 
        return referralCode; 
    }
    
    public void setReferralCode(String referralCode) { 
        this.referralCode = referralCode; 
    }
}