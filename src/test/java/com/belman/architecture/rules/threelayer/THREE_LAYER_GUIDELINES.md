# Three-Layer Architecture Guidelines

## Overview

The BelSign Photo Documentation System follows a three-layer architecture as required by the project specifications. This document outlines the guidelines for maintaining this architecture.

## Layers

The system is divided into three distinct layers:

1. **Presentation Layer** (`com.belman.presentation`)
   - User interface components
   - View models
   - Controllers
   - Navigation/routing

2. **Business Layer** (`com.belman.business`)
   - Domain model (entities, value objects, aggregates)
   - Business logic
   - Use cases
   - Application services
   - Domain events

3. **Data Layer** (`com.belman.data`)
   - Repository implementations
   - Data access
   - External services integration
   - Infrastructure concerns

## Dependency Rules

The following dependency rules must be followed:

1. The Presentation layer may depend on the Business layer but not on the Data layer
2. The Business layer may depend on its own components but not on the Presentation or Data layers
3. The Data layer may depend on the Business layer (to implement interfaces) but not on the Presentation layer

## Component Placement Guidelines

### Presentation Layer

- **Views**: All UI components should be placed in `com.belman.presentation.views.[feature]`
- **ViewModels**: All view models should be placed in `com.belman.presentation.views.[feature]` and named with the suffix "ViewModel"
- **Controllers**: All controllers should be placed in `com.belman.presentation.views.[feature]` and named with the suffix "Controller"

### Business Layer

- **Domain Model**: All domain entities, value objects, and aggregates should be placed in `com.belman.business.domain`
- **Use Cases**: All use cases should be placed in `com.belman.business.usecases.[feature]` and named with the suffix "UseCase"
- **Services**: All business services should be placed in `com.belman.business.services` or `com.belman.business.domain.[feature].services` and named with the suffix "Service"
- **Repository Interfaces**: All repository interfaces should be placed in `com.belman.business.domain.[feature]` and named with the suffix "Repository"

### Data Layer

- **Repository Implementations**: All repository implementations should be placed in `com.belman.data.persistence` and named with a prefix indicating the storage mechanism (e.g., "InMemory", "Sql") and the suffix "Repository"
- **External Services**: All external service integrations should be placed in appropriate packages under `com.belman.data`

## Naming Conventions

- **ViewModels**: `[Feature]ViewModel`
- **Controllers**: `[Feature]Controller`
- **Views**: `[Feature]View`
- **Use Cases**: `[Action]UseCase`
- **Repository Interfaces**: `[Entity]Repository`
- **Repository Implementations**: `[Storage][Entity]Repository`

## Architecture Tests

The architecture is enforced through automated tests in the `com.belman.architecture.rules.threelayer` package. These tests verify:

1. The layered architecture structure (Presentation, Business, Data)
2. Dependency rules between layers
3. Proper placement of components in their respective layers
4. Naming conventions for components

## Best Practices

1. **Separation of Concerns**: Each layer should focus on its specific responsibilities
2. **Interface-Based Design**: Use interfaces to define contracts between layers
3. **Dependency Injection**: Use dependency injection to provide implementations to higher layers
4. **Testability**: Design components to be easily testable in isolation
5. **Consistency**: Follow the established patterns and conventions consistently

## Examples

### Presentation Layer Example

```java
// View
public class OrderGalleryView extends BaseView<OrderGalleryViewModel> {
    // View implementation
}

// ViewModel
public class OrderGalleryViewModel extends BaseViewModel<OrderGalleryViewModel> {
    // ViewModel implementation
}

// Controller
public class OrderGalleryViewController extends BaseController<OrderGalleryViewModel> {
    // Controller implementation
}
```

### Business Layer Example

```java
// Domain Entity
public class OrderAggregate extends AggregateRoot {
    // Entity implementation
}

// Repository Interface
public interface OrderRepository {
    // Repository contract
}

// Use Case
public class CreateOrderUseCase {
    // Use case implementation
}
```

### Data Layer Example

```java
// Repository Implementation
public class SqlOrderRepository implements OrderRepository {
    // Repository implementation
}
```