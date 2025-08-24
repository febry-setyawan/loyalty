package com.example.loyalty.common.security;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Basic fraud detection service for point transactions
 * Implements velocity checks, pattern analysis, and anomaly detection
 */
@Service
public class FraudDetectionService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final SecurityAuditService auditService;
    
    public FraudDetectionService(RedisTemplate<String, String> redisTemplate, 
                                SecurityAuditService auditService) {
        this.redisTemplate = redisTemplate;
        this.auditService = auditService;
    }
    
    /**
     * Assess transaction for fraud risk
     */
    public FraudAssessment assessTransaction(TransactionRequest request) {
        FraudScore score = calculateFraudScore(request);
        
        if (score.getScore() > 0.8) {
            // Block transaction
            blockTransaction(request, "High fraud score: " + score.getScore());
            return FraudAssessment.blocked(score);
        } else if (score.getScore() > 0.5) {
            // Require additional verification
            return FraudAssessment.requiresVerification(score);
        }
        
        return FraudAssessment.approved(score);
    }
    
    private FraudScore calculateFraudScore(TransactionRequest request) {
        double score = 0.0;
        List<String> reasons = new ArrayList<>();
        
        // Check velocity patterns
        if (isHighVelocity(request.getUserId())) {
            score += 0.3;
            reasons.add("High transaction velocity");
        }
        
        // Check unusual time patterns
        if (isUnusualTime(request.getTimestamp())) {
            score += 0.2;
            reasons.add("Unusual transaction time");
        }
        
        // Check large point amounts
        if (isLargeAmount(request.getPointAmount())) {
            score += 0.3;
            reasons.add("Large point amount");
        }
        
        // Check device/location patterns (simplified)
        if (isNewDevice(request.getUserId(), request.getDeviceFingerprint())) {
            score += 0.3;
            reasons.add("New device");
        }
        
        return new FraudScore(Math.min(score, 1.0), reasons);
    }
    
    private boolean isHighVelocity(String userId) {
        String key = "transaction_velocity:" + userId;
        
        try {
            String countStr = redisTemplate.opsForValue().get(key);
            int count = countStr != null ? Integer.parseInt(countStr) : 0;
            
            // Increment counter
            Long newCount = redisTemplate.opsForValue().increment(key);
            
            // Set expiration for 10 minutes window
            if (newCount == 1) {
                redisTemplate.expire(key, 10, TimeUnit.MINUTES);
            }
            
            // Flag as high velocity if more than 5 transactions in 10 minutes
            return newCount > 5;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isUnusualTime(Instant timestamp) {
        LocalTime time = LocalTime.ofInstant(timestamp, java.time.ZoneId.systemDefault());
        
        // Flag transactions between 1 AM and 6 AM as unusual
        return time.isAfter(LocalTime.of(1, 0)) && time.isBefore(LocalTime.of(6, 0));
    }
    
    private boolean isLargeAmount(int pointAmount) {
        // Flag transactions above 10,000 points as potentially fraudulent
        return pointAmount > 10000;
    }
    
    private boolean isNewDevice(String userId, String deviceFingerprint) {
        if (deviceFingerprint == null) return false;
        
        String key = "user_devices:" + userId;
        
        try {
            String devices = redisTemplate.opsForValue().get(key);
            if (devices == null) {
                // First time seeing this user, store device
                redisTemplate.opsForValue().set(key, deviceFingerprint, Duration.ofDays(30));
                return true;
            }
            
            // Check if device fingerprint is in known devices
            return !devices.contains(deviceFingerprint);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    private void blockTransaction(TransactionRequest request, String reason) {
        // Log the blocked transaction
        auditService.logPointTransaction(
            request.getUserId(),
            request.getTransactionType(),
            request.getPointAmount(),
            "BLOCKED",
            reason
        );
    }
    
    /**
     * Transaction request data structure
     */
    public static class TransactionRequest {
        private String userId;
        private String transactionType;
        private int pointAmount;
        private Instant timestamp;
        private String deviceFingerprint;
        private String ipAddress;
        
        public TransactionRequest(String userId, String transactionType, int pointAmount, 
                                Instant timestamp, String deviceFingerprint, String ipAddress) {
            this.userId = userId;
            this.transactionType = transactionType;
            this.pointAmount = pointAmount;
            this.timestamp = timestamp;
            this.deviceFingerprint = deviceFingerprint;
            this.ipAddress = ipAddress;
        }
        
        // Getters
        public String getUserId() { return userId; }
        public String getTransactionType() { return transactionType; }
        public int getPointAmount() { return pointAmount; }
        public Instant getTimestamp() { return timestamp; }
        public String getDeviceFingerprint() { return deviceFingerprint; }
        public String getIpAddress() { return ipAddress; }
    }
    
    /**
     * Fraud score with reasons
     */
    public static class FraudScore {
        private final double score;
        private final List<String> reasons;
        
        public FraudScore(double score, List<String> reasons) {
            this.score = score;
            this.reasons = reasons;
        }
        
        public double getScore() { return score; }
        public List<String> getReasons() { return reasons; }
    }
    
    /**
     * Fraud assessment result
     */
    public static class FraudAssessment {
        private final FraudResult result;
        private final FraudScore score;
        
        private FraudAssessment(FraudResult result, FraudScore score) {
            this.result = result;
            this.score = score;
        }
        
        public static FraudAssessment approved(FraudScore score) {
            return new FraudAssessment(FraudResult.APPROVED, score);
        }
        
        public static FraudAssessment requiresVerification(FraudScore score) {
            return new FraudAssessment(FraudResult.REQUIRES_VERIFICATION, score);
        }
        
        public static FraudAssessment blocked(FraudScore score) {
            return new FraudAssessment(FraudResult.BLOCKED, score);
        }
        
        public FraudResult getResult() { return result; }
        public FraudScore getScore() { return score; }
        
        public enum FraudResult {
            APPROVED,
            REQUIRES_VERIFICATION,
            BLOCKED
        }
    }
}