# Domain Architecture

## Overview

This document outlines the architecture of the BelSign photo documentation system domain layer. The architecture follows
Domain-Driven Design (DDD) principles and Clean Architecture, organizing the codebase by business capabilities rather
than technical concerns.

## Key DDD Concepts Applied

1. **Bounded Contexts**: The domain is divided into clearly defined bounded contexts, each representing a coherent area
   of business functionality.

2. **Ubiquitous Language**: Each bounded context has its own language, reflected in the code through clear naming and
   consistent terminology.

3. **Aggregates**: Business entities are grouped into aggregates with clear boundaries and a single entry point (the
   aggregate root).

4. **Value Objects**: Immutable objects that represent a descriptive aspect of the domain without identity.

5. **Domain Events**: Important occurrences within the domain that other parts of the application might be interested
   in.

6. **Domain Services**: Operations that conceptually belong to the domain but don't naturally fit within an entity or
   value object.

7. **Repositories**: Abstractions for retrieving and persisting aggregates.

8. **Specifications**: Encapsulations of business rules that can be combined and reused.

9. **Policies**: Encapsulations of varying business rules that may change over time or by context.

10. **Anti-corruption Layers**: Components that translate between different bounded contexts to maintain isolation.

## Bounded Contexts

The domain is organized into the following bounded contexts:

### Photo Context

Responsible for managing photo documents, including metadata, approval workflows, and quality standards.

**Key Components:**

- `PhotoDocument` (Aggregate Root): Represents a photo taken as part of quality documentation
- `PhotoAngle`: Value object for the angle of a photo
- `PhotoAnnotation`: Value object for annotations on photos
- `ApprovalStatus`: Enum for photo approval workflow states
- `PhotoQualityPolicy`: Policy for determining photo quality requirements
- `PhotoValidationService`: Domain service for validating photos

### Order Context

Manages customer orders and their lifecycle, including status tracking and completion criteria.

**Key Components:**

- `OrderAggregate` (Aggregate Root): Represents a customer order
- `OrderId`: Value object for order identification
- `OrderNumber`: Value object for business-readable order identification
- `OrderStatus`: Enum for order lifecycle states
- `ProductDescription`: Value object for product details
- `DeliveryInformation`: Value object for delivery details

### Report Context

Handles the generation and management of quality control reports.

**Key Components:**

- `Report` (Aggregate Root): Represents a quality documentation report
- `ReportId`: Value object for report identification
- `ReportType`: Enum for different types of reports
- `ReportStatus`: Enum for report workflow states
- `ReportFormat`: Enum for report output formats
- `PhotoReportGenerationService`: Domain service for creating reports from photos

### User Context

Manages user accounts, authentication, and permissions.

**Key Components:**

- `UserAggregate` (Aggregate Root): Represents a user of the system
- `UserReference`: Value object for referring to users from other contexts
- `UserStatus`: Enum for user account states

### Customer Context

Handles customer information and relationships.

**Key Components:**

- `CustomerAggregate` (Aggregate Root): Represents a customer
- `CustomerId`: Value object for customer identification
- `CustomerType`: Enum for different types of customers

### Common (Cross-cutting)

Contains value objects and primitives used across multiple bounded contexts.

**Key Components:**

- `Timestamp`: Value object for representing points in time
- `Money`: Value object for monetary amounts
- `EmailAddress`: Value object for email addresses
- `PhoneNumber`: Value object for phone numbers

## Cross-context Communication

Communication between bounded contexts is managed through:

1. **Domain Events**: Contexts publish events that other contexts can subscribe to, allowing loose coupling.

2. **Anti-corruption Layers**: When direct communication is necessary, ACLs translate between contexts.

3. **Shared Kernel**: The `common` package contains carefully managed shared concepts.

## Repository Pattern

Each aggregate root has a corresponding repository interface that:

- Provides collection-like access to aggregates
- Abstracts persistence details
- Returns fully reconstituted aggregates
- Handles saving new and updated aggregates
- Publishes domain events when aggregates are successfully saved

## Domain Events

The domain event system allows:

- Registration of domain events with aggregates
- Publishing events when aggregates are saved
- Subscribing to events across bounded contexts
- Decoupled communication between bounded contexts

## Validation

Business rule validation is handled through:

- Self-validation in value objects
- Invariant enforcement in entities and aggregates
- Specifications for complex validation rules
- Domain services for validation of multiple aggregates
- Policies for business rules that may change

## Dependency Rule

Following Clean Architecture principles:

- The domain layer has no dependencies on outer layers
- Domain interfaces (repositories, services) are implemented in outer layers
- Domain events are published and subscribed to without knowledge of infrastructure
- Anti-corruption layers protect bounded context boundaries

## Package Structure

```
com.belman.domain
├── common          # Shared value objects and base classes
├── events          # Domain event infrastructure
├── core            # Core domain concepts (Entity, AggregateRoot)
├── specification   # Specification pattern infrastructure
├── photo           # Photo bounded context
├── order           # Order bounded context
├── report          # Report bounded context
├── user            # User bounded context
└── customer        # Customer bounded context
```

Each bounded context package follows a similar structure:

- Aggregate roots and entities at the root
- Value objects specific to the context
- Domain events in an `events` subpackage
- Domain services in a `service` subpackage
- Specifications in a `specification` subpackage
- Policies in a `policy` subpackage

## Conclusion

This architecture provides a solid foundation for the BelSign photo documentation system, with clear boundaries between
business capabilities, strong domain modeling, and proper encapsulation of business rules. It allows for evolution of
the system over time while maintaining conceptual integrity and alignment with the business domain.