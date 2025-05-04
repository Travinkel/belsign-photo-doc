# ArchUnit Architecture Tests

This package contains architecture tests using ArchUnit to enforce the clean architecture principles described in the project guidelines.

## What is ArchUnit?

ArchUnit is a Java library for testing the architecture of your Java code. It allows you to check if your code adheres to architectural rules and constraints. ArchUnit can be used to:

- Enforce layer dependencies
- Enforce package dependencies
- Enforce naming conventions
- Enforce class and method visibility
- Enforce inheritance hierarchies
- And much more

## Tests in this Package

### LayerDependencyTest

This test enforces the clean architecture layer dependencies:

- Domain layer should not depend on other layers
- Application layer should not depend on presentation or infrastructure layers
- Infrastructure layer should not depend on presentation layer
- Presentation layer can depend on any layer

### PackageDependencyTest

This test enforces specific package dependencies:

- Domain entities should only depend on domain packages
- Domain value objects should be immutable
- Repository implementations should reside in the infrastructure layer
- Repository interfaces should reside in the domain layer
- Service implementations should reside in the application or infrastructure layer
- Service interfaces should reside in the domain layer
- Controllers should reside in the presentation layer
- View models should reside in the presentation layer

### NamingConventionTest

This test enforces naming conventions:

- Controllers should be suffixed with "Controller"
- View models should be suffixed with "ViewModel"
- Services should be suffixed with "Service"
- Repositories should be suffixed with "Repository"
- Entities should not have a suffix
- Value objects should not have a suffix
- Interfaces should not have an "I" prefix or "Interface" suffix
- Exceptions should be suffixed with "Exception"

## Running the Tests

You can run the tests using Maven:

```bash
mvn test -Dtest=com.belman.architecture.*Test
```

Or run individual test classes:

```bash
mvn test -Dtest=com.belman.architecture.LayerDependencyTest
mvn test -Dtest=com.belman.architecture.PackageDependencyTest
mvn test -Dtest=com.belman.architecture.NamingConventionTest
```

## Interpreting Test Results

When a test fails, ArchUnit provides detailed information about the violations:

- Which rule was violated
- Which classes violated the rule
- Where the violation occurred (file and line number)

This information can be used to fix the architectural violations and improve the codebase.

## Customizing the Tests

You can customize the tests by modifying the rules in the test classes. For example, you can:

- Add new rules
- Modify existing rules
- Exclude certain classes or packages from the rules

See the [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html) for more information.