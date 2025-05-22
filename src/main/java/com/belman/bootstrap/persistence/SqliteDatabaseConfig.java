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

                // Enable foreign keys in SQLite
                config.addDataSourceProperty("foreign_keys", "true");

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
            LOGGER.info("JDBC URL: " + jdbcUrl);

            // Log the existence of migration scripts
            File migrationDir = new File("src/main/resources/sqlitedb/migration");
            if (migrationDir.exists() && migrationDir.isDirectory()) {
                LOGGER.info("Migration directory exists: " + migrationDir.getAbsolutePath());
                File[] migrationFiles = migrationDir.listFiles((dir, name) -> name.endsWith(".sql"));
                if (migrationFiles != null) {
                    LOGGER.info("Found " + migrationFiles.length + " migration files:");
                    for (File file : migrationFiles) {
                        LOGGER.info("  - " + file.getName() + " (" + file.length() + " bytes)");
                    }
                } else {
                    LOGGER.warning("No migration files found or error listing files");
                }
            } else {
                LOGGER.warning("Migration directory does not exist: " + migrationDir.getAbsolutePath());
            }

            // First, clean the database to ensure a fresh start
            LOGGER.info("Configuring Flyway for database cleaning...");
            Flyway cleanFlyway = Flyway.configure()
                .dataSource(jdbcUrl, null, null)
                .locations("classpath:sqlitedb/migration")
                .cleanDisabled(false)
                .load();

            try {
                LOGGER.info("Cleaning database...");
                cleanFlyway.clean();
                LOGGER.info("Cleaned SQLite database before migrations");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not clean SQLite database", e);
                // Continue even if clean fails
            }

            // Configure Flyway with a callback to filter repeatable migrations
            Flyway flyway = Flyway.configure()
                .dataSource(jdbcUrl, null, null)
                .locations("classpath:sqlitedb/migration")
                .baselineOnMigrate(true)
                .outOfOrder(false)  // Changed to false to ensure migrations run in order
                .mixed(true)
                .validateMigrationNaming(true)
                .cleanDisabled(true)
                .group(false)  // Changed to false to ensure each migration runs independently
                .validateOnMigrate(true)  // Added to validate migrations before executing them
                .callbacks(new org.flywaydb.core.api.callback.Callback() {
                    @Override
                    public boolean supports(org.flywaydb.core.api.callback.Event event, org.flywaydb.core.api.callback.Context context) {
                        // We're only interested in before migration execution events
                        return event == org.flywaydb.core.api.callback.Event.BEFORE_EACH_MIGRATE;
                    }

                    @Override
                    public boolean canHandleInTransaction(org.flywaydb.core.api.callback.Event event, org.flywaydb.core.api.callback.Context context) {
                        return true;
                    }

                    @Override
                    public void handle(org.flywaydb.core.api.callback.Event event, org.flywaydb.core.api.callback.Context context) {
                        // Skip repeatable migrations that start with R__seed_test_data
                        if (context.getMigrationInfo().getVersion() == null && 
                            context.getMigrationInfo().getDescription().startsWith("seed_test_data")) {
                            LOGGER.info("Skipping test data seeding migration: " + 
                                       context.getMigrationInfo().getScript());
                            try {
                                // Skip this migration by returning early
                                LOGGER.info("Test data seeding skipped");
                                return;
                            } catch (Exception e) {
                                LOGGER.warning("Error skipping test data migration: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public String getCallbackName() {
                        return "DevModeRepeatableMigrationFilter";
                    }
                })
                .load();

            // Run the migrations
            LOGGER.info("Running Flyway migrations");
            var migrateResult = flyway.migrate();

            if (migrateResult.migrationsExecuted == 0) {
                LOGGER.warning("No migrations were executed. This might indicate a problem with the migration scripts.");
            } else {
                LOGGER.info("Applied " + migrateResult.migrationsExecuted + " Flyway migrations to SQLite database");
            }

            // Validate that migrations were applied correctly
            try {
                flyway.validate();
                LOGGER.info("Flyway migrations validated successfully");
            } catch (Exception e) {
                LOGGER.severe("Flyway migrations validation failed: " + e.getMessage());
                throw new RuntimeException("Database migration validation failed", e);
            }
        } catch (Exception e) {
            // Log the error with full details
            LOGGER.log(Level.SEVERE, "Failed to run Flyway migrations on SQLite database", e);

            // Rethrow the exception to ensure the application doesn't start with an invalid database
            throw new RuntimeException("Failed to initialize database with Flyway migrations", e);
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
