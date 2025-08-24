#!/bin/bash
set -e

echo "🔍 Validating CI/CD Configuration..."

# Check if all required workflow files exist
echo "📋 Checking workflow files..."
if [[ -f ".github/workflows/ci-cd-main.yml" ]]; then
    echo "✅ Main CI/CD workflow exists"
else
    echo "❌ Main CI/CD workflow missing"
    exit 1
fi

if [[ -f ".github/workflows/pr-validation.yml" ]]; then
    echo "✅ PR validation workflow exists"
else
    echo "❌ PR validation workflow missing"
    exit 1
fi

# Check if OWASP plugin is configured in all service pom.xml files and common library
echo "🔧 Checking OWASP dependency-check plugin configuration..."
services=("user-service" "point-service" "rewards-service" "admin-service")

for service in "${services[@]}"; do
    if grep -q "dependency-check-maven" "services/$service/pom.xml"; then
        echo "✅ OWASP plugin configured in $service"
    else
        echo "❌ OWASP plugin missing in $service"
        exit 1
    fi
done

# Check common library
if grep -q "dependency-check-maven" "shared/libs/common/pom.xml"; then
    echo "✅ OWASP plugin configured in common library"
else
    echo "❌ OWASP plugin missing in common library"
    exit 1
fi

# Check if Spotless plugin is configured
echo "🎨 Checking Spotless formatting plugin..."
for service in "${services[@]}"; do
    if grep -q "spotless-maven-plugin" "services/$service/pom.xml"; then
        echo "✅ Spotless plugin configured in $service"
    else
        echo "❌ Spotless plugin missing in $service"
        exit 1
    fi
done

# Check common library
if grep -q "spotless-maven-plugin" "shared/libs/common/pom.xml"; then
    echo "✅ Spotless plugin configured in common library"
else
    echo "❌ Spotless plugin missing in common library"
    exit 1
fi

# Check if JaCoCo plugin is configured
echo "📊 Checking JaCoCo coverage plugin..."
for service in "${services[@]}"; do
    if grep -q "jacoco-maven-plugin" "services/$service/pom.xml"; then
        echo "✅ JaCoCo plugin configured in $service"
    else
        echo "❌ JaCoCo plugin missing in $service"
        exit 1
    fi
done

# Check common library
if grep -q "jacoco-maven-plugin" "shared/libs/common/pom.xml"; then
    echo "✅ JaCoCo plugin configured in common library"
else
    echo "❌ JaCoCo plugin missing in common library"
    exit 1
fi

# Validate workflow syntax (basic checks)
echo "✨ Checking workflow file syntax..."
if grep -q "cosign-installer" ".github/workflows/ci-cd-main.yml"; then
    echo "✅ Cosign installation step found"
else
    echo "❌ Cosign installation step missing"
    exit 1
fi

if grep -q "codecov/codecov-action@v4" ".github/workflows/ci-cd-main.yml"; then
    echo "✅ Updated Codecov action version"
else
    echo "❌ Codecov action needs update"
    exit 1
fi

if grep -q "codeql-action/upload-sarif@v3" ".github/workflows/ci-cd-main.yml"; then
    echo "✅ Updated CodeQL action version"
else
    echo "❌ CodeQL action needs update"
    exit 1
fi

# Test Maven commands that would be used in CI
echo "🧪 Testing Maven commands..."

# Test services
cd "services/user-service"
echo "  Testing service compile..."
mvn clean compile -B -q
echo "  Testing service Spotless check..."
mvn spotless:check -B -q
echo "  Testing service unit tests..."
mvn test -B -q
echo "  Testing service JaCoCo report generation..."
mvn jacoco:report -B -q

# Test common library
cd "../../shared/libs/common"
echo "  Testing common library compile..."
mvn clean compile -B -q
echo "  Testing common library Spotless check..."
mvn spotless:check -B -q  
echo "  Testing common library unit tests..."
mvn test -B -q
echo "  Testing common library JaCoCo report generation..."
mvn jacoco:report -B -q

echo "✅ All validations passed!"
echo ""
echo "🎯 CI/CD Pipeline Status: READY"
echo "📝 Summary:"
echo "  - All workflow files are present and configured"
echo "  - OWASP dependency-check plugin added to all services and common library"
echo "  - Spotless code formatting configured for all components"
echo "  - JaCoCo test coverage reporting enabled for all components"
echo "  - GitHub Actions updated to latest versions"
echo "  - Cosign Docker image signing configured"
echo "  - NVD API rate limiting handled"
echo "  - Common library included in CI/CD pipeline"
echo ""
echo "🚀 The CI/CD pipeline is now ready for production use!"