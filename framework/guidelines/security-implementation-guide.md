# Security Implementation Framework

**Author:** Senior Principal Engineer  
**Version:** 1.0  
**Date:** December 2024  
**Purpose:** Comprehensive security guidelines for loyalty system implementation  
**Scope:** All development teams and security practices  

---

## 🔒 Security Philosophy

### Core Security Principles
1. **Security by Design:** Security considerations integrated from the beginning
2. **Defense in Depth:** Multiple layers of security controls
3. **Zero Trust Architecture:** Never trust, always verify
4. **Privacy by Design:** Data protection built into system architecture
5. **Continuous Security:** Ongoing monitoring and improvement
6. **Compliance First:** Meet all regulatory requirements (GDPR, CCPA, PCI DSS)

---

## 🛡️ Authentication & Authorization

### 1. User Authentication

#### Multi-Factor Authentication (MFA)
```java
@Component
public class MFAService {
    
    public MFAChallenge generateChallenge(String userId) {
        // Generate time-based OTP or SMS challenge
        String secret = generateTOTPSecret();
        String challenge = generateTOTP(secret);
        
        // Store challenge with expiration
        redisTemplate.opsForValue().set(
            "mfa:" + userId, 
            challenge, 
            Duration.ofMinutes(5)
        );
        
        return new MFAChallenge(challenge, getQRCode(secret));
    }
    
    public boolean validateMFA(String userId, String token) {
        String storedChallenge = redisTemplate.opsForValue()
            .get("mfa:" + userId);
            
        return storedChallenge != null && 
               constantTimeEquals(storedChallenge, token);
    }
}
```

#### JWT Token Management
```java
@Service
public class JWTService {
    
    private static final int ACCESS_TOKEN_EXPIRY = 15; // minutes
    private static final int REFRESH_TOKEN_EXPIRY = 7; // days
    
    public TokenPair generateTokens(User user) {
        String accessToken = Jwts.builder()
            .setSubject(user.getId())
            .setIssuer("loyalty-system")
            .setIssuedAt(new Date())
            .setExpiration(Date.from(Instant.now()
                .plusSeconds(ACCESS_TOKEN_EXPIRY * 60)))
            .claim("role", user.getRole())
            .claim("permissions", user.getPermissions())
            .signWith(getSigningKey(), SignatureAlgorithm.RS256)
            .compact();
            
        String refreshToken = generateSecureRefreshToken();
        storeRefreshToken(user.getId(), refreshToken);
        
        return new TokenPair(accessToken, refreshToken);
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getPublicKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
```

### 2. Role-Based Access Control (RBAC)

#### Permission Framework
```java
public enum Permission {
    // User permissions
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    USER_DELETE("user:delete"),
    
    // Point permissions
    POINTS_EARN("points:earn"),
    POINTS_REDEEM("points:redeem"),
    POINTS_ADJUST("points:adjust"),
    
    // Admin permissions
    ADMIN_DASHBOARD("admin:dashboard"),
    ADMIN_REPORTS("admin:reports"),
    ADMIN_CONFIG("admin:config"),
    
    // System permissions
    SYSTEM_ALL("system:*");
    
    private final String permission;
    
    Permission(String permission) {
        this.permission = permission;
    }
}

@PreAuthorize("hasPermission(#userId, 'USER', 'READ')")
public UserProfile getUserProfile(String userId) {
    return userService.getProfile(userId);
}

@PreAuthorize("hasRole('ADMIN') and hasPermission('POINTS', 'ADJUST')")
public void adjustUserPoints(String userId, int points, String reason) {
    pointService.adjustPoints(userId, points, reason);
}
```

#### Security Context
```java
@Component
public class SecurityContextService {
    
    public SecurityContext getCurrentContext() {
        Authentication auth = SecurityContextHolder
            .getContext().getAuthentication();
            
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("No authenticated user");
        }
        
        return new SecurityContext(
            auth.getName(),
            extractRoles(auth),
            extractPermissions(auth),
            extractTenantId(auth)
        );
    }
    
    public boolean hasPermission(String resource, String action) {
        SecurityContext context = getCurrentContext();
        return context.getPermissions().contains(
            resource + ":" + action
        ) || context.getPermissions().contains(resource + ":*");
    }
}
```

---

## 🔐 Data Protection

### 1. Encryption Standards

#### Data at Rest Encryption
```java
@Service
public class EncryptionService {
    
    private final AESUtil aesUtil;
    
    @Value("${encryption.key}")
    private String encryptionKey;
    
    public String encryptSensitiveData(String data) {
        if (StringUtils.isBlank(data)) return data;
        
        try {
            return aesUtil.encrypt(data, encryptionKey);
        } catch (Exception e) {
            log.error("Encryption failed", e);
            throw new SecurityException("Data encryption failed");
        }
    }
    
    public String decryptSensitiveData(String encryptedData) {
        if (StringUtils.isBlank(encryptedData)) return encryptedData;
        
        try {
            return aesUtil.decrypt(encryptedData, encryptionKey);
        } catch (Exception e) {
            log.error("Decryption failed", e);
            throw new SecurityException("Data decryption failed");
        }
    }
}

@Entity
public class User {
    @Id
    private String id;
    
    @Column(nullable = false)
    private String email;
    
    @Convert(converter = EncryptedStringConverter.class)
    private String phoneNumber;
    
    @Convert(converter = EncryptedStringConverter.class) 
    private String dateOfBirth;
    
    // Sensitive data automatically encrypted/decrypted
}
```

#### Data in Transit Encryption
```yaml
# application.yml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: loyalty-system
  port: 8443

spring:
  security:
    require-ssl: true
```

### 2. PII Data Handling

#### Data Classification
```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveData {
    DataClassification classification() default DataClassification.CONFIDENTIAL;
    String reason() default "";
}

public enum DataClassification {
    PUBLIC,           // No protection needed
    INTERNAL,         // Company internal only
    CONFIDENTIAL,     // Requires authorization
    RESTRICTED        // Highest protection level
}

@Entity
public class CustomerProfile {
    
    @SensitiveData(classification = DataClassification.RESTRICTED)
    private String socialSecurityNumber;
    
    @SensitiveData(classification = DataClassification.CONFIDENTIAL)
    private String creditCardNumber;
    
    @SensitiveData(classification = DataClassification.CONFIDENTIAL)
    private String phoneNumber;
    
    @SensitiveData(classification = DataClassification.INTERNAL)
    private String loyaltyTier;
}
```

#### Data Masking
```java
@Service
public class DataMaskingService {
    
    public String maskCreditCard(String creditCard) {
        if (StringUtils.isBlank(creditCard) || creditCard.length() < 4) {
            return creditCard;
        }
        
        String lastFour = creditCard.substring(creditCard.length() - 4);
        return "**** **** **** " + lastFour;
    }
    
    public String maskEmail(String email) {
        if (StringUtils.isBlank(email) || !email.contains("@")) {
            return email;
        }
        
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        String maskedUsername = username.charAt(0) + 
            "*".repeat(Math.max(0, username.length() - 2)) +
            (username.length() > 1 ? username.charAt(username.length() - 1) : "");
            
        return maskedUsername + "@" + domain;
    }
    
    public String maskPhoneNumber(String phone) {
        if (StringUtils.isBlank(phone) || phone.length() < 4) {
            return phone;
        }
        
        return "*".repeat(phone.length() - 4) + phone.substring(phone.length() - 4);
    }
}
```

---

## 🚨 Input Validation & Sanitization

### 1. Input Validation Framework

#### Request Validation
```java
public class UserRegistrationRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email too long")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must be at least 8 characters with uppercase, lowercase, digit and special character"
    )
    private String password;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    @Past(message = "Birth date must be in the past")
    @NotNull(message = "Birth date is required")
    private LocalDate dateOfBirth;
}

@RestController
@Validated
public class UserController {
    
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
        @Valid @RequestBody UserRegistrationRequest request) {
        
        // Additional business validation
        validateBusinessRules(request);
        
        User user = userService.register(request);
        return ResponseEntity.ok(new UserResponse(user));
    }
}
```

#### SQL Injection Prevention
```java
@Repository
public class UserRepository {
    
    // GOOD: Using parameterized queries
    public List<User> findUsersByTier(String tier) {
        String sql = "SELECT * FROM users WHERE tier = ?";
        return jdbcTemplate.query(sql, new Object[]{tier}, userRowMapper);
    }
    
    // GOOD: Using JPA with parameters
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.status = :status")
    public Optional<User> findActiveUserByEmail(
        @Param("email") String email, 
        @Param("status") UserStatus status
    );
    
    // AVOID: String concatenation
    // String sql = "SELECT * FROM users WHERE tier = '" + tier + "'"; // DANGEROUS!
}
```

### 2. XSS Prevention

#### Output Encoding
```java
@Component
public class SecurityEncodingService {
    
    public String encodeForHTML(String input) {
        if (input == null) return null;
        return HtmlUtils.htmlEscape(input);
    }
    
    public String encodeForJavaScript(String input) {
        if (input == null) return null;
        return JavaScriptUtils.javaScriptEscape(input);
    }
    
    public String encodeForURL(String input) {
        if (input == null) return null;
        try {
            return URLEncoder.encode(input, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("URL encoding failed", e);
            return "";
        }
    }
}

// Content Security Policy
@Configuration
public class SecurityHeadersConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   Object handler) {
                response.setHeader("Content-Security-Policy", 
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline'; " +
                    "style-src 'self' 'unsafe-inline'; " +
                    "img-src 'self' data:; " +
                    "connect-src 'self'");
                return true;
            }
        });
    }
}
```

---

## 🔍 Security Monitoring & Logging

### 1. Security Event Logging

#### Audit Trail Implementation
```java
@Component
public class SecurityAuditService {
    
    private static final Logger securityLogger = 
        LoggerFactory.getLogger("SECURITY");
    
    public void logSecurityEvent(SecurityEvent event) {
        SecurityEventLog log = SecurityEventLog.builder()
            .eventType(event.getType())
            .userId(event.getUserId())
            .ipAddress(event.getIpAddress())
            .userAgent(event.getUserAgent())
            .resource(event.getResource())
            .action(event.getAction())
            .result(event.getResult())
            .timestamp(Instant.now())
            .sessionId(event.getSessionId())
            .build();
            
        // Log to security log file
        securityLogger.info("Security Event: {}", log.toJson());
        
        // Store in database for analysis
        auditRepository.save(log);
        
        // Send to SIEM if critical
        if (event.isCritical()) {
            siemService.sendAlert(log);
        }
    }
    
    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        logSecurityEvent(SecurityEvent.builder()
            .type("AUTHENTICATION_SUCCESS")
            .userId(event.getAuthentication().getName())
            .ipAddress(getClientIpAddress())
            .result("SUCCESS")
            .build());
    }
    
    @EventListener
    public void handleAuthenticationFailure(AuthenticationFailureEvent event) {
        logSecurityEvent(SecurityEvent.builder()
            .type("AUTHENTICATION_FAILURE")
            .userId(event.getAuthentication().getName())
            .ipAddress(getClientIpAddress())
            .result("FAILURE")
            .reason(event.getException().getMessage())
            .critical(true)
            .build());
    }
}
```

### 2. Threat Detection

#### Rate Limiting & DDoS Protection
```java
@Component
public class RateLimitingService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    public boolean isAllowed(String identifier, int maxRequests, Duration window) {
        String key = "rate_limit:" + identifier;
        String countStr = redisTemplate.opsForValue().get(key);
        
        int count = countStr != null ? Integer.parseInt(countStr) : 0;
        
        if (count >= maxRequests) {
            logSecurityEvent(SecurityEvent.builder()
                .type("RATE_LIMIT_EXCEEDED")
                .userId(identifier)
                .critical(true)
                .build());
            return false;
        }
        
        // Increment counter
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, window);
        
        return true;
    }
}

@RestControllerAdvice
public class RateLimitingInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) {
        
        String clientIp = getClientIpAddress(request);
        
        // Different limits for different endpoints
        if (request.getRequestURI().contains("/auth/")) {
            if (!rateLimitingService.isAllowed(clientIp, 5, Duration.ofMinutes(15))) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                return false;
            }
        }
        
        return true;
    }
}
```

#### Fraud Detection
```java
@Service
public class FraudDetectionService {
    
    public FraudAssessment assessTransaction(TransactionRequest request) {
        FraudScore score = calculateFraudScore(request);
        
        if (score.getScore() > 0.8) {
            // Block transaction
            blockTransaction(request, "High fraud score: " + score.getScore());
            return FraudAssessment.blocked(score);
        } else if (score.getScore() > 0.5) {
            // Require additional verification
            return FraudAssessment.requiresVerification(score);
        }
        
        return FraudAssessment.approved(score);
    }
    
    private FraudScore calculateFraudScore(TransactionRequest request) {
        double score = 0.0;
        List<String> reasons = new ArrayList<>();
        
        // Check velocity patterns
        if (isHighVelocity(request.getUserId())) {
            score += 0.3;
            reasons.add("High transaction velocity");
        }
        
        // Check unusual patterns
        if (isUnusualTime(request.getTimestamp())) {
            score += 0.2;
            reasons.add("Unusual transaction time");
        }
        
        // Check location patterns
        if (isUnusualLocation(request.getUserId(), request.getIpAddress())) {
            score += 0.4;
            reasons.add("Unusual location");
        }
        
        // Check device patterns
        if (isNewDevice(request.getUserId(), request.getDeviceFingerprint())) {
            score += 0.3;
            reasons.add("New device");
        }
        
        return new FraudScore(Math.min(score, 1.0), reasons);
    }
}
```

---

## 🔐 API Security

### 1. API Gateway Security

#### OAuth2 Implementation
```yaml
# API Gateway Configuration
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth.loyalty-system.com
          jwk-set-uri: https://auth.loyalty-system.com/.well-known/jwks.json
          
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: http://user-service:8080
          predicates:
            - Path=/api/users/**
          filters:
            - TokenRelay=
            - RateLimiter=user-service-rate-limiter
```

#### API Rate Limiting
```java
@Configuration
public class GatewaySecurityConfig {
    
    @Bean
    public RedisRateLimiter userServiceRateLimiter() {
        return new RedisRateLimiter(
            10,  // replenishRate: tokens per second
            20,  // burstCapacity: total tokens in bucket
            1    // requestedTokens: tokens requested per request
        );
    }
    
    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal()
            .cast(JwtAuthenticationToken.class)
            .map(JwtAuthenticationToken::getToken)
            .map(jwt -> jwt.getClaimAsString("sub"))
            .switchIfEmpty(Mono.just("anonymous"));
    }
}
```

### 2. API Input Validation

#### Request Size Limiting
```yaml
server:
  max-http-header-size: 8KB
  
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
```

#### CORS Configuration
```java
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOriginPatterns("https://*.loyalty-system.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

---

## 🛡️ Infrastructure Security

### 1. Container Security

#### Dockerfile Security Best Practices
```dockerfile
# Use specific version, not latest
FROM openjdk:17-jre-slim@sha256:specific-hash

# Create non-root user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy only necessary files
COPY --chown=appuser:appuser target/loyalty-service.jar app.jar

# Switch to non-root user
USER appuser

# Expose only necessary port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

#### Kubernetes Security Policies
```yaml
apiVersion: v1
kind: Pod
spec:
  securityContext:
    runAsNonRoot: true
    runAsUser: 10001
    fsGroup: 10001
  containers:
    - name: loyalty-service
      securityContext:
        allowPrivilegeEscalation: false
        readOnlyRootFilesystem: true
        capabilities:
          drop:
            - ALL
      resources:
        limits:
          memory: "512Mi"
          cpu: "500m"
        requests:
          memory: "256Mi"
          cpu: "250m"
```

### 2. Network Security

#### Network Policies
```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: loyalty-system-network-policy
spec:
  podSelector:
    matchLabels:
      app: loyalty-system
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          role: api-gateway
    ports:
    - protocol: TCP
      port: 8080
  egress:
  - to:
    - podSelector:
        matchLabels:
          app: postgres
    ports:
    - protocol: TCP
      port: 5432
```

---

## 📋 Security Checklists

### Development Security Checklist
- [ ] All inputs validated and sanitized
- [ ] Parameterized queries used to prevent SQL injection
- [ ] Output encoded to prevent XSS
- [ ] Sensitive data encrypted at rest and in transit
- [ ] Authentication and authorization implemented
- [ ] Rate limiting configured for all endpoints
- [ ] Security headers configured (CSP, HSTS, etc.)
- [ ] Error messages don't leak sensitive information
- [ ] Logging captures security events
- [ ] Dependencies scanned for vulnerabilities

### Deployment Security Checklist
- [ ] Containers running as non-root users
- [ ] Network policies restricting communication
- [ ] Secrets managed securely (not in environment variables)
- [ ] TLS/SSL configured with strong ciphers
- [ ] Security scanning integrated into CI/CD pipeline
- [ ] Infrastructure as code reviewed for security
- [ ] Monitoring and alerting configured for security events
- [ ] Backup encryption enabled
- [ ] Access controls configured for cloud resources
- [ ] Security incident response plan in place

### Compliance Checklist (GDPR/CCPA)
- [ ] Data privacy impact assessment completed
- [ ] Consent management implemented
- [ ] Data subject rights functionality implemented (access, rectification, erasure)
- [ ] Data breach notification procedures in place
- [ ] Privacy policy updated and accessible
- [ ] Data processing records maintained
- [ ] Cross-border data transfer safeguards implemented
- [ ] Privacy by design principles followed
- [ ] Data retention policies implemented
- [ ] Third-party vendor security assessments completed

---

## 🚨 Incident Response

### Security Incident Response Plan
```java
@Component
public class SecurityIncidentHandler {
    
    @EventListener
    public void handleSecurityIncident(SecurityIncidentEvent event) {
        
        // 1. Immediate containment
        if (event.getSeverity() == Severity.CRITICAL) {
            containmentService.isolateAffectedSystems(event.getAffectedSystems());
            notificationService.alertSecurityTeam(event);
        }
        
        // 2. Investigation
        forensicsService.preserveEvidence(event);
        investigationService.analyzeIncident(event);
        
        // 3. Communication
        if (event.requiresPublicDisclosure()) {
            communicationService.notifyStakeholders(event);
        }
        
        // 4. Recovery
        recoveryService.initiateRecoveryProcedures(event);
        
        // 5. Lessons learned
        postIncidentService.scheduleReview(event);
    }
}
```

---

This security framework ensures comprehensive protection for the loyalty system while maintaining usability and performance. Regular security reviews and updates should be scheduled to adapt to evolving threats and regulatory requirements.