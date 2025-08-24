package com.example.loyalty.common.security;

import java.time.Instant;

/**
 * Security event data structure for audit logging
 * Contains all necessary information for security event tracking
 */
public class SecurityEvent {
    
    private String type;
    private String userId;
    private String ipAddress;
    private String userAgent;
    private String resource;
    private String action;
    private String result;
    private String reason;
    private Instant timestamp;
    private String sessionId;
    private boolean critical;
    
    // Private constructor - use builder pattern
    private SecurityEvent() {}
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters
    public String getType() { return type; }
    public String getUserId() { return userId; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public String getResource() { return resource; }
    public String getAction() { return action; }
    public String getResult() { return result; }
    public String getReason() { return reason; }
    public Instant getTimestamp() { return timestamp; }
    public String getSessionId() { return sessionId; }
    public boolean isCritical() { return critical; }
    
    /**
     * Builder pattern for SecurityEvent
     */
    public static class Builder {
        private final SecurityEvent event = new SecurityEvent();
        
        public Builder type(String type) {
            event.type = type;
            return this;
        }
        
        public Builder userId(String userId) {
            event.userId = userId;
            return this;
        }
        
        public Builder ipAddress(String ipAddress) {
            event.ipAddress = ipAddress;
            return this;
        }
        
        public Builder userAgent(String userAgent) {
            event.userAgent = userAgent;
            return this;
        }
        
        public Builder resource(String resource) {
            event.resource = resource;
            return this;
        }
        
        public Builder action(String action) {
            event.action = action;
            return this;
        }
        
        public Builder result(String result) {
            event.result = result;
            return this;
        }
        
        public Builder reason(String reason) {
            event.reason = reason;
            return this;
        }
        
        public Builder timestamp(Instant timestamp) {
            event.timestamp = timestamp;
            return this;
        }
        
        public Builder sessionId(String sessionId) {
            event.sessionId = sessionId;
            return this;
        }
        
        public Builder critical(boolean critical) {
            event.critical = critical;
            return this;
        }
        
        public SecurityEvent build() {
            if (event.type == null) {
                throw new IllegalArgumentException("Event type is required");
            }
            if (event.timestamp == null) {
                event.timestamp = Instant.now();
            }
            return event;
        }
    }
}