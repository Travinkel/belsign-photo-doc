package com.belman.bootstrap.config;

import com.belman.common.logging.EmojiLogger;

/**
 * Configuration class for storage type.
 * This class provides methods to determine which storage type to use based on the BELSIGN_STORAGE_TYPE environment variable.
 * Supported storage types are:
 * - memory: In-memory repositories for testing
 * - sqlite: SQLite database for development
 * - sqlserver: SQL Server database for production
 */
public class StorageTypeConfig {
    private static final EmojiLogger logger = EmojiLogger.getLogger(StorageTypeConfig.class);
    public static final String ENV_STORAGE_TYPE = "BELSIGN_STORAGE_TYPE";
    // Changed default from "memory" to "sqlite" to ensure database migrations run and tables are created
    // This fixes the "no such table: USERS" error when trying to authenticate
    private static final String DEFAULT_STORAGE_TYPE = "memory"; // Changed default to "memory" to ensure in-memory repositories are used by default

    public enum StorageType {
        MEMORY,
        SQLITE,
        SQLSERVER
    }

    private static StorageType storageType;
    private static boolean initialized = false;
    private static boolean forceMemoryMode = false; // Flag to force memory mode regardless of environment variable

    /**
     * Resets the storage type configuration.
     * This method is primarily used for testing.
     */
    public static synchronized void reset() {
        initialized = false;
        storageType = null;
        logger.debug("StorageTypeConfig reset");
    }

    /**
     * Initializes the storage type configuration.
     * This method should be called once during application startup.
     */
    public static synchronized void initialize() {
        if (initialized) {
            logger.debug("StorageTypeConfig already initialized, skipping initialization");
            return;
        }

        logger.debug("Initializing storage type configuration...");

        // If force memory mode is enabled, use MEMORY storage type
        if (forceMemoryMode) {
            logger.info("Force memory mode is enabled, using MEMORY storage type");
            storageType = StorageType.MEMORY;
            initialized = true;
            logger.startup("🗄️ Storage type: " + storageType + " (FORCED)");
            return;
        }

        // Check environment variable first
        String storageTypeStr = System.getenv(ENV_STORAGE_TYPE);
        if (storageTypeStr != null && !storageTypeStr.isEmpty()) {
            logger.info("Found storage type in environment variable: " + storageTypeStr);
        } else {
            // Then check system property
            storageTypeStr = System.getProperty(ENV_STORAGE_TYPE);
            if (storageTypeStr != null && !storageTypeStr.isEmpty()) {
                logger.info("Found storage type in system property: " + storageTypeStr);
            } else {
                // Use default if neither is set
                storageTypeStr = DEFAULT_STORAGE_TYPE;
                logger.info("No storage type specified in environment or system properties, using default: " + DEFAULT_STORAGE_TYPE);
            }
        }

        storageType = parseStorageType(storageTypeStr);
        logger.info("Parsed storage type: " + storageTypeStr + " -> " + storageType);

        initialized = true;
        logger.startup("🗄️ Storage type: " + storageType);
    }

    /**
     * Parses the storage type string into a StorageType enum.
     *
     * @param storageTypeStr the storage type string
     * @return the StorageType enum
     */
    private static StorageType parseStorageType(String storageTypeStr) {
        if (storageTypeStr == null || storageTypeStr.isEmpty()) {
            logger.warn("Storage type not specified, using default: " + DEFAULT_STORAGE_TYPE);
            // Return SQLITE to match DEFAULT_STORAGE_TYPE
            return StorageType.SQLITE;
        }

        logger.debug("Parsing storage type string: " + storageTypeStr);

        switch (storageTypeStr.toLowerCase()) {
            case "memory":
                logger.debug("Parsed as MEMORY storage type");
                return StorageType.MEMORY;
            case "sqlite":
                logger.debug("Parsed as SQLITE storage type");
                return StorageType.SQLITE;
            case "sqlserver":
                logger.debug("Parsed as SQLSERVER storage type");
                return StorageType.SQLSERVER;
            default:
                logger.warn("Unknown storage type: " + storageTypeStr + ", using default: " + DEFAULT_STORAGE_TYPE);
                // Return SQLITE to match DEFAULT_STORAGE_TYPE
                return StorageType.SQLITE;
        }
    }

    /**
     * Gets the current storage type.
     *
     * @return the current storage type
     */
    public static StorageType getStorageType() {
        if (!initialized) {
            initialize();
        }
        return storageType;
    }

    /**
     * Checks if the current storage type is memory.
     *
     * @return true if the current storage type is memory, false otherwise
     */
    public static boolean isMemoryMode() {
        return getStorageType() == StorageType.MEMORY;
    }

    /**
     * Checks if the current storage type is SQLite.
     *
     * @return true if the current storage type is SQLite, false otherwise
     */
    public static boolean isSqliteMode() {
        return getStorageType() == StorageType.SQLITE;
    }

    /**
     * Checks if the current storage type is SQL Server.
     *
     * @return true if the current storage type is SQL Server, false otherwise
     */
    public static boolean isSqlServerMode() {
        return getStorageType() == StorageType.SQLSERVER;
    }

    /**
     * Forces the application to use memory mode regardless of the environment variable.
     * This method should be called before the StorageTypeConfig is initialized.
     */
    public static void forceMemoryMode() {
        forceMemoryMode = true;
        // Reset initialization state to force re-initialization
        reset();
        logger.info("Memory mode forced. Application will use in-memory repositories.");
    }
}
