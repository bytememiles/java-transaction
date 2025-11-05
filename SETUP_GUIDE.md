# Setup Guide - Transaction System

This guide will help you install the required tools and launch the Transaction System application.

## Prerequisites

You need to install the following tools:

### 1. Java 17 or Higher

```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

Verify installation:
```bash
java -version
# Should show: openjdk version "17" or higher
```

### 2. Maven

```bash
sudo apt install maven
```

Verify installation:
```bash
mvn --version
# Should show: Apache Maven 3.6+ or higher
```

### 3. Docker and Docker Compose

#### Option A: Install Docker via apt (recommended)
```bash
sudo apt update
sudo apt install docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker
```

Add your user to docker group (to run without sudo):
```bash
sudo usermod -aG docker $USER
su - ${USER}
# Log out and log back in for changes to take effect
```

#### Option B: Install Docker via snap
```bash
sudo snap install docker
```

Verify installation:
```bash
docker --version
docker-compose --version
# OR for Docker v2+
docker compose version
```

## Quick Launch

Once all prerequisites are installed, you can use the launch script:

```bash
./launch.sh
```

This script will:
1. Check all prerequisites
2. Start PostgreSQL database
3. Build the project
4. Run the application

## Manual Launch Steps

If you prefer to launch manually:

### Step 1: Start PostgreSQL Database

```bash
# Using docker-compose (older Docker)
docker-compose up -d

# OR using docker compose (Docker v2+)
docker compose up -d

# Verify database is running
docker ps
```

### Step 2: Build the Project

```bash
mvn clean install -DskipTests
```

### Step 3: Run the Application

```bash
mvn spring-boot:run
```

Or if you want to run tests first:

```bash
mvn clean install
mvn spring-boot:run
```

## Verify Everything is Working

1. **Check Database**: 
   ```bash
   docker ps
   # Should show transaction-system-db container running
   ```

2. **Check Application**: 
   - Open browser: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html

3. **Check Logs**:
   ```bash
   # Application logs will show in the terminal
   # Database logs:
   docker logs transaction-system-db
   ```

## Troubleshooting

### Database Connection Issues

If you see database connection errors:

1. Check if PostgreSQL is running:
   ```bash
   docker ps | grep transaction-system-db
   ```

2. Check database logs:
   ```bash
   docker logs transaction-system-db
   ```

3. Restart the database:
   ```bash
   docker-compose down
   docker-compose up -d
   ```

### Port Already in Use

If port 8080 is already in use:

1. Find what's using the port:
   ```bash
   sudo lsof -i :8080
   # OR
   sudo netstat -tulpn | grep 8080
   ```

2. Change the port in `src/main/resources/application.yml`:
   ```yaml
   server:
     port: 8081  # Change to available port
   ```

### Maven Build Errors

If Maven build fails:

1. Clean and rebuild:
   ```bash
   mvn clean
   mvn install -DskipTests
   ```

2. Check Java version:
   ```bash
   java -version  # Should be Java 17+
   ```

3. Clear Maven cache (if needed):
   ```bash
   rm -rf ~/.m2/repository
   mvn clean install -U
   ```

### Docker Permission Issues

If you get "permission denied" errors with Docker:

```bash
# Add user to docker group
sudo usermod -aG docker $USER

# Log out and log back in, then verify:
groups
# Should include 'docker'
```

## Alternative: Using IDE

You can also run the application from your IDE:

1. **IntelliJ IDEA**:
   - Open the project
   - Right-click on `TransactionSystemApplication.java`
   - Select "Run 'TransactionSystemApplication'"

2. **Eclipse**:
   - Import as Maven project
   - Right-click on `TransactionSystemApplication.java`
   - Run As > Spring Boot App

3. **VS Code**:
   - Install Java extensions
   - Open the project
   - Run the main class

## Next Steps

Once the application is running:

1. Access Swagger UI: http://localhost:8080/swagger-ui.html
2. Test the APIs using Swagger UI or Postman
3. Check the README.md for API documentation

## Stopping the Application

- Press `Ctrl+C` in the terminal running the application
- Stop database: `docker-compose down` (or `docker compose down`)

