# Architecture Tests

This package contains architecture tests that verify the codebase follows the defined architectural rules and patterns. These tests help maintain the architectural integrity of the codebase and detect architectural antipatterns.

## Test Categories

The architecture tests are organized into the following categories:

### 1. Module Tests

Located in `com.belman.architecture.rules.module`:

- **RichBusinessEntitiesTest**: Verifies that domain classes follow the rich business entities pattern.
- **DependencyValidationTest**: Verifies that dependencies between layers follow the defined rules.
- **BootstrapLayerTest**: Verifies that bootstrap code follows the correct patterns.
- **CommonPackageTest**: Verifies that common code follows the correct patterns.
- **ServiceLayerUseCaseTest**: Verifies that service layer usecases follow the correct patterns.

### 2. Three-Layer Architecture Tests

Located in `com.belman.architecture.rules.threelayer`:

- **ThreeLayerArchitectureTest**: Verifies that the codebase follows the three-layer architecture pattern.

### 3. Presentation Tests

Located in `com.belman.architecture.rules.presentation`:

- **MVVMAndPresentationRulesTest**: Verifies that the UI layer follows the MVVM pattern.
- **MVVMCArchitectureTest**: Verifies that the UI layer follows the MVVMC pattern.

### 4. Antipattern Tests

Located in `com.belman.architecture.rules.antipattern`:

- **ArchitecturalAntipatternTest**: Detects common architectural antipatterns in the codebase.

## Running the Tests

You can run the architecture tests using the following command:

```bash
mvn test -Dtest=com.belman.architecture.rules.*
```

To run a specific test category, use:

```bash
mvn test -Dtest=com.belman.architecture.rules.module.*
mvn test -Dtest=com.belman.architecture.rules.threelayer.*
mvn test -Dtest=com.belman.architecture.rules.presentation.*
mvn test -Dtest=com.belman.architecture.rules.antipattern.*
```

To run a specific test, use:

```bash
mvn test -Dtest=com.belman.architecture.rules.antipattern.ArchitecturalAntipatternTest
```

## Interpreting Test Results

When a test fails, it provides detailed information about the architectural violations:

1. The rule that was violated
2. The classes or methods that violated the rule
3. The reason why the rule was violated

For example:

```
Architecture Violation [Priority: MEDIUM] - Rule 'classes that are not interfaces and are not enums and are not annotated with @Test and have name not matching '.*Test' should not be a god class, because God classes violate the Single Responsibility Principle' was violated (15 times):
Class com.belman.domain.order.OrderBusiness is a god class with 27 methods and 9 fields
...
```

## Fixing Architectural Violations

When you encounter architectural violations, you should:

1. Understand the rule that was violated
2. Identify the classes or methods that violated the rule
3. Refactor the code to fix the violation

For detailed recommendations on fixing architectural antipatterns, see the [ARCHITECTURAL_ANTIPATTERNS.md](../../../../../../../../ARCHITECTURAL_ANTIPATTERNS.md) file in the project root.

## Adding New Architecture Tests

To add a new architecture test:

1. Create a new test class in the appropriate package
2. Use ArchUnit to define the architectural rules
3. Run the test to verify the rules

Example:

```java
@Test
public void servicesShouldNotDependOnRepositoryImplementations() {
    ArchRule rule = noClasses()
            .that().resideInAPackage("com.belman.service..")
            .should().dependOnClassesThat().resideInAPackage("com.belman.repository.persistence..")
            .because("Services should depend on repository interfaces, not implementations");

    rule.check(importedClasses);
}
```

## Resources

- [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html)
- [ArchUnit API Documentation](https://javadoc.io/doc/com.tngtech.archunit/archunit/latest/index.html)