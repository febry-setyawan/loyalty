package com.example.loyalty.users.infrastructure.security;

import com.example.loyalty.common.exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

/**
 * Rate limiting aspect using Redis for distributed rate limiting
 */
@Aspect
@Component
@ConditionalOnClass(RedisTemplate.class)
@ConditionalOnBean(RedisTemplate.class)
public class RateLimitAspect {
  
  private static final Logger logger = LoggerFactory.getLogger(RateLimitAspect.class);
  
  private final RedisTemplate<String, String> redisTemplate;
  
  public RateLimitAspect(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }
  
  @Around("@annotation(rateLimit)")
  public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
    HttpServletRequest request = getCurrentRequest();
    if (request == null) {
      return joinPoint.proceed();
    }
    
    String key = generateKey(request, rateLimit);
    String rateLimitKey = "rate_limit:" + key;
    
    try {
      // Get current count
      String currentCountStr = redisTemplate.opsForValue().get(rateLimitKey);
      int currentCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;
      
      if (currentCount >= rateLimit.limit()) {
        logger.warn("Rate limit exceeded for key: {} (count: {}, limit: {})", key, currentCount, rateLimit.limit());
        throw new ValidationException("Rate limit exceeded. Please try again later.");
      }
      
      // Increment counter
      if (currentCount == 0) {
        // Set with expiration
        redisTemplate.opsForValue().set(rateLimitKey, "1", Duration.ofSeconds(rateLimit.window()));
      } else {
        // Increment existing
        redisTemplate.opsForValue().increment(rateLimitKey);
      }
      
      return joinPoint.proceed();
      
    } catch (Exception e) {
      if (e instanceof ValidationException) {
        throw e;
      }
      // If Redis is not available, allow the request to proceed
      logger.warn("Rate limiting failed, allowing request: {}", e.getMessage());
      return joinPoint.proceed();
    }
  }
  
  private HttpServletRequest getCurrentRequest() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    return attributes != null ? attributes.getRequest() : null;
  }
  
  private String generateKey(HttpServletRequest request, RateLimit rateLimit) {
    if ("ip".equals(rateLimit.key())) {
      String xForwardedFor = request.getHeader("X-Forwarded-For");
      return xForwardedFor != null ? xForwardedFor.split(",")[0].trim() : request.getRemoteAddr();
    }
    return rateLimit.key();
  }
}