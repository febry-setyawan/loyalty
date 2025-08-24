package com.example.loyalty.common.security;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Base security configuration for all loyalty system services Provides JWT authentication and
 * common security settings
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class LoyaltySecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public LoyaltySecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .headers(
            headers ->
                headers
                    // Security headers for protection against common attacks
                    .frameOptions(frameOptions -> frameOptions.deny())
                    .contentTypeOptions(contentTypeOptions -> {})
                    .httpStrictTransportSecurity(
                        hsts ->
                            hsts.maxAgeInSeconds(31536000) // 1 year
                                .includeSubDomains(true))
                    .addHeaderWriter(
                        (request, response) -> {
                          // Content Security Policy
                          response.setHeader(
                              "Content-Security-Policy",
                              "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:;");
                          // Additional security headers
                          response.setHeader("X-Content-Type-Options", "nosniff");
                          response.setHeader("X-Frame-Options", "DENY");
                          response.setHeader("X-XSS-Protection", "1; mode=block");
                          response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
                          response.setHeader(
                              "Permissions-Policy", "geolocation=(), microphone=(), camera=()");
                        }))
        .authorizeHttpRequests(
            authz ->
                authz
                    // Public endpoints
                    .requestMatchers("/health/**", "/metrics/**", "/actuator/**")
                    .permitAll()
                    .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html")
                    .permitAll()
                    .requestMatchers("/api/v1/users/register", "/api/v1/users/login")
                    .permitAll()
                    .requestMatchers("/api/v1/users/password-reset/**")
                    .permitAll()
                    // All other requests require authentication
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // 12 salt rounds for security
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Read allowed origins from environment variable (comma-separated)
    String allowedOriginsEnv = System.getenv("ALLOWED_ORIGINS");
    List<String> allowedOrigins;
    if (allowedOriginsEnv != null && !allowedOriginsEnv.isBlank()) {
      allowedOrigins = Arrays.asList(allowedOriginsEnv.split(","));
    } else {
      // Default to localhost for development if not set
      allowedOrigins = List.of("http://localhost:3000");
    }
    configuration.setAllowedOrigins(allowedOrigins);
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    configuration.setExposedHeaders(List.of("X-Correlation-ID", "Authorization"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
