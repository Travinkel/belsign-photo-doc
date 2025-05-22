package com.belman.integration.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the database.
 * These tests verify that the database is properly initialized with test data
 * and that the data can be accessed and manipulated.
 */
public class DatabaseIntegrationTest {

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
    public void testDatabaseContainsTestUsers() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM USERS")) {

            assertTrue(resultSet.next(), "Result set should have at least one row");
            int userCount = resultSet.getInt(1);
            assertEquals(3, userCount, "Users table should contain 3 test users");

            System.out.println("[DEBUG_LOG] Found " + userCount + " users in the test database");
        }
    }

    @Test
    public void testDatabaseContainsTestOrders() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM ORDERS")) {

            assertTrue(resultSet.next(), "Result set should have at least one row");
            int orderCount = resultSet.getInt(1);
            assertEquals(3, orderCount, "Orders table should contain 3 test orders");

            System.out.println("[DEBUG_LOG] Found " + orderCount + " orders in the test database");
        }
    }

    @Test
    public void testDatabaseContainsTestPhotos() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM PHOTOS")) {

            assertTrue(resultSet.next(), "Result set should have at least one row");
            int photoCount = resultSet.getInt(1);
            assertEquals(4, photoCount, "Photos table should contain 4 test photos");

            System.out.println("[DEBUG_LOG] Found " + photoCount + " photos in the test database");
        }
    }

    @Test
    public void testDatabaseContainsTestPhotoTemplates() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM PHOTO_TEMPLATES")) {

            assertTrue(resultSet.next(), "Result set should have at least one row");
            int templateCount = resultSet.getInt(1);
            assertTrue(templateCount >= 4, "Photo templates table should contain at least 4 test templates");

            System.out.println("[DEBUG_LOG] Found " + templateCount + " photo templates in the test database");
        }
    }

    @Test
    public void testDatabaseContainsTestReports() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM REPORTS")) {

            assertTrue(resultSet.next(), "Result set should have at least one row");
            int reportCount = resultSet.getInt(1);
            assertEquals(3, reportCount, "Reports table should contain 3 test reports");

            System.out.println("[DEBUG_LOG] Found " + reportCount + " reports in the test database");
        }
    }

    @Test
    public void testDatabaseContainsTestPhotoAnnotations() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM PHOTO_ANNOTATIONS")) {

            assertTrue(resultSet.next(), "Result set should have at least one row");
            int annotationCount = resultSet.getInt(1);
            assertEquals(3, annotationCount, "Photo annotations table should contain 3 test annotations");

            System.out.println("[DEBUG_LOG] Found " + annotationCount + " photo annotations in the test database");
        }
    }

    @Test
    public void testDatabaseContainsTestReportPhotos() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM REPORT_PHOTOS")) {

            assertTrue(resultSet.next(), "Result set should have at least one row");
            int reportPhotoCount = resultSet.getInt(1);
            assertEquals(4, reportPhotoCount, "Report photos table should contain 4 test report-photo associations");

            System.out.println("[DEBUG_LOG] Found " + reportPhotoCount + " report-photo associations in the test database");
        }
    }

    @Test
    public void testUserRolesAssignedCorrectly() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                 "SELECT u.username, ur.role FROM USERS u " +
                 "JOIN USER_ROLES ur ON u.user_id = ur.user_id " +
                 "ORDER BY u.username")) {

            // Admin user should have ADMIN role
            assertTrue(resultSet.next(), "Result set should have at least one row");
            assertEquals("admin", resultSet.getString("username"));
            assertEquals("ADMIN", resultSet.getString("role"));

            // Production user should have PRODUCTION role
            assertTrue(resultSet.next(), "Result set should have at least two rows");
            assertEquals("production", resultSet.getString("username"));
            assertEquals("PRODUCTION", resultSet.getString("role"));

            // QA user should have QA role
            assertTrue(resultSet.next(), "Result set should have at least three rows");
            assertEquals("qa", resultSet.getString("username"));
            assertEquals("QA", resultSet.getString("role"));

            System.out.println("[DEBUG_LOG] User roles are assigned correctly in the test database");
        }
    }

    @Test
    public void testOrdersAssignedToProductionWorker() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                 "SELECT o.order_number, u.username FROM ORDERS o " +
                 "JOIN USERS u ON o.assigned_to = u.user_id " +
                 "WHERE o.order_id IN ('77777777-7777-7777-7777-777777777777', '88888888-8888-8888-8888-888888888888')")) {

            // First order should be assigned to production worker
            assertTrue(resultSet.next(), "Result set should have at least one row");
            assertEquals("production", resultSet.getString("username"));

            // Second order should also be assigned to production worker
            assertTrue(resultSet.next(), "Result set should have at least two rows");
            assertEquals("production", resultSet.getString("username"));

            System.out.println("[DEBUG_LOG] Orders are correctly assigned to production worker in the test database");
        }
    }

    @Test
    public void testOrdersHavePhotoTemplatesAttached() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                 "SELECT o.order_id, COUNT(opt.template_id) as template_count " +
                 "FROM ORDERS o " +
                 "LEFT JOIN ORDER_PHOTO_TEMPLATES opt ON o.order_id = opt.order_id " +
                 "GROUP BY o.order_id")) {

            // Check that each order has at least one template attached
            while (resultSet.next()) {
                String orderId = resultSet.getString("order_id");
                int templateCount = resultSet.getInt("template_count");

                assertTrue(templateCount > 0, "Order " + orderId + " should have at least one template attached");
                System.out.println("[DEBUG_LOG] Order " + orderId + " has " + templateCount + " templates attached");
            }
        }
    }
}
