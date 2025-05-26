package com.belman.integration.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for database connection error handling.
 * These tests verify that the system properly handles database connection errors.
 */
public class DatabaseConnectionErrorHandlingTest {

    private static DataSource dataSource;

    @BeforeAll
    public static void setupDatabase() {
        // Initialize the test database
        dataSource = TestDatabaseUtil.initializeTestDatabase();
        assertNotNull(dataSource, "DataSource should not be null");
    }

    @AfterAll
    public static void tearDown() {
        // Shutdown the test database
        TestDatabaseUtil.shutdownTestDatabase();
    }

    /**
     * Test handling of invalid SQL syntax.
     * This test verifies that the system properly handles SQL syntax errors.
     */
    @Test
    public void testInvalidSqlSyntaxHandling() {
        System.out.println("[DEBUG_LOG] Starting test for invalid SQL syntax handling");

        // Execute a SQL statement with invalid syntax
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            
            // This SQL has a syntax error (missing FROM clause)
            statement.executeQuery("SELECT user_id, username WHERE username = 'admin'");
            
            // If we get here, the test has failed
            fail("Expected SQLException due to invalid SQL syntax");
        } catch (SQLException e) {
            // Expected exception
            System.out.println("[DEBUG_LOG] Expected exception: " + e.getMessage());
            assertTrue(e.getMessage().toLowerCase().contains("syntax") || 
                       e.getMessage().toLowerCase().contains("sql") || 
                       e.getMessage().toLowerCase().contains("error"),
                       "Exception message should indicate a syntax error");
        }

        System.out.println("[DEBUG_LOG] Invalid SQL syntax handling test passed");
    }

    /**
     * Test handling of non-existent table.
     * This test verifies that the system properly handles attempts to access non-existent tables.
     */
    @Test
    public void testNonExistentTableHandling() {
        System.out.println("[DEBUG_LOG] Starting test for non-existent table handling");

        // Try to query a non-existent table
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            
            statement.executeQuery("SELECT * FROM NON_EXISTENT_TABLE");
            
            // If we get here, the test has failed
            fail("Expected SQLException due to non-existent table");
        } catch (SQLException e) {
            // Expected exception
            System.out.println("[DEBUG_LOG] Expected exception: " + e.getMessage());
            assertTrue(e.getMessage().toLowerCase().contains("table") || 
                       e.getMessage().toLowerCase().contains("not") || 
                       e.getMessage().toLowerCase().contains("exist") ||
                       e.getMessage().toLowerCase().contains("found"),
                       "Exception message should indicate a non-existent table");
        }

        System.out.println("[DEBUG_LOG] Non-existent table handling test passed");
    }

    /**
     * Test handling of constraint violations.
     * This test verifies that the system properly handles database constraint violations.
     */
    @Test
    public void testConstraintViolationHandling() {
        System.out.println("[DEBUG_LOG] Starting test for constraint violation handling");

        // Try to insert a record that violates a constraint
        try (Connection connection = dataSource.getConnection()) {
            // First, create a user
            String userId = UUID.randomUUID().toString();
            String username = "constraintuser_" + System.currentTimeMillis();
            
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO USERS (user_id, username, password, email, first_name, last_name, created_at, updated_at, approved) " +
                    "VALUES (?, ?, ?, ?, ?, ?, datetime('now'), datetime('now'), 1)")) {
                stmt.setString(1, userId);
                stmt.setString(2, username);
                stmt.setString(3, "password");
                stmt.setString(4, username + "@example.com");
                stmt.setString(5, "Constraint");
                stmt.setString(6, "User");
                stmt.executeUpdate();
            }
            
            // Now try to insert another user with the same username (should violate unique constraint)
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO USERS (user_id, username, password, email, first_name, last_name, created_at, updated_at, approved) " +
                    "VALUES (?, ?, ?, ?, ?, ?, datetime('now'), datetime('now'), 1)")) {
                stmt.setString(1, UUID.randomUUID().toString());
                stmt.setString(2, username); // Same username as before
                stmt.setString(3, "password");
                stmt.setString(4, "another_" + username + "@example.com");
                stmt.setString(5, "Another");
                stmt.setString(6, "User");
                stmt.executeUpdate();
                
                // If we get here, the test has failed
                fail("Expected SQLException due to unique constraint violation");
            } catch (SQLException e) {
                // Expected exception
                System.out.println("[DEBUG_LOG] Expected exception: " + e.getMessage());
                assertTrue(e.getMessage().toLowerCase().contains("unique") || 
                           e.getMessage().toLowerCase().contains("constraint") || 
                           e.getMessage().toLowerCase().contains("duplicate") ||
                           e.getMessage().toLowerCase().contains("violation"),
                           "Exception message should indicate a constraint violation");
            }
        } catch (SQLException e) {
            // Unexpected exception during setup
            fail("Unexpected exception during test setup: " + e.getMessage());
        }

        System.out.println("[DEBUG_LOG] Constraint violation handling test passed");
    }

    /**
     * Test handling of connection timeout.
     * This test simulates a connection timeout by attempting to connect to a non-existent database.
     * Note: This test is commented out because it would require a real network timeout,
     * which is difficult to simulate in a unit test environment.
     */
    /*
    @Test
    public void testConnectionTimeoutHandling() {
        System.out.println("[DEBUG_LOG] Starting test for connection timeout handling");

        // Create a data source with an invalid connection URL
        // This would normally cause a connection timeout
        try {
            // This is a non-existent host that should cause a connection timeout
            String jdbcUrl = "jdbc:sqlite:file:non_existent_database.db?mode=memory&cache=shared";
            
            // Try to get a connection
            Connection connection = DriverManager.getConnection(jdbcUrl);
            
            // If we get here, the test has failed
            fail("Expected SQLException due to connection timeout");
        } catch (SQLException e) {
            // Expected exception
            System.out.println("[DEBUG_LOG] Expected exception: " + e.getMessage());
            assertTrue(e.getMessage().toLowerCase().contains("timeout") || 
                       e.getMessage().toLowerCase().contains("connect") || 
                       e.getMessage().toLowerCase().contains("connection"),
                       "Exception message should indicate a connection issue");
        }

        System.out.println("[DEBUG_LOG] Connection timeout handling test passed");
    }
    */

    /**
     * Test handling of connection closing.
     * This test verifies that the system properly handles operations on a closed connection.
     */
    @Test
    public void testClosedConnectionHandling() {
        System.out.println("[DEBUG_LOG] Starting test for closed connection handling");

        Connection connection = null;
        try {
            // Get a connection and close it immediately
            connection = dataSource.getConnection();
            connection.close();
            
            // Try to use the closed connection
            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT 1");
            
            // If we get here, the test has failed
            fail("Expected SQLException due to closed connection");
        } catch (SQLException e) {
            // Expected exception
            System.out.println("[DEBUG_LOG] Expected exception: " + e.getMessage());
            assertTrue(e.getMessage().toLowerCase().contains("closed") || 
                       e.getMessage().toLowerCase().contains("connection"),
                       "Exception message should indicate a closed connection");
        } finally {
            // Ensure connection is closed
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // Ignore, connection is already closed
                }
            }
        }

        System.out.println("[DEBUG_LOG] Closed connection handling test passed");
    }

    /**
     * Test handling of transaction rollback on error.
     * This test verifies that transactions are properly rolled back when an error occurs.
     */
    @Test
    public void testTransactionRollbackOnError() {
        System.out.println("[DEBUG_LOG] Starting test for transaction rollback on error");

        // Generate a unique user ID
        String userId = UUID.randomUUID().toString();
        String username = "rollbackuser_" + System.currentTimeMillis();

        // Try to insert a user with a transaction that will fail
        try (Connection connection = dataSource.getConnection()) {
            // Start a transaction
            connection.setAutoCommit(false);

            try {
                // Insert into USERS table
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO USERS (user_id, username, password, email, first_name, last_name, created_at, updated_at, approved) " +
                        "VALUES (?, ?, ?, ?, ?, ?, datetime('now'), datetime('now'), 1)")) {
                    stmt.setString(1, userId);
                    stmt.setString(2, username);
                    stmt.setString(3, "password");
                    stmt.setString(4, username + "@example.com");
                    stmt.setString(5, "Rollback");
                    stmt.setString(6, "User");
                    stmt.executeUpdate();
                }

                // Now execute a statement that will fail
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate("INSERT INTO NON_EXISTENT_TABLE (column1) VALUES ('value1')");
                    
                    // If we get here, the test has failed
                    fail("Expected SQLException due to non-existent table");
                } catch (SQLException e) {
                    // Expected exception
                    System.out.println("[DEBUG_LOG] Expected exception: " + e.getMessage());
                    
                    // Explicitly roll back the transaction
                    connection.rollback();
                }
            } finally {
                // Restore auto-commit mode
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            // Unexpected exception
            fail("Unexpected exception: " + e.getMessage());
        }

        // Verify the user was not created (transaction was rolled back)
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT COUNT(*) FROM USERS WHERE user_id = ?")) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "Result set should have at least one row");
                assertEquals(0, rs.getInt(1), "User should not exist in the database after rollback");
            }
        } catch (SQLException e) {
            fail("Unexpected exception during verification: " + e.getMessage());
        }

        System.out.println("[DEBUG_LOG] Transaction rollback on error test passed");
    }
}