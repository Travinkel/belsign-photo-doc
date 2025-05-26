# Belsign Photo Documentation - Test Guide

This guide provides instructions for running different types of tests in the Belsign Photo Documentation project.

## Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- Git (for accessing the repository)

## Test Categories

The project includes several categories of tests:

1. **Unit Tests**: Test individual components in isolation
2. **Integration Tests**: Test interactions between components
3. **Architecture Tests**: Verify architectural constraints
4. **Clean Code Tests**: Ensure code quality and maintainability
5. **UI Tests**: Test the user interface and user interactions

## Running Tests

### Using Maven

#### Run All Tests

```bash
mvn test
```

#### Run Tests in a Specific Package

```bash
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

```bash
mvn test -Dtest=com.belman.unit.presentation.usecases.worker.photocube.PhotoCubeViewModelTest
```

#### Run a Specific Test Method

```bash
mvn test -Dtest=com.belman.unit.presentation.usecases.worker.photocube.PhotoCubeViewModelTest#testInitialization
```

### Using an IDE

1. **IntelliJ IDEA**:
   - Right-click on a test class or method and select "Run"
   - Use the test runner panel to run, debug, or rerun tests
   - Configure test run configurations for frequently run tests

2. **Eclipse**:
   - Right-click on a test class or method and select "Run As > JUnit Test"
   - Use the JUnit panel to view test results
   - Create run configurations for test suites

## Generating Test Reports

### Maven Surefire Reports

Maven Surefire automatically generates XML and text reports when you run tests:

```bash
# Run tests and generate reports
mvn test

# Reports are generated in:
# target/surefire-reports/
```

### Custom HTML/PDF Reports

The project includes a custom report generator that converts Surefire XML reports to HTML or PDF:

```bash
# Compile the report converter
javac -d target/classes src/test/java/com/belman/test/reporting/XmlReportConverter.java

# Generate HTML reports
java -cp target/classes com.belman.test.reporting.XmlReportConverter --format=html

# Generate PDF reports
java -cp target/classes com.belman.test.reporting.XmlReportConverter --format=pdf

# Reports are generated in:
# test-reports/
```

### JaCoCo Coverage Reports

Generate code coverage reports using JaCoCo:

```bash
# Generate JaCoCo reports
mvn jacoco:report

# Reports are generated in:
# target/site/jacoco/
```

## Continuous Integration

The project uses GitHub Actions for continuous integration:

1. Tests run automatically on push to main/develop branches
2. Tests run on pull requests to main/develop branches
3. Test reports are generated and published as artifacts
4. Code coverage reports are generated and published

You can view the CI workflow configuration in:
`.github/workflows/test-workflow.yml`

## Writing Tests

### Unit Test Example

```java
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

### Integration Test Example

```java
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

## Debugging Tests

### Debug Logging

Add debug logs to your tests with the "[DEBUG_LOG]" prefix:

```java
System.out.println("[DEBUG_LOG] Current value: " + value);
```

These logs will be visible in the console output and can help diagnose test failures.

### Remote Debugging

To debug tests remotely:

```bash
mvn -Dmaven.surefire.debug test
```

This will pause execution until a debugger is attached to port 5005.

## Test Coverage Requirements

The project has the following test coverage targets:

1. **Business Logic**: 90% line coverage
2. **Service Layer**: 80% line coverage
3. **Presentation Layer**: 70% line coverage
4. **Overall Project**: 75% line coverage

You can check current coverage by generating JaCoCo reports.

## Troubleshooting

### Common Issues

1. **Tests fail with "Toolkit not initialized" error**:
   - This is a JavaFX initialization issue
   - Use the `@ExtendWith(JavaFXInitializer.class)` annotation on your test class

2. **Database tests fail with "No suitable driver found"**:
   - Ensure the JDBC driver is in the classpath
   - Check that the database URL is correct
   - Verify that TestDatabaseUtil is properly initialized

3. **OutOfMemoryError during tests**:
   - Increase Maven memory: `export MAVEN_OPTS="-Xmx1024m"`
   - Check for memory leaks in test code

### Getting Help

If you encounter issues running tests:

1. Check the test logs for error messages
2. Review the testing strategy document
3. Consult the project documentation
4. Ask for help from the development team

## Conclusion

This guide provides the basic information needed to run and understand tests in the Belsign Photo Documentation project. For more detailed information, refer to the testing strategy document and the project documentation.