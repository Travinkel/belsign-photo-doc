package com.belman.integration.database;

import com.belman.bootstrap.persistence.SqliteDatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for database operations in tests.
 * This class provides methods for initializing, resetting, and populating the test database.
 */
public class TestDatabaseUtil {
    private static final Logger LOGGER = Logger.getLogger(TestDatabaseUtil.class.getName());
    private static final String TEST_DB_PATH = "src/test/resources/sqlitedb/testdb.db";
    private static DataSource testDataSource;

    /**
     * Initializes the test database.
     * This method creates a new test database file if it doesn't exist,
     * creates a minimal schema, and returns a DataSource for the test database.
     *
     * @return a DataSource for the test database
     */
    public static DataSource initializeTestDatabase() {
        if (testDataSource == null) {
            try {
                // Check if the test database file exists
                File dbFile = new File(TEST_DB_PATH);
                if (dbFile.exists()) {
                    // Delete the existing test database file
                    dbFile.delete();
                    LOGGER.info("Deleted existing test database file: " + TEST_DB_PATH);
                }

                // Create the directory if it doesn't exist
                Path dbDir = Paths.get(dbFile.getParent());
                if (!Files.exists(dbDir)) {
                    Files.createDirectories(dbDir);
                    LOGGER.info("Created directory: " + dbDir);
                }

                // Create an empty database file
                dbFile.createNewFile();
                LOGGER.info("Created test database file at: " + TEST_DB_PATH);

                // Set up the JDBC URL for SQLite
                String jdbcUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

                // Create a minimal schema directly using SQL
                createMinimalSchema(jdbcUrl);

                // Create a DataSource for the test database
                com.zaxxer.hikari.HikariConfig config = new com.zaxxer.hikari.HikariConfig();
                config.setJdbcUrl(jdbcUrl);
                config.setDriverClassName("org.sqlite.JDBC");
                config.addDataSourceProperty("foreign_keys", "true");
                config.setMaximumPoolSize(5);
                config.setMinimumIdle(1);
                config.setIdleTimeout(30000);
                config.setConnectionTimeout(30000);
                config.setPoolName("TestDatabaseHikariPool");

                testDataSource = new com.zaxxer.hikari.HikariDataSource(config);

                // Initialize SqliteDatabaseConfig as a fallback if our direct initialization fails
                if (testDataSource == null) {
                    LOGGER.info("Direct initialization failed, trying SqliteDatabaseConfig as fallback");
                    SqliteDatabaseConfig.initialize();
                    testDataSource = SqliteDatabaseConfig.getDataSource();
                }

                LOGGER.info("Test database initialized successfully");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize test database directly, trying SqliteDatabaseConfig as fallback", e);

                // Try using SqliteDatabaseConfig as a fallback
                try {
                    SqliteDatabaseConfig.initialize();
                    testDataSource = SqliteDatabaseConfig.getDataSource();
                    if (testDataSource != null) {
                        LOGGER.info("Successfully initialized test database using SqliteDatabaseConfig fallback");
                    } else {
                        LOGGER.severe("SqliteDatabaseConfig fallback also failed to provide a valid DataSource");
                        throw new RuntimeException("Failed to initialize test database with all methods", e);
                    }
                } catch (Exception fallbackException) {
                    LOGGER.log(Level.SEVERE, "SqliteDatabaseConfig fallback also failed", fallbackException);
                    throw new RuntimeException("Failed to initialize test database with all methods", fallbackException);
                }
            }
        }
        return testDataSource;
    }

    /**
     * Creates a minimal schema for the test database.
     * 
     * @param jdbcUrl the JDBC URL for the test database
     */
    private static void createMinimalSchema(String jdbcUrl) {
        try {
            LOGGER.info("Creating minimal schema for test database...");

            try (java.sql.Connection conn = java.sql.DriverManager.getConnection(jdbcUrl);
                 java.sql.Statement stmt = conn.createStatement()) {

                // Enable foreign keys
                stmt.execute("PRAGMA foreign_keys = ON");

                // Create users table
                stmt.execute("CREATE TABLE IF NOT EXISTS USERS (" +
                             "user_id TEXT PRIMARY KEY, " +
                             "username TEXT NOT NULL UNIQUE, " +
                             "password TEXT, " +
                             "email TEXT, " +
                             "first_name TEXT, " +
                             "last_name TEXT, " +
                             "role TEXT, " +
                             "created_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                             "updated_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                             "approved INTEGER DEFAULT 1)");

                // Insert test users
                stmt.execute("INSERT OR IGNORE INTO USERS (user_id, username, password, email, first_name, last_name, role, approved) " +
                             "VALUES ('11111111-1111-1111-1111-111111111111', 'admin', 'password', 'admin@example.com', 'Admin', 'User', 'ADMIN', 1)");

                stmt.execute("INSERT OR IGNORE INTO USERS (user_id, username, password, email, first_name, last_name, role, approved) " +
                             "VALUES ('22222222-2222-2222-2222-222222222222', 'production', 'password', 'production@example.com', 'Production', 'Worker', 'PRODUCTION', 1)");

                stmt.execute("INSERT OR IGNORE INTO USERS (user_id, username, password, email, first_name, last_name, role, approved) " +
                             "VALUES ('33333333-3333-3333-3333-333333333333', 'qa', 'password', 'qa@example.com', 'QA', 'User', 'QA', 1)");

                // Create user_roles table
                stmt.execute("CREATE TABLE IF NOT EXISTS USER_ROLES (" +
                             "user_id TEXT NOT NULL, " +
                             "role TEXT NOT NULL, " +
                             "PRIMARY KEY (user_id, role), " +
                             "FOREIGN KEY (user_id) REFERENCES USERS(user_id))");

                // Insert user roles
                stmt.execute("INSERT OR IGNORE INTO USER_ROLES (user_id, role) " +
                             "VALUES ('11111111-1111-1111-1111-111111111111', 'ADMIN')");

                stmt.execute("INSERT OR IGNORE INTO USER_ROLES (user_id, role) " +
                             "VALUES ('22222222-2222-2222-2222-222222222222', 'PRODUCTION')");

                stmt.execute("INSERT OR IGNORE INTO USER_ROLES (user_id, role) " +
                             "VALUES ('33333333-3333-3333-3333-333333333333', 'QA')");

                // Create orders table
                stmt.execute("CREATE TABLE IF NOT EXISTS ORDERS (" +
                             "order_id TEXT PRIMARY KEY, " +
                             "order_number TEXT NOT NULL UNIQUE, " +
                             "customer_id TEXT, " +
                             "description TEXT, " +
                             "status TEXT, " +
                             "assigned_to TEXT, " +
                             "created_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                             "updated_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                             "FOREIGN KEY (assigned_to) REFERENCES USERS(user_id))");

                // Insert test orders
                stmt.execute("INSERT OR IGNORE INTO ORDERS (order_id, order_number, description, status, assigned_to) " +
                             "VALUES ('77777777-7777-7777-7777-777777777777', 'ORD-XX-230101-ABC-0001', 'Test Order 1', 'PENDING', '22222222-2222-2222-2222-222222222222')");

                stmt.execute("INSERT OR IGNORE INTO ORDERS (order_id, order_number, description, status, assigned_to) " +
                             "VALUES ('88888888-8888-8888-8888-888888888888', 'ORD-XX-230102-ABC-0002', 'Test Order 2', 'PENDING', '22222222-2222-2222-2222-222222222222')");

                stmt.execute("INSERT OR IGNORE INTO ORDERS (order_id, order_number, description, status) " +
                             "VALUES ('99999999-9999-9999-9999-999999999999', 'ORD-XX-230103-ABC-0003', 'Test Order 3', 'PENDING')");

                // Create photo_templates table
                stmt.execute("CREATE TABLE IF NOT EXISTS PHOTO_TEMPLATES (" +
                             "template_id TEXT PRIMARY KEY, " +
                             "name TEXT NOT NULL, " +
                             "description TEXT, " +
                             "required BOOLEAN DEFAULT 0, " +
                             "created_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                             "updated_at TEXT DEFAULT CURRENT_TIMESTAMP)");

                // Insert test photo templates
                stmt.execute("INSERT OR IGNORE INTO PHOTO_TEMPLATES (template_id, name, description, required) " +
                             "VALUES ('44444444-4444-4444-4444-444444444444', 'TOP_VIEW', 'Top view of the joint', 1)");

                stmt.execute("INSERT OR IGNORE INTO PHOTO_TEMPLATES (template_id, name, description, required) " +
                             "VALUES ('55555555-5555-5555-5555-555555555555', 'SIDE_VIEW', 'Side view of the weld', 1)");

                stmt.execute("INSERT OR IGNORE INTO PHOTO_TEMPLATES (template_id, name, description, required) " +
                             "VALUES ('66666666-6666-6666-6666-666666666666', 'FRONT_VIEW', 'Front view of the assembly', 0)");

                stmt.execute("INSERT OR IGNORE INTO PHOTO_TEMPLATES (template_id, name, description, required) " +
                             "VALUES ('77777777-7777-7777-7777-777777777778', 'BACK_VIEW', 'Back view of the assembly', 0)");

                // Create order_photo_templates table
                stmt.execute("CREATE TABLE IF NOT EXISTS ORDER_PHOTO_TEMPLATES (" +
                             "order_id TEXT NOT NULL, " +
                             "template_id TEXT NOT NULL, " +
                             "required BOOLEAN DEFAULT 0, " +
                             "PRIMARY KEY (order_id, template_id), " +
                             "FOREIGN KEY (order_id) REFERENCES ORDERS(order_id), " +
                             "FOREIGN KEY (template_id) REFERENCES PHOTO_TEMPLATES(template_id))");

                // Associate templates with orders
                stmt.execute("INSERT OR IGNORE INTO ORDER_PHOTO_TEMPLATES (order_id, template_id, required) " +
                             "VALUES ('77777777-7777-7777-7777-777777777777', '44444444-4444-4444-4444-444444444444', 1)");

                stmt.execute("INSERT OR IGNORE INTO ORDER_PHOTO_TEMPLATES (order_id, template_id, required) " +
                             "VALUES ('77777777-7777-7777-7777-777777777777', '55555555-5555-5555-5555-555555555555', 1)");

                stmt.execute("INSERT OR IGNORE INTO ORDER_PHOTO_TEMPLATES (order_id, template_id, required) " +
                             "VALUES ('88888888-8888-8888-8888-888888888888', '44444444-4444-4444-4444-444444444444', 1)");

                stmt.execute("INSERT OR IGNORE INTO ORDER_PHOTO_TEMPLATES (order_id, template_id, required) " +
                             "VALUES ('88888888-8888-8888-8888-888888888888', '66666666-6666-6666-6666-666666666666', 0)");

                stmt.execute("INSERT OR IGNORE INTO ORDER_PHOTO_TEMPLATES (order_id, template_id, required) " +
                             "VALUES ('99999999-9999-9999-9999-999999999999', '55555555-5555-5555-5555-555555555555', 1)");

                // Create photos table
                stmt.execute("CREATE TABLE IF NOT EXISTS PHOTOS (" +
                             "photo_id TEXT PRIMARY KEY, " +
                             "order_id TEXT NOT NULL, " +
                             "file_path TEXT NOT NULL, " +
                             "template_type TEXT, " +
                             "status TEXT, " +
                             "created_by TEXT, " +
                             "created_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                             "updated_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                             "FOREIGN KEY (order_id) REFERENCES ORDERS(order_id), " +
                             "FOREIGN KEY (created_by) REFERENCES USERS(user_id))");

                // Insert test photos
                stmt.execute("INSERT OR IGNORE INTO PHOTOS (photo_id, order_id, file_path, template_type, status, created_by) " +
                             "VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '77777777-7777-7777-7777-777777777777', 'test/photo1.jpg', 'TOP_VIEW', 'APPROVED', '22222222-2222-2222-2222-222222222222')");

                stmt.execute("INSERT OR IGNORE INTO PHOTOS (photo_id, order_id, file_path, template_type, status, created_by) " +
                             "VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '77777777-7777-7777-7777-777777777777', 'test/photo2.jpg', 'SIDE_VIEW', 'PENDING', '22222222-2222-2222-2222-222222222222')");

                stmt.execute("INSERT OR IGNORE INTO PHOTOS (photo_id, order_id, file_path, template_type, status, created_by) " +
                             "VALUES ('cccccccc-cccc-cccc-cccc-cccccccccccc', '88888888-8888-8888-8888-888888888888', 'test/photo3.jpg', 'TOP_VIEW', 'APPROVED', '22222222-2222-2222-2222-222222222222')");

                stmt.execute("INSERT OR IGNORE INTO PHOTOS (photo_id, order_id, file_path, template_type, status, created_by) " +
                             "VALUES ('dddddddd-dddd-dddd-dddd-dddddddddddd', '99999999-9999-9999-9999-999999999999', 'test/photo4.jpg', 'SIDE_VIEW', 'REJECTED', '22222222-2222-2222-2222-222222222222')");

                // Create photo_annotations table
                stmt.execute("CREATE TABLE IF NOT EXISTS PHOTO_ANNOTATIONS (" +
                             "annotation_id TEXT PRIMARY KEY, " +
                             "photo_id TEXT NOT NULL, " +
                             "x REAL, " +
                             "y REAL, " +
                             "width REAL, " +
                             "height REAL, " +
                             "text TEXT, " +
                             "created_by TEXT, " +
                             "created_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                             "updated_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                             "FOREIGN KEY (photo_id) REFERENCES PHOTOS(photo_id), " +
                             "FOREIGN KEY (created_by) REFERENCES USERS(user_id))");

                // Insert test annotations
                stmt.execute("INSERT OR IGNORE INTO PHOTO_ANNOTATIONS (annotation_id, photo_id, x, y, width, height, text, created_by) " +
                             "VALUES ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 100, 100, 50, 50, 'Weld joint', '33333333-3333-3333-3333-333333333333')");

                stmt.execute("INSERT OR IGNORE INTO PHOTO_ANNOTATIONS (annotation_id, photo_id, x, y, width, height, text, created_by) " +
                             "VALUES ('ffffffff-ffff-ffff-ffff-ffffffffffff', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 200, 150, 75, 30, 'Surface defect', '33333333-3333-3333-3333-333333333333')");

                stmt.execute("INSERT OR IGNORE INTO PHOTO_ANNOTATIONS (annotation_id, photo_id, x, y, width, height, text, created_by) " +
                             "VALUES ('gggggggg-gggg-gggg-gggg-gggggggggggg', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 150, 200, 60, 40, 'Measurement point', '33333333-3333-3333-3333-333333333333')");

                // Create reports table
                stmt.execute("CREATE TABLE IF NOT EXISTS REPORTS (" +
                             "report_id TEXT PRIMARY KEY, " +
                             "order_id TEXT NOT NULL, " +
                             "title TEXT NOT NULL, " +
                             "description TEXT, " +
                             "status TEXT, " +
                             "created_by TEXT, " +
                             "created_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                             "updated_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                             "FOREIGN KEY (order_id) REFERENCES ORDERS(order_id), " +
                             "FOREIGN KEY (created_by) REFERENCES USERS(user_id))");

                // Insert test reports
                stmt.execute("INSERT OR IGNORE INTO REPORTS (report_id, order_id, title, description, status, created_by) " +
                             "VALUES ('hhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh', '77777777-7777-7777-7777-777777777777', 'QC Report 1', 'Quality control report for order 1', 'APPROVED', '33333333-3333-3333-3333-333333333333')");

                stmt.execute("INSERT OR IGNORE INTO REPORTS (report_id, order_id, title, description, status, created_by) " +
                             "VALUES ('iiiiiiii-iiii-iiii-iiii-iiiiiiiiiiii', '88888888-8888-8888-8888-888888888888', 'QC Report 2', 'Quality control report for order 2', 'PENDING', '33333333-3333-3333-3333-333333333333')");

                stmt.execute("INSERT OR IGNORE INTO REPORTS (report_id, order_id, title, description, status, created_by) " +
                             "VALUES ('jjjjjjjj-jjjj-jjjj-jjjj-jjjjjjjjjjjj', '99999999-9999-9999-9999-999999999999', 'QC Report 3', 'Quality control report for order 3', 'REJECTED', '33333333-3333-3333-3333-333333333333')");

                // Create report_photos table
                stmt.execute("CREATE TABLE IF NOT EXISTS REPORT_PHOTOS (" +
                             "report_id TEXT NOT NULL, " +
                             "photo_id TEXT NOT NULL, " +
                             "sequence_number INTEGER, " +
                             "PRIMARY KEY (report_id, photo_id), " +
                             "FOREIGN KEY (report_id) REFERENCES REPORTS(report_id), " +
                             "FOREIGN KEY (photo_id) REFERENCES PHOTOS(photo_id))");

                // Associate photos with reports
                stmt.execute("INSERT OR IGNORE INTO REPORT_PHOTOS (report_id, photo_id, sequence_number) " +
                             "VALUES ('hhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 1)");

                stmt.execute("INSERT OR IGNORE INTO REPORT_PHOTOS (report_id, photo_id, sequence_number) " +
                             "VALUES ('hhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 2)");

                stmt.execute("INSERT OR IGNORE INTO REPORT_PHOTOS (report_id, photo_id, sequence_number) " +
                             "VALUES ('iiiiiiii-iiii-iiii-iiii-iiiiiiiiiiii', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 1)");

                stmt.execute("INSERT OR IGNORE INTO REPORT_PHOTOS (report_id, photo_id, sequence_number) " +
                             "VALUES ('jjjjjjjj-jjjj-jjjj-jjjj-jjjjjjjjjjjj', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 1)");

                LOGGER.info("Minimal schema created successfully for test database");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to create minimal schema for test database", e);
            throw new RuntimeException("Failed to create minimal schema for test database", e);
        }
    }

    /**
     * Resets the test database by recreating the minimal schema.
     * This method should be called before each test to ensure a clean state.
     */
    public static void resetTestDatabase() {
        try {
            LOGGER.info("Resetting test database...");

            // Set up the JDBC URL for SQLite
            File dbFile = new File(TEST_DB_PATH);
            String jdbcUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

            // Delete all data from tables
            try (java.sql.Connection conn = testDataSource.getConnection();
                 java.sql.Statement stmt = conn.createStatement()) {

                // Disable foreign key constraints temporarily
                stmt.execute("PRAGMA foreign_keys = OFF");

                // Delete all data from tables
                stmt.execute("DELETE FROM PHOTOS");
                stmt.execute("DELETE FROM ORDERS");
                stmt.execute("DELETE FROM USERS");

                // Re-enable foreign key constraints
                stmt.execute("PRAGMA foreign_keys = ON");

                LOGGER.info("Deleted all data from test database tables");
            }

            // Recreate the minimal schema
            createMinimalSchema(jdbcUrl);

            LOGGER.info("Test database reset successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to reset test database", e);
            throw new RuntimeException("Failed to reset test database", e);
        }
    }

    /**
     * Executes a SQL script on the test database.
     * 
     * @param sql the SQL script to execute
     */
    public static void executeSql(String sql) {
        try (Connection connection = testDataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
            LOGGER.info("Executed SQL script on test database");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to execute SQL script on test database", e);
            throw new RuntimeException("Failed to execute SQL script on test database", e);
        }
    }

    /**
     * Shuts down the test database connection.
     * This method should be called after all tests are complete.
     */
    public static void shutdownTestDatabase() {
        if (testDataSource != null && testDataSource instanceof com.zaxxer.hikari.HikariDataSource) {
            try {
                ((com.zaxxer.hikari.HikariDataSource) testDataSource).close();
                LOGGER.info("Test database connection pool closed");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error closing test database connection pool", e);
            } finally {
                testDataSource = null;
            }
        }
    }
}
