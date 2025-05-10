package com.belman.bootstrap.config;

import com.belman.bootstrap.di.ServiceRegistry;
import com.belman.repository.logging.EmojiLogger;

/**
 * Bootstraps the application by initializing all required services and components.
 * This class is part of the infrastructure layer and is responsible for application initialization.
 */
public class ApplicationBootstrapper {

    private static final EmojiLogger logger = EmojiLogger.getLogger(ApplicationBootstrapper.class);

    /**
     * Initializes the application.
     */
    public static void initialize() {
        logger.startup("Bootstrapping application...");

        // Initialize the ServiceRegistry with a logger first
        logger.debug("Initializing LoggerFactory");
        com.belman.repository.logging.EmojiLoggerFactory loggerFactory =
                com.belman.repository.logging.EmojiLoggerFactory.getInstance();
        ServiceRegistry.setLogger(loggerFactory);
        ServiceRegistry.registerService(loggerFactory);

        // Initialize application services and repositories
        logger.startup("Initializing application services and repositories");
        ApplicationInitializer.initialize();

        logger.success("Application bootstrapped successfully");
    }

    /**
     * Shuts down the application.
     */
    public static void shutdown() {
        logger.shutdown("Shutting down application...");

        // Shutdown application services and resources
        logger.shutdown("Shutting down application services and resources");
        ApplicationInitializer.shutdown();

        logger.success("Application shut down successfully");
    }
}