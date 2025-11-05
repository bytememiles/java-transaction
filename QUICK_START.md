# Quick Start Guide

## Prerequisites Installation (One-time setup)

Run these commands to install all required tools:

```bash
# Update package list
sudo apt update

# Install Java 17
sudo apt install -y openjdk-17-jdk

# Install Maven
sudo apt install -y maven

# Install Docker
sudo apt install -y docker.io docker-compose

# Start Docker service
sudo systemctl start docker
sudo systemctl enable docker

# Add your user to docker group (to run without sudo)
sudo usermod -aG docker $USER

# Log out and log back in for docker group to take effect
# OR run: newgrp docker
```

## Launch the Application

After installing prerequisites and logging back in:

```bash
# Navigate to project directory
cd /home/dev/Dev/Assessments/Mamoru

# Run the launch script
./launch.sh
```

Or manually:

```bash
# 1. Start database
docker-compose up -d

# 2. Build project
mvn clean install -DskipTests

# 3. Run application
mvn spring-boot:run
```

## Access the Application

- **Main Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## Troubleshooting

If you encounter issues, see `SETUP_GUIDE.md` for detailed troubleshooting steps.

