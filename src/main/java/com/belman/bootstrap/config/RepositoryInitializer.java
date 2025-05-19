package com.belman.bootstrap.config;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.bootstrap.di.ServiceRegistry;
import com.belman.common.logging.EmojiLogger;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.report.ReportRepository;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserRepository;
import com.belman.dataaccess.persistence.memory.InMemoryOrderRepository;
import com.belman.dataaccess.persistence.memory.InMemoryUserRepository;
import com.belman.dataaccess.repository.memory.InMemoryPhotoRepository;
import com.belman.dataaccess.repository.memory.InMemoryReportRepository;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;

/**
 * Initializes repositories for the application.
 * This class is responsible for creating and registering repositories.
 */
public class RepositoryInitializer {

    private static final EmojiLogger logger = EmojiLogger.getLogger(RepositoryInitializer.class);

    /**
     * Initializes repositories with the given data source.
     * If the data source is null, falls back to in-memory repositories.
     *
     * @param dataSource the data source to use for SQL repositories, or null to use in-memory repositories
     * @return an array containing the initialized repositories in the order [userRepository, orderRepository, photoRepository, reportRepository]
     */
    public static Object[] initializeRepositories(DataSource dataSource) {
        UserRepository userRepository;
        OrderRepository orderRepository;
        PhotoRepository photoRepository;
        ReportRepository reportRepository;
        LoggerFactory loggerFactory = ServiceLocator.getService(LoggerFactory.class);

        if (dataSource != null) {
            try {
                // Initialize UserRepository - try SQL implementation first
                userRepository = createRepository(UserRepository.class, "SqlUserRepository", dataSource,
                        InMemoryUserRepository.class);
                ServiceRegistry.registerService(userRepository);

                // Initialize OrderRepository - try SQL implementation first
                orderRepository = createRepository(OrderRepository.class, "SqlOrderRepository", dataSource,
                        InMemoryOrderRepository.class);
                ServiceRegistry.registerService(orderRepository);

                // Initialize PhotoRepository - use in-memory implementation for now
                photoRepository = initializeInMemoryPhotoRepository(loggerFactory);
                ServiceRegistry.registerService(photoRepository);

                // Initialize ReportRepository - use in-memory implementation for now
                reportRepository = initializeInMemoryReportRepository(loggerFactory);
                ServiceRegistry.registerService(reportRepository);
            } catch (Exception e) {
                // Fall back to in-memory repositories if there's an error
                logger.warn("Failed to initialize repositories, falling back to in-memory repositories", e);
                userRepository = initializeInMemoryUserRepository();
                orderRepository = initializeInMemoryOrderRepository();
                photoRepository = initializeInMemoryPhotoRepository(loggerFactory);
                reportRepository = initializeInMemoryReportRepository(loggerFactory);
            }
        } else {
            // Fall back to in-memory repositories if database is not available
            logger.warn("Database is not available, falling back to in-memory repositories");
            userRepository = initializeInMemoryUserRepository();
            orderRepository = initializeInMemoryOrderRepository();
            photoRepository = initializeInMemoryPhotoRepository(loggerFactory);
            reportRepository = initializeInMemoryReportRepository(loggerFactory);
        }

        // Log a summary of which repository mode is active
        String repositoryMode = dataSource != null ? "SQL database" : "in-memory";
        logger.startup("📊 Repository mode: " + repositoryMode);
        logger.startup("📊 UserRepository: " + userRepository.getClass().getSimpleName());
        logger.startup("📊 OrderRepository: " + orderRepository.getClass().getSimpleName());
        logger.startup("📊 PhotoRepository: " + photoRepository.getClass().getSimpleName());
        logger.startup("📊 ReportRepository: " + reportRepository.getClass().getSimpleName());

        return new Object[]{userRepository, orderRepository, photoRepository, reportRepository};
    }

    /**
     * Initializes an in-memory user repository.
     *
     * @return the initialized in-memory user repository
     */
    private static UserRepository initializeInMemoryUserRepository() {
        logger.database("Creating in-memory UserRepository as fallback");
        UserRepository userRepository = new InMemoryUserRepository(true); // Create with default users
        ServiceRegistry.registerService(userRepository);
        logger.info("Using in-memory UserRepository as fallback");
        return userRepository;
    }

    /**
     * Initializes an in-memory order repository.
     *
     * @return the initialized in-memory order repository
     */
    private static OrderRepository initializeInMemoryOrderRepository() {
        logger.database("Creating in-memory OrderRepository as fallback");
        OrderRepository orderRepository = new InMemoryOrderRepository();
        ServiceRegistry.registerService(orderRepository);
        logger.info("Using in-memory OrderRepository as fallback");
        return orderRepository;
    }

    /**
     * Initializes an in-memory photo repository.
     *
     * @param loggerFactory the logger factory to use
     * @return the initialized in-memory photo repository
     */
    private static PhotoRepository initializeInMemoryPhotoRepository(LoggerFactory loggerFactory) {
        logger.database("Creating in-memory PhotoRepository as fallback");
        PhotoRepository photoRepository = new InMemoryPhotoRepository(loggerFactory);
        ServiceRegistry.registerService(photoRepository);
        logger.info("Using in-memory PhotoRepository as fallback");
        return photoRepository;
    }

    /**
     * Initializes an in-memory report repository.
     *
     * @param loggerFactory the logger factory to use
     * @return the initialized in-memory report repository
     */
    private static ReportRepository initializeInMemoryReportRepository(LoggerFactory loggerFactory) {
        logger.database("Creating in-memory ReportRepository as fallback");
        ReportRepository reportRepository = new InMemoryReportRepository(loggerFactory);
        ServiceRegistry.registerService(reportRepository);
        logger.info("Using in-memory ReportRepository as fallback");
        return reportRepository;
    }

    /**
     * Initializes repositories for development mode.
     * This method configures repositories for development, using a hybrid approach:
     * - SQL repositories for persistent data (users, orders)
     * - In-memory repositories for transient data or development features
     *
     * @param dataSource the data source to use for SQL repositories, or null to use in-memory repositories for everything
     * @return an array containing the initialized repositories in the order [userRepository, orderRepository, photoRepository, reportRepository]
     */
    public static Object[] initializeDevModeRepositories(DataSource dataSource) {
        UserRepository userRepository;
        OrderRepository orderRepository;
        PhotoRepository photoRepository;
        ReportRepository reportRepository;
        LoggerFactory loggerFactory = ServiceLocator.getService(LoggerFactory.class);

        logger.startup("🛠️ Initializing repositories for development mode");

        if (dataSource != null) {
            try {
                // Initialize UserRepository - use SQL implementation for persistence
                userRepository = createRepository(UserRepository.class, "SqlUserRepository", dataSource,
                        InMemoryUserRepository.class);
                ServiceRegistry.registerService(userRepository);
                logger.info("Dev mode: Using SQL-based UserRepository for persistence");

                // Initialize OrderRepository - use SQL implementation for persistence
                orderRepository = createRepository(OrderRepository.class, "SqlOrderRepository", dataSource,
                        InMemoryOrderRepository.class);
                ServiceRegistry.registerService(orderRepository);
                logger.info("Dev mode: Using SQL-based OrderRepository for persistence");

                // Initialize PhotoRepository - use in-memory implementation for development
                photoRepository = initializeInMemoryPhotoRepository(loggerFactory);
                ServiceRegistry.registerService(photoRepository);
                logger.info("Dev mode: Using in-memory PhotoRepository for development");

                // Initialize ReportRepository - use in-memory implementation for development
                reportRepository = initializeInMemoryReportRepository(loggerFactory);
                ServiceRegistry.registerService(reportRepository);
                logger.info("Dev mode: Using in-memory ReportRepository for development");
            } catch (Exception e) {
                // Fall back to in-memory repositories if there's an error
                logger.warn("Failed to initialize repositories for dev mode, falling back to in-memory repositories", e);
                userRepository = initializeInMemoryUserRepository();
                orderRepository = initializeInMemoryOrderRepository();
                photoRepository = initializeInMemoryPhotoRepository(loggerFactory);
                reportRepository = initializeInMemoryReportRepository(loggerFactory);
            }
        } else {
            // Fall back to in-memory repositories if database is not available
            logger.warn("Database is not available for dev mode, falling back to in-memory repositories");
            userRepository = initializeInMemoryUserRepository();
            orderRepository = initializeInMemoryOrderRepository();
            photoRepository = initializeInMemoryPhotoRepository(loggerFactory);
            reportRepository = initializeInMemoryReportRepository(loggerFactory);
        }

        // Log a summary of which repository mode is active
        logger.startup("📊 Dev mode repository configuration:");
        logger.startup("📊 UserRepository: " + userRepository.getClass().getSimpleName());
        logger.startup("📊 OrderRepository: " + orderRepository.getClass().getSimpleName());
        logger.startup("📊 PhotoRepository: " + photoRepository.getClass().getSimpleName());
        logger.startup("📊 ReportRepository: " + reportRepository.getClass().getSimpleName());

        // Seed test data for development
        seedTestData(userRepository, orderRepository, photoRepository, reportRepository);

        return new Object[]{userRepository, orderRepository, photoRepository, reportRepository};
    }

    /**
     * Seeds test data for development mode.
     * This method adds sample data to repositories for testing and development purposes.
     *
     * @param userRepository the user repository to seed
     * @param orderRepository the order repository to seed
     * @param photoRepository the photo repository to seed
     * @param reportRepository the report repository to seed
     */
    private static void seedTestData(UserRepository userRepository, OrderRepository orderRepository, 
                                    PhotoRepository photoRepository, ReportRepository reportRepository) {
        try {
            logger.info("🌱 Seeding test data for development mode");

            // Check if we need to seed data (only if repositories are empty)
            if (userRepository.findAll().isEmpty()) {
                logger.info("Seeding test users");
                // Users are typically seeded by the InMemoryUserRepository constructor
                // or by SQL migrations, so we don't need to add them here
            } else {
                logger.info("User repository already contains data, skipping user seeding");
            }

            // Add test orders if needed
            if (orderRepository.findAll().isEmpty()) {
                logger.info("Seeding test orders");

                // Get a default user for creating orders
                var users = userRepository.findAll();
                if (!users.isEmpty()) {
                    var defaultUser = users.get(0);

                    // Create and save test orders
                    try {
                        // Create order 1
                        var order1 = createTestOrder(
                            "ORD-123-230615-WLD-0001", 
                            "Test Customer 1", 
                            "Welding work on expansion joint", 
                            defaultUser
                        );
                        orderRepository.save(order1);
                        logger.info("Created test order: " + order1.getOrderNumber().value());

                        // Create order 2
                        var order2 = createTestOrder(
                            "ORD-456-230620-EXP-0002", 
                            "Test Customer 2", 
                            "Expansion joint assembly", 
                            defaultUser
                        );
                        orderRepository.save(order2);
                        logger.info("Created test order: " + order2.getOrderNumber().value());

                        // Create order 3
                        var order3 = createTestOrder(
                            "ORD-789-230625-PIP-0003", 
                            "Test Customer 3", 
                            "Pipe end-cap inspection", 
                            defaultUser
                        );
                        orderRepository.save(order3);
                        logger.info("Created test order: " + order3.getOrderNumber().value());
                    } catch (Exception e) {
                        logger.error("Error creating test orders", e);
                    }
                } else {
                    logger.warn("No users found, cannot create test orders");
                }
            } else {
                logger.info("Order repository already contains data, skipping order seeding");
            }

            logger.success("✅ Test data seeding completed");
        } catch (Exception e) {
            logger.error("❌ Error seeding test data", e);
        }
    }

    /**
     * Creates a test order with the given parameters.
     *
     * @param orderNumber the order number
     * @param customerName the customer name
     * @param description the product description
     * @param createdBy the user who created the order
     * @return the created order
     */
    private static com.belman.domain.order.OrderBusiness createTestOrder(
            String orderNumber, String customerName, String description, com.belman.domain.user.UserBusiness createdBy) {

        // Create order ID
        var orderId = new com.belman.domain.order.OrderId(java.util.UUID.randomUUID().toString());

        // Create customer ID
        var customerId = new com.belman.domain.customer.CustomerId(java.util.UUID.randomUUID().toString());

        // Create timestamp
        var timestamp = new com.belman.domain.common.valueobjects.Timestamp(java.time.Instant.now());

        // Convert UserBusiness to UserReference
        var userReference = new com.belman.domain.user.UserReference(createdBy.getId(), createdBy.getUsername());

        // Create order
        var order = new com.belman.domain.order.OrderBusiness(orderId, userReference, timestamp);

        // Set order properties
        order.setOrderNumber(new com.belman.domain.order.OrderNumber(orderNumber));
        order.setCustomerId(customerId);

        // Create ProductDescription with all required parameters
        var productDesc = new com.belman.domain.order.ProductDescription(
            description,                  // name
            "Test specifications",        // specifications
            "Test notes"                  // notes
        );
        order.setProductDescription(productDesc);

        // Create DeliveryInformation with all required parameters
        var deliveryDate = java.time.LocalDate.now().plusDays(14); // 2 weeks from now
        var emailAddress = new com.belman.domain.common.valueobjects.EmailAddress(customerName.toLowerCase().replace(" ", ".") + "@example.com");
        var deliveryInfo = new com.belman.domain.order.DeliveryInformation(
            "123 Test Street, Test City, 12345",  // address
            deliveryDate,                         // estimatedDeliveryDate
            "Contact " + customerName,            // contactName
            emailAddress,                         // contactEmail
            "Handle with care"                    // specialInstructions
        );
        order.setDeliveryInformation(deliveryInfo);

        order.setStatus(com.belman.domain.order.OrderStatus.PENDING);

        return order;
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
            String sqlImplClassName = "com.belman.dataaccess.persistence.sql." + sqlImplName;

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
}
