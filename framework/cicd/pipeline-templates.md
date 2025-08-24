# CI/CD Pipeline Templates

**Author:** Senior Principal Engineer  
**Version:** 1.0  
**Purpose:** Comprehensive CI/CD pipeline configurations for loyalty system services  
**Scope:** All microservices in the loyalty system  

---

## 🏗️ Pipeline Architecture

### Pipeline Strategy
```
┌─────────────────────────────────────────────────────────────────┐
│                       Source Control                            │
│                     (GitHub/GitLab)                            │
└─────────────────────┬───────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Continuous Integration                       │
│  • Code Quality     • Security Scan    • Unit Tests           │
│  • Linting          • Dependency Check • Integration Tests     │
│  • Build Process    • Coverage Report  • Performance Tests     │
└─────────────────────┬───────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Artifact Management                           │
│  • Docker Images   • Test Reports     • Security Reports       │
│  • Documentation   • Coverage Data    • Performance Data       │
└─────────────────────┬───────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────┐
│                  Continuous Deployment                          │
│  Development → Staging → Production                            │
│  • Auto Deploy    • Manual Approval   • Blue-Green Deploy     │
│  • Smoke Tests    • Integration Tests • Monitoring Setup      │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔧 GitHub Actions Workflows

### 1. Main CI/CD Pipeline

#### `.github/workflows/ci-cd-main.yml`
```yaml
name: CI/CD Main Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

env:
  REGISTRY: ghcr.io
  JAVA_VERSION: '17'
  MAVEN_VERSION: '3.9.4'

jobs:
  # ============================================================================
  # CODE QUALITY & SECURITY CHECKS
  # ============================================================================
  code-quality:
    name: Code Quality Analysis
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        with:
          fetch-depth: 0  # Needed for SonarCloud analysis

      - name: Setup Java
        uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: ${{ env.JAVA_VERSION }}
          cache: maven

      - name: Install Maven dependencies
        run: mvn dependency:resolve

      - name: Run checkstyle
        run: mvn checkstyle:check

      - name: Run SpotBugs
        run: mvn spotbugs:check

      - name: SonarCloud Scan
        uses: SonarSource/sonarcloud-github-action@master
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}

  security-scan:
    name: Security Analysis
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup Java
        uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: ${{ env.JAVA_VERSION }}
          cache: maven

      - name: Install dependencies
        run: npm ci

      - name: Run npm audit
        run: npm audit --audit-level=moderate

      - name: Run Snyk Security Scan
        uses: snyk/actions/node@master
        env:
          SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
        with:
          args: --severity-threshold=high

      - name: Run CodeQL Analysis
        uses: github/codeql-action/init@v2
        with:
          languages: javascript, java

      - name: Perform CodeQL Analysis
        uses: github/codeql-action/analyze@v2

  # ============================================================================
  # TESTING JOBS
  # ============================================================================
  test-node-services:
    name: Test Node.js Services
    runs-on: ubuntu-latest
    strategy:
      matrix:
        service: [user-service, rewards-service, admin-service]
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: test_password
          POSTGRES_USER: loyalty_test
          POSTGRES_DB: loyalty_test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432

      redis:
        image: redis:7-alpine
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 6379:6379

      kafka:
        image: confluentinc/cp-kafka:latest
        env:
          KAFKA_BROKER_ID: 1
          KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
          KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
          KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
        ports:
          - 9092:9092
          
      zookeeper:
        image: confluentinc/cp-zookeeper:latest
        env:
          ZOOKEEPER_CLIENT_PORT: 2181
          ZOOKEEPER_TICK_TIME: 2000
        ports:
          - 2181:2181

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: ${{ env.NODE_VERSION }}
          cache: 'npm'

      - name: Install dependencies
        working-directory: ./services/${{ matrix.service }}
        run: npm ci

      - name: Wait for services
        run: |
          until pg_isready -h localhost -p 5432 -U loyalty_test; do sleep 1; done
          until redis-cli -h localhost -p 6379 ping; do sleep 1; done

      - name: Run database migrations
        working-directory: ./services/${{ matrix.service }}
        run: npm run migrate:test
        env:
          DB_HOST: localhost
          DB_PORT: 5432
          DB_NAME: loyalty_test
          DB_USERNAME: loyalty_test
          DB_PASSWORD: test_password
          REDIS_HOST: localhost
          REDIS_PORT: 6379
          RABBITMQ_URL: amqp://test_user:test_password@localhost:5672

      - name: Run unit tests
        working-directory: ./services/${{ matrix.service }}
        run: npm run test:unit
        env:
          NODE_ENV: test

      - name: Run integration tests
        working-directory: ./services/${{ matrix.service }}
        run: npm run test:integration
        env:
          DB_HOST: localhost
          DB_PORT: 5432
          DB_NAME: loyalty_test
          DB_USERNAME: loyalty_test
          DB_PASSWORD: test_password
          REDIS_HOST: localhost
          REDIS_PORT: 6379
          RABBITMQ_URL: amqp://test_user:test_password@localhost:5672

      - name: Generate coverage report
        working-directory: ./services/${{ matrix.service }}
        run: npm run test:coverage

      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          directory: ./services/${{ matrix.service }}/coverage
          flags: ${{ matrix.service }}

  test-java-services:
    name: Test Java Services
    runs-on: ubuntu-latest
    strategy:
      matrix:
        service: [point-service]

    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: test_password
          POSTGRES_USER: loyalty_test
          POSTGRES_DB: loyalty_test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432

      redis:
        image: redis:7-alpine
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 6379:6379

      kafka:
        image: confluentinc/cp-kafka:latest
        env:
          KAFKA_BROKER_ID: 1
          KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
          KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
          KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
        ports:
          - 9092:9092

      zookeeper:
        image: confluentinc/cp-zookeeper:latest
        env:
          ZOOKEEPER_CLIENT_PORT: 2181
          ZOOKEEPER_TICK_TIME: 2000
        ports:
          - 2181:2181

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup Java
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: maven

      - name: Wait for services
        run: |
          until pg_isready -h localhost -p 5432 -U loyalty_test; do sleep 1; done
          until redis-cli -h localhost -p 6379 ping; do sleep 1; done

      - name: Run tests
        working-directory: ./services/${{ matrix.service }}
        run: mvn clean test
        env:
          SPRING_PROFILES_ACTIVE: test
          DB_HOST: localhost
          DB_PORT: 5432
          DB_NAME: loyalty_test
          DB_USERNAME: loyalty_test
          DB_PASSWORD: test_password
          REDIS_HOST: localhost
          REDIS_PORT: 6379
          KAFKA_SERVERS: localhost:9092

      - name: Generate coverage report
        working-directory: ./services/${{ matrix.service }}
        run: mvn jacoco:report

      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          directory: ./services/${{ matrix.service }}/target/site/jacoco
          flags: ${{ matrix.service }}

  # ============================================================================
  # END-TO-END TESTING
  # ============================================================================
  e2e-tests:
    name: End-to-End Tests
    runs-on: ubuntu-latest
    needs: [test-node-services, test-java-services]
    if: github.event_name == 'push' && github.ref == 'refs/heads/main'

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: ${{ env.NODE_VERSION }}
          cache: 'npm'

      - name: Start services with Docker Compose
        run: |
          cd e2e-tests
          docker-compose up -d
          sleep 30  # Wait for services to be ready

      - name: Run E2E tests
        working-directory: ./e2e-tests
        run: |
          npm ci
          npm run test:e2e
        env:
          API_BASE_URL: http://localhost:8080

      - name: Generate E2E test report
        if: always()
        uses: dorny/test-reporter@v1
        with:
          name: E2E Test Results
          path: 'e2e-tests/test-results/*.xml'
          reporter: jest-junit

      - name: Cleanup
        if: always()
        run: |
          cd e2e-tests
          docker-compose down -v

  # ============================================================================
  # PERFORMANCE TESTING
  # ============================================================================
  performance-tests:
    name: Performance Tests
    runs-on: ubuntu-latest
    needs: [test-node-services, test-java-services]
    if: github.event_name == 'push' && github.ref == 'refs/heads/main'

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: ${{ env.NODE_VERSION }}
          cache: 'npm'

      - name: Start services for performance testing
        run: |
          cd performance-tests
          docker-compose up -d
          sleep 45  # Wait for services to be fully ready

      - name: Run K6 Performance Tests
        uses: grafana/k6-action@v0.3.0
        with:
          filename: performance-tests/k6-test-suite.js
        env:
          K6_PROMETHEUS_RW_SERVER_URL: ${{ secrets.K6_PROMETHEUS_SERVER }}

      - name: Upload performance results
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: performance-results
          path: performance-tests/results/

  # ============================================================================
  # BUILD & PUBLISH
  # ============================================================================
  build:
    name: Build and Push Images
    runs-on: ubuntu-latest
    needs: [code-quality, security-scan, test-node-services, test-java-services]
    if: github.event_name == 'push'
    
    strategy:
      matrix:
        service: [user-service, point-service, rewards-service, admin-service]

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Login to Container Registry
        uses: docker/login-action@v3
        with:
          registry: ${{ env.REGISTRY }}
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Extract metadata
        id: meta
        uses: docker/metadata-action@v5
        with:
          images: ${{ env.REGISTRY }}/${{ github.repository }}/${{ matrix.service }}
          tags: |
            type=ref,event=branch
            type=ref,event=pr
            type=sha,prefix={{branch}}-
            type=raw,value=latest,enable={{is_default_branch}}

      - name: Build and push Docker image
        uses: docker/build-push-action@v5
        with:
          context: ./services/${{ matrix.service }}
          push: true
          tags: ${{ steps.meta.outputs.tags }}
          labels: ${{ steps.meta.outputs.labels }}
          cache-from: type=gha
          cache-to: type=gha,mode=max
          build-args: |
            GIT_COMMIT=${{ github.sha }}
            BUILD_DATE=${{ steps.meta.outputs.labels.build-date }}

  # ============================================================================
  # DEPLOYMENT
  # ============================================================================
  deploy-staging:
    name: Deploy to Staging
    runs-on: ubuntu-latest
    needs: [build, e2e-tests]
    if: github.ref == 'refs/heads/develop'
    environment: staging

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup kubectl
        uses: azure/setup-kubectl@v3
        with:
          version: '1.28.0'

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ${{ secrets.AWS_REGION }}

      - name: Update kubeconfig
        run: |
          aws eks update-kubeconfig --name loyalty-staging-cluster --region ${{ secrets.AWS_REGION }}

      - name: Deploy to staging
        run: |
          cd k8s/staging
          kubectl apply -f .
          kubectl set image deployment/user-service user-service=${{ env.REGISTRY }}/${{ github.repository }}/user-service:develop-${{ github.sha }}
          kubectl set image deployment/point-service point-service=${{ env.REGISTRY }}/${{ github.repository }}/point-service:develop-${{ github.sha }}
          kubectl set image deployment/rewards-service rewards-service=${{ env.REGISTRY }}/${{ github.repository }}/rewards-service:develop-${{ github.sha }}
          kubectl set image deployment/admin-service admin-service=${{ env.REGISTRY }}/${{ github.repository }}/admin-service:develop-${{ github.sha }}

      - name: Wait for deployment
        run: |
          kubectl rollout status deployment/user-service --timeout=300s
          kubectl rollout status deployment/point-service --timeout=300s
          kubectl rollout status deployment/rewards-service --timeout=300s
          kubectl rollout status deployment/admin-service --timeout=300s

      - name: Run smoke tests
        run: |
          cd smoke-tests
          npm ci
          npm run test:staging
        env:
          STAGING_API_URL: ${{ secrets.STAGING_API_URL }}

  deploy-production:
    name: Deploy to Production
    runs-on: ubuntu-latest
    needs: [build, e2e-tests, performance-tests]
    if: github.ref == 'refs/heads/main'
    environment: production

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup kubectl
        uses: azure/setup-kubectl@v3
        with:
          version: '1.28.0'

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ${{ secrets.AWS_REGION }}

      - name: Update kubeconfig
        run: |
          aws eks update-kubeconfig --name loyalty-production-cluster --region ${{ secrets.AWS_REGION }}

      - name: Blue-Green Deployment
        run: |
          cd k8s/production
          
          # Deploy green environment
          kubectl apply -f green/
          kubectl set image deployment/user-service-green user-service=${{ env.REGISTRY }}/${{ github.repository }}/user-service:main-${{ github.sha }} -n production
          kubectl set image deployment/point-service-green point-service=${{ env.REGISTRY }}/${{ github.repository }}/point-service:main-${{ github.sha }} -n production
          kubectl set image deployment/rewards-service-green rewards-service=${{ env.REGISTRY }}/${{ github.repository }}/rewards-service:main-${{ github.sha }} -n production
          kubectl set image deployment/admin-service-green admin-service=${{ env.REGISTRY }}/${{ github.repository }}/admin-service:main-${{ github.sha }} -n production
          
          # Wait for green deployment
          kubectl rollout status deployment/user-service-green --timeout=600s -n production
          kubectl rollout status deployment/point-service-green --timeout=600s -n production
          kubectl rollout status deployment/rewards-service-green --timeout=600s -n production
          kubectl rollout status deployment/admin-service-green --timeout=600s -n production

      - name: Run production smoke tests
        run: |
          cd smoke-tests
          npm ci
          npm run test:production:green
        env:
          GREEN_API_URL: ${{ secrets.GREEN_API_URL }}

      - name: Switch traffic to green
        run: |
          cd k8s/production
          kubectl patch service loyalty-api-service -p '{"spec":{"selector":{"version":"green"}}}' -n production

      - name: Final smoke tests
        run: |
          cd smoke-tests
          npm run test:production:final
        env:
          PRODUCTION_API_URL: ${{ secrets.PRODUCTION_API_URL }}

      - name: Cleanup blue environment
        if: success()
        run: |
          cd k8s/production
          kubectl delete -f blue/ -n production

  # ============================================================================
  # POST-DEPLOYMENT MONITORING
  # ============================================================================
  post-deploy-monitoring:
    name: Post-Deployment Monitoring
    runs-on: ubuntu-latest
    needs: [deploy-production]
    if: success()

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Wait for metrics stabilization
        run: sleep 300  # 5 minutes

      - name: Check application health
        run: |
          cd monitoring/scripts
          python check_health.py --env production
        env:
          DATADOG_API_KEY: ${{ secrets.DATADOG_API_KEY }}
          DATADOG_APP_KEY: ${{ secrets.DATADOG_APP_KEY }}

      - name: Validate performance metrics
        run: |
          cd monitoring/scripts
          python validate_metrics.py --deployment-time "5 minutes ago"
        env:
          PROMETHEUS_URL: ${{ secrets.PROMETHEUS_URL }}

      - name: Create deployment notification
        uses: 8398a7/action-slack@v3
        with:
          status: ${{ job.status }}
          fields: repo,message,commit,author,action,eventName,ref,workflow
          text: 'Production deployment completed successfully! :rocket:'
        env:
          SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK }}
```

### 2. Service-Specific Pipelines

#### Node.js Service Pipeline
```yaml
# .github/workflows/service-node.yml
name: Node.js Service CI/CD

on:
  push:
    paths:
      - 'services/user-service/**'
      - 'services/rewards-service/**'
      - 'services/admin-service/**'
  pull_request:
    paths:
      - 'services/user-service/**'
      - 'services/rewards-service/**'
      - 'services/admin-service/**'

env:
  NODE_VERSION: '18'

jobs:
  detect-changes:
    name: Detect Service Changes
    runs-on: ubuntu-latest
    outputs:
      user-service: ${{ steps.changes.outputs.user-service }}
      rewards-service: ${{ steps.changes.outputs.rewards-service }}
      admin-service: ${{ steps.changes.outputs.admin-service }}
    steps:
      - uses: actions/checkout@v4
      - uses: dorny/paths-filter@v2
        id: changes
        with:
          filters: |
            user-service:
              - 'services/user-service/**'
            rewards-service:
              - 'services/rewards-service/**'
            admin-service:
              - 'services/admin-service/**'

  build-and-test:
    name: Build and Test
    runs-on: ubuntu-latest
    needs: detect-changes
    strategy:
      matrix:
        service: [user-service, rewards-service, admin-service]
        include:
          - service: user-service
            port: 3001
            test-db: loyalty_user_test
          - service: rewards-service
            port: 3002
            test-db: loyalty_rewards_test
          - service: admin-service
            port: 3003
            test-db: loyalty_admin_test
    
    if: needs.detect-changes.outputs[matrix.service] == 'true'

    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: test_password
          POSTGRES_USER: test_user
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432

      redis:
        image: redis:7-alpine
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 6379:6379

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: ${{ env.NODE_VERSION }}
          cache: 'npm'
          cache-dependency-path: services/${{ matrix.service }}/package-lock.json

      - name: Install dependencies
        working-directory: services/${{ matrix.service }}
        run: npm ci

      - name: Create test database
        run: |
          PGPASSWORD=test_password createdb -h localhost -U test_user ${{ matrix.test-db }}
        env:
          PGPASSWORD: test_password

      - name: Run linting
        working-directory: services/${{ matrix.service }}
        run: npm run lint

      - name: Run type checking
        working-directory: services/${{ matrix.service }}
        run: npm run type-check

      - name: Run unit tests
        working-directory: services/${{ matrix.service }}
        run: npm run test:unit -- --coverage
        env:
          NODE_ENV: test

      - name: Run integration tests
        working-directory: services/${{ matrix.service }}
        run: npm run test:integration
        env:
          NODE_ENV: test
          DB_HOST: localhost
          DB_PORT: 5432
          DB_NAME: ${{ matrix.test-db }}
          DB_USERNAME: test_user
          DB_PASSWORD: test_password
          REDIS_HOST: localhost
          REDIS_PORT: 6379

      - name: Build application
        working-directory: services/${{ matrix.service }}
        run: npm run build

      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: test-results-${{ matrix.service }}
          path: services/${{ matrix.service }}/test-results/

      - name: Upload coverage
        uses: codecov/codecov-action@v3
        with:
          directory: services/${{ matrix.service }}/coverage
          flags: ${{ matrix.service }}
          name: ${{ matrix.service }}-coverage
```

#### Java Service Pipeline
```yaml
# .github/workflows/service-java.yml
name: Java Service CI/CD

on:
  push:
    paths:
      - 'services/point-service/**'
  pull_request:
    paths:
      - 'services/point-service/**'

env:
  JAVA_VERSION: '17'

jobs:
  test:
    name: Test Point Service
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: test_password
          POSTGRES_USER: test_user
          POSTGRES_DB: loyalty_points_test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432

      redis:
        image: redis:7-alpine
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 6379:6379

      kafka:
        image: confluentinc/cp-kafka:latest
        env:
          KAFKA_BROKER_ID: 1
          KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
          KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
          KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
        ports:
          - 9092:9092

      zookeeper:
        image: confluentinc/cp-zookeeper:latest
        env:
          ZOOKEEPER_CLIENT_PORT: 2181
          ZOOKEEPER_TICK_TIME: 2000
        ports:
          - 2181:2181

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup Java
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: maven

      - name: Wait for services
        run: |
          timeout 300 bash -c 'until pg_isready -h localhost -p 5432 -U test_user; do sleep 1; done'
          timeout 300 bash -c 'until redis-cli -h localhost -p 6379 ping; do sleep 1; done'
          sleep 30  # Additional wait for Kafka

      - name: Run checkstyle
        working-directory: services/point-service
        run: mvn checkstyle:check

      - name: Run SpotBugs
        working-directory: services/point-service
        run: mvn spotbugs:check

      - name: Run unit tests
        working-directory: services/point-service
        run: mvn test -Dspring.profiles.active=test
        env:
          DB_HOST: localhost
          DB_PORT: 5432
          DB_NAME: loyalty_points_test
          DB_USERNAME: test_user
          DB_PASSWORD: test_password
          REDIS_HOST: localhost
          REDIS_PORT: 6379
          KAFKA_SERVERS: localhost:9092

      - name: Run integration tests
        working-directory: services/point-service
        run: mvn verify -P integration-tests -Dspring.profiles.active=test
        env:
          DB_HOST: localhost
          DB_PORT: 5432
          DB_NAME: loyalty_points_test
          DB_USERNAME: test_user
          DB_PASSWORD: test_password
          REDIS_HOST: localhost
          REDIS_PORT: 6379
          KAFKA_SERVERS: localhost:9092

      - name: Generate test report
        if: always()
        uses: dorny/test-reporter@v1
        with:
          name: Maven Tests
          path: services/point-service/target/surefire-reports/*.xml
          reporter: java-junit

      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          directory: services/point-service/target/site/jacoco
          flags: point-service
          name: point-service-coverage

      - name: Build application
        working-directory: services/point-service
        run: mvn package -DskipTests

      - name: Upload build artifacts
        uses: actions/upload-artifact@v3
        with:
          name: point-service-jar
          path: services/point-service/target/*.jar
```

---

## 🔄 GitLab CI/CD Alternative

### `.gitlab-ci.yml`
```yaml
# GitLab CI/CD Pipeline
variables:
  DOCKER_DRIVER: overlay2
  DOCKER_TLS_CERTDIR: "/certs"
  NODE_VERSION: "18"
  JAVA_VERSION: "17"
  POSTGRES_DB: loyalty_test
  POSTGRES_USER: test_user
  POSTGRES_PASSWORD: test_password
  REDIS_URL: redis://redis:6379

stages:
  - validate
  - test
  - security
  - build
  - deploy-staging
  - deploy-production

include:
  - template: Security/SAST.gitlab-ci.yml
  - template: Security/Dependency-Scanning.gitlab-ci.yml
  - template: Security/Container-Scanning.gitlab-ci.yml

# ============================================================================
# VALIDATION STAGE
# ============================================================================
.node-base:
  image: node:${NODE_VERSION}-alpine
  cache:
    key: ${CI_COMMIT_REF_SLUG}
    paths:
      - node_modules/
      - services/*/node_modules/

lint-node-services:
  extends: .node-base
  stage: validate
  script:
    - cd services/user-service && npm ci && npm run lint
    - cd ../rewards-service && npm ci && npm run lint
    - cd ../admin-service && npm ci && npm run lint
  only:
    changes:
      - services/user-service/**/*
      - services/rewards-service/**/*
      - services/admin-service/**/*

lint-java-services:
  image: maven:3.9-openjdk-17
  stage: validate
  cache:
    key: ${CI_COMMIT_REF_SLUG}-maven
    paths:
      - .m2/repository/
  script:
    - cd services/point-service
    - mvn checkstyle:check
    - mvn spotbugs:check
  only:
    changes:
      - services/point-service/**/*

# ============================================================================
# TEST STAGE
# ============================================================================
test-user-service:
  extends: .node-base
  stage: test
  services:
    - postgres:15
    - redis:7-alpine
    - rabbitmq:3-alpine
  variables:
    POSTGRES_DB: ${POSTGRES_DB}
    POSTGRES_USER: ${POSTGRES_USER}
    POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
  script:
    - cd services/user-service
    - npm ci
    - npm run test:unit -- --coverage
    - npm run test:integration
  artifacts:
    reports:
      coverage_report:
        coverage_format: cobertura
        path: services/user-service/coverage/cobertura-coverage.xml
      junit:
        - services/user-service/test-results/*.xml
    expire_in: 1 week
  coverage: '/Lines\s*:\s*(\d+\.\d+)%/'
  only:
    changes:
      - services/user-service/**/*

test-point-service:
  image: maven:3.9-openjdk-17
  stage: test
  services:
    - postgres:15
    - redis:7-alpine
    - confluentinc/cp-kafka:latest
    - confluentinc/cp-zookeeper:latest
  variables:
    POSTGRES_DB: ${POSTGRES_DB}
    POSTGRES_USER: ${POSTGRES_USER}
    POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
  cache:
    key: ${CI_COMMIT_REF_SLUG}-maven
    paths:
      - .m2/repository/
  script:
    - cd services/point-service
    - mvn clean test -Dspring.profiles.active=test
    - mvn verify -P integration-tests
  artifacts:
    reports:
      junit:
        - services/point-service/target/surefire-reports/TEST-*.xml
      coverage_report:
        coverage_format: cobertura
        path: services/point-service/target/site/cobertura/coverage.xml
    expire_in: 1 week
  coverage: '/Line coverage: (\d+\.\d+)%/'
  only:
    changes:
      - services/point-service/**/*

# ============================================================================
# SECURITY STAGE
# ============================================================================
security-audit-node:
  extends: .node-base
  stage: security
  script:
    - cd services/user-service && npm audit --audit-level=moderate
    - cd ../rewards-service && npm audit --audit-level=moderate
    - cd ../admin-service && npm audit --audit-level=moderate
  allow_failure: false
  only:
    changes:
      - services/user-service/**/*
      - services/rewards-service/**/*
      - services/admin-service/**/*

dependency-scanning:
  stage: security
  variables:
    DS_EXCLUDE_PATHS: "spec, test, tests, tmp"

container-scanning:
  stage: security
  variables:
    CS_IMAGE: $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA

# ============================================================================
# BUILD STAGE
# ============================================================================
.build-service:
  image: docker:24-dind
  services:
    - docker:24-dind
  before_script:
    - docker login -u $CI_REGISTRY_USER -p $CI_REGISTRY_PASSWORD $CI_REGISTRY
  script:
    - cd services/$SERVICE_NAME
    - |
      docker build \
        --build-arg GIT_COMMIT=$CI_COMMIT_SHA \
        --build-arg BUILD_DATE=$(date -u +'%Y-%m-%dT%H:%M:%SZ') \
        --cache-from $CI_REGISTRY_IMAGE/$SERVICE_NAME:latest \
        --tag $CI_REGISTRY_IMAGE/$SERVICE_NAME:$CI_COMMIT_SHA \
        --tag $CI_REGISTRY_IMAGE/$SERVICE_NAME:latest \
        .
    - docker push $CI_REGISTRY_IMAGE/$SERVICE_NAME:$CI_COMMIT_SHA
    - docker push $CI_REGISTRY_IMAGE/$SERVICE_NAME:latest

build-user-service:
  extends: .build-service
  stage: build
  variables:
    SERVICE_NAME: user-service
  needs: ["test-user-service", "security-audit-node"]
  only:
    changes:
      - services/user-service/**/*

build-point-service:
  extends: .build-service
  stage: build
  variables:
    SERVICE_NAME: point-service
  needs: ["test-point-service"]
  only:
    changes:
      - services/point-service/**/*

# ============================================================================
# DEPLOYMENT STAGES
# ============================================================================
.deploy-base:
  image: alpine/helm:latest
  before_script:
    - apk add --no-cache curl
    - curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
    - chmod +x kubectl
    - mv kubectl /usr/local/bin/
    - echo $KUBECONFIG_BASE64 | base64 -d > ~/.kube/config

deploy-staging:
  extends: .deploy-base
  stage: deploy-staging
  environment:
    name: staging
    url: https://staging-api.loyalty.com
  script:
    - helm upgrade --install loyalty-staging ./helm/loyalty-system
        --namespace staging
        --set image.tag=$CI_COMMIT_SHA
        --set environment=staging
        --set ingress.hostname=staging-api.loyalty.com
        --wait --timeout=10m
  only:
    - develop

deploy-production:
  extends: .deploy-base
  stage: deploy-production
  environment:
    name: production
    url: https://api.loyalty.com
  when: manual
  script:
    # Blue-Green Deployment
    - |
      # Deploy green environment
      helm upgrade --install loyalty-green ./helm/loyalty-system \
        --namespace production \
        --set image.tag=$CI_COMMIT_SHA \
        --set environment=production \
        --set deployment.color=green \
        --set ingress.hostname=green-api.loyalty.com \
        --wait --timeout=15m
      
      # Run smoke tests on green
      cd smoke-tests
      npm ci
      GREEN_API_URL=https://green-api.loyalty.com npm run test
      
      # Switch traffic to green
      kubectl patch service loyalty-api-service \
        -p '{"spec":{"selector":{"color":"green"}}}' \
        -n production
      
      # Cleanup blue deployment
      helm uninstall loyalty-blue --namespace production || true
      
      # Rename green to blue for next deployment
      helm upgrade --install loyalty-blue ./helm/loyalty-system \
        --namespace production \
        --set image.tag=$CI_COMMIT_SHA \
        --set environment=production \
        --set deployment.color=blue \
        --set ingress.hostname=api.loyalty.com \
        --wait --timeout=10m
  only:
    - main
```

---

## 🐳 Docker Configurations

### Multi-Service Docker Compose for Development
```yaml
# docker-compose.dev.yml
version: '3.8'

services:
  # ============================================================================
  # DATABASES
  # ============================================================================
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: loyalty
      POSTGRES_USER: loyalty_user
      POSTGRES_PASSWORD: loyalty_pass
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./scripts/init-db.sql:/docker-entrypoint-initdb.d/init-db.sql
    networks:
      - loyalty-network

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - loyalty-network

  # ============================================================================
  # MESSAGE QUEUE
  # ============================================================================
  rabbitmq:
    image: rabbitmq:3-management-alpine
    environment:
      RABBITMQ_DEFAULT_USER: loyalty_user
      RABBITMQ_DEFAULT_PASS: loyalty_pass
    ports:
      - "5672:5672"
      - "15672:15672"
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq
    networks:
      - loyalty-network

  # ============================================================================
  # SERVICES
  # ============================================================================
  user-service:
    build:
      context: ./services/user-service
      dockerfile: Dockerfile.dev
    ports:
      - "3001:3001"
    environment:
      - NODE_ENV=development
      - DB_HOST=postgres
      - DB_PORT=5432
      - DB_NAME=loyalty
      - DB_USERNAME=loyalty_user
      - DB_PASSWORD=loyalty_pass
      - REDIS_HOST=redis
      - REDIS_PORT=6379
      - RABBITMQ_URL=amqp://loyalty_user:loyalty_pass@rabbitmq:5672
    depends_on:
      - postgres
      - redis
      - rabbitmq
    volumes:
      - ./services/user-service:/app
      - /app/node_modules
    networks:
      - loyalty-network

  point-service:
    build:
      context: ./services/point-service
      dockerfile: Dockerfile.dev
    ports:
      - "8081:8081"
    environment:
      - SPRING_PROFILES_ACTIVE=development
      - DB_HOST=postgres
      - DB_PORT=5432
      - DB_NAME=loyalty
      - DB_USERNAME=loyalty_user
      - DB_PASSWORD=loyalty_pass
      - REDIS_HOST=redis
      - REDIS_PORT=6379
      - KAFKA_SERVERS=kafka:9092
    depends_on:
      - postgres
      - redis
      - kafka
    volumes:
      - ./services/point-service:/app
    networks:
      - loyalty-network

  rewards-service:
    build:
      context: ./services/rewards-service
      dockerfile: Dockerfile.dev
    ports:
      - "3002:3002"
    environment:
      - NODE_ENV=development
      - DB_HOST=postgres
      - DB_PORT=5432
      - DB_NAME=loyalty
      - DB_USERNAME=loyalty_user
      - DB_PASSWORD=loyalty_pass
      - REDIS_HOST=redis
      - REDIS_PORT=6379
      - S3_BUCKET=loyalty-dev-assets
    depends_on:
      - postgres
      - redis
    volumes:
      - ./services/rewards-service:/app
      - /app/node_modules
    networks:
      - loyalty-network

  admin-service:
    build:
      context: ./services/admin-service
      dockerfile: Dockerfile.dev
    ports:
      - "3003:3003"
    environment:
      - NODE_ENV=development
      - DB_HOST=postgres
      - DB_PORT=5432
      - DB_NAME=loyalty
      - DB_USERNAME=loyalty_user
      - DB_PASSWORD=loyalty_pass
      - REDIS_HOST=redis
      - REDIS_PORT=6379
    depends_on:
      - postgres
      - redis
    volumes:
      - ./services/admin-service:/app
      - /app/node_modules
    networks:
      - loyalty-network

  # ============================================================================
  # API GATEWAY
  # ============================================================================
  api-gateway:
    image: kong:3.4-alpine
    environment:
      KONG_DATABASE: "off"
      KONG_DECLARATIVE_CONFIG: /kong/declarative/kong.yml
      KONG_PROXY_ACCESS_LOG: /dev/stdout
      KONG_ADMIN_ACCESS_LOG: /dev/stdout
      KONG_PROXY_ERROR_LOG: /dev/stderr
      KONG_ADMIN_ERROR_LOG: /dev/stderr
      KONG_ADMIN_LISTEN: "0.0.0.0:8001"
    ports:
      - "8000:8000"
      - "8443:8443"
      - "8001:8001"
      - "8444:8444"
    volumes:
      - ./infrastructure/kong:/kong/declarative
    networks:
      - loyalty-network

  # ============================================================================
  # MONITORING
  # ============================================================================
  prometheus:
    image: prom/prometheus:v2.45.0
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus:/etc/prometheus
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
    networks:
      - loyalty-network

  grafana:
    image: grafana/grafana:10.1.0
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana_data:/var/lib/grafana
      - ./monitoring/grafana:/etc/grafana/provisioning
    networks:
      - loyalty-network

volumes:
  postgres_data:
  redis_data:
  rabbitmq_data:
  prometheus_data:
  grafana_data:

networks:
  loyalty-network:
    driver: bridge
```

---

## 📊 Monitoring and Alerting

### Prometheus Configuration
```yaml
# monitoring/prometheus/prometheus.yml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "alert_rules.yml"

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093

scrape_configs:
  - job_name: 'user-service'
    static_configs:
      - targets: ['user-service:3001']
    metrics_path: '/metrics'
    scrape_interval: 30s

  - job_name: 'point-service'
    static_configs:
      - targets: ['point-service:8081']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 30s

  - job_name: 'rewards-service'
    static_configs:
      - targets: ['rewards-service:3002']
    metrics_path: '/metrics'
    scrape_interval: 30s

  - job_name: 'admin-service'
    static_configs:
      - targets: ['admin-service:3003']
    metrics_path: '/metrics'
    scrape_interval: 30s

  - job_name: 'postgres-exporter'
    static_configs:
      - targets: ['postgres-exporter:9187']

  - job_name: 'redis-exporter'
    static_configs:
      - targets: ['redis-exporter:9121']
```

### Alert Rules
```yaml
# monitoring/prometheus/alert_rules.yml
groups:
  - name: loyalty-system-alerts
    rules:
      # High response time alert
      - alert: HighResponseTime
        expr: histogram_quantile(0.95, sum(rate(http_request_duration_seconds_bucket[5m])) by (le, service)) > 3
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "High response time detected"
          description: "{{ $labels.service }} has 95th percentile response time above 3 seconds"

      # High error rate alert
      - alert: HighErrorRate
        expr: sum(rate(http_requests_total{status=~"5.."}[5m])) by (service) / sum(rate(http_requests_total[5m])) by (service) > 0.05
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"
          description: "{{ $labels.service }} has error rate above 5%"

      # Database connection issues
      - alert: DatabaseConnectionError
        expr: up{job=~".*-service"} == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Service down"
          description: "{{ $labels.job }} is down"

      # Memory usage alert
      - alert: HighMemoryUsage
        expr: (container_memory_usage_bytes / container_spec_memory_limit_bytes) > 0.85
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High memory usage"
          description: "{{ $labels.container_label_com_docker_compose_service }} is using more than 85% of available memory"

      # Point calculation performance
      - alert: PointCalculationSlow
        expr: histogram_quantile(0.95, sum(rate(point_calculation_duration_seconds_bucket[5m])) by (le)) > 2
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "Slow point calculation"
          description: "Point calculation is taking more than 2 seconds for 95th percentile"
```

This comprehensive CI/CD framework ensures quality, security, and reliability throughout the development and deployment lifecycle of the loyalty system.