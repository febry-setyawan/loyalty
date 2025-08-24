package com.example.loyalty.common.security;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SecurityEventTest {

    @Test
    void shouldBuildSecurityEventWithBuilder() {
        Instant timestamp = Instant.now();
        
        SecurityEvent event = SecurityEvent.builder()
            .type("TEST_EVENT")
            .userId("user123")
            .ipAddress("192.168.1.1")
            .userAgent("Mozilla/5.0")
            .resource("/test")
            .action("TEST_ACTION")
            .result("SUCCESS")
            .reason("Test reason")
            .timestamp(timestamp)
            .sessionId("session123")
            .critical(true)
            .build();
            
        assertEquals("TEST_EVENT", event.getType());
        assertEquals("user123", event.getUserId());
        assertEquals("192.168.1.1", event.getIpAddress());
        assertEquals("Mozilla/5.0", event.getUserAgent());
        assertEquals("/test", event.getResource());
        assertEquals("TEST_ACTION", event.getAction());
        assertEquals("SUCCESS", event.getResult());
        assertEquals("Test reason", event.getReason());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("session123", event.getSessionId());
        assertTrue(event.isCritical());
    }

    @Test
    void shouldRequireEventType() {
        assertThrows(IllegalArgumentException.class, () -> {
            SecurityEvent.builder()
                .userId("user123")
                .action("TEST")
                .build();
        });
    }

    @Test
    void shouldAutoSetTimestampIfNotProvided() {
        Instant before = Instant.now();
        
        SecurityEvent event = SecurityEvent.builder()
            .type("TEST_EVENT")
            .build();
            
        Instant after = Instant.now();
        
        assertNotNull(event.getTimestamp());
        assertTrue(event.getTimestamp().isAfter(before.minusSeconds(1)));
        assertTrue(event.getTimestamp().isBefore(after.plusSeconds(1)));
    }

    @Test
    void shouldAllowPartialEventData() {
        SecurityEvent event = SecurityEvent.builder()
            .type("MINIMAL_EVENT")
            .userId("user123")
            .build();
            
        assertEquals("MINIMAL_EVENT", event.getType());
        assertEquals("user123", event.getUserId());
        assertNotNull(event.getTimestamp());
        assertNull(event.getIpAddress());
        assertNull(event.getReason());
        assertFalse(event.isCritical()); // Default false
    }

    @Test
    void shouldHandleNullValues() {
        SecurityEvent event = SecurityEvent.builder()
            .type("TEST_EVENT")
            .userId(null)
            .ipAddress(null)
            .reason(null)
            .build();
            
        assertEquals("TEST_EVENT", event.getType());
        assertNull(event.getUserId());
        assertNull(event.getIpAddress());
        assertNull(event.getReason());
    }
}