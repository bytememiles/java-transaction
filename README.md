# Transaction System

A complete product transaction system built with Spring Boot, implementing user and merchant modules for product purchases with prepaid cash accounts.

## Features

- **User Module**: User management, prepaid account management, account recharge
- **Merchant Module**: Merchant management, product inventory management
- **Transaction Module**: Order processing with payment deduction and inventory updates
- **Reconciliation Module**: Daily scheduled job to reconcile merchant account balance with sales
- **Payment Gateway**: Mocked REST API for account recharge (extensible for real banking APIs)

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0
- **PostgreSQL**: 15 (via Docker)
- **Flyway**: Database migration
- **Lombok**: Boilerplate reduction
- **Swagger/OpenAPI**: API documentation
- **JUnit 5 & Mockito**: Testing
- **Testcontainers**: Integration testing

## Architecture

The system follows **Domain-Driven Design (DDD)** principles with clear boundary contexts:

- **User Context**: User and Account aggregates
- **Merchant Context**: Merchant, Product, and Inventory aggregates
- **Transaction Context**: Order and Payment aggregates
- **Reconciliation Context**: Reconciliation reports
- **External Integration Context**: Payment gateway adapters

## Project Structure

```
src/main/java/com/mamoru/transactionsystem/
├── common/                    # Common utilities
│   ├── config/               # Configuration classes
│   ├── dto/                  # Shared DTOs
│   └── exception/            # Exception handling
├── user/                      # User module
│   ├── domain/               # Domain entities
│   ├── application/          # Application services
│   ├── infrastructure/      # Repositories
│   └── presentation/         # REST controllers
├── merchant/                  # Merchant module
├── transaction/               # Transaction module
├── reconciliation/            # Reconciliation module
└── payment-gateway/           # Payment gateway module
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## Setup Instructions

### 1. Clone and Navigate

```bash
cd /home/dev/Dev/Assessments/Mamoru
```

### 2. Start PostgreSQL Database

```bash
docker-compose up -d
```

This will start PostgreSQL on port 5432 with:
- Database: `transaction_system`
- Username: `postgres`
- Password: `postgres`

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

Or from your IDE, run the `TransactionSystemApplication` class.

The application will start on `http://localhost:8080`

### 5. Access API Documentation

Once the application is running, access Swagger UI at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## API Endpoints

### User APIs

- `POST /api/v1/users` - Create a new user
- `GET /api/v1/users/{userId}` - Get user details
- `POST /api/v1/users/{userId}/accounts/recharge` - Recharge user account
- `GET /api/v1/users/{userId}/accounts/balance` - Get account balance

### Merchant APIs

- `POST /api/v1/merchants` - Create a new merchant
- `GET /api/v1/merchants/{merchantId}` - Get merchant details
- `POST /api/v1/merchants/{merchantId}/products` - Add a product
- `POST /api/v1/merchants/{merchantId}/inventory/{productId}/add` - Add inventory quantity
- `GET /api/v1/merchants/{merchantId}/inventory` - Get inventory

### Transaction APIs

- `POST /api/v1/orders` - Place an order
- `GET /api/v1/orders/{orderId}` - Get order status
- `GET /api/v1/orders` - List orders (with filters)

### Reconciliation APIs

- `GET /api/v1/reconciliation/merchants/{merchantId}/reports` - Get reconciliation reports
- `POST /api/v1/reconciliation/merchants/{merchantId}/run` - Manually trigger reconciliation

## Configuration

Application configuration is in `src/main/resources/application.yml`:

- Database connection settings
- Reconciliation cron schedule (default: daily at 2 AM)
- Default currency (USD)
- Logging levels

## Testing

### Run All Tests

```bash
mvn test
```

### Run Tests with Coverage

```bash
mvn test jacoco:report
```

Coverage report will be generated at: `target/site/jacoco/index.html`

### Test Coverage Requirement

The core code should have **80% unit test coverage**.

## Database Migration

Database schema is managed by Flyway. Migrations are located in:
- `src/main/resources/db/migration/`

The initial schema (`V1__initial_schema.sql`) creates all necessary tables:
- Users and Accounts
- Merchants, Products, and Inventory
- Orders and Payments
- Reconciliation Reports

## Development Workflow

1. **Create Feature Branch**: `git checkout -b feature/your-feature-name`
2. **Implement Feature**: Follow DDD principles and maintain test coverage
3. **Run Tests**: Ensure all tests pass and coverage is maintained
4. **Commit Changes**: `git commit -m "feat: your feature description"`
5. **Push and Create PR**: Push to remote and create pull request

## Extensibility

The system is designed to be highly configurable and extensible:

- **Payment Gateway**: Interface-based design allows easy integration of real banking APIs
- **Configuration**: Externalized configuration via `application.yml`
- **Modular Structure**: Clear module boundaries enable future microservice split
- **Event-Driven Hooks**: Ready for Kafka integration if needed

## Future Enhancements

- Integration with real banking API (https://sandbox.bind.com.ar/apidoc/)
- Kafka event-driven architecture
- Multi-currency support
- Advanced reporting and analytics
- WebSocket support for real-time updates

## Troubleshooting

### Database Connection Issues

If you encounter database connection errors:
1. Ensure Docker is running: `docker ps`
2. Check PostgreSQL container: `docker-compose ps`
3. Verify database is accessible: `docker exec -it transaction-system-db psql -U postgres -d transaction_system`

### Port Already in Use

If port 8080 is already in use:
- Change `server.port` in `application.yml`
- Or stop the conflicting service

### Flyway Migration Issues

If migrations fail:
- Check database connection
- Verify migration files are in correct location
- Check Flyway logs in application output

## License

This is an assessment project for technical evaluation purposes.

## Contact

For questions or issues, please refer to the assessment requirements document.

