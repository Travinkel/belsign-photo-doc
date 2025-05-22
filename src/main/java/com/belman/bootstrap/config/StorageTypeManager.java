package com.belman.bootstrap.config;

import com.belman.bootstrap.persistence.DatabaseConfig;
import com.belman.bootstrap.persistence.SqliteDatabaseConfig;
import com.belman.common.logging.EmojiLogger;

import javax.sql.DataSource;

/**
 * Manager class for storage type configuration.
 * This class centralizes storage type configuration and manages the initialization
 * of the appropriate database configuration based on the storage type.
 */
public class StorageTypeManager {
    private static final EmojiLogger logger = EmojiLogger.getLogger(StorageTypeManager.class);
    private static boolean initialized = false;
    private static DataSource activeDataSource;

    /**
     * Resets the storage type manager.
     * This method is primarily used for testing.
     */
    public static synchronized void reset() {
        initialized = false;
        activeDataSource = null;
        logger.debug("StorageTypeManager reset");
    }

    /**
     * Initializes the storage type manager.
     * This method should be called once during application startup.
     */
    public static synchronized void initialize() {
        if (initialized) {
            logger.debug("StorageTypeManager already initialized, skipping initialization");
            return;
        }

        // Initialize storage type configuration
        StorageTypeConfig.initialize();

        // Initialize the appropriate database configuration based on the storage type
        initializeDatabaseConfig();

        initialized = true;
        logger.startup("🗄️ Storage type manager initialized");
    }

    /**
     * Initializes the appropriate database configuration based on the storage type.
     */
    private static void initializeDatabaseConfig() {
        StorageTypeConfig.StorageType storageType = StorageTypeConfig.getStorageType();
        logger.info("Initializing database configuration for storage type: " + storageType);

        switch (storageType) {
            case MEMORY:
                logger.startup("🧠 Memory mode active - using in-memory repositories");
                logger.debug("No database initialization needed for memory mode");
                activeDataSource = null;
                break;

            case SQLITE:
                logger.startup("🔄 SQLite mode active - using SQLite database");
                logger.debug("Starting SQLite database initialization...");

                // Initialize SQLite database
                try {
                    SqliteDatabaseConfig.initialize();
                    logger.debug("SQLite database initialization completed");

                    activeDataSource = SqliteDatabaseConfig.getDataSource();
                    if (activeDataSource != null) {
                        logger.info("Successfully obtained SQLite DataSource");

                        // Test the connection to verify it's working
                        try (java.sql.Connection conn = activeDataSource.getConnection()) {
                            logger.info("Successfully connected to SQLite database");

                            // Check if tables exist
                            try (java.sql.Statement stmt = conn.createStatement();
                                 java.sql.ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'")) {

                                StringBuilder tables = new StringBuilder("Tables in SQLite database: ");
                                boolean hasTables = false;

                                while (rs.next()) {
                                    hasTables = true;
                                    tables.append(rs.getString(1)).append(", ");
                                }

                                if (hasTables) {
                                    // Remove the trailing comma and space
                                    tables.setLength(tables.length() - 2);
                                    logger.info(tables.toString());
                                } else {
                                    logger.warn("No tables found in SQLite database!");
                                }
                            }
                        } catch (Exception e) {
                            logger.error("Failed to connect to SQLite database or query tables", e);
                        }
                    } else {
                        logger.warn("SQLite database initialization failed, DataSource is null");
                    }
                } catch (Exception e) {
                    logger.error("Exception during SQLite database initialization", e);
                }

                if (activeDataSource == null) {
                    logger.warn("SQLite database initialization failed, falling back to memory mode");
                }
                break;

            case SQLSERVER:
                logger.startup("🏢 SQL Server mode active - using SQL Server database");
                logger.debug("Starting SQL Server database initialization...");

                // Initialize SQL Server database
                try {
                    DatabaseConfig.initialize();
                    logger.debug("SQL Server database initialization completed");

                    activeDataSource = DatabaseConfig.getDataSource();
                    if (activeDataSource != null) {
                        logger.info("Successfully obtained SQL Server DataSource");
                    } else {
                        logger.warn("SQL Server database initialization failed, DataSource is null");
                    }
                } catch (Exception e) {
                    logger.error("Exception during SQL Server database initialization", e);
                }

                if (activeDataSource == null) {
                    logger.warn("SQL Server database initialization failed, falling back to SQLite mode");

                    // Try SQLite as fallback
                    logger.debug("Starting SQLite database initialization as fallback...");
                    try {
                        SqliteDatabaseConfig.initialize();
                        logger.debug("SQLite fallback initialization completed");

                        activeDataSource = SqliteDatabaseConfig.getDataSource();
                        if (activeDataSource != null) {
                            logger.info("Successfully obtained SQLite DataSource as fallback");
                        } else {
                            logger.warn("SQLite fallback initialization failed, DataSource is null");
                        }
                    } catch (Exception e) {
                        logger.error("Exception during SQLite fallback initialization", e);
                    }

                    if (activeDataSource == null) {
                        logger.warn("SQLite database initialization also failed, falling back to memory mode");
                    }
                }
                break;
        }
    }

    /**
     * Gets the active data source.
     *
     * @return the active data source, or null if using in-memory repositories
     */
    public static DataSource getActiveDataSource() {
        if (!initialized) {
            initialize();
        }
        return activeDataSource;
    }

    /**
     * Checks if the current storage type is memory.
     *
     * @return true if the current storage type is memory, false otherwise
     */
    public static boolean isMemoryMode() {
        return StorageTypeConfig.isMemoryMode();
    }

    /**
     * Checks if the current storage type is SQLite.
     *
     * @return true if the current storage type is SQLite, false otherwise
     */
    public static boolean isSqliteMode() {
        return StorageTypeConfig.isSqliteMode();
    }

    /**
     * Checks if the current storage type is SQL Server.
     *
     * @return true if the current storage type is SQL Server, false otherwise
     */
    public static boolean isSqlServerMode() {
        return StorageTypeConfig.isSqlServerMode();
    }

    /**
     * Shuts down the storage type manager.
     * This method should be called once during application shutdown.
     */
    public static synchronized void shutdown() {
        if (!initialized) {
            logger.debug("StorageTypeManager not initialized, skipping shutdown");
            return;
        }

        // Shutdown the appropriate database configuration based on the storage type
        StorageTypeConfig.StorageType storageType = StorageTypeConfig.getStorageType();

        switch (storageType) {
            case MEMORY:
                // No database shutdown needed for memory mode
                break;

            case SQLITE:
                // Shutdown SQLite database
                SqliteDatabaseConfig.shutdown();
                break;

            case SQLSERVER:
                // Shutdown SQL Server database
                DatabaseConfig.shutdown();
                break;
        }

        initialized = false;
        activeDataSource = null;
        logger.shutdown("🗄️ Storage type manager shut down");
    }
}
