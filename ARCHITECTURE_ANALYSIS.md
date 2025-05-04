# Architecture Analysis and Recommendations

## Overview

This document provides an analysis of the current architecture of the BelSign Photo Documentation Module and recommendations for improving it to better adhere to clean architecture principles. The analysis is based on the results of ArchUnit tests that were implemented to enforce architectural rules.

## Current Architecture Issues

The ArchUnit tests have revealed several architectural violations in the codebase:

### Layer Dependency Violations

1. **Domain Layer Dependencies**: The domain layer depends on the application and infrastructure layers, which violates the clean architecture principle that the domain layer should be independent of outer layers.
   - Domain classes extend `BaseService` from the presentation layer
   - Domain classes directly use `EmojiLogger` from the infrastructure layer
   - Domain classes are annotated with `@Inject` from the application layer

2. **Application Layer Dependencies**: The application layer depends on the presentation and infrastructure layers, which violates the clean architecture principle that inner layers should not depend on outer layers.
   - Application classes extend `BaseService` from the presentation layer
   - Application classes directly use `EmojiLogger` from the infrastructure layer
   - Application classes directly reference presentation layer components

3. **Infrastructure Layer Dependencies**: The infrastructure layer depends on the presentation layer, which violates the clean architecture principle that infrastructure should only depend on the domain and application layers.
   - Infrastructure classes extend `BaseService` from the presentation layer
   - Infrastructure classes directly reference presentation layer components

### Package Dependency Violations

1. **Controller Placement**: Some controllers are not in the presentation layer (e.g., `RoleBasedAccessController`), which violates the rule that controllers should be in the presentation layer.

2. **Service Implementation Placement**: Some service implementations are not in the application or infrastructure layers (e.g., `BaseService`), which violates the rule that service implementations should be in the application or infrastructure layers.

3. **Service Interface Placement**: Some service interfaces are not in the domain layer, which violates the rule that service interfaces should be in the domain layer.

4. **ViewModel Placement**: Some view models are not in the presentation layer (e.g., `QCReportViewModel`), which violates the rule that view models should be in the presentation layer.

### Naming Convention Violations

1. **Interface Naming**: The `Inject` interface starts with "I", which violates the naming convention that interfaces should not have an "I" prefix.

## Recommendations

To address these architectural issues, the following changes are recommended:

### 1. Create Proper Abstractions in the Domain Layer

Create interfaces in the domain layer for all services and repositories:

- **Logger Interface**: Create a `Logger` interface in the domain.services package to abstract logging functionality.
- **Service Interfaces**: Move all service interfaces to the domain.services package.
- **Repository Interfaces**: Ensure all repository interfaces are in the domain.repositories package.

### 2. Move BaseService to the Application Layer

Move the `BaseService` class from the presentation.core package to the application.core package:

- Update `BaseService` to use the `Logger` interface instead of directly using `EmojiLogger`.
- Update all classes that extend `BaseService` to use the new version in the application.core package.

### 3. Create Adapters in the Infrastructure Layer

Create adapter classes in the infrastructure layer that implement the domain interfaces:

- **EmojiLoggerAdapter**: Create an adapter that implements the `Logger` interface and delegates to `EmojiLogger`.
- **Repository Implementations**: Ensure all repository implementations are in the infrastructure.persistence package.
- **Service Implementations**: Ensure all service implementations are in the infrastructure.service package.

### 4. Fix Naming Convention Issues

Rename interfaces and classes to follow naming conventions:

- Rename `Inject` to `DependencyInject` or `ServiceInject` to avoid the "I" prefix.
- Rename `RoleBasedAccessController` to `RoleBasedAccessManager` to avoid confusion with UI controllers.

### 5. Move ViewModels to the Presentation Layer

Move all view models to the presentation layer:

- Move `QCReportViewModel` from the application.qcreport package to the presentation.views.qcreport package.

### 6. Update Domain Classes

Update domain classes to avoid dependencies on outer layers:

- Create a `DomainService` base class in the domain layer that provides common functionality without depending on outer layers.
- Update domain classes to use the `DomainService` base class instead of `BaseService`.

### 7. Update Application Classes

Update application classes to avoid dependencies on the presentation layer:

- Create an `ApplicationService` base class in the application layer that provides common functionality without depending on the presentation layer.
- Update application classes to use the `ApplicationService` base class instead of `BaseService`.

### 8. Update Infrastructure Classes

Update infrastructure classes to avoid dependencies on the presentation layer:

- Create an `InfrastructureService` base class in the infrastructure layer that provides common functionality without depending on the presentation layer.
- Update infrastructure classes to use the `InfrastructureService` base class instead of `BaseService`.

## Implementation Progress

The following changes have been implemented:

1. Created a `Logger` interface in the domain.services package.
2. Created an `EmojiLoggerAdapter` in the infrastructure.logging package.
3. Moved `BaseService` from presentation.core to application.core.
4. Renamed `Inject` to `DependencyInject`.
5. Moved `QCReportViewModel` from application.qcreport to presentation.views.qcreport.
6. Created a new `RoleBasedAccessManager` class in the domain.rbac package.

## Next Steps

To fully address the architectural issues, the following steps are needed:

1. Update all classes that extend `BaseService` from the presentation layer to use the new `BaseService` in the application layer.
2. Update all domain classes to use the `Logger` interface instead of directly using `EmojiLogger`.
3. Create a `DomainService` base class in the domain layer and update domain classes to use it.
4. Update all application classes to avoid dependencies on the presentation layer.
5. Update all infrastructure classes to avoid dependencies on the presentation layer.
6. Run the ArchUnit tests again to verify that the architectural violations have been fixed.

## Conclusion

The BelSign Photo Documentation Module has several architectural issues that violate clean architecture principles. By implementing the recommendations in this document, the codebase can be improved to better adhere to these principles, resulting in a more maintainable and testable application.