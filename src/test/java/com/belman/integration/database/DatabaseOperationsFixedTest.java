package com.belman.integration.database;

import com.belman.bootstrap.persistence.SqliteDatabaseConfig;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Fixed integration tests for database operations.
 * These tests verify that basic CRUD operations on the database work correctly.
 */
public class DatabaseOperationsFixedTest {

    private static final Logger LOGGER = Logger.getLogger(DatabaseOperationsFixedTest.class.getName());
    private static DataSource dataSource;

    @BeforeAll
    public static void setupDatabase() {
        // Initialize the SQLite database using SqliteDatabaseConfig
        LOGGER.info("Initializing database for DatabaseOperationsFixedTest using SqliteDatabaseConfig");
        SqliteDatabaseConfig.initialize();
        dataSource = SqliteDatabaseConfig.getDataSource();
        assertNotNull(dataSource, "DataSource should not be null");
        LOGGER.info("Database initialized successfully for DatabaseOperationsFixedTest");
        
        // Verify connection works
        try (Connection conn = dataSource.getConnection()) {
            LOGGER.info("Successfully obtained database connection");
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT 1")) {
                    if (rs.next()) {
                        LOGGER.info("Database connection test query executed successfully");
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error testing database connection", e);
        }
    }

    @AfterAll
    public static void tearDown() {
        // Shutdown the database
        LOGGER.info("Shutting down database for DatabaseOperationsFixedTest");
        SqliteDatabaseConfig.shutdown();
        LOGGER.info("Database shutdown completed");
    }

    /**
     * Test creating, updating, and deleting a user in the database.
     */
    @Test
    public void testUserCRUD() throws SQLException {
        LOGGER.info("Starting test for user CRUD operations");

        // Create a test table for users
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create a simple test table for users
            stmt.execute("CREATE TABLE IF NOT EXISTS test_users (" +
                         "id TEXT PRIMARY KEY, " +
                         "username TEXT NOT NULL, " +
                         "email TEXT, " +
                         "first_name TEXT, " +
                         "last_name TEXT)");
            
            LOGGER.info("Created test_users table");
        }

        // Generate a unique user ID and data
        String userId = UUID.randomUUID().toString();
        String username = "testuser_" + System.currentTimeMillis();
        String email = username + "@example.com";
        String firstName = "Test";
        String lastName = "User";

        // Insert a user
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO test_users (id, username, email, first_name, last_name) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, userId);
            stmt.setString(2, username);
            stmt.setString(3, email);
            stmt.setString(4, firstName);
            stmt.setString(5, lastName);
            int rowsAffected = stmt.executeUpdate();
            
            assertEquals(1, rowsAffected, "One row should be inserted into test_users table");
            LOGGER.info("Inserted user into test_users table: " + userId + ", " + username);
        }

        // Retrieve the user
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM test_users WHERE id = ?")) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "User should exist in the database");
                assertEquals(userId, rs.getString("id"), "User ID should match");
                assertEquals(username, rs.getString("username"), "Username should match");
                assertEquals(email, rs.getString("email"), "Email should match");
                assertEquals(firstName, rs.getString("first_name"), "First name should match");
                assertEquals(lastName, rs.getString("last_name"), "Last name should match");
                LOGGER.info("Successfully retrieved user from test_users table");
            }
        }

        // Update the user
        String newEmail = "updated_" + username + "@example.com";
        String newFirstName = "Updated";
        String newLastName = "User";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE test_users SET email = ?, first_name = ?, last_name = ? WHERE id = ?")) {
            stmt.setString(1, newEmail);
            stmt.setString(2, newFirstName);
            stmt.setString(3, newLastName);
            stmt.setString(4, userId);
            int rowsAffected = stmt.executeUpdate();
            
            assertEquals(1, rowsAffected, "One row should be updated in test_users table");
            LOGGER.info("Updated user in test_users table: " + userId);
        }
        
        // Verify the update
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM test_users WHERE id = ?")) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "User should exist in the database");
                assertEquals(newEmail, rs.getString("email"), "Updated email should match");
                assertEquals(newFirstName, rs.getString("first_name"), "Updated first name should match");
                assertEquals(newLastName, rs.getString("last_name"), "Updated last name should match");
                LOGGER.info("Successfully verified user update in test_users table");
            }
        }
        
        // Delete the user
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM test_users WHERE id = ?")) {
            stmt.setString(1, userId);
            int rowsAffected = stmt.executeUpdate();
            
            assertEquals(1, rowsAffected, "One row should be deleted from test_users table");
            LOGGER.info("Deleted user from test_users table: " + userId);
        }
        
        // Verify the deletion
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM test_users WHERE id = ?")) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "Result set should have at least one row");
                assertEquals(0, rs.getInt(1), "User should not exist in the database after deletion");
                LOGGER.info("Successfully verified user deletion from test_users table");
            }
        }

        // Clean up
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS test_users");
            LOGGER.info("Dropped test_users table");
        }

        LOGGER.info("User CRUD operations test passed");
    }
}