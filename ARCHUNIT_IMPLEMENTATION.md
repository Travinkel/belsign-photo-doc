# ArchUnit Implementation

## Overview

This document describes the implementation of ArchUnit in the BelSign Photo Documentation Module project. ArchUnit is a Java library for testing the architecture of your Java code, allowing you to check if your code adheres to architectural rules and constraints.

## Changes Made

1. Added ArchUnit dependency to `pom.xml`:
   ```xml
   <!-- ArchUnit for architecture testing -->
   <dependency>
       <groupId>com.tngtech.archunit</groupId>
       <artifactId>archunit-junit5</artifactId>
       <version>1.0.1</version>
       <scope>test</scope>
   </dependency>
   ```

2. Created architecture test classes in `src/test/java/com/belman/architecture/`:
   - `LayerDependencyTest.java`: Enforces clean architecture layer dependencies
   - `PackageDependencyTest.java`: Enforces specific package dependencies
   - `NamingConventionTest.java`: Enforces naming conventions
   - `GluonArchitectureTest.java`: Enforces Gluon Mobile specific architectural rules
   - `DddArchitectureTest.java`: Enforces Domain-Driven Design principles
   - `CleanArchitectureTest.java` (formerly `OnionArchitectureTest.java`): Enforces Clean Architecture / Onion Architecture principles

3. Created a README.md file in the architecture test package to document the tests and how to use them.

## Test Results

Running the architecture tests revealed several violations of clean architecture principles in the current codebase:

### Layer Dependency Violations

- Domain layer depends on presentation and infrastructure layers
- Application layer depends on presentation layer
- Infrastructure layer depends on presentation layer

These violations go against clean architecture principles where dependencies should only point inward (towards the domain layer).

### Package Dependency Violations

- Some controllers are not in the presentation layer (e.g., RoleBasedAccessController)
- Some service implementations are not in the application or infrastructure layers
- Some service interfaces are not in the domain layer
- Some view models are not in the presentation layer (e.g., QCReportViewModel)

### Naming Convention Violations

- The Inject interface starts with "I", which violates the naming convention that interfaces should not have an "I" prefix

## Benefits of ArchUnit

1. **Automated Architecture Verification**: ArchUnit allows you to automatically verify that your code adheres to your architectural rules.

2. **Early Detection of Violations**: Architecture violations are detected early in the development process, making them easier to fix.

3. **Documentation as Code**: Architectural rules are expressed as code, making them executable and always up-to-date.

4. **Improved Code Quality**: Enforcing architectural rules leads to better code organization and maintainability.

5. **Easier Onboarding**: New developers can quickly understand the architectural rules by looking at the ArchUnit tests.

## Next Steps

1. **Fix Violations**: Address the architectural violations identified by the ArchUnit tests.

2. **Integrate with CI/CD**: Run ArchUnit tests as part of the continuous integration pipeline.

3. **Refine Rules**: Refine the architectural rules based on project needs and feedback.

4. **Monitor Compliance**: Regularly run the tests to ensure continued compliance with architectural principles.

## Conclusion

ArchUnit has been successfully installed and used to test the architecture of the BelSign Photo Documentation Module. The tests have revealed several architectural violations that should be addressed to improve the codebase's adherence to clean architecture principles.

Additional tests have been implemented to enforce Domain-Driven Design (DDD) principles, Clean Architecture / Onion Architecture patterns, and other best practices. The project follows:

- **Clean Architecture / Onion Architecture** for overall structure
- **MVVM+C (Model-View-ViewModel + Coordinator)** for UI architecture
- **SOLID principles** for object-oriented design
- **SRP (Single Responsibility Principle)** for focused components
- **Other best practices** for maintainable and testable code

These tests ensure that:

1. **DDD Concepts**: Aggregates, entities, value objects, domain events, and specifications are implemented correctly and placed in the appropriate packages.

2. **Clean Architecture / Onion Architecture**: Dependencies flow inward, with the domain layer at the center, and infrastructure implementations properly implement domain interfaces.

3. **Gluon Mobile Integration**: Gluon Mobile components are used correctly, with UI components only in the presentation layer and platform-specific code isolated from the domain.

## Recent Architectural Improvements

Several architectural improvements have been made to address violations identified by the ArchUnit tests:

1. **BaseService Refactoring**: The `BaseService` class was duplicated in both the presentation and application layers, creating confusion and architectural violations. The following changes were made:
   - Identified application.core.BaseService as the canonical version
   - Updated all references in domain and infrastructure layers to use application.core.BaseService
   - Refactored presentation.core.BaseService to extend application.core.BaseService and marked it as deprecated

2. **Domain Layer Independence**: Fixed violations where domain layer classes were depending on presentation layer components:
   - Updated AccessPolicyFactory, RoleBasedAccessControlFactory, and RoleBasedAccessController to use application.core.BaseService

3. **Infrastructure Layer Improvements**: Fixed violations where infrastructure layer classes were depending on presentation layer components:
   - Updated GluonCameraService, InMemoryCustomerRepository, DefaultAuthenticationService, MockCameraService, and SessionManager to use application.core.BaseService

4. **Test Code Alignment**: Updated test code to use the correct architectural components:
   - Updated ServiceRegistryTest to use application.core.BaseService
   - Updated BaseServiceTest to use application.core.BaseService instead of presentation.core.BaseService

These changes have significantly improved the project's adherence to clean architecture principles, particularly the Onion Architecture pattern where dependencies should flow inward toward the domain layer.

## Remaining Architectural Issues

Despite the improvements made, there are still some systemic architectural violations that would require more extensive refactoring:

1. **Infrastructure Layer Depending on Presentation Layer**: The `RouteGuardInitializer` class in the infrastructure layer imports and uses several components from the presentation layer, including the Router and various view classes. This violates clean architecture principles.

2. **Application Layer Depending on Presentation Layer**: Several classes in the application layer import presentation components like Router, TransitionPresets, ViewTransition, BaseController, BaseView, BaseViewModel, and ViewLoader.

### Recommendations for Further Refactoring

1. **Move UI-Related Infrastructure Components to Presentation Layer**: Components like `RouteGuardInitializer` that are primarily concerned with UI routing and views should be moved to the presentation layer.

2. **Create Abstractions for Cross-Layer Communication**: Use interfaces and the dependency inversion principle to allow higher layers to define contracts that lower layers implement.

3. **Implement a Mediator Pattern**: Use a mediator to handle communication between layers without direct dependencies.

4. **Use Events for Cross-Layer Communication**: Implement an event system that allows lower layers to publish events that higher layers can subscribe to.

These comprehensive tests will help maintain architectural integrity as the project evolves, and the recommendations above provide a roadmap for further improving the architecture.
