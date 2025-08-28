package com.example.loyalty.points.application.dto;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Data Transfer Objects for Earn Points operations
 */
public class EarnPointsDTO {
    
    public static class Request {
        @NotNull
        private UUID userId;
        
        @NotNull
        @Positive
        private BigDecimal transactionAmount;
        
        private String earningType = "PURCHASE";
        
        private String description;
        
        private UUID referenceId;
        
        private String userTier;
        
        private BigDecimal bonusMultiplier;

        // Constructors
        public Request() {}
        
        public Request(UUID userId, BigDecimal transactionAmount, String earningType) {
            this.userId = userId;
            this.transactionAmount = transactionAmount;
            this.earningType = earningType;
        }

        // Getters and Setters
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
        
        public BigDecimal getTransactionAmount() { return transactionAmount; }
        public void setTransactionAmount(BigDecimal transactionAmount) { this.transactionAmount = transactionAmount; }
        
        public String getEarningType() { return earningType; }
        public void setEarningType(String earningType) { this.earningType = earningType; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public UUID getReferenceId() { return referenceId; }
        public void setReferenceId(UUID referenceId) { this.referenceId = referenceId; }
        
        public String getUserTier() { return userTier; }
        public void setUserTier(String userTier) { this.userTier = userTier; }
        
        public BigDecimal getBonusMultiplier() { return bonusMultiplier; }
        public void setBonusMultiplier(BigDecimal bonusMultiplier) { this.bonusMultiplier = bonusMultiplier; }
    }
    
    public static class Response {
        private String transactionId;
        private UUID userId;
        private long pointsEarned;
        private long newBalance;
        private String message;
        private String earningType;
        private BigDecimal transactionAmount;

        // Constructors
        public Response() {}
        
        public Response(String transactionId, UUID userId, long pointsEarned, long newBalance, String message) {
            this.transactionId = transactionId;
            this.userId = userId;
            this.pointsEarned = pointsEarned;
            this.newBalance = newBalance;
            this.message = message;
        }

        // Getters and Setters
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
        
        public long getPointsEarned() { return pointsEarned; }
        public void setPointsEarned(long pointsEarned) { this.pointsEarned = pointsEarned; }
        
        public long getNewBalance() { return newBalance; }
        public void setNewBalance(long newBalance) { this.newBalance = newBalance; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getEarningType() { return earningType; }
        public void setEarningType(String earningType) { this.earningType = earningType; }
        
        public BigDecimal getTransactionAmount() { return transactionAmount; }
        public void setTransactionAmount(BigDecimal transactionAmount) { this.transactionAmount = transactionAmount; }
    }
}