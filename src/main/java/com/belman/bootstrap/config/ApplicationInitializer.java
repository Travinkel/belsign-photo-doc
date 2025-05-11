package com.belman.bootstrap.config;

import com.belman.bootstrap.di.ServiceRegistry;
import com.belman.bootstrap.persistence.DatabaseConfig;
import com.belman.common.logging.EmojiLogger;
import com.belman.domain.customer.CustomerDataAccess;
import com.belman.domain.customer.CustomerRepository;
import com.belman.domain.order.OrderDataAccess;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.order.photo.PhotoDataAccess;
import com.belman.domain.order.photo.PhotoRepository;
import com.belman.domain.report.ReportDataAccess;
import com.belman.domain.report.ReportRepository;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.services.PhotoService;
import com.belman.domain.user.UserDataAccess;
import com.belman.domain.user.UserRepository;
import com.belman.domain.user.rbac.AccessPolicyFactory;
import com.belman.domain.user.rbac.RoleBasedAccessControlFactory;
import com.belman.repository.persistence.adapter.*;
import com.belman.repository.persistence.memory.*;
import com.belman.service.session.SessionManager;
import com.belman.service.usecase.photo.DefaultPhotoService;
import com.belman.service.usecase.security.DefaultAuthenticationService;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;

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
            CustomerRepository customerRepository;
            ReportRepository reportRepository;
            PhotoRepository photoRepository;
            logger.debug("Creating repositories");

            // Try to use SQL-based repositories if database is available
            DataSource dataSource = DatabaseConfig.getDataSource();
            if (dataSource != null) {
                try {
                    // Initialize UserRepository - try SQL implementation first
                    userRepository = createRepository(UserRepository.class, "SqlUserRepository", dataSource,
                            InMemoryUserRepository.class);
                    ServiceRegistry.registerService(userRepository);

                    // Create and register UserDataAccessAdapter
                    if (userRepository instanceof InMemoryUserRepository) {
                        logger.database("Creating UserDataAccessAdapter");
                        UserDataAccess userDataAccess = new UserDataAccessAdapter(
                                (InMemoryUserRepository) userRepository);
                        ServiceRegistry.registerService(userDataAccess);
                        logger.success("UserDataAccessAdapter created successfully");
                    }

                    // Initialize OrderRepository - try SQL implementation first
                    orderRepository = createRepository(OrderRepository.class, "SqlOrderRepository", dataSource,
                            InMemoryOrderRepository.class);
                    ServiceRegistry.registerService(orderRepository);

                    // Create and register OrderDataAccessAdapter
                    if (orderRepository instanceof InMemoryOrderRepository) {
                        logger.database("Creating OrderDataAccessAdapter");
                        OrderDataAccess orderDataAccess = new OrderDataAccessAdapter(
                                (InMemoryOrderRepository) orderRepository);
                        ServiceRegistry.registerService(orderDataAccess);
                        logger.success("OrderDataAccessAdapter created successfully");
                    }

                    // Initialize CustomerRepository - try SQL implementation first
                    customerRepository = createRepository(CustomerRepository.class, "SqlCustomerRepository", dataSource,
                            InMemoryCustomerRepository.class);
                    ServiceRegistry.registerService(customerRepository);

                    // Create and register CustomerDataAccessAdapter
                    if (customerRepository instanceof InMemoryCustomerRepository) {
                        logger.database("Creating CustomerDataAccessAdapter");
                        CustomerDataAccess customerDataAccess = new CustomerDataAccessAdapter(
                                (InMemoryCustomerRepository) customerRepository);
                        ServiceRegistry.registerService(customerDataAccess);
                        logger.success("CustomerDataAccessAdapter created successfully");
                    }

                    // Initialize ReportRepository - try SQL implementation first
                    reportRepository = createRepository(ReportRepository.class, "SqlReportRepository", dataSource,
                            InMemoryReportRepository.class);
                    ServiceRegistry.registerService(reportRepository);

                    // Create and register ReportDataAccessAdapter
                    if (reportRepository instanceof InMemoryReportRepository) {
                        logger.database("Creating ReportDataAccessAdapter");
                        ReportDataAccess reportDataAccess = new ReportDataAccessAdapter(
                                (InMemoryReportRepository) reportRepository);
                        ServiceRegistry.registerService(reportDataAccess);
                        logger.success("ReportDataAccessAdapter created successfully");
                    }

                    // Initialize PhotoRepository - use InMemoryPhotoRepository for now
                    logger.database("Creating InMemoryPhotoRepository");
                    photoRepository = new InMemoryPhotoRepository();
                    ServiceRegistry.registerService(photoRepository);
                    logger.success("Using InMemoryPhotoRepository");

                    // Create and register PhotoDataAccessAdapter
                    logger.database("Creating PhotoDataAccessAdapter");
                    PhotoDataAccess photoDataAccess = new PhotoDataAccessAdapter(photoRepository);
                    ServiceRegistry.registerService(photoDataAccess);
                    logger.success("PhotoDataAccessAdapter created successfully");

                    // Initialize PhotoService
                    logger.database("Creating DefaultPhotoService");
                    PhotoService photoService = new DefaultPhotoService(orderRepository, PHOTO_STORAGE_DIRECTORY);
                    ServiceRegistry.registerService(photoService);
                    logger.success("Using DefaultPhotoService");
                } catch (Exception e) {
                    // Fall back to in-memory repositories if there's an error
                    logger.warn("Failed to initialize repositories, falling back to in-memory repositories", e);

                    // Initialize UserRepository as fallback
                    logger.database("Creating in-memory UserRepository as fallback");
                    userRepository = new InMemoryUserRepository();
                    ServiceRegistry.registerService(userRepository);
                    logger.info("Using in-memory UserRepository as fallback");

                    // Create and register UserDataAccessAdapter
                    logger.database("Creating UserDataAccessAdapter");
                    UserDataAccess userDataAccess = new UserDataAccessAdapter((InMemoryUserRepository) userRepository);
                    ServiceRegistry.registerService(userDataAccess);
                    logger.success("UserDataAccessAdapter created successfully");

                    // Initialize OrderRepository as fallback
                    logger.database("Creating in-memory OrderRepository as fallback");
                    orderRepository = new InMemoryOrderRepository();
                    ServiceRegistry.registerService(orderRepository);
                    logger.info("Using in-memory OrderRepository as fallback");

                    // Create and register OrderDataAccessAdapter
                    logger.database("Creating OrderDataAccessAdapter");
                    OrderDataAccess orderDataAccess = new OrderDataAccessAdapter(
                            (InMemoryOrderRepository) orderRepository);
                    ServiceRegistry.registerService(orderDataAccess);
                    logger.success("OrderDataAccessAdapter created successfully");

                    // Initialize CustomerRepository as fallback
                    logger.database("Creating in-memory CustomerRepository as fallback");
                    customerRepository = new InMemoryCustomerRepository();
                    ServiceRegistry.registerService(customerRepository);
                    logger.info("Using in-memory CustomerRepository as fallback");

                    // Create and register CustomerDataAccessAdapter
                    logger.database("Creating CustomerDataAccessAdapter");
                    CustomerDataAccess customerDataAccess = new CustomerDataAccessAdapter(
                            (InMemoryCustomerRepository) customerRepository);
                    ServiceRegistry.registerService(customerDataAccess);
                    logger.success("CustomerDataAccessAdapter created successfully");

                    // Initialize ReportRepository as fallback
                    logger.database("Creating in-memory ReportRepository as fallback");
                    reportRepository = new InMemoryReportRepository();
                    ServiceRegistry.registerService(reportRepository);
                    logger.info("Using in-memory ReportRepository as fallback");

                    // Create and register ReportDataAccessAdapter
                    logger.database("Creating ReportDataAccessAdapter");
                    ReportDataAccess reportDataAccess = new ReportDataAccessAdapter(
                            (InMemoryReportRepository) reportRepository);
                    ServiceRegistry.registerService(reportDataAccess);
                    logger.success("ReportDataAccessAdapter created successfully");

                    // Initialize PhotoRepository as fallback
                    logger.database("Creating in-memory PhotoRepository as fallback");
                    photoRepository = new InMemoryPhotoRepository();
                    ServiceRegistry.registerService(photoRepository);
                    logger.info("Using in-memory PhotoRepository as fallback");

                    // Create and register PhotoDataAccessAdapter
                    logger.database("Creating PhotoDataAccessAdapter");
                    PhotoDataAccess photoDataAccess = new PhotoDataAccessAdapter(photoRepository);
                    ServiceRegistry.registerService(photoDataAccess);
                    logger.success("PhotoDataAccessAdapter created successfully");

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

                // Create and register UserDataAccessAdapter
                logger.database("Creating UserDataAccessAdapter");
                UserDataAccess userDataAccess = new UserDataAccessAdapter((InMemoryUserRepository) userRepository);
                ServiceRegistry.registerService(userDataAccess);
                logger.success("UserDataAccessAdapter created successfully");

                // Initialize OrderRepository as fallback
                logger.database("Creating in-memory OrderRepository as fallback");
                orderRepository = new InMemoryOrderRepository();
                ServiceRegistry.registerService(orderRepository);
                logger.info("Using in-memory OrderRepository as fallback");

                // Create and register OrderDataAccessAdapter
                logger.database("Creating OrderDataAccessAdapter");
                OrderDataAccess orderDataAccess = new OrderDataAccessAdapter((InMemoryOrderRepository) orderRepository);
                ServiceRegistry.registerService(orderDataAccess);
                logger.success("OrderDataAccessAdapter created successfully");

                // Initialize CustomerRepository as fallback
                logger.database("Creating in-memory CustomerRepository as fallback");
                customerRepository = new InMemoryCustomerRepository();
                ServiceRegistry.registerService(customerRepository);
                logger.info("Using in-memory CustomerRepository as fallback");

                // Create and register CustomerDataAccessAdapter
                logger.database("Creating CustomerDataAccessAdapter");
                CustomerDataAccess customerDataAccess = new CustomerDataAccessAdapter(
                        (InMemoryCustomerRepository) customerRepository);
                ServiceRegistry.registerService(customerDataAccess);
                logger.success("CustomerDataAccessAdapter created successfully");

                // Initialize ReportRepository as fallback
                logger.database("Creating in-memory ReportRepository as fallback");
                reportRepository = new InMemoryReportRepository();
                ServiceRegistry.registerService(reportRepository);
                logger.info("Using in-memory ReportRepository as fallback");

                // Create and register ReportDataAccessAdapter
                logger.database("Creating ReportDataAccessAdapter");
                ReportDataAccess reportDataAccess = new ReportDataAccessAdapter(
                        (InMemoryReportRepository) reportRepository);
                ServiceRegistry.registerService(reportDataAccess);
                logger.success("ReportDataAccessAdapter created successfully");

                // Initialize PhotoRepository as fallback
                logger.database("Creating in-memory PhotoRepository as fallback");
                photoRepository = new InMemoryPhotoRepository();
                ServiceRegistry.registerService(photoRepository);
                logger.info("Using in-memory PhotoRepository as fallback");

                // Create and register PhotoDataAccessAdapter
                logger.database("Creating PhotoDataAccessAdapter");
                PhotoDataAccess photoDataAccess = new PhotoDataAccessAdapter(photoRepository);
                ServiceRegistry.registerService(photoDataAccess);
                logger.success("PhotoDataAccessAdapter created successfully");

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
     * Creates a repository instance, trying to use a SQL implementation first if available,
     * and falling back to an in-memory implementation if the SQL implementation is not available.
     *
     * @param <T>                 the repository interface type
     * @param repositoryInterface the repository interface class
     * @param sqlImplName         the name of the SQL implementation class
     * @param dataSource          the DataSource to pass to the SQL implementation constructor
     * @param inMemoryImplClass   the in-memory implementation class to use as fallback
     * @return a repository instance
     * @throws Exception if an error occurs while creating the repository
     */
    @SuppressWarnings("unchecked")
    private static <T> T createRepository(Class<T> repositoryInterface, String sqlImplName, DataSource dataSource,
                                          Class<? extends T> inMemoryImplClass) throws Exception {
        // Try to create SQL implementation first
        try {
            // Construct the full class name for the SQL implementation
            String sqlImplClassName = "com.belman.infrastructure.persistence." + sqlImplName;

            // Try to load the SQL implementation class
            Class<?> sqlImplClass = Class.forName(sqlImplClassName);

            // Check if the class implements the repository interface
            if (repositoryInterface.isAssignableFrom(sqlImplClass)) {
                logger.database("Creating SQL-based " + repositoryInterface.getSimpleName());

                // Find constructor that takes a DataSource
                Constructor<?> constructor = sqlImplClass.getConstructor(DataSource.class);

                // Create instance
                T repository = (T) constructor.newInstance(dataSource);

                logger.success("Using SQL-based " + repositoryInterface.getSimpleName());
                return repository;
            } else {
                logger.warn("Class " + sqlImplClassName + " does not implement " + repositoryInterface.getSimpleName());
            }
        } catch (ClassNotFoundException e) {
            // SQL implementation not found, this is expected for repositories that don't have SQL implementations yet
            logger.info("SQL implementation " + sqlImplName + " not found, using in-memory implementation");
        } catch (Exception e) {
            // Other error occurred while trying to create SQL implementation
            logger.warn("Failed to create SQL-based " + repositoryInterface.getSimpleName() +
                        ", falling back to in-memory implementation", e);
        }

        // Fall back to in-memory implementation
        logger.database("Creating in-memory " + repositoryInterface.getSimpleName());
        T repository = inMemoryImplClass.getDeclaredConstructor().newInstance();
        logger.info("Using in-memory " + repositoryInterface.getSimpleName());
        return repository;
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
