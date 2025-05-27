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
 * Simple integration tests for database operations.
 * These tests verify that basic CRUD operations on the database work correctly.
 */
public class SimpleDatabaseOperationsTest {

    private static final Logger LOGGER = Logger.getLogger(SimpleDatabaseOperationsTest.class.getName());
    private static DataSource dataSource;

    @BeforeAll
    public static void setupDatabase() {
        // Initialize the SQLite database using SqliteDatabaseConfig
        LOGGER.info("Initializing database for SimpleDatabaseOperationsTest using SqliteDatabaseConfig");
        SqliteDatabaseConfig.initialize();
        dataSource = SqliteDatabaseConfig.getDataSource();
        assertNotNull(dataSource, "DataSource should not be null");
        LOGGER.info("Database initialized successfully for SimpleDatabaseOperationsTest");
        
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
        LOGGER.info("Shutting down database for SimpleDatabaseOperationsTest");
        SqliteDatabaseConfig.shutdown();
        LOGGER.info("Database shutdown completed");
    }

    /**
     * Test creating and retrieving a simple record in the database.
     */
    @Test
    public void testSimpleDatabaseOperation() throws SQLException {
        LOGGER.info("Starting test for simple database operation");

        // Create a test table
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create a simple test table
            stmt.execute("CREATE TABLE IF NOT EXISTS test_table (" +
                         "id TEXT PRIMARY KEY, " +
                         "name TEXT NOT NULL)");
            
            LOGGER.info("Created test_table");
        }

        // Generate a unique ID
        String id = UUID.randomUUID().toString();
        String name = "Test Name " + System.currentTimeMillis();

        // Insert a record
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO test_table (id, name) VALUES (?, ?)")) {
            stmt.setString(1, id);
            stmt.setString(2, name);
            int rowsAffected = stmt.executeUpdate();
            
            assertEquals(1, rowsAffected, "One row should be inserted into test_table");
            LOGGER.info("Inserted record into test_table: " + id + ", " + name);
        }

        // Retrieve the record
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM test_table WHERE id = ?")) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "Record should exist in the database");
                assertEquals(id, rs.getString("id"), "ID should match");
                assertEquals(name, rs.getString("name"), "Name should match");
                LOGGER.info("Successfully retrieved record from test_table");
            }
        }

        // Clean up
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS test_table");
            LOGGER.info("Dropped test_table");
        }

        LOGGER.info("Simple database operation test passed");
    }
}