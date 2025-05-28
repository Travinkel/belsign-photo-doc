package com.belman.integration.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the RAT phase data initializer.
 * These tests verify that the data initializer correctly populates the database with data useful for the RAT phase.
 * 
 * This class uses the IT suffix (Integration Test) to be run by Maven Failsafe
 * instead of Surefire, demonstrating the separation between unit tests and integration tests.
 */
public class RatPhaseDataInitializerIT {

    private static DataSource dataSource;

    @BeforeAll
    public static void setupDatabase() {
        // Initialize the test database
        dataSource = TestDatabaseUtil.initializeTestDatabase();
        assertNotNull(dataSource, "DataSource should not be null");
        System.out.println("[DEBUG_LOG] Test database initialized successfully for RAT phase data initializer tests");
        
        // Initialize the RAT phase data initializer
        RatPhaseDataInitializer.initialize(dataSource);
        System.out.println("[DEBUG_LOG] RAT phase data initializer initialized");
    }

    @AfterAll
    public static void tearDown() {
        // Shutdown the test database
        TestDatabaseUtil.shutdownTestDatabase();
        System.out.println("[DEBUG_LOG] Test database shut down after RAT phase data initializer tests");
    }

    /**
     * Test that the RAT phase data initializer correctly populates the database with test data.
     */
    @Test
    public void testPopulateRatPhaseData() {
        System.out.println("[DEBUG_LOG] Starting test for populating RAT phase data");

        // Populate the database with RAT phase test data
        RatPhaseDataInitializer.populateRatPhaseData();
        System.out.println("[DEBUG_LOG] RAT phase test data populated");

        // Verify that the database contains the expected data
        verifyTestUsers();
        verifyTestOrders();
        verifyTestPhotoTemplates();
        verifyTestPhotos();
        verifyTestAnnotations();
        verifyTestReports();

        System.out.println("[DEBUG_LOG] RAT phase test data verification completed successfully");
    }

    /**
     * Verify that the database contains the expected test users.
     */
    private void verifyTestUsers() {
        System.out.println("[DEBUG_LOG] Verifying test users");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT u.*, r.role FROM USERS u JOIN USER_ROLES r ON u.user_id = r.user_id WHERE u.username LIKE 'rat_%'")) {
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> usernames = new ArrayList<>();
                List<String> roles = new ArrayList<>();

                while (rs.next()) {
                    usernames.add(rs.getString("username"));
                    roles.add(rs.getString("role"));
                    System.out.println("[DEBUG_LOG] Found user: " + rs.getString("username") + 
                                      ", Role: " + rs.getString("role"));
                }

                // Verify that we found the expected users
                assertTrue(usernames.contains("rat_production"), "Production user should exist");
                assertTrue(usernames.contains("rat_qa"), "QA user should exist");
                assertTrue(usernames.contains("rat_admin"), "Admin user should exist");

                // Verify that we found the expected roles
                assertTrue(roles.contains("PRODUCTION"), "PRODUCTION role should exist");
                assertTrue(roles.contains("QA"), "QA role should exist");
                assertTrue(roles.contains("ADMIN"), "ADMIN role should exist");

                System.out.println("[DEBUG_LOG] Found " + usernames.size() + " test users");
            }
        } catch (SQLException e) {
            fail("Exception occurred while verifying test users: " + e.getMessage());
        }
    }

    /**
     * Verify that the database contains the expected test orders.
     */
    private void verifyTestOrders() {
        System.out.println("[DEBUG_LOG] Verifying test orders");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT * FROM ORDERS WHERE order_number LIKE 'RAT-%'")) {
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> orderNumbers = new ArrayList<>();
                List<String> statuses = new ArrayList<>();

                while (rs.next()) {
                    orderNumbers.add(rs.getString("order_number"));
                    statuses.add(rs.getString("status"));
                    System.out.println("[DEBUG_LOG] Found order: " + rs.getString("order_number") + 
                                      ", Status: " + rs.getString("status"));
                }

                // Verify that we found the expected orders
                assertTrue(orderNumbers.contains("RAT-XX-230501-ABC-0001"), "Pending order should exist");
                assertTrue(orderNumbers.contains("RAT-XX-230502-ABC-0002"), "In-progress order should exist");
                assertTrue(orderNumbers.contains("RAT-XX-230503-ABC-0003"), "Completed order should exist");
                assertTrue(orderNumbers.contains("RAT-XX-230504-ABC-0004"), "Approved order should exist");

                // Verify that we found the expected statuses
                assertTrue(statuses.contains("PENDING"), "PENDING status should exist");
                assertTrue(statuses.contains("IN_PROGRESS"), "IN_PROGRESS status should exist");
                assertTrue(statuses.contains("COMPLETED"), "COMPLETED status should exist");
                assertTrue(statuses.contains("APPROVED"), "APPROVED status should exist");

                System.out.println("[DEBUG_LOG] Found " + orderNumbers.size() + " test orders");
            }
        } catch (SQLException e) {
            fail("Exception occurred while verifying test orders: " + e.getMessage());
        }
    }

    /**
     * Verify that the database contains the expected test photo templates.
     */
    private void verifyTestPhotoTemplates() {
        System.out.println("[DEBUG_LOG] Verifying test photo templates");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT * FROM PHOTO_TEMPLATES WHERE name LIKE 'RAT_%'")) {
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> templateNames = new ArrayList<>();
                List<Boolean> requiredFlags = new ArrayList<>();

                while (rs.next()) {
                    templateNames.add(rs.getString("name"));
                    requiredFlags.add(rs.getBoolean("required"));
                    System.out.println("[DEBUG_LOG] Found template: " + rs.getString("name") + 
                                      ", Required: " + rs.getBoolean("required"));
                }

                // Verify that we found the expected templates
                assertTrue(templateNames.contains("RAT_REQUIRED_VIEW"), "Required template should exist");
                assertTrue(templateNames.contains("RAT_OPTIONAL_VIEW"), "Optional template should exist");

                // Verify that we found the expected required flags
                assertTrue(requiredFlags.contains(true), "Required flag should exist");
                assertTrue(requiredFlags.contains(false), "Optional flag should exist");

                System.out.println("[DEBUG_LOG] Found " + templateNames.size() + " test photo templates");
            }
        } catch (SQLException e) {
            fail("Exception occurred while verifying test photo templates: " + e.getMessage());
        }
    }

    /**
     * Verify that the database contains the expected test photos.
     */
    private void verifyTestPhotos() {
        System.out.println("[DEBUG_LOG] Verifying test photos");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT * FROM PHOTOS WHERE file_path LIKE 'test/rat_%'")) {
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> filePaths = new ArrayList<>();
                List<String> statuses = new ArrayList<>();

                while (rs.next()) {
                    filePaths.add(rs.getString("file_path"));
                    statuses.add(rs.getString("status"));
                    System.out.println("[DEBUG_LOG] Found photo: " + rs.getString("file_path") + 
                                      ", Status: " + rs.getString("status"));
                }

                // Verify that we found the expected photos
                assertTrue(filePaths.contains("test/rat_pending_photo.jpg"), "Pending photo should exist");
                assertTrue(filePaths.contains("test/rat_approved_photo.jpg"), "Approved photo should exist");
                assertTrue(filePaths.contains("test/rat_rejected_photo.jpg"), "Rejected photo should exist");

                // Verify that we found the expected statuses
                assertTrue(statuses.contains("PENDING"), "PENDING status should exist");
                assertTrue(statuses.contains("APPROVED"), "APPROVED status should exist");
                assertTrue(statuses.contains("REJECTED"), "REJECTED status should exist");

                System.out.println("[DEBUG_LOG] Found " + filePaths.size() + " test photos");
            }
        } catch (SQLException e) {
            fail("Exception occurred while verifying test photos: " + e.getMessage());
        }
    }

    /**
     * Verify that the database contains the expected test annotations.
     */
    private void verifyTestAnnotations() {
        System.out.println("[DEBUG_LOG] Verifying test annotations");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT * FROM PHOTO_ANNOTATIONS WHERE text LIKE 'RAT phase%'")) {
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> texts = new ArrayList<>();

                while (rs.next()) {
                    texts.add(rs.getString("text"));
                    System.out.println("[DEBUG_LOG] Found annotation: " + rs.getString("text"));
                }

                // Verify that we found the expected annotations
                assertTrue(texts.contains("RAT phase test annotation"), "Test annotation should exist");

                System.out.println("[DEBUG_LOG] Found " + texts.size() + " test annotations");
            }
        } catch (SQLException e) {
            fail("Exception occurred while verifying test annotations: " + e.getMessage());
        }
    }

    /**
     * Verify that the database contains the expected test reports.
     */
    private void verifyTestReports() {
        System.out.println("[DEBUG_LOG] Verifying test reports");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT * FROM REPORTS WHERE title LIKE 'RAT %'")) {
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> titles = new ArrayList<>();
                List<String> statuses = new ArrayList<>();

                while (rs.next()) {
                    titles.add(rs.getString("title"));
                    statuses.add(rs.getString("status"));
                    System.out.println("[DEBUG_LOG] Found report: " + rs.getString("title") + 
                                      ", Status: " + rs.getString("status"));
                }

                // Verify that we found the expected reports
                assertTrue(titles.contains("RAT Pending Report"), "Pending report should exist");
                assertTrue(titles.contains("RAT Approved Report"), "Approved report should exist");
                assertTrue(titles.contains("RAT Rejected Report"), "Rejected report should exist");

                // Verify that we found the expected statuses
                assertTrue(statuses.contains("PENDING"), "PENDING status should exist");
                assertTrue(statuses.contains("APPROVED"), "APPROVED status should exist");
                assertTrue(statuses.contains("REJECTED"), "REJECTED status should exist");

                System.out.println("[DEBUG_LOG] Found " + titles.size() + " test reports");
            }
        } catch (SQLException e) {
            fail("Exception occurred while verifying test reports: " + e.getMessage());
        }
    }
}