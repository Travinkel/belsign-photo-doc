package com.belman.integration.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for initializing the database with data useful for the Requirements Acceptance Testing phase.
 * This class provides methods for adding test data that covers various test scenarios for the photo documentation system.
 */
public class AcceptanceTestDataInitializer {
    private static final Logger LOGGER = Logger.getLogger(AcceptanceTestDataInitializer.class.getName());
    private static DataSource dataSource;

    /**
     * Initializes the data source.
     * 
     * @param dataSource the data source to use
     */
    public static void initialize(DataSource dataSource) {
        AcceptanceTestDataInitializer.dataSource = dataSource;
        LOGGER.info("AcceptanceTestDataInitializer initialized with data source");
    }

    /**
     * Populates the database with data useful for the acceptance testing phase.
     * This method adds test data that covers various test scenarios for the photo documentation system.
     */
    public static void populateAcceptanceTestData() {
        if (dataSource == null) {
            LOGGER.severe("Data source not initialized. Call initialize() first.");
            return;
        }

        try (Connection connection = dataSource.getConnection()) {
            // Start a transaction
            connection.setAutoCommit(false);

            try {
                // Add test users with different roles
                addTestUsers(connection);

                // Add test orders in different states
                addTestOrders(connection);

                // Add test photo templates
                addTestPhotoTemplates(connection);

                // Add test photos with different statuses
                addTestPhotos(connection);

                // Add test annotations for photos
                addTestAnnotations(connection);

                // Add test reports
                addTestReports(connection);

                // Commit the transaction
                connection.commit();
                LOGGER.info("Acceptance test data populated successfully");
            } catch (SQLException e) {
                // Rollback the transaction if an error occurs
                connection.rollback();
                LOGGER.log(Level.SEVERE, "Error populating acceptance test data", e);
                throw e;
            } finally {
                // Restore auto-commit mode
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting connection from data source", e);
            throw new RuntimeException("Failed to populate acceptance test data", e);
        }
    }

    /**
     * Adds test users with different roles to the database.
     * 
     * @param connection the database connection
     * @throws SQLException if an error occurs
     */
    private static void addTestUsers(Connection connection) throws SQLException {
        LOGGER.info("Adding test users for acceptance testing...");

        // Add a production worker user
        String productionUserId = UUID.randomUUID().toString();
        addUser(connection, productionUserId, "rat_production", "$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS",
                "rat_production@example.com", "RAT", "Production", "PRODUCTION");

        // Add a QA user
        String qaUserId = UUID.randomUUID().toString();
        addUser(connection, qaUserId, "rat_qa", "$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS",
                "rat_qa@example.com", "RAT", "QA", "QA");

        // Add an admin user
        String adminUserId = UUID.randomUUID().toString();
        addUser(connection, adminUserId, "rat_admin", "$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS",
                "rat_admin@example.com", "RAT", "Admin", "ADMIN");

        LOGGER.info("Test users added successfully");
    }

    /**
     * Adds a user to the database.
     * 
     * @param connection the database connection
     * @param userId the user ID
     * @param username the username
     * @param password the password (hashed)
     * @param email the email
     * @param firstName the first name
     * @param lastName the last name
     * @param role the role
     * @throws SQLException if an error occurs
     */
    private static void addUser(Connection connection, String userId, String username, String password,
                               String email, String firstName, String lastName, String role) throws SQLException {
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
    }

    /**
     * Adds test orders in different states to the database.
     * 
     * @param connection the database connection
     * @throws SQLException if an error occurs
     */
    private static void addTestOrders(Connection connection) throws SQLException {
        LOGGER.info("Adding test orders for acceptance testing...");

        // Add a pending order
        String pendingOrderId = UUID.randomUUID().toString();
        addOrder(connection, pendingOrderId, "RAT-XX-230501-ABC-0001", "RAT Pending Order", "PENDING", null);

        // Add an in-progress order
        String inProgressOrderId = UUID.randomUUID().toString();
        addOrder(connection, inProgressOrderId, "RAT-XX-230502-ABC-0002", "RAT In-Progress Order", "IN_PROGRESS", null);

        // Add a completed order
        String completedOrderId = UUID.randomUUID().toString();
        addOrder(connection, completedOrderId, "RAT-XX-230503-ABC-0003", "RAT Completed Order", "COMPLETED", null);

        // Add an approved order
        String approvedOrderId = UUID.randomUUID().toString();
        addOrder(connection, approvedOrderId, "RAT-XX-230504-ABC-0004", "RAT Approved Order", "APPROVED", null);

        LOGGER.info("Test orders added successfully");
    }

    /**
     * Adds an order to the database.
     * 
     * @param connection the database connection
     * @param orderId the order ID
     * @param orderNumber the order number
     * @param description the description
     * @param status the status
     * @param assignedTo the user ID of the assigned user (can be null)
     * @throws SQLException if an error occurs
     */
    private static void addOrder(Connection connection, String orderId, String orderNumber, String description,
                                String status, String assignedTo) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO ORDERS (order_id, order_number, description, status, assigned_to, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, datetime('now'), datetime('now'))")) {
            stmt.setString(1, orderId);
            stmt.setString(2, orderNumber);
            stmt.setString(3, description);
            stmt.setString(4, status);
            if (assignedTo != null) {
                stmt.setString(5, assignedTo);
            } else {
                stmt.setNull(5, java.sql.Types.VARCHAR);
            }
            stmt.executeUpdate();
        }
    }

    /**
     * Adds test photo templates to the database.
     * 
     * @param connection the database connection
     * @throws SQLException if an error occurs
     */
    private static void addTestPhotoTemplates(Connection connection) throws SQLException {
        LOGGER.info("Adding test photo templates for acceptance testing...");

        // Add a required photo template
        String requiredTemplateId = UUID.randomUUID().toString();
        addPhotoTemplate(connection, requiredTemplateId, "RAT_REQUIRED_VIEW", "Required view for acceptance testing", true);

        // Add an optional photo template
        String optionalTemplateId = UUID.randomUUID().toString();
        addPhotoTemplate(connection, optionalTemplateId, "RAT_OPTIONAL_VIEW", "Optional view for acceptance testing", false);

        LOGGER.info("Test photo templates added successfully");
    }

    /**
     * Adds a photo template to the database.
     * 
     * @param connection the database connection
     * @param templateId the template ID
     * @param name the name
     * @param description the description
     * @param required whether the template is required
     * @throws SQLException if an error occurs
     */
    private static void addPhotoTemplate(Connection connection, String templateId, String name, String description,
                                        boolean required) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO PHOTO_TEMPLATES (template_id, name, description, required, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, datetime('now'), datetime('now'))")) {
            stmt.setString(1, templateId);
            stmt.setString(2, name);
            stmt.setString(3, description);
            stmt.setBoolean(4, required);
            stmt.executeUpdate();
        }
    }

    /**
     * Adds test photos with different statuses to the database.
     * 
     * @param connection the database connection
     * @throws SQLException if an error occurs
     */
    private static void addTestPhotos(Connection connection) throws SQLException {
        LOGGER.info("Adding test photos for acceptance testing...");

        // Get a random order ID
        String orderId = getRandomOrderId(connection);
        if (orderId == null) {
            LOGGER.warning("No orders found in the database. Skipping photo creation.");
            return;
        }

        // Add a pending photo
        String pendingPhotoId = UUID.randomUUID().toString();
        addPhoto(connection, pendingPhotoId, orderId, "test/rat_pending_photo.jpg", "RAT_REQUIRED_VIEW", "PENDING", null);

        // Add an approved photo
        String approvedPhotoId = UUID.randomUUID().toString();
        addPhoto(connection, approvedPhotoId, orderId, "test/rat_approved_photo.jpg", "RAT_REQUIRED_VIEW", "APPROVED", null);

        // Add a rejected photo
        String rejectedPhotoId = UUID.randomUUID().toString();
        addPhoto(connection, rejectedPhotoId, orderId, "test/rat_rejected_photo.jpg", "RAT_OPTIONAL_VIEW", "REJECTED", null);

        LOGGER.info("Test photos added successfully");
    }

    /**
     * Gets a random order ID from the database.
     * 
     * @param connection the database connection
     * @return a random order ID, or null if no orders are found
     * @throws SQLException if an error occurs
     */
    private static String getRandomOrderId(Connection connection) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT order_id FROM ORDERS LIMIT 1")) {
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("order_id");
                }
            }
        }
        return null;
    }

    /**
     * Adds a photo to the database.
     * 
     * @param connection the database connection
     * @param photoId the photo ID
     * @param orderId the order ID
     * @param filePath the file path
     * @param templateType the template type
     * @param status the status
     * @param createdBy the user ID of the creator (can be null)
     * @throws SQLException if an error occurs
     */
    private static void addPhoto(Connection connection, String photoId, String orderId, String filePath,
                                String templateType, String status, String createdBy) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO PHOTOS (photo_id, order_id, file_path, template_type, status, created_by, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, datetime('now'), datetime('now'))")) {
            stmt.setString(1, photoId);
            stmt.setString(2, orderId);
            stmt.setString(3, filePath);
            stmt.setString(4, templateType);
            stmt.setString(5, status);
            if (createdBy != null) {
                stmt.setString(6, createdBy);
            } else {
                stmt.setNull(6, java.sql.Types.VARCHAR);
            }
            stmt.executeUpdate();
        }
    }

    /**
     * Adds test annotations for photos to the database.
     * 
     * @param connection the database connection
     * @throws SQLException if an error occurs
     */
    private static void addTestAnnotations(Connection connection) throws SQLException {
        LOGGER.info("Adding test annotations for acceptance testing...");

        // Get a random photo ID
        String photoId = getRandomPhotoId(connection);
        if (photoId == null) {
            LOGGER.warning("No photos found in the database. Skipping annotation creation.");
            return;
        }

        // Add an annotation
        String annotationId = UUID.randomUUID().toString();
        addAnnotation(connection, annotationId, photoId, 100.0, 100.0, 50.0, 50.0, "Acceptance test annotation", null);

        LOGGER.info("Test annotations added successfully");
    }

    /**
     * Gets a random photo ID from the database.
     * 
     * @param connection the database connection
     * @return a random photo ID, or null if no photos are found
     * @throws SQLException if an error occurs
     */
    private static String getRandomPhotoId(Connection connection) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT photo_id FROM PHOTOS LIMIT 1")) {
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("photo_id");
                }
            }
        }
        return null;
    }

    /**
     * Adds an annotation to the database.
     * 
     * @param connection the database connection
     * @param annotationId the annotation ID
     * @param photoId the photo ID
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width
     * @param height the height
     * @param text the text
     * @param createdBy the user ID of the creator (can be null)
     * @throws SQLException if an error occurs
     */
    private static void addAnnotation(Connection connection, String annotationId, String photoId, double x, double y,
                                     double width, double height, String text, String createdBy) throws SQLException {
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
            if (createdBy != null) {
                stmt.setString(8, createdBy);
            } else {
                stmt.setNull(8, java.sql.Types.VARCHAR);
            }
            stmt.executeUpdate();
        }
    }

    /**
     * Adds test reports to the database.
     * 
     * @param connection the database connection
     * @throws SQLException if an error occurs
     */
    private static void addTestReports(Connection connection) throws SQLException {
        LOGGER.info("Adding test reports for acceptance testing...");

        // Get a random order ID
        String orderId = getRandomOrderId(connection);
        if (orderId == null) {
            LOGGER.warning("No orders found in the database. Skipping report creation.");
            return;
        }

        // Add a pending report
        String pendingReportId = UUID.randomUUID().toString();
        addReport(connection, pendingReportId, orderId, "RAT Pending Report", "Report for acceptance testing (pending)", "PENDING", null);

        // Add an approved report
        String approvedReportId = UUID.randomUUID().toString();
        addReport(connection, approvedReportId, orderId, "RAT Approved Report", "Report for acceptance testing (approved)", "APPROVED", null);

        // Add a rejected report
        String rejectedReportId = UUID.randomUUID().toString();
        addReport(connection, rejectedReportId, orderId, "RAT Rejected Report", "Report for acceptance testing (rejected)", "REJECTED", null);

        LOGGER.info("Test reports added successfully");
    }

    /**
     * Adds a report to the database.
     * 
     * @param connection the database connection
     * @param reportId the report ID
     * @param orderId the order ID
     * @param title the title
     * @param description the description
     * @param status the status
     * @param createdBy the user ID of the creator (can be null)
     * @throws SQLException if an error occurs
     */
    private static void addReport(Connection connection, String reportId, String orderId, String title,
                                 String description, String status, String createdBy) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO REPORTS (report_id, order_id, title, description, status, created_by, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, datetime('now'), datetime('now'))")) {
            stmt.setString(1, reportId);
            stmt.setString(2, orderId);
            stmt.setString(3, title);
            stmt.setString(4, description);
            stmt.setString(5, status);
            if (createdBy != null) {
                stmt.setString(6, createdBy);
            } else {
                stmt.setNull(6, java.sql.Types.VARCHAR);
            }
            stmt.executeUpdate();
        }
    }
}
