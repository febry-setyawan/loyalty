# Shared Library Integration Guide

## 🎯 For Existing Services

To integrate the `loyalty-common` library into existing services (`user-service`, `point-service`, `rewards-service`, `admin-service`):

### 1. Add Dependency
Add to each service's `pom.xml`:
```xml
<dependency>
    <groupId>com.example.loyalty</groupId>
    <artifactId>loyalty-common</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. Remove Duplicate Code
- Replace custom error handling with shared exceptions
- Remove custom JWT implementations and use `JwtTokenService`
- Replace custom validation with `ValidationUtils`
- Update logging to use `StructuredLogger`

### 3. Configuration Updates
Add to `application.yml`:
```yaml
app:
  jwt:
    secret: ${JWT_SECRET:change-in-production}
    refresh-secret: ${JWT_REFRESH_SECRET:change-in-production}
    access-expiration: 900000
    refresh-expiration: 604800000
    issuer: loyalty-system
```

### 4. Update Controllers
Use `ApiResponse` wrapper and let `GlobalExceptionHandler` handle errors automatically.

### 5. Testing
Update tests to use shared exception types and validation utilities.

---

## 🆕 For New Services

New services automatically benefit from the shared library when using the updated Spring Boot template at `framework/templates/SPRING-BOOT-STANDARD-TEMPLATE.md`.

The template now includes:
- ✅ `loyalty-common` dependency pre-configured
- ✅ Usage examples for all shared components
- ✅ Consistent error handling and logging setup
- ✅ JWT authentication ready to use
- ✅ Database utilities with connection pooling
- ✅ Metrics collection setup

---

**📋 Implementation Status:** ✅ Story 1.3 COMPLETED  
**📅 Ready for:** Service integration and new service development