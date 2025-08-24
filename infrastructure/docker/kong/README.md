# Kong API Gateway Configuration - Loyalty System

## 🎯 Overview

This Kong Gateway configuration implements **User Story 1.2: API Gateway Configuration** requirements:

- ✅ Kong API Gateway with Docker setup
- ✅ Rate limiting rules per API with different limits for users (100 req/min) and partners (1000 req/min)
- ✅ JWT authentication middleware for protected routes
- ✅ Load balancing configuration for production readiness
- ✅ CORS policies for frontend-backend communication
- ✅ Gateway monitoring dashboard integration with Prometheus

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Kong Gateway  │    │  Microservices  │
│   (React/Mobile)│    │   (Port 8000)   │    │                 │
│                 │───▶│                 │───▶│  User Service   │
│                 │    │  • JWT Auth     │    │  Point Service  │
│                 │    │  • Rate Limit   │    │  Rewards Service│
│                 │    │  • Load Balance │    │  Admin Service  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   Monitoring    │
                       │  (Prometheus)   │
                       └─────────────────┘
```

## 📡 API Routes Configuration

### Public Routes (No Authentication)
- `POST /api/v1/users/register` - User registration
- `POST /api/v1/users/verify` - Email/SMS verification
- `POST /api/v1/users/login` - User login
- `POST /api/v1/users/password-reset` - Password reset
- `GET /api/v1/rewards/catalog` - Browse rewards catalog
- `GET /api/v1/rewards/search` - Search rewards
- `GET /health/*` - Health check endpoints

### Protected Routes (JWT Required)
- `GET/PUT /api/v1/users/profile` - User profile management
- `POST /api/v1/users/logout` - User logout
- `POST/GET /api/v1/points/*` - Point operations
- `POST/PUT/DELETE /api/v1/rewards` - Rewards management (admin)
- `GET/POST/PUT/DELETE /api/v1/admin/*` - Admin operations
- `GET/POST/PUT/DELETE /api/v1/partners/*` - Partner API operations

## 🔐 Authentication Configuration

### JWT Authentication
```yaml
# Kong JWT Plugin Configuration
- name: jwt
  config:
    key_claim_name: iss
    claims_to_verify:
      - exp  # Expiration time
      - iss  # Issuer
      - role # User role (for admin routes)
      - partner_id # Partner ID (for partner routes)
```

### JWT Consumers
- **loyalty-user**: Standard user authentication
- **loyalty-partner**: Partner API authentication with higher rate limits

## 🚦 Rate Limiting Configuration

### User Types & Limits

| User Type | Endpoint Type | Per Minute | Per Hour | Notes |
|-----------|---------------|------------|----------|-------|
| **Public** | Registration/Auth | 10 | 50 | Strict for security |
| **Users** | Standard APIs | 100 | 1,000 | Per story requirements |
| **Users** | Catalog Browsing | 200 | 2,000 | Higher for read operations |
| **Partners** | Partner APIs | 1,000 | 10,000 | Per story requirements |
| **Admin** | Admin Operations | 200 | 2,000 | Moderate limit for admin |
| **Health** | Health Checks | 60 | 600 | Allow monitoring |

### Rate Limiting Policy
- **Development**: `local` policy (in-memory)
- **Production**: Should use `redis` policy for distributed rate limiting

## ⚖️ Load Balancing Configuration

### Health Check Configuration
```yaml
healthchecks:
  active:
    http_path: "/health"
    timeout: 5
    interval: 30
  passive:
    healthy.successes: 5
    unhealthy.http_failures: 3
```

### Upstream Targets
- **Algorithm**: Round-robin
- **Health Checks**: Active and passive monitoring
- **Failover**: Automatic unhealthy target removal

## 📊 Monitoring Integration

### Prometheus Metrics
Kong exports metrics to Prometheus via the `prometheus` plugin:
- Request count and latency per service
- Rate limiting statistics
- Upstream health status
- Consumer usage patterns

### Access via:
- **Kong Admin API**: http://localhost:8001
- **Kong Proxy**: http://localhost:8000
- **Prometheus Metrics**: http://localhost:8001/metrics

## 🔧 Development Usage

### Starting the Gateway
```bash
# Start all services including Kong Gateway
docker-compose up

# Gateway will be available at:
# - Proxy: http://localhost:8000
# - Admin: http://localhost:8001
```

### Testing API Routes
```bash
# Public endpoint (no auth)
curl http://localhost:8000/api/v1/rewards/catalog

# Protected endpoint (requires JWT)
curl -H "Authorization: Bearer <jwt-token>" \
     http://localhost:8000/api/v1/users/profile

# Health check
curl http://localhost:8000/health/user
```

### Generating JWT Tokens
For development, JWT tokens can be generated using the shared secret:
```bash
# Use the secret: loyalty-jwt-secret-key-change-in-production
# Issuer: loyalty-system
# Claims: exp, iss, role (for admin), partner_id (for partners)
```

## 🛡️ Security Features

### CORS Configuration
- **Origins**: `*` (development only)
- **Methods**: GET, POST, PUT, DELETE, PATCH, OPTIONS
- **Headers**: Accept, Content-Type, Authorization, X-Auth-Token
- **Credentials**: Enabled

### Security Headers
Kong automatically adds security headers and provides protection against:
- Cross-Origin Resource Sharing (CORS) attacks
- Rate limiting for DDoS protection
- JWT validation for authentication

## 🔄 Production Considerations

### Environment Variables
```bash
# Production secrets should be externalized
KONG_JWT_SECRET=<production-secret>
KONG_RATE_LIMIT_POLICY=redis
REDIS_HOST=<redis-cluster-endpoint>
```

### Rate Limiting Policy
Switch from `local` to `redis` policy for distributed rate limiting across Kong instances.

### SSL/TLS Configuration
Add SSL certificates and HTTPS configuration for production deployment.

---

**Status**: ✅ **COMPLETED** - Story 1.2 Requirements Met  
**Next**: Story 1.3 - Shared Libraries & Utilities  
**Owner**: Backend Developer / DevOps Engineer