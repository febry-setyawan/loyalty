# Development Environment Setup - Loyalty System

## Quick Start

To get the complete loyalty system running locally, simply run:

```bash
docker-compose up
```

This will start all required services including:
- PostgreSQL database
- Redis cache
- Apache Kafka message queue
- 4 microservices (user, point, rewards, admin)
- Kong API Gateway
- Prometheus monitoring
- Grafana dashboards

## Prerequisites

- Docker Desktop or Docker Engine + Docker Compose
- Minimum 8GB RAM recommended
- Available ports: 3000, 5432, 6379, 8000-8001, 8080-8083, 9090, 9092

## Services Overview

| Service | Port | Health Check | Description |
|---------|------|--------------|-------------|
| user-service | 8080 | http://localhost:8080/health | User management and authentication |
| point-service | 8081 | http://localhost:8081/health | Point transactions and calculations |
| rewards-service | 8082 | http://localhost:8082/health | Rewards catalog and redemptions |
| admin-service | 8083 | http://localhost:8083/health | Admin dashboard and management |
| api-gateway | 8000 | http://localhost:8000/health | Kong API Gateway |
| postgres | 5432 | - | PostgreSQL database |
| redis | 6379 | - | Redis cache |
| kafka | 9092 | - | Apache Kafka message queue |
| prometheus | 9090 | http://localhost:9090 | Metrics collection |
| grafana | 3000 | http://localhost:3000 | Dashboards (admin:admin) |

## API Endpoints

All services are accessible through the API Gateway at `http://localhost:8000`:

- **User Service**: `http://localhost:8000/api/v1/users/*`
- **Point Service**: `http://localhost:8000/api/v1/points/*`
- **Rewards Service**: `http://localhost:8000/api/v1/rewards/*`
- **Admin Service**: `http://localhost:8000/api/v1/admin/*`

Direct service access (for development):
- **User Service**: `http://localhost:8080/api/v1/*`
- **Point Service**: `http://localhost:8081/api/v1/*`
- **Rewards Service**: `http://localhost:8082/api/v1/*`
- **Admin Service**: `http://localhost:8083/api/v1/*`

## Database Information

- **Host**: localhost:5432
- **Database**: loyalty
- **Username**: loyalty_user
- **Password**: loyalty_pass

The database is automatically initialized with:
- Schema for all services (users, points, rewards, admin, audit)
- Basic tables for service startup
- Sample data for health checks
- Proper indexes for development performance

## Development Workflow

### Starting the Environment

```bash
# Start all services
docker-compose up

# Start in detached mode
docker-compose up -d

# Start specific services
docker-compose up postgres redis user-service

# View logs
docker-compose logs -f user-service
```

### Health Checks

Check if all services are running:

```bash
# Check all health endpoints
curl http://localhost:8080/health  # User Service
curl http://localhost:8081/health  # Point Service
curl http://localhost:8082/health  # Rewards Service
curl http://localhost:8083/health  # Admin Service

# Check via API Gateway
curl http://localhost:8000/health
```

### Service Communication Testing

The services can communicate with each other through:
1. **HTTP calls** between services
2. **Kafka messaging** for asynchronous communication
3. **Shared database** for data consistency
4. **Redis cache** for performance optimization

### Development Features

- **Hot reload**: Code changes are automatically detected and services restart
- **Debug ports**: Each service exposes a debug port (5005) for remote debugging
- **Volume mounting**: Source code is mounted for live development
- **Health monitoring**: Comprehensive health checks for all components
- **Metrics collection**: Prometheus metrics available for all services
- **Log aggregation**: Centralized logging with correlation IDs

## Stopping the Environment

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (WARNING: This will delete all data)
docker-compose down -v

# Stop and remove images
docker-compose down --rmi all
```

## Troubleshooting

### Common Issues

1. **Port conflicts**: Ensure ports 3000, 5432, 6379, 8000-8083, 9090, 9092 are available
2. **Memory issues**: Increase Docker memory limit to at least 8GB
3. **Service startup order**: Services have proper dependencies, but may take 2-3 minutes for full startup
4. **Database connection issues**: Wait for PostgreSQL to fully initialize before services start

### Service Dependencies

Services start in this order:
1. PostgreSQL, Redis, Kafka infrastructure
2. Microservices (user, point, rewards, admin)
3. API Gateway and monitoring

### Useful Commands

```bash
# View service status
docker-compose ps

# Restart specific service
docker-compose restart user-service

# View service logs
docker-compose logs -f user-service

# Access database directly
docker-compose exec postgres psql -U loyalty_user -d loyalty

# Access Redis CLI
docker-compose exec redis redis-cli

# View Kafka topics
docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --list
```

## Next Steps

After the environment is running:

1. **Verify Health Checks**: All services should return 200 OK
2. **Test Database Connectivity**: Services should connect to PostgreSQL
3. **Verify Message Queue**: Services should be able to publish/consume Kafka messages
4. **Check Monitoring**: Prometheus should be collecting metrics
5. **Access Grafana**: View system dashboards at http://localhost:3000

## Architecture Compliance

This development environment follows all requirements from:
- ✅ **Story 1.1**: Development Environment Setup
- ✅ **project-structure.md**: Monorepo structure
- ✅ **development-standards.md**: Coding and deployment standards
- ✅ **pipeline-templates.md**: Docker configuration templates

The environment is ready for development of all loyalty system features according to the framework specifications.