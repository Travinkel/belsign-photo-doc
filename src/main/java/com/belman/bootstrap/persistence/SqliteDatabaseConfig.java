package com.belman.bootstrap.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration class for SQLite database connectivity.
 * Sets up a connection pool using HikariCP and provides a DataSource for the SQLite database.
 */
public class SqliteDatabaseConfig {
    private static final Logger LOGGER = Logger.getLogger(SqliteDatabaseConfig.class.getName());
    private static final String SQLITE_DB_PATH = "src/main/resources/sqlitedb/mydb.db";
    private static HikariDataSource dataSource;

    /**
     * Initializes the SQLite database connection pool.
     * This method should be called when the main database connection fails.
     */
    public static void initialize() {
        if (dataSource == null) {
            try {
                // Check if the SQLite database file exists
                File dbFile = new File(SQLITE_DB_PATH);
                if (!dbFile.exists()) {
                    LOGGER.log(Level.WARNING, "SQLite database file not found at: " + SQLITE_DB_PATH);
                    return;
                }

                HikariConfig config = new HikariConfig();
                config.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
                // SQLite doesn't use username/password
                config.setDriverClassName("org.sqlite.JDBC");

                // Connection pool settings
                config.setMaximumPoolSize(5);
                config.setMinimumIdle(1);
                config.setIdleTimeout(30000);
                config.setConnectionTimeout(30000);

                // Set pool name for easier debugging
                config.setPoolName("BelSignSQLiteHikariPool");

                dataSource = new HikariDataSource(config);
                LOGGER.info("SQLite database connection pool initialized successfully");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize SQLite database connection pool", e);
                // Don't throw an exception, just log it and return
                // The application will fall back to using in-memory repositories
            }
        }
    }

    /**
     * Gets the DataSource for SQLite database connections.
     *
     * @return the DataSource, or null if the DataSource has not been initialized or failed to initialize
     */
    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Closes the SQLite database connection pool.
     * This method should be called during application shutdown.
     */
    public static void shutdown() {
        if (dataSource != null) {
            try {
                if (!dataSource.isClosed()) {
                    dataSource.close();
                    LOGGER.info("SQLite database connection pool closed");
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error closing SQLite database connection pool", e);
            }
        }
    }
}