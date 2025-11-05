#!/bin/bash

# Transaction System Launch Script
# This script helps launch the Transaction System application

# Note: We don't use 'set -e' to allow graceful error handling

echo "=========================================="
echo "Transaction System - Launch Script"
echo "=========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if Docker is installed
check_docker() {
    if ! command -v docker &> /dev/null; then
        echo -e "${RED}❌ Docker is not installed${NC}"
        echo "Please install Docker:"
        echo "  sudo apt install docker.io"
        echo "  OR"
        echo "  sudo snap install docker"
        echo ""
        return 1
    else
        echo -e "${GREEN}✓ Docker is installed${NC}"
        docker --version
        return 0
    fi
}

# Check if Docker Compose is available
check_docker_compose() {
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        echo -e "${RED}❌ Docker Compose is not installed${NC}"
        echo "Please install Docker Compose or use 'docker compose' (Docker v2+)"
        echo ""
        return 1
    else
        echo -e "${GREEN}✓ Docker Compose is available${NC}"
        return 0
    fi
}

# Check if Docker daemon is accessible
check_docker_permissions() {
    if ! docker info &> /dev/null; then
        echo -e "${RED}❌ Cannot access Docker daemon${NC}"
        echo ""
        echo "Docker permission error detected. To fix this:"
        echo ""
        echo "1. Add your user to the docker group:"
        echo "   sudo usermod -aG docker $USER"
        echo ""
        echo "2. Log out and log back in (or run 'newgrp docker') for the changes to take effect"
        echo ""
        echo "3. Verify access:"
        echo "   docker ps"
        echo ""
        echo "Alternatively, you can run this script with sudo (not recommended):"
        echo "   sudo ./launch.sh"
        echo ""
        return 1
    else
        echo -e "${GREEN}✓ Docker daemon is accessible${NC}"
        return 0
    fi
}

# Check if Java is installed
check_java() {
    if ! command -v java &> /dev/null; then
        echo -e "${RED}❌ Java is not installed${NC}"
        echo "Please install Java 17:"
        echo "  sudo apt install openjdk-17-jdk"
        echo ""
        return 1
    else
        JAVA_VERSION=$(java -version 2>&1 | head -n 1)
        echo -e "${GREEN}✓ Java is installed${NC}"
        echo "  $JAVA_VERSION"
        
        # Check if it's Java 17 or higher
        JAVA_MAJOR=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
        if [ "$JAVA_MAJOR" -lt 17 ]; then
            echo -e "${YELLOW}⚠ Warning: Java 17 or higher is required. Current: Java $JAVA_MAJOR${NC}"
            return 1
        fi
        return 0
    fi
}

# Check if Maven is installed
check_maven() {
    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}❌ Maven is not installed${NC}"
        echo "Please install Maven:"
        echo "  sudo apt install maven"
        echo ""
        return 1
    else
        echo -e "${GREEN}✓ Maven is installed${NC}"
        mvn --version | head -n 1
        return 0
    fi
}

# Start PostgreSQL database
start_database() {
    echo ""
    echo -e "${YELLOW}Starting PostgreSQL database...${NC}"
    
    # Try to start the database
    local OUTPUT
    if command -v docker-compose &> /dev/null; then
        OUTPUT=$(docker-compose up -d 2>&1)
    else
        OUTPUT=$(docker compose up -d 2>&1)
    fi
    local EXIT_CODE=$?
    
    # Filter and show output (excluding warnings)
    echo "$OUTPUT" | grep -v "^WARN" || true
    
    if [ $EXIT_CODE -ne 0 ]; then
        echo -e "${RED}❌ Failed to start database${NC}"
        return $EXIT_CODE
    fi
    
    echo "Waiting for database to be ready..."
    sleep 5
    
    # Check if database is running
    if docker ps 2>/dev/null | grep -q transaction-system-db; then
        echo -e "${GREEN}✓ Database is running${NC}"
        return 0
    else
        echo -e "${RED}❌ Failed to start database${NC}"
        return 1
    fi
}

# Build the project
build_project() {
    echo ""
    echo -e "${YELLOW}Building the project...${NC}"
    mvn clean install -DskipTests
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Project built successfully${NC}"
        return 0
    else
        echo -e "${RED}❌ Build failed${NC}"
        return 1
    fi
}

# Run the application
run_application() {
    echo ""
    echo -e "${YELLOW}Starting the application...${NC}"
    echo ""
    echo "Application will be available at:"
    echo "  - Main: http://localhost:8080"
    echo "  - Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "  - API Docs: http://localhost:8080/api-docs"
    echo ""
    echo "Press Ctrl+C to stop the application"
    echo ""
    
    mvn spring-boot:run
}

# Main execution
main() {
    echo "Checking prerequisites..."
    echo ""
    
    DOCKER_OK=false
    JAVA_OK=false
    MAVEN_OK=false
    
    if check_docker && check_docker_compose && check_docker_permissions; then
        DOCKER_OK=true
    fi
    
    if check_java; then
        JAVA_OK=true
    fi
    
    if check_maven; then
        MAVEN_OK=true
    fi
    
    echo ""
    if [ "$DOCKER_OK" = true ] && [ "$JAVA_OK" = true ] && [ "$MAVEN_OK" = true ]; then
        echo -e "${GREEN}All prerequisites are met!${NC}"
        echo ""
        
        # Start database
        if start_database; then
            # Build project
            if build_project; then
                # Run application
                run_application
            fi
        fi
    else
        echo -e "${RED}Please install the missing prerequisites and run this script again.${NC}"
        exit 1
    fi
}

# Run main function
main

