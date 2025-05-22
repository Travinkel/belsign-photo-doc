# Order Loading Mechanism Documentation

This document describes the order loading mechanism in the Belsign Photo Documentation application.

## Overview

The Belsign Photo Documentation application loads orders from a database (either SQLite or SQL Server) using a repository pattern. The application supports multiple storage types and can be configured to use different repositories based on the environment.

## Storage Types

The application supports three storage types:

1. **Memory Mode** (`BELSIGN_STORAGE_TYPE=memory`): Uses in-memory repositories for testing and development.
2. **SQLite Mode** (`BELSIGN_STORAGE_TYPE=sqlite`): Uses SQLite database for development.
3. **SQL Server Mode** (`BELSIGN_STORAGE_TYPE=sqlserver`): Uses SQL Server database for production.

## Repository Selection

The `RepositoryInitializer` class is responsible for creating repository instances based on the storage type and mock mode. The repository selection logic has been modified to prioritize SQL repositories for order loading in production environments, even if mock mode is enabled.

```
// Special case for OrderRepository: Always try to use SQL implementation first if not in memory mode
if (repositoryInterface == OrderRepository.class && 
    storageType != StorageTypeConfig.StorageType.MEMORY) {
    logger.database("OrderRepository: Prioritizing SQL implementation regardless of mock mode");

    try {
        // Try to create SQL implementation first
        T sqlRepository = createSqlRepository(repositoryInterface, sqlImplName, dataSource);
        if (sqlRepository != null) {
            logger.info("Using SQL-based OrderRepository for order loading (priority mode)");
            logger.startup("📊 IMPORTANT: Orders will be loaded from the database regardless of mock mode");
            return sqlRepository;
        }
    } catch (Exception e) {
        logger.warn("Failed to create SQL-based OrderRepository, falling back to in-memory implementation", e);
        logger.error("Error details: " + e.getMessage());
        // Fall through to in-memory implementation
    }
}
```

This ensures that orders are loaded from the database even if mock mode is enabled, which is important for production environments.

## Order Loading Process

The order loading process involves the following components:

1. **OrderRepository**: Interface for accessing orders in the database.
   - **SqlOrderRepository**: Implementation that loads orders from a SQL database.
   - **InMemoryOrderRepository**: Implementation that stores orders in memory.

2. **OrderService**: Service for managing orders.
   - Uses the OrderRepository to load orders from the database.
   - Provides methods for creating, updating, and retrieving orders.

3. **OrderIntakeService**: Service for coordinating order intake operations.
   - Delegates to specialized services for different responsibilities.
   - Uses the OrderCreationService to create new orders.
   - Uses the MockFolderRefreshService to refresh orders from the mock folder.

4. **MockFolderRefreshService**: Service for refreshing orders from the mock folder.
   - Uses an OrderProvider to fetch new orders.
   - Uses the OrderCreationService to create orders in the database.

5. **OrderProvider**: Interface for providing orders from external sources.
   - **MockOrderProvider**: Implementation that creates mock orders with random data.
   - **DatabaseOrderProvider**: Implementation that provides orders from the database.

## Order Number Handling

The application supports two order number formats:

1. Legacy format: "ORD-XX-YYMMDD-ABC-NNNN" (e.g., "ORD-01-230701-WLD-0001")
2. New format: "MM/YY-CUSTOMER-SEQUENCE" (e.g., "01/23-123456-12345678")

The `OrderNumber` class validates that order numbers match one of these formats and provides methods to extract parts of the order number regardless of the format.

The `SqlOrderRepository` has been enhanced to handle both formats and to validate order numbers when loading orders from the database. If an order number is invalid, a warning is logged, but the order is still loaded.

## Diagnostic Tools

The application includes a diagnostic tool for checking the database for order data issues:

- **OrderDiagnosticTool**: Tool for identifying and reporting issues with order data in the database.
  - Checks for missing or invalid order numbers, missing customer IDs, missing product descriptions, etc.
  - Can attempt to fix issues with order data, such as invalid order numbers.

## Conclusion

The order loading mechanism in the Belsign Photo Documentation application has been designed to be robust and flexible, supporting multiple storage types and order number formats. The application prioritizes loading orders from the database in production environments, even if mock mode is enabled, to ensure that orders are correctly loaded from the database.
