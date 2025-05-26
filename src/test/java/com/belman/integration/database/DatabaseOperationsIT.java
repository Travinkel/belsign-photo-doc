package com.belman.integration.database;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for database operations using Failsafe.
 * These tests verify that CRUD operations on the database work correctly.
 * 
 * This class uses the IT suffix (Integration Test) to be run by Maven Failsafe
 * instead of Surefire, demonstrating the separation between unit tests and integration tests.
 */
public class DatabaseOperationsIT {

    private static DataSource dataSource;

    @BeforeAll
    public static void setupDatabase() {
        // Initialize the test database
        dataSource = TestDatabaseUtil.initializeTestDatabase();
        assertNotNull(dataSource, "DataSource should not be null");
        System.out.println("[DEBUG_LOG] Test database initialized successfully");
    }

    @BeforeEach
    public void resetDatabase() {
        // Reset the database before each test
        TestDatabaseUtil.resetTestDatabase();
        System.out.println("[DEBUG_LOG] Test database reset for new test");
    }

    @AfterAll
    public static void tearDown() {
        // Shutdown the test database
        TestDatabaseUtil.shutdownTestDatabase();
        System.out.println("[DEBUG_LOG] Test database shut down");
    }

    /**
     * Test creating a new user in the database.
     */
    @Test
    public void testCreateUser() throws SQLException {
        System.out.println("[DEBUG_LOG] Starting test for creating a user (IT)");

        // Generate a unique user ID
        String userId = UUID.randomUUID().toString();
        String username = "testuser_it_" + System.currentTimeMillis();
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
                System.out.println("[DEBUG_LOG] User created successfully: " + username);
            } catch (SQLException e) {
                // Rollback the transaction if an error occurs
                connection.rollback();
                System.out.println("[DEBUG_LOG] Error creating user: " + e.getMessage());
                throw e;
            } finally {
                // Restore auto-commit mode
                connection.setAutoCommit(true);
            }
        }

        // Verify the user was created
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT * FROM USERS WHERE user_id = ?")) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "User should exist in the database");
                assertEquals(username, rs.getString("username"), "Username should match");
                assertEquals(email, rs.getString("email"), "Email should match");
                System.out.println("[DEBUG_LOG] User verification successful");
            }
        }
    }

    /**
     * Test retrieving a user from the database.
     */
    @Test
    public void testRetrieveUser() throws SQLException {
        System.out.println("[DEBUG_LOG] Starting test for retrieving a user (IT)");

        // Create a user to retrieve
        String userId = UUID.randomUUID().toString();
        String username = "retrieveuser_it_" + System.currentTimeMillis();
        String password = "$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS";
        String email = username + "@example.com";
        String firstName = "Retrieve";
        String lastName = "User";
        String role = "QA";

        // Insert the user
        try (Connection connection = dataSource.getConnection()) {
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

                // Insert into USER_ROLES table
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO USER_ROLES (user_id, role) VALUES (?, ?)")) {
                    stmt.setString(1, userId);
                    stmt.setString(2, role);
                    stmt.executeUpdate();
                }

                connection.commit();
                System.out.println("[DEBUG_LOG] Test user created for retrieval test");
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }

        // Retrieve the user
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT u.*, r.role FROM USERS u " +
                     "JOIN USER_ROLES r ON u.user_id = r.user_id " +
                     "WHERE u.username = ?")) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "User should exist in the database");
                assertEquals(userId, rs.getString("user_id"), "User ID should match");
                assertEquals(email, rs.getString("email"), "Email should match");
                assertEquals(firstName, rs.getString("first_name"), "First name should match");
                assertEquals(lastName, rs.getString("last_name"), "Last name should match");
                assertEquals(role, rs.getString("role"), "Role should match");
                System.out.println("[DEBUG_LOG] User retrieved successfully");
            }
        }
    }
}