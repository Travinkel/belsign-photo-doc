package com.belman.bootstrap.persistence;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manager class for database migrations.
 * This class provides methods to run Flyway migrations on the database.
 */
public class DatabaseMigrationManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseMigrationManager.class.getName());
    private static final String MIGRATION_DIR = "src/main/resources/db/migration";

    /**
     * Runs Flyway migrations on the database.
     *
     * @param dataSource the DataSource to use for migrations
     * @return true if migrations were successful, false otherwise
     */
    public static boolean runMigrations(DataSource dataSource) {
        if (dataSource == null) {
            LOGGER.severe("Cannot run migrations: DataSource is null");
            return false;
        }

        try {
            LOGGER.info("Running Flyway migrations on SQL Server database...");

            // Log the existence of migration scripts
            File migrationDir = new File(MIGRATION_DIR);
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
                    return false;
                }
            } else {
                LOGGER.warning("Migration directory does not exist: " + migrationDir.getAbsolutePath());
                return false;
            }

            // Configure Flyway
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:db/migration")
                    .load();

            // Run migrations
            org.flywaydb.core.api.output.MigrateResult result = flyway.migrate();
            LOGGER.info("Applied " + result.migrationsExecuted + " migrations");

            // Run seed data script
            if (runSeedDataScript(dataSource)) {
                LOGGER.info("Seed data script executed successfully");
            } else {
                LOGGER.warning("Failed to execute seed data script");
            }

            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to run Flyway migrations on SQL Server database", e);
            return false;
        }
    }

    /**
     * Runs the seed data script on the database.
     *
     * @param dataSource the DataSource to use for executing the script
     * @return true if the script was executed successfully, false otherwise
     */
    private static boolean runSeedDataScript(DataSource dataSource) {
        if (dataSource == null) {
            LOGGER.severe("Cannot run seed data script: DataSource is null");
            return false;
        }

        try {
            LOGGER.info("Running seed data script on SQL Server database...");

            // Check if the seed data script exists
            String seedDataScriptPath = "src/main/resources/db/populate_dummy_data.sql";
            File seedDataScriptFile = new File(seedDataScriptPath);
            if (!seedDataScriptFile.exists()) {
                LOGGER.warning("Seed data script not found at: " + seedDataScriptPath);
                return false;
            }

            LOGGER.info("Seed data script found at: " + seedDataScriptFile.getAbsolutePath());

            // Read the script content
            StringBuilder scriptContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(seedDataScriptFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    scriptContent.append(line).append("\n");
                }
            }

            // Execute the script
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {

                // Split the script into individual statements
                String[] statements = scriptContent.toString().split(";");

                // Execute each statement
                for (String sql : statements) {
                    sql = sql.trim();
                    if (!sql.isEmpty()) {
                        statement.execute(sql);
                    }
                }

                LOGGER.info("Seed data script executed successfully");
                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to run seed data script on SQL Server database", e);
            return false;
        }
    }
}
