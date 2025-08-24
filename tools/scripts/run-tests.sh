#!/bin/bash

# Loyalty System - Test Runner Script
# This script runs all tests across the loyalty system services

set -e

echo "🧪 Running Loyalty System Tests..."

# Test configuration
COVERAGE_THRESHOLD=80
TEST_RESULTS_DIR="./test-results"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Create test results directory
mkdir -p $TEST_RESULTS_DIR

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to run service tests
run_service_tests() {
    local service=$1
    local service_path="./services/$service"
    
    echo -e "${YELLOW}🧪 Testing $service...${NC}"
    
    if [ -d "$service_path" ]; then
        cd "$service_path"
        
        # Check if it's a Java/Maven project
        if [ -f "pom.xml" ]; then
            echo "📋 Running Maven tests for $service..."
            mvn clean test jacoco:report \
                -Dmaven.test.failure.ignore=false \
                -Djacoco.minimum.coverage=$COVERAGE_THRESHOLD
            
            # Copy test results
            if [ -d "target/surefire-reports" ]; then
                cp -r target/surefire-reports "$TEST_RESULTS_DIR/${service}_junit_${TIMESTAMP}/"
            fi
            
            if [ -d "target/site/jacoco" ]; then
                cp -r target/site/jacoco "$TEST_RESULTS_DIR/${service}_coverage_${TIMESTAMP}/"
            fi
            
        # Check if it's a Node.js project
        elif [ -f "package.json" ]; then
            echo "📋 Running npm tests for $service..."
            npm ci
            npm run test -- --coverage --coverageThreshold.global.lines=$COVERAGE_THRESHOLD
            
            # Copy test results
            if [ -d "coverage" ]; then
                cp -r coverage "$TEST_RESULTS_DIR/${service}_coverage_${TIMESTAMP}/"
            fi
        else
            echo "⚠️ No recognized test framework for $service"
        fi
        
        cd - > /dev/null
    else
        echo "⚠️ Service directory not found: $service_path"
    fi
}

# Function to run integration tests
run_integration_tests() {
    echo -e "${YELLOW}🔗 Running Integration Tests...${NC}"
    
    if [ -d "./tests/integration" ]; then
        cd ./tests/integration
        
        # Ensure test environment is ready
        echo "🔍 Checking service availability..."
        if ! curl -f http://localhost:8080/health > /dev/null 2>&1; then
            echo "❌ Services not available. Please run './tools/scripts/setup-dev-environment.sh' first."
            exit 1
        fi
        
        # Run integration tests
        if [ -f "package.json" ]; then
            npm ci
            npm run test
        elif [ -f "pom.xml" ]; then
            mvn clean test
        fi
        
        cd - > /dev/null
    else
        echo "⚠️ Integration tests directory not found"
    fi
}

# Function to run e2e tests
run_e2e_tests() {
    echo -e "${YELLOW}🎯 Running End-to-End Tests...${NC}"
    
    if [ -d "./tests/e2e" ]; then
        cd ./tests/e2e
        
        # Run e2e tests
        if [ -f "package.json" ]; then
            npm ci
            npm run test
        fi
        
        cd - > /dev/null
    else
        echo "⚠️ E2E tests directory not found"
    fi
}

# Main test execution
main() {
    echo "📊 Test Results will be saved to: $TEST_RESULTS_DIR"
    
    # Parse command line arguments
    RUN_UNIT=true
    RUN_INTEGRATION=true
    RUN_E2E=true
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            --unit-only)
                RUN_INTEGRATION=false
                RUN_E2E=false
                shift
                ;;
            --integration-only)
                RUN_UNIT=false
                RUN_E2E=false
                shift
                ;;
            --e2e-only)
                RUN_UNIT=false
                RUN_INTEGRATION=false
                shift
                ;;
            --service)
                SPECIFIC_SERVICE="$2"
                shift 2
                ;;
            *)
                echo "Unknown option $1"
                echo "Usage: $0 [--unit-only|--integration-only|--e2e-only] [--service SERVICE_NAME]"
                exit 1
                ;;
        esac
    done
    
    # Run tests based on configuration
    if [ "$RUN_UNIT" = true ]; then
        if [ -n "$SPECIFIC_SERVICE" ]; then
            run_service_tests "$SPECIFIC_SERVICE"
        else
            # Run tests for all services
            services=("user-service" "point-service" "rewards-service" "admin-service")
            for service in "${services[@]}"; do
                run_service_tests "$service"
            done
        fi
    fi
    
    if [ "$RUN_INTEGRATION" = true ]; then
        run_integration_tests
    fi
    
    if [ "$RUN_E2E" = true ]; then
        run_e2e_tests
    fi
    
    echo ""
    echo -e "${GREEN}🎉 All tests completed!${NC}"
    echo "📊 Test results available in: $TEST_RESULTS_DIR"
    echo ""
}

# Run main function with all arguments
main "$@"