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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for photo documentation database operations using Failsafe.
 * These tests verify that CRUD operations on the photo documentation tables work correctly.
 * 
 * This class uses the IT suffix (Integration Test) to be run by Maven Failsafe
 * instead of Surefire, demonstrating the separation between unit tests and integration tests.
 * 
 * Since the system is in the RAT (Requirements Acceptance Testing) phase, these tests
 * focus on operations that would be performed during this phase, such as creating,
 * retrieving, and updating photos and their annotations.
 */
public class PhotoDocumentationDatabaseIT {

    private static DataSource dataSource;

    @BeforeAll
    public static void setupDatabase() {
        // Initialize the test database
        dataSource = TestDatabaseUtil.initializeTestDatabase();
        assertNotNull(dataSource, "DataSource should not be null");
        System.out.println("[DEBUG_LOG] Test database initialized successfully for photo documentation tests");
    }

    @BeforeEach
    public void resetDatabase() {
        // Reset the database before each test
        TestDatabaseUtil.resetTestDatabase();
        System.out.println("[DEBUG_LOG] Test database reset for new photo documentation test");
    }

    @AfterAll
    public static void tearDown() {
        // Shutdown the test database
        TestDatabaseUtil.shutdownTestDatabase();
        System.out.println("[DEBUG_LOG] Test database shut down after photo documentation tests");
    }

    /**
     * Test creating a new photo in the database.
     */
    @Test
    public void testCreatePhoto() throws SQLException {
        System.out.println("[DEBUG_LOG] Starting test for creating a photo (IT)");

        // Generate a unique photo ID
        String photoId = UUID.randomUUID().toString();
        String orderId = "77777777-7777-7777-7777-777777777777"; // Using existing order ID from test data
        String filePath = "test/new_photo.jpg";
        String templateType = "TOP_VIEW";
        String status = "PENDING";
        String createdBy = "22222222-2222-2222-2222-222222222222"; // Using existing user ID from test data

        // Insert the photo
        try (Connection connection = dataSource.getConnection()) {
            // Start a transaction
            connection.setAutoCommit(false);

            try {
                // Insert into PHOTOS table
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO PHOTOS (photo_id, order_id, file_path, template_type, status, created_by, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, datetime('now'), datetime('now'))")) {
                    stmt.setString(1, photoId);
                    stmt.setString(2, orderId);
                    stmt.setString(3, filePath);
                    stmt.setString(4, templateType);
                    stmt.setString(5, status);
                    stmt.setString(6, createdBy);
                    int rowsAffected = stmt.executeUpdate();
                    assertEquals(1, rowsAffected, "One row should be inserted into PHOTOS table");
                }

                // Commit the transaction
                connection.commit();
                System.out.println("[DEBUG_LOG] Photo created successfully: " + photoId);
            } catch (SQLException e) {
                // Rollback the transaction if an error occurs
                connection.rollback();
                System.out.println("[DEBUG_LOG] Error creating photo: " + e.getMessage());
                throw e;
            } finally {
                // Restore auto-commit mode
                connection.setAutoCommit(true);
            }
        }

        // Verify the photo was created
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT * FROM PHOTOS WHERE photo_id = ?")) {
            stmt.setString(1, photoId);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "Photo should exist in the database");
                assertEquals(orderId, rs.getString("order_id"), "Order ID should match");
                assertEquals(filePath, rs.getString("file_path"), "File path should match");
                assertEquals(templateType, rs.getString("template_type"), "Template type should match");
                assertEquals(status, rs.getString("status"), "Status should match");
                assertEquals(createdBy, rs.getString("created_by"), "Created by should match");
                System.out.println("[DEBUG_LOG] Photo verification successful");
            }
        }
    }

    /**
     * Test retrieving photos for an order from the database.
     */
    @Test
    public void testRetrievePhotosForOrder() throws SQLException {
        System.out.println("[DEBUG_LOG] Starting test for retrieving photos for an order (IT)");

        String orderId = "77777777-7777-7777-7777-777777777777"; // Using existing order ID from test data

        // Retrieve photos for the order
        List<String> photoIds = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT * FROM PHOTOS WHERE order_id = ?")) {
            stmt.setString(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    photoIds.add(rs.getString("photo_id"));
                    System.out.println("[DEBUG_LOG] Found photo: " + rs.getString("photo_id") + 
                                      ", Template: " + rs.getString("template_type") + 
                                      ", Status: " + rs.getString("status"));
                }
            }
        }

        // Verify that photos were found
        assertFalse(photoIds.isEmpty(), "Photos should be found for the order");
        System.out.println("[DEBUG_LOG] Found " + photoIds.size() + " photos for order " + orderId);
    }

    /**
     * Test updating a photo's status in the database.
     */
    @Test
    public void testUpdatePhotoStatus() throws SQLException {
        System.out.println("[DEBUG_LOG] Starting test for updating a photo's status (IT)");

        // Use an existing photo ID from test data
        String photoId = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";
        String newStatus = "APPROVED";

        // Update the photo's status
        try (Connection connection = dataSource.getConnection()) {
            // Start a transaction
            connection.setAutoCommit(false);

            try {
                // Update the PHOTOS table
                try (PreparedStatement stmt = connection.prepareStatement(
                        "UPDATE PHOTOS SET status = ?, updated_at = datetime('now') WHERE photo_id = ?")) {
                    stmt.setString(1, newStatus);
                    stmt.setString(2, photoId);
                    int rowsAffected = stmt.executeUpdate();
                    assertEquals(1, rowsAffected, "One row should be updated in PHOTOS table");
                }

                // Commit the transaction
                connection.commit();
                System.out.println("[DEBUG_LOG] Photo status updated successfully: " + photoId + " -> " + newStatus);
            } catch (SQLException e) {
                // Rollback the transaction if an error occurs
                connection.rollback();
                System.out.println("[DEBUG_LOG] Error updating photo status: " + e.getMessage());
                throw e;
            } finally {
                // Restore auto-commit mode
                connection.setAutoCommit(true);
            }
        }

        // Verify the photo's status was updated
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT * FROM PHOTOS WHERE photo_id = ?")) {
            stmt.setString(1, photoId);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "Photo should exist in the database");
                assertEquals(newStatus, rs.getString("status"), "Status should be updated");
                System.out.println("[DEBUG_LOG] Photo status verification successful");
            }
        }
    }

    /**
     * Test creating a new annotation for a photo in the database.
     */
    @Test
    public void testCreatePhotoAnnotation() throws SQLException {
        System.out.println("[DEBUG_LOG] Starting test for creating a photo annotation (IT)");

        // Generate a unique annotation ID
        String annotationId = UUID.randomUUID().toString();
        String photoId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"; // Using existing photo ID from test data
        double x = 150.0;
        double y = 120.0;
        double width = 40.0;
        double height = 30.0;
        String text = "Test annotation for RAT phase";
        String createdBy = "33333333-3333-3333-3333-333333333333"; // Using existing user ID from test data

        // Insert the annotation
        try (Connection connection = dataSource.getConnection()) {
            // Start a transaction
            connection.setAutoCommit(false);

            try {
                // Insert into PHOTO_ANNOTATIONS table
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO PHOTO_ANNOTATIONS (annotation_id, photo_id, x, y, width, height, text, created_by, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, datetime('now'), datetime('now'))")) {
                    stmt.setString(1, annotationId);
                    stmt.setString(2, photoId);
                    stmt.setDouble(3, x);
                    stmt.setDouble(4, y);
                    stmt.setDouble(5, width);
                    stmt.setDouble(6, height);
                    stmt.setString(7, text);
                    stmt.setString(8, createdBy);
                    int rowsAffected = stmt.executeUpdate();
                    assertEquals(1, rowsAffected, "One row should be inserted into PHOTO_ANNOTATIONS table");
                }

                // Commit the transaction
                connection.commit();
                System.out.println("[DEBUG_LOG] Photo annotation created successfully: " + annotationId);
            } catch (SQLException e) {
                // Rollback the transaction if an error occurs
                connection.rollback();
                System.out.println("[DEBUG_LOG] Error creating photo annotation: " + e.getMessage());
                throw e;
            } finally {
                // Restore auto-commit mode
                connection.setAutoCommit(true);
            }
        }

        // Verify the annotation was created
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT * FROM PHOTO_ANNOTATIONS WHERE annotation_id = ?")) {
            stmt.setString(1, annotationId);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "Annotation should exist in the database");
                assertEquals(photoId, rs.getString("photo_id"), "Photo ID should match");
                assertEquals(x, rs.getDouble("x"), 0.001, "X coordinate should match");
                assertEquals(y, rs.getDouble("y"), 0.001, "Y coordinate should match");
                assertEquals(width, rs.getDouble("width"), 0.001, "Width should match");
                assertEquals(height, rs.getDouble("height"), 0.001, "Height should match");
                assertEquals(text, rs.getString("text"), "Text should match");
                assertEquals(createdBy, rs.getString("created_by"), "Created by should match");
                System.out.println("[DEBUG_LOG] Photo annotation verification successful");
            }
        }
    }

    /**
     * Test retrieving annotations for a photo from the database.
     */
    @Test
    public void testRetrieveAnnotationsForPhoto() throws SQLException {
        System.out.println("[DEBUG_LOG] Starting test for retrieving annotations for a photo (IT)");

        String photoId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"; // Using existing photo ID from test data

        // Retrieve annotations for the photo
        List<String> annotationIds = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT * FROM PHOTO_ANNOTATIONS WHERE photo_id = ?")) {
            stmt.setString(1, photoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    annotationIds.add(rs.getString("annotation_id"));
                    System.out.println("[DEBUG_LOG] Found annotation: " + rs.getString("annotation_id") + 
                                      ", Text: " + rs.getString("text"));
                }
            }
        }

        // Verify that annotations were found
        assertFalse(annotationIds.isEmpty(), "Annotations should be found for the photo");
        System.out.println("[DEBUG_LOG] Found " + annotationIds.size() + " annotations for photo " + photoId);
    }
}