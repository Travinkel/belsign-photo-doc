# Database Testing Approach for Belsign Photo Documentation System

This document outlines the approach for testing the database components of the Belsign Photo Documentation System.

## Overview

The Belsign Photo Documentation System uses a SQLite database for persistence. The database schema includes tables for users, user roles, customers, orders, photos, photo templates, photo annotations, reports, and report photos.

For testing purposes, we've implemented a separate test database that can be reset between tests to ensure a clean state. This approach allows us to run integration tests against a real database without affecting the main application database.

## Test Database Structure

The test database is a separate SQLite database file located at `src/test/resources/sqlitedb/testdb.db`. It is created and initialized using Flyway migrations, which apply the same schema as the main database.

The test database is populated with test data using a dedicated migration script (`V1000__Insert_Test_Data.sql`) that inserts test data into all tables.

## Test Utilities

The `TestDatabaseUtil` class provides utilities for working with the test database:

- `initializeTestDatabase()`: Initializes the test database by creating a new database file, running migrations, and returning a DataSource.
- `resetTestDatabase()`: Resets the test database by cleaning and re-running migrations, ensuring a clean state for each test.
- `executeSql(String sql)`: Executes a SQL script on the test database.
- `shutdownTestDatabase()`: Shuts down the test database connection.

## Test Data

The test data includes:

- 3 users with different roles (admin, production, qa)
- 3 customers (2 businesses, 1 individual)
- 3 orders in different states (pending, in progress, completed)
- 4 photo templates (top view, side view, front view, back view)
- 4 photos with different statuses (pending, approved, rejected)
- 3 photo annotations
- 3 reports with different statuses (pending, approved, rejected)
- 4 report-photo associations

All test data uses fixed UUIDs to ensure consistent relationships between tables.

## Using the Test Database in Integration Tests

To use the test database in integration tests, follow these steps:

1. Initialize the test database in a `@BeforeAll` method
2. Reset the database before each test in a `@BeforeEach` method
3. Shut down the test database in an `@AfterAll` method
4. Use the DataSource to get a connection to the test database in your tests

See `DatabaseIntegrationTest` for a complete example of how to use the test database in integration tests.

## Test Images

The test data references test images in the `src/test/resources/mock/camera/` directory. These images need to be created manually or copied from the main resources directory.

## Adding New Tests

When adding new tests:

1. Ensure that the test database is reset before each test to maintain a clean state.
2. Use fixed UUIDs for test data to ensure consistent relationships between tables.
3. Verify that the test data is correctly inserted and that relationships between tables are maintained.
4. Use the `TestDatabaseUtil` class to interact with the test database.

## Troubleshooting

If you encounter issues with the test database:

1. Check that the test database file exists at `src/test/resources/sqlitedb/testdb.db`.
2. Verify that the Flyway migrations are being applied correctly.
3. Check that the test data is being inserted correctly.
4. Ensure that the test database is being reset before each test.
5. Check for any errors in the console output.

## Conclusion

This testing approach provides a robust way to test the database components of the Belsign Photo Documentation System. By using a separate test database that can be reset between tests, we can ensure that each test runs in a clean environment without affecting the main application database.
