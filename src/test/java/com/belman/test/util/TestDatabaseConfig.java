package com.belman.test.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration class for SQLite database connectivity in tests.
 * Sets up an in-memory SQLite database with Flyway migrations.
 */
public class TestDatabaseConfig {
    private static final Logger LOGGER = Logger.getLogger(TestDatabaseConfig.class.getName());
    private static final String DB_PROPERTIES_FILE = "database.properties";
    private static HikariDataSource dataSource;

    /**
     * Initializes the SQLite database for testing.
     * This method should be called at the beginning of each test that requires database access.
     */
    public static void initialize() {
        if (dataSource == null) {
            try {
                HikariConfig config = new HikariConfig();

                // Use SQLite in-memory database for testing with a shared name
                // This ensures all connections use the same in-memory database
                config.setJdbcUrl("jdbc:sqlite:file:memdb1?mode=memory&cache=shared");
                // SQLite doesn't use username/password
                config.setDriverClassName("org.sqlite.JDBC");

                // Connection pool settings
                config.setMaximumPoolSize(10);
                config.setMinimumIdle(5);
                config.setIdleTimeout(30000);
                config.setConnectionTimeout(30000);

                // Set pool name for easier debugging
                config.setPoolName("BelSignTestHikariPool");

                dataSource = new HikariDataSource(config);
                LOGGER.info("Test database connection pool initialized successfully");

                // Run Flyway migrations to set up the schema
                runMigrations();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize test database connection pool", e);
                throw new RuntimeException("Failed to initialize test database", e);
            }
        }
    }

    /**
     * Runs Flyway migrations to set up the database schema.
     */
    private static void runMigrations() {
        try {
            // For SQLite, we'll create the tables directly instead of using Flyway migrations
            // This is because the SQL Server migrations might use features not supported by SQLite
            createTestTables();

            LOGGER.info("Test database tables created successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to create test database tables", e);
            throw new RuntimeException("Failed to create test database tables", e);
        }
    }

    /**
     * Creates the necessary tables for testing.
     */
    private static void createTestTables() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            // Create users table
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                             "id VARCHAR(36) PRIMARY KEY, " +
                             "username VARCHAR(50) NOT NULL UNIQUE, " +
                             "password VARCHAR(100) NOT NULL, " +
                             "first_name VARCHAR(50), " +
                             "last_name VARCHAR(50), " +
                             "email VARCHAR(100) NOT NULL UNIQUE, " +
                             "status VARCHAR(20) NOT NULL, " +
                             "approval_state VARCHAR(20) NOT NULL DEFAULT 'APPROVED', " +
                             "phone_number VARCHAR(20), " +
                             "pin_code VARCHAR(10), " +
                             "qr_code_hash VARCHAR(100), " +
                             "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                             "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                             ")");

                // Create user_roles table
                stmt.execute("CREATE TABLE IF NOT EXISTS user_roles (" +
                             "user_id VARCHAR(36) NOT NULL, " +
                             "role VARCHAR(20) NOT NULL, " +
                             "PRIMARY KEY (user_id, role), " +
                             "FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE" +
                             ")");

                // Create indexes
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_users_username ON users (username)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_users_email ON users (email)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_users_pin_code ON users (pin_code)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_users_qr_code_hash ON users (qr_code_hash)");
            }
        }
    }

    /**
     * Loads database properties from the properties file.
     *
     * @return the database properties
     * @throws IOException if the properties file cannot be loaded
     */
    private static Properties loadDatabaseProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = TestDatabaseConfig.class.getClassLoader().getResourceAsStream(DB_PROPERTIES_FILE)) {
            if (input == null) {
                throw new IOException("Unable to find " + DB_PROPERTIES_FILE);
            }
            props.load(input);
        }
        return props;
    }

    /**
     * Gets the DataSource for database connections.
     *
     * @return the DataSource, or null if the DataSource has not been initialized or failed to initialize
     */
    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Resets the database connection pool.
     * This method should be called at the end of each test that required database access.
     * <p>
     * Note: We don't actually close the data source here to avoid issues with multiple tests
     * using the same data source. Instead, we'll rely on JVM shutdown to close the data source.
     */
    public static void shutdown() {
        // Don't close the data source, just log that we're done with it
        LOGGER.info("Test database connection pool reset");
    }
}
