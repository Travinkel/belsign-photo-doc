package com.belman.bootstrap.persistence.secure;

import com.belman.common.config.SecureConfigStorage;
import com.belman.common.logging.EmojiLoggerFactory;
import com.belman.domain.services.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * Secure configuration class for database connectivity.
 * Uses SecureConfigStorage to store and retrieve database credentials securely.
 * Sets up a connection pool using HikariCP and provides a DataSource.
 * This is a utility class with static methods and should not be instantiated.
 */
public final class SecureDatabaseConfig {
    private static final Logger LOGGER = EmojiLoggerFactory.getInstance().getLogger(SecureDatabaseConfig.class);
    private static final String DB_PROPERTIES_FILE = "database.properties";
    // Configuration keys
    private static final String DB_URL_KEY = "db.url";
    private static final String DB_USERNAME_KEY = "db.username";
    private static final String DB_PASSWORD_KEY = "db.password";
    private static final String DB_DRIVER_KEY = "db.driver";
    private static final String DB_POOL_MAX_SIZE_KEY = "db.pool.maxSize";
    private static final String DB_POOL_MIN_IDLE_KEY = "db.pool.minIdle";
    private static final String DB_POOL_IDLE_TIMEOUT_KEY = "db.pool.idleTimeout";
    private static final String DB_POOL_CONNECTION_TIMEOUT_KEY = "db.pool.connectionTimeout";
    private static HikariDataSource dataSource;

    /**
     * Private constructor to prevent instantiation.
     */
    private SecureDatabaseConfig() {
        // Utility class should not be instantiated
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
     * Updates a database configuration value in secure storage.
     *
     * @param key   the configuration key
     * @param value the configuration value
     * @return true if the value was updated successfully, false otherwise
     */
    public static boolean updateConfigValue(String key, String value) {
        try {
            SecureConfigStorage secureStorage = SecureConfigStorage.getInstance();
            boolean stored = secureStorage.storeValue(key, value);

            if (stored) {
                LOGGER.info("Database configuration value updated: " + key);
                return true;
            } else {
                LOGGER.warn("Failed to update database configuration value: " + key);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("Error updating database configuration value: " + key, e);
            return false;
        }
    }

    /**
     * Reinitializes the database connection pool with updated configuration.
     * This method should be called after updating configuration values.
     *
     * @return true if reinitialization was successful, false otherwise
     */
    public static boolean reinitialize() {
        try {
            // Close existing connection pool if it exists
            shutdown();

            // Reset dataSource to null to force reinitialization
            dataSource = null;

            // Initialize with updated configuration
            initialize();

            return dataSource != null;
        } catch (Exception e) {
            LOGGER.error("Failed to reinitialize secure database connection pool", e);
            return false;
        }
    }

    /**
     * Closes the database connection pool.
     * This method should be called during application shutdown.
     */
    public static void shutdown() {
        if (dataSource != null) {
            try {
                if (!dataSource.isClosed()) {
                    dataSource.close();
                    LOGGER.info("Secure database connection pool closed");
                }
            } catch (Exception e) {
                LOGGER.warn("Error closing secure database connection pool", e);
            }
        }
    }

    /**
     * Initializes the database connection pool.
     * This method should be called during application startup.
     */
    public static void initialize() {
        if (dataSource == null) {
            try {
                // Get the secure config storage instance
                SecureConfigStorage secureStorage = SecureConfigStorage.getInstance();

                // Check if secure storage has been initialized with database properties
                if (secureStorage.getValue(DB_URL_KEY) == null) {
                    // Import database properties from the properties file
                    boolean imported = secureStorage.importFromProperties(DB_PROPERTIES_FILE);
                    if (!imported) {
                        LOGGER.error("Failed to import database properties from " + DB_PROPERTIES_FILE);
                        return;
                    }
                    LOGGER.info("Database properties imported to secure storage");
                }

                // Configure HikariCP with secure properties
                HikariConfig config = new HikariConfig();

                config.setJdbcUrl(secureStorage.getValue(DB_URL_KEY));
                config.setUsername(secureStorage.getValue(DB_USERNAME_KEY));
                config.setPassword(secureStorage.getValue(DB_PASSWORD_KEY));
                config.setDriverClassName(secureStorage.getValue(DB_DRIVER_KEY));

                // Connection pool settings
                config.setMaximumPoolSize(Integer.parseInt(secureStorage.getValue(DB_POOL_MAX_SIZE_KEY, "10")));
                config.setMinimumIdle(Integer.parseInt(secureStorage.getValue(DB_POOL_MIN_IDLE_KEY, "5")));
                config.setIdleTimeout(Long.parseLong(secureStorage.getValue(DB_POOL_IDLE_TIMEOUT_KEY, "30000")));
                config.setConnectionTimeout(
                        Long.parseLong(secureStorage.getValue(DB_POOL_CONNECTION_TIMEOUT_KEY, "30000")));

                // Set pool name for easier debugging
                config.setPoolName("BelSignSecureHikariPool");

                dataSource = new HikariDataSource(config);
                LOGGER.info("Secure database connection pool initialized successfully");
            } catch (Exception e) {
                LOGGER.error("Failed to initialize secure database connection pool", e);
                // Don't throw an exception, just log it and return
                // The application will fall back to using in-memory repositories
            }
        }
    }
}
