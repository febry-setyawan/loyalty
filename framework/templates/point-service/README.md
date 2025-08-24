# Point Service Template - Java Spring Boot

**Technology Stack:** Java 17, Spring Boot, PostgreSQL, Redis, Apache Kafka  
**Architecture Pattern:** Clean Architecture (Hexagonal Architecture)  
**Purpose:** Point earning, redemption, balance management, transaction processing  

---

## 📁 Project Structure

```
point-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/loyalty/points/
│   │   │       ├── domain/              # Business Logic Layer
│   │   │       │   ├── entities/        # Domain entities
│   │   │       │   │   ├── PointTransaction.java
│   │   │       │   │   ├── PointBalance.java
│   │   │       │   │   ├── EarningRule.java
│   │   │       │   │   └── RedemptionRule.java
│   │   │       │   ├── repositories/    # Repository interfaces
│   │   │       │   │   ├── PointTransactionRepository.java
│   │   │       │   │   ├── PointBalanceRepository.java
│   │   │       │   │   └── EarningRuleRepository.java
│   │   │       │   ├── services/       # Domain services
│   │   │       │   │   ├── PointCalculationService.java
│   │   │       │   │   ├── FraudDetectionService.java
│   │   │       │   │   └── BalanceService.java
│   │   │       │   └── valueobjects/   # Value objects
│   │   │       │       ├── Points.java
│   │   │       │       ├── Money.java
│   │   │       │       └── TransactionType.java
│   │   │       │
│   │   │       ├── application/         # Application Logic Layer
│   │   │       │   ├── usecases/       # Use case implementations
│   │   │       │   │   ├── EarnPointsUseCase.java
│   │   │       │   │   ├── RedeemPointsUseCase.java
│   │   │       │   │   ├── GetBalanceUseCase.java
│   │   │       │   │   └── GetTransactionHistoryUseCase.java
│   │   │       │   ├── dto/            # Data Transfer Objects
│   │   │       │   │   ├── EarnPointsDTO.java
│   │   │       │   │   ├── RedeemPointsDTO.java
│   │   │       │   │   └── BalanceDTO.java
│   │   │       │   └── validators/     # Input validation
│   │   │       │       ├── PointsValidator.java
│   │   │       │       └── TransactionValidator.java
│   │   │       │
│   │   │       ├── infrastructure/      # Infrastructure Layer
│   │   │       │   ├── persistence/    # Database implementations
│   │   │       │   │   ├── repositories/
│   │   │       │   │   │   ├── JpaPointTransactionRepository.java
│   │   │       │   │   │   ├── RedisPointBalanceRepository.java
│   │   │       │   │   │   └── JpaEarningRuleRepository.java
│   │   │       │   │   ├── entities/   # JPA entities
│   │   │       │   │   │   ├── PointTransactionEntity.java
│   │   │       │   │   │   └── EarningRuleEntity.java
│   │   │       │   │   └── config/     # Database configuration
│   │   │       │   │       ├── DatabaseConfig.java
│   │   │       │   │       └── RedisConfig.java
│   │   │       │   ├── messaging/      # Message handling
│   │   │       │   │   ├── publishers/
│   │   │       │   │   │   └── PointEventPublisher.java
│   │   │       │   │   ├── consumers/
│   │   │       │   │   │   └── TransactionEventConsumer.java
│   │   │       │   │   └── config/
│   │   │       │   │       └── KafkaConfig.java
│   │   │       │   └── external/       # External integrations
│   │   │       │       ├── NotificationClient.java
│   │   │       │       └── UserServiceClient.java
│   │   │       │
│   │   │       ├── interfaces/          # Interface Layer
│   │   │       │   ├── web/            # REST controllers
│   │   │       │   │   ├── controllers/
│   │   │       │   │   │   ├── PointsController.java
│   │   │       │   │   │   ├── BalanceController.java
│   │   │       │   │   │   └── TransactionController.java
│   │   │       │   │   ├── dto/        # Web DTOs
│   │   │       │   │   │   ├── EarnPointsRequest.java
│   │   │       │   │   │   ├── RedeemPointsRequest.java
│   │   │       │   │   │   └── ApiResponse.java
│   │   │       │   │   └── config/     # Web configuration
│   │   │       │   │       ├── WebConfig.java
│   │   │       │   │       └── SecurityConfig.java
│   │   │       │   └── messaging/      # Message handlers
│   │   │       │       └── EventMessageHandler.java
│   │   │       │
│   │   │       ├── shared/              # Shared utilities
│   │   │       │   ├── exceptions/     # Custom exceptions
│   │   │       │   ├── utils/          # Utility classes
│   │   │       │   └── constants/      # Application constants
│   │   │       │
│   │   │       └── PointServiceApplication.java  # Main application
│   │   │
│   │   └── resources/
│   │       ├── application.yml         # Application configuration
│   │       ├── application-dev.yml     # Development configuration
│   │       ├── application-prod.yml    # Production configuration
│   │       └── db/
│   │           └── migration/          # Database migrations
│   │               ├── V1__Create_point_transaction_table.sql
│   │               └── V2__Create_earning_rule_table.sql
│   │
│   └── test/
│       └── java/
│           └── com/loyalty/points/
│               ├── domain/              # Domain tests
│               ├── application/         # Use case tests
│               ├── infrastructure/      # Infrastructure tests
│               └── interfaces/         # Controller tests
│
├── docker/                            # Docker configurations
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── .dockerignore
│
├── docs/                              # Service documentation
│   ├── api/                           # API documentation
│   ├── deployment/                    # Deployment guides
│   └── development/                   # Development guides
│
├── scripts/                           # Utility scripts
│   ├── setup.sh
│   └── run-tests.sh
│
├── pom.xml                           # Maven dependencies
├── .gitignore
└── README.md                         # Service documentation
```

## 🔧 Core Components

### 1. Domain Layer

#### Point Transaction Entity
```java
// src/main/java/com/example/loyalty/points/domain/entities/PointTransaction.java
package com.example.loyalty.points.domain.entities;

import com.example.loyalty.points.domain.valueobjects.Points;
import com.example.loyalty.points.domain.valueobjects.Money;
import com.example.loyalty.points.domain.valueobjects.TransactionType;
import java.time.LocalDateTime;
import java.util.UUID;

public class PointTransaction {
    private UUID id;
    private String userId;
    private Points points;
    private Money transactionAmount;
    private TransactionType type;
    private String description;
    private String referenceId;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private boolean isProcessed;

    public PointTransaction(String userId, Points points, Money transactionAmount, 
                           TransactionType type, String description, String referenceId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.points = points;
        this.transactionAmount = transactionAmount;
        this.type = type;
        this.description = description;
        this.referenceId = referenceId;
        this.createdAt = LocalDateTime.now();
        this.isProcessed = false;
    }

    public void process() {
        if (isProcessed) {
            throw new IllegalStateException("Transaction already processed");
        }
        this.isProcessed = true;
        this.processedAt = LocalDateTime.now();
    }

    public boolean canBeProcessed() {
        return !isProcessed && points.isPositive();
    }

    // Getters and setters
    public UUID getId() { return id; }
    public String getUserId() { return userId; }
    public Points getPoints() { return points; }
    public TransactionType getType() { return type; }
    public boolean isProcessed() { return isProcessed; }
    // ... other getters
}
```

#### Points Value Object
```java
// src/main/java/com/loyalty/points/domain/valueobjects/Points.java
package com.example.loyalty.points.domain.valueobjects;

import java.math.BigDecimal;
import java.util.Objects;

public class Points {
    private final BigDecimal value;

    public Points(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("Points value cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Points value cannot be negative");
        }
        this.value = value;
    }

    public Points add(Points other) {
        return new Points(this.value.add(other.value));
    }

    public Points subtract(Points other) {
        if (this.value.compareTo(other.value) < 0) {
            throw new IllegalArgumentException("Insufficient points");
        }
        return new Points(this.value.subtract(other.value));
    }

    public boolean isGreaterThan(Points other) {
        return this.value.compareTo(other.value) > 0;
    }

    public boolean isPositive() {
        return this.value.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Points points = (Points) o;
        return Objects.equals(value, points.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Points{" + value + "}";
    }
}
```

#### Repository Interface
```java
// src/main/java/com/loyalty/points/domain/repositories/PointTransactionRepository.java
package com.example.loyalty.points.domain.repositories;

import com.example.loyalty.points.domain.entities.PointTransaction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PointTransactionRepository {
    PointTransaction save(PointTransaction transaction);
    Optional<PointTransaction> findById(UUID id);
    List<PointTransaction> findByUserId(String userId);
    List<PointTransaction> findByUserIdAndDateRange(String userId, 
                                                   java.time.LocalDateTime from, 
                                                   java.time.LocalDateTime to);
    List<PointTransaction> findUnprocessedTransactions();
}
```

### 2. Application Layer

#### Earn Points Use Case
```java
// src/main/java/com/loyalty/points/application/usecases/EarnPointsUseCase.java
package com.example.loyalty.points.application.usecases;

import com.example.loyalty.points.domain.entities.PointTransaction;
import com.example.loyalty.points.domain.repositories.PointTransactionRepository;
import com.example.loyalty.points.domain.repositories.PointBalanceRepository;
import com.example.loyalty.points.domain.services.PointCalculationService;
import com.example.loyalty.points.domain.valueobjects.Points;
import com.example.loyalty.points.domain.valueobjects.Money;
import com.example.loyalty.points.domain.valueobjects.TransactionType;
import com.example.loyalty.points.application.dto.EarnPointsDTO;
import com.example.loyalty.points.infrastructure.messaging.publishers.PointEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EarnPointsUseCase {
    private final PointTransactionRepository transactionRepository;
    private final PointBalanceRepository balanceRepository;
    private final PointCalculationService calculationService;
    private final PointEventPublisher eventPublisher;

    public EarnPointsUseCase(PointTransactionRepository transactionRepository,
                            PointBalanceRepository balanceRepository,
                            PointCalculationService calculationService,
                            PointEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.balanceRepository = balanceRepository;
        this.calculationService = calculationService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public EarnPointsDTO.Response execute(EarnPointsDTO.Request request) {
        // Validate request
        validateRequest(request);

        // Calculate points based on transaction amount and earning rules
        Money transactionAmount = new Money(request.getTransactionAmount());
        Points earnedPoints = calculationService.calculateEarnedPoints(
            transactionAmount, 
            request.getUserId(),
            request.getEarningType()
        );

        // Create point transaction
        PointTransaction transaction = new PointTransaction(
            request.getUserId(),
            earnedPoints,
            transactionAmount,
            TransactionType.EARNING,
            request.getDescription(),
            request.getReferenceId()
        );

        // Save transaction
        PointTransaction savedTransaction = transactionRepository.save(transaction);

        // Update balance
        balanceRepository.addPoints(request.getUserId(), earnedPoints);

        // Process transaction
        savedTransaction.process();
        transactionRepository.save(savedTransaction);

        // Publish event
        eventPublisher.publishPointsEarned(savedTransaction);

        return new EarnPointsDTO.Response(
            savedTransaction.getId().toString(),
            earnedPoints.getValue(),
            "Points earned successfully"
        );
    }

    private void validateRequest(EarnPointsDTO.Request request) {
        if (request.getUserId() == null || request.getUserId().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (request.getTransactionAmount() == null || 
            request.getTransactionAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
    }
}
```

### 3. Infrastructure Layer

#### JPA Point Transaction Repository
```java
// src/main/java/com/loyalty/points/infrastructure/persistence/repositories/JpaPointTransactionRepository.java
package com.example.loyalty.points.infrastructure.persistence.repositories;

import com.example.loyalty.points.domain.entities.PointTransaction;
import com.example.loyalty.points.domain.repositories.PointTransactionRepository;
import com.example.loyalty.points.infrastructure.persistence.entities.PointTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class JpaPointTransactionRepository implements PointTransactionRepository {
    private final SpringDataPointTransactionRepository springRepository;

    public JpaPointTransactionRepository(SpringDataPointTransactionRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public PointTransaction save(PointTransaction transaction) {
        PointTransactionEntity entity = mapToEntity(transaction);
        PointTransactionEntity saved = springRepository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<PointTransaction> findById(UUID id) {
        return springRepository.findById(id)
                .map(this::mapToDomain);
    }

    @Override
    public List<PointTransaction> findByUserId(String userId) {
        return springRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PointTransaction> findByUserIdAndDateRange(String userId, 
                                                          LocalDateTime from, 
                                                          LocalDateTime to) {
        return springRepository.findByUserIdAndCreatedAtBetween(userId, from, to)
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PointTransaction> findUnprocessedTransactions() {
        return springRepository.findByIsProcessedFalse()
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    private PointTransactionEntity mapToEntity(PointTransaction transaction) {
        PointTransactionEntity entity = new PointTransactionEntity();
        entity.setId(transaction.getId());
        entity.setUserId(transaction.getUserId());
        entity.setPoints(transaction.getPoints().getValue());
        entity.setTransactionAmount(transaction.getTransactionAmount().getValue());
        entity.setType(transaction.getType().toString());
        entity.setDescription(transaction.getDescription());
        entity.setReferenceId(transaction.getReferenceId());
        entity.setCreatedAt(transaction.getCreatedAt());
        entity.setProcessedAt(transaction.getProcessedAt());
        entity.setProcessed(transaction.isProcessed());
        return entity;
    }

    private PointTransaction mapToDomain(PointTransactionEntity entity) {
        // Mapping logic from entity to domain object
        // Implementation details...
        return new PointTransaction(/* parameters */);
    }
}

interface SpringDataPointTransactionRepository extends JpaRepository<PointTransactionEntity, UUID> {
    List<PointTransactionEntity> findByUserIdOrderByCreatedAtDesc(String userId);
    List<PointTransactionEntity> findByUserIdAndCreatedAtBetween(String userId, 
                                                                LocalDateTime from, 
                                                                LocalDateTime to);
    List<PointTransactionEntity> findByIsProcessedFalse();
}
```

#### Redis Balance Repository
```java
// src/main/java/com/loyalty/points/infrastructure/persistence/repositories/RedisPointBalanceRepository.java
package com.example.loyalty.points.infrastructure.persistence.repositories;

import com.example.loyalty.points.domain.repositories.PointBalanceRepository;
import com.example.loyalty.points.domain.valueobjects.Points;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisPointBalanceRepository implements PointBalanceRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String BALANCE_KEY_PREFIX = "point_balance:";
    private static final long CACHE_TTL_HOURS = 24;

    public RedisPointBalanceRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Points getBalance(String userId) {
        String key = BALANCE_KEY_PREFIX + userId;
        String balance = redisTemplate.opsForValue().get(key);
        
        if (balance == null) {
            // Calculate balance from database and cache it
            Points calculatedBalance = calculateBalanceFromDatabase(userId);
            redisTemplate.opsForValue().set(key, calculatedBalance.getValue().toString(), 
                                          CACHE_TTL_HOURS, TimeUnit.HOURS);
            return calculatedBalance;
        }
        
        return new Points(new BigDecimal(balance));
    }

    @Override
    public void addPoints(String userId, Points points) {
        String key = BALANCE_KEY_PREFIX + userId;
        Points currentBalance = getBalance(userId);
        Points newBalance = currentBalance.add(points);
        
        redisTemplate.opsForValue().set(key, newBalance.getValue().toString(), 
                                      CACHE_TTL_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void subtractPoints(String userId, Points points) {
        String key = BALANCE_KEY_PREFIX + userId;
        Points currentBalance = getBalance(userId);
        
        if (!currentBalance.isGreaterThan(points) && !currentBalance.equals(points)) {
            throw new IllegalArgumentException("Insufficient points");
        }
        
        Points newBalance = currentBalance.subtract(points);
        redisTemplate.opsForValue().set(key, newBalance.getValue().toString(), 
                                      CACHE_TTL_HOURS, TimeUnit.HOURS);
    }

    private Points calculateBalanceFromDatabase(String userId) {
        // Logic to calculate balance from database
        // This would typically involve summing all transactions
        return new Points(BigDecimal.ZERO); // Placeholder
    }
}
```

### 4. Interface Layer

#### Points Controller
```java
// src/main/java/com/loyalty/points/interfaces/web/controllers/PointsController.java
package com.example.loyalty.points.interfaces.web.controllers;

import com.example.loyalty.points.application.usecases.EarnPointsUseCase;
import com.example.loyalty.points.application.usecases.RedeemPointsUseCase;
import com.example.loyalty.points.application.dto.EarnPointsDTO;
import com.example.loyalty.points.application.dto.RedeemPointsDTO;
import com.example.loyalty.points.interfaces.web.dto.EarnPointsRequest;
import com.example.loyalty.points.interfaces.web.dto.RedeemPointsRequest;
import com.example.loyalty.points.interfaces.web.dto.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/points")
@CrossOrigin(origins = "*")
public class PointsController {
    private final EarnPointsUseCase earnPointsUseCase;
    private final RedeemPointsUseCase redeemPointsUseCase;

    public PointsController(EarnPointsUseCase earnPointsUseCase,
                           RedeemPointsUseCase redeemPointsUseCase) {
        this.earnPointsUseCase = earnPointsUseCase;
        this.redeemPointsUseCase = redeemPointsUseCase;
    }

    @PostMapping("/earn")
    public ResponseEntity<ApiResponse<EarnPointsDTO.Response>> earnPoints(
            @Valid @RequestBody EarnPointsRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        EarnPointsDTO.Request useCaseRequest = new EarnPointsDTO.Request(
            userDetails.getUsername(),
            request.getTransactionAmount(),
            request.getEarningType(),
            request.getDescription(),
            request.getReferenceId()
        );

        EarnPointsDTO.Response response = earnPointsUseCase.execute(useCaseRequest);

        return ResponseEntity.ok(new ApiResponse<>(
            true,
            response,
            "Points earned successfully"
        ));
    }

    @PostMapping("/redeem")
    public ResponseEntity<ApiResponse<RedeemPointsDTO.Response>> redeemPoints(
            @Valid @RequestBody RedeemPointsRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        RedeemPointsDTO.Request useCaseRequest = new RedeemPointsDTO.Request(
            userDetails.getUsername(),
            request.getPoints(),
            request.getRewardId(),
            request.getDescription()
        );

        RedeemPointsDTO.Response response = redeemPointsUseCase.execute(useCaseRequest);

        return ResponseEntity.ok(new ApiResponse<>(
            true,
            response,
            "Points redeemed successfully"
        ));
    }
}
```

## 📦 Maven Configuration (pom.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <groupId>com.example.loyalty</groupId>
    <artifactId>point-service</artifactId>
    <version>1.0.0</version>
    <name>Point Service</name>
    <description>Point management service for loyalty system</description>

    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
        <testcontainers.version>1.19.3</testcontainers.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>

        <!-- Messaging -->
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>

        <!-- Documentation -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.2.0</version>
        </dependency>

        <!-- Test Dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.10</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

## 🐳 Docker Configuration

```dockerfile
# docker/Dockerfile
FROM openjdk:17-jdk-alpine

VOLUME /tmp

# Install curl for health checks
RUN apk --no-cache add curl

# Create app user
RUN addgroup -g 1001 -S loyalty && \
    adduser -S loyalty -u 1001

WORKDIR /app

# Copy the jar file
COPY target/point-service-1.0.0.jar app.jar

# Change ownership
RUN chown -R loyalty:loyalty /app

USER loyalty

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
```

## 📊 Application Configuration

```yaml
# src/main/resources/application.yml
server:
  port: 8081
  servlet:
    context-path: /

spring:
  application:
    name: point-service
  
  datasource:
    url: jdbc:postgresql://localhost:5432/loyalty_points
    username: ${DB_USERNAME:loyalty_user}
    password: ${DB_PASSWORD:loyalty_pass}
    driver-class-name: org.postgresql.Driver
    
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    timeout: 2000ms
    
  kafka:
    bootstrap-servers: ${KAFKA_SERVERS:localhost:9092}
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: point-service
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer

  security:
    jwt:
      secret: ${JWT_SECRET:your-secret-key}
      expiration: 86400000

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always

logging:
  level:
    com.example.loyalty.points: DEBUG
    org.springframework.security: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"

# Custom application properties
loyalty:
  points:
    earning-rules:
      default-rate: 0.001  # 1 point per 1000 currency units
      bonus-multipliers:
        weekend: 2.0
        holiday: 3.0
        special-event: 5.0
    fraud-detection:
      max-daily-earning: 10000
      max-transaction-amount: 1000000
      suspicious-pattern-threshold: 5
```

## 🧪 Test Template

```java
// src/test/java/com/loyalty/points/application/usecases/EarnPointsUseCaseTest.java
package com.example.loyalty.points.application.usecases;

import com.example.loyalty.points.domain.entities.PointTransaction;
import com.example.loyalty.points.domain.repositories.PointTransactionRepository;
import com.example.loyalty.points.domain.repositories.PointBalanceRepository;
import com.example.loyalty.points.domain.services.PointCalculationService;
import com.example.loyalty.points.domain.valueobjects.Points;
import com.example.loyalty.points.domain.valueobjects.Money;
import com.example.loyalty.points.application.dto.EarnPointsDTO;
import com.example.loyalty.points.infrastructure.messaging.publishers.PointEventPublisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EarnPointsUseCaseTest {

    @Mock
    private PointTransactionRepository transactionRepository;
    
    @Mock
    private PointBalanceRepository balanceRepository;
    
    @Mock
    private PointCalculationService calculationService;
    
    @Mock
    private PointEventPublisher eventPublisher;
    
    private EarnPointsUseCase earnPointsUseCase;

    @BeforeEach
    void setUp() {
        earnPointsUseCase = new EarnPointsUseCase(
            transactionRepository,
            balanceRepository,
            calculationService,
            eventPublisher
        );
    }

    @Test
    void execute_ShouldEarnPointsSuccessfully_WhenValidRequest() {
        // Arrange
        String userId = "user123";
        BigDecimal transactionAmount = new BigDecimal("100.00");
        Points expectedPoints = new Points(new BigDecimal("100"));
        
        EarnPointsDTO.Request request = new EarnPointsDTO.Request(
            userId,
            transactionAmount,
            "PURCHASE",
            "Product purchase",
            "ref123"
        );

        when(calculationService.calculateEarnedPoints(any(Money.class), eq(userId), eq("PURCHASE")))
            .thenReturn(expectedPoints);
        
        PointTransaction mockTransaction = mock(PointTransaction.class);
        when(mockTransaction.getId()).thenReturn(java.util.UUID.randomUUID());
        when(transactionRepository.save(any(PointTransaction.class))).thenReturn(mockTransaction);

        // Act
        EarnPointsDTO.Response response = earnPointsUseCase.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals(expectedPoints.getValue(), response.getPointsEarned());
        assertEquals("Points earned successfully", response.getMessage());
        
        verify(calculationService).calculateEarnedPoints(any(Money.class), eq(userId), eq("PURCHASE"));
        verify(transactionRepository, times(2)).save(any(PointTransaction.class));
        verify(balanceRepository).addPoints(eq(userId), eq(expectedPoints));
        verify(eventPublisher).publishPointsEarned(any(PointTransaction.class));
    }

    @Test
    void execute_ShouldThrowException_WhenUserIdIsNull() {
        // Arrange
        EarnPointsDTO.Request request = new EarnPointsDTO.Request(
            null,
            new BigDecimal("100.00"),
            "PURCHASE",
            "Product purchase",
            "ref123"
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> earnPointsUseCase.execute(request));
        
        verify(transactionRepository, never()).save(any(PointTransaction.class));
        verify(balanceRepository, never()).addPoints(any(), any());
        verify(eventPublisher, never()).publishPointsEarned(any());
    }
}
```

## 📋 Development Checklist

### Setup Tasks
- [ ] Initialize Spring Boot project with dependencies
- [ ] Setup folder structure following clean architecture
- [ ] Configure PostgreSQL database connection
- [ ] Setup Redis for caching
- [ ] Configure Kafka for messaging
- [ ] Create Docker configuration
- [ ] Setup testing framework with TestContainers

### Domain Implementation
- [ ] Implement domain entities (PointTransaction, PointBalance)
- [ ] Create value objects (Points, Money, TransactionType)
- [ ] Define repository interfaces
- [ ] Implement domain services (PointCalculationService, FraudDetectionService)

### Application Layer
- [ ] Implement use cases (EarnPoints, RedeemPoints, GetBalance)
- [ ] Create DTOs for data transfer
- [ ] Add input validation
- [ ] Implement error handling

### Infrastructure Layer
- [ ] Implement JPA repositories
- [ ] Setup Redis repositories for caching
- [ ] Configure Kafka producers and consumers
- [ ] Add database migrations
- [ ] Setup external service clients

### Interface Layer
- [ ] Create REST controllers
- [ ] Setup security configuration
- [ ] Add API documentation with OpenAPI
- [ ] Implement health check endpoints
- [ ] Add logging and monitoring

### Testing
- [ ] Write unit tests for domain logic
- [ ] Create integration tests for repositories
- [ ] Add controller tests
- [ ] Setup end-to-end tests
- [ ] Configure test coverage reporting

---

**Next Steps:**
1. Use this template to create the point service
2. Start with domain implementation
3. Add infrastructure components
4. Implement use cases and controllers
5. Create comprehensive tests