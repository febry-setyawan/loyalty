# Development Guidelines & Standards

**Author:** Senior Principal Engineer  
**Version:** 1.0  
**Purpose:** Comprehensive development standards for loyalty system implementation  
**Scope:** All development teams working on the loyalty system  

---

## 🎯 Development Philosophy

### Core Principles
1. **Clean Architecture First:** Domain logic independent of infrastructure
2. **Test-Driven Development:** Write tests before implementation
3. **Security by Design:** Security considerations in every decision
4. **Performance by Design:** Optimize for scale from the beginning
5. **API-First Development:** Design APIs before implementation
6. **Observability Built-in:** Logging, monitoring, and tracing from day one

---

## 🏗️ Architecture Standards

### 1. Clean Architecture Implementation

#### Layer Responsibilities
```
┌─────────────────────────────────────────────────────────────────┐
│                        Interfaces Layer                         │
│  • Controllers (REST/GraphQL)     • Message Consumers          │
│  • API Validation                 • External Event Handlers    │
│  • Request/Response Mapping       • Health Check Endpoints     │
└─────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────┐
│                       Application Layer                         │
│  • Use Cases/Interactors         • Application Services        │
│  • Input/Output DTOs             • Workflow Orchestration      │
│  • Validation Logic              • Transaction Management      │
└─────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────┐
│                         Domain Layer                            │
│  • Entities                      • Domain Services             │
│  • Value Objects                 • Domain Events               │
│  • Repository Interfaces         • Business Rules              │
└─────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────┐
│                     Infrastructure Layer                        │
│  • Database Repositories         • External Service Clients    │
│  • Message Publishers/Consumers  • Configuration               │
│  • File Storage                  • Caching Implementations     │
└─────────────────────────────────────────────────────────────────┘
```

#### Dependency Rules
- **Inward Only:** Dependencies point inward toward the domain
- **No Leaking:** Infrastructure details don't leak to higher layers
- **Interface Segregation:** Use specific interfaces, not generic ones
- **Dependency Injection:** All dependencies injected, not created

### 2. Service Communication Patterns

#### Synchronous Communication
- **Use for:** Real-time queries, user-facing operations
- **Protocol:** REST APIs with JSON
- **Timeout:** Maximum 5 seconds
- **Retry:** Circuit breaker pattern with exponential backoff

#### Asynchronous Communication
- **Use for:** Event notifications, background processing
- **Protocol:** Message queues (Kafka/RabbitMQ)
- **Delivery:** At least once delivery guarantee
- **Idempotency:** All event handlers must be idempotent

---

## 📝 Coding Standards

### 1. General Code Quality

#### Naming Conventions
```javascript
// Constants - UPPER_SNAKE_CASE
const MAX_RETRY_ATTEMPTS = 3;
const DATABASE_CONNECTION_TIMEOUT = 5000;

// Classes - PascalCase
class UserRegistrationService {
  // Methods - camelCase
  async registerNewUser(userData) {
    // Variables - camelCase
    const hashedPassword = await this.hashPassword(userData.password);
    return this.userRepository.save(userData);
  }
}

// Files and directories - kebab-case
user-registration-service.js
point-calculation-engine.java
```

#### Code Structure
```javascript
// File structure template
class ServiceName {
  // 1. Dependencies injection in constructor
  constructor(dependency1, dependency2) {
    this.dependency1 = dependency1;
    this.dependency2 = dependency2;
  }

  // 2. Public methods first
  async publicMethod(input) {
    // Validate input
    this._validateInput(input);
    
    // Execute business logic
    const result = await this._processBusinessLogic(input);
    
    // Return result
    return this._formatResult(result);
  }

  // 3. Private methods last
  _validateInput(input) {
    if (!input) {
      throw new ValidationError('Input is required');
    }
  }

  _processBusinessLogic(input) {
    // Implementation
  }

  _formatResult(result) {
    // Implementation
  }
}
```

### 2. Error Handling Standards

#### Custom Error Classes
```javascript
// Base error class
class LoyaltyError extends Error {
  constructor(message, code, statusCode = 500) {
    super(message);
    this.name = this.constructor.name;
    this.code = code;
    this.statusCode = statusCode;
    this.timestamp = new Date().toISOString();
  }
}

// Specific error types
class ValidationError extends LoyaltyError {
  constructor(message, field = null) {
    super(message, 'VALIDATION_ERROR', 400);
    this.field = field;
  }
}

class BusinessRuleError extends LoyaltyError {
  constructor(message) {
    super(message, 'BUSINESS_RULE_ERROR', 422);
  }
}

class NotFoundError extends LoyaltyError {
  constructor(resource, id) {
    super(`${resource} with ID ${id} not found`, 'NOT_FOUND', 404);
    this.resource = resource;
    this.id = id;
  }
}
```

#### Error Handling Pattern
```javascript
// Use case implementation
class RegisterUserUseCase {
  async execute(input) {
    try {
      // Validate input
      await this._validateInput(input);
      
      // Check business rules
      await this._validateBusinessRules(input);
      
      // Execute use case
      const result = await this._executeCore(input);
      
      // Return success
      return {
        success: true,
        data: result,
        message: 'User registered successfully'
      };
    } catch (error) {
      // Log error with context
      this.logger.error('User registration failed', {
        error: error.message,
        stack: error.stack,
        input: this._sanitizeInput(input),
        correlationId: input.correlationId
      });
      
      // Re-throw for upper layers to handle
      throw error;
    }
  }
}

// Controller error handling
class UserController {
  async register(req, res, next) {
    try {
      const result = await this.registerUserUseCase.execute(req.body);
      res.status(201).json(result);
    } catch (error) {
      next(error); // Pass to error middleware
    }
  }
}

// Error middleware
const errorHandler = (error, req, res, next) => {
  const response = {
    success: false,
    error: {
      code: error.code || 'INTERNAL_ERROR',
      message: error.message,
      timestamp: error.timestamp || new Date().toISOString()
    }
  };

  // Add validation details if available
  if (error instanceof ValidationError && error.field) {
    response.error.field = error.field;
  }

  // Add correlation ID for tracing
  if (req.correlationId) {
    response.error.correlationId = req.correlationId;
  }

  res.status(error.statusCode || 500).json(response);
};
```

### 3. Logging Standards

#### Structured Logging
```javascript
// Logger configuration
const logger = winston.createLogger({
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.errors({ stack: true }),
    winston.format.json()
  ),
  defaultMeta: {
    service: 'user-service',
    version: '1.0.0'
  },
  transports: [
    new winston.transports.Console(),
    new winston.transports.File({ filename: 'app.log' })
  ]
});

// Logging usage
class UserService {
  async registerUser(userData) {
    const correlationId = userData.correlationId || uuid.v4();
    
    logger.info('User registration started', {
      correlationId,
      userId: userData.id,
      email: userData.email,
      source: userData.source
    });

    try {
      const user = await this.userRepository.save(userData);
      
      logger.info('User registration completed', {
        correlationId,
        userId: user.id,
        duration: Date.now() - startTime
      });
      
      return user;
    } catch (error) {
      logger.error('User registration failed', {
        correlationId,
        error: error.message,
        stack: error.stack,
        userData: this._sanitizeUserData(userData)
      });
      
      throw error;
    }
  }
}
```

#### Log Levels
- **ERROR:** System errors, exceptions, critical failures
- **WARN:** Recoverable errors, deprecated API usage, performance issues
- **INFO:** Important business events, user actions, system state changes
- **DEBUG:** Detailed execution flow, variable values (development only)

---

## 🔒 Security Standards

### 1. Authentication & Authorization

#### JWT Token Implementation
```javascript
// Token service
class TokenService {
  generateAccessToken(user) {
    return jwt.sign(
      {
        userId: user.id,
        email: user.email,
        roles: user.roles
      },
      process.env.JWT_SECRET,
      {
        expiresIn: '15m',
        issuer: 'loyalty-system',
        audience: 'loyalty-users'
      }
    );
  }

  generateRefreshToken(user) {
    return jwt.sign(
      { userId: user.id },
      process.env.JWT_REFRESH_SECRET,
      {
        expiresIn: '7d',
        issuer: 'loyalty-system'
      }
    );
  }

  verifyToken(token, secret = process.env.JWT_SECRET) {
    try {
      return jwt.verify(token, secret);
    } catch (error) {
      throw new AuthenticationError('Invalid token');
    }
  }
}

// Authentication middleware
const authenticateToken = (req, res, next) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];

  if (!token) {
    return res.status(401).json({
      success: false,
      error: { code: 'MISSING_TOKEN', message: 'Access token required' }
    });
  }

  try {
    const decoded = tokenService.verifyToken(token);
    req.user = decoded;
    next();
  } catch (error) {
    return res.status(403).json({
      success: false,
      error: { code: 'INVALID_TOKEN', message: 'Invalid access token' }
    });
  }
};
```

#### Role-Based Access Control
```javascript
// Permission definitions
const PERMISSIONS = {
  USER_READ: 'user:read',
  USER_WRITE: 'user:write',
  POINTS_ADJUST: 'points:adjust',
  REWARDS_MANAGE: 'rewards:manage',
  ADMIN_ALL: 'admin:*'
};

const ROLES = {
  CUSTOMER: [PERMISSIONS.USER_READ],
  CUSTOMER_SERVICE: [PERMISSIONS.USER_READ, PERMISSIONS.USER_WRITE, PERMISSIONS.POINTS_ADJUST],
  ADMIN: [PERMISSIONS.USER_READ, PERMISSIONS.USER_WRITE, PERMISSIONS.REWARDS_MANAGE],
  SUPER_ADMIN: [PERMISSIONS.ADMIN_ALL]
};

// Authorization middleware
const requirePermission = (permission) => {
  return (req, res, next) => {
    const userRoles = req.user.roles || [];
    const userPermissions = userRoles.flatMap(role => ROLES[role] || []);

    if (!userPermissions.includes(permission) && !userPermissions.includes(PERMISSIONS.ADMIN_ALL)) {
      return res.status(403).json({
        success: false,
        error: {
          code: 'INSUFFICIENT_PERMISSIONS',
          message: `Required permission: ${permission}`
        }
      });
    }

    next();
  };
};
```

### 2. Input Validation & Sanitization

#### Validation Schema
```javascript
// Joi validation schemas
const userRegistrationSchema = Joi.object({
  email: Joi.string().email().required().max(255),
  phone: Joi.string().pattern(/^\+[1-9]\d{1,14}$/).optional(),
  password: Joi.string().min(8).max(128).required()
    .pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/)
    .message('Password must contain at least one uppercase letter, lowercase letter, number, and special character'),
  firstName: Joi.string().min(1).max(100).required().trim(),
  lastName: Joi.string().min(1).max(100).required().trim(),
  dateOfBirth: Joi.date().max('now').optional()
});

// Validation middleware
const validateRequest = (schema) => {
  return (req, res, next) => {
    const { error, value } = schema.validate(req.body, {
      abortEarly: false,
      stripUnknown: true
    });

    if (error) {
      const validationErrors = error.details.map(detail => ({
        field: detail.path.join('.'),
        message: detail.message
      }));

      return res.status(400).json({
        success: false,
        error: {
          code: 'VALIDATION_ERROR',
          message: 'Invalid input data',
          details: validationErrors
        }
      });
    }

    req.body = value; // Use sanitized data
    next();
  };
};
```

### 3. Data Protection

#### Sensitive Data Handling
```javascript
// Encryption utilities
class EncryptionService {
  static encrypt(text, key = process.env.ENCRYPTION_KEY) {
    const algorithm = 'aes-256-gcm';
    const iv = crypto.randomBytes(16);
    const cipher = crypto.createCipher(algorithm, key);
    
    let encrypted = cipher.update(text, 'utf8', 'hex');
    encrypted += cipher.final('hex');
    
    const authTag = cipher.getAuthTag();
    
    return {
      encrypted,
      iv: iv.toString('hex'),
      authTag: authTag.toString('hex')
    };
  }

  static decrypt(encryptedData, key = process.env.ENCRYPTION_KEY) {
    const algorithm = 'aes-256-gcm';
    const decipher = crypto.createDecipher(algorithm, key);
    
    decipher.setAuthTag(Buffer.from(encryptedData.authTag, 'hex'));
    
    let decrypted = decipher.update(encryptedData.encrypted, 'hex', 'utf8');
    decrypted += decipher.final('utf8');
    
    return decrypted;
  }
}

// Data masking for logs
class DataMasker {
  static maskEmail(email) {
    if (!email) return email;
    const [username, domain] = email.split('@');
    return `${username.charAt(0)}***@${domain}`;
  }

  static maskPhone(phone) {
    if (!phone) return phone;
    return phone.replace(/(\d{3})\d{4}(\d{3})/, '$1****$2');
  }

  static maskCreditCard(cardNumber) {
    if (!cardNumber) return cardNumber;
    return cardNumber.replace(/\d(?=\d{4})/g, '*');
  }
}
```

---

## 🧪 Testing Standards

### 1. Test Strategy

#### Testing Pyramid
```
                    E2E Tests (5%)
                   /             \
                  /               \
             Integration Tests (25%)
            /                       \
           /                         \
      Unit Tests (70%)
```

#### Test Categories
- **Unit Tests (70%):** Domain logic, use cases, utilities
- **Integration Tests (25%):** Database operations, external services
- **End-to-End Tests (5%):** Complete user workflows

### 2. Unit Testing Standards

#### Test Structure
```javascript
// Test file naming: [ComponentName].test.js
describe('UserRegistrationService', () => {
  let userRegistrationService;
  let mockUserRepository;
  let mockEmailService;

  beforeEach(() => {
    // Setup test doubles
    mockUserRepository = {
      findByEmail: jest.fn(),
      save: jest.fn()
    };
    mockEmailService = {
      sendVerificationEmail: jest.fn()
    };

    // Create service instance
    userRegistrationService = new UserRegistrationService(
      mockUserRepository,
      mockEmailService
    );
  });

  describe('registerUser', () => {
    it('should register user successfully when valid data provided', async () => {
      // Arrange
      const userData = {
        email: 'test@example.com',
        password: 'ValidPass123!',
        firstName: 'John',
        lastName: 'Doe'
      };

      mockUserRepository.findByEmail.mockResolvedValue(null);
      mockUserRepository.save.mockResolvedValue({ id: '123', ...userData });

      // Act
      const result = await userRegistrationService.registerUser(userData);

      // Assert
      expect(result).toHaveProperty('id', '123');
      expect(result.email).toBe(userData.email);
      expect(mockUserRepository.save).toHaveBeenCalledWith(
        expect.objectContaining({
          email: userData.email,
          firstName: userData.firstName,
          lastName: userData.lastName
        })
      );
      expect(mockEmailService.sendVerificationEmail).toHaveBeenCalledWith(result);
    });

    it('should throw error when user already exists', async () => {
      // Arrange
      const userData = { email: 'existing@example.com' };
      mockUserRepository.findByEmail.mockResolvedValue({ id: '456' });

      // Act & Assert
      await expect(userRegistrationService.registerUser(userData))
        .rejects.toThrow('User already exists with this email');
      
      expect(mockUserRepository.save).not.toHaveBeenCalled();
      expect(mockEmailService.sendVerificationEmail).not.toHaveBeenCalled();
    });
  });
});
```

#### Test Naming Convention
```javascript
// Pattern: should_[expected outcome]_when_[condition]
it('should_return_user_balance_when_valid_user_id_provided', () => {});
it('should_throw_validation_error_when_user_id_is_null', () => {});
it('should_update_cache_when_balance_changes', () => {});
```

### 3. Integration Testing

#### Database Integration Tests
```javascript
describe('UserRepository Integration', () => {
  let database;
  let userRepository;

  beforeAll(async () => {
    // Setup test database
    database = await createTestDatabase();
    userRepository = new PostgresUserRepository(database);
  });

  beforeEach(async () => {
    // Clean database before each test
    await database.query('TRUNCATE TABLE users CASCADE');
  });

  afterAll(async () => {
    await database.close();
  });

  it('should save and retrieve user correctly', async () => {
    // Arrange
    const user = new User({
      email: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe'
    });

    // Act
    const savedUser = await userRepository.save(user);
    const retrievedUser = await userRepository.findById(savedUser.id);

    // Assert
    expect(retrievedUser).not.toBeNull();
    expect(retrievedUser.email).toBe(user.email);
    expect(retrievedUser.firstName).toBe(user.firstName);
  });
});
```

### 4. Performance Testing

#### Load Testing Guidelines
```javascript
// Performance test example using Jest
describe('Point Calculation Performance', () => {
  it('should calculate points for 1000 transactions within 3 seconds', async () => {
    const transactions = generateMockTransactions(1000);
    const startTime = Date.now();

    const results = await Promise.all(
      transactions.map(tx => pointCalculationService.calculate(tx))
    );

    const endTime = Date.now();
    const duration = endTime - startTime;

    expect(duration).toBeLessThan(3000); // 3 seconds max
    expect(results).toHaveLength(1000);
    expect(results.every(r => r.points > 0)).toBe(true);
  });
});
```

---

## 📊 Performance Standards

### 1. Response Time Requirements

| Operation Type | Target Response Time | Maximum Response Time |
|----------------|---------------------|---------------------|
| User Authentication | < 1 second | 2 seconds |
| Point Balance Query | < 500ms | 1 second |
| Point Earning | < 2 seconds | 3 seconds |
| Point Redemption | < 3 seconds | 5 seconds |
| Admin Dashboard | < 2 seconds | 4 seconds |

### 2. Caching Strategy

#### Cache Layers
```javascript
// Redis caching implementation
class CacheService {
  constructor(redisClient) {
    this.redis = redisClient;
    this.defaultTTL = 300; // 5 minutes
  }

  async get(key) {
    try {
      const data = await this.redis.get(key);
      return data ? JSON.parse(data) : null;
    } catch (error) {
      logger.warn('Cache get failed', { key, error: error.message });
      return null;
    }
  }

  async set(key, value, ttl = this.defaultTTL) {
    try {
      await this.redis.setex(key, ttl, JSON.stringify(value));
    } catch (error) {
      logger.warn('Cache set failed', { key, error: error.message });
    }
  }

  async del(key) {
    try {
      await this.redis.del(key);
    } catch (error) {
      logger.warn('Cache delete failed', { key, error: error.message });
    }
  }
}

// Cache-aside pattern implementation
class UserService {
  async getUserById(id) {
    const cacheKey = `user:${id}`;
    
    // Try cache first
    let user = await this.cacheService.get(cacheKey);
    
    if (!user) {
      // Cache miss - get from database
      user = await this.userRepository.findById(id);
      
      if (user) {
        // Cache the result
        await this.cacheService.set(cacheKey, user, 600); // 10 minutes
      }
    }
    
    return user;
  }

  async updateUser(id, updates) {
    const user = await this.userRepository.update(id, updates);
    
    // Invalidate cache
    await this.cacheService.del(`user:${id}`);
    
    return user;
  }
}
```

#### Cache TTL Guidelines
- **User profiles:** 10 minutes
- **Point balances:** 5 minutes
- **Rewards catalog:** 30 minutes
- **System configuration:** 60 minutes
- **Session data:** 15 minutes

### 3. Database Optimization

#### Query Performance
```sql
-- Index strategy for point transactions
CREATE INDEX CONCURRENTLY idx_point_transactions_user_id_created_at 
ON point_transactions (user_id, created_at DESC);

CREATE INDEX CONCURRENTLY idx_point_transactions_type_status 
ON point_transactions (transaction_type, status) 
WHERE status = 'PROCESSED';

-- Partitioning for large tables
CREATE TABLE point_transactions_2024 PARTITION OF point_transactions
FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');
```

---

## 📈 Monitoring & Observability

### 1. Metrics Collection

#### Application Metrics
```javascript
// Prometheus metrics
const prometheus = require('prom-client');

// Counter metrics
const requestCounter = new prometheus.Counter({
  name: 'http_requests_total',
  help: 'Total number of HTTP requests',
  labelNames: ['method', 'route', 'status_code']
});

const pointsEarnedCounter = new prometheus.Counter({
  name: 'points_earned_total',
  help: 'Total points earned by users',
  labelNames: ['user_tier', 'earning_type']
});

// Histogram metrics
const responseTimeHistogram = new prometheus.Histogram({
  name: 'http_request_duration_seconds',
  help: 'Duration of HTTP requests in seconds',
  labelNames: ['method', 'route', 'status_code'],
  buckets: [0.1, 0.3, 0.5, 0.7, 1, 3, 5, 7, 10]
});

// Gauge metrics
const activeUsersGauge = new prometheus.Gauge({
  name: 'active_users_current',
  help: 'Current number of active users'
});

// Metrics middleware
const metricsMiddleware = (req, res, next) => {
  const startTime = Date.now();
  
  res.on('finish', () => {
    const duration = (Date.now() - startTime) / 1000;
    const route = req.route ? req.route.path : req.path;
    
    requestCounter.inc({
      method: req.method,
      route: route,
      status_code: res.statusCode
    });
    
    responseTimeHistogram.observe(
      { method: req.method, route: route, status_code: res.statusCode },
      duration
    );
  });
  
  next();
};
```

### 2. Health Checks

#### Comprehensive Health Check
```javascript
class HealthCheckService {
  constructor(dependencies) {
    this.database = dependencies.database;
    this.redis = dependencies.redis;
    this.messageQueue = dependencies.messageQueue;
    this.externalServices = dependencies.externalServices;
  }

  async getHealthStatus() {
    const checks = await Promise.allSettled([
      this.checkDatabase(),
      this.checkRedis(),
      this.checkMessageQueue(),
      this.checkExternalServices(),
      this.checkSystemResources()
    ]);

    const results = {
      status: 'healthy',
      timestamp: new Date().toISOString(),
      version: process.env.APP_VERSION,
      checks: {}
    };

    checks.forEach((check, index) => {
      const checkNames = ['database', 'redis', 'messageQueue', 'externalServices', 'systemResources'];
      const checkName = checkNames[index];
      
      if (check.status === 'fulfilled') {
        results.checks[checkName] = check.value;
      } else {
        results.checks[checkName] = {
          status: 'unhealthy',
          error: check.reason.message
        };
        results.status = 'unhealthy';
      }
    });

    return results;
  }

  async checkDatabase() {
    const startTime = Date.now();
    await this.database.query('SELECT 1');
    const responseTime = Date.now() - startTime;

    return {
      status: 'healthy',
      responseTime: `${responseTime}ms`,
      message: 'Database connection successful'
    };
  }

  async checkRedis() {
    const startTime = Date.now();
    await this.redis.ping();
    const responseTime = Date.now() - startTime;

    return {
      status: 'healthy',
      responseTime: `${responseTime}ms`,
      message: 'Redis connection successful'
    };
  }

  async checkSystemResources() {
    const memoryUsage = process.memoryUsage();
    const cpuUsage = process.cpuUsage();

    return {
      status: 'healthy',
      memory: {
        used: `${Math.round(memoryUsage.heapUsed / 1024 / 1024)}MB`,
        total: `${Math.round(memoryUsage.heapTotal / 1024 / 1024)}MB`
      },
      cpu: {
        user: `${cpuUsage.user}μs`,
        system: `${cpuUsage.system}μs`
      }
    };
  }
}
```

---

## 🚀 Deployment Standards

### 1. Environment Configuration

#### Environment Variables
```bash
# Application
NODE_ENV=production
APP_NAME=loyalty-user-service
APP_VERSION=1.0.0
PORT=3000

# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=loyalty
DB_USERNAME=loyalty_user
DB_PASSWORD=secure_password
DB_SSL=true
DB_POOL_MIN=2
DB_POOL_MAX=10

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=redis_password
REDIS_DB=0

# JWT
JWT_SECRET=your-super-secret-jwt-key
JWT_REFRESH_SECRET=your-refresh-token-secret
JWT_EXPIRES_IN=15m
JWT_REFRESH_EXPIRES_IN=7d

# External Services
EMAIL_SERVICE_API_KEY=ses_api_key
SMS_SERVICE_API_KEY=twilio_api_key
FILE_STORAGE_BUCKET=loyalty-files-bucket

# Monitoring
LOG_LEVEL=info
METRICS_PORT=9090
HEALTH_CHECK_PORT=8080
```

### 2. Docker Best Practices

#### Multi-stage Dockerfile
```dockerfile
# Build stage
FROM node:18-alpine AS builder

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci --only=production && npm cache clean --force

# Copy source code
COPY src/ ./src/

# Build application
RUN npm run build

# Production stage
FROM node:18-alpine AS production

# Install security updates
RUN apk update && apk upgrade

# Create non-root user
RUN addgroup -g 1001 -S loyalty && \
    adduser -S loyalty -u 1001

# Set working directory
WORKDIR /app

# Copy built application
COPY --from=builder --chown=loyalty:loyalty /app/node_modules ./node_modules
COPY --from=builder --chown=loyalty:loyalty /app/dist ./dist
COPY --from=builder --chown=loyalty:loyalty /app/package.json ./package.json

# Switch to non-root user
USER loyalty

# Expose port
EXPOSE 3000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:3000/health || exit 1

# Start application
CMD ["node", "dist/server.js"]
```

### 3. CI/CD Pipeline

#### GitHub Actions Workflow
```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: test_password
          POSTGRES_DB: loyalty_test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
      
      redis:
        image: redis:7-alpine
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
    - uses: actions/checkout@v4
    
    - name: Setup Java
      uses: actions/setup-java@v3
      with:
        distribution: 'temurin'
        java-version: '17'
        cache: 'maven'
    
    - name: Install dependencies
      run: mvn dependency:resolve
    
    - name: Run linting
      run: mvn checkstyle:check
    
    - name: Run unit tests
      run: mvn test
      
    - name: Run integration tests
      run: npm run test:integration
      env:
        DB_HOST: localhost
        DB_PORT: 5432
        DB_NAME: loyalty_test
        DB_USERNAME: postgres
        DB_PASSWORD: test_password
        REDIS_HOST: localhost
        REDIS_PORT: 6379
    
    - name: Generate coverage report
      run: npm run test:coverage
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
      
    - name: Security audit
      run: npm audit --audit-level moderate

  build:
    needs: test
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v3
    
    - name: Login to Container Registry
      uses: docker/login-action@v3
      with:
        registry: ghcr.io
        username: ${{ github.actor }}
        password: ${{ secrets.GITHUB_TOKEN }}
    
    - name: Build and push Docker image
      uses: docker/build-push-action@v5
      with:
        context: .
        push: true
        tags: |
          ghcr.io/${{ github.repository }}:latest
          ghcr.io/${{ github.repository }}:${{ github.sha }}
        cache-from: type=gha
        cache-to: type=gha,mode=max

  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
    - name: Deploy to production
      run: |
        # Deployment script would go here
        echo "Deploying to production..."
```

---

## 📚 Documentation Standards

### 1. API Documentation

#### OpenAPI Specification
```yaml
openapi: 3.0.0
info:
  title: Loyalty System API
  description: User management and loyalty program API
  version: 1.0.0
  contact:
    name: Development Team
    email: dev-team@loyalty.com

servers:
  - url: https://api.loyalty.com/v1
    description: Production server
  - url: https://staging-api.loyalty.com/v1
    description: Staging server

security:
  - BearerAuth: []

paths:
  /users/register:
    post:
      summary: Register a new user
      description: Create a new user account with email verification
      tags: [Users]
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UserRegistration'
            example:
              email: user@example.com
              password: SecurePass123!
              firstName: John
              lastName: Doe
              phone: "+1234567890"
      responses:
        201:
          description: User registered successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SuccessResponse'
        400:
          description: Validation error
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
        409:
          description: User already exists
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
  
  schemas:
    UserRegistration:
      type: object
      required: [email, password, firstName, lastName]
      properties:
        email:
          type: string
          format: email
          example: user@example.com
        password:
          type: string
          minLength: 8
          pattern: '^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]'
        firstName:
          type: string
          minLength: 1
          maxLength: 100
        lastName:
          type: string
          minLength: 1
          maxLength: 100
        phone:
          type: string
          pattern: '^\+[1-9]\d{1,14}$'
          example: "+1234567890"
    
    SuccessResponse:
      type: object
      properties:
        success:
          type: boolean
          example: true
        data:
          type: object
        message:
          type: string
    
    ErrorResponse:
      type: object
      properties:
        success:
          type: boolean
          example: false
        error:
          type: object
          properties:
            code:
              type: string
            message:
              type: string
            details:
              type: array
              items:
                type: object
```

### 2. Code Documentation

#### Function Documentation
```javascript
/**
 * Calculates points earned based on transaction amount and user tier
 * 
 * @param {Object} transaction - Transaction details
 * @param {string} transaction.userId - Unique user identifier
 * @param {number} transaction.amount - Transaction amount in cents
 * @param {string} transaction.type - Transaction type (PURCHASE, REFERRAL, BONUS)
 * @param {string} transaction.merchantId - Merchant identifier
 * @param {Object} options - Calculation options
 * @param {boolean} options.applyMultipliers - Whether to apply bonus multipliers
 * @param {string} options.campaignId - Optional campaign identifier
 * 
 * @returns {Promise<Object>} Calculation result
 * @returns {number} returns.pointsEarned - Points calculated for the transaction
 * @returns {number} returns.multiplierApplied - Multiplier used in calculation
 * @returns {string} returns.ruleId - Earning rule ID used
 * @returns {Object} returns.breakdown - Detailed calculation breakdown
 * 
 * @throws {ValidationError} When transaction data is invalid
 * @throws {BusinessRuleError} When transaction violates business rules
 * @throws {NotFoundError} When user or merchant is not found
 * 
 * @example
 * const result = await calculatePoints({
 *   userId: 'user123',
 *   amount: 10000, // $100.00
 *   type: 'PURCHASE',
 *   merchantId: 'merchant456'
 * });
 * console.log(result.pointsEarned); // 100
 */
async function calculatePoints(transaction, options = {}) {
  // Implementation
}
```

---

This comprehensive development guidelines document ensures consistency, quality, and maintainability across the entire loyalty system development. All team members should follow these standards to ensure a cohesive and professional codebase.