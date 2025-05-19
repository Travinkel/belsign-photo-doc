package com.belman.bootstrap.config;

import com.belman.common.logging.EmojiLogger;

/**
 * Configuration class for development mode.
 * This class provides methods to enable or disable development mode in the application.
 * Development mode enables additional features and tools for development purposes.
 */
public class DevModeConfig {
    private static final EmojiLogger logger = EmojiLogger.getLogger(DevModeConfig.class);
    private static boolean devMode = false;
    private static boolean initialized = false;

    /**
     * Initializes the development mode configuration.
     * This method should be called once during application startup.
     *
     * @param enabled whether development mode should be enabled
     */
    public static synchronized void initialize(boolean enabled) {
        if (initialized) {
            logger.debug("DevModeConfig already initialized, skipping initialization");
            return;
        }

        devMode = enabled;
        initialized = true;

        if (devMode) {
            logger.startup("🛠️ Development mode enabled");
        } else {
            logger.startup("🚀 Production mode enabled");
        }
    }

    /**
     * Checks if development mode is enabled.
     *
     * @return true if development mode is enabled, false otherwise
     */
    public static boolean isDevMode() {
        return devMode;
    }

    /**
     * Sets whether development mode is enabled.
     * This method should only be called during application initialization.
     *
     * @param enabled whether development mode should be enabled
     */
    public static void setDevMode(boolean enabled) {
        if (initialized && devMode != enabled) {
            logger.warn("Changing dev mode after initialization is not recommended");
        }
        devMode = enabled;
        if (devMode) {
            logger.info("🛠️ Development mode enabled");
        } else {
            logger.info("🚀 Production mode enabled");
        }
    }
}