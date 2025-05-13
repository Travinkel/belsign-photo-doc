package com.belman.test.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
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
                Properties props = loadDatabaseProperties();
                HikariConfig config = new HikariConfig();

                config.setJdbcUrl(props.getProperty("db.url"));
                // SQLite doesn't use username/password
                config.setDriverClassName(props.getProperty("db.driver"));

                // Connection pool settings
                config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maxSize", "10")));
                config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minIdle", "5")));
                config.setIdleTimeout(Long.parseLong(props.getProperty("db.pool.idleTimeout", "30000")));
                config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.connectionTimeout", "30000")));

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
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:db/migration")
                    .load();
            
            // Clean the database before running migrations (since it's an in-memory test database)
            flyway.clean();
            
            // Run the migrations
            flyway.migrate();
            
            LOGGER.info("Database migrations completed successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to run database migrations", e);
            throw new RuntimeException("Failed to run database migrations", e);
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
     * Closes the database connection pool.
     * This method should be called at the end of each test that required database access.
     */
    public static void shutdown() {
        if (dataSource != null) {
            try {
                if (!dataSource.isClosed()) {
                    dataSource.close();
                    LOGGER.info("Test database connection pool closed");
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error closing test database connection pool", e);
            }
        }
    }
}