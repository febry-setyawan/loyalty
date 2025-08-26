package com.example.loyalty.points.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Objects for Point Balance operations
 */
public class PointBalanceDTO {
    
    public static class Response {
        private UUID userId;
        private long totalPoints;
        private long availablePoints;
        private long pendingPoints;
        private long lifetimeEarned;
        private long lifetimeSpent;
        private LocalDateTime lastUpdated;

        // Constructors
        public Response() {}
        
        public Response(UUID userId, long totalPoints, long availablePoints, long pendingPoints,
                       long lifetimeEarned, long lifetimeSpent, LocalDateTime lastUpdated) {
            this.userId = userId;
            this.totalPoints = totalPoints;
            this.availablePoints = availablePoints;
            this.pendingPoints = pendingPoints;
            this.lifetimeEarned = lifetimeEarned;
            this.lifetimeSpent = lifetimeSpent;
            this.lastUpdated = lastUpdated;
        }

        // Getters and Setters
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
        
        public long getTotalPoints() { return totalPoints; }
        public void setTotalPoints(long totalPoints) { this.totalPoints = totalPoints; }
        
        public long getAvailablePoints() { return availablePoints; }
        public void setAvailablePoints(long availablePoints) { this.availablePoints = availablePoints; }
        
        public long getPendingPoints() { return pendingPoints; }
        public void setPendingPoints(long pendingPoints) { this.pendingPoints = pendingPoints; }
        
        public long getLifetimeEarned() { return lifetimeEarned; }
        public void setLifetimeEarned(long lifetimeEarned) { this.lifetimeEarned = lifetimeEarned; }
        
        public long getLifetimeSpent() { return lifetimeSpent; }
        public void setLifetimeSpent(long lifetimeSpent) { this.lifetimeSpent = lifetimeSpent; }
        
        public LocalDateTime getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    }
}