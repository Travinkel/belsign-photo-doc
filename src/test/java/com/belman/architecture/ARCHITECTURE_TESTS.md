# Architecture Tests

This package contains architecture tests using [ArchUnit](https://www.archunit.org/) to enforce architectural
constraints and principles in the BelSign Photo Documentation application. These tests ensure that the code adheres to
the desired architectural patterns and best practices.

## Existing Architecture Tests

The following architecture tests are currently implemented:

### 1. CleanArchitectureTest

Tests that enforce Clean Architecture principles (also known as Onion Architecture):

- Domain layer should not depend on outer layers
- Application layer should not access presentation layer
- Infrastructure implementations should implement domain interfaces

### 2. CleanArchitectureLayerTest

Tests for proper layer dependencies:

- Domain layer should not depend on other layers
- Application layer should not depend on presentation or infrastructure layers
- Infrastructure layer should not depend on presentation layer

### 3. DddArchitectureTest

Tests for Domain-Driven Design principles:

- Entities should have identity
- Value objects should be immutable
- Aggregate roots should be in the aggregates package
- Domain events should be immutable
- Domain services should not have state

### 4. GluonArchitectureTest

Tests for Gluon Mobile specific architectural rules:

- Glisten UI components should only be used in the presentation layer
- Attach services should not be accessed from the domain layer
- JavaFX should not be used in the domain layer

### 5. NamingConventionTest

Tests for naming conventions:

- Controllers should have "Controller" suffix
- View models should have "ViewModel" suffix
- Services should have "Service" suffix
- Repositories should have "Repository" suffix

### 6. PackageDependencyTest

Tests for package dependencies:

- Repository interfaces should be in the domain layer
- Repository implementations should be in the infrastructure layer
- Services should have interfaces in the domain layer

## New Architecture Tests

The following architecture tests have been added to further enhance architectural quality:

### 1. MvvmArchitectureTest

Tests for Model-View-ViewModel architecture pattern:

- ViewModels should reside in the presentation layer
- Views should only depend on ViewModels
- ViewModels should not depend on Views
- Coordinators should manage navigation

### 2. DependencyInjectionTest

Tests for proper dependency injection patterns:

- Domain classes should use interfaces, not concrete implementations
- Service fields should be interfaces, not implementations
- Repository implementations should implement domain interfaces
- Constructors should accept interfaces for dependencies

### 3. ExceptionHandlingTest

Tests for exception handling best practices:

- Exceptions should be suffixed with "Exception"
- Domain exceptions should be in the domain.exceptions package
- Application exceptions should be in the application.exceptions package
- Services should not throw generic exceptions

### 4. SecurityArchitectureTest

Tests for security principles:

- Security utilities should be in security packages
- Sensitive data should not be stored in plaintext
- Only security layer should access credentials
- Secure config must be protected

### 5. MobileArchitectureTest

Tests for mobile-specific architecture:

- Mobile-specific code should be in dedicated packages
- Domain should not depend on mobile libraries
- Camera and storage access should be encapsulated
- Platform-specific code should be isolated

### 6. LoggingArchitectureTest

Tests for logging patterns:

- Loggers should be private static final
- SLF4J should be used for logging
- System.out should not be used for logging
- Logger implementations should be in the infrastructure layer

## Running the Tests

You can run all architecture tests using Maven:

```bash
mvn test -Dtest=com.belman.architecture.*Test
```

Or run specific test classes:

```bash
mvn test -Dtest=com.belman.architecture.CleanArchitectureTest
mvn test -Dtest=com.belman.architecture.MvvmArchitectureTest
```

## Interpreting Test Results

When a test fails, ArchUnit provides detailed information about the violations:

- Which rule was violated
- Which classes violated the rule
- Where the violation occurred

This information can help identify areas where the code needs to be refactored to adhere to architectural principles.

## Adding New Tests

To add new architecture tests:

1. Create a new test class in the `com.belman.architecture` package
2. Use ArchUnit's fluent API to define rules
3. Add the test to this documentation

## Best Practices for Architecture Tests

- Focus on critical architectural boundaries
- Don't overspecify - allow for necessary flexibility
- Keep tests maintainable - don't duplicate rules
- Document the purpose of each test
- Gradually increase architectural constraints as the project matures

## References

- [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html)
- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Domain-Driven Design by Eric Evans](https://domainlanguage.com/ddd/)
- [MVVM Pattern](https://docs.microsoft.com/en-us/xamarin/xamarin-forms/enterprise-application-patterns/mvvm)