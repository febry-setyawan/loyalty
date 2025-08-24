# Shared Libraries & Utilities - Implementation Guide

**Story 1.3 Implementation**  
**Epic 1: Infrastructure & Foundation Setup**  
**Phase 1: Foundation Development**  

---

## 📋 Implementation Summary

This document outlines the implementation of **Story 1.3: Shared Libraries & Utilities** which provides consistent, reusable components across all loyalty system microservices.

### ✅ Tasks Completed

- [x] **Create shared error handling library** - Consistent exception handling across services
- [x] **Setup logging framework with structured logging** - Correlation ID support and structured logs
- [x] **Create database connection utilities** - Optimized connection pooling configuration
- [x] **Setup authentication utilities (JWT)** - JWT token generation and validation
- [x] **Create validation schemas library** - Common validation utilities and patterns
- [x] **Setup monitoring and metrics collection** - Prometheus metrics and monitoring utilities

### ✅ Acceptance Criteria Met

- [x] **Error responses follow consistent format across services** - Global exception handler with standardized API responses
- [x] **All logs include correlation ID and structured fields** - Automatic correlation ID generation and MDC support
- [x] **Database connections use connection pooling** - HikariCP configuration with optimized settings
- [x] **JWT tokens generated and validated consistently** - Centralized JWT service with configurable settings

---

## 📦 Library Structure

The shared library is located at `/shared/libs/common/` and provides:

```
loyalty-common/
├── src/main/java/com/example/loyalty/common/
│   ├── config/                          # Auto-configuration
│   │   └── LoyaltyCommonAutoConfiguration.java
│   ├── exceptions/                      # Error handling
│   │   ├── LoyaltyException.java       # Base exception
│   │   ├── ValidationException.java    # Validation errors (400)
│   │   ├── AuthenticationException.java # Auth errors (401)
│   │   ├── AuthorizationException.java # Authorization errors (403)
│   │   ├── NotFoundException.java      # Not found errors (404)
│   │   ├── ConflictException.java      # Conflict errors (409)
│   │   ├── BusinessRuleException.java  # Business rule errors (422)
│   │   └── GlobalExceptionHandler.java # Global error handler
│   ├── logging/                        # Structured logging
│   │   ├── StructuredLogger.java       # Enhanced logger with context
│   │   └── CorrelationIdFilter.java    # Request correlation tracking
│   ├── security/                       # Authentication & authorization
│   │   ├── JwtTokenService.java        # JWT token management
│   │   ├── JwtAuthenticationFilter.java # Spring Security JWT filter
│   │   └── LoyaltySecurityConfig.java  # Base security configuration
│   ├── database/                       # Database utilities
│   │   ├── DatabaseConfig.java         # Connection pool configuration
│   │   ├── BaseEntity.java            # Base JPA entity with audit fields
│   │   └── BaseRepository.java        # Common repository interface
│   ├── validation/                     # Validation utilities
│   │   └── ValidationUtils.java       # Common validation methods
│   ├── monitoring/                     # Metrics & monitoring
│   │   └── LoyaltyMetrics.java        # Prometheus metrics utilities
│   └── response/                       # API responses
│       └── ApiResponse.java           # Standard response wrapper
└── src/main/resources/
    ├── logback-spring.xml              # Structured logging configuration
    └── META-INF/spring.factories       # Auto-configuration registration
```

---

## 🚀 Usage in Microservices

### 1. Add Dependency

Add to your service's `pom.xml`:

```xml
<dependency>
    <groupId>com.example.loyalty</groupId>
    <artifactId>loyalty-common</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. Auto-Configuration

The library auto-configures when included. No additional setup required.

### 3. Configuration Properties

Add to your `application.yml`:

```yaml
app:
  jwt:
    secret: ${JWT_SECRET:your-secret-key-change-in-production}
    refresh-secret: ${JWT_REFRESH_SECRET:your-refresh-secret-change-in-production}
    access-expiration: 900000      # 15 minutes
    refresh-expiration: 604800000  # 7 days
    issuer: loyalty-system
  database:
    pool:
      minimum-idle: 5
      maximum-pool-size: 20
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
```

### 4. Usage Examples

#### Error Handling
```java
@RestController
public class UserController {
    
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success(user, "User created successfully"));
    }
    
    // GlobalExceptionHandler automatically handles ValidationException, NotFoundException, etc.
}
```

#### Structured Logging
```java
@Service
public class UserService {
    private static final StructuredLogger logger = StructuredLogger.getLogger(UserService.class);
    
    public User createUser(CreateUserRequest request) {
        StructuredLogger.TimingContext timing = logger.startTiming("create_user", 
            "email", request.getEmail());
        
        try {
            User user = userRepository.save(new User(request));
            logger.info("User created successfully", "userId", user.getId());
            timing.end("status", "success");
            return user;
        } catch (Exception ex) {
            timing.endWithError(ex.getMessage());
            throw ex;
        }
    }
}
```

#### JWT Authentication
```java
@Service
public class AuthService {
    
    private final JwtTokenService jwtTokenService;
    
    public LoginResponse login(LoginRequest request) {
        // Validate credentials...
        String accessToken = jwtTokenService.generateAccessToken(
            user.getId(), user.getEmail(), user.getRoles());
        String refreshToken = jwtTokenService.generateRefreshToken(user.getId());
        return new LoginResponse(accessToken, refreshToken);
    }
}
```

#### Validation
```java
@Service
public class UserService {
    
    public User registerUser(RegisterRequest request) {
        String email = ValidationUtils.requireValidEmail(request.getEmail());
        String password = ValidationUtils.requireValidPassword(request.getPassword());
        String firstName = ValidationUtils.requireLength(request.getFirstName(), "firstName", 1, 100);
        
        // Create user with validated data
        return userRepository.save(new User(email, password, firstName));
    }
}
```

#### Database Entity
```java
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String firstName;
    
    // BaseEntity provides: id, createdAt, updatedAt, version
    // Constructor, getters, etc.
}
```

#### Metrics Collection
```java
@Service
public class PointService {
    
    private final LoyaltyMetrics metrics;
    
    public void earnPoints(String userId, int points) {
        Timer.Sample sample = metrics.startRequestTimer();
        
        try {
            // Business logic...
            metrics.incrementBusinessEvent("points_earned", userTier);
            metrics.stopTimer(sample, "earn_points", "success");
        } catch (Exception ex) {
            metrics.incrementErrors("business_error", "point-service", "earn_points");
            metrics.stopTimer(sample, "earn_points", "error");
            throw ex;
        }
    }
}
```

---

## 🔧 Integration with Service Templates

### Updated Spring Boot Template

The standard Spring Boot template should now include the shared library dependency:

```xml
<dependency>
    <groupId>com.example.loyalty</groupId>
    <artifactId>loyalty-common</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Service Configuration

Services can extend the base security configuration:

```java
@Configuration
public class UserServiceSecurityConfig extends LoyaltySecurityConfig {
    
    public UserServiceSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        super(jwtAuthenticationFilter);
    }
    
    // Additional service-specific security configuration if needed
}
```

---

## 📊 Testing & Quality Assurance

### Test Coverage
- **Unit Tests:** 30 tests covering all utilities
- **Exception Handling:** Complete test coverage for all exception types
- **Validation:** Comprehensive validation testing with edge cases
- **JWT:** Token generation and validation testing

### Build Verification
```bash
cd shared/libs/common
mvn clean compile  # ✅ PASSED
mvn test          # ✅ PASSED (30 tests)
mvn install       # ✅ PASSED - Library available for other services
```

---

## 🎯 Benefits Achieved

### 1. Consistency
- **Standardized error responses** across all services
- **Consistent logging format** with correlation IDs
- **Uniform authentication** mechanism
- **Common validation patterns**

### 2. Developer Experience
- **Reduced boilerplate code** in microservices
- **Consistent APIs** and error handling
- **Easy debugging** with correlation IDs
- **Standardized metrics** collection

### 3. Operational Benefits
- **Better observability** with structured logs and metrics
- **Improved debugging** with request tracing
- **Consistent monitoring** across services
- **Simplified configuration management**

---

## 📞 Next Steps

### For Service Development
1. **Add dependency** to existing services (`user-service`, `point-service`, etc.)
2. **Replace custom error handling** with shared exceptions
3. **Update logging** to use StructuredLogger
4. **Implement JWT authentication** using shared utilities
5. **Add metrics collection** using LoyaltyMetrics

### For New Services
1. **Use updated Spring Boot template** with shared library included
2. **Follow usage examples** in this documentation
3. **Extend base classes** (BaseEntity, BaseRepository) as needed

---

**✅ Story 1.3 Status: COMPLETED**  
**📅 Completion Date:** December 2024  
**👤 Implemented By:** Senior Developer  
**📋 Ready for:** Integration with existing services and new service development