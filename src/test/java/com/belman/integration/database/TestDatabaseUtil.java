package com.belman.integration.database;

import com.belman.bootstrap.persistence.SqliteDatabaseConfig;
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
     * runs migrations, and returns a DataSource for the test database.
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

                // Run Flyway migrations
                runFlywayMigrations(jdbcUrl);

                // Use the SqliteDatabaseConfig to get a DataSource
                // This ensures we use the same connection pool configuration
                SqliteDatabaseConfig.initialize();
                testDataSource = SqliteDatabaseConfig.getDataSource();

                LOGGER.info("Test database initialized successfully");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize test database", e);
                throw new RuntimeException("Failed to initialize test database", e);
            }
        }
        return testDataSource;
    }

    /**
     * Runs Flyway migrations on the test database.
     * 
     * @param jdbcUrl the JDBC URL for the test database
     */
    private static void runFlywayMigrations(String jdbcUrl) {
        try {
            LOGGER.info("Running Flyway migrations on test database...");

            Flyway flyway = Flyway.configure()
                .dataSource(jdbcUrl, null, null)
                .locations("classpath:sqlitedb/migration", "classpath:sqlitedb/testdata")
                .baselineOnMigrate(true)
                .outOfOrder(true)
                .sqlMigrationSeparator(";")
                .mixed(true)
                .validateMigrationNaming(false)
                .cleanDisabled(false) // Enable clean for tests
                .group(true)
                .load();

            // Clean the database for tests
            flyway.clean();

            // Run the migrations
            var migrateResult = flyway.migrate();

            LOGGER.info("Applied " + migrateResult.migrationsExecuted + " Flyway migrations to test database");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to run Flyway migrations on test database", e);
            throw new RuntimeException("Failed to run Flyway migrations on test database", e);
        }
    }

    /**
     * Resets the test database by cleaning and re-running migrations.
     * This method should be called before each test to ensure a clean state.
     */
    public static void resetTestDatabase() {
        try {
            LOGGER.info("Resetting test database...");

            // Set up the JDBC URL for SQLite
            File dbFile = new File(TEST_DB_PATH);
            String jdbcUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

            // Run Flyway migrations with clean
            Flyway flyway = Flyway.configure()
                .dataSource(jdbcUrl, null, null)
                .locations("classpath:sqlitedb/migration", "classpath:sqlitedb/testdata")
                .baselineOnMigrate(true)
                .outOfOrder(true)
                .sqlMigrationSeparator(";")
                .mixed(true)
                .validateMigrationNaming(false)
                .cleanDisabled(false) // Enable clean for tests
                .group(true)
                .load();

            // Clean the database
            flyway.clean();

            // Run the migrations
            flyway.migrate();

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
        SqliteDatabaseConfig.shutdown();
        LOGGER.info("Test database connection closed");
    }
}