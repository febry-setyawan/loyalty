package com.example.loyalty.admin.interfaces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller for User Service
 * Provides health endpoints for service monitoring
 */
@RestController
public class HealthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check database connectivity
            String result = jdbcTemplate.queryForObject("SELECT 'OK'", String.class);
            
            response.put("status", "UP");
            response.put("service", "admin-service");
            response.put("version", "1.0.0");
            response.put("database", "Connected");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("service", "admin-service");
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
        response.put("service", "admin-service");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health/live")
    public ResponseEntity<Map<String, String>> live() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ALIVE");
        response.put("service", "admin-service");
        return ResponseEntity.ok(response);
    }
}