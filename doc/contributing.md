# Contributing to Loyalty System

**Author:** Senior Principal Engineer  
**Version:** 1.0  
**Date:** December 2024  
**Purpose:** Comprehensive contribution guidelines for loyalty system development  
**Scope:** All contributors to the loyalty system project  

---

## 🎯 Welcome Contributors!

Thank you for your interest in contributing to the Loyalty System project! This document provides guidelines for contributing code, documentation, and other improvements to ensure a smooth collaboration experience.

---

## 🚀 Getting Started

### Prerequisites

Before you begin contributing, ensure you have:

- **Java 17** or higher installed
- **Maven 3.8+** for dependency management
- **Docker** and **Docker Compose** for local development
- **Git** for version control
- **IDE** with Java support (IntelliJ IDEA recommended)

### Development Environment Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-org/loyalty-system.git
   cd loyalty-system
   ```

2. **Set up Local Infrastructure**
   ```bash
   # Start PostgreSQL and Redis containers
   docker-compose -f docker-compose.dev.yml up -d
   
   # Wait for services to be ready
   ./scripts/wait-for-services.sh
   ```

3. **Configure Environment Variables**
   ```bash
   cp .env.example .env.local
   # Edit .env.local with your local configuration
   ```

4. **Install Dependencies and Run Tests**
   ```bash
   mvn clean install
   mvn test
   ```

5. **Start the Application**
   ```bash
   mvn spring-boot:run -Dspring.profiles.active=local
   ```

---

## 🏗️ Development Workflow

### Branch Strategy

We follow **Git Flow** branching model:

- `main` - Production-ready code
- `develop` - Integration branch for features
- `feature/*` - New features and enhancements
- `hotfix/*` - Critical fixes for production
- `release/*` - Release preparation

### Feature Development Workflow

1. **Create Feature Branch**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/user-profile-enhancement
   ```

2. **Implement Feature**
   - Follow [development standards](../framework/guidelines/development-standards.md)
   - Write comprehensive tests
   - Update documentation as needed

3. **Commit Changes**
   ```bash
   # Stage your changes
   git add .
   
   # Commit with conventional commit message
   git commit -m "feat(user): add user profile enhancement with validation
   
   - Add email verification for profile updates
   - Implement profile picture upload functionality
   - Add comprehensive validation for user data
   
   Closes #123"
   ```

4. **Push and Create Pull Request**
   ```bash
   git push origin feature/user-profile-enhancement
   ```

   Then create a pull request through GitHub interface.

### Commit Message Convention

We use [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>(<scope>): <description>

[optional body]

[optional footer(s)]
```

#### Commit Types:
- `feat` - New feature
- `fix` - Bug fix
- `docs` - Documentation changes
- `style` - Code style changes (formatting, etc.)
- `refactor` - Code refactoring
- `test` - Adding or updating tests
- `chore` - Maintenance tasks

#### Examples:
```bash
feat(auth): implement JWT refresh token mechanism
fix(points): resolve point calculation rounding error
docs(api): update authentication endpoint documentation
test(user): add comprehensive user service tests
refactor(rewards): extract reward calculation logic
```

---

## 📝 Code Standards

### Java Code Style

#### 1. Naming Conventions
```java
// Classes - PascalCase
public class UserRegistrationService {
    
    // Constants - UPPER_SNAKE_CASE
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    
    // Fields and methods - camelCase
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    public User registerNewUser(CreateUserRequest request) {
        // Local variables - camelCase
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        
        // Method implementation
        return user;
    }
}

// Interfaces - PascalCase with descriptive names
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    List<User> findByStatusAndTier(UserStatus status, UserTier tier);
}

// Enums - PascalCase with UPPER_CASE values
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    PENDING_VERIFICATION
}
```

#### 2. Code Structure and Documentation
```java
/**
 * Service responsible for user registration and account management.
 * 
 * This service handles the complete user registration process including:
 * - Email uniqueness validation
 * - Password encryption
 * - Account activation
 * - Welcome email sending
 * 
 * @author Development Team
 * @since 1.0
 */
@Service
@Transactional
@Slf4j
public class UserRegistrationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    /**
     * Registers a new user with the provided information.
     * 
     * @param request The user registration request containing user details
     * @return The newly created and persisted user
     * @throws ConflictException if email already exists
     * @throws ValidationException if request data is invalid
     */
    public User registerUser(CreateUserRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        
        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists: " + request.getEmail());
        }
        
        // Create user entity
        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .status(UserStatus.PENDING_VERIFICATION)
            .createdDate(Instant.now())
            .build();
        
        // Persist user
        User savedUser = userRepository.save(user);
        
        // Send welcome email
        try {
            emailService.sendWelcomeEmail(savedUser);
            log.info("Welcome email sent to user: {}", savedUser.getId());
        } catch (Exception e) {
            log.warn("Failed to send welcome email to user: {}", savedUser.getId(), e);
            // Don't fail registration if email fails
        }
        
        log.info("User registered successfully with ID: {}", savedUser.getId());
        return savedUser;
    }
}
```

### API Design Standards

#### REST Endpoint Structure
```java
@RestController
@RequestMapping("/api/v1/users")
@Validated
@Tag(name = "Users", description = "User management operations")
public class UserController {
    
    @PostMapping
    @Operation(summary = "Create new user", description = "Register a new user account")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "409", description = "Email already exists")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        
        User user = userService.createUser(request);
        
        return ResponseEntity
            .created(URI.create("/api/v1/users/" + user.getId()))
            .body(ApiResponse.success(UserResponse.from(user), "User created successfully"));
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieve user information by ID")
    @PreAuthorize("hasPermission(#userId, 'USER', 'READ')")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String userId) {
        User user = userService.findById(userId);
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(user)));
    }
}
```

---

## 🧪 Testing Requirements

### Test Coverage Expectations

- **Unit Tests:** Minimum 80% line coverage, 90% branch coverage
- **Integration Tests:** All database operations and external service interactions
- **E2E Tests:** Critical user flows and business processes

### Test Structure

#### Unit Test Example
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private UserService userService;
    
    @Nested
    @DisplayName("User Creation")
    class UserCreation {
        
        @Test
        @DisplayName("Should create user successfully with valid data")
        void should_create_user_successfully_with_valid_data() {
            // Given
            CreateUserRequest request = createValidUserRequest();
            User expectedUser = createUserFromRequest(request);
            
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(expectedUser);
            
            // When
            User result = userService.createUser(request);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(request.getEmail());
            assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
            
            verify(userRepository).save(argThat(user -> 
                user.getEmail().equals(request.getEmail()) &&
                user.getStatus() == UserStatus.ACTIVE
            ));
            verify(emailService).sendWelcomeEmail(result);
        }
        
        @Test
        @DisplayName("Should throw ConflictException when email exists")
        void should_throw_conflict_exception_when_email_exists() {
            // Given
            CreateUserRequest request = createValidUserRequest();
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
            
            // When & Then
            assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email already exists: " + request.getEmail());
            
            verify(userRepository, never()).save(any(User.class));
        }
    }
    
    private CreateUserRequest createValidUserRequest() {
        return CreateUserRequest.builder()
            .email("test@example.com")
            .password("SecurePass123!")
            .firstName("John")
            .lastName("Doe")
            .build();
    }
}
```

#### Integration Test Example
```java
@SpringBootTest
@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
class UserControllerIntegrationTest {
    
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("loyalty_test")
        .withUsername("test")
        .withPassword("test");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    @Order(1)
    void should_create_user_via_rest_endpoint() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
            .email("integration@test.com")
            .password("SecurePass123!")
            .firstName("Integration")
            .lastName("Test")
            .build();
        
        // When
        ResponseEntity<ApiResponse<UserResponse>> response = restTemplate.postForEntity(
            "/api/v1/users", 
            request, 
            new ParameterizedTypeReference<ApiResponse<UserResponse>>() {}
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData().getEmail()).isEqualTo(request.getEmail());
        
        // Verify in database
        Optional<User> savedUser = userRepository.findByEmail(request.getEmail());
        assertThat(savedUser).isPresent();
    }
}
```

---

## 📚 Documentation Requirements

### Code Documentation

1. **All public classes and methods must have Javadoc**
2. **Complex business logic should be commented**
3. **API endpoints must have OpenAPI annotations**
4. **README files for each service/module**

### Documentation Updates

When contributing, ensure you update:

- **API documentation** (OpenAPI specs) for new/modified endpoints
- **Service README** files for architectural changes
- **User guides** for new features
- **Migration guides** for breaking changes

---

## 🔍 Code Review Process

### Before Submitting PR

- [ ] Code follows project coding standards
- [ ] All tests pass (`mvn test`)
- [ ] Code coverage meets minimum requirements
- [ ] No security vulnerabilities (run `mvn dependency-check:check`)
- [ ] Documentation updated for changes
- [ ] Self-review completed

### PR Requirements

#### PR Title and Description
```markdown
feat(user-service): add user profile enhancement

## Summary
This PR adds comprehensive user profile enhancement functionality including:
- Email verification for profile updates
- Profile picture upload with validation
- Enhanced user data validation

## Changes Made
- Added `ProfileUpdateRequest` DTO with validation
- Implemented profile picture upload service
- Added email verification workflow
- Updated user entity with new fields

## Testing
- Added unit tests for new services (95% coverage)
- Added integration tests for profile update flow
- Manual testing completed for file upload functionality

## Breaking Changes
None

## Migration Required
Database migration script included in `src/main/resources/db/migration/`

## Related Issues
Closes #123
Addresses #124
```

#### PR Checklist
- [ ] PR title follows conventional commit format
- [ ] Description clearly explains changes and rationale
- [ ] All CI/CD checks pass
- [ ] Code reviewed by at least one senior developer
- [ ] Documentation updated
- [ ] Breaking changes documented
- [ ] Migration scripts provided if needed

### Review Criteria

Reviewers will check for:

1. **Functionality:** Does the code work as intended?
2. **Architecture:** Does it follow clean architecture principles?
3. **Performance:** Are there any performance implications?
4. **Security:** Are security best practices followed?
5. **Testing:** Is test coverage adequate?
6. **Documentation:** Is code well documented?

---

## 🚨 Issue Reporting

### Bug Reports

Use the bug report template:

```markdown
**Bug Description**
A clear description of the bug

**Steps to Reproduce**
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

**Expected Behavior**
What you expected to happen

**Actual Behavior** 
What actually happened

**Environment**
- OS: [e.g. Windows 10, macOS 11.5]
- Java Version: [e.g. 17]
- Browser (if applicable): [e.g. Chrome 96]
- Version: [e.g. 1.2.3]

**Additional Context**
Any other context about the problem
```

### Feature Requests

Use the feature request template:

```markdown
**Feature Description**
Clear description of the requested feature

**Problem Statement**
What problem does this solve?

**Proposed Solution**
Describe your ideal solution

**Alternative Solutions**
Any alternative approaches considered

**Additional Context**
Any other context or screenshots
```

---

## 🎯 Development Guidelines

### Performance Considerations

- **Database Queries:** Use appropriate indexes and avoid N+1 queries
- **Caching:** Implement caching for frequently accessed data
- **API Design:** Use pagination for list endpoints
- **Resource Management:** Properly close resources and handle exceptions

### Security Guidelines

- **Input Validation:** Validate all user inputs
- **SQL Injection:** Use parameterized queries
- **Authentication:** Implement proper authentication for all endpoints
- **Authorization:** Check permissions for sensitive operations
- **Data Protection:** Encrypt sensitive data at rest and in transit

### Error Handling

```java
// Good error handling
@ExceptionHandler(ValidationException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public ApiResponse<Object> handleValidation(ValidationException e) {
    return ApiResponse.error("Validation failed", e.getErrors());
}

// Service layer error handling
public User createUser(CreateUserRequest request) {
    try {
        // Business logic
        return userRepository.save(user);
    } catch (DataAccessException e) {
        log.error("Database error during user creation", e);
        throw new ServiceException("Failed to create user", e);
    }
}
```

---

## 🏆 Recognition

We appreciate all contributions to the Loyalty System! Contributors who make significant impacts will be:

- **Acknowledged** in release notes
- **Featured** in project documentation
- **Invited** to technical discussions and architecture reviews
- **Considered** for maintainer roles

---

## 📞 Getting Help

Need help with contribution? Reach out through:

- **GitHub Discussions** for general questions
- **GitHub Issues** for bugs and feature requests
- **Slack Channel** #loyalty-system-dev for real-time discussion
- **Email** dev-team@loyalty-system.com for private inquiries

---

## 📋 Contribution Checklist

Before submitting your contribution:

- [ ] Read and understood contribution guidelines
- [ ] Set up local development environment
- [ ] Created feature branch from `develop`
- [ ] Implemented feature following coding standards
- [ ] Added comprehensive tests (unit + integration)
- [ ] Updated documentation as needed
- [ ] Tested locally and all tests pass
- [ ] Created meaningful commit messages
- [ ] Submitted pull request with proper description
- [ ] Addressed all review feedback

---

**Thank you for contributing to the Loyalty System! Your efforts help build a better product for everyone.** 🚀