# User Service Template - Node.js/Express

**Technology Stack:** Node.js, Express.js, PostgreSQL, Redis, JWT  
**Architecture Pattern:** Clean Architecture (Domain-Driven Design)  
**Purpose:** User management, authentication, profile management  

---

## 📁 Project Structure

```
user-service/
├── src/
│   ├── domain/                 # Business Logic Layer
│   │   ├── entities/          # Domain entities
│   │   │   ├── User.js
│   │   │   ├── Profile.js
│   │   │   └── AuthToken.js
│   │   ├── repositories/      # Repository interfaces
│   │   │   ├── UserRepository.js
│   │   │   └── AuthRepository.js
│   │   ├── services/         # Domain services
│   │   │   ├── AuthService.js
│   │   │   ├── ProfileService.js
│   │   │   └── NotificationService.js
│   │   └── value-objects/    # Value objects
│   │       ├── Email.js
│   │       ├── Phone.js
│   │       └── Password.js
│   │
│   ├── application/           # Application Logic Layer
│   │   ├── use-cases/        # Use case implementations
│   │   │   ├── RegisterUser.js
│   │   │   ├── AuthenticateUser.js
│   │   │   ├── UpdateProfile.js
│   │   │   └── ResetPassword.js
│   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── UserDTO.js
│   │   │   └── ProfileDTO.js
│   │   └── validators/       # Input validation
│   │       ├── UserValidator.js
│   │       └── ProfileValidator.js
│   │
│   ├── infrastructure/        # Infrastructure Layer
│   │   ├── database/         # Database implementations
│   │   │   ├── migrations/
│   │   │   ├── repositories/
│   │   │   │   ├── PostgresUserRepository.js
│   │   │   │   └── RedisAuthRepository.js
│   │   │   └── models/
│   │   │       ├── UserModel.js
│   │   │       └── ProfileModel.js
│   │   ├── external/         # External service integrations
│   │   │   ├── EmailService.js
│   │   │   ├── SMSService.js
│   │   │   └── ImageUploadService.js
│   │   ├── messaging/        # Message queue handling
│   │   │   ├── EventPublisher.js
│   │   │   └── EventHandler.js
│   │   └── config/           # Configuration
│   │       ├── database.js
│   │       ├── redis.js
│   │       └── jwt.js
│   │
│   ├── interfaces/            # Interface Layer (Controllers)
│   │   ├── http/             # HTTP controllers
│   │   │   ├── controllers/
│   │   │   │   ├── UserController.js
│   │   │   │   ├── AuthController.js
│   │   │   │   └── ProfileController.js
│   │   │   ├── middleware/
│   │   │   │   ├── AuthMiddleware.js
│   │   │   │   ├── ValidationMiddleware.js
│   │   │   │   └── RateLimitMiddleware.js
│   │   │   ├── routes/
│   │   │   │   ├── userRoutes.js
│   │   │   │   ├── authRoutes.js
│   │   │   │   └── profileRoutes.js
│   │   │   └── app.js
│   │   └── messaging/        # Message consumers
│   │       └── EventConsumer.js
│   │
│   ├── shared/               # Shared utilities
│   │   ├── errors/          # Custom error classes
│   │   ├── logger/          # Logging utilities
│   │   ├── utils/           # Helper functions
│   │   └── constants/       # Application constants
│   │
│   └── server.js            # Application entry point
│
├── tests/                   # Test files
│   ├── unit/               # Unit tests
│   ├── integration/        # Integration tests
│   ├── e2e/               # End-to-end tests
│   └── fixtures/          # Test data fixtures
│
├── docker/                 # Docker configurations
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── .dockerignore
│
├── docs/                   # Service documentation
│   ├── api/               # API documentation
│   ├── deployment/        # Deployment guides
│   └── development/       # Development guides
│
├── scripts/                # Utility scripts
│   ├── setup.sh
│   ├── migrate.js
│   └── seed.js
│
├── .env.example            # Environment variables template
├── package.json            # Dependencies and scripts
├── jest.config.js         # Test configuration
├── .eslintrc.js           # Linting configuration
├── .gitignore
└── README.md              # Service documentation
```

## 🔧 Core Components

### 1. Domain Layer (`/src/domain/`)

#### User Entity
```javascript
// src/domain/entities/User.js
class User {
  constructor({
    id,
    email,
    phone,
    password,
    isVerified = false,
    isActive = true,
    createdAt = new Date(),
    updatedAt = new Date()
  }) {
    this.id = id;
    this.email = email;
    this.phone = phone;
    this.password = password;
    this.isVerified = isVerified;
    this.isActive = isActive;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  verify() {
    this.isVerified = true;
    this.updatedAt = new Date();
  }

  deactivate() {
    this.isActive = false;
    this.updatedAt = new Date();
  }

  updatePassword(newPassword) {
    this.password = newPassword;
    this.updatedAt = new Date();
  }
}

module.exports = User;
```

#### Repository Interface
```javascript
// src/domain/repositories/UserRepository.js
class UserRepository {
  async save(user) {
    throw new Error('Method must be implemented');
  }

  async findById(id) {
    throw new Error('Method must be implemented');
  }

  async findByEmail(email) {
    throw new Error('Method must be implemented');
  }

  async findByPhone(phone) {
    throw new Error('Method must be implemented');
  }

  async update(id, userData) {
    throw new Error('Method must be implemented');
  }

  async delete(id) {
    throw new Error('Method must be implemented');
  }
}

module.exports = UserRepository;
```

### 2. Application Layer (`/src/application/`)

#### Use Case Example
```javascript
// src/application/use-cases/RegisterUser.js
const User = require('../../domain/entities/User');
const Email = require('../../domain/value-objects/Email');
const Password = require('../../domain/value-objects/Password');

class RegisterUser {
  constructor(userRepository, authService, eventPublisher) {
    this.userRepository = userRepository;
    this.authService = authService;
    this.eventPublisher = eventPublisher;
  }

  async execute({ email, phone, password, firstName, lastName }) {
    // Validate input
    const emailVO = new Email(email);
    const passwordVO = new Password(password);

    // Check if user already exists
    const existingUser = await this.userRepository.findByEmail(email);
    if (existingUser) {
      throw new Error('User already exists with this email');
    }

    // Hash password
    const hashedPassword = await this.authService.hashPassword(password);

    // Create user entity
    const user = new User({
      email: emailVO.value,
      phone,
      password: hashedPassword,
      firstName,
      lastName
    });

    // Save user
    const savedUser = await this.userRepository.save(user);

    // Send verification email
    await this.authService.sendVerificationEmail(savedUser);

    // Publish event
    await this.eventPublisher.publish('user.registered', {
      userId: savedUser.id,
      email: savedUser.email,
      timestamp: new Date()
    });

    return {
      id: savedUser.id,
      email: savedUser.email,
      isVerified: savedUser.isVerified
    };
  }
}

module.exports = RegisterUser;
```

### 3. Infrastructure Layer (`/src/infrastructure/`)

#### Database Repository Implementation
```javascript
// src/infrastructure/database/repositories/PostgresUserRepository.js
const UserRepository = require('../../../domain/repositories/UserRepository');
const User = require('../../../domain/entities/User');

class PostgresUserRepository extends UserRepository {
  constructor(dbConnection) {
    super();
    this.db = dbConnection;
  }

  async save(user) {
    const query = `
      INSERT INTO users (email, phone, password, first_name, last_name, is_verified, is_active)
      VALUES ($1, $2, $3, $4, $5, $6, $7)
      RETURNING *
    `;
    
    const values = [
      user.email,
      user.phone,
      user.password,
      user.firstName,
      user.lastName,
      user.isVerified,
      user.isActive
    ];

    const result = await this.db.query(query, values);
    return this._mapToEntity(result.rows[0]);
  }

  async findById(id) {
    const query = 'SELECT * FROM users WHERE id = $1';
    const result = await this.db.query(query, [id]);
    
    if (result.rows.length === 0) {
      return null;
    }

    return this._mapToEntity(result.rows[0]);
  }

  _mapToEntity(row) {
    return new User({
      id: row.id,
      email: row.email,
      phone: row.phone,
      password: row.password,
      firstName: row.first_name,
      lastName: row.last_name,
      isVerified: row.is_verified,
      isActive: row.is_active,
      createdAt: row.created_at,
      updatedAt: row.updated_at
    });
  }
}

module.exports = PostgresUserRepository;
```

### 4. Interface Layer (`/src/interfaces/`)

#### HTTP Controller
```javascript
// src/interfaces/http/controllers/UserController.js
class UserController {
  constructor(registerUserUseCase, authenticateUserUseCase) {
    this.registerUserUseCase = registerUserUseCase;
    this.authenticateUserUseCase = authenticateUserUseCase;
  }

  async register(req, res, next) {
    try {
      const { email, phone, password, firstName, lastName } = req.body;
      
      const result = await this.registerUserUseCase.execute({
        email,
        phone,
        password,
        firstName,
        lastName
      });

      res.status(201).json({
        success: true,
        data: result,
        message: 'User registered successfully'
      });
    } catch (error) {
      next(error);
    }
  }

  async login(req, res, next) {
    try {
      const { email, password } = req.body;
      
      const result = await this.authenticateUserUseCase.execute({
        email,
        password
      });

      res.status(200).json({
        success: true,
        data: result,
        message: 'Login successful'
      });
    } catch (error) {
      next(error);
    }
  }
}

module.exports = UserController;
```

## 📦 Package.json Template

```json
{
  "name": "loyalty-user-service",
  "version": "1.0.0",
  "description": "User management service for loyalty system",
  "main": "src/server.js",
  "scripts": {
    "start": "node src/server.js",
    "dev": "nodemon src/server.js",
    "test": "jest",
    "test:watch": "jest --watch",
    "test:coverage": "jest --coverage",
    "lint": "eslint src/",
    "lint:fix": "eslint src/ --fix",
    "migrate": "node scripts/migrate.js",
    "seed": "node scripts/seed.js",
    "docker:build": "docker build -t loyalty-user-service .",
    "docker:run": "docker run -p 3001:3001 loyalty-user-service"
  },
  "dependencies": {
    "express": "^4.18.2",
    "cors": "^2.8.5",
    "helmet": "^7.1.0",
    "morgan": "^1.10.0",
    "pg": "^8.11.3",
    "redis": "^4.6.10",
    "jsonwebtoken": "^9.0.2",
    "bcrypt": "^5.1.1",
    "joi": "^17.11.0",
    "winston": "^3.11.0",
    "express-rate-limit": "^7.1.5",
    "amqplib": "^0.10.3",
    "aws-sdk": "^2.1490.0",
    "nodemailer": "^6.9.7",
    "twilio": "^4.17.0",
    "multer": "^1.4.5-lts.1"
  },
  "devDependencies": {
    "nodemon": "^3.0.1",
    "jest": "^29.7.0",
    "supertest": "^6.3.3",
    "eslint": "^8.53.0",
    "eslint-config-airbnb-base": "^15.0.0",
    "eslint-plugin-import": "^2.29.0"
  },
  "engines": {
    "node": ">=18.0.0"
  }
}
```

## 🐳 Docker Configuration

```dockerfile
# docker/Dockerfile
FROM node:18-alpine

WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm ci --only=production

# Copy source code
COPY src/ ./src/
COPY scripts/ ./scripts/

# Create non-root user
RUN addgroup -g 1001 -S loyalty && \
    adduser -S loyalty -u 1001

USER loyalty

EXPOSE 3001

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:3001/health || exit 1

CMD ["npm", "start"]
```

## 📚 API Documentation Template

```yaml
# docs/api/openapi.yaml
openapi: 3.0.0
info:
  title: User Service API
  description: User management and authentication service
  version: 1.0.0

paths:
  /api/v1/users/register:
    post:
      summary: Register new user
      tags: [Authentication]
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required: [email, password, firstName, lastName]
              properties:
                email:
                  type: string
                  format: email
                phone:
                  type: string
                password:
                  type: string
                  minLength: 8
                firstName:
                  type: string
                lastName:
                  type: string
      responses:
        201:
          description: User registered successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  success:
                    type: boolean
                  data:
                    type: object
                    properties:
                      id:
                        type: string
                      email:
                        type: string
                      isVerified:
                        type: boolean
        400:
          description: Validation error
        409:
          description: User already exists
```

## 🧪 Test Template

```javascript
// tests/unit/use-cases/RegisterUser.test.js
const RegisterUser = require('../../../src/application/use-cases/RegisterUser');
const User = require('../../../src/domain/entities/User');

describe('RegisterUser Use Case', () => {
  let registerUser;
  let mockUserRepository;
  let mockAuthService;
  let mockEventPublisher;

  beforeEach(() => {
    mockUserRepository = {
      findByEmail: jest.fn(),
      save: jest.fn()
    };
    mockAuthService = {
      hashPassword: jest.fn(),
      sendVerificationEmail: jest.fn()
    };
    mockEventPublisher = {
      publish: jest.fn()
    };

    registerUser = new RegisterUser(
      mockUserRepository,
      mockAuthService,
      mockEventPublisher
    );
  });

  test('should register user successfully', async () => {
    // Arrange
    const userData = {
      email: 'test@example.com',
      phone: '+1234567890',
      password: 'password123',
      firstName: 'John',
      lastName: 'Doe'
    };

    mockUserRepository.findByEmail.mockResolvedValue(null);
    mockAuthService.hashPassword.mockResolvedValue('hashedpassword');
    mockUserRepository.save.mockResolvedValue(new User({
      id: '1',
      ...userData,
      password: 'hashedpassword'
    }));

    // Act
    const result = await registerUser.execute(userData);

    // Assert
    expect(mockUserRepository.findByEmail).toHaveBeenCalledWith(userData.email);
    expect(mockAuthService.hashPassword).toHaveBeenCalledWith(userData.password);
    expect(mockUserRepository.save).toHaveBeenCalled();
    expect(mockEventPublisher.publish).toHaveBeenCalledWith('user.registered', expect.any(Object));
    expect(result).toEqual({
      id: '1',
      email: userData.email,
      isVerified: false
    });
  });

  test('should throw error when user already exists', async () => {
    // Arrange
    const userData = {
      email: 'existing@example.com',
      password: 'password123'
    };

    mockUserRepository.findByEmail.mockResolvedValue(new User({
      id: '1',
      email: userData.email
    }));

    // Act & Assert
    await expect(registerUser.execute(userData)).rejects.toThrow('User already exists with this email');
  });
});
```

## 📋 Development Checklist

### Setup Tasks
- [ ] Initialize Node.js project with dependencies
- [ ] Setup folder structure following clean architecture
- [ ] Configure database connection and migrations
- [ ] Setup Redis for caching and sessions
- [ ] Configure JWT authentication
- [ ] Setup message queue connection
- [ ] Create Docker configuration
- [ ] Setup testing framework

### Implementation Tasks
- [ ] Implement domain entities (User, Profile, AuthToken)
- [ ] Create repository interfaces and implementations
- [ ] Implement use cases (Register, Login, Profile management)
- [ ] Create HTTP controllers and routes
- [ ] Setup authentication middleware
- [ ] Implement validation and error handling
- [ ] Add logging and monitoring
- [ ] Create comprehensive tests

### Integration Tasks
- [ ] Setup email service integration
- [ ] Configure SMS service for verification
- [ ] Setup image upload to S3
- [ ] Implement message queue publishing/consuming
- [ ] Add health check endpoints
- [ ] Setup API documentation
- [ ] Configure CI/CD pipeline

---

**Next Steps:**
1. Use this template to create the user service
2. Implement core domain logic first
3. Add infrastructure implementations
4. Create comprehensive tests
5. Setup CI/CD pipeline