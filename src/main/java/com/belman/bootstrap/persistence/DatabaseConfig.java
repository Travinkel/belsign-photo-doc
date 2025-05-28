package com.belman.bootstrap.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration class for database connectivity.
 * Sets up a connection pool using HikariCP and provides a DataSource.
 */
public final class DatabaseConfig {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    private static final String DB_PROPERTIES_FILE = "database.properties";
    private static HikariDataSource dataSource;

    /**
     * Initializes the database connection pool.
     * This method should be called during application startup.
     */
    public static void initialize() {
        if (dataSource == null) {
            try {
                Properties props = loadDatabaseProperties();
                HikariConfig config = new HikariConfig();

                config.setJdbcUrl(props.getProperty("db.url"));
                config.setUsername(props.getProperty("db.username"));
                config.setPassword(props.getProperty("db.password"));
                config.setDriverClassName(props.getProperty("db.driver"));

                // Connection pool settings
                config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maxSize", "10")));
                config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minIdle", "5")));
                config.setIdleTimeout(Long.parseLong(props.getProperty("db.pool.idleTimeout", "30000")));
                config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.connectionTimeout", "30000")));

                // Set pool name for easier debugging
                config.setPoolName("BelSignHikariPool");

                dataSource = new HikariDataSource(config);
                LOGGER.info("Database connection pool initialized successfully");

                // Run database migrations
                if (DatabaseMigrationManager.runMigrations(dataSource)) {
                    LOGGER.info("Database migrations completed successfully");
                } else {
                    LOGGER.warning("Database migrations failed or were not applied");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize database connection pool", e);
                // Don't throw an exception, just log it and return
                // The application will fall back to using in-memory repositories
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
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream(DB_PROPERTIES_FILE)) {
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
     * Closes the database connection pool.
     * This method should be called during application shutdown.
     */
    public static void shutdown() {
        if (dataSource != null) {
            try {
                if (!dataSource.isClosed()) {
                    dataSource.close();
                    LOGGER.info("Database connection pool closed");
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error closing database connection pool", e);
            }
        }
    }
}
