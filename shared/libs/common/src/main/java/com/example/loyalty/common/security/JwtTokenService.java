package com.example.loyalty.common.security;

import com.example.loyalty.common.exceptions.AuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * JWT token service for authentication and authorization Provides consistent token generation and
 * validation across all services
 */
@Service
public class JwtTokenService {

  private final SecretKey accessTokenKey;
  private final SecretKey refreshTokenKey;
  private final long accessTokenExpiration;
  private final long refreshTokenExpiration;
  private final String issuer;

  public JwtTokenService(
      @Value("${app.jwt.secret:default-secret-key-change-in-production}") String jwtSecret,
      @Value("${app.jwt.refresh-secret:default-refresh-secret-change-in-production}")
          String refreshSecret,
      @Value("${app.jwt.access-expiration:900000}") long accessTokenExpiration, // 15 minutes
      @Value("${app.jwt.refresh-expiration:604800000}") long refreshTokenExpiration, // 7 days
      @Value("${app.jwt.issuer:loyalty-system}") String issuer) {

    this.accessTokenKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    this.refreshTokenKey = Keys.hmacShaKeyFor(refreshSecret.getBytes());
    this.accessTokenExpiration = accessTokenExpiration;
    this.refreshTokenExpiration = refreshTokenExpiration;
    this.issuer = issuer;
  }

  /** Generate access token for authenticated user */
  public String generateAccessToken(String userId, String email, List<String> roles) {
    Date expirationDate = new Date(System.currentTimeMillis() + accessTokenExpiration);

    return Jwts.builder()
        .subject(userId)
        .claim("email", email)
        .claim("roles", roles)
        .issuer(issuer)
        .audience()
        .add("loyalty-users")
        .and()
        .issuedAt(new Date())
        .expiration(expirationDate)
        .signWith(accessTokenKey)
        .compact();
  }

  /** Generate refresh token for user */
  public String generateRefreshToken(String userId) {
    Date expirationDate = new Date(System.currentTimeMillis() + refreshTokenExpiration);

    return Jwts.builder()
        .subject(userId)
        .issuer(issuer)
        .issuedAt(new Date())
        .expiration(expirationDate)
        .signWith(refreshTokenKey)
        .compact();
  }

  /** Validate and parse access token */
  public JwtClaims validateAccessToken(String token) {
    try {
      Claims claims =
          Jwts.parser()
              .verifyWith(accessTokenKey)
              .requireIssuer(issuer)
              .build()
              .parseSignedClaims(token)
              .getPayload();

      return new JwtClaims(
          claims.getSubject(),
          claims.get("email", String.class),
          claims.get("roles", List.class),
          claims.getIssuedAt(),
          claims.getExpiration());
    } catch (JwtException ex) {
      throw new AuthenticationException("Invalid access token: " + ex.getMessage());
    }
  }

  /** Validate and parse refresh token */
  public JwtClaims validateRefreshToken(String token) {
    try {
      Claims claims =
          Jwts.parser()
              .verifyWith(refreshTokenKey)
              .requireIssuer(issuer)
              .build()
              .parseSignedClaims(token)
              .getPayload();

      return new JwtClaims(
          claims.getSubject(),
          null, // Refresh tokens don't contain email
          null, // Refresh tokens don't contain roles
          claims.getIssuedAt(),
          claims.getExpiration());
    } catch (JwtException ex) {
      throw new AuthenticationException("Invalid refresh token: " + ex.getMessage());
    }
  }

  /** Extract user ID from token without validation (for logging purposes) */
  public String extractUserIdUnsafe(String token) {
    try {
      // Parse without verification for logging/metrics
      String[] parts = token.split("\\.");
      if (parts.length != 3) {
        return "unknown";
      }

      // Decode payload (second part)
      String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));

      // Simple JSON parsing to extract subject
      if (payload.contains("\"sub\"")) {
        int start = payload.indexOf("\"sub\":\"") + 7;
        int end = payload.indexOf("\"", start);
        if (start > 6 && end > start) {
          return payload.substring(start, end);
        }
      }

      return "unknown";
    } catch (Exception ex) {
      return "unknown";
    }
  }

  /** Check if token is expired */
  public boolean isTokenExpired(String token) {
    try {
      Claims claims =
          Jwts.parser().verifyWith(accessTokenKey).build().parseSignedClaims(token).getPayload();

      return claims.getExpiration().before(new Date());
    } catch (ExpiredJwtException ex) {
      // Token is expired
      return true;
    } catch (JwtException ex) {
      // Token is invalid for other reasons
      return true;
    }
  }

  /** JWT claims wrapper class */
  public static class JwtClaims {
    private final String userId;
    private final String email;
    private final List<String> roles;
    private final Date issuedAt;
    private final Date expiration;

    public JwtClaims(
        String userId, String email, List<String> roles, Date issuedAt, Date expiration) {
      this.userId = userId;
      this.email = email;
      this.roles = roles;
      this.issuedAt = issuedAt;
      this.expiration = expiration;
    }

    public String getUserId() {
      return userId;
    }

    public String getEmail() {
      return email;
    }

    public List<String> getRoles() {
      return roles;
    }

    public Date getIssuedAt() {
      return issuedAt;
    }

    public Date getExpiration() {
      return expiration;
    }

    public LocalDateTime getExpirationLocalDateTime() {
      return LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault());
    }
  }
}
