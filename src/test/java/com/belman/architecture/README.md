# Architecture Tests

This package contains architecture tests using [ArchUnit](https://www.archunit.org/) to enforce architectural
constraints and principles in the BelSign Photo Documentation application. These tests ensure that the code adheres to
the desired architectural patterns and best practices.

## Consolidated Architecture Tests

We've consolidated the architecture tests into more focused and comprehensive test classes:

### 1. ConsolidatedCleanArchitectureTest

Tests that enforce the Clean Architecture principles and layer dependencies:
- Domain layer should not depend on other layers
- Application layer should not depend on presentation or infrastructure layers
- Infrastructure layer should not depend on presentation layer
- Controllers should be in the presentation layer
- ViewModels should be in the presentation layer
- Repository interfaces should be in the domain layer
- Repository implementations should be in the infrastructure layer

### 2. ConsolidatedDddPackageStructureTest

Tests for Domain-Driven Design package structure and class placement:

- Entities should be in the domain.entities package
- Value objects should be in the domain.valueobjects package and be immutable
- Aggregate roots should be in the domain.aggregates package
- Domain events should be in the domain.events package and be immutable
- Repository interfaces should be in the domain.repositories package
- Service interfaces should be in the domain.services package
- Service implementations should be in the application or infrastructure layers
- Specifications should be in the domain.specification package

### 3. LayerPackagingRulesTest

Tests for proper organization of classes within each layer:

- Application classes should be organized by feature
- Infrastructure classes should be organized by technology
- Presentation classes should be organized by UI component
- Domain classes should be organized by DDD concept

### 4. ConsolidatedNamingConventionsTest

Tests for consistent naming conventions across the codebase:

- Controllers should have "Controller" suffix
- ViewModels should have "ViewModel" suffix
- Repositories should have "Repository" suffix
- Services should have "Service" suffix
- Use cases should have "UseCase", "Command", "Query", or "Handler" suffix
- Exceptions should have "Exception" suffix
- Factories should have "Factory" suffix
- Coordinators should have "Coordinator" suffix
- Interfaces should not have "I" prefix or "Interface" suffix
- Events should have "Event" suffix
- Specifications should have "Specification" suffix

## Running the Tests

You can run all architecture tests using Maven:
