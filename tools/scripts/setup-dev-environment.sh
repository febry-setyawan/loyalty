#!/bin/bash

# Loyalty System - Development Environment Setup Script
# This script sets up the complete development environment for the loyalty system

set -e

echo "🚀 Setting up Loyalty System Development Environment..."

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Create .env file if it doesn't exist
if [ ! -f ".env" ]; then
    echo "📝 Creating .env file from template..."
    cp .env.example .env
    echo "✅ .env file created. Please review and update the values as needed."
fi

# Stop any existing containers
echo "🛑 Stopping existing containers..."
docker-compose down

# Pull latest images
echo "📥 Pulling latest Docker images..."
docker-compose pull

# Build and start services
echo "🏗️ Building and starting services..."
docker-compose up -d --build

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 30

# Check service health
echo "🔍 Checking service health..."
services=("user-service" "point-service" "rewards-service" "admin-service")

for service in "${services[@]}"; do
    if docker-compose ps $service | grep -q "Up"; then
        echo "✅ $service is running"
    else
        echo "❌ $service failed to start"
        docker-compose logs $service
    fi
done

# Check database connectivity
echo "🗄️ Testing database connectivity..."
if docker-compose exec -T postgres psql -U loyalty_user -d loyalty_db -c "SELECT 1;" > /dev/null 2>&1; then
    echo "✅ Database connection successful"
else
    echo "❌ Database connection failed"
    exit 1
fi

# Check Redis connectivity
echo "🔴 Testing Redis connectivity..."
if docker-compose exec -T redis redis-cli ping | grep -q "PONG"; then
    echo "✅ Redis connection successful"
else
    echo "❌ Redis connection failed"
    exit 1
fi

echo ""
echo "🎉 Development environment setup completed successfully!"
echo ""
echo "📋 Next steps:"
echo "1. Review and update .env file if needed"
echo "2. Access services at:"
echo "   - API Gateway: http://localhost:8080"
echo "   - Admin Dashboard: http://localhost:3000"
echo "   - User Portal: http://localhost:3001"
echo "3. Check service logs: docker-compose logs -f [service-name]"
echo "4. Run tests: ./tools/scripts/run-tests.sh"
echo ""