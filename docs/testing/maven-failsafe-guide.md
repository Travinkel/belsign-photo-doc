# Maven Failsafe Integration Testing Guide

## Overview

This guide explains how to use Maven Failsafe for integration testing in the Belsign Photo Documentation project. Maven Failsafe is a plugin designed to run integration tests, separating them from unit tests that are run by Maven Surefire.

## Why Use Failsafe?

Separating integration tests from unit tests has several benefits:

1. **Build Lifecycle Management**: Integration tests run in a different phase of the Maven build lifecycle, allowing the build to continue even if integration tests fail.
2. **Clearer Separation of Concerns**: Unit tests focus on testing individual components in isolation, while integration tests verify that components work together correctly.
3. **Performance**: Unit tests can run quickly during development, while integration tests (which are often slower) can be run less frequently or on a CI server.
4. **Different Configuration**: Integration tests often require different configuration (e.g., database setup, network access) than unit tests.

## Configuration

The Maven Failsafe plugin is configured in the project's `pom.xml` file:

```xml
<!-- Maven Failsafe plugin for integration test execution -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>3.1.2</version>
    <configuration>
        <includes>
            <include>**/*IT.java</include>
        </includes>
        <excludes>
            <exclude>**/Abstract*.java</exclude>
            <!-- Exclude UI tests that require JavaFX -->
            <exclude>**/presentation/**/*IT.java</exclude>
        </excludes>
        <!-- Configure headless mode for JavaFX -->
        <argLine>
            -Djava.awt.headless=true
            -Dtestfx.robot=glass
            -Dtestfx.headless=true
            -Dprism.order=sw
            -Dprism.text=t2k
            -Dheadless.geometry=1280x720-32
        </argLine>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

The key configuration points are:

- **Includes**: Files matching `**/*IT.java` will be run as integration tests
- **Excludes**: Abstract classes and UI tests are excluded
- **Executions**: The plugin is bound to the `integration-test` and `verify` phases of the Maven build lifecycle

## Naming Convention

Integration tests should follow these naming conventions:

- **File Name**: End with `IT.java` (e.g., `DatabaseOperationsIT.java`)
- **Class Name**: End with `IT` (e.g., `DatabaseOperationsIT`)
- **Package**: Place in the appropriate `com.belman.integration.*` package

## Running Integration Tests

### Running All Integration Tests

To run all integration tests:

```bash
mvn verify
```

This will:
1. Compile the code
2. Run unit tests (with Surefire)
3. Package the application
4. Run integration tests (with Failsafe)

### Running Specific Integration Tests

To run a specific integration test class:

```bash
mvn verify -Dit.test=DatabaseOperationsIT
```

To run a specific test method:

```bash
mvn verify -Dit.test=DatabaseOperationsIT#testCreateUser
```

To run tests in a specific package:

```bash
mvn verify -Dit.test=com.belman.integration.database.*IT
```

### Skipping Tests

To skip unit tests but run integration tests:

```bash
mvn verify -DskipTests
```

To skip integration tests:

```bash
mvn verify -DskipITs
```

To skip all tests:

```bash
mvn verify -DskipTests -DskipITs
```

## Writing Integration Tests

### Example Integration Test

Here's a simple example of an integration test:

```java
package com.belman.integration.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for database operations using Failsafe.
 */
public class DatabaseOperationsIT {

    private static DataSource dataSource;

    @BeforeAll
    public static void setupDatabase() {
        // Initialize the test database
        dataSource = TestDatabaseUtil.initializeTestDatabase();
        assertNotNull(dataSource, "DataSource should not be null");
    }

    @BeforeEach
    public void resetDatabase() {
        // Reset the database before each test
        TestDatabaseUtil.resetTestDatabase();
    }

    @AfterAll
    public static void tearDown() {
        // Shutdown the test database
        TestDatabaseUtil.shutdownTestDatabase();
    }

    @Test
    @DisplayName("Should create a user in the database")
    public void testCreateUser() throws SQLException {
        // Test implementation
    }
}
```

### Best Practices

1. **Use @DisplayName**: Add descriptive names to your tests for better readability in reports
2. **Add Debug Logging**: Use `System.out.println("[DEBUG_LOG] Your message")` for debugging
3. **Clean Up Resources**: Always clean up resources in @AfterEach or @AfterAll methods
4. **Test Real Interactions**: Focus on testing real interactions between components
5. **Isolate Tests**: Each test should be independent and not rely on the state from other tests

## Integration Test Categories

The project includes several categories of integration tests:

1. **Database Integration Tests**: Test interactions with the database
   - Located in `com.belman.integration.database`
   - Example: `DatabaseOperationsIT`

2. **Order Processing Tests**: Test order-related functionality
   - Located in `com.belman.integration.order`
   - Example: `OrderLoadingWithUserRolesIT`

3. **Worker Flow Tests**: Test workflows for production workers
   - Located in `com.belman.integration.worker`
   - Example: `WorkerFlowIntegrationTest`

4. **Service Integration Tests**: Test interactions between services
   - Located in `com.belman.integration.service`
   - Example: `AdminServiceUserRepositoryIntegrationTest`

## Troubleshooting

### Common Issues

1. **Tests Not Running**: Ensure the test class name ends with `IT` and is included in the Failsafe configuration
2. **Database Connection Issues**: Check that the test database is properly initialized
3. **Dependency Injection Problems**: Verify that mock services are properly registered with ServiceLocator

### Debugging Tips

1. Add debug logs with the `[DEBUG_LOG]` prefix:
   ```java
   System.out.println("[DEBUG_LOG] Current value: " + value);
   ```

2. Run with debug output:
   ```bash
   mvn verify -Dit.test=YourTestClass -X
   ```

## Conclusion

Using Maven Failsafe for integration tests helps maintain a clear separation between unit tests and integration tests, making the build process more robust and providing better feedback about different types of test failures.

For more information, refer to:
- [Maven Failsafe Plugin Documentation](https://maven.apache.org/surefire/maven-failsafe-plugin/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- The project's testing strategy document at `docs/testing/testing-strategy.md`