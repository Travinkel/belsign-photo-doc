package com.belman.infrastructure.config;

import com.belman.application.core.ServiceRegistry;
import com.belman.infrastructure.EmojiLogger;
import com.belman.domain.repositories.CustomerRepository;
import com.belman.domain.repositories.OrderRepository;
import com.belman.domain.repositories.UserRepository;
import com.belman.domain.rbac.AccessPolicyFactory;
import com.belman.domain.rbac.RoleBasedAccessControlFactory;
import com.belman.domain.services.AuthenticationService;
import com.belman.infrastructure.persistence.InMemoryCustomerRepository;
import com.belman.infrastructure.persistence.InMemoryOrderRepository;
import com.belman.infrastructure.persistence.InMemoryUserRepository;
import com.belman.infrastructure.persistence.SqlUserRepository;
import com.belman.infrastructure.service.DefaultAuthenticationService;
import com.belman.infrastructure.service.DefaultPhotoService;
import com.belman.infrastructure.service.SessionManager;
import com.belman.domain.services.PhotoService;

import javax.sql.DataSource;

/**
 * Initializes the application's services and repositories.
 * This class is responsible for setting up the application's dependencies
 * and ensuring they are properly initialized before use.
 */
public class ApplicationInitializer {

    private static final EmojiLogger logger = EmojiLogger.getLogger(ApplicationInitializer.class);
    private static boolean initialized = false;

    // Photo storage directory
    private static final String PHOTO_STORAGE_DIRECTORY = "photos";

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
            CustomerRepository customerRepository;
            logger.debug("Creating repositories");

            // Try to use SQL-based repository if database is available
            DataSource dataSource = DatabaseConfig.getDataSource();
            if (dataSource != null) {
                try {
                    logger.database("Creating SQL-based UserRepository");
                    userRepository = new SqlUserRepository(dataSource);
                    // Register the UserRepository with the ServiceRegistry
                    ServiceRegistry.registerService(userRepository);
                    logger.success("Using SQL-based UserRepository");

                    logger.database("Creating in-memory OrderRepository as no SQL implementation exists yet");
                    orderRepository = new InMemoryOrderRepository();
                    ServiceRegistry.registerService(orderRepository);
                    logger.success("Using in-memory OrderRepository");

                    // Initialize CustomerRepository
                    logger.database("Creating in-memory CustomerRepository as no SQL implementation exists yet");
                    customerRepository = new InMemoryCustomerRepository();
                    ServiceRegistry.registerService(customerRepository);
                    logger.success("Using in-memory CustomerRepository");

                    // Initialize PhotoService
                    logger.database("Creating DefaultPhotoService");
                    PhotoService photoService = new DefaultPhotoService(orderRepository, PHOTO_STORAGE_DIRECTORY);
                    ServiceRegistry.registerService(photoService);
                    logger.success("Using DefaultPhotoService");

                } catch (Exception e) {
                    // Fall back to in-memory repository if there's an error with the SQL repository
                    logger.warn("Failed to initialize SQL-based UserRepository, falling back to in-memory repository", e);
                    logger.database("Creating in-memory UserRepository as fallback");
                    userRepository = new InMemoryUserRepository();
                    // Register the UserRepository with the ServiceRegistry
                    ServiceRegistry.registerService(userRepository);
                    logger.info("Using in-memory UserRepository as fallback");

                    // Initialize OrderRepository as fallback
                    orderRepository = new InMemoryOrderRepository();
                    ServiceRegistry.registerService(orderRepository);
                    logger.info("Using in-memory OrderRepository as fallback");

                    // Initialize CustomerRepository as fallback
                    customerRepository = new InMemoryCustomerRepository();
                    ServiceRegistry.registerService(customerRepository);
                    logger.info("Using in-memory CustomerRepository as fallback");

                    // Initialize PhotoService as fallback
                    logger.database("Creating DefaultPhotoService as fallback");
                    PhotoService photoService = new DefaultPhotoService(orderRepository, PHOTO_STORAGE_DIRECTORY);
                    ServiceRegistry.registerService(photoService);
                    logger.info("Using DefaultPhotoService as fallback");

                }
            } else {
                // Fall back to in-memory repositories if database is not available
                logger.warn("Database is not available, falling back to in-memory repositories");

                // Initialize UserRepository as fallback
                logger.database("Creating in-memory UserRepository as fallback");
                userRepository = new InMemoryUserRepository();
                ServiceRegistry.registerService(userRepository);
                logger.info("Using in-memory UserRepository as fallback");

                // Initialize OrderRepository as fallback
                logger.database("Creating in-memory OrderRepository as fallback");
                orderRepository = new InMemoryOrderRepository();
                ServiceRegistry.registerService(orderRepository);
                logger.info("Using in-memory OrderRepository as fallback");

                // Initialize CustomerRepository as fallback
                logger.database("Creating in-memory CustomerRepository as fallback");
                customerRepository = new InMemoryCustomerRepository();
                ServiceRegistry.registerService(customerRepository);
                logger.info("Using in-memory CustomerRepository as fallback");

                // Initialize PhotoService as fallback
                logger.database("Creating DefaultPhotoService as fallback");
                PhotoService photoService = new DefaultPhotoService(orderRepository, PHOTO_STORAGE_DIRECTORY);
                ServiceRegistry.registerService(photoService);
                logger.info("Using DefaultPhotoService as fallback");
            }

            // Create services
            logger.debug("Creating authentication service");
            AuthenticationService authenticationService = new DefaultAuthenticationService(userRepository);
            // Register the AuthenticationService with the ServiceRegistry
            ServiceRegistry.registerService(authenticationService);
            logger.success("Authentication service created successfully");

            // Initialize SessionManager
            logger.debug("Initializing SessionManager");
            SessionManager sessionManager = SessionManager.getInstance(authenticationService);
            // Register the SessionManager with the ServiceRegistry
            ServiceRegistry.registerService(sessionManager);
            logger.success("SessionManager initialized successfully");

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
