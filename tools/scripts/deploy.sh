#!/bin/bash

# Loyalty System - Deployment Script
# This script handles deployment to different environments

set -e

# Configuration
REGISTRY=${DOCKER_REGISTRY:-"loyalty-system-registry"}
NAMESPACE=${KUBERNETES_NAMESPACE:-"loyalty-system"}
ENVIRONMENT=${DEPLOY_ENV:-"development"}

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to show usage
show_usage() {
    echo "Usage: $0 [ENVIRONMENT] [OPTIONS]"
    echo ""
    echo "Environments:"
    echo "  development  - Deploy to development environment (default)"
    echo "  staging      - Deploy to staging environment"
    echo "  production   - Deploy to production environment"
    echo ""
    echo "Options:"
    echo "  --service SERVICE_NAME  - Deploy specific service only"
    echo "  --no-build             - Skip building Docker images"
    echo "  --dry-run              - Show what would be deployed without deploying"
    echo "  --rollback VERSION     - Rollback to specific version"
    echo ""
    echo "Examples:"
    echo "  $0 development                    # Deploy all services to development"
    echo "  $0 staging --service user-service # Deploy only user-service to staging"
    echo "  $0 production --dry-run          # Show production deployment plan"
}

# Function to build Docker images
build_images() {
    local service=$1
    
    echo -e "${BLUE}🏗️ Building Docker image for $service...${NC}"
    
    if [ -d "./services/$service" ]; then
        cd "./services/$service"
        
        # Build Docker image
        docker build -t "$REGISTRY/$service:$ENVIRONMENT-$GITHUB_SHA" .
        docker build -t "$REGISTRY/$service:$ENVIRONMENT-latest" .
        
        # Push to registry if not local development
        if [ "$ENVIRONMENT" != "development" ]; then
            echo "📤 Pushing image to registry..."
            docker push "$REGISTRY/$service:$ENVIRONMENT-$GITHUB_SHA"
            docker push "$REGISTRY/$service:$ENVIRONMENT-latest"
        fi
        
        cd - > /dev/null
    else
        echo -e "${RED}❌ Service directory not found: ./services/$service${NC}"
        exit 1
    fi
}

# Function to deploy to Kubernetes
deploy_to_k8s() {
    local service=$1
    
    echo -e "${BLUE}🚀 Deploying $service to $ENVIRONMENT...${NC}"
    
    # Update deployment with new image
    if [ "$ENVIRONMENT" == "production" ]; then
        # Blue-green deployment for production
        kubectl set image deployment/${service}-green ${service}=$REGISTRY/$service:$ENVIRONMENT-$GITHUB_SHA -n $NAMESPACE
        kubectl rollout status deployment/${service}-green --timeout=600s -n $NAMESPACE
        
        # Switch traffic to green deployment
        kubectl patch service $service -p '{"spec":{"selector":{"version":"green"}}}' -n $NAMESPACE
        
        # Scale down blue deployment
        kubectl scale deployment ${service}-blue --replicas=0 -n $NAMESPACE
    else
        # Rolling deployment for development/staging
        kubectl set image deployment/$service $service=$REGISTRY/$service:$ENVIRONMENT-$GITHUB_SHA -n $NAMESPACE
        kubectl rollout status deployment/$service --timeout=600s -n $NAMESPACE
    fi
}

# Function to deploy with Docker Compose (for development)
deploy_with_compose() {
    echo -e "${BLUE}🐳 Deploying with Docker Compose to $ENVIRONMENT...${NC}"
    
    if [ -f "docker-compose.${ENVIRONMENT}.yml" ]; then
        docker-compose -f docker-compose.${ENVIRONMENT}.yml up -d --build
    else
        docker-compose up -d --build
    fi
    
    # Wait for services to be ready
    echo "⏳ Waiting for services to be ready..."
    sleep 30
    
    # Health check
    if curl -f http://localhost:8080/health > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Services are healthy${NC}"
    else
        echo -e "${RED}❌ Health check failed${NC}"
        exit 1
    fi
}

# Function to rollback deployment
rollback_deployment() {
    local version=$1
    local service=$2
    
    echo -e "${YELLOW}🔄 Rolling back $service to version $version...${NC}"
    
    if [ "$ENVIRONMENT" == "development" ]; then
        echo "❌ Rollback not supported for development environment"
        exit 1
    fi
    
    kubectl set image deployment/$service $service=$REGISTRY/$service:$ENVIRONMENT-$version -n $NAMESPACE
    kubectl rollout status deployment/$service --timeout=600s -n $NAMESPACE
}

# Parse command line arguments
SERVICES=("user-service" "point-service" "rewards-service" "admin-service")
SPECIFIC_SERVICE=""
BUILD_IMAGES=true
DRY_RUN=false
ROLLBACK_VERSION=""

if [ $# -gt 0 ]; then
    ENVIRONMENT=$1
    shift
fi

while [[ $# -gt 0 ]]; do
    case $1 in
        --service)
            SPECIFIC_SERVICE="$2"
            shift 2
            ;;
        --no-build)
            BUILD_IMAGES=false
            shift
            ;;
        --dry-run)
            DRY_RUN=true
            shift
            ;;
        --rollback)
            ROLLBACK_VERSION="$2"
            shift 2
            ;;
        --help)
            show_usage
            exit 0
            ;;
        *)
            echo -e "${RED}❌ Unknown option: $1${NC}"
            show_usage
            exit 1
            ;;
    esac
done

# Validate environment
case $ENVIRONMENT in
    development|staging|production)
        echo -e "${GREEN}🎯 Deploying to $ENVIRONMENT environment${NC}"
        ;;
    *)
        echo -e "${RED}❌ Invalid environment: $ENVIRONMENT${NC}"
        show_usage
        exit 1
        ;;
esac

# Handle rollback
if [ -n "$ROLLBACK_VERSION" ]; then
    if [ -n "$SPECIFIC_SERVICE" ]; then
        rollback_deployment "$ROLLBACK_VERSION" "$SPECIFIC_SERVICE"
    else
        for service in "${SERVICES[@]}"; do
            rollback_deployment "$ROLLBACK_VERSION" "$service"
        done
    fi
    exit 0
fi

# Main deployment logic
if [ "$DRY_RUN" = true ]; then
    echo -e "${YELLOW}📋 Dry run - showing deployment plan:${NC}"
    echo "Environment: $ENVIRONMENT"
    echo "Registry: $REGISTRY"
    echo "Namespace: $NAMESPACE"
    if [ -n "$SPECIFIC_SERVICE" ]; then
        echo "Service: $SPECIFIC_SERVICE"
    else
        echo "Services: ${SERVICES[*]}"
    fi
    exit 0
fi

# Determine services to deploy
if [ -n "$SPECIFIC_SERVICE" ]; then
    DEPLOY_SERVICES=("$SPECIFIC_SERVICE")
else
    DEPLOY_SERVICES=("${SERVICES[@]}")
fi

# Build images if requested
if [ "$BUILD_IMAGES" = true ]; then
    for service in "${DEPLOY_SERVICES[@]}"; do
        build_images "$service"
    done
fi

# Deploy based on environment
if [ "$ENVIRONMENT" == "development" ]; then
    deploy_with_compose
else
    # Deploy to Kubernetes for staging/production
    for service in "${DEPLOY_SERVICES[@]}"; do
        deploy_to_k8s "$service"
    done
fi

echo ""
echo -e "${GREEN}🎉 Deployment to $ENVIRONMENT completed successfully!${NC}"
echo ""
echo "📋 Post-deployment steps:"
echo "1. Verify service health checks"
echo "2. Run smoke tests"
echo "3. Monitor application logs"
echo "4. Update deployment documentation"
echo ""