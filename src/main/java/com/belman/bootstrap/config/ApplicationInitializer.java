package com.belman.bootstrap.config;

import com.belman.bootstrap.di.ServiceRegistry;
import com.belman.bootstrap.persistence.DatabaseConfig;
import com.belman.bootstrap.persistence.SqliteDatabaseConfig;
import com.belman.common.logging.EmojiLogger;
import com.belman.common.session.SessionContext;
import com.belman.common.session.SimpleSessionContext;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.services.PhotoService;
import com.belman.domain.user.UserRepository;
import com.belman.service.usecase.photo.DefaultPhotoService;
import com.belman.service.usecase.security.DefaultExtendedAuthenticationService;

import javax.sql.DataSource;

/**
 * Initializes the application's services and repositories.
 * This class is responsible for setting up the application's dependencies
 * and ensuring they are properly initialized before use.
 */
public class ApplicationInitializer {

    private static final EmojiLogger logger = EmojiLogger.getLogger(ApplicationInitializer.class);
    // Photo storage directory
    private static final String PHOTO_STORAGE_DIRECTORY = "photos";
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
            OrderRepository orderRepository;
            logger.debug("Creating repositories");

            // Try to use SQL-based repositories if database is available
            DataSource dataSource = DatabaseConfig.getDataSource();

            // If main database is not available, try SQLite database
            if (dataSource == null) {
                logger.database("Main database not available, trying SQLite database");
                SqliteDatabaseConfig.initialize();
                dataSource = SqliteDatabaseConfig.getDataSource();
                if (dataSource != null) {
                    logger.success("SQLite database connection pool initialized successfully");
                } else {
                    logger.warn("SQLite database not available, falling back to in-memory repositories");
                }
            }

            // Initialize repositories using RepositoryInitializer
            Object[] repositories = RepositoryInitializer.initializeRepositories(dataSource);
            userRepository = (UserRepository) repositories[0];
            orderRepository = (OrderRepository) repositories[1];

            // Initialize PhotoService
            logger.database("Creating DefaultPhotoService");
            PhotoService photoService = new DefaultPhotoService(orderRepository, PHOTO_STORAGE_DIRECTORY);
            ServiceRegistry.registerService(photoService);
            logger.success("Using DefaultPhotoService");

            // Create services
            logger.debug("Creating extended authentication service");
            AuthenticationService authenticationService = new DefaultExtendedAuthenticationService(userRepository);
            // Register the AuthenticationService with the ServiceRegistry
            ServiceRegistry.registerService(authenticationService);
            logger.success("Extended authentication service created successfully");

            // Initialize a simple SessionContext
            logger.debug("Initializing simple SessionContext");
            SessionContext sessionContext = new SimpleSessionContext(authenticationService);
            // Register the SessionContext with the ServiceRegistry
            ServiceRegistry.registerService(sessionContext);
            logger.success("Simple SessionContext initialized successfully");

            // Initialize AccessPolicyFactory
            logger.debug("Initializing AccessPolicyFactory");
            AccessPolicyFactory accessPolicyFactory = new AccessPolicyFactory();
            // Register the AccessPolicyFactory with the ServiceRegistry
            ServiceRegistry.registerService(accessPolicyFactory);
            logger.success("AccessPolicyFactory initialized successfully");

            // Initialize RoleBasedAccessControlFactory
            logger.debug("Initializing RoleBasedAccessControlFactory");
            RoleBasedAccessControlFactory rbacFactory = new RoleBasedAccessControlFactory(
                    authenticationService, accessPolicyFactory);
            // Register the RoleBasedAccessControlFactory with the ServiceRegistry
            ServiceRegistry.registerService(rbacFactory);
            logger.success("RoleBasedAccessControlFactory initialized successfully");

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
            // Shutdown database connection pools
            logger.database("Shutting down database connection pools");
            DatabaseConfig.shutdown();
            SqliteDatabaseConfig.shutdown();
            logger.success("Database connection pools shut down successfully");

            initialized = false;
            logger.shutdown("Application shut down successfully 👋");
        } catch (Exception e) {
            logger.failure("Failed to shut down application properly");
            logger.error("Shutdown error details", e);
        }
    }
}