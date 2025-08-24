# API Design Guidelines

**Author:** Senior Principal Engineer  
**Version:** 1.0  
**Date:** December 2024  
**Purpose:** Comprehensive API design standards for loyalty system  
**Scope:** All REST APIs and service interfaces  

---

## 🎯 API Design Philosophy

### Core Principles
1. **Developer Experience First:** APIs should be intuitive and easy to use
2. **Consistency:** Uniform patterns across all services
3. **RESTful Design:** Follow REST architectural principles
4. **Security by Default:** Built-in authentication and authorization
5. **Versioning Strategy:** Backward compatible evolution
6. **Performance Optimized:** Efficient data transfer and caching

---

## 🏗️ RESTful API Standards

### 1. URL Structure

#### Resource Naming Conventions
```
✅ GOOD Examples:
GET    /api/v1/users                    # Get all users
GET    /api/v1/users/123                # Get specific user
POST   /api/v1/users                    # Create user
PUT    /api/v1/users/123                # Update user (full)
PATCH  /api/v1/users/123                # Update user (partial)
DELETE /api/v1/users/123                # Delete user

GET    /api/v1/users/123/points         # Get user's points
POST   /api/v1/users/123/points/earn    # Earn points
POST   /api/v1/users/123/points/redeem  # Redeem points

GET    /api/v1/rewards                  # Get all rewards
GET    /api/v1/rewards/categories       # Get reward categories
GET    /api/v1/rewards?category=food&tier=gold  # Filtered rewards

❌ BAD Examples:
GET    /api/v1/getUsers                 # Verb in URL
POST   /api/v1/user                     # Singular noun
GET    /api/v1/users-list               # Inconsistent naming
GET    /api/v1/users/123/getUserPoints  # Mixed conventions
```

#### URL Pattern Rules
- Use **nouns** for resources, not verbs
- Use **plural nouns** for collections (`/users`, not `/user`)
- Use **hierarchical relationships** (`/users/123/points`)
- Use **query parameters** for filtering and pagination
- Keep URLs **lowercase** with hyphens for word separation
- Avoid **deep nesting** (max 3 levels: `/resource/id/sub-resource`)

### 2. HTTP Methods

#### Standard HTTP Verbs
```java
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    
    @GetMapping
    public ResponseEntity<PagedResponse<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserStatus status) {
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> users = userService.findUsers(search, status, pageRequest);
        
        return ResponseEntity.ok(PagedResponse.of(users, UserResponse::from));
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String userId) {
        User user = userService.findById(userId);
        return ResponseEntity.ok(UserResponse.from(user));
    }
    
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        
        return ResponseEntity
            .created(URI.create("/api/v1/users/" + user.getId()))
            .body(UserResponse.from(user));
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest request) {
        User user = userService.updateUser(userId, request);
        return ResponseEntity.ok(UserResponse.from(user));
    }
    
    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> patchUser(
            @PathVariable String userId,
            @Valid @RequestBody JsonPatch patch) {
        User user = userService.patchUser(userId, patch);
        return ResponseEntity.ok(UserResponse.from(user));
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
```

### 3. Request/Response Format

#### Standard Request Structure
```java
// Base request classes
public abstract class BaseRequest {
    @Valid
    private RequestMetadata metadata;
    
    // Standard getters/setters
}

public class RequestMetadata {
    private String requestId;
    private Instant timestamp;
    private String clientVersion;
    private Map<String, String> headers;
}

// Specific request examples
public class CreateUserRequest extends BaseRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$")
    private String password;
    
    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;
    
    @NotBlank(message = "Last name is required") 
    @Size(max = 100)
    private String lastName;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    private String phoneNumber;
    
    @Past(message = "Birth date must be in the past")
    private LocalDate dateOfBirth;
    
    private Address address;
    private UserPreferences preferences;
}

public class EarnPointsRequest extends BaseRequest {
    @NotNull(message = "Transaction amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal transactionAmount;
    
    @NotBlank(message = "Transaction ID is required")
    private String transactionId;
    
    @NotBlank(message = "Merchant ID is required")
    private String merchantId;
    
    @NotNull(message = "Transaction date is required")
    private Instant transactionDate;
    
    private String description;
    private Map<String, Object> metadata;
}
```

#### Standard Response Structure
```java
// Base response wrapper
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private List<String> errors;
    private ResponseMetadata metadata;
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .metadata(ResponseMetadata.current())
            .build();
    }
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .message(message)
            .metadata(ResponseMetadata.current())
            .build();
    }
    
    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .errors(errors)
            .metadata(ResponseMetadata.current())
            .build();
    }
}

public class ResponseMetadata {
    private String requestId;
    private Instant timestamp;
    private String version;
    private Long processingTimeMs;
    
    public static ResponseMetadata current() {
        return ResponseMetadata.builder()
            .requestId(RequestContextHolder.getCurrentRequestId())
            .timestamp(Instant.now())
            .version("1.0")
            .processingTimeMs(RequestContextHolder.getProcessingTime())
            .build();
    }
}

// Paged response
public class PagedResponse<T> {
    private List<T> data;
    private PageInfo pagination;
    
    public static <T> PagedResponse<T> of(Page<T> page) {
        return PagedResponse.<T>builder()
            .data(page.getContent())
            .pagination(PageInfo.from(page))
            .build();
    }
    
    public static <T, R> PagedResponse<R> of(Page<T> page, Function<T, R> mapper) {
        return PagedResponse.<R>builder()
            .data(page.getContent().stream().map(mapper).collect(Collectors.toList()))
            .pagination(PageInfo.from(page))
            .build();
    }
}

public class PageInfo {
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private boolean hasNext;
    private boolean hasPrevious;
    
    public static PageInfo from(Page<?> page) {
        return PageInfo.builder()
            .currentPage(page.getNumber())
            .pageSize(page.getSize())
            .totalPages(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .hasNext(page.hasNext())
            .hasPrevious(page.hasPrevious())
            .build();
    }
}
```

---

## 📊 HTTP Status Codes

### Standard Status Code Usage
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // 2xx Success
    @ResponseStatus(HttpStatus.OK) // 200
    public ApiResponse<Object> handleSuccess() {
        return ApiResponse.success("Operation completed successfully");
    }
    
    @ResponseStatus(HttpStatus.CREATED) // 201
    public ApiResponse<Object> handleCreated() {
        return ApiResponse.success("Resource created successfully");
    }
    
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void handleDeleted() {
        // No response body for successful deletion
    }
    
    // 4xx Client Errors
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ApiResponse<Object> handleValidation(ValidationException e) {
        return ApiResponse.error("Validation failed", e.getErrors());
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
    public ApiResponse<Object> handleUnauthorized(UnauthorizedException e) {
        return ApiResponse.error("Authentication required", List.of(e.getMessage()));
    }
    
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) // 403
    public ApiResponse<Object> handleForbidden(ForbiddenException e) {
        return ApiResponse.error("Access denied", List.of(e.getMessage()));
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ApiResponse<Object> handleNotFound(ResourceNotFoundException e) {
        return ApiResponse.error("Resource not found", List.of(e.getMessage()));
    }
    
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public ApiResponse<Object> handleConflict(ConflictException e) {
        return ApiResponse.error("Resource conflict", List.of(e.getMessage()));
    }
    
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // 422
    public ApiResponse<Object> handleBusinessValidation(ValidationException e) {
        return ApiResponse.error("Business validation failed", e.getErrors());
    }
    
    @ExceptionHandler(TooManyRequestsException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS) // 429
    public ApiResponse<Object> handleRateLimit(TooManyRequestsException e) {
        return ApiResponse.error("Rate limit exceeded", List.of(e.getMessage()));
    }
    
    // 5xx Server Errors
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    public ApiResponse<Object> handleGeneral(Exception e) {
        log.error("Unexpected error occurred", e);
        return ApiResponse.error("Internal server error", 
            List.of("An unexpected error occurred. Please try again later."));
    }
    
    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE) // 503
    public ApiResponse<Object> handleServiceUnavailable(ServiceUnavailableException e) {
        return ApiResponse.error("Service temporarily unavailable", List.of(e.getMessage()));
    }
}
```

### Status Code Reference
| Status Code | Use Case | Example |
|-------------|----------|---------|
| **200 OK** | Successful GET, PUT, PATCH | User profile retrieved |
| **201 Created** | Successful POST | User account created |
| **204 No Content** | Successful DELETE | User deleted successfully |
| **400 Bad Request** | Invalid request format | Missing required field |
| **401 Unauthorized** | Authentication required | Invalid or missing token |
| **403 Forbidden** | Access denied | Insufficient permissions |
| **404 Not Found** | Resource doesn't exist | User ID not found |
| **409 Conflict** | Resource already exists | Email already registered |
| **422 Unprocessable Entity** | Business logic validation | Insufficient points balance |
| **429 Too Many Requests** | Rate limit exceeded | API quota exceeded |
| **500 Internal Server Error** | Unexpected server error | Database connection failed |
| **503 Service Unavailable** | Service temporarily down | Maintenance mode active |

---

## 🔍 Query Parameters & Filtering

### 1. Pagination Standards
```java
@GetMapping("/users")
public ResponseEntity<PagedResponse<UserResponse>> getUsers(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
        @RequestParam(defaultValue = "createdDate") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDirection) {
    
    Sort.Direction direction = Sort.Direction.fromString(sortDirection);
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
    
    Page<User> users = userService.findAll(pageable);
    return ResponseEntity.ok(PagedResponse.of(users, UserResponse::from));
}

// Example request:
// GET /api/v1/users?page=0&size=20&sortBy=createdDate&sortDirection=desc
```

### 2. Filtering Standards
```java
@GetMapping("/users")
public ResponseEntity<PagedResponse<UserResponse>> getUsers(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) UserStatus status,
        @RequestParam(required = false) UserTier tier,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdAfter,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdBefore,
        Pageable pageable) {
    
    UserFilter filter = UserFilter.builder()
        .search(search)
        .status(status)
        .tier(tier)
        .createdAfter(createdAfter)
        .createdBefore(createdBefore)
        .build();
    
    Page<User> users = userService.findUsers(filter, pageable);
    return ResponseEntity.ok(PagedResponse.of(users, UserResponse::from));
}

// Example request:
// GET /api/v1/users?search=john&status=ACTIVE&tier=GOLD&createdAfter=2024-01-01&page=0&size=20
```

### 3. Advanced Filtering with Specifications
```java
public class UserSpecifications {
    
    public static Specification<User> hasEmailContaining(String email) {
        return (root, query, criteriaBuilder) -> 
            email == null ? null : criteriaBuilder.like(
                criteriaBuilder.lower(root.get("email")), 
                "%" + email.toLowerCase() + "%"
            );
    }
    
    public static Specification<User> hasStatus(UserStatus status) {
        return (root, query, criteriaBuilder) ->
            status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }
    
    public static Specification<User> createdBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) return null;
            
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("createdDate"), 
                    startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
            } else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), 
                    startDate.atStartOfDay());
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), 
                    endDate.atTime(23, 59, 59));
            }
        };
    }
}
```

---

## 🔐 Authentication & Authorization

### 1. JWT Authentication
```java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        
        // Authenticate user
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        // Generate tokens
        User user = (User) auth.getPrincipal();
        TokenPair tokens = jwtService.generateTokens(user);
        
        // Update last login
        userService.updateLastLogin(user.getId());
        
        return ResponseEntity.ok(LoginResponse.builder()
            .accessToken(tokens.getAccessToken())
            .refreshToken(tokens.getRefreshToken())
            .expiresIn(Duration.ofMinutes(15).toSeconds())
            .user(UserResponse.from(user))
            .build());
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        
        // Validate refresh token
        if (!jwtService.validateRefreshToken(request.getRefreshToken())) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        
        // Generate new access token
        String userId = jwtService.getUserIdFromRefreshToken(request.getRefreshToken());
        User user = userService.findById(userId);
        String accessToken = jwtService.generateAccessToken(user);
        
        return ResponseEntity.ok(RefreshResponse.builder()
            .accessToken(accessToken)
            .expiresIn(Duration.ofMinutes(15).toSeconds())
            .build());
    }
    
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        
        // Blacklist current token
        String token = jwtService.extractTokenFromRequest(request);
        jwtService.blacklistToken(token);
        
        // Clear refresh token
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        jwtService.revokeRefreshTokens(userId);
        
        return ResponseEntity.noContent().build();
    }
}
```

### 2. Role-Based Access Control
```java
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @GetMapping("/users")
    @PreAuthorize("hasPermission('USER', 'READ')")
    public ResponseEntity<PagedResponse<UserResponse>> getAllUsers(Pageable pageable) {
        // Admin can see all users
    }
    
    @PostMapping("/users/{userId}/points/adjust")
    @PreAuthorize("hasPermission('POINTS', 'ADJUST')")
    public ResponseEntity<PointsResponse> adjustPoints(
            @PathVariable String userId,
            @Valid @RequestBody AdjustPointsRequest request) {
        
        // Log admin action for audit
        auditService.logAdminAction(
            getCurrentUserId(),
            "POINTS_ADJUSTMENT", 
            userId,
            Map.of("amount", request.getAmount(), "reason", request.getReason())
        );
        
        Points points = pointService.adjustPoints(userId, request.getAmount(), request.getReason());
        return ResponseEntity.ok(PointsResponse.from(points));
    }
}

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @GetMapping("/{userId}")
    @PreAuthorize("hasPermission(#userId, 'USER', 'READ')")
    public ResponseEntity<UserResponse> getUser(@PathVariable String userId) {
        // User can only access their own data or admin can access any
    }
    
    @PutMapping("/{userId}/profile")
    @PreAuthorize("hasPermission(#userId, 'USER', 'WRITE')")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable String userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        // User can only update their own profile
    }
}
```

---

## 📚 API Documentation

### 1. OpenAPI Specification
```yaml
openapi: 3.0.0
info:
  title: Loyalty System API
  description: Comprehensive loyalty program management system
  version: 1.0.0
  contact:
    name: Development Team
    email: dev-team@loyalty-system.com
  license:
    name: MIT
    url: https://opensource.org/licenses/MIT

servers:
  - url: https://api.loyalty-system.com/v1
    description: Production server
  - url: https://staging-api.loyalty-system.com/v1
    description: Staging server
  - url: http://localhost:8080/api/v1
    description: Local development server

security:
  - BearerAuth: []

paths:
  /users:
    get:
      summary: Get users
      description: Retrieve a paginated list of users with optional filtering
      tags: [Users]
      parameters:
        - name: page
          in: query
          description: Page number (0-based)
          schema:
            type: integer
            minimum: 0
            default: 0
        - name: size
          in: query
          description: Number of items per page
          schema:
            type: integer
            minimum: 1
            maximum: 100
            default: 20
        - name: search
          in: query
          description: Search term for email or name
          schema:
            type: string
        - name: status
          in: query
          description: Filter by user status
          schema:
            $ref: '#/components/schemas/UserStatus'
      responses:
        '200':
          description: Successfully retrieved users
          content:
            application/json:
              schema:
                allOf:
                  - $ref: '#/components/schemas/ApiResponse'
                  - type: object
                    properties:
                      data:
                        $ref: '#/components/schemas/PagedUserResponse'
        '401':
          $ref: '#/components/responses/UnauthorizedError'
        '403':
          $ref: '#/components/responses/ForbiddenError'

    post:
      summary: Create user
      description: Register a new user account
      tags: [Users]
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateUserRequest'
            example:
              email: john.doe@example.com
              password: SecurePass123!
              firstName: John
              lastName: Doe
              phoneNumber: +1234567890
              dateOfBirth: "1990-01-01"
      responses:
        '201':
          description: User created successfully
          content:
            application/json:
              schema:
                allOf:
                  - $ref: '#/components/schemas/ApiResponse'
                  - type: object
                    properties:
                      data:
                        $ref: '#/components/schemas/UserResponse'
        '400':
          $ref: '#/components/responses/ValidationError'
        '409':
          $ref: '#/components/responses/ConflictError'

components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

  schemas:
    ApiResponse:
      type: object
      properties:
        success:
          type: boolean
          description: Indicates if the request was successful
        message:
          type: string
          description: Human-readable message
        errors:
          type: array
          items:
            type: string
          description: List of error messages
        metadata:
          $ref: '#/components/schemas/ResponseMetadata'

    ResponseMetadata:
      type: object
      properties:
        requestId:
          type: string
          description: Unique request identifier
        timestamp:
          type: string
          format: date-time
          description: Response timestamp
        version:
          type: string
          description: API version
        processingTimeMs:
          type: integer
          description: Request processing time in milliseconds

    UserResponse:
      type: object
      properties:
        id:
          type: string
          description: Unique user identifier
        email:
          type: string
          format: email
          description: User email address
        firstName:
          type: string
          description: User first name
        lastName:
          type: string
          description: User last name
        status:
          $ref: '#/components/schemas/UserStatus'
        tier:
          $ref: '#/components/schemas/UserTier'
        pointsBalance:
          type: integer
          description: Current points balance
        createdDate:
          type: string
          format: date-time
          description: Account creation timestamp
        lastLoginDate:
          type: string
          format: date-time
          description: Last login timestamp

    UserStatus:
      type: string
      enum: [ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION]
      description: User account status

    UserTier:
      type: string
      enum: [BRONZE, SILVER, GOLD, PLATINUM]
      description: User loyalty tier

  responses:
    UnauthorizedError:
      description: Authentication required
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ApiResponse'
          example:
            success: false
            message: "Authentication required"
            errors: ["Invalid or missing authentication token"]

    ForbiddenError:
      description: Access denied
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ApiResponse'
          example:
            success: false
            message: "Access denied"
            errors: ["Insufficient permissions to access this resource"]

    ValidationError:
      description: Request validation failed
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ApiResponse'
          example:
            success: false
            message: "Validation failed"
            errors: ["Email is required", "Password must be at least 8 characters"]
```

### 2. Java Documentation Standards
```java
/**
 * User management controller providing endpoints for user operations.
 * 
 * This controller handles user registration, profile management, and user queries.
 * All endpoints require appropriate authentication and authorization.
 * 
 * @author Development Team
 * @version 1.0
 * @since 2024-12-01
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management operations")
public class UserController {
    
    /**
     * Retrieves a paginated list of users with optional filtering.
     * 
     * @param page Zero-based page number (default: 0)
     * @param size Number of items per page (default: 20, max: 100)
     * @param search Optional search term for email or name filtering
     * @param status Optional status filter
     * @param sortBy Field to sort by (default: createdDate)
     * @param sortDirection Sort direction: 'asc' or 'desc' (default: desc)
     * @return Paginated response containing users matching the criteria
     * @throws UnauthorizedException if user is not authenticated
     * @throws ForbiddenException if user lacks required permissions
     * 
     * @apiNote This endpoint supports advanced filtering and sorting options.
     *          Use query parameters to customize the response.
     * 
     * @example
     * GET /api/v1/users?page=0&size=20&search=john&status=ACTIVE&sortBy=createdDate&sortDirection=desc
     */
    @GetMapping
    @Operation(
        summary = "Get users",
        description = "Retrieve a paginated list of users with optional filtering and sorting",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    public ResponseEntity<PagedResponse<UserResponse>> getUsers(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Number of items per page", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            
            @Parameter(description = "Search term for email or name", example = "john")
            @RequestParam(required = false) String search,
            
            @Parameter(description = "Filter by user status")
            @RequestParam(required = false) UserStatus status,
            
            @Parameter(description = "Field to sort by", example = "createdDate")
            @RequestParam(defaultValue = "createdDate") String sortBy,
            
            @Parameter(description = "Sort direction", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        // Implementation
    }
}
```

---

## ⚡ Performance Optimization

### 1. Caching Strategy
```java
@RestController
@RequestMapping("/api/v1/rewards")
public class RewardController {
    
    @GetMapping
    @Cacheable(value = "rewards", key = "#category + '_' + #tier + '_' + #page + '_' + #size")
    @CacheEvict(value = "rewards", allEntries = true, condition = "#result.data.size() == 0")
    public ResponseEntity<PagedResponse<RewardResponse>> getRewards(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) UserTier tier,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Cache for 5 minutes
        // Evict when inventory changes
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)))
            .body(rewardService.getRewards(category, tier, PageRequest.of(page, size)));
    }
    
    @PostMapping
    @CacheEvict(value = {"rewards", "reward-categories"}, allEntries = true)
    public ResponseEntity<RewardResponse> createReward(@Valid @RequestBody CreateRewardRequest request) {
        // Clear cache when new reward is created
        Reward reward = rewardService.createReward(request);
        return ResponseEntity.created(URI.create("/api/v1/rewards/" + reward.getId()))
            .body(RewardResponse.from(reward));
    }
}
```

### 2. Rate Limiting
```java
@Component
public class RateLimitingInterceptor implements HandlerInterceptor {
    
    private final RateLimitingService rateLimitingService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        
        String clientId = extractClientId(request);
        String endpoint = request.getRequestURI();
        
        // Different limits for different endpoints
        RateLimit rateLimit = getRateLimit(endpoint);
        
        if (!rateLimitingService.isAllowed(clientId, rateLimit)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", String.valueOf(rateLimit.getWindowSeconds()));
            response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.getMaxRequests()));
            response.setHeader("X-RateLimit-Remaining", "0");
            return false;
        }
        
        // Add rate limit headers
        RateLimitStatus status = rateLimitingService.getStatus(clientId, rateLimit);
        response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.getMaxRequests()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(status.getRemaining()));
        response.setHeader("X-RateLimit-Reset", String.valueOf(status.getResetTime()));
        
        return true;
    }
    
    private RateLimit getRateLimit(String endpoint) {
        if (endpoint.contains("/auth/")) {
            return RateLimit.of(5, Duration.ofMinutes(15)); // 5 requests per 15 minutes
        } else if (endpoint.contains("/points/")) {
            return RateLimit.of(100, Duration.ofHours(1)); // 100 requests per hour
        } else {
            return RateLimit.of(1000, Duration.ofHours(1)); // 1000 requests per hour
        }
    }
}
```

---

## 📋 API Development Checklist

### Pre-Development
- [ ] API design reviewed and approved by architecture team
- [ ] OpenAPI specification created and validated
- [ ] Security requirements identified and documented
- [ ] Performance requirements defined
- [ ] Error handling strategy planned

### During Development
- [ ] RESTful principles followed consistently
- [ ] Input validation implemented for all endpoints
- [ ] Authentication and authorization properly configured
- [ ] Error responses follow standard format
- [ ] Logging and monitoring instrumentation added
- [ ] Unit and integration tests written
- [ ] API documentation updated

### Pre-Release
- [ ] Load testing performed and benchmarks met
- [ ] Security testing completed (OWASP top 10)
- [ ] API documentation reviewed and published
- [ ] Rate limiting configured and tested
- [ ] Caching strategy implemented where appropriate
- [ ] Monitoring and alerting configured
- [ ] Backward compatibility verified

### Post-Release
- [ ] API metrics monitored and analyzed
- [ ] User feedback collected and incorporated
- [ ] Performance optimizations implemented as needed
- [ ] Documentation kept up-to-date with changes
- [ ] Deprecation notices provided for breaking changes

---

This comprehensive API design guide ensures consistent, secure, and performant APIs across the entire loyalty system ecosystem.