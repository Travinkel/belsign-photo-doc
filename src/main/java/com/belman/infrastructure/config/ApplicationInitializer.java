package com.belman.infrastructure.config;

import com.belman.domain.repositories.UserRepository;
import com.belman.domain.services.AuthenticationService;
import com.belman.infrastructure.persistence.InMemoryUserRepository;
import com.belman.infrastructure.persistence.SqlUserRepository;
import com.belman.infrastructure.service.DefaultAuthenticationService;
import com.belman.infrastructure.service.SessionManager;

import javax.sql.DataSource;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Initializes the application's services and repositories.
 * This class is responsible for setting up the application's dependencies
 * and ensuring they are properly initialized before use.
 */
public class ApplicationInitializer {

    private static final Logger LOGGER = Logger.getLogger(ApplicationInitializer.class.getName());
    private static boolean initialized = false;

    /**
     * Initializes the application's services and repositories.
     * This method should be called once during application startup.
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        try {
            // Initialize database connection pool
            DatabaseConfig.initialize();
            LOGGER.info("Database connection pool initialized");

            // Create repositories
            UserRepository userRepository;

            // Try to use SQL-based repository if database is available
            DataSource dataSource = DatabaseConfig.getDataSource();
            if (dataSource != null) {
                try {
                    userRepository = new SqlUserRepository(dataSource);
                    LOGGER.info("Using SQL-based UserRepository");
                } catch (Exception e) {
                    // Fall back to in-memory repository if there's an error with the SQL repository
                    LOGGER.log(Level.WARNING, "Failed to initialize SQL-based UserRepository, falling back to in-memory repository", e);
                    userRepository = new InMemoryUserRepository();
                    LOGGER.info("Using in-memory UserRepository");
                }
            } else {
                // Fall back to in-memory repository if database is not available
                LOGGER.log(Level.WARNING, "Database is not available, falling back to in-memory repository");
                userRepository = new InMemoryUserRepository();
                LOGGER.info("Using in-memory UserRepository");
            }

            // Create services
            AuthenticationService authenticationService = new DefaultAuthenticationService(userRepository);

            // Initialize SessionManager
            SessionManager.getInstance(authenticationService);

            initialized = true;
            LOGGER.info("Application initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize application", e);
            throw new RuntimeException("Failed to initialize application", e);
        }
    }

    /**
     * Shuts down the application's services and resources.
     * This method should be called once during application shutdown.
     */
    public static synchronized void shutdown() {
        if (!initialized) {
            return;
        }

        try {
            // Shutdown database connection pool
            DatabaseConfig.shutdown();
            LOGGER.info("Database connection pool shut down");

            initialized = false;
            LOGGER.info("Application shut down successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to shut down application", e);
        }
    }
}
