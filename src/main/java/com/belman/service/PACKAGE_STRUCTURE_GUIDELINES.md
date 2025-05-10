# Business Layer Package Structure Guidelines

## Overview

This document outlines the guidelines for organizing classes in the business layer by responsibility or feature. The business layer is organized into modules, with each module representing a business capability or feature.

## Package Structure

```
com.belman.service
├── audit                  # Audit-related classes
├── commands               # Command pattern implementations
│   ├── data               # Data-related commands
│   ├── ui                 # UI-related commands
│   └── validation         # Validation-related commands
├── core                   # Core infrastructure classes
├── module                 # Business modules
│   ├── common             # Common value objects and utilities
│   │   ├── base           # Base classes for value objects
│   │   └── validation     # Validation-related classes
│   ├── core               # Core domain abstractions
│   ├── customer           # Customer-related domain objects
│   │   ├── events         # Customer-related events
│   │   └── services       # Customer-related services
│   ├── events             # Event-related classes
│   ├── exceptions         # Domain-specific exceptions
│   ├── order              # Order-related domain objects
│   │   ├── events         # Order-related events
│   │   ├── photo          # Photo-related domain objects
│   │   │   ├── events     # Photo-related events
│   │   │   ├── policy     # Photo-related policies
│   │   │   └── services   # Photo-related services
│   │   ├── policy         # Order-related policies
│   │   ├── services       # Order-related services
│   │   └── specification  # Order-related specifications
│   ├── report             # Report-related domain objects
│   │   ├── events         # Report-related events
│   │   └── service        # Report-related services
│   ├── security           # Security-related domain objects
│   ├── services           # Domain services
│   ├── shared             # Shared utilities
│   ├── specification      # Specification pattern implementations
│   └── user               # User-related domain objects
│       ├── events         # User-related events
│       ├── factory        # User-related factories
│       ├── rbac           # Role-based access control
│       └── services       # User-related services
├── routing                # Routing-related classes
└── usecases               # Use case implementations
```

## Naming Conventions

- **Business Objects**: Classes that represent the main business entities should end with "Business" (e.g., `OrderBusiness`, `UserBusiness`).
- **Business Components**: Classes that represent parts of business entities should end with "Component" (e.g., `CustomerComponent`).
- **Data Access Interfaces**: Interfaces for data access should end with "DataAccess" or "Repository" (e.g., `OrderDataAccess`, `UserRepository`).
- **Business Services**: Classes that provide business logic should end with "BusinessService" or "DomainService" (e.g., `OrderBusinessService`).
- **Data Objects**: Classes that represent immutable data should end with "DataObject" (e.g., `AddressDataObject`).
- **Audit Events**: Classes that represent audit events should end with "Event" (e.g., `OrderCreatedEvent`).

## Package Organization Rules

### By Responsibility

1. **Business Objects** should be in the module package or a feature-specific package (e.g., `com.belman.service.module.order`).
2. **Business Components** should be in the module package or a feature-specific package (e.g., `com.belman.service.module.customer`).
3. **Data Access Interfaces** should be in the module package or a feature-specific package (e.g., `com.belman.service.module.user`).
4. **Business Services** should be in the module package or a feature-specific services package (e.g., `com.belman.service.module.order.services`).
5. **Data Objects** should be in the module.common.base package (e.g., `com.belman.service.module.common.base`).
6. **Audit Events** should be in the module.events package or a feature-specific events package (e.g., `com.belman.service.module.order.events`).
7. **Specifications** should be in the module.specification package or a feature-specific specification package (e.g., `com.belman.service.module.order.specification`).
8. **Exceptions** should be in the module.exceptions package (e.g., `com.belman.service.module.exceptions`).
9. **Service Interfaces** should be in the module.services package or a feature-specific services package (e.g., `com.belman.service.module.services`).

### By Feature

1. **Order-related classes** should be in the module.order package (e.g., `com.belman.service.module.order`).
2. **User-related classes** should be in the module.user package (e.g., `com.belman.service.module.user`).
3. **Customer-related classes** should be in the module.customer package (e.g., `com.belman.service.module.customer`).
4. **Report-related classes** should be in the module.report package (e.g., `com.belman.service.module.report`).
5. **Photo-related classes** should be in the module.order.photo package (e.g., `com.belman.service.module.order.photo`).
6. **Security-related classes** should be in the module.security package (e.g., `com.belman.service.module.security`).
7. **Common value objects** should be in the module.common package (e.g., `com.belman.service.module.common`).

## Examples

### Business Objects

```java
// OrderBusiness.java
package com.belman.service.module.order;

import com.belman.domain.order.OrderId;

public class OrderBusiness extends BusinessObject<OrderId> {
    // Implementation
}
```

### Business Components

```java
// CustomerComponent.java
package com.belman.service.module.customer;

import com.belman.domain.customer.CustomerId;

public class CustomerComponent extends BusinessComponent<CustomerId> {
    // Implementation
}
```

### Data Access Interfaces

```java
// UserRepository.java
package com.belman.service.module.user;

public interface UserRepository {
    // Interface methods
}
```

### Business Services

```java
// OrderBusinessService.java
package com.belman.service.module.order.services;

public class OrderBusinessService implements BusinessService {
    // Implementation
}
```

### Data Objects

```java
// AddressDataObject.java
package com.belman.service.module.common.base;

import com.belman.domain.common.base.DataObject;

public final class AddressDataObject extends DataObject {
    // Implementation
}
```

### Audit Events

```java
// OrderCreatedEvent.java
package com.belman.service.module.order.events;

public class OrderCreatedEvent extends BaseAuditEvent {
    // Implementation
}
```

### Specifications

```java
// OrderCompletionSpecification.java
package com.belman.service.module.order.specification;

public class OrderCompletionSpecification implements Specification<Order> {
    // Implementation
}
```

### Exceptions

```java
// BusinessRuleViolationException.java
package com.belman.service.module.exceptions;

import com.belman.domain.exceptions.BusinessException;

public class BusinessRuleViolationException extends BusinessException {
    // Implementation
}
```

### Service Interfaces

```java
// EmailService.java
package com.belman.service.module.services;

public interface EmailService {
    // Interface methods
}
```

## Benefits of This Organization

1. **High Cohesion**: Classes that are related to the same feature or responsibility are grouped together, making it easier to understand and maintain the codebase.
2. **Low Coupling**: Classes are organized in a way that minimizes dependencies between packages, making it easier to change one part of the system without affecting others.
3. **Clear Boundaries**: The package structure clearly defines the boundaries between different parts of the system, making it easier to understand the system's architecture.
4. **Improved Maintainability**: The consistent package structure makes it easier to find and modify classes, reducing the time needed to make changes to the system.
5. **Better Testability**: The clear separation of concerns makes it easier to test individual components in isolation.

## Implementation Strategy

1. **Identify Classes**: Identify classes that should be moved to different packages based on the rules above.
2. **Create Packages**: Create the necessary packages if they don't already exist.
3. **Move Classes**: Move the classes to the appropriate packages.
4. **Update Imports**: Update import statements in all affected classes.
5. **Run Tests**: Run tests to ensure that everything still works after the reorganization.
6. **Document Changes**: Document the changes made to the package structure.

## Conclusion

Following these guidelines will help ensure that the business layer is organized in a way that is easy to understand, maintain, and extend. The package structure should reflect the business domain and make it clear where different types of classes should be placed.