# Loyalty Common Library

This library provides shared utilities and components for all loyalty system microservices, ensuring consistency and reducing code duplication across services.

## 📦 Components

### 1. Error Handling (`exceptions` package)
- **LoyaltyException**: Base exception class with consistent error structure
- **ValidationException**: Input validation errors (400)
- **BusinessRuleException**: Business logic violations (422)
- **NotFoundException**: Resource not found errors (404)
- **AuthenticationException**: Authentication failures (401)
- **AuthorizationException**: Authorization failures (403)
- **ConflictException**: Resource conflicts (409)
- **GlobalExceptionHandler**: Global exception handling for consistent API responses

### 2. Logging (`logging` package)
- **StructuredLogger**: Enhanced logging with structured context and correlation IDs
- **CorrelationIdFilter**: Automatic correlation ID generation for request tracing

### 3. Security (`security` package)
- **JwtTokenService**: JWT token generation and validation
- **JwtAuthenticationFilter**: Spring Security JWT authentication filter
- **LoyaltySecurityConfig**: Base security configuration for all services

### 4. Database (`database` package)
- **DatabaseConfig**: Optimized HikariCP connection pool configuration
- **BaseEntity**: Base JPA entity with audit fields (ID, createdAt, updatedAt, version)
- **BaseRepository**: Common repository interface with standard operations

### 5. Validation (`validation` package)
- **ValidationUtils**: Common validation utilities for email, phone, password, etc.

### 6. Monitoring (`monitoring` package)
- **LoyaltyMetrics**: Standardized metrics collection for Prometheus
- **LoyaltyHealthIndicator**: Custom health checks for dependencies

### 7. Response (`response` package)
- **ApiResponse**: Standard API response wrapper for consistent responses

## 🚀 Usage

### 1. Add Dependency
Add this to your service's `pom.xml`:

```xml
<dependency>
    <groupId>com.example.loyalty</groupId>
    <artifactId>loyalty-common</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. Enable Auto-Configuration
The library auto-configures when included. No additional setup required.

### 3. Use Components

#### Error Handling
```java
@RestController
public class UserController {
    
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(request);
            return ResponseEntity.ok(ApiResponse.success(user, "User created successfully"));
        } catch (ValidationException ex) {
            // GlobalExceptionHandler will handle this automatically
            throw ex;
        }
    }
}
```

#### Structured Logging
```java
public class UserService {
    private static final StructuredLogger logger = StructuredLogger.getLogger(UserService.class);
    
    public User createUser(CreateUserRequest request) {
        StructuredLogger.TimingContext timing = logger.startTiming("create_user", 
            "email", request.getEmail());
        
        try {
            // Business logic
            User user = userRepository.save(new User(request));
            
            logger.info("User created successfully", 
                "userId", user.getId(),
                "email", user.getEmail());
            
            timing.end("status", "success");
            return user;
        } catch (Exception ex) {
            timing.endWithError(ex.getMessage(), "email", request.getEmail());
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
public class UserService {
    
    public User registerUser(RegisterRequest request) {
        // Validate using utility methods
        String email = ValidationUtils.requireValidEmail(request.getEmail());
        String password = ValidationUtils.requireValidPassword(request.getPassword());
        String firstName = ValidationUtils.requireLength(request.getFirstName(), "firstName", 1, 100);
        
        // Create user...
    }
}
```

#### Metrics
```java
@Service
public class PointService {
    private final LoyaltyMetrics metrics;
    
    public void earnPoints(String userId, int points) {
        Timer.Sample sample = metrics.startRequestTimer();
        
        try {
            // Business logic...
            metrics.incrementBusinessEvent("points_earned", userTier);
            sample.stop("operation", "earn_points", "result", "success");
        } catch (Exception ex) {
            metrics.incrementErrors("business_error", "point-service", "earn_points");
            sample.stop("operation", "earn_points", "result", "error");
            throw ex;
        }
    }
}
```

## 🏗️ Architecture Integration

This library follows the Clean Architecture principles:
- **Domain Layer**: Exception types and validation utilities
- **Application Layer**: Response wrappers and validation logic
- **Infrastructure Layer**: Database config, security filters, logging
- **Interface Layer**: Global exception handlers, metrics

## 🔧 Configuration

The library uses Spring Boot's configuration properties. Add these to your `application.yml`:

```yaml
app:
  jwt:
    secret: ${JWT_SECRET:your-secret-key}
    refresh-secret: ${JWT_REFRESH_SECRET:your-refresh-secret}
    access-expiration: 900000  # 15 minutes
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

## 📝 Development

### Building
```bash
mvn clean compile
```

### Testing
```bash
mvn test
```

### Installing to Local Repository
```bash
mvn clean install
```

This will make the library available to other services in the loyalty system.

## 🔄 Versioning

This library follows semantic versioning. Update the version in `pom.xml` when making changes:
- **MAJOR**: Breaking changes
- **MINOR**: New features, backward compatible
- **PATCH**: Bug fixes, backward compatible