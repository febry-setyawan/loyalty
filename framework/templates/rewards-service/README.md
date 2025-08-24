# Rewards Service Template - Java Spring Boot

**⚠️ IMPORTANT: This service follows the [STANDARD SPRING BOOT TEMPLATE](../SPRING-BOOT-STANDARD-TEMPLATE.md)**  
**For new services, use the standard template and replace placeholders with rewards-service specifics.**

**Technology Stack:** Java Spring Boot, JDK 17, Maven, PostgreSQL, Redis, S3  
**Architecture Pattern:** Clean Architecture (Domain-Driven Design)  
**Purpose:** Catalog management, inventory, tier-based availability  

---

## 📁 **STANDARDIZED** Project Structure

```
rewards-service/
├── src/
│   ├── main/java/com/example/loyalty/rewards/
│   │   ├── domain/                      # Business Logic Layer
│   │   │   ├── entities/               # Domain entities
│   │   │   │   ├── Reward.java
│   │   │   │   ├── Category.java
│   │   │   │   └── Inventory.java
│   │   │   ├── repositories/           # Repository interfaces
│   │   │   │   ├── RewardRepository.java
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   └── InventoryRepository.java
│   │   │   ├── services/              # Domain services
│   │   │   │   ├── RewardCatalogService.java
│   │   │   │   ├── InventoryService.java
│   │   │   │   └── TierEligibilityService.java
│   │   │   └── valueobjects/          # Value objects
│   │   │       ├── Points.java
│   │   │       ├── Tier.java
│   │   │       └── ImageUrl.java
│   │   │
│   │   ├── application/               # Application Logic Layer
│   │   │   ├── usecases/             # Use case implementations
│   │   │   │   ├── CreateReward.java
│   │   │   │   ├── UpdateReward.java
│   │   │   │   ├── SearchRewards.java
│   │   │   │   └── CheckAvailability.java
│   │   │   ├── dto/                   # Data Transfer Objects
│   │   │   │   ├── RewardDTO.java
│   │   │   │   ├── CategoryDTO.java
│   │   │   │   └── SearchCriteriaDTO.java
│   │   │   └── validators/            # Input validation
│   │   │       ├── RewardValidator.java
│   │   │       └── CategoryValidator.java
│   │   │
│   │   ├── infrastructure/            # Infrastructure Layer
│   │   │   ├── persistence/          # Database implementations
│   │   │   │   ├── repositories/
│   │   │   │   │   ├── JpaRewardRepository.java
│   │   │   │   │   ├── JpaCategoryRepository.java
│   │   │   │   │   └── RedisInventoryRepository.java
│   │   │   │   └── entities/
│   │   │   │       ├── RewardEntity.java
│   │   │   │       ├── CategoryEntity.java
│   │   │   │       └── InventoryEntity.java
│   │   │   ├── external/             # External service integrations
│   │   │   │   ├── S3ImageService.java
│   │   │   │   ├── EmailNotificationService.java
│   │   │   │   └── UserTierService.java
│   │   │   ├── messaging/            # Message queue handling
│   │   │   │   ├── EventPublisher.java
│   │   │   │   └── EventHandler.java
│   │   │   └── config/               # Configuration
│   │   │       ├── DatabaseConfig.java
│   │   │       ├── RedisConfig.java
│   │   │       └── S3Config.java
│   │   │
│   │   ├── interfaces/               # Interface Layer (Controllers)
│   │   │   ├── web/                  # REST controllers
│   │   │   │   ├── controllers/
│   │   │   │   │   ├── RewardController.java
│   │   │   │   │   ├── CategoryController.java
│   │   │   │   │   └── SearchController.java
│   │   │   │   ├── dto/              # Web DTOs
│   │   │   │   │   ├── CreateRewardRequest.java
│   │   │   │   │   ├── UpdateRewardRequest.java
│   │   │   │   │   ├── SearchRequest.java
│   │   │   │   │   └── ApiResponse.java
│   │   │   │   ├── config/           # Web configuration
│   │   │   │   │   ├── WebConfig.java
│   │   │   │   │   └── SecurityConfig.java
│   │   │   │   └── filters/          # Request/Response filters
│   │   │   │       ├── AuthenticationFilter.java
│   │   │   │       └── RateLimitFilter.java
│   │   │   └── messaging/            # Message consumers
│   │   │       └── EventConsumer.java
│   │   │
│   │   ├── shared/                   # Shared utilities
│   │   │   ├── exceptions/           # Custom exception classes
│   │   │   ├── utils/                # Helper functions
│   │   │   └── constants/            # Application constants
│   │   │
│   │   └── RewardsServiceApplication.java    # Application entry point
│   │
│   ├── main/resources/               # Application resources
│   │   ├── application.yml           # Configuration
│   │   ├── application-dev.yml       # Development config
│   │   ├── application-prod.yml      # Production config
│   │   └── db/migration/             # Database migrations
│   │
│   └── test/java/                    # Test files
│       ├── unit/                     # Unit tests
│       ├── integration/              # Integration tests
│       └── e2e/                      # End-to-end tests
│
├── docker/                           # Docker configurations
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── docker-compose.test.yml
│
├── docs/                             # Service documentation
│   ├── api/                          # API documentation
│   ├── deployment/                   # Deployment guides
│   └── development/                  # Development guides
│
├── scripts/                          # Utility scripts
│   ├── setup.sh
│   └── run-tests.sh
│
├── pom.xml                           # Maven dependencies
├── .gitignore
└── README.md                         # Service documentation
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
    <artifactId>rewards-service</artifactId>
    <version>1.0.0</version>
    <name>Rewards Service</name>
    <description>Rewards catalog management service for loyalty system</description>

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

        <!-- AWS S3 for image storage -->
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>s3</artifactId>
            <version>2.20.143</version>
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

## 🔧 Core Components

### 1. Domain Layer

#### Reward Entity
```java
// src/main/java/com/example/loyalty/rewards/domain/entities/Reward.java
package com.example.loyalty.rewards.domain.entities;

import com.example.loyalty.rewards.domain.valueobjects.Points;
import com.example.loyalty.rewards.domain.valueobjects.Tier;
import com.example.loyalty.rewards.domain.valueobjects.ImageUrl;
import java.time.LocalDateTime;
import java.util.UUID;

public class Reward {
    private UUID id;
    private String name;
    private String description;
    private String category;
    private Points pointCost;
    private ImageUrl imageUrl;
    private Tier minTier;
    private Integer maxRedemptionsPerUser;
    private Integer totalInventory;
    private Integer availableInventory;
    private boolean isActive;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Reward(UUID id, String name, String description, String category, 
                  Points pointCost, Tier minTier) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.pointCost = pointCost;
        this.minTier = minTier;
        this.isActive = true;
        this.maxRedemptionsPerUser = 1;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateInventory(Integer newInventory) {
        this.availableInventory = newInventory;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isEligibleForTier(Tier userTier) {
        return userTier.isEqualOrHigher(this.minTier);
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public Points getPointCost() { return pointCost; }
    public Tier getMinTier() { return minTier; }
    public boolean isActive() { return isActive; }
    // ... other getters
}
```

## 📚 API Documentation Template

### REST Endpoints

#### Rewards Controller
```java
// src/main/java/com/example/loyalty/rewards/interfaces/web/controllers/RewardController.java
package com.example.loyalty.rewards.interfaces.web.controllers;

import com.example.loyalty.rewards.application.usecases.CreateReward;
import com.example.loyalty.rewards.application.usecases.SearchRewards;
import com.example.loyalty.rewards.interfaces.web.dto.CreateRewardRequest;
import com.example.loyalty.rewards.interfaces.web.dto.SearchRequest;
import com.example.loyalty.rewards.interfaces.web.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rewards")
public class RewardController {
    
    private final CreateReward createReward;
    private final SearchRewards searchRewards;

    public RewardController(CreateReward createReward, SearchRewards searchRewards) {
        this.createReward = createReward;
        this.searchRewards = searchRewards;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createReward(@RequestBody CreateRewardRequest request) {
        // Implementation
        return ResponseEntity.ok(new ApiResponse("Reward created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> searchRewards(@ModelAttribute SearchRequest request) {
        // Implementation
        return ResponseEntity.ok(new ApiResponse("Search completed"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getReward(@PathVariable String id) {
        // Implementation
        return ResponseEntity.ok(new ApiResponse("Reward retrieved"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateReward(@PathVariable String id, 
                                                   @RequestBody CreateRewardRequest request) {
        // Implementation
        return ResponseEntity.ok(new ApiResponse("Reward updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteReward(@PathVariable String id) {
        // Implementation
        return ResponseEntity.ok(new ApiResponse("Reward deleted"));
    }
}
```

## 📋 Development Checklist

### Setup Tasks
- [ ] Setup Maven project with correct groupId (com.example.loyalty)
- [ ] Configure PostgreSQL database connection
- [ ] Setup Redis for caching
- [ ] Configure AWS S3 for image storage
- [ ] Setup Kafka for messaging
- [ ] Configure application profiles (dev, prod)

### Implementation Tasks
- [ ] Implement domain entities (Reward, Category, Inventory)
- [ ] Create repository interfaces
- [ ] Implement use cases (CreateReward, SearchRewards, etc.)
- [ ] Create REST controllers
- [ ] Setup database migrations
- [ ] Implement image upload functionality
- [ ] Add validation and error handling
- [ ] Setup logging and monitoring

### Integration Tasks
- [ ] Integrate with User Service for tier validation
- [ ] Setup messaging for inventory updates
- [ ] Configure S3 bucket policies
- [ ] Setup API documentation (OpenAPI)
- [ ] Create Docker configuration
- [ ] Setup CI/CD pipeline

---

**Technology:** Java Spring Boot + JDK 17 + Maven  
**GroupId:** com.example.loyalty  
**ArtifactId:** rewards-service  
**Version:** 1.0.0