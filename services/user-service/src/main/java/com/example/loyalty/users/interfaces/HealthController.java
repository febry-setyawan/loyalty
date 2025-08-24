package com.example.loyalty.users.interfaces;

import com.example.loyalty.common.response.ApiResponse;
import com.example.loyalty.common.validation.ValidationUtils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Health Check Controller for User Service Provides health endpoints for service monitoring */
@RestController
public class HealthController {

  @Autowired private JdbcTemplate jdbcTemplate;

  @GetMapping("/health")
  public ResponseEntity<Map<String, Object>> health() {
    Map<String, Object> response = new HashMap<>();

    try {
      // Check database connectivity
      String result = jdbcTemplate.queryForObject("SELECT 'OK'", String.class);

      response.put("status", "UP");
      response.put("service", "user-service");
      response.put("version", "1.0.0");
      response.put("database", "Connected");
      response.put("timestamp", System.currentTimeMillis());

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("status", "DOWN");
      response.put("service", "user-service");
      response.put("version", "1.0.0");
      response.put("database", "Error: " + e.getMessage());
      response.put("timestamp", System.currentTimeMillis());

      return ResponseEntity.status(503).body(response);
    }
  }

  @GetMapping("/health/ready")
  public ResponseEntity<Map<String, String>> ready() {
    Map<String, String> response = new HashMap<>();
    response.put("status", "READY");
    response.put("service", "user-service");
    return ResponseEntity.ok(response);
  }

  @GetMapping("/health/live")
  public ResponseEntity<Map<String, String>> live() {
    Map<String, String> response = new HashMap<>();
    response.put("status", "ALIVE");
    response.put("service", "user-service");
    return ResponseEntity.ok(response);
  }

  /** Test endpoint to demonstrate common library integration */
  @GetMapping("/test-validation")
  public ResponseEntity<ApiResponse<String>> testValidation(@RequestParam(required = false) String email) {
    try {
      if (email != null) {
        // Use ValidationUtils from common library
        String validEmail = ValidationUtils.requireValidEmail(email);
        return ResponseEntity.ok(
            ApiResponse.success(
                "Valid email: " + validEmail,
                "Email validation successful using common library"));
      } else {
        return ResponseEntity.ok(
            ApiResponse.success(
                "Common library integration working",
                "ValidationUtils and ApiResponse are available from loyalty-common"));
      }
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("VALIDATION_ERROR", e.getMessage(), null));
    }
  }
}
