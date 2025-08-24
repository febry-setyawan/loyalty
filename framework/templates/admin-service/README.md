# Admin Service Template - Java Spring Boot

**⚠️ IMPORTANT: This service follows the [STANDARD SPRING BOOT TEMPLATE](../SPRING-BOOT-STANDARD-TEMPLATE.md)**  
**For new services, use the standard template and replace placeholders with admin-service specifics.**

**Technology Stack:** Java Spring Boot, JDK 17, Maven, PostgreSQL, React Frontend  
**Architecture Pattern:** Clean Architecture (Domain-Driven Design) + Frontend Integration  
**Purpose:** Administrative dashboard, reporting, system configuration  

---

## 📁 **STANDARDIZED** Project Structure

```
admin-service/
├── backend/                          # Spring Boot backend
│   ├── src/
│   │   ├── main/java/com/example/loyalty/admin/
│   │   │   ├── domain/                      # Business Logic Layer
│   │   │   │   ├── entities/               # Domain entities
│   │   │   │   │   ├── AdminUser.java
│   │   │   │   │   ├── SystemConfig.java
│   │   │   │   │   └── AuditLog.java
│   │   │   │   ├── repositories/           # Repository interfaces
│   │   │   │   │   ├── AdminUserRepository.java
│   │   │   │   │   ├── SystemConfigRepository.java
│   │   │   │   │   └── AuditLogRepository.java
│   │   │   │   ├── services/              # Domain services
│   │   │   │   │   ├── AdminAuthService.java
│   │   │   │   │   ├── SystemConfigService.java
│   │   │   │   │   └── ReportingService.java
│   │   │   │   └── valueobjects/          # Value objects
│   │   │   │       ├── Role.java
│   │   │   │       ├── Permission.java
│   │   │   │       └── ConfigValue.java
│   │   │   │
│   │   │   ├── application/               # Application Logic Layer
│   │   │   │   ├── usecases/             # Use case implementations
│   │   │   │   │   ├── CreateAdminUser.java
│   │   │   │   │   ├── GenerateReport.java
│   │   │   │   │   ├── UpdateSystemConfig.java
│   │   │   │   │   └── ViewAuditLogs.java
│   │   │   │   ├── dto/                   # Data Transfer Objects
│   │   │   │   │   ├── AdminUserDTO.java
│   │   │   │   │   ├── ReportDTO.java
│   │   │   │   │   └── ConfigDTO.java
│   │   │   │   └── validators/            # Input validation
│   │   │   │       ├── AdminUserValidator.java
│   │   │   │       └── ConfigValidator.java
│   │   │   │
│   │   │   ├── infrastructure/            # Infrastructure Layer
│   │   │   │   ├── persistence/          # Database implementations
│   │   │   │   │   ├── repositories/
│   │   │   │   │   │   ├── JpaAdminUserRepository.java
│   │   │   │   │   │   ├── JpaSystemConfigRepository.java
│   │   │   │   │   │   └── JpaAuditLogRepository.java
│   │   │   │   │   └── entities/
│   │   │   │   │       ├── AdminUserEntity.java
│   │   │   │   │       ├── SystemConfigEntity.java
│   │   │   │   │       └── AuditLogEntity.java
│   │   │   │   ├── external/             # External service integrations
│   │   │   │   │   ├── UserServiceClient.java
│   │   │   │   │   ├── PointServiceClient.java
│   │   │   │   │   └── RewardsServiceClient.java
│   │   │   │   ├── messaging/            # Message queue handling
│   │   │   │   │   ├── EventPublisher.java
│   │   │   │   │   └── EventHandler.java
│   │   │   │   └── config/               # Configuration
│   │   │   │       ├── DatabaseConfig.java
│   │   │   │       ├── SecurityConfig.java
│   │   │   │       └── ClientConfig.java
│   │   │   │
│   │   │   ├── interfaces/               # Interface Layer (Controllers)
│   │   │   │   ├── web/                  # REST controllers
│   │   │   │   │   ├── controllers/
│   │   │   │   │   │   ├── AdminUserController.java
│   │   │   │   │   │   ├── DashboardController.java
│   │   │   │   │   │   ├── ReportController.java
│   │   │   │   │   │   └── SystemConfigController.java
│   │   │   │   │   ├── dto/              # Web DTOs
│   │   │   │   │   │   ├── CreateAdminUserRequest.java
│   │   │   │   │   │   ├── ReportRequest.java
│   │   │   │   │   │   ├── ConfigUpdateRequest.java
│   │   │   │   │   │   └── DashboardResponse.java
│   │   │   │   │   ├── config/           # Web configuration
│   │   │   │   │   │   ├── WebConfig.java
│   │   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   │   └── CorsConfig.java
│   │   │   │   │   └── filters/          # Request/Response filters
│   │   │   │   │       ├── AdminAuthenticationFilter.java
│   │   │   │   │       └── AuditLogFilter.java
│   │   │   │   └── messaging/            # Message consumers
│   │   │   │       └── SystemEventConsumer.java
│   │   │   │
│   │   │   ├── shared/                   # Shared utilities
│   │   │   │   ├── exceptions/           # Custom exception classes
│   │   │   │   ├── utils/                # Helper functions
│   │   │   │   └── constants/            # Application constants
│   │   │   │
│   │   │   └── AdminServiceApplication.java    # Application entry point
│   │   │
│   │   ├── main/resources/               # Application resources
│   │   │   ├── application.yml           # Configuration
│   │   │   ├── application-dev.yml       # Development config
│   │   │   ├── application-prod.yml      # Production config
│   │   │   ├── static/                   # Static files (React build)
│   │   │   └── db/migration/             # Database migrations
│   │   │
│   │   └── test/java/                    # Test files
│   │       ├── unit/                     # Unit tests
│   │       ├── integration/              # Integration tests
│   │       └── e2e/                      # End-to-end tests
│   │
│   ├── pom.xml                           # Maven dependencies
│   ├── .gitignore
│   └── README.md
│
├── frontend/                             # React frontend
│   ├── src/
│   │   ├── components/                   # React components
│   │   │   ├── common/                   # Common components
│   │   │   ├── dashboard/                # Dashboard components
│   │   │   ├── users/                    # User management components
│   │   │   ├── reports/                  # Report components
│   │   │   └── settings/                 # Settings components
│   │   ├── pages/                        # Page components
│   │   │   ├── Dashboard.jsx
│   │   │   ├── UserManagement.jsx
│   │   │   ├── Reports.jsx
│   │   │   └── Settings.jsx
│   │   ├── services/                     # API services
│   │   │   ├── api.js                    # Base API configuration
│   │   │   ├── adminService.js           # Admin service calls
│   │   │   └── reportService.js          # Report service calls
│   │   ├── hooks/                        # Custom React hooks
│   │   ├── store/                        # State management
│   │   ├── utils/                        # Utilities
│   │   ├── styles/                       # CSS/SCSS files
│   │   ├── App.jsx                       # Main App component
│   │   └── index.js                      # Entry point
│   │
│   ├── public/                           # Static assets
│   ├── package.json                      # Frontend dependencies
│   └── README.md
│
├── docker/                               # Docker configurations
│   ├── Dockerfile.backend
│   ├── Dockerfile.frontend
│   ├── docker-compose.yml
│   └── docker-compose.prod.yml
│
├── docs/                                 # Service documentation
├── scripts/                              # Utility scripts
└── README.md                             # Main documentation
```

## 📦 Maven Configuration (backend/pom.xml)

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
    <artifactId>admin-service</artifactId>
    <version>1.0.0</version>
    <name>Admin Service</name>
    <description>Administrative dashboard service for loyalty system</description>

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

        <!-- Service Communication -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>

        <!-- Messaging -->
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>

        <!-- JWT for admin authentication -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
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

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

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
            </plugin>
            <!-- Frontend build integration -->
            <plugin>
                <groupId>com.github.eirslett</groupId>
                <artifactId>frontend-maven-plugin</artifactId>
                <version>1.12.1</version>
                <configuration>
                    <workingDirectory>../frontend</workingDirectory>
                    <installDirectory>target</installDirectory>
                </configuration>
                <executions>
                    <execution>
                        <id>install node and npm</id>
                        <goals>
                            <goal>install-node-and-npm</goal>
                        </goals>
                        <configuration>
                            <nodeVersion>v18.17.0</nodeVersion>
                            <npmVersion>9.6.7</npmVersion>
                        </configuration>
                    </execution>
                    <execution>
                        <id>npm install</id>
                        <goals>
                            <goal>npm</goal>
                        </goals>
                        <configuration>
                            <arguments>install</arguments>
                        </configuration>
                    </execution>
                    <execution>
                        <id>npm build</id>
                        <goals>
                            <goal>npm</goal>
                        </goals>
                        <configuration>
                            <arguments>run build</arguments>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

## 🔧 Core Components

### 1. Domain Layer

#### Admin User Entity
```java
// src/main/java/com/example/loyalty/admin/domain/entities/AdminUser.java
package com.example.loyalty.admin.domain.entities;

import com.example.loyalty.admin.domain.valueobjects.Role;
import com.example.loyalty.admin.domain.valueobjects.Permission;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class AdminUser {
    private UUID id;
    private String username;
    private String email;
    private String passwordHash;
    private String fullName;
    private Role role;
    private Set<Permission> permissions;
    private boolean isActive;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AdminUser(UUID id, String username, String email, String fullName, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.permissions = role.getPermissions();
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasPermission(Permission permission) {
        return this.permissions.contains(permission);
    }

    // Getters
    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public boolean isActive() { return isActive; }
    // ... other getters
}
```

### 2. Frontend Integration

#### Package.json (frontend/package.json)
```json
{
  "name": "loyalty-admin-frontend",
  "version": "1.0.0",
  "private": true,
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.8.0",
    "axios": "^1.3.0",
    "antd": "^5.2.0",
    "@ant-design/icons": "^5.0.1",
    "recharts": "^2.5.0",
    "react-query": "^3.39.0",
    "moment": "^2.29.0"
  },
  "devDependencies": {
    "@vitejs/plugin-react": "^3.1.0",
    "vite": "^4.1.0",
    "eslint": "^8.35.0",
    "eslint-plugin-react": "^7.32.0"
  },
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview",
    "lint": "eslint src --ext .js,.jsx,.ts,.tsx"
  }
}
```

## 📚 API Documentation

### Dashboard Controller
```java
// src/main/java/com/example/loyalty/admin/interfaces/web/controllers/DashboardController.java
package com.example.loyalty.admin.interfaces.web.controllers;

import com.example.loyalty.admin.application.usecases.GenerateReport;
import com.example.loyalty.admin.interfaces.web.dto.DashboardResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    private final GenerateReport generateReport;

    public DashboardController(GenerateReport generateReport) {
        this.generateReport = generateReport;
    }

    @GetMapping("/metrics")
    public ResponseEntity<DashboardResponse> getDashboardMetrics() {
        // Implementation for dashboard metrics
        return ResponseEntity.ok(new DashboardResponse());
    }

    @GetMapping("/users/stats")
    public ResponseEntity<Object> getUserStats() {
        // User statistics
        return ResponseEntity.ok(Map.of("totalUsers", 1000, "activeUsers", 800));
    }

    @GetMapping("/points/stats")
    public ResponseEntity<Object> getPointStats() {
        // Point statistics
        return ResponseEntity.ok(Map.of("totalPoints", 100000, "redeemedPoints", 30000));
    }
}
```

## 📋 Development Checklist

### Backend Setup Tasks
- [ ] Setup Maven project with correct groupId (com.example.loyalty)
- [ ] Configure PostgreSQL database connection
- [ ] Setup Spring Security for admin authentication
- [ ] Configure Feign clients for other services
- [ ] Setup Kafka for messaging
- [ ] Configure application profiles (dev, prod)

### Frontend Setup Tasks
- [ ] Initialize React project with Vite
- [ ] Setup Ant Design UI components
- [ ] Configure API client with Axios
- [ ] Setup React Query for data fetching
- [ ] Configure routing with React Router
- [ ] Setup build integration with Maven

### Implementation Tasks
- [ ] Implement admin user management
- [ ] Create dashboard with key metrics
- [ ] Build reporting functionality
- [ ] Implement system configuration management
- [ ] Setup audit logging
- [ ] Create user management interface
- [ ] Build report generation and export
- [ ] Implement role-based access control

### Integration Tasks
- [ ] Connect to all microservices for data aggregation
- [ ] Setup real-time dashboard updates
- [ ] Configure CORS for frontend-backend communication
- [ ] Setup production build pipeline
- [ ] Create Docker configurations for both backend and frontend
- [ ] Setup CI/CD pipeline

---

**Technology:** Java Spring Boot + JDK 17 + Maven (Backend) + React (Frontend)  
**GroupId:** com.example.loyalty  
**ArtifactId:** admin-service  
**Version:** 1.0.0