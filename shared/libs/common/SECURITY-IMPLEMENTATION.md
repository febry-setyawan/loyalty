# Security Foundation Implementation

**Story 1.4 - Complete Implementation Guide**

This document describes the complete security foundation implementation for the loyalty system, following industry best practices and compliance requirements.

## 🚨 Overview

The security foundation provides comprehensive protection through:

- **Data Encryption**: AES-256-GCM encryption for sensitive data at rest
- **Transport Security**: TLS 1.3 enforcement with strong cipher suites
- **Authentication**: BCrypt password hashing with 12 salt rounds
- **Authorization**: JWT-based authentication with proper security headers
- **Audit Logging**: Comprehensive security event tracking
- **Rate Limiting**: Redis-based distributed rate limiting
- **Fraud Detection**: Basic velocity and pattern-based fraud detection
- **Data Protection**: PII masking and data classification

## 🔧 Components Implemented

### 1. EncryptionService

**Location**: `com.example.loyalty.common.security.EncryptionService`

```java
// Encrypt sensitive data
String encrypted = encryptionService.encryptSensitiveData("sensitive-info");

// Decrypt sensitive data  
String decrypted = encryptionService.decryptSensitiveData(encrypted);
```

**Features**:
- AES-256-GCM encryption with random IV
- Base64 encoding for database storage
- Automatic key validation
- Secure exception handling

### 2. SecurityAuditService

**Location**: `com.example.loyalty.common.security.SecurityAuditService`

```java
// Log authentication events
auditService.logSecurityEvent(securityEvent);

// Log data access
auditService.logDataAccess(userId, "/users/profile", "READ", "SUCCESS");

// Log point transactions
auditService.logPointTransaction(userId, "EARN", 100, "SUCCESS", "Purchase");
```

**Features**:
- Structured security event logging
- IP address and user agent tracking
- Critical event flagging
- Spring Security integration

### 3. RateLimitingService

**Location**: `com.example.loyalty.common.security.RateLimitingService`

```java
// Check authentication rate limit
if (!rateLimitingService.isAuthAttemptAllowed(clientIp)) {
    throw new TooManyRequestsException("Too many authentication attempts");
}

// Check API rate limit
if (!rateLimitingService.isApiCallAllowed(userId)) {
    throw new TooManyRequestsException("API rate limit exceeded");
}
```

**Features**:
- Redis-based distributed rate limiting
- Sliding window implementation
- Different limits for different endpoints
- Graceful degradation (fail-open)

### 4. FraudDetectionService

**Location**: `com.example.loyalty.common.security.FraudDetectionService`

```java
// Assess transaction for fraud
TransactionRequest request = new TransactionRequest(userId, "EARN", 5000, 
    Instant.now(), deviceFingerprint, ipAddress);
    
FraudAssessment assessment = fraudDetectionService.assessTransaction(request);

if (assessment.getResult() == FraudResult.BLOCKED) {
    throw new FraudDetectedException("Transaction blocked due to fraud risk");
}
```

**Features**:
- Velocity-based detection
- Time pattern analysis
- Large amount detection
- Device fingerprinting

### 5. DataMaskingService

**Location**: `com.example.loyalty.common.security.DataMaskingService`

```java
// Mask sensitive data for logs/display
String maskedEmail = maskingService.maskEmail("user@example.com"); // u**r@example.com
String maskedCard = maskingService.maskCreditCard("1234567890123456"); // **** **** **** 3456
String maskedPhone = maskingService.maskPhoneNumber("1234567890"); // ******7890
```

**Features**:
- Email masking
- Credit card masking  
- Phone number masking
- Name masking
- SSN masking
- Generic masking utilities

### 6. Enhanced Security Configuration

**Location**: `com.example.loyalty.common.security.LoyaltySecurityConfig`

**Security Headers Added**:
- `Content-Security-Policy`
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `X-XSS-Protection: 1; mode=block`
- `Strict-Transport-Security` with 1-year max-age
- `Referrer-Policy: strict-origin-when-cross-origin`
- `Permissions-Policy`

### 7. JPA Encryption Support

**Location**: `com.example.loyalty.common.security.EncryptedStringConverter`

```java
@Entity
public class User {
    @Convert(converter = EncryptedStringConverter.class)
    private String socialSecurityNumber;
    
    @Convert(converter = EncryptedStringConverter.class)
    private String phoneNumber;
}
```

### 8. Data Classification

**Location**: `com.example.loyalty.common.security.SensitiveData`

```java
@Entity
public class CustomerProfile {
    @SensitiveData(classification = DataClassification.RESTRICTED)
    private String socialSecurityNumber;
    
    @SensitiveData(classification = DataClassification.CONFIDENTIAL)
    private String creditCardNumber;
}
```

## 🔧 Configuration

### Environment Variables Required

```bash
# Encryption (Generate using EncryptionService.generateEncryptionKey())
ENCRYPTION_KEY=<base64-encoded-256-bit-key>

# JWT Secrets
JWT_SECRET=<strong-random-secret>
JWT_REFRESH_SECRET=<strong-random-secret>

# Database with SSL
DB_URL=jdbc:postgresql://localhost:5432/loyalty_db?sslmode=require

# TLS Certificate (Production)
SSL_KEYSTORE_PASSWORD=<keystore-password>

# Redis for Rate Limiting
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=<redis-password>
```

### Application Configuration

See `application-security.yml` for complete configuration templates including:

- TLS 1.3 configuration
- Security headers
- Rate limiting settings
- Encryption settings
- Environment-specific configurations

## 🧪 Testing

Comprehensive test coverage includes:

- **EncryptionServiceTest**: 9 tests covering encryption/decryption, key validation, error handling
- **DataMaskingServiceTest**: 15 tests covering all masking scenarios
- **SecurityEventTest**: 5 tests covering event building and validation
- **JwtTokenServiceTest**: 5 existing tests for JWT functionality

**Run Tests**:
```bash
cd shared/libs/common
mvn test
```

## 🔐 Security Features Implemented

### ✅ Data Encryption at Rest
- AES-256-GCM encryption for sensitive database fields
- Automatic JPA converter for transparent encryption/decryption
- Secure key management with environment variables

### ✅ TLS 1.3 Transport Security
- Complete TLS 1.3 configuration template
- Strong cipher suite selection
- HSTS headers for browser security

### ✅ Password Security
- BCrypt with 12 salt rounds (industry standard)
- Configured in `LoyaltySecurityConfig`

### ✅ PII Data Protection
- Comprehensive data masking utilities
- Data classification annotations
- Automatic encryption for sensitive fields

### ✅ Security Headers
- Complete set of security headers
- XSS, CSRF, clickjacking protection
- Content Security Policy

### ✅ Rate Limiting
- Distributed rate limiting with Redis
- Different limits for auth, API, transactions
- Graceful degradation

### ✅ Audit Logging
- Comprehensive security event logging
- Structured logging with correlation IDs
- Critical event flagging

### ✅ Basic Fraud Detection
- Velocity-based detection
- Pattern analysis (time, amount, device)
- Configurable risk thresholds

## 📈 Usage Examples

### Securing an Entity

```java
@Entity
public class UserProfile {
    @Id
    private String id;
    
    @Column
    private String email;
    
    // This will be automatically encrypted/decrypted
    @Convert(converter = EncryptedStringConverter.class)
    @SensitiveData(classification = DataClassification.CONFIDENTIAL)
    private String phoneNumber;
    
    @Convert(converter = EncryptedStringConverter.class)
    @SensitiveData(classification = DataClassification.RESTRICTED)
    private String socialSecurityNumber;
}
```

### Implementing Rate Limiting in Controller

```java
@RestController
public class AuthController {
    
    @Autowired
    private RateLimitingService rateLimitingService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, 
                                   HttpServletRequest httpRequest) {
        
        String clientIp = getClientIp(httpRequest);
        
        if (!rateLimitingService.isAuthAttemptAllowed(clientIp)) {
            throw new TooManyRequestsException("Too many login attempts");
        }
        
        // Continue with authentication...
    }
}
```

### Adding Audit Logging

```java
@Service
public class UserService {
    
    @Autowired
    private SecurityAuditService auditService;
    
    public User getUserProfile(String userId) {
        User user = userRepository.findById(userId);
        
        // Log data access
        auditService.logDataAccess(userId, "/users/profile", "READ", "SUCCESS");
        
        return user;
    }
}
```

## 🚨 Security Considerations

### Key Management
- Store encryption keys securely (environment variables, not code)
- Rotate encryption keys regularly
- Use different keys for different environments

### Rate Limiting
- Monitor rate limit violations
- Adjust limits based on usage patterns
- Implement IP whitelisting for trusted sources

### Audit Logging
- Ensure logs are centralized and monitored
- Implement alerting for critical security events
- Retain logs according to compliance requirements

### Fraud Detection
- Tune thresholds based on business requirements
- Implement manual review processes
- Monitor false positive rates

## 🎯 Compliance Features

- **GDPR**: Data encryption, masking, audit trails
- **PCI DSS**: Credit card data protection, secure transmission
- **SOX**: Audit logging, data integrity
- **HIPAA**: Data encryption, access logging (if health data)

## 📞 Next Steps

1. **Environment Setup**: Configure encryption keys and certificates
2. **Monitoring**: Set up alerting for security events
3. **Testing**: Perform security testing and penetration testing
4. **Documentation**: Train development team on security practices
5. **Compliance**: Review with compliance team for certification

---

**✅ User Story 1.4: Security Foundation Implementation - COMPLETED**

All acceptance criteria met with comprehensive test coverage and documentation.