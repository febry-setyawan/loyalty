const http = require('http');
const url = require('url');

const PORT = process.env.SERVER_PORT || 8080;

// Simple HTTP server
const server = http.createServer(async (req, res) => {
  const parsedUrl = url.parse(req.url, true);
  const path = parsedUrl.pathname;
  const method = req.method;

  // Set CORS headers
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');
  res.setHeader('Content-Type', 'application/json');

  if (method === 'OPTIONS') {
    res.writeHead(200);
    res.end();
    return;
  }

  // Health check endpoint
  if (path === '/health' && method === 'GET') {
    try {
      // Mock database connectivity test
      const dbConnected = process.env.DB_HOST ? true : false;
      
      const healthData = {
        status: 'UP',
        service: 'user-service',
        version: '1.0.0',
        database: dbConnected ? 'Connected' : 'Mock Mode',
        redis: process.env.REDIS_HOST ? 'Connected' : 'Mock Mode',
        kafka: process.env.KAFKA_SERVERS ? 'Connected' : 'Mock Mode',
        timestamp: Date.now(),
        environment: process.env.NODE_ENV || 'development',
        config: {
          dbHost: process.env.DB_HOST || 'not set',
          redisHost: process.env.REDIS_HOST || 'not set',
          kafkaServers: process.env.KAFKA_SERVERS || 'not set'
        }
      };
      
      res.writeHead(200);
      res.end(JSON.stringify(healthData, null, 2));
    } catch (error) {
      res.writeHead(503);
      res.end(JSON.stringify({
        status: 'DOWN',
        service: 'user-service',
        version: '1.0.0',
        error: error.message,
        timestamp: Date.now()
      }, null, 2));
    }
    return;
  }

  // Ready endpoint
  if (path === '/health/ready' && method === 'GET') {
    res.writeHead(200);
    res.end(JSON.stringify({
      status: 'READY',
      service: 'user-service'
    }, null, 2));
    return;
  }

  // Live endpoint
  if (path === '/health/live' && method === 'GET') {
    res.writeHead(200);
    res.end(JSON.stringify({
      status: 'ALIVE',
      service: 'user-service'
    }, null, 2));
    return;
  }

  // API endpoints (mock data)
  if (path === '/api/v1/users' && method === 'GET') {
    const mockUsers = [
      {
        id: '123e4567-e89b-12d3-a456-426614174000',
        email: 'health.check@loyalty.com',
        first_name: 'Health',
        last_name: 'Check',
        status: 'ACTIVE',
        created_at: '2024-01-01T00:00:00Z'
      },
      {
        id: '123e4567-e89b-12d3-a456-426614174001',
        email: 'demo@loyalty.com',
        first_name: 'Demo',
        last_name: 'User',
        status: 'ACTIVE',
        created_at: '2024-01-01T00:00:00Z'
      }
    ];

    res.writeHead(200);
    res.end(JSON.stringify({
      success: true,
      data: mockUsers,
      count: mockUsers.length,
      message: 'Mock data - service is running correctly'
    }, null, 2));
    return;
  }

  // Service info endpoint
  if (path === '/api/v1/info' && method === 'GET') {
    res.writeHead(200);
    res.end(JSON.stringify({
      service: 'user-service',
      version: '1.0.0',
      description: 'User Management Service for Loyalty System',
      endpoints: [
        'GET /health - Health check',
        'GET /health/ready - Readiness probe',
        'GET /health/live - Liveness probe',
        'GET /api/v1/users - List users (mock)',
        'GET /api/v1/info - Service information'
      ],
      environment: {
        port: PORT,
        nodeEnv: process.env.NODE_ENV || 'development',
        dbHost: process.env.DB_HOST || 'not configured',
        redisHost: process.env.REDIS_HOST || 'not configured'
      }
    }, null, 2));
    return;
  }

  // Default 404
  res.writeHead(404);
  res.end(JSON.stringify({
    success: false,
    error: 'Not Found',
    path: path,
    method: method,
    availableEndpoints: ['/health', '/health/ready', '/health/live', '/api/v1/users', '/api/v1/info']
  }, null, 2));
});

// Start server
server.listen(PORT, () => {
  console.log(`🚀 User Service running on port ${PORT}`);
  console.log(`📍 Health check: http://localhost:${PORT}/health`);
  console.log(`📍 Users API: http://localhost:${PORT}/api/v1/users`);
  console.log(`📍 Service Info: http://localhost:${PORT}/api/v1/info`);
  console.log(`🔧 Environment: ${process.env.NODE_ENV || 'development'}`);
  console.log(`🗄️  Database Host: ${process.env.DB_HOST || 'not configured'}`);
  console.log(`🔴 Redis Host: ${process.env.REDIS_HOST || 'not configured'}`);
  console.log(`📨 Kafka Servers: ${process.env.KAFKA_SERVERS || 'not configured'}`);
  console.log('✅ User Service is ready!');
});

// Graceful shutdown
process.on('SIGTERM', () => {
  console.log('SIGTERM received, shutting down gracefully');
  process.exit(0);
});

process.on('SIGINT', () => {
  console.log('SIGINT received, shutting down gracefully');
  process.exit(0);
});