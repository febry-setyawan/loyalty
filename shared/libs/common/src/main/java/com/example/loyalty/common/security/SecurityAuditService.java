package com.example.loyalty.common.security;

import com.example.loyalty.common.logging.StructuredLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Security audit service for comprehensive security event logging Provides audit trail
 * functionality for security events and compliance
 */
@Component
public class SecurityAuditService {

  private final StructuredLogger logger;
  private final ObjectMapper objectMapper;

  public SecurityAuditService(StructuredLogger logger, ObjectMapper objectMapper) {
    this.logger = logger;
    this.objectMapper = objectMapper;
  }

  /** Log general security event */
  public void logSecurityEvent(SecurityEvent event) {
    try {
      Map<String, Object> logData = new HashMap<>();
      logData.put("eventType", event.getType());
      logData.put("userId", event.getUserId());
      logData.put("ipAddress", event.getIpAddress());
      logData.put("userAgent", event.getUserAgent());
      logData.put("resource", event.getResource());
      logData.put("action", event.getAction());
      logData.put("result", event.getResult());
      logData.put("timestamp", event.getTimestamp());
      logData.put("sessionId", event.getSessionId());
      logData.put("reason", event.getReason());
      logData.put("critical", event.isCritical());

      if (event.isCritical()) {
        logger.error(
            "Critical Security Event",
            "eventType",
            event.getType(),
            "userId",
            event.getUserId(),
            "result",
            event.getResult(),
            "reason",
            event.getReason());
      } else {
        logger.info(
            "Security Event",
            "eventType",
            event.getType(),
            "userId",
            event.getUserId(),
            "result",
            event.getResult());
      }

    } catch (Exception e) {
      logger.error(
          "Failed to log security event",
          e,
          "eventType",
          event.getType(),
          "userId",
          event.getUserId());
    }
  }

  /** Handle successful authentication events */
  @EventListener
  public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
    Authentication auth = event.getAuthentication();

    SecurityEvent securityEvent =
        SecurityEvent.builder()
            .type("AUTHENTICATION_SUCCESS")
            .userId(auth.getName())
            .ipAddress(getClientIpAddress())
            .userAgent(getUserAgent())
            .resource("/auth")
            .action("LOGIN")
            .result("SUCCESS")
            .timestamp(Instant.now())
            .sessionId(getSessionId())
            .critical(false)
            .build();

    logSecurityEvent(securityEvent);
  }

  /** Handle failed authentication events (manual call from authentication controller) */
  public void handleAuthenticationFailure(String username, String reason) {
    SecurityEvent securityEvent =
        SecurityEvent.builder()
            .type("AUTHENTICATION_FAILURE")
            .userId(username != null ? username : "unknown")
            .ipAddress(getClientIpAddress())
            .userAgent(getUserAgent())
            .resource("/auth")
            .action("LOGIN")
            .result("FAILURE")
            .reason(reason)
            .timestamp(Instant.now())
            .sessionId(getSessionId())
            .critical(true)
            .build();

    logSecurityEvent(securityEvent);
  }

  /** Log data access events for sensitive operations */
  public void logDataAccess(String userId, String resource, String action, String result) {
    SecurityEvent event =
        SecurityEvent.builder()
            .type("DATA_ACCESS")
            .userId(userId)
            .ipAddress(getClientIpAddress())
            .userAgent(getUserAgent())
            .resource(resource)
            .action(action)
            .result(result)
            .timestamp(Instant.now())
            .sessionId(getSessionId())
            .critical(false)
            .build();

    logSecurityEvent(event);
  }

  /** Log point transaction events for fraud detection */
  public void logPointTransaction(
      String userId, String transactionType, int points, String result, String reason) {
    SecurityEvent event =
        SecurityEvent.builder()
            .type("POINT_TRANSACTION")
            .userId(userId)
            .ipAddress(getClientIpAddress())
            .userAgent(getUserAgent())
            .resource("/points")
            .action(transactionType)
            .result(result)
            .reason(reason)
            .timestamp(Instant.now())
            .sessionId(getSessionId())
            .critical("BLOCKED".equals(result))
            .build();

    logSecurityEvent(event);
  }

  /** Log administrative actions */
  public void logAdminAction(
      String adminUserId, String targetUserId, String action, String result, String reason) {
    SecurityEvent event =
        SecurityEvent.builder()
            .type("ADMIN_ACTION")
            .userId(adminUserId)
            .ipAddress(getClientIpAddress())
            .userAgent(getUserAgent())
            .resource("/admin")
            .action(action)
            .result(result)
            .reason(reason + " (target: " + targetUserId + ")")
            .timestamp(Instant.now())
            .sessionId(getSessionId())
            .critical(action.contains("DELETE") || action.contains("SUSPEND"))
            .build();

    logSecurityEvent(event);
  }

  private String getClientIpAddress() {
    try {
      ServletRequestAttributes attributes =
          (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (attributes != null) {
        HttpServletRequest request = attributes.getRequest();

        // Check for forwarded IP headers (load balancer/proxy)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
          return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIp)) {
          return xRealIp;
        }

        return request.getRemoteAddr();
      }
    } catch (Exception e) {
      // Ignore errors and return unknown
    }
    return "unknown";
  }

  private String getUserAgent() {
    try {
      ServletRequestAttributes attributes =
          (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (attributes != null) {
        HttpServletRequest request = attributes.getRequest();
        return request.getHeader("User-Agent");
      }
    } catch (Exception e) {
      // Ignore errors and return unknown
    }
    return "unknown";
  }

  private String getSessionId() {
    try {
      ServletRequestAttributes attributes =
          (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (attributes != null) {
        HttpServletRequest request = attributes.getRequest();
        return request.getSession(false) != null ? request.getSession().getId() : "no-session";
      }
    } catch (Exception e) {
      // Ignore errors and return unknown
    }
    return "unknown";
  }
}
