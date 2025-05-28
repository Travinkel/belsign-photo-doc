package com.belman.integration.database;

import com.belman.bootstrap.persistence.SqliteDatabaseConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for database operations.
 * These tests verify that CRUD operations on the database work correctly.
 */
public class DatabaseOperationsTest {

    private static final Logger LOGGER = Logger.getLogger(DatabaseOperationsTest.class.getName());
    private static DataSource dataSource;

    @BeforeAll
    public static void setupDatabase() {
        // Initialize the SQLite database using SqliteDatabaseConfig
        System.out.println("[DEBUG_LOG] Initializing database for DatabaseOperationsTest using SqliteDatabaseConfig");
        SqliteDatabaseConfig.initialize();
        dataSource = SqliteDatabaseConfig.getDataSource();
        assertNotNull(dataSource, "DataSource should not be null");
        System.out.println("[DEBUG_LOG] Database initialized successfully for DatabaseOperationsTest");

        // Verify connection works
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("[DEBUG_LOG] Successfully obtained database connection");
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT 1")) {
                    if (rs.next()) {
                        System.out.println("[DEBUG_LOG] Database connection test query executed successfully");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("[DEBUG_LOG] Error testing database connection: " + e.getMessage());
            e.printStackTrace();
        }

        // Create minimal schema for testing
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // Drop existing tables if they exist
            try {
                stmt.execute("DROP TABLE IF EXISTS USER_ROLES");
                stmt.execute("DROP TABLE IF EXISTS USERS");
                System.out.println("[DEBUG_LOG] Dropped existing tables");
            } catch (SQLException e) {
                System.out.println("[DEBUG_LOG] Error dropping tables: " + e.getMessage());
                // Continue with table creation even if drop fails
            }

            // Create USERS table with uppercase name and user_id column to match the schema used in tests
            stmt.execute("CREATE TABLE USERS (" +
                         "user_id TEXT PRIMARY KEY, " +
                         "username TEXT NOT NULL, " +
                         "password TEXT, " +
                         "email TEXT, " +
                         "first_name TEXT, " +
                         "last_name TEXT, " +
                         "status TEXT, " +
                         "created_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                         "updated_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                         "approved INTEGER DEFAULT 0)");

            System.out.println("[DEBUG_LOG] Created USERS table");

            // Create USER_ROLES table with constraint on role column
            stmt.execute("CREATE TABLE USER_ROLES (" +
                         "user_id TEXT NOT NULL, " +
                         "role TEXT NOT NULL CHECK (role IN ('ADMIN', 'QA', 'PRODUCTION')), " +
                         "PRIMARY KEY (user_id, role), " +
                         "FOREIGN KEY (user_id) REFERENCES USERS(user_id))");

            System.out.println("[DEBUG_LOG] Created USER_ROLES table with role constraint");

            // Insert test user
            stmt.execute("INSERT INTO USERS (user_id, username, password, email, first_name, last_name, status, approved) " +
                         "VALUES ('test-user-id', 'testuser', 'password', 'test@example.com', 'Test', 'User', 'ACTIVE', 1)");

            // Insert test user role
            stmt.execute("INSERT INTO USER_ROLES (user_id, role) " +
                         "VALUES ('test-user-id', 'ADMIN')");

            System.out.println("[DEBUG_LOG] Inserted test user and role");
        } catch (SQLException e) {
            System.out.println("[DEBUG_LOG] Error creating schema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void resetDatabase() {
        // For simplicity, we won't reset the database between tests
        // This is acceptable for these tests since they use unique IDs
        System.out.println("[DEBUG_LOG] Skipping database reset for DatabaseOperationsTest");
    }

    @AfterAll
    public static void tearDown() {
        // Shutdown the database
        System.out.println("[DEBUG_LOG] Shutting down database for DatabaseOperationsTest");
        SqliteDatabaseConfig.shutdown();
        System.out.println("[DEBUG_LOG] Database shutdown completed");
    }

    /**
     * Test creating a new user in the database.
     */
    @Test
    public void testCreateUser() throws SQLException {
        System.out.println("[DEBUG_LOG] Starting test for creating a user");

        // Generate a unique user ID
        String userId = UUID.randomUUID().toString();
        String username = "testuser_" + System.currentTimeMillis();
        String password = "$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS"; // Hashed password
        String email = username + "@example.com";
        String firstName = "Test";
        String lastName = "User";
        String role = "PRODUCTION";

        // Insert the user
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
                    stmt.setString(3, password);
                    stmt.setString(4, email);
                    stmt.setString(5, firstName);
                    stmt.setString(6, lastName);
                    int rowsAffected = stmt.executeUpdate();
                    assertEquals(1, rowsAffected, "One row should be inserted into USERS table");
                }

                // Insert into USER_ROLES table
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO USER_ROLES (user_id, role) VALUES (?, ?)")) {
                    stmt.setString(1, userId);
                    stmt.setString(2, role);
                    int rowsAffected = stmt.executeUpdate();
                    assertEquals(1, rowsAffected, "One row should be inserted into USER_ROLES table");
                }

                // Commit the transaction
                connection.commit();
            } catch (SQLException e) {
                // Rollback the transaction if an error occurs
                connection.rollback();
                throw e;
            } finally {
                // Restore auto-commit mode
                connection.setAutoCommit(true);
            }
        }

        // Verify the user was created
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT u.*, ur.role FROM USERS u " +
                     "JOIN USER_ROLES ur ON u.user_id = ur.user_id " +
                     "WHERE u.user_id = ?")) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "User should exist in the database");
                assertEquals(username, rs.getString("username"), "Username should match");
                assertEquals(email, rs.getString("email"), "Email should match");
                assertEquals(firstName, rs.getString("first_name"), "First name should match");
                assertEquals(lastName, rs.getString("last_name"), "Last name should match");
                assertEquals(role, rs.getString("role"), "Role should match");
            }
        }

        System.out.println("[DEBUG_LOG] User creation test passed");
    }

    /**
     * Test updating a user in the database.
     */
    @Test
    public void testUpdateUser() throws SQLException {
        System.out.println("[DEBUG_LOG] Starting test for updating a user");

        // First, create a user to update
        String userId = UUID.randomUUID().toString();
        String username = "updateuser_" + System.currentTimeMillis();
        String password = "$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS";
        String email = username + "@example.com";
        String firstName = "Update";
        String lastName = "User";
        String role = "PRODUCTION";

        // Insert the user
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO USERS (user_id, username, password, email, first_name, last_name, created_at, updated_at, approved) " +
                        "VALUES (?, ?, ?, ?, ?, ?, datetime('now'), datetime('now'), 1)")) {
                    stmt.setString(1, userId);
                    stmt.setString(2, username);
                    stmt.setString(3, password);
                    stmt.setString(4, email);
                    stmt.setString(5, firstName);
                    stmt.setString(6, lastName);
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO USER_ROLES (user_id, role) VALUES (?, ?)")) {
                    stmt.setString(1, userId);
                    stmt.setString(2, role);
                    stmt.executeUpdate();
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }

        // Now update the user
        String newEmail = "updated_" + username + "@example.com";
        String newFirstName = "Updated";
        String newLastName = "UserName";
        String newRole = "QA";

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Update the USERS table
                try (PreparedStatement stmt = connection.prepareStatement(
                        "UPDATE USERS SET email = ?, first_name = ?, last_name = ?, updated_at = datetime('now') " +
                        "WHERE user_id = ?")) {
                    stmt.setString(1, newEmail);
                    stmt.setString(2, newFirstName);
                    stmt.setString(3, newLastName);
                    stmt.setString(4, userId);
                    int rowsAffected = stmt.executeUpdate();
                    assertEquals(1, rowsAffected, "One row should be updated in USERS table");
                }

                // Delete old role
                try (PreparedStatement stmt = connection.prepareStatement(
                        "DELETE FROM USER_ROLES WHERE user_id = ?")) {
                    stmt.setString(1, userId);
                    stmt.executeUpdate();
                }

                // Insert new role
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO USER_ROLES (user_id, role) VALUES (?, ?)")) {
                    stmt.setString(1, userId);
                    stmt.setString(2, newRole);
                    int rowsAffected = stmt.executeUpdate();
                    assertEquals(1, rowsAffected, "One row should be inserted into USER_ROLES table");
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }

        // Verify the user was updated
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT u.*, ur.role FROM USERS u " +
                     "JOIN USER_ROLES ur ON u.user_id = ur.user_id " +
                     "WHERE u.user_id = ?")) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "User should exist in the database");
                assertEquals(username, rs.getString("username"), "Username should not change");
                assertEquals(newEmail, rs.getString("email"), "Email should be updated");
                assertEquals(newFirstName, rs.getString("first_name"), "First name should be updated");
                assertEquals(newLastName, rs.getString("last_name"), "Last name should be updated");
                assertEquals(newRole, rs.getString("role"), "Role should be updated");
            }
        }

        System.out.println("[DEBUG_LOG] User update test passed");
    }

    /**
     * Test deleting a user from the database.
     */
    @Test
    public void testDeleteUser() throws SQLException {
        System.out.println("[DEBUG_LOG] Starting test for deleting a user");

        // First, create a user to delete
        String userId = UUID.randomUUID().toString();
        String username = "deleteuser_" + System.currentTimeMillis();
        String password = "$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS";
        String email = username + "@example.com";
        String firstName = "Delete";
        String lastName = "User";
        String role = "PRODUCTION";

        // Insert the user
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO USERS (user_id, username, password, email, first_name, last_name, created_at, updated_at, approved) " +
                        "VALUES (?, ?, ?, ?, ?, ?, datetime('now'), datetime('now'), 1)")) {
                    stmt.setString(1, userId);
                    stmt.setString(2, username);
                    stmt.setString(3, password);
                    stmt.setString(4, email);
                    stmt.setString(5, firstName);
                    stmt.setString(6, lastName);
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO USER_ROLES (user_id, role) VALUES (?, ?)")) {
                    stmt.setString(1, userId);
                    stmt.setString(2, role);
                    stmt.executeUpdate();
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }

        // Verify the user exists
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT COUNT(*) FROM USERS WHERE user_id = ?")) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "Result set should have at least one row");
                assertEquals(1, rs.getInt(1), "User should exist in the database");
            }
        }

        // Now delete the user
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Delete from USER_ROLES table first (foreign key constraint)
                try (PreparedStatement stmt = connection.prepareStatement(
                        "DELETE FROM USER_ROLES WHERE user_id = ?")) {
                    stmt.setString(1, userId);
                    int rowsAffected = stmt.executeUpdate();
                    assertEquals(1, rowsAffected, "One row should be deleted from USER_ROLES table");
                }

                // Delete from USERS table
                try (PreparedStatement stmt = connection.prepareStatement(
                        "DELETE FROM USERS WHERE user_id = ?")) {
                    stmt.setString(1, userId);
                    int rowsAffected = stmt.executeUpdate();
                    assertEquals(1, rowsAffected, "One row should be deleted from USERS table");
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }

        // Verify the user was deleted
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT COUNT(*) FROM USERS WHERE user_id = ?")) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "Result set should have at least one row");
                assertEquals(0, rs.getInt(1), "User should not exist in the database");
            }
        }

        System.out.println("[DEBUG_LOG] User deletion test passed");
    }

    /**
     * Test transaction management with rollback.
     */
    @Test
    public void testTransactionRollback() throws SQLException {
        System.out.println("[DEBUG_LOG] Starting test for transaction rollback");

        // Generate a unique user ID
        String userId = UUID.randomUUID().toString();
        String username = "rollbackuser_" + System.currentTimeMillis();
        String password = "$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS";
        String email = username + "@example.com";
        String firstName = "Rollback";
        String lastName = "User";
        String role = "PRODUCTION";

        // Try to insert the user with a transaction that will be rolled back
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
                    stmt.setString(3, password);
                    stmt.setString(4, email);
                    stmt.setString(5, firstName);
                    stmt.setString(6, lastName);
                    stmt.executeUpdate();
                }

                // Simulate an error by trying to insert a role with an invalid value
                // This should cause the transaction to be rolled back
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO USER_ROLES (user_id, role) VALUES (?, ?)")) {
                    stmt.setString(1, userId);
                    stmt.setString(2, "INVALID_ROLE"); // This should cause a constraint violation
                    stmt.executeUpdate();
                    fail("Expected a constraint violation exception");
                } catch (SQLException e) {
                    // Expected exception due to constraint violation
                    System.out.println("[DEBUG_LOG] Expected exception: " + e.getMessage());
                    // Explicitly roll back the transaction
                    connection.rollback();
                }
            } finally {
                // Restore auto-commit mode
                connection.setAutoCommit(true);
            }
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
        }

        System.out.println("[DEBUG_LOG] Transaction rollback test passed");
    }
}
