# DevOps & Deployment Guide

**Author:** Senior Principal Engineer  
**Version:** 1.0  
**Date:** December 2024  
**Purpose:** Comprehensive DevOps practices and deployment guidelines for loyalty system  
**Scope:** All deployment environments and infrastructure management  

---

## 🚀 DevOps Philosophy

### Core DevOps Principles
1. **Infrastructure as Code:** All infrastructure defined and versioned in code
2. **Continuous Integration/Continuous Deployment:** Automated testing and deployment
3. **Monitoring & Observability:** Comprehensive system visibility
4. **Security Integration:** Security checks in every stage
5. **Automation First:** Minimize manual processes
6. **Fail Fast, Recover Faster:** Quick detection and resolution

---

## 🏗️ Infrastructure as Code

### 1. Terraform Infrastructure

#### Main Infrastructure Configuration
```hcl
# main.tf
terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  
  backend "s3" {
    bucket = "loyalty-system-terraform-state"
    key    = "infrastructure/terraform.tfstate"
    region = "us-west-2"
    encrypt = true
  }
}

provider "aws" {
  region = var.aws_region
  
  default_tags {
    tags = {
      Project     = "loyalty-system"
      Environment = var.environment
      ManagedBy   = "terraform"
      Owner       = "platform-team"
    }
  }
}

# VPC and Networking
module "vpc" {
  source = "terraform-aws-modules/vpc/aws"
  
  name = "${var.project_name}-${var.environment}"
  cidr = var.vpc_cidr
  
  azs             = var.availability_zones
  private_subnets = var.private_subnet_cidrs
  public_subnets  = var.public_subnet_cidrs
  
  enable_nat_gateway = true
  enable_vpn_gateway = false
  enable_dns_hostnames = true
  enable_dns_support = true
  
  # Flow logs for security monitoring
  enable_flow_log = true
  flow_log_destination_type = "cloud-watch-logs"
}

# EKS Cluster
module "eks" {
  source = "terraform-aws-modules/eks/aws"
  
  cluster_name    = "${var.project_name}-${var.environment}"
  cluster_version = "1.28"
  
  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.private_subnets
  
  # Node groups
  eks_managed_node_groups = {
    main = {
      desired_size = var.node_group_desired_size
      max_size     = var.node_group_max_size
      min_size     = var.node_group_min_size
      
      instance_types = ["t3.large"]
      
      k8s_labels = {
        Environment = var.environment
        NodeGroup   = "main"
      }
    }
  }
  
  # Enable cluster logging
  cluster_enabled_log_types = ["api", "audit", "authenticator", "controllerManager", "scheduler"]
}

# RDS Database
resource "aws_db_instance" "main" {
  identifier = "${var.project_name}-${var.environment}"
  
  engine         = "postgres"
  engine_version = "15.4"
  instance_class = var.db_instance_class
  
  allocated_storage     = var.db_allocated_storage
  max_allocated_storage = var.db_max_allocated_storage
  storage_encrypted     = true
  
  db_name  = var.db_name
  username = var.db_username
  password = var.db_password
  
  vpc_security_group_ids = [aws_security_group.rds.id]
  db_subnet_group_name   = aws_db_subnet_group.main.name
  
  backup_retention_period = var.environment == "production" ? 7 : 1
  backup_window          = "03:00-04:00"
  maintenance_window     = "sun:04:00-sun:05:00"
  
  skip_final_snapshot = var.environment != "production"
  deletion_protection = var.environment == "production"
  
  performance_insights_enabled = true
  monitoring_interval          = 60
  
  tags = {
    Name = "${var.project_name}-${var.environment}-db"
  }
}

# Redis Cluster
resource "aws_elasticache_replication_group" "main" {
  replication_group_id       = "${var.project_name}-${var.environment}"
  description                = "Redis cluster for ${var.project_name} ${var.environment}"
  
  node_type            = var.redis_node_type
  port                 = 6379
  parameter_group_name = "default.redis7"
  
  num_cache_clusters = var.redis_num_nodes
  
  subnet_group_name  = aws_elasticache_subnet_group.main.name
  security_group_ids = [aws_security_group.redis.id]
  
  at_rest_encryption_enabled = true
  transit_encryption_enabled = true
  auth_token                 = var.redis_auth_token
  
  automatic_failover_enabled = var.environment == "production"
  multi_az_enabled          = var.environment == "production"
  
  log_delivery_configuration {
    destination      = aws_cloudwatch_log_group.redis_slow.name
    destination_type = "cloudwatch-logs"
    log_format       = "text"
    log_type         = "slow-log"
  }
}
```

#### Environment-Specific Variables
```hcl
# environments/production/terraform.tfvars
aws_region = "us-west-2"
environment = "production"

# VPC Configuration
vpc_cidr = "10.0.0.0/16"
availability_zones = ["us-west-2a", "us-west-2b", "us-west-2c"]
private_subnet_cidrs = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
public_subnet_cidrs = ["10.0.101.0/24", "10.0.102.0/24", "10.0.103.0/24"]

# EKS Configuration
node_group_desired_size = 6
node_group_max_size = 12
node_group_min_size = 3

# Database Configuration
db_instance_class = "db.r6g.xlarge"
db_allocated_storage = 100
db_max_allocated_storage = 1000

# Redis Configuration
redis_node_type = "cache.r6g.large"
redis_num_nodes = 3
```

### 2. Kubernetes Manifests

#### Service Deployment Template
```yaml
# k8s/base/user-service/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
  labels:
    app: user-service
    version: v1
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: user-service
  template:
    metadata:
      labels:
        app: user-service
        version: v1
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8080"
        prometheus.io/path: "/actuator/prometheus"
    spec:
      serviceAccountName: user-service
      securityContext:
        runAsNonRoot: true
        runAsUser: 10001
        fsGroup: 10001
      containers:
      - name: user-service
        image: loyalty-system/user-service:latest
        ports:
        - containerPort: 8080
          name: http
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: database-secret
              key: url
        - name: REDIS_URL
          valueFrom:
            secretKeyRef:
              name: redis-secret
              key: url
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 2
        securityContext:
          allowPrivilegeEscalation: false
          readOnlyRootFilesystem: true
          capabilities:
            drop:
            - ALL
        volumeMounts:
        - name: tmp
          mountPath: /tmp
        - name: logs
          mountPath: /app/logs
      volumes:
      - name: tmp
        emptyDir: {}
      - name: logs
        emptyDir: {}
```

#### Horizontal Pod Autoscaler
```yaml
# k8s/base/user-service/hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: user-service
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: user-service
  minReplicas: 3
  maxReplicas: 20
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
  - type: Pods
    pods:
      metric:
        name: http_requests_per_second
      target:
        type: AverageValue
        averageValue: "100"
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
      - type: Percent
        value: 10
        periodSeconds: 60
    scaleUp:
      stabilizationWindowSeconds: 60
      policies:
      - type: Percent
        value: 50
        periodSeconds: 60
```

---

## 🔄 CI/CD Pipeline

### 1. GitHub Actions Workflow

#### Main CI/CD Pipeline
```yaml
# .github/workflows/main.yml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

env:
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}

jobs:
  test:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: postgres
          POSTGRES_DB: loyalty_test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
      redis:
        image: redis:7
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven

    - name: Run unit tests
      run: mvn clean test

    - name: Run integration tests
      run: mvn clean integration-test
      env:
        DATABASE_URL: jdbc:postgresql://localhost:5432/loyalty_test
        REDIS_URL: redis://localhost:6379

    - name: Code coverage
      run: mvn jacoco:report

    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        file: ./target/site/jacoco/jacoco.xml

    - name: SonarQube analysis
      uses: sonarqube-quality-gate-action@master
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}

  security:
    runs-on: ubuntu-latest
    needs: test
    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Security scan with Snyk
      uses: snyk/actions/maven@master
      env:
        SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}

    - name: OWASP Dependency Check
      uses: dependency-check/Dependency-Check_Action@main
      with:
        project: 'loyalty-system'
        path: '.'
        format: 'HTML'

    - name: Upload security results
      uses: github/codeql-action/upload-sarif@v2
      if: always()
      with:
        sarif_file: reports/dependency-check-report.sarif

  build:
    runs-on: ubuntu-latest
    needs: [test, security]
    if: github.ref == 'refs/heads/main'
    permissions:
      contents: read
      packages: write
      id-token: write  # Required for keyless signing with GitHub OIDC
    outputs:
      image-tag: ${{ steps.meta.outputs.tags }}
      image-digest: ${{ steps.build.outputs.digest }}
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven

    - name: Build application
      run: mvn clean package -DskipTests

    - name: Log in to Container Registry
      uses: docker/login-action@v3
      with:
        registry: ${{ env.REGISTRY }}
        username: ${{ github.actor }}
        password: ${{ secrets.GITHUB_TOKEN }}

    - name: Extract metadata
      id: meta
      uses: docker/metadata-action@v5
      with:
        images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
        tags: |
          type=ref,event=branch
          type=sha,prefix={{branch}}-
          type=raw,value=latest,enable={{is_default_branch}}

    - name: Build and push Docker image
      id: build
      uses: docker/build-push-action@v5
      with:
        context: .
        file: ./Dockerfile
        push: true
        tags: ${{ steps.meta.outputs.tags }}
        labels: ${{ steps.meta.outputs.labels }}
        platforms: linux/amd64,linux/arm64
        cache-from: type=gha
        cache-to: type=gha,mode=max

    - name: Sign container image
      uses: sigstore/cosign-installer@v3
      with:
        cosign-release: 'v2.2.0'
    
    - name: Sign the published Docker image
      env:
        COSIGN_EXPERIMENTAL: 1
      run: |
        # Sign using GitHub's OIDC identity - no manual interaction required
        echo "${{ steps.meta.outputs.tags }}" | xargs -I {} cosign sign --yes {}@${{ steps.build.outputs.digest }}

  deploy-staging:
    runs-on: ubuntu-latest
    needs: build
    environment: staging
    if: github.ref == 'refs/heads/main'
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Configure AWS credentials
      uses: aws-actions/configure-aws-credentials@v4
      with:
        aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        aws-region: us-west-2

    - name: Update kubeconfig
      run: aws eks update-kubeconfig --name loyalty-system-staging

    - name: Deploy to staging
      run: |
        helm upgrade --install loyalty-system ./helm/loyalty-system \
          --namespace loyalty-system-staging \
          --create-namespace \
          --set image.tag=${{ needs.build.outputs.image-tag }} \
          --set environment=staging \
          --values ./helm/values/staging.yaml

    - name: Run smoke tests
      run: |
        kubectl wait --for=condition=ready pod -l app=user-service -n loyalty-system-staging --timeout=300s
        ./scripts/smoke-tests.sh staging

  deploy-production:
    runs-on: ubuntu-latest
    needs: [build, deploy-staging]
    environment: production
    if: github.ref == 'refs/heads/main'
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Configure AWS credentials
      uses: aws-actions/configure-aws-credentials@v4
      with:
        aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        aws-region: us-west-2

    - name: Update kubeconfig
      run: aws eks update-kubeconfig --name loyalty-system-production

    - name: Blue-Green deployment
      run: |
        # Deploy to green environment
        helm upgrade --install loyalty-system-green ./helm/loyalty-system \
          --namespace loyalty-system-production \
          --set image.tag=${{ needs.build.outputs.image-tag }} \
          --set environment=production \
          --set deployment.color=green \
          --values ./helm/values/production.yaml

        # Wait for green deployment to be ready
        kubectl wait --for=condition=ready pod -l app=user-service,color=green -n loyalty-system-production --timeout=600s

        # Run production readiness tests
        ./scripts/production-readiness-tests.sh

        # Switch traffic to green
        kubectl patch service user-service -n loyalty-system-production -p '{"spec":{"selector":{"color":"green"}}}'

        MONITOR_DURATION=${MONITOR_DURATION:-300}
        sleep $MONITOR_DURATION

        # Cleanup blue deployment
        helm uninstall loyalty-system-blue -n loyalty-system-production || true
```

### 2. Deployment Scripts

#### Blue-Green Deployment Script
```bash
#!/bin/bash
# scripts/blue-green-deploy.sh

set -euo pipefail

NAMESPACE=${1:-loyalty-system-production}
NEW_TAG=${2}
CURRENT_COLOR=$(kubectl get service user-service -n $NAMESPACE -o jsonpath='{.spec.selector.color}' || echo "blue")
NEW_COLOR=$([ "$CURRENT_COLOR" = "blue" ] && echo "green" || echo "blue")

echo "Current deployment color: $CURRENT_COLOR"
echo "Deploying new version to: $NEW_COLOR"

# Deploy new version
helm upgrade --install loyalty-system-$NEW_COLOR ./helm/loyalty-system \
  --namespace $NAMESPACE \
  --set image.tag=$NEW_TAG \
  --set deployment.color=$NEW_COLOR \
  --values ./helm/values/production.yaml \
  --wait \
  --timeout=10m

# Health check new deployment
echo "Running health checks on $NEW_COLOR deployment..."
kubectl wait --for=condition=ready pod -l color=$NEW_COLOR -n $NAMESPACE --timeout=300s

# Run readiness tests
./scripts/readiness-tests.sh $NAMESPACE $NEW_COLOR

if [ $? -eq 0 ]; then
    echo "Readiness tests passed. Switching traffic to $NEW_COLOR..."
    
    # Switch traffic
    kubectl patch service user-service -n $NAMESPACE -p "{\"spec\":{\"selector\":{\"color\":\"$NEW_COLOR\"}}}"
    kubectl patch service point-service -n $NAMESPACE -p "{\"spec\":{\"selector\":{\"color\":\"$NEW_COLOR\"}}}"
    kubectl patch service rewards-service -n $NAMESPACE -p "{\"spec\":{\"selector\":{\"color\":\"$NEW_COLOR\"}}}"
    kubectl patch service admin-service -n $NAMESPACE -p "{\"spec\":{\"selector\":{\"color\":\"$NEW_COLOR\"}}}"
    
    echo "Traffic switched to $NEW_COLOR. Monitoring for 5 minutes..."
    sleep 300
    
    # Final health check
    if ./scripts/health-check.sh $NAMESPACE; then
        echo "Deployment successful. Cleaning up $CURRENT_COLOR..."
        helm uninstall loyalty-system-$CURRENT_COLOR -n $NAMESPACE || true
    else
        echo "Health check failed. Rolling back..."
        kubectl patch service user-service -n $NAMESPACE -p "{\"spec\":{\"selector\":{\"color\":\"$CURRENT_COLOR\"}}}"
        exit 1
    fi
else
    echo "Readiness tests failed. Cleaning up $NEW_COLOR deployment..."
    helm uninstall loyalty-system-$NEW_COLOR -n $NAMESPACE
    exit 1
fi

echo "Blue-green deployment completed successfully!"
```

---

## 📊 Monitoring & Observability

### 1. Prometheus Configuration

#### Service Monitoring
```yaml
# monitoring/prometheus/prometheus.yml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "alert-rules.yml"

alertmanager_configs:
  - static_configs:
      - targets:
        - alertmanager:9093

scrape_configs:
  - job_name: 'kubernetes-pods'
    kubernetes_sd_configs:
      - role: pod
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_path]
        action: replace
        target_label: __metrics_path__
        regex: (.+)
      - source_labels: [__address__, __meta_kubernetes_pod_annotation_prometheus_io_port]
        action: replace
        regex: ([^:]+)(?::\d+)?;(\d+)
        replacement: $1:$2
        target_label: __address__

  - job_name: 'loyalty-services'
    static_configs:
      - targets:
        - user-service:8080
        - point-service:8080
        - rewards-service:8080
        - admin-service:8080
    metrics_path: /actuator/prometheus
    scrape_interval: 10s
```

#### Alert Rules
```yaml
# monitoring/prometheus/alert-rules.yml
groups:
  - name: loyalty-system-alerts
    rules:
      - alert: ServiceDown
        expr: up == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Service {{ $labels.instance }} is down"
          description: "{{ $labels.instance }} has been down for more than 1 minute"

      - alert: HighErrorRate
        expr: |
          (
            sum(rate(http_requests_total{status=~"5.."}[5m])) by (service)
            /
            sum(rate(http_requests_total[5m])) by (service)
          ) > 0.05
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "High error rate on {{ $labels.service }}"
          description: "Error rate is {{ $value | humanizePercentage }} for {{ $labels.service }}"

      - alert: HighLatency
        expr: histogram_quantile(0.95, sum(rate(http_request_duration_seconds_bucket[5m])) by (le, service)) > 2
        for: 3m
        labels:
          severity: warning
        annotations:
          summary: "High latency on {{ $labels.service }}"
          description: "95th percentile latency is {{ $value }}s for {{ $labels.service }}"

      - alert: DatabaseConnectionPoolExhausted
        expr: hikaricp_connections_active >= hikaricp_connections_max
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Database connection pool exhausted"
          description: "Database connection pool is full for {{ $labels.service }}"

      - alert: MemoryUsageHigh
        expr: (process_resident_memory_bytes / process_virtual_memory_max_bytes) > 0.8
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High memory usage on {{ $labels.instance }}"
          description: "Memory usage is {{ $value | humanizePercentage }} on {{ $labels.instance }}"
```

### 2. Grafana Dashboards

#### Application Performance Dashboard
```json
{
  "dashboard": {
    "id": null,
    "title": "Loyalty System - Application Performance",
    "tags": ["loyalty", "performance"],
    "timezone": "browser",
    "panels": [
      {
        "title": "Request Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "sum(rate(http_requests_total[5m])) by (service)",
            "legendFormat": "{{service}}"
          }
        ],
        "yAxes": [
          {
            "label": "Requests/sec",
            "min": 0
          }
        ]
      },
      {
        "title": "Error Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "sum(rate(http_requests_total{status=~\"5..\"}[5m])) by (service) / sum(rate(http_requests_total[5m])) by (service)",
            "legendFormat": "{{service}}"
          }
        ],
        "yAxes": [
          {
            "label": "Error Rate",
            "min": 0,
            "max": 1
          }
        ]
      },
      {
        "title": "Response Time (95th percentile)",
        "type": "graph",
        "targets": [
          {
            "expr": "histogram_quantile(0.95, sum(rate(http_request_duration_seconds_bucket[5m])) by (le, service))",
            "legendFormat": "{{service}}"
          }
        ],
        "yAxes": [
          {
            "label": "Seconds",
            "min": 0
          }
        ]
      }
    ]
  }
}
```

### 3. Logging Configuration

#### Centralized Logging with ELK Stack
```yaml
# logging/logstash/pipeline/loyalty-system.conf
input {
  beats {
    port => 5044
  }
}

filter {
  if [fields][service] {
    mutate {
      add_field => { "service" => "%{[fields][service]}" }
    }
  }

  # Parse JSON logs
  if [message] =~ /^{.*}$/ {
    json {
      source => "message"
    }
  }

  # Parse common log format
  grok {
    match => { 
      "message" => "%{TIMESTAMP_ISO8601:timestamp} %{LOGLEVEL:level} \[%{DATA:service},%{DATA:trace_id},%{DATA:span_id}\] %{DATA:logger} : %{GREEDYDATA:log_message}"
    }
  }

  # Add geo information for IP addresses
  if [client_ip] {
    geoip {
      source => "client_ip"
      target => "geoip"
    }
  }

  # Security log enrichment
  if [logger] == "SECURITY" {
    mutate {
      add_tag => [ "security" ]
    }
  }

  date {
    match => [ "timestamp", "ISO8601" ]
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "loyalty-system-%{+YYYY.MM.dd}"
  }

  # Send security logs to SIEM
  if "security" in [tags] {
    http {
      url => "${SIEM_ENDPOINT}"
      http_method => "post"
      headers => {
        "Authorization" => "Bearer ${SIEM_TOKEN}"
      }
    }
  }

  stdout {
    codec => rubydebug
  }
}
```

---

## 🔧 Environment Management

### 1. Environment Configuration

#### Helm Values per Environment
```yaml
# helm/values/production.yaml
replicaCount: 6

image:
  repository: ghcr.io/loyalty-system/user-service
  pullPolicy: IfNotPresent
  tag: "latest"

resources:
  limits:
    cpu: 1000m
    memory: 1Gi
  requests:
    cpu: 500m
    memory: 512Mi

autoscaling:
  enabled: true
  minReplicas: 6
  maxReplicas: 20
  targetCPUUtilizationPercentage: 70
  targetMemoryUtilizationPercentage: 80

database:
  host: loyalty-system-production.cluster-xxxxx.us-west-2.rds.amazonaws.com
  port: 5432
  name: loyalty_production
  ssl: true

redis:
  host: loyalty-system-production.xxxxx.cache.amazonaws.com
  port: 6379
  ssl: true

monitoring:
  enabled: true
  serviceMonitor: true

security:
  networkPolicy: true
  podSecurityPolicy: true

ingress:
  enabled: true
  className: "alb"
  annotations:
    kubernetes.io/ingress.class: alb
    alb.ingress.kubernetes.io/scheme: internet-facing
    alb.ingress.kubernetes.io/target-type: ip
    alb.ingress.kubernetes.io/ssl-redirect: '443'
    alb.ingress.kubernetes.io/certificate-arn: arn:aws:acm:us-west-2:123456789012:certificate/xxx
  hosts:
    - host: api.loyalty-system.com
      paths:
        - path: /
          pathType: Prefix
  tls: []
```

### 2. Configuration Management

#### ConfigMap and Secrets
```yaml
# k8s/base/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: loyalty-system-config
data:
  application.yml: |
    server:
      port: 8080
      servlet:
        context-path: /api/v1

    spring:
      application:
        name: loyalty-system
      
      jpa:
        hibernate:
          ddl-auto: validate
        show-sql: false
        properties:
          hibernate:
            format_sql: true
      
      redis:
        timeout: 2000ms
        lettuce:
          pool:
            max-active: 8
            max-wait: -1ms
            max-idle: 8
            min-idle: 0

    management:
      endpoints:
        web:
          exposure:
            include: health,info,metrics,prometheus
      endpoint:
        health:
          show-details: always
      metrics:
        export:
          prometheus:
            enabled: true

    logging:
      level:
        com.loyalty: INFO
        org.springframework.security: DEBUG
        org.springframework.web: INFO
      pattern:
        console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{traceId},%X{spanId}] %logger{36} - %msg%n"

---
apiVersion: v1
kind: Secret
metadata:
  name: loyalty-system-secrets
type: Opaque
data:
  database-url: <base64-encoded-url>
  database-username: <base64-encoded-username>
  database-password: <base64-encoded-password>
  redis-url: <base64-encoded-url>
  redis-password: <base64-encoded-password>
  jwt-secret: <base64-encoded-jwt-secret>
```

---

## 🚨 Disaster Recovery

### 1. Backup Strategy

#### Database Backup Automation
```bash
#!/bin/bash
# scripts/backup-database.sh

set -euo pipefail

# Configuration
DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-5432}
DB_NAME=${DB_NAME:-loyalty_production}
DB_USER=${DB_USER:-postgres}
BACKUP_RETENTION_DAYS=${BACKUP_RETENTION_DAYS:-7}
S3_BUCKET=${S3_BUCKET:-loyalty-system-backups}
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Create backup
echo "Starting database backup..."
pg_dump -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME \
  --verbose --format=custom --compress=9 \
  --file="/tmp/loyalty_backup_$TIMESTAMP.dump"

if [ $? -eq 0 ]; then
    echo "Database backup created successfully"
    
    # Upload to S3
    aws s3 cp "/tmp/loyalty_backup_$TIMESTAMP.dump" \
      "s3://$S3_BUCKET/database/loyalty_backup_$TIMESTAMP.dump"
    
    # Cleanup local file
    rm "/tmp/loyalty_backup_$TIMESTAMP.dump"
    
    # Cleanup old backups
    aws s3api list-objects-v2 --bucket $S3_BUCKET --prefix "database/" \
      --query 'Contents[?LastModified<=`'$(date -d "$BACKUP_RETENTION_DAYS days ago" --iso-8601)'`].Key' \
      --output text | xargs -n1 aws s3 rm s3://$S3_BUCKET/
    
    echo "Backup completed and uploaded to S3"
else
    echo "Database backup failed"
    exit 1
fi
```

### 2. Disaster Recovery Plan

#### Recovery Procedures
```bash
#!/bin/bash
# scripts/disaster-recovery.sh

set -euo pipefail

RECOVERY_TYPE=${1:-"partial"} # partial, full
BACKUP_DATE=${2:-"latest"}

echo "Starting disaster recovery process: $RECOVERY_TYPE"

case $RECOVERY_TYPE in
    "database")
        echo "Restoring database from backup..."
        ./scripts/restore-database.sh $BACKUP_DATE
        ;;
    
    "application")
        echo "Restoring application services..."
        kubectl apply -f k8s/disaster-recovery/
        ;;
    
    "full")
        echo "Full system recovery..."
        
        # 1. Restore infrastructure
        echo "Restoring infrastructure..."
        cd terraform/
        terraform apply -var-file="environments/production/terraform.tfvars" -auto-approve
        
        # 2. Restore database
        echo "Restoring database..."
        ./scripts/restore-database.sh $BACKUP_DATE
        
        # 3. Restore applications
        echo "Restoring applications..."
        kubectl apply -f k8s/production/
        
        # 4. Verify services
        echo "Verifying services..."
        ./scripts/health-check.sh production
        ;;
    
    *)
        echo "Unknown recovery type: $RECOVERY_TYPE"
        exit 1
        ;;
esac

echo "Disaster recovery completed: $RECOVERY_TYPE"
```

---

This comprehensive DevOps guide ensures reliable, secure, and scalable deployment of the loyalty system across all environments.