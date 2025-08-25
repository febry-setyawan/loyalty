# Production-optimized multi-stage build for User Service
FROM maven:3.9-eclipse-temurin-17 AS builder

# Set build argument for service name
ARG SERVICE_NAME=user-service

WORKDIR /app

# Copy common library first and build it
COPY shared/libs/common/ ./shared/libs/common/
RUN cd shared/libs/common && mvn clean install -DskipTests -B

# Copy service pom.xml for dependency caching
COPY services/${SERVICE_NAME}/pom.xml ./services/${SERVICE_NAME}/

# Set working directory to service
WORKDIR /app/services/${SERVICE_NAME}

# Download dependencies for better layer caching  
RUN mvn dependency:go-offline -B || true

# Copy source code and build
COPY services/${SERVICE_NAME}/src/ ./src/
RUN mvn clean package -DskipTests -B

# Production runtime with minimal base image  
FROM registry.access.redhat.com/ubi9/openjdk-17-runtime:1.18

# Set application directory
WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /app/services/*/target/*.jar app.jar

# Health check configuration
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM optimization for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0 -XX:+ExitOnOutOfMemoryError"

# Expose service port
EXPOSE 8080

# Use exec form for proper signal handling
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]