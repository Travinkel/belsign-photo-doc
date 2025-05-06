# BelSign Architectural Guide

This document outlines the architectural patterns, principles, and organization of the BelSign Photo Documentation
System. The system follows a combination of Clean Architecture, Domain-Driven Design (DDD), and MVVM+C (
Model-View-ViewModel + Controller) patterns adapted for JavaFX.

## Core Architectural Principles

### Clean Architecture

The application is organized into concentric layers, with dependencies pointing inward:

1. **Domain Layer** (innermost): Pure business logic with no dependencies on other layers
2. **Application Layer**: Use cases, application services, and DTOs
3. **Infrastructure Layer**: External concerns like database, networking, and platform services
4. **Presentation Layer** (outermost): UI components and view-related code

Each layer is protected by interfaces, and dependencies flow inward, never outward. This means:

- Domain doesn't know about Application, Infrastructure, or Presentation
- Application knows about Domain but not Infrastructure or Presentation
- Infrastructure knows about Domain and Application but not Presentation
- Presentation knows about all inner layers

### Domain-Driven Design (DDD)

The domain model is at the heart of the application and is organized around business capabilities:

- **Bounded Contexts**: Clearly defined business areas with their own models and terminology
- **Aggregates**: Clusters of entities and value objects with clear boundaries
- **Entities**: Objects with identity that persists over time
- **Value Objects**: Immutable objects defined by their attributes
- **Domain Events**: Significant occurrences in the domain
- **Repositories**: Abstractions for persistence of aggregates
- **Domain Services**: Operations that don't naturally fit into entities or value objects

### Package-by-Feature (Vertical Slicing)

The codebase is organized primarily by business feature rather than by technical layers:

```
com.belman
├── auth          # Authentication feature
│   ├── domain      # Domain layer for authentication
│   ├── application # Application layer for authentication
│   ├── infra       # Infrastructure layer for authentication
│   └── ui          # Presentation layer for authentication
├── photos        # Photo documentation feature
│   ├── domain      # Domain layer for photos
│   ├── application # Application layer for photos 
│   ├── infra       # Infrastructure layer for photos
│   └── ui          # Presentation layer for photos
├── orders        # Order management feature
...
```

This organization:

- Makes feature boundaries explicit
- Reduces coupling between features
- Makes it clearer what code is related to each feature
- Facilitates feature development by teams

### MVVM+C Pattern for UI

For the presentation layer, we use an MVVM+C (Model-View-ViewModel + Controller) pattern adapted for JavaFX:

- **Model**: Domain entities and business logic
- **View**: FXML files and minimal JavaFX controller classes
- **ViewModel**: Classes that transform domain models into observable properties for views
- **Controller**: Coordinates between the view and the application layer

## Layer Responsibilities

### Domain Layer

The domain layer is the heart of the application and contains:

- Entities
- Value objects
- Aggregates
- Domain events
- Repository interfaces
- Domain services
- Domain exceptions

The domain layer has no dependencies on other layers or external frameworks.

### Application Layer

The application layer coordinates the flow of data and actions:

- Use cases
- Commands and queries
- Application services
- DTOs (Data Transfer Objects)
- Command and query handlers
- Application events
- Validation logic

The application layer depends only on the domain layer.

### Infrastructure Layer

The infrastructure layer deals with external concerns:

- Database implementations
- Repository implementations
- External API clients
- File system access
- Email services
- Security implementations
- Logging
- Configuration

The infrastructure layer depends on both the domain and application layers.

### Presentation Layer

The presentation layer is responsible for the UI:

- FXML files
- Controllers
- ViewModels
- UI events
- UI validation
- Navigation

The presentation layer depends on the domain, application, and infrastructure layers.

## Feature Organization

Each business feature is organized into its own vertical slice, containing all the necessary code from domain to
presentation layers. Common features include:

- **Auth**: User authentication and authorization
- **Photos**: Photo capture, uploading, annotation, and approval
- **Orders**: Order creation, tracking, and fulfillment
- **Reports**: Report generation and delivery
- **Customers**: Customer information and management
- **Common**: Cross-cutting concerns and shared functionality

## Communication Between Layers

### Domain to Application

- Application services depend directly on domain objects
- Repository interfaces defined in domain, implemented in infrastructure

### Application to Infrastructure

- Dependencies are inverted with interfaces
- Infrastructure implements interfaces defined in application

### Application to Presentation

- ViewModels translate application DTOs to UI-ready data
- Controllers call application services and update ViewModels

## Mobile-Friendly Design Considerations

As a mobile-friendly JavaFX application using Gluon Mobile:

- Responsive layouts that adapt to different screen sizes
- Touch-friendly UI components with appropriate sizing
- Platform detection for device-specific behavior
- Asynchronous operations to avoid UI freezes
- Offline-first design with data synchronization

## Testing Strategy

- Domain layer: Unit tests with in-memory repositories
- Application layer: Unit tests with mocked dependencies
- Infrastructure layer: Integration tests with test databases
- Presentation layer: UI tests with TestFX
- End-to-end tests for critical user journeys

## Architecture Enforcement

The architecture is enforced through:

- Package structure and visibility modifiers
- ArchUnit tests to detect violations
- Clear naming conventions
- Consistent patterns and templates
- Documentation and team reviews

## Boundary Rules

1. The domain layer must never access infrastructure or presentation code
2. External frameworks and libraries should be isolated in the infrastructure layer
3. Domain entities should never be exposed directly to the presentation layer
4. UI thread safety must be maintained using JavaFX thread utilities
5. Business logic must reside in domain or application layers, never in presentation

## Conclusion

This architectural approach provides a solid foundation for building a maintainable, testable, and scalable photo
documentation system. By combining Clean Architecture, DDD, and MVVM+C patterns with a package-by-feature organization,
we create clear boundaries and responsibilities that facilitate development and evolution of the system.