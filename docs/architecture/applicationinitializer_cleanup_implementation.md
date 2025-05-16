# ApplicationInitializer Cleanup Implementation

## Overview

This document provides the implementation details for cleaning up the `ApplicationInitializer` class based on the following requirements:

1. There is no CustomerDataAccess, it's CustomerRepository
2. We will only be accessing the repository of the aggregates User and Order
3. There is no SessionManager, but we may want an extremely simple user SessionContext
4. This is a small project, so we should keep it simple

## Implementation Details

The following changes need to be made to the `ApplicationInitializer.java` file:

### 1. Update Imports

Remove unnecessary imports and keep only the essential ones:

```java
package com.belman.bootstrap.config;


```

### 2. Update Repository Declarations

Remove unnecessary repository declarations and keep only User and Order repositories:

```java
// Create repositories
UserRepository userRepository;
OrderRepository orderRepository;
logger.debug("Creating repositories");
```

### 3. Update Repository Initialization

Remove initialization of CustomerRepository, ReportRepository, and PhotoRepository, and keep only User and Order repository initializations:

#### For SQL-based repositories:

```java
// Initialize UserRepository - try SQL implementation first
userRepository = createRepository(UserRepository.class, "SqlUserRepository", dataSource,
        InMemoryUserRepository.class);
ServiceRegistry.registerService(userRepository);

// Initialize OrderRepository - try SQL implementation first
orderRepository = createRepository(OrderRepository.class, "SqlOrderRepository", dataSource,
        InMemoryOrderRepository.class);
ServiceRegistry.registerService(orderRepository);
```

#### For in-memory repositories (fallback):

```java
// Initialize UserRepository as fallback
logger.database("Creating in-memory UserRepository as fallback");
userRepository = new InMemoryUserRepository();
ServiceRegistry.registerService(userRepository);
logger.info("Using in-memory UserRepository as fallback");

// Initialize OrderRepository as fallback
logger.database("Creating in-memory OrderRepository as fallback");
orderRepository = new InMemoryOrderRepository();
ServiceRegistry.registerService(orderRepository);
logger.info("Using in-memory OrderRepository as fallback");
```

### 4. Remove SessionManager Initialization

Remove the SessionManager initialization and registration:

```java
// Initialize SessionManager
logger.debug("Initializing SessionManager");
SessionManager sessionManager = SessionManager.getInstance(authenticationService);
// Register the SessionManager with the ServiceRegistry
ServiceRegistry.registerService(sessionManager);
logger.success("SessionManager initialized successfully");
```

### 5. Create a Simple SessionContext (Optional)

If needed, add a simple SessionContext initialization:

```java
// Initialize a simple SessionContext
logger.debug("Initializing simple SessionContext");
SessionContext sessionContext = new SimpleSessionContext(authenticationService);
// Register the SessionContext with the ServiceRegistry
ServiceRegistry.registerService(sessionContext);
logger.success("Simple SessionContext initialized successfully");
```

## Conclusion

By making these changes, the `ApplicationInitializer` class will be cleaned up to:
1. Remove references to CustomerDataAccess (which should be CustomerRepository)
2. Only access the repositories of the aggregates User and Order
3. Remove SessionManager (optionally replacing it with a simple SessionContext)
4. Keep the code simple and focused on the essential functionality