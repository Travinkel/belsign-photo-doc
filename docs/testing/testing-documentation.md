# Belsign Photo Documentation - Testing Documentation

This document provides comprehensive information about testing in the Belsign Photo Documentation project, including testing strategy, guides for running tests, and test coverage requirements.

## Table of Contents

1. [Testing Strategy](#testing-strategy)
2. [Running Tests Guide](#running-tests-guide)
3. [Test Coverage Requirements](#test-coverage-requirements)
4. [Troubleshooting](#troubleshooting)

---

## Testing Strategy

### Overview

This section outlines the testing strategy for the Belsign Photo Documentation project. It provides guidelines for different types of tests, testing tools, and best practices to ensure the quality and reliability of the application.

### Testing Objectives

The primary objectives of our testing strategy are:

1. **Ensure Functionality**: Verify that all features work as specified in the requirements
2. **Maintain Quality**: Prevent regressions and ensure code quality
3. **Improve Reliability**: Identify and fix issues before they reach production
4. **Support Refactoring**: Enable safe refactoring and code improvements
5. **Document Behavior**: Tests serve as executable documentation of expected behavior

### Test Categories

The project employs a comprehensive testing approach with different categories of tests:

#### 1. Unit Tests

**Purpose**: Test individual components in isolation.

**Location**: `src/test/java/com/belman/unit`

**Organization**:
- Organized by layer (presentation, service, bootstrap)
- Further organized by domain areas (admin, worker, security)

**Tools**:
- JUnit 5 for test execution
- Mockito for mocking dependencies

**Best Practices**:
- Focus on testing a single unit of functionality
- Mock all external dependencies
- Aim for high coverage of business logic
- Keep tests fast and independent

#### 2. Integration Tests

**Purpose**: Test interactions between components.

**Location**: `src/test/java/com/belman/integration`

**Organization**:
- Database integration tests
- Service integration tests
- Worker flow integration tests
- Order processing integration tests

**Tools**:
- JUnit 5 for test execution
- Test database with known data

**Best Practices**:
- Initialize the test database with `TestDatabaseUtil.initializeTestDatabase()`
- Reset the database before each test with `TestDatabaseUtil.resetTestDatabase()`
- Shutdown the database after tests with `TestDatabaseUtil.shutdownTestDatabase()`
- Test complete workflows across multiple components

#### 3. Architecture Tests

**Purpose**: Verify architectural constraints.

**Location**: `src/test/java/com/belman/architecture`

**Organization**:
- Three-layer architecture tests
- MVVM pattern tests
- Antipattern tests

**Tools**:
- ArchUnit for defining and testing architectural rules

**Best Practices**:
- Define clear architectural boundaries
- Test for unwanted dependencies between layers
- Verify that patterns are correctly implemented

#### 4. Clean Code Tests

**Purpose**: Ensure code quality and maintainability.

**Location**: `src/test/java/com/belman/cleancode`

**Organization**:
- Code complexity tests
- Design pattern tests
- Exception handling tests

**Tools**:
- Custom test utilities for measuring complexity
- Reflection-based tests for pattern verification

**Best Practices**:
- Set clear thresholds for code complexity
- Verify proper implementation of design patterns
- Ensure consistent exception handling

#### 5. UI Testing

**Purpose**: Test the user interface and user interactions.

**Location**: `src/test/java/com/belman/ui`

**Challenges**:
- JavaFX Toolkit initialization issues in JUnit
- Mockito limitations with JavaFX classes
- Property binding complexities

**Approach**:
1. Test the ViewModel logic without JavaFX dependencies
2. Create testable implementations for JavaFX components
3. Test controllers using reflection and avoiding JavaFX mocking
4. Create simplified tests focusing on core logic

---

## Running Tests Guide

This section provides instructions for running different types of tests in the Belsign Photo Documentation project.

### Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- Git (for accessing the repository)

### Using Maven

#### Run All Tests

To run all tests in the project:

```
mvn test
```

#### Run Tests in a Specific Package

To run tests in specific packages:

```
# Run all unit tests
mvn test -Dtest=com.belman.unit.*

# Run all integration tests
mvn test -Dtest=com.belman.integration.*

# Run all architecture tests
mvn test -Dtest=com.belman.architecture.*

# Run all clean code tests
mvn test -Dtest=com.belman.cleancode.*
```

#### Run a Specific Test Class

To run a specific test class:

```
mvn test -Dtest=com.belman.unit.presentation.usecases.worker.photocube.PhotoCubeViewModelTest
```

#### Run a Specific Test Method

To run a specific test method:

```
mvn test -Dtest=com.belman.unit.presentation.usecases.worker.photocube.PhotoCubeViewModelTest#testInitialization
```

### Using an IDE

#### IntelliJ IDEA

1. Right-click on a test class or method and select "Run"
2. Use the test runner panel to run, debug, or rerun tests
3. Configure test run configurations for frequently run tests

#### Eclipse

1. Right-click on a test class or method and select "Run As > JUnit Test"
2. Use the JUnit panel to view test results
3. Create run configurations for test suites

### Generating Test Reports

#### Maven Surefire Reports

Maven Surefire automatically generates XML and text reports when you run tests:

```
# Run tests and generate reports
mvn test

# Reports are generated in:
# target/surefire-reports/
```

#### Custom HTML/PDF Reports

The project includes a custom report generator that converts Surefire XML reports to HTML or PDF:

```
# Compile the report converter
javac -d target/classes src/test/java/com/belman/test/reporting/XmlReportConverter.java

# Generate HTML reports
java -cp target/classes com.belman.test.reporting.XmlReportConverter --format=html

# Generate PDF reports
java -cp target/classes com.belman.test.reporting.XmlReportConverter --format=pdf

# Reports are generated in:
# test-reports/
```

#### JaCoCo Coverage Reports

Generate code coverage reports using JaCoCo:

```
# Generate JaCoCo reports
mvn jacoco:report

# Reports are generated in:
# target/site/jacoco/
```

### Continuous Integration

The project uses GitHub Actions for continuous integration:

1. Tests run automatically on push to main/develop branches
2. Tests run on pull requests to main/develop branches
3. Test reports are generated and published as artifacts
4. Code coverage reports are generated and published

You can view the CI workflow configuration in:
`.github/workflows/test-workflow.yml`

### Writing Tests

#### Unit Test Example

Here's an example of a simple unit test:

```
package com.belman.unit.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimpleTest {

    @Test
    public void testStringConcatenation() {
        String part1 = "Hello";
        String part2 = "World";
        String result = part1 + " " + part2;

        assertEquals("Hello World", result, "String concatenation should work correctly");
        System.out.println("[DEBUG_LOG] String concatenation test passed");
    }
}
```

#### Integration Test Example

Here's an example of a simple integration test:

```
package com.belman.integration.example;

import com.belman.test.util.TestDatabaseUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseIntegrationTest {

    @BeforeEach
    public void setUp() {
        TestDatabaseUtil.initializeTestDatabase();
    }

    @AfterEach
    public void tearDown() {
        TestDatabaseUtil.resetTestDatabase();
    }

    @Test
    public void testDatabaseConnection() {
        boolean isConnected = TestDatabaseUtil.isConnected();
        assertTrue(isConnected, "Database should be connected");
    }
}
```

### Debugging Tests

#### Debug Logging

Add debug logs to your tests with the "[DEBUG_LOG]" prefix:

```
System.out.println("[DEBUG_LOG] Current value: " + value);
```

These logs will be visible in the console output and can help diagnose test failures.

#### Remote Debugging

To debug tests remotely:

```
mvn -Dmaven.surefire.debug test
```

This will pause execution until a debugger is attached to port 5005.

---

## Test Coverage Requirements

This section outlines the test coverage requirements for the Belsign Photo Documentation project.

### Coverage Targets

The project aims for the following test coverage targets:

1. **Business Logic**: 90% line coverage
2. **Service Layer**: 80% line coverage
3. **Presentation Layer**: 70% line coverage
4. **Overall Project**: 75% line coverage

### Measuring Coverage

Coverage is measured using JaCoCo and reported in the CI pipeline. To generate coverage reports locally:

```
mvn jacoco:report
```

The reports will be available in `target/site/jacoco/`.

### Coverage Priorities

When writing tests, prioritize coverage in the following order:

1. **Critical Business Logic**: Core domain logic and business rules
2. **Error Handling**: Exception paths and error conditions
3. **Edge Cases**: Boundary conditions and special cases
4. **Happy Paths**: Standard usage scenarios

### Excluded from Coverage Requirements

The following areas are excluded from coverage requirements:

1. **Generated Code**: Auto-generated code
2. **Simple Getters/Setters**: Basic accessor methods
3. **UI Initialization Code**: JavaFX initialization code
4. **External Library Integration**: Code that primarily integrates with external libraries

### Coverage Enforcement

Coverage is enforced through:

1. **CI Pipeline**: Coverage reports are generated in the CI pipeline
2. **Pull Request Reviews**: Coverage is reviewed during code reviews
3. **JaCoCo Minimum Rules**: JaCoCo is configured with minimum coverage thresholds

---

## Troubleshooting

### Common Issues

#### Tests fail with "Toolkit not initialized" error

This is a JavaFX initialization issue. Use the `@ExtendWith(JavaFXInitializer.class)` annotation on your test class.

#### Database tests fail with "No suitable driver found"

- Ensure the JDBC driver is in the classpath
- Check that the database URL is correct
- Verify that TestDatabaseUtil is properly initialized

#### OutOfMemoryError during tests

- Increase Maven memory: `export MAVEN_OPTS="-Xmx1024m"`
- Check for memory leaks in test code

### Getting Help

If you encounter issues running tests:

1. Check the test logs for error messages
2. Review this testing documentation
3. Consult the project documentation
4. Ask for help from the development team

---

## Conclusion

This documentation provides comprehensive information about testing in the Belsign Photo Documentation project. By following these guidelines, we can maintain high standards of code quality, prevent regressions, and support ongoing development and refactoring efforts.