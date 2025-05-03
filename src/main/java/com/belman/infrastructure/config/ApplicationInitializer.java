package com.belman.infrastructure.config;

import com.belman.backbone.core.logging.EmojiLogger;
import com.belman.domain.repositories.UserRepository;
import com.belman.domain.services.AuthenticationService;
import com.belman.infrastructure.persistence.InMemoryUserRepository;
import com.belman.infrastructure.persistence.SqlUserRepository;
import com.belman.infrastructure.service.DefaultAuthenticationService;
import com.belman.infrastructure.service.SessionManager;

import javax.sql.DataSource;

/**
 * Initializes the application's services and repositories.
 * This class is responsible for setting up the application's dependencies
 * and ensuring they are properly initialized before use.
 */
public class ApplicationInitializer {

    private static final EmojiLogger logger = EmojiLogger.getLogger(ApplicationInitializer.class);
    private static boolean initialized = false;

    /**
     * Initializes the application's services and repositories.
     * This method should be called once during application startup.
     */
    public static synchronized void initialize() {
        if (initialized) {
            logger.debug("Application already initialized, skipping initialization");
            return;
        }

        logger.startup("Starting application initialization");

        try {
            // Initialize database connection pool
            logger.database("Initializing database connection pool");
            DatabaseConfig.initialize();
            logger.success("Database connection pool initialized successfully");

            // Create repositories
            UserRepository userRepository;

            // Try to use SQL-based repository if database is available
            DataSource dataSource = DatabaseConfig.getDataSource();
            if (dataSource != null) {
                try {
                    logger.database("Creating SQL-based UserRepository");
                    userRepository = new SqlUserRepository(dataSource);
                    logger.success("Using SQL-based UserRepository");
                } catch (Exception e) {
                    // Fall back to in-memory repository if there's an error with the SQL repository
                    logger.warn("Failed to initialize SQL-based UserRepository, falling back to in-memory repository", e);
                    logger.database("Creating in-memory UserRepository as fallback");
                    userRepository = new InMemoryUserRepository();
                    logger.info("Using in-memory UserRepository as fallback");
                }
            } else {
                // Fall back to in-memory repository if database is not available
                logger.warn("Database is not available, falling back to in-memory repository");
                logger.database("Creating in-memory UserRepository as fallback");
                userRepository = new InMemoryUserRepository();
                logger.info("Using in-memory UserRepository as fallback");
            }

            // Create services
            logger.debug("Creating authentication service");
            AuthenticationService authenticationService = new DefaultAuthenticationService(userRepository);
            logger.success("Authentication service created successfully");

            // Initialize SessionManager
            logger.debug("Initializing SessionManager");
            SessionManager.getInstance(authenticationService);
            logger.success("SessionManager initialized successfully");

            initialized = true;
            logger.startup("Application initialized successfully ✨");
        } catch (Exception e) {
            logger.failure("Failed to initialize application");
            logger.error("Initialization error details", e);
            throw new RuntimeException("Failed to initialize application", e);
        }
    }

    /**
     * Shuts down the application's services and resources.
     * This method should be called once during application shutdown.
     */
    public static synchronized void shutdown() {
        if (!initialized) {
            logger.debug("Application not initialized, skipping shutdown");
            return;
        }

        logger.shutdown("Starting application shutdown");

        try {
            // Shutdown database connection pool
            logger.database("Shutting down database connection pool");
            DatabaseConfig.shutdown();
            logger.success("Database connection pool shut down successfully");

            initialized = false;
            logger.shutdown("Application shut down successfully 👋");
        } catch (Exception e) {
            logger.failure("Failed to shut down application properly");
            logger.error("Shutdown error details", e);
        }
    }
}
