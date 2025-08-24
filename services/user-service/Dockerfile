# Production-optimized multi-stage build for User Service
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml for dependency caching
COPY pom.xml ./

# Download dependencies for better layer caching  
RUN mvn dependency:go-offline -B || true

# Copy source code and build
COPY src/ ./src/
RUN mvn clean package -DskipTests -B

# Production runtime with minimal base image  
FROM registry.access.redhat.com/ubi9/openjdk-17-runtime:1.18

# Set application directory
WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Health check configuration
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM optimization for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0 -XX:+ExitOnOutOfMemoryError"

# Expose service port
EXPOSE 8080

# Use exec form for proper signal handling
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]