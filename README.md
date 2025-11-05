# Transaction System

A complete product transaction system built with Spring Boot, implementing user and merchant modules for product purchases with prepaid cash accounts.

## Features

- **User Module**: User management, prepaid account management, account recharge via mocked payment gateway
- **Merchant Module**: Merchant management, product catalog, inventory management with audit trails
- **Transaction Module**: Order processing with atomic transactions (deducts user balance, credits merchant, deducts inventory)
- **Reconciliation Module**: Daily scheduled job to reconcile merchant account balance with calculated sales value
- **Payment Gateway**: Mocked REST API for account recharge (extensible for real banking APIs)
- **Audit Trails**: Complete transaction history for account and inventory changes
- **Optimistic Locking**: Concurrency control for balance and inventory updates
- **RESTful API**: Comprehensive REST API with Swagger/OpenAPI documentation

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0
- **PostgreSQL**: 15 (via Docker)
- **Flyway**: Database migration
- **Lombok**: Boilerplate reduction
- **Swagger/OpenAPI**: API documentation (SpringDoc OpenAPI 3)
- **Spring Retry**: Retry logic for optimistic locking failures
- **JUnit 5 & Mockito**: Unit testing
- **Testcontainers**: Integration testing with real PostgreSQL
- **JaCoCo**: Code coverage reporting

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

All endpoints are documented in Swagger UI. Access at: http://localhost:8080/swagger-ui.html

### User Management APIs

**Base Path**: `/api/v1/users`

- `POST /api/v1/users` - Create a new user (automatically creates prepaid account)
- `GET /api/v1/users/{userId}` - Get user details by ID
- `POST /api/v1/users/{userId}/accounts/recharge` - Recharge user prepaid account via mocked payment gateway
- `GET /api/v1/users/{userId}/accounts/balance` - Get current account balance

### Merchant Management APIs

**Base Path**: `/api/v1/merchants`

- `POST /api/v1/merchants` - Create a new merchant
- `GET /api/v1/merchants/{merchantId}` - Get merchant details (including account balance)

**Products** (Base Path: `/api/v1/merchants/{merchantId}/products`)

- `POST /api/v1/merchants/{merchantId}/products` - Create a new product
- `GET /api/v1/merchants/{merchantId}/products` - Get all products for a merchant
- `GET /api/v1/merchants/{merchantId}/products/{productId}` - Get product details by ID

**Inventory** (Base Path: `/api/v1/merchants/{merchantId}/inventory`)

- `POST /api/v1/merchants/{merchantId}/inventory/products/{productId}/add` - Add quantity to inventory
- `GET /api/v1/merchants/{merchantId}/inventory` - Get all inventory records for a merchant
- `GET /api/v1/merchants/{merchantId}/inventory/products/{productId}` - Get inventory for a specific product

### Transaction APIs

**Base Path**: `/api/v1/orders`

- `POST /api/v1/orders` - Place an order (requires `X-User-Id` header)
  - Atomically: deducts user balance, credits merchant, deducts inventory
- `GET /api/v1/orders/{orderId}` - Get order details by order ID
- `GET /api/v1/orders/order-number/{orderNumber}` - Get order details by order number

### Reconciliation APIs

**Base Path**: `/api/v1/reconciliation/merchants/{merchantId}`

- `POST /api/v1/reconciliation/merchants/{merchantId}/run` - Manually trigger reconciliation for yesterday
- `POST /api/v1/reconciliation/merchants/{merchantId}/run/{reportDate}` - Manually trigger reconciliation for specific date (format: yyyy-MM-dd)
- `GET /api/v1/reconciliation/merchants/{merchantId}/reports` - Get all reconciliation reports for a merchant
- `GET /api/v1/reconciliation/merchants/{merchantId}/reports/{reportDate}` - Get reconciliation report for specific date (format: yyyy-MM-dd)

## Configuration

The application uses a `.env` file for environment-based configuration. This allows you to configure the application without modifying code files.

### Setting Up Environment Variables

1. **Copy the example file:**
   ```bash
   cp .env.example .env
   ```

2. **Edit `.env` file** with your configuration values:
   ```bash
   # Database Configuration
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=transaction_system
   DB_USERNAME=postgres
   DB_PASSWORD=postgres
   
   # Application Configuration
   SERVER_PORT=8080
   APP_NAME=transaction-system
   
   # ... and more
   ```

3. **The `.env` file is git-ignored** - your local configuration won't be committed to the repository.

### Configuration Variables

#### Database Configuration
- `DB_HOST` - Database host (default: `localhost`)
- `DB_PORT` - Database port (default: `5432`)
- `DB_NAME` - Database name (default: `transaction_system`)
- `DB_USERNAME` - Database username (default: `postgres`)
- `DB_PASSWORD` - Database password (default: `postgres`)

#### Application Configuration
- `SERVER_PORT` - Server port (default: `8080`)
- `APP_NAME` - Application name (default: `transaction-system`)

#### Reconciliation
- `RECONCILIATION_ENABLED` - Enable/disable reconciliation (default: `true`)
- `RECONCILIATION_CRON` - Cron schedule (default: `0 0 2 * * *` - daily at 2 AM)
- `DEFAULT_CURRENCY` - Default currency (default: `USD`)

#### Logging
- `LOG_LEVEL_ROOT` - Root log level (default: `INFO`)
- `LOG_LEVEL_APP` - Application log level (default: `DEBUG`)
- `LOG_SQL_ENABLED` - Enable SQL logging (default: `false`)

#### JPA/Hibernate
- `JPA_DDL_AUTO` - DDL mode (default: `validate`)
- `JPA_SHOW_SQL` - Show SQL queries (default: `false`)
- `JPA_BATCH_SIZE` - Batch size (default: `20`)

#### Connection Pool (HikariCP)
- `HIKARI_MAX_POOL_SIZE` - Maximum pool size (default: `10`)
- `HIKARI_MIN_IDLE` - Minimum idle connections (default: `5`)
- `HIKARI_CONNECTION_TIMEOUT` - Connection timeout in ms (default: `30000`)

#### Flyway
- `FLYWAY_ENABLED` - Enable Flyway migrations (default: `true`)

#### Swagger/OpenAPI
- `SWAGGER_ENABLED` - Enable Swagger UI (default: `true`)
- `SWAGGER_UI_PATH` - Swagger UI path (default: `/swagger-ui.html`)

### Configuration Priority

Configuration is loaded in the following order (highest priority first):
1. System environment variables
2. `.env` file (project root)
3. `application.yml` defaults

### Docker Compose Integration

The `docker-compose.yml` file automatically loads variables from `.env`, so your database configuration will be consistent across the application and Docker containers.

## Testing

### Test Structure

The project includes comprehensive unit and integration tests:

**Unit Tests:**
- Domain entity tests (Account, Merchant, Inventory, Product, Order)
- Service layer tests (UserService, OrderService, InventoryService)
- Business logic validation and edge cases

**Integration Tests:**
- Repository integration tests (using Testcontainers with real PostgreSQL)
- Controller integration tests (full end-to-end API testing with MockMvc)

### Run All Tests

```bash
mvn test
```

This runs all **unit tests** by default. Integration tests are excluded by default (require Docker).

### Run Integration Tests

Integration tests require Docker to be running and accessible. To run all tests including integration tests:

```bash
mvn test -Dsurefire.excludedGroups=
```

**Note:** Make sure Docker is running and your user has permission to access Docker. If you get permission errors, see [Docker Permission Issues](#docker-permission-issues) in the SETUP_GUIDE.md.

### Run Tests with Coverage

```bash
mvn test jacoco:report
```

Coverage report will be generated at: `target/site/jacoco/index.html`

### Test Coverage

The project targets **80% unit test coverage** for core business logic:
- Domain entities: ✅ Covered
- Application services: ✅ Covered
- Controllers: ✅ Integration tests included
- Repositories: ✅ Integration tests included

### Test Configuration

- **Unit Tests**: Run by default with `mvn test`
- **Integration Tests**: Excluded by default, require Docker. Run with `mvn test -Dsurefire.excludedGroups=`
- **Testcontainers**: Real PostgreSQL container for integration tests
- **Test Profile**: Uses `application-test.yml` with test-specific configuration
- **Flyway**: Disabled in tests (uses `ddl-auto: create-drop` instead)

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

## Key Features & Design Patterns

### Transaction Management
- **Atomic Operations**: Order processing ensures all-or-nothing execution
- **Optimistic Locking**: Version-based concurrency control for balance and inventory updates
- **Retry Logic**: Automatic retry on optimistic locking failures (Spring Retry)

### Audit Trails
- **Account Transactions**: Complete history of all balance changes (RECHARGE, DEBIT, CREDIT)
- **Inventory Transactions**: Complete history of all inventory changes (ADD, DEDUCT)
- **Reference Tracking**: All transactions linked to orders or operations

### Reconciliation
- **Daily Scheduled Job**: Automatically reconciles all merchants at 2 AM
- **Discrepancy Detection**: Compares account balance vs calculated sales value
- **Report Generation**: Historical reconciliation reports with status (MATCHED/DISCREPANCY)
- **Manual Triggers**: API endpoints for on-demand reconciliation

### Error Handling
- **Global Exception Handler**: Centralized error handling with structured error responses
- **Custom Exceptions**: ResourceNotFound, InsufficientBalance, InvalidOperation
- **Validation**: Jakarta Bean Validation on all request DTOs

## Extensibility

The system is designed to be highly configurable and extensible:

- **Payment Gateway**: Interface-based design allows easy integration of real banking APIs (e.g., https://sandbox.bind.com.ar/apidoc/)
- **Configuration**: Externalized configuration via `application.yml` with Spring `@ConfigurationProperties`
- **Modular Structure**: Clear module boundaries enable future microservice split
- **DDD Architecture**: Domain-driven design with clear aggregate roots and boundaries
- **Event-Driven Hooks**: Ready for Kafka integration if needed
- **Repository Pattern**: Easy to swap implementations or add new data sources

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
4. Check connection settings in `application.yml`

### Port Already in Use

If port 8080 is already in use:
- Change `server.port` in `application.yml`
- Or stop the conflicting service: `lsof -i :8080` or `netstat -tulpn | grep 8080`

### Flyway Migration Issues

If migrations fail:
- Check database connection
- Verify migration files are in `src/main/resources/db/migration/`
- Check Flyway logs in application output
- Ensure database is empty or use `baseline-on-migrate: true`

### Swagger UI Not Showing Endpoints

If Swagger UI is empty:
1. Restart the application (SpringDoc scans at startup)
2. Check application logs for errors
3. Verify controllers are in `com.mamoru.transactionsystem` package
4. Access Swagger UI at: http://localhost:8080/swagger-ui.html

### Test Failures

If tests fail:
1. Ensure Docker is running (for Testcontainers)
2. Check test logs for specific errors
3. Verify test profile is active: `@ActiveProfiles("test")`
4. For integration tests, ensure PostgreSQL container starts successfully

## Quick Start Example

### 1. Create a User
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com"
  }'
```

### 2. Recharge Account
```bash
curl -X POST http://localhost:8080/api/v1/users/1/accounts/recharge \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1000.00
  }'
```

### 3. Create a Merchant
```bash
curl -X POST http://localhost:8080/api/v1/merchants \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tech Store"
  }'
```

### 4. Add a Product
```bash
curl -X POST http://localhost:8080/api/v1/merchants/1/products \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "LAPTOP-001",
    "name": "Gaming Laptop",
    "price": 999.99
  }'
```

### 5. Add Inventory
```bash
curl -X POST http://localhost:8080/api/v1/merchants/1/inventory/products/1/add \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 10
  }'
```

### 6. Place an Order
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "merchantId": 1,
    "sku": "LAPTOP-001",
    "quantity": 1
  }'
```

### 7. Run Reconciliation
```bash
curl -X POST http://localhost:8080/api/v1/reconciliation/merchants/1/run
```

## License

This is an assessment project for technical evaluation purposes.

## Contact

For questions or issues, please refer to the assessment requirements document.

