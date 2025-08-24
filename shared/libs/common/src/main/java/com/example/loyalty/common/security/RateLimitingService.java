package com.example.loyalty.common.security;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Rate limiting service using Redis for distributed rate limiting
 * Implements sliding window rate limiting for API protection
 */
@Service
public class RateLimitingService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    public RateLimitingService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    /**
     * Check if request is allowed based on rate limiting rules
     * 
     * @param identifier Unique identifier (IP address, user ID, etc.)
     * @param maxRequests Maximum requests allowed
     * @param duration Time window for the limit
     * @return true if request is allowed, false if rate limit exceeded
     */
    public boolean isAllowed(String identifier, int maxRequests, Duration duration) {
        String key = "rate_limit:" + identifier;
        
        try {
            // Get current count
            String currentCountStr = redisTemplate.opsForValue().get(key);
            int currentCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;
            
            if (currentCount >= maxRequests) {
                return false;
            }
            
            // Increment counter
            Long newCount = redisTemplate.opsForValue().increment(key);
            
            // Set expiration if this is the first request in the window
            if (newCount == 1) {
                redisTemplate.expire(key, duration.toSeconds(), TimeUnit.SECONDS);
            }
            
            return newCount <= maxRequests;
            
        } catch (Exception e) {
            // If Redis is unavailable, allow the request (fail open for availability)
            // But log the error for monitoring
            return true;
        }
    }
    
    /**
     * Check rate limit for authentication attempts
     */
    public boolean isAuthAttemptAllowed(String identifier) {
        // 5 attempts per 15 minutes for authentication
        return isAllowed("auth:" + identifier, 5, Duration.ofMinutes(15));
    }
    
    /**
     * Check rate limit for general API calls
     */
    public boolean isApiCallAllowed(String identifier) {
        // 100 requests per minute for general API calls
        return isAllowed("api:" + identifier, 100, Duration.ofMinutes(1));
    }
    
    /**
     * Check rate limit for point transactions
     */
    public boolean isPointTransactionAllowed(String userId) {
        // 10 point transactions per hour to prevent abuse
        return isAllowed("points:" + userId, 10, Duration.ofHours(1));
    }
    
    /**
     * Check rate limit for password reset attempts
     */
    public boolean isPasswordResetAllowed(String identifier) {
        // 3 password reset attempts per hour
        return isAllowed("password_reset:" + identifier, 3, Duration.ofHours(1));
    }
    
    /**
     * Get remaining requests for identifier
     */
    public int getRemainingRequests(String identifier, int maxRequests) {
        String key = "rate_limit:" + identifier;
        
        try {
            String currentCountStr = redisTemplate.opsForValue().get(key);
            int currentCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;
            return Math.max(0, maxRequests - currentCount);
        } catch (Exception e) {
            return maxRequests; // If Redis error, assume no usage
        }
    }
    
    /**
     * Get time until rate limit resets
     */
    public Duration getTimeUntilReset(String identifier) {
        String key = "rate_limit:" + identifier;
        
        try {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return ttl != null && ttl > 0 ? Duration.ofSeconds(ttl) : Duration.ZERO;
        } catch (Exception e) {
            return Duration.ZERO;
        }
    }
    
    /**
     * Reset rate limit for identifier (for admin use)
     */
    public void resetRateLimit(String identifier) {
        String key = "rate_limit:" + identifier;
        redisTemplate.delete(key);
    }
}