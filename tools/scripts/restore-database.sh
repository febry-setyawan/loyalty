#!/bin/bash

# Loyalty System - Database Restore Script
# This script restores the loyalty system database from backup

set -e

# Configuration
BACKUP_DIR="${BACKUP_DIR:-./backups}"
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-loyalty_db}"
DB_USER="${DB_USER:-loyalty_user}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "🔄 Loyalty System Database Restore"
echo "=================================="

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS] BACKUP_FILE"
    echo ""
    echo "Options:"
    echo "  --force         - Force restore without confirmation"
    echo "  --no-drop       - Don't drop existing database"
    echo "  --data-only     - Restore data only (preserve schema)"
    echo "  --list-backups  - List available backup files"
    echo "  --help          - Show this help message"
    echo ""
    echo "Environment Variables:"
    echo "  DB_HOST         - Database host (default: localhost)"
    echo "  DB_PORT         - Database port (default: 5432)"
    echo "  DB_NAME         - Database name (default: loyalty_db)"
    echo "  DB_USER         - Database user (default: loyalty_user)"
    echo "  BACKUP_DIR      - Backup directory (default: ./backups)"
    echo ""
    echo "Examples:"
    echo "  $0 loyalty_full_20241215_143022.sql                # Restore from specific backup"
    echo "  $0 --list-backups                                  # List available backups"
    echo "  $0 --data-only loyalty_data_20241215_143022.sql    # Restore data only"
}

# Function to list available backups
list_backups() {
    echo "📁 Available backup files in $BACKUP_DIR:"
    echo ""
    
    if [ -d "$BACKUP_DIR" ]; then
        backups=($(find "$BACKUP_DIR" -name "loyalty_*.sql*" -type f -printf "%f\n" | sort -r))
        
        if [ ${#backups[@]} -eq 0 ]; then
            echo "  No backup files found."
        else
            for i in "${!backups[@]}"; do
                backup_file="$BACKUP_DIR/${backups[$i]}"
                size=$(du -h "$backup_file" | cut -f1)
                date=$(stat -c %y "$backup_file" | cut -d' ' -f1,2 | cut -d'.' -f1)
                echo "  $((i+1)). ${backups[$i]} (${size}, $date)"
            done
        fi
    else
        echo "  Backup directory not found: $BACKUP_DIR"
    fi
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

# Function to verify backup file
verify_backup_file() {
    local backup_file=$1
    
    echo "🔍 Verifying backup file..."
    
    if [ ! -f "$backup_file" ]; then
        echo -e "${RED}❌ Backup file not found: $backup_file${NC}"
        exit 1
    fi
    
    # Check if file is compressed
    if [[ "$backup_file" == *.gz ]]; then
        if gzip -t "$backup_file"; then
            echo -e "${GREEN}✅ Compressed backup file is valid${NC}"
        else
            echo -e "${RED}❌ Compressed backup file is corrupted${NC}"
            exit 1
        fi
    else
        if head -1 "$backup_file" | grep -q "PostgreSQL database dump"; then
            echo -e "${GREEN}✅ Backup file appears valid${NC}"
        else
            echo -e "${RED}❌ Invalid backup file format${NC}"
            exit 1
        fi
    fi
    
    # Show backup file info
    size=$(du -h "$backup_file" | cut -f1)
    date=$(stat -c %y "$backup_file" | cut -d' ' -f1,2 | cut -d'.' -f1)
    echo "📊 Backup file: $backup_file ($size, created: $date)"
}

# Function to confirm restore operation
confirm_restore() {
    local backup_file=$1
    local force=$2
    
    if [ "$force" = false ]; then
        echo ""
        echo -e "${YELLOW}⚠️ WARNING: This operation will replace the current database!${NC}"
        echo "Database: $DB_HOST:$DB_PORT/$DB_NAME"
        echo "Backup: $(basename "$backup_file")"
        echo ""
        read -p "Are you sure you want to continue? (yes/no): " confirm
        
        if [ "$confirm" != "yes" ]; then
            echo "❌ Restore operation cancelled"
            exit 0
        fi
    fi
}

# Function to restore database
restore_database() {
    local backup_file=$1
    local drop_db=$2
    local data_only=$3
    
    echo -e "${BLUE}🔄 Starting database restore...${NC}"
    
    # Stop application services if running locally
    if [ -f "docker-compose.yml" ]; then
        echo "🛑 Stopping application services..."
        docker-compose stop user-service point-service rewards-service admin-service || true
    fi
    
    # Create restore command
    restore_cmd="psql -h $DB_HOST -p $DB_PORT -U $DB_USER"
    
    if [ "$data_only" = true ]; then
        restore_cmd="$restore_cmd -d $DB_NAME"
    elif [ "$drop_db" = true ]; then
        # Drop and recreate database
        echo "🗑️ Dropping existing database..."
        dropdb -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" --if-exists "$DB_NAME"
        echo "🏗️ Creating new database..."
        createdb -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" "$DB_NAME"
        restore_cmd="$restore_cmd -d $DB_NAME"
    else
        restore_cmd="$restore_cmd -d $DB_NAME"
    fi
    
    # Execute restore
    if [[ "$backup_file" == *.gz ]]; then
        echo "📥 Restoring from compressed backup..."
        gunzip -c "$backup_file" | $restore_cmd
    else
        echo "📥 Restoring from backup..."
        $restore_cmd < "$backup_file"
    fi
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Database restored successfully${NC}"
    else
        echo -e "${RED}❌ Database restore failed${NC}"
        exit 1
    fi
}

# Function to verify restore
verify_restore() {
    echo "🔍 Verifying database restore..."
    
    # Check if main tables exist
    tables=("users.users" "points.point_transactions" "rewards.rewards" "admin.admin_users")
    
    for table in "${tables[@]}"; do
        if psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" \
               -c "SELECT COUNT(*) FROM $table;" > /dev/null 2>&1; then
            echo -e "${GREEN}✅ Table $table exists and accessible${NC}"
        else
            echo -e "${RED}❌ Table $table not found or not accessible${NC}"
            exit 1
        fi
    done
}

# Main execution
main() {
    # Parse command line arguments
    BACKUP_FILE=""
    FORCE=false
    DROP_DB=true
    DATA_ONLY=false
    LIST_BACKUPS=false
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            --force)
                FORCE=true
                shift
                ;;
            --no-drop)
                DROP_DB=false
                shift
                ;;
            --data-only)
                DATA_ONLY=true
                DROP_DB=false
                shift
                ;;
            --list-backups)
                LIST_BACKUPS=true
                shift
                ;;
            --help)
                show_usage
                exit 0
                ;;
            --*)
                echo -e "${RED}❌ Unknown option: $1${NC}"
                show_usage
                exit 1
                ;;
            *)
                BACKUP_FILE="$1"
                shift
                ;;
        esac
    done
    
    # Handle list backups
    if [ "$LIST_BACKUPS" = true ]; then
        list_backups
        exit 0
    fi
    
    # Validate backup file parameter
    if [ -z "$BACKUP_FILE" ]; then
        echo -e "${RED}❌ Backup file parameter is required${NC}"
        echo ""
        show_usage
        exit 1
    fi
    
    # If relative path, assume it's in backup directory
    if [[ "$BACKUP_FILE" != /* ]]; then
        BACKUP_FILE="$BACKUP_DIR/$BACKUP_FILE"
    fi
    
    # Check dependencies
    if ! command -v pg_dump &> /dev/null; then
        echo -e "${RED}❌ PostgreSQL client tools are not installed${NC}"
        exit 1
    fi
    
    # Execute restore process
    check_db_connection
    verify_backup_file "$BACKUP_FILE"
    confirm_restore "$BACKUP_FILE" "$FORCE"
    restore_database "$BACKUP_FILE" "$DROP_DB" "$DATA_ONLY"
    verify_restore
    
    echo ""
    echo -e "${GREEN}🎉 Database restore completed successfully!${NC}"
    echo ""
    echo "📋 Next steps:"
    echo "1. Restart application services if needed"
    echo "2. Verify application functionality"
    echo "3. Check application logs for any issues"
    echo ""
}

# Run main function with all arguments
main "$@"