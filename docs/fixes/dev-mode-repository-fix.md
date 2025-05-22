# Dev Mode Repository Fix

## Issue
In development mode, the application was using an in-memory OrderRepository instead of the SQL-based OrderRepository. This was causing the application to find 0 orders in the database, even though orders should be available in the SQLite database.

## Symptoms
The application was reporting the following in the logs:
```
[DEBUG_LOG] AssignedOrderViewModel: Found 0 total orders in database
[DEBUG_LOG] AssignedOrderViewModel: No orders found in database
[DEBUG_LOG] AssignedOrderViewModel: Dev mode is enabled
[DEBUG_LOG] AssignedOrderViewModel: User is a production worker
[DEBUG_LOG] AssignedOrderViewModel: Dev mode enabled, showing all orders
[DEBUG_LOG] AssignedOrderViewModel: No orders found for current user
```

## Root Cause
The issue was in the `initializeDevModeRepositories` method in `RepositoryInitializer.java`. In development mode, the application was initializing an in-memory OrderRepository instead of a SQL-based OrderRepository:

```java
// Initialize OrderRepository - use in-memory implementation for development mode
logger.database("Dev mode: Creating InMemoryOrderRepository for development");
orderRepository = initializeInMemoryOrderRepository();
logger.info("Dev mode: Using InMemoryOrderRepository for development");
```

This was inconsistent with how the UserRepository was initialized in development mode, which was using a SQL-based repository:

```java
// Initialize UserRepository - use SQLite for persistence in development mode
logger.database("Dev mode: Creating SQL UserRepository with SQLite");
userRepository = createRepository(UserRepository.class, "SqlUserRepository", dataSource,
        InMemoryUserRepository.class);
ServiceRegistry.registerService(userRepository);
logger.info("Dev mode: Using SQLite-based UserRepository for persistence");
```

## Solution
The solution was to update the `initializeDevModeRepositories` method to use a SQL-based OrderRepository in development mode, similar to how the UserRepository is initialized:

```java
// Initialize OrderRepository - use SQL implementation for development mode
logger.database("Dev mode: Creating SQL OrderRepository with SQLite");
orderRepository = createRepository(OrderRepository.class, "SqlOrderRepository", dataSource,
        InMemoryOrderRepository.class);
ServiceRegistry.registerService(orderRepository);
logger.info("Dev mode: Using SQLite-based OrderRepository for persistence");
```

This ensures that in development mode, the application uses the SQLite database for both users and orders, providing a more consistent development experience and allowing orders to be loaded correctly.

## Related Issues
This fix works in conjunction with the photo templates migration (see `docs/fixes/photo-templates-migration.md`) to ensure that orders are loaded correctly in development mode and have photo templates associated with them.