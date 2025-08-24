package com.example.loyalty.common.security;

import com.example.loyalty.common.exceptions.AuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT authentication filter for Spring Security Validates JWT tokens and sets up security context
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenService jwtTokenService;

  public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
    this.jwtTokenService = jwtTokenService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      String token = extractTokenFromRequest(request);

      if (token != null) {
        JwtTokenService.JwtClaims claims = jwtTokenService.validateAccessToken(token);

        // Create authorities from roles
        List<SimpleGrantedAuthority> authorities =
            claims.getRoles() != null
                ? claims.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList())
                : List.of();

        // Create authentication token
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(claims.getUserId(), null, authorities);

        // Add claims as details
        authentication.setDetails(claims);

        // Set security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }

    } catch (AuthenticationException ex) {
      // Let Spring Security handle authentication failures
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response
          .getWriter()
          .write(
              "{\"success\":false,\"error\":{\"code\":\""
                  + ex.getCode()
                  + "\",\"message\":\""
                  + ex.getMessage()
                  + "\"}}");
      return;
    }

    filterChain.doFilter(request, response);
  }

  /** Extract JWT token from Authorization header */
  private String extractTokenFromRequest(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }

    return null;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();

    // Skip authentication for public endpoints
    return path.contains("/health")
        || path.contains("/metrics")
        || path.contains("/swagger")
        || path.contains("/api-docs")
        || path.endsWith("/register")
        || path.endsWith("/login");
  }
}
