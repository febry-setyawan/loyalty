#!/bin/bash

# Loyalty System - Database Backup Script
# This script creates backups of the loyalty system database

set -e

# Configuration
BACKUP_DIR="${BACKUP_DIR:-./backups}"
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-loyalty_db}"
DB_USER="${DB_USER:-loyalty_user}"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
RETENTION_DAYS=${RETENTION_DAYS:-30}

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "🗄️ Loyalty System Database Backup"
echo "================================="

# Create backup directory
mkdir -p "$BACKUP_DIR"

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  --full          - Full database backup (default)"
    echo "  --schema-only   - Schema only backup"
    echo "  --data-only     - Data only backup"
    echo "  --compress      - Compress backup with gzip"
    echo "  --remote        - Backup remote database (requires SSH tunnel)"
    echo "  --help          - Show this help message"
    echo ""
    echo "Environment Variables:"
    echo "  DB_HOST         - Database host (default: localhost)"
    echo "  DB_PORT         - Database port (default: 5432)"
    echo "  DB_NAME         - Database name (default: loyalty_db)"
    echo "  DB_USER         - Database user (default: loyalty_user)"
    echo "  BACKUP_DIR      - Backup directory (default: ./backups)"
    echo "  RETENTION_DAYS  - Backup retention in days (default: 30)"
    echo ""
}

# Function to check database connectivity
check_db_connection() {
    echo "🔍 Checking database connectivity..."
    
    if pg_isready -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Database is accessible${NC}"
    else
        echo -e "${RED}❌ Cannot connect to database${NC}"
        echo "Please check your database connection parameters:"
        echo "  Host: $DB_HOST"
        echo "  Port: $DB_PORT"
        echo "  User: $DB_USER"
        echo "  Database: $DB_NAME"
        exit 1
    fi
}

# Function to create backup
create_backup() {
    local backup_type=$1
    local compress=$2
    
    local backup_file="loyalty_${backup_type}_${TIMESTAMP}.sql"
    local backup_path="$BACKUP_DIR/$backup_file"
    
    echo -e "${YELLOW}📦 Creating $backup_type backup...${NC}"
    
    case $backup_type in
        "full")
            pg_dump -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" \
                --verbose --clean --if-exists --create \
                > "$backup_path"
            ;;
        "schema")
            pg_dump -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" \
                --schema-only --verbose --clean --if-exists --create \
                > "$backup_path"
            ;;
        "data")
            pg_dump -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" \
                --data-only --verbose --column-inserts \
                > "$backup_path"
            ;;
    esac
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Backup created successfully: $backup_path${NC}"
        
        # Compress if requested
        if [ "$compress" = true ]; then
            echo "🗜️ Compressing backup..."
            gzip "$backup_path"
            backup_path="${backup_path}.gz"
            echo -e "${GREEN}✅ Backup compressed: $backup_path${NC}"
        fi
        
        # Show backup size
        backup_size=$(du -h "$backup_path" | cut -f1)
        echo "📊 Backup size: $backup_size"
        
    else
        echo -e "${RED}❌ Backup failed${NC}"
        exit 1
    fi
}

# Function to cleanup old backups
cleanup_old_backups() {
    echo "🧹 Cleaning up backups older than $RETENTION_DAYS days..."
    
    find "$BACKUP_DIR" -name "loyalty_*.sql*" -type f -mtime +$RETENTION_DAYS -delete
    
    remaining_backups=$(find "$BACKUP_DIR" -name "loyalty_*.sql*" -type f | wc -l)
    echo -e "${GREEN}✅ Cleanup completed. $remaining_backups backups remaining.${NC}"
}

# Function to verify backup integrity
verify_backup() {
    local backup_file=$1
    
    echo "🔍 Verifying backup integrity..."
    
    if [[ "$backup_file" == *.gz ]]; then
        if gzip -t "$backup_file"; then
            echo -e "${GREEN}✅ Compressed backup is valid${NC}"
        else
            echo -e "${RED}❌ Compressed backup is corrupted${NC}"
            exit 1
        fi
    else
        if head -1 "$backup_file" | grep -q "PostgreSQL database dump"; then
            echo -e "${GREEN}✅ Backup file appears valid${NC}"
        else
            echo -e "${RED}❌ Backup file may be corrupted${NC}"
            exit 1
        fi
    fi
}

# Main execution
main() {
    # Parse command line arguments
    BACKUP_TYPE="full"
    COMPRESS=false
    REMOTE=false
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            --full)
                BACKUP_TYPE="full"
                shift
                ;;
            --schema-only)
                BACKUP_TYPE="schema"
                shift
                ;;
            --data-only)
                BACKUP_TYPE="data"
                shift
                ;;
            --compress)
                COMPRESS=true
                shift
                ;;
            --remote)
                REMOTE=true
                shift
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
    
    echo "🎯 Backup Configuration:"
    echo "  Type: $BACKUP_TYPE"
    echo "  Compress: $COMPRESS"
    echo "  Database: $DB_HOST:$DB_PORT/$DB_NAME"
    echo "  User: $DB_USER"
    echo "  Backup Directory: $BACKUP_DIR"
    echo ""
    
    # Check dependencies
    if ! command -v pg_dump &> /dev/null; then
        echo -e "${RED}❌ pg_dump is not installed${NC}"
        exit 1
    fi
    
    if ! command -v pg_isready &> /dev/null; then
        echo -e "${RED}❌ pg_isready is not installed${NC}"
        exit 1
    fi
    
    # Execute backup process
    check_db_connection
    create_backup "$BACKUP_TYPE" "$COMPRESS"
    
    # Find the created backup file
    backup_file=$(find "$BACKUP_DIR" -name "loyalty_${BACKUP_TYPE}_${TIMESTAMP}.sql*" -type f | head -1)
    
    if [ -n "$backup_file" ]; then
        verify_backup "$backup_file"
        cleanup_old_backups
        
        echo ""
        echo -e "${GREEN}🎉 Database backup completed successfully!${NC}"
        echo "📁 Backup location: $backup_file"
        echo ""
    else
        echo -e "${RED}❌ Backup file not found${NC}"
        exit 1
    fi
}

# Run main function with all arguments
main "$@"