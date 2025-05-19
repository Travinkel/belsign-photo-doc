package com.belman.bootstrap.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
     * It will create the database file and run migrations if the file doesn't exist.
     */
    public static void initialize() {
        if (dataSource == null) {
            try {
                // Check if the SQLite database file exists
                File dbFile = new File(SQLITE_DB_PATH);
                if (!dbFile.exists()) {
                    LOGGER.log(Level.INFO, "SQLite database file not found at: " + SQLITE_DB_PATH + ". Creating it...");

                    // Create the directory if it doesn't exist
                    Path dbDir = Paths.get(dbFile.getParent());
                    if (!Files.exists(dbDir)) {
                        Files.createDirectories(dbDir);
                        LOGGER.log(Level.INFO, "Created directory: " + dbDir);
                    }

                    // Create an empty database file
                    dbFile.createNewFile();
                    LOGGER.log(Level.INFO, "Created SQLite database file at: " + SQLITE_DB_PATH);
                }

                // Set up the JDBC URL for SQLite
                String jdbcUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

                // Run Flyway migrations
                runFlywayMigrations(jdbcUrl);

                // Configure HikariCP
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(jdbcUrl);
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
     * Runs Flyway migrations on the SQLite database.
     * 
     * @param jdbcUrl the JDBC URL for the SQLite database
     */
    private static void runFlywayMigrations(String jdbcUrl) {
        try {
            LOGGER.info("Running Flyway migrations on SQLite database...");

            Flyway flyway = Flyway.configure()
                .dataSource(jdbcUrl, null, null)
                .locations("classpath:sqlitedb/migration")
                .baselineOnMigrate(true)
                .outOfOrder(true)
                .sqlMigrationSeparator(";")
                .mixed(true)
                .validateMigrationNaming(false)
                .cleanDisabled(true)
                .group(true)
                .load();

            // Clean the database (for development only - remove in production)
            // flyway.clean();

            // Run the migrations
            var migrateResult = flyway.migrate();

            LOGGER.info("Applied " + migrateResult.migrationsExecuted + " Flyway migrations to SQLite database");
        } catch (Exception e) {
            // Check if the error is related to duplicate column, table exists, or other common SQLite migration issues
            if (e.getMessage() != null && 
                (e.getMessage().contains("duplicate column name") || 
                 e.getMessage().contains("table already exists") ||
                 e.getMessage().contains("syntax error") ||
                 e.getMessage().contains("already exists") ||
                 e.getMessage().contains("SQLITE_ERROR") ||
                 e.getMessage().contains("Unable to parse") ||
                 e.getMessage().contains("Incomplete statement"))) {
                LOGGER.warning("Ignoring migration error related to SQLite constraints: " + e.getMessage());
                // Don't rethrow, just log and continue
                // This allows the application to start even if some migrations fail
            } else {
                LOGGER.log(Level.SEVERE, "Failed to run Flyway migrations on SQLite database", e);
                // Don't throw the exception, just log it and continue
                // This ensures the application can start even with migration errors
                // The application will use whatever schema is available
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
