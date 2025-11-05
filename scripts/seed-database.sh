#!/bin/bash

# Database Seeding Script
# This script seeds the database with sample data for development and testing

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Get script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_DIR="$( cd "$SCRIPT_DIR/.." && pwd )"
cd "$PROJECT_DIR"

# Load environment variables from .env file if it exists
if [ -f .env ]; then
    # Safely load .env file by exporting only KEY=VALUE pairs
    while IFS= read -r line || [ -n "$line" ]; do
        # Skip comments and empty lines
        [[ "$line" =~ ^[[:space:]]*# ]] && continue
        [[ -z "${line// }" ]] && continue
        # Extract key and value (handles values with spaces)
        if [[ "$line" =~ ^[[:space:]]*([A-Za-z_][A-Za-z0-9_]*)=(.*)$ ]]; then
            key="${BASH_REMATCH[1]}"
            value="${BASH_REMATCH[2]}"
            # Remove leading/trailing whitespace from value
            value="${value#"${value%%[![:space:]]*}"}"
            value="${value%"${value##*[![:space:]]}"}"
            # Export the variable
            export "$key=$value"
        fi
    done < .env
fi

# Set default values if not provided
DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-5432}
DB_NAME=${DB_NAME:-transaction_system}
DB_USERNAME=${DB_USERNAME:-postgres}
DB_PASSWORD=${DB_PASSWORD:-postgres}

SEED_FILE="$PROJECT_DIR/src/main/resources/db/seeds/seed_data.sql"

echo -e "${YELLOW}Database Seeding Script${NC}"
echo "=========================="
echo "Database: $DB_NAME"
echo "Host: $DB_HOST:$DB_PORT"
echo "User: $DB_USERNAME"
echo ""

# Check if seed file exists
if [ ! -f "$SEED_FILE" ]; then
    echo -e "${RED}Error: Seed file not found at $SEED_FILE${NC}"
    exit 1
fi

# Check if database is running in Docker
DOCKER_CONTAINER_NAME=${DB_CONTAINER_NAME:-transaction-system-db}
USE_DOCKER=false

if command -v docker &> /dev/null; then
    if docker ps --format "{{.Names}}" | grep -q "^${DOCKER_CONTAINER_NAME}$"; then
        USE_DOCKER=true
        echo -e "${GREEN}✓ Found Docker container: ${DOCKER_CONTAINER_NAME}${NC}"
    fi
fi

# Check database connection
echo -e "${YELLOW}Checking database connection...${NC}"

if [ "$USE_DOCKER" = true ]; then
    # Use Docker exec to connect to database in container
    if docker exec "$DOCKER_CONTAINER_NAME" psql -U "$DB_USERNAME" -d "$DB_NAME" -c "SELECT 1;" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Database connection successful (via Docker)${NC}"
    else
        echo -e "${RED}Error: Cannot connect to database in Docker container.${NC}"
        echo "  - Container name: ${DOCKER_CONTAINER_NAME}"
        echo "  - Check if container is running: docker ps"
        exit 1
    fi
else
    # Use local psql client
    if ! command -v psql &> /dev/null; then
        echo -e "${RED}Error: psql command not found and Docker container not available.${NC}"
        echo "  Please either:"
        echo "  1. Install PostgreSQL client: sudo apt install postgresql-client"
        echo "  2. Or start Docker container: docker-compose up -d"
        exit 1
    fi
    
    export PGPASSWORD="$DB_PASSWORD"
    if ! psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" -c "SELECT 1;" > /dev/null 2>&1; then
        echo -e "${RED}Error: Cannot connect to database. Please check:${NC}"
        echo "  - Database is running"
        echo "  - Connection settings in .env file"
        echo "  - Database credentials are correct"
        exit 1
    fi
    
    echo -e "${GREEN}✓ Database connection successful${NC}"
fi

echo ""

# Confirm before seeding
echo -e "${YELLOW}This will seed the database with sample data.${NC}"
echo "Existing seed data may be updated (users/merchants/products)."
read -p "Continue? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Seeding cancelled."
    exit 0
fi

# Run seed script
echo -e "${YELLOW}Running seed script...${NC}"

if [ "$USE_DOCKER" = true ]; then
    # Copy seed file to container and execute it
    docker cp "$SEED_FILE" "${DOCKER_CONTAINER_NAME}:/tmp/seed_data.sql"
    docker exec "$DOCKER_CONTAINER_NAME" psql -U "$DB_USERNAME" -d "$DB_NAME" -f /tmp/seed_data.sql
    docker exec "$DOCKER_CONTAINER_NAME" rm /tmp/seed_data.sql
    SEED_EXIT_CODE=$?
else
    # Use local psql client
    export PGPASSWORD="$DB_PASSWORD"
    psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -d "$DB_NAME" -f "$SEED_FILE"
    SEED_EXIT_CODE=$?
    unset PGPASSWORD
fi

if [ $SEED_EXIT_CODE -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✓ Database seeding completed successfully!${NC}"
    echo ""
    echo "Sample data created:"
    echo "  - 5 users with accounts"
    echo "  - 4 merchants"
    echo "  - Multiple products across merchants"
    echo "  - Inventory for all products"
    echo "  - Sample account transactions"
    echo ""
    echo "You can now test the API with these sample users and merchants."
else
    echo -e "${RED}✗ Seeding failed. Please check the error messages above.${NC}"
    exit 1
fi

