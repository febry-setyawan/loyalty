# Development & Deployment Tools

This directory contains utility scripts and tools for the loyalty system development and deployment.

## 📁 Directory Structure

### `scripts/` - Utility Scripts
- **setup-dev-environment.sh** - Sets up complete development environment with Docker
- **run-tests.sh** - Comprehensive test runner for all services
- **deploy.sh** - Deployment script for development/staging/production
- **backup-database.sh** - Database backup utility
- **restore-database.sh** - Database restore utility

### `testing/` - Testing Utilities
- **fixtures/** - Test data fixtures
- **mocks/** - Mock implementations
- **helpers/** - Test helper functions  
- **performance/** - Performance test tools

### `docs/` - Generated Documentation
- **api/** - API documentation
- **database/** - Database documentation
- **deployment/** - Deployment guides

## 🚀 Quick Start

### Setup Development Environment
```bash
./tools/scripts/setup-dev-environment.sh
```

### Run All Tests
```bash
./tools/scripts/run-tests.sh
```

### Deploy to Environment
```bash
# Development
./tools/scripts/deploy.sh development

# Staging  
./tools/scripts/deploy.sh staging

# Production
./tools/scripts/deploy.sh production
```

### Database Operations
```bash
# Backup database
./tools/scripts/backup-database.sh --compress

# List available backups
./tools/scripts/restore-database.sh --list-backups

# Restore from backup
./tools/scripts/restore-database.sh backup_file.sql
```

## 📋 Usage Guidelines

1. **All scripts should be run from the project root directory**
2. **Ensure .env file is configured before running setup**
3. **Use --help flag with any script to see detailed options**
4. **Test scripts in development before using in staging/production**

For detailed documentation, see the individual script files and [PROJECT-STRUCTURE.md](../../PROJECT-STRUCTURE.md).