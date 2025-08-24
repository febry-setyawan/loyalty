package com.example.loyalty.common.security;

import com.example.loyalty.common.exceptions.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenServiceTest {
    
    private JwtTokenService jwtTokenService;
    
    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenService(
            "test-secret-key-for-jwt-signing-minimum-256-bits",
            "test-refresh-secret-key-for-jwt-signing-minimum-256-bits",
            900000L,  // 15 minutes
            604800000L,  // 7 days
            "loyalty-test"
        );
    }

    @Test
    void shouldGenerateValidAccessToken() {
        // Given
        String userId = "user123";
        String email = "test@example.com";
        List<String> roles = List.of("USER", "CUSTOMER");

        // When
        String token = jwtTokenService.generateAccessToken(userId, email, roles);

        // Then
        assertNotNull(token);
        assertTrue(token.length() > 100); // JWT tokens are long
        
        // Should be able to validate the token
        JwtTokenService.JwtClaims claims = jwtTokenService.validateAccessToken(token);
        assertEquals(userId, claims.getUserId());
        assertEquals(email, claims.getEmail());
        assertEquals(roles, claims.getRoles());
    }

    @Test
    void shouldGenerateValidRefreshToken() {
        // Given
        String userId = "user123";

        // When
        String token = jwtTokenService.generateRefreshToken(userId);

        // Then
        assertNotNull(token);
        assertTrue(token.length() > 100);
        
        // Should be able to validate the token
        JwtTokenService.JwtClaims claims = jwtTokenService.validateRefreshToken(token);
        assertEquals(userId, claims.getUserId());
        assertNull(claims.getEmail()); // Refresh tokens don't contain email
        assertNull(claims.getRoles()); // Refresh tokens don't contain roles
    }

    @Test
    void shouldThrowExceptionForInvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When & Then
        assertThrows(AuthenticationException.class, () -> 
            jwtTokenService.validateAccessToken(invalidToken));
    }

    @Test
    void shouldExtractUserIdUnsafely() {
        // Given
        String userId = "user123";
        String token = jwtTokenService.generateAccessToken(userId, "test@example.com", List.of("USER"));

        // When
        String extractedUserId = jwtTokenService.extractUserIdUnsafe(token);

        // Then
        assertEquals(userId, extractedUserId);
    }

    @Test
    void shouldDetectExpiredToken() {
        // Create service with very short expiration
        JwtTokenService shortExpiryService = new JwtTokenService(
            "test-secret-key-for-jwt-signing-minimum-256-bits",
            "test-refresh-secret-key-for-jwt-signing-minimum-256-bits",
            1L,  // 1 millisecond
            1L,  // 1 millisecond
            "loyalty-test"
        );

        // Given
        String token = shortExpiryService.generateAccessToken("user123", "test@example.com", List.of("USER"));

        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When & Then
        assertTrue(shortExpiryService.isTokenExpired(token));
    }
}