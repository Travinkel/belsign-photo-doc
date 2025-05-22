package com.belman.bootstrap.config;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.bootstrap.di.ServiceRegistry;
import com.belman.bootstrap.config.StorageTypeConfig;
import com.belman.bootstrap.config.StorageTypeManager;
import com.belman.common.logging.EmojiLogger;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.photo.PhotoTemplateRepository;
import com.belman.domain.report.ReportRepository;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserRepository;
import com.belman.dataaccess.persistence.memory.InMemoryOrderRepository;
import com.belman.dataaccess.persistence.memory.InMemoryUserRepository;
import com.belman.dataaccess.repository.memory.InMemoryPhotoRepository;
import com.belman.dataaccess.repository.memory.InMemoryPhotoTemplateRepository;
import com.belman.dataaccess.repository.memory.InMemoryReportRepository;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.util.List;

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
        PhotoTemplateRepository photoTemplateRepository;
        ReportRepository reportRepository;
        LoggerFactory loggerFactory = ServiceLocator.getService(LoggerFactory.class);

        if (dataSource != null) {
            try {
                // Initialize UserRepository with SQL implementation
                logger.database("Creating SQL UserRepository");
                userRepository = createRepository(UserRepository.class, "SqlUserRepository", dataSource,
                        InMemoryUserRepository.class);
                ServiceRegistry.registerService(userRepository);

                // Initialize OrderRepository with SQL implementation
                logger.database("Creating SQL OrderRepository");
                orderRepository = createRepository(OrderRepository.class, "SqlOrderRepository", dataSource,
                        InMemoryOrderRepository.class);
                ServiceRegistry.registerService(orderRepository);

                // Initialize PhotoRepository - use in-memory implementation for now
                // In the future, this could be replaced with a SQL implementation
                photoRepository = initializeInMemoryPhotoRepository(loggerFactory);
                ServiceRegistry.registerService(photoRepository);

                // Initialize PhotoTemplateRepository with SQL implementation
                logger.database("Creating SQL PhotoTemplateRepository");
                photoTemplateRepository = createRepository(PhotoTemplateRepository.class, "SqlPhotoTemplateRepository", dataSource,
                        InMemoryPhotoTemplateRepository.class);
                ServiceRegistry.registerService(photoTemplateRepository);
                logger.success("Using SQL-based PhotoTemplateRepository");

                // Initialize ReportRepository - use in-memory implementation for now
                // In the future, this could be replaced with a SQL implementation
                reportRepository = initializeInMemoryReportRepository(loggerFactory);
                ServiceRegistry.registerService(reportRepository);
            } catch (Exception e) {
                // Fall back to in-memory repositories if there's an error
                logger.warn("Failed to initialize SQL repositories, falling back to in-memory repositories", e);
                userRepository = initializeInMemoryUserRepository();
                orderRepository = initializeInMemoryOrderRepository();
                photoRepository = initializeInMemoryPhotoRepository(loggerFactory);
                photoTemplateRepository = initializeInMemoryPhotoTemplateRepository(loggerFactory);
                reportRepository = initializeInMemoryReportRepository(loggerFactory);
            }
        } else {
            // Fall back to in-memory repositories if database is not available
            logger.warn("Database is not available, falling back to in-memory repositories");
            userRepository = initializeInMemoryUserRepository();
            orderRepository = initializeInMemoryOrderRepository();
            photoRepository = initializeInMemoryPhotoRepository(loggerFactory);
            photoTemplateRepository = initializeInMemoryPhotoTemplateRepository(loggerFactory);
            reportRepository = initializeInMemoryReportRepository(loggerFactory);
        }

        // Log a summary of which repository mode is active
        StorageTypeConfig.StorageType storageType = StorageTypeConfig.getStorageType();
        String repositoryMode;
        switch (storageType) {
            case MEMORY:
                repositoryMode = "in-memory (BELSIGN_STORAGE_TYPE=memory)";
                break;
            case SQLITE:
                repositoryMode = "SQLite database (BELSIGN_STORAGE_TYPE=sqlite)";
                break;
            case SQLSERVER:
                repositoryMode = "SQL Server database (BELSIGN_STORAGE_TYPE=sqlserver)";
                break;
            default:
                repositoryMode = dataSource != null ? "SQL database" : "in-memory";
                break;
        }

        logger.startup("📊 Repository mode: " + repositoryMode);
        logger.startup("📊 UserRepository: " + userRepository.getClass().getSimpleName());
        logger.startup("📊 OrderRepository: " + orderRepository.getClass().getSimpleName());
        logger.startup("📊 PhotoRepository: " + photoRepository.getClass().getSimpleName());
        logger.startup("📊 PhotoTemplateRepository: " + photoTemplateRepository.getClass().getSimpleName());
        logger.startup("📊 ReportRepository: " + reportRepository.getClass().getSimpleName());

        // Seed test data only for in-memory mode
        if (StorageTypeConfig.getStorageType() == StorageTypeConfig.StorageType.MEMORY) {
            logger.info("Using in-memory storage, seeding test data");
            seedTestData(userRepository, orderRepository, photoRepository, photoTemplateRepository, reportRepository);
            logger.success("Test data seeded successfully");
        }

        return new Object[]{userRepository, orderRepository, photoRepository, photoTemplateRepository, reportRepository};
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
     * Initializes an in-memory photo template repository.
     *
     * @param loggerFactory the logger factory to use
     * @return the initialized in-memory photo template repository
     */
    private static PhotoTemplateRepository initializeInMemoryPhotoTemplateRepository(LoggerFactory loggerFactory) {
        logger.database("Creating in-memory PhotoTemplateRepository as fallback");
        PhotoTemplateRepository photoTemplateRepository = new InMemoryPhotoTemplateRepository(loggerFactory);
        ServiceRegistry.registerService(photoTemplateRepository);
        logger.info("Using in-memory PhotoTemplateRepository as fallback");
        return photoTemplateRepository;
    }

    /**
     * Initializes repositories for development mode.
     * This method configures repositories for development, using SQLite for persistent data:
     * - SQL repositories with SQLite for persistent data (users, orders)
     * - In-memory repositories for transient data or development features (photos, reports)
     *
     * @param dataSource the SQLite data source to use for SQL repositories, or null to use in-memory repositories for everything
     * @return an array containing the initialized repositories in the order [userRepository, orderRepository, photoRepository, reportRepository]
     */
    public static Object[] initializeDevModeRepositories(DataSource dataSource) {
        UserRepository userRepository;
        OrderRepository orderRepository;
        PhotoRepository photoRepository;
        PhotoTemplateRepository photoTemplateRepository;
        ReportRepository reportRepository;
        LoggerFactory loggerFactory = ServiceLocator.getService(LoggerFactory.class);

        logger.startup("🛠️ Initializing repositories for development mode");

        if (dataSource != null) {
            try {
                // Initialize UserRepository - use SQLite for persistence in development mode
                logger.database("Dev mode: Creating SQL UserRepository with SQLite");
                userRepository = createRepository(UserRepository.class, "SqlUserRepository", dataSource,
                        InMemoryUserRepository.class);
                ServiceRegistry.registerService(userRepository);
                logger.info("Dev mode: Using SQLite-based UserRepository for persistence");

                // Initialize OrderRepository - use SQL implementation for development mode
                logger.database("Dev mode: Creating SQL OrderRepository with SQLite");
                orderRepository = createRepository(OrderRepository.class, "SqlOrderRepository", dataSource,
                        InMemoryOrderRepository.class);
                ServiceRegistry.registerService(orderRepository);
                logger.info("Dev mode: Using SQLite-based OrderRepository for persistence");

                // Initialize PhotoRepository - use in-memory implementation for development
                // This could be replaced with a SQLite implementation in the future
                photoRepository = initializeInMemoryPhotoRepository(loggerFactory);
                ServiceRegistry.registerService(photoRepository);
                logger.info("Dev mode: Using in-memory PhotoRepository for development");

                // Initialize PhotoTemplateRepository with SQL implementation for development mode
                logger.database("Dev mode: Creating SQL PhotoTemplateRepository with SQLite");
                photoTemplateRepository = createRepository(PhotoTemplateRepository.class, "SqlPhotoTemplateRepository", dataSource,
                        InMemoryPhotoTemplateRepository.class);
                ServiceRegistry.registerService(photoTemplateRepository);
                logger.info("Dev mode: Using SQLite-based PhotoTemplateRepository for persistence");

                // Initialize ReportRepository - use in-memory implementation for development
                // This could be replaced with a SQLite implementation in the future
                reportRepository = initializeInMemoryReportRepository(loggerFactory);
                ServiceRegistry.registerService(reportRepository);
                logger.info("Dev mode: Using in-memory ReportRepository for development");
            } catch (Exception e) {
                // Fall back to in-memory repositories if there's an error with SQLite
                logger.warn("Failed to initialize SQLite repositories for dev mode, falling back to in-memory repositories", e);
                userRepository = initializeInMemoryUserRepository();
                orderRepository = initializeInMemoryOrderRepository();
                photoRepository = initializeInMemoryPhotoRepository(loggerFactory);
                photoTemplateRepository = initializeInMemoryPhotoTemplateRepository(loggerFactory);
                reportRepository = initializeInMemoryReportRepository(loggerFactory);
            }
        } else {
            // Fall back to in-memory repositories if SQLite is not available
            logger.warn("SQLite database is not available for dev mode, falling back to in-memory repositories");
            userRepository = initializeInMemoryUserRepository();
            orderRepository = initializeInMemoryOrderRepository();
            photoRepository = initializeInMemoryPhotoRepository(loggerFactory);
            photoTemplateRepository = initializeInMemoryPhotoTemplateRepository(loggerFactory);
            reportRepository = initializeInMemoryReportRepository(loggerFactory);
        }

        // Log a summary of which repository mode is active
        StorageTypeConfig.StorageType storageType = StorageTypeConfig.getStorageType();
        String repositoryMode;
        switch (storageType) {
            case MEMORY:
                repositoryMode = "in-memory (BELSIGN_STORAGE_TYPE=memory)";
                break;
            case SQLITE:
                repositoryMode = "SQLite database (BELSIGN_STORAGE_TYPE=sqlite)";
                break;
            case SQLSERVER:
                repositoryMode = "SQL Server database (BELSIGN_STORAGE_TYPE=sqlserver)";
                break;
            default:
                repositoryMode = dataSource != null ? "SQLite database" : "in-memory";
                break;
        }

        logger.startup("📊 Dev mode repository configuration (" + repositoryMode + "):");
        logger.startup("📊 UserRepository: " + userRepository.getClass().getSimpleName());
        logger.startup("📊 OrderRepository: " + orderRepository.getClass().getSimpleName());
        logger.startup("📊 PhotoRepository: " + photoRepository.getClass().getSimpleName());
        logger.startup("📊 PhotoTemplateRepository: " + photoTemplateRepository.getClass().getSimpleName());
        logger.startup("📊 ReportRepository: " + reportRepository.getClass().getSimpleName());

        // Seed test data for development
        seedTestData(userRepository, orderRepository, photoRepository, photoTemplateRepository, reportRepository);

        return new Object[]{userRepository, orderRepository, photoRepository, photoTemplateRepository, reportRepository};
    }

    /**
     * Checks if an order with the given order number already exists in the repository.
     *
     * @param orderRepository the order repository to check
     * @param orderNumber the order number to check
     * @return true if an order with the given order number already exists, false otherwise
     */
    private static boolean orderExists(OrderRepository orderRepository, String orderNumber) {
        // Get all orders and check if any has the given order number
        return orderRepository.findAll().stream()
                .anyMatch(order -> order.getOrderNumber() != null && 
                          order.getOrderNumber().value().equals(orderNumber));
    }

    /**
     * Seeds test data for the application.
     * This method adds sample data to repositories for testing and demonstration purposes.
     *
     * @param userRepository the user repository to seed
     * @param orderRepository the order repository to seed
     * @param photoRepository the photo repository to seed
     * @param photoTemplateRepository the photo template repository to seed
     * @param reportRepository the report repository to seed
     */
    public static void seedTestData(UserRepository userRepository, OrderRepository orderRepository, 
                                    PhotoRepository photoRepository, PhotoTemplateRepository photoTemplateRepository,
                                    ReportRepository reportRepository) {
        try {
            logger.info("🌱 Seeding test data for application");

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

                    // Find production workers for order assignment
                    var productionWorkers = users.stream()
                        .filter(user -> user.getRoles().contains(com.belman.domain.user.UserRole.PRODUCTION))
                        .toList();

                    // Find QA users for photo approval
                    var qaUsers = users.stream()
                        .filter(user -> user.getRoles().contains(com.belman.domain.user.UserRole.QA))
                        .toList();

                    logger.info("Found " + productionWorkers.size() + " production workers for order assignment");
                    logger.info("Found " + qaUsers.size() + " QA users for photo approval");

                    // Create and save test orders
                    try {
                        // Order 1 - Check if it already exists
                        String orderNumber1 = "ORD-12-230615-WLD-0001";
                        if (!orderExists(orderRepository, orderNumber1)) {
                            // Create order 1
                            var order1 = createTestOrder(
                                orderNumber1, 
                                "Test Customer 1", 
                                "Welding work on expansion joint", 
                                defaultUser
                            );

                            // Assign to first production worker if available
                            if (!productionWorkers.isEmpty()) {
                                var worker = productionWorkers.get(0);
                                order1.setAssignedTo(new com.belman.domain.user.UserReference(
                                    worker.getId(), worker.getUsername()));
                                logger.info("Assigned order " + order1.getOrderNumber().value() + 
                                           " to production worker: " + worker.getUsername().value());
                            }

                            orderRepository.save(order1);
                            logger.info("Created test order: " + order1.getOrderNumber().value());

                            // Associate photo templates with order
                            associateTemplatesWithOrder(photoTemplateRepository, order1.getId());
                        } else {
                            logger.info("Order with number " + orderNumber1 + " already exists, skipping creation");
                        }

                        // Order 2 - Check if it already exists
                        String orderNumber2 = "ORD-45-230620-EXP-0002";
                        if (!orderExists(orderRepository, orderNumber2)) {
                            // Create order 2
                            var order2 = createTestOrder(
                                orderNumber2, 
                                "Test Customer 2", 
                                "Expansion joint assembly", 
                                defaultUser
                            );

                            // Assign to second production worker if available, or first if only one exists
                            if (productionWorkers.size() > 1) {
                                var worker = productionWorkers.get(1);
                                order2.setAssignedTo(new com.belman.domain.user.UserReference(
                                    worker.getId(), worker.getUsername()));
                                logger.info("Assigned order " + order2.getOrderNumber().value() + 
                                           " to production worker: " + worker.getUsername().value());
                            } else if (!productionWorkers.isEmpty()) {
                                var worker = productionWorkers.get(0);
                                order2.setAssignedTo(new com.belman.domain.user.UserReference(
                                    worker.getId(), worker.getUsername()));
                                logger.info("Assigned order " + order2.getOrderNumber().value() + 
                                           " to production worker: " + worker.getUsername().value());
                            }

                            orderRepository.save(order2);
                            logger.info("Created test order: " + order2.getOrderNumber().value());

                            // Associate photo templates with order
                            associateTemplatesWithOrder(photoTemplateRepository, order2.getId());

                            // Add test photos to this order for QA review
                            if (!productionWorkers.isEmpty() && !qaUsers.isEmpty()) {
                                createTestPhotosForOrder(order2.getId(), productionWorkers.get(0), qaUsers.get(0), photoRepository, photoTemplateRepository);
                                logger.info("Created test photos for order: " + order2.getOrderNumber().value());
                            }
                        } else {
                            logger.info("Order with number " + orderNumber2 + " already exists, skipping creation");
                        }

                        // Order 3 - Check if it already exists
                        String orderNumber3 = "ORD-78-230625-PIP-0003";
                        if (!orderExists(orderRepository, orderNumber3)) {
                            // Create order 3
                            var order3 = createTestOrder(
                                orderNumber3, 
                                "Test Customer 3", 
                                "Pipe end-cap inspection", 
                                defaultUser
                            );

                            // Assign to third production worker if available
                            if (productionWorkers.size() > 2) {
                                var worker = productionWorkers.get(2);
                                order3.setAssignedTo(new com.belman.domain.user.UserReference(
                                    worker.getId(), worker.getUsername()));
                                logger.info("Assigned order " + order3.getOrderNumber().value() + 
                                           " to production worker: " + worker.getUsername().value());
                            }

                            orderRepository.save(order3);
                            logger.info("Created test order: " + order3.getOrderNumber().value());

                            // Associate photo templates with order
                            associateTemplatesWithOrder(photoTemplateRepository, order3.getId());
                        } else {
                            logger.info("Order with number " + orderNumber3 + " already exists, skipping creation");
                        }

                        // Templates are already associated with orders in their respective creation blocks
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
     * Associates photo templates with an order.
     * This method associates all available photo templates with the specified order.
     *
     * @param photoTemplateRepository the photo template repository
     * @param orderId the ID of the order to associate templates with
     */
    private static void associateTemplatesWithOrder(PhotoTemplateRepository photoTemplateRepository, OrderId orderId) {
        try {
            // Get all available templates
            List<PhotoTemplate> templates = photoTemplateRepository.findAll();
            logger.info("Associating " + templates.size() + " templates with order: " + orderId.id());

            // Associate each template with the order
            for (PhotoTemplate template : templates) {
                photoTemplateRepository.associateWithOrder(orderId, template.name(), true);
                logger.info("Associated template '" + template.name() + "' with order: " + orderId.id());
            }
        } catch (Exception e) {
            logger.error("Error associating templates with order: " + orderId.id(), e);
        }
    }

    /**
     * Creates test photos for an order with different approval statuses.
     * This method creates photos for each template associated with the order,
     * with some photos in PENDING status, some APPROVED, and some REJECTED.
     *
     * @param orderId the ID of the order to create photos for
     * @param productionUser the production user who uploads the photos
     * @param qaUser the QA user who reviews the photos
     * @param photoRepository the photo repository to save photos to
     * @param photoTemplateRepository the photo template repository to get templates from
     */
    private static void createTestPhotosForOrder(
            com.belman.domain.order.OrderId orderId,
            com.belman.domain.user.UserBusiness productionUser,
            com.belman.domain.user.UserBusiness qaUser,
            com.belman.domain.photo.PhotoRepository photoRepository,
            com.belman.domain.photo.PhotoTemplateRepository photoTemplateRepository) {

        try {
            // Get templates for this order
            List<com.belman.domain.photo.PhotoTemplate> templates = photoTemplateRepository.findByOrderId(orderId);
            if (templates.isEmpty()) {
                logger.warn("No templates found for order: " + orderId.id() + ", cannot create test photos");
                return;
            }

            logger.info("Creating test photos for " + templates.size() + " templates");

            // Create a reference to the QA user for approval/rejection
            com.belman.domain.user.UserReference qaUserRef = new com.belman.domain.user.UserReference(
                qaUser.getId(), qaUser.getUsername());

            // Create photos for each template with different statuses
            int count = 0;
            for (com.belman.domain.photo.PhotoTemplate template : templates) {
                // Create a test photo path
                String photoPath = "memory://test-photos/order-" + orderId.id() + "/photo-" + count + ".jpg";
                com.belman.domain.photo.Photo photo = new com.belman.domain.photo.Photo(photoPath);

                // Create the photo document
                com.belman.domain.photo.PhotoDocument photoDoc = 
                    com.belman.domain.photo.PhotoDocumentFactory.createForOrderWithCurrentTimestamp(
                        template, photo, productionUser, orderId);

                // Set different statuses based on the count
                if (count % 3 == 0) {
                    // Leave as PENDING for the first template and every third one
                    logger.info("Created PENDING photo for template: " + template.name());
                } else if (count % 3 == 1) {
                    // Approve the second template and every third one after that
                    photoDoc.approve(qaUserRef, com.belman.domain.common.valueobjects.Timestamp.now());
                    logger.info("Created APPROVED photo for template: " + template.name());
                } else {
                    // Reject the third template and every third one after that
                    photoDoc.reject(qaUserRef, com.belman.domain.common.valueobjects.Timestamp.now(), 
                                   "Test rejection comment for template " + template.name());
                    logger.info("Created REJECTED photo for template: " + template.name());
                }

                // Save the photo
                photoRepository.save(photoDoc);
                count++;
            }

            logger.info("Created " + count + " test photos for order: " + orderId.id());
        } catch (Exception e) {
            logger.error("Error creating test photos for order: " + orderId.id(), e);
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

        // Create customer ID
        var customerId = new com.belman.domain.customer.CustomerId(java.util.UUID.randomUUID().toString());

        // Create order using factory method
        var orderNumberObj = new com.belman.domain.order.OrderNumber(orderNumber);
        var order = com.belman.domain.order.OrderBusiness.createNew(orderNumberObj, createdBy);

        // Set customer ID
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
        // Check storage type to determine which implementation to use
        StorageTypeConfig.StorageType storageType = StorageTypeConfig.getStorageType();

        // Special case for OrderRepository: Always try to use SQL implementation first if not in memory mode
        // This ensures that orders are loaded from the database even if mock mode is enabled
        if (repositoryInterface == OrderRepository.class && 
            storageType != StorageTypeConfig.StorageType.MEMORY) {
            logger.database("OrderRepository: Prioritizing SQL implementation regardless of mock mode");

            try {
                // Try to create SQL implementation first
                T sqlRepository = createSqlRepository(repositoryInterface, sqlImplName, dataSource);
                if (sqlRepository != null) {
                    logger.info("Using SQL-based OrderRepository for order loading (priority mode)");
                    logger.startup("📊 IMPORTANT: Orders will be loaded from the database regardless of mock mode");
                    return sqlRepository;
                }
            } catch (Exception e) {
                logger.warn("Failed to create SQL-based OrderRepository, falling back to in-memory implementation", e);
                logger.error("Error details: " + e.getMessage());
                // Fall through to in-memory implementation
            }
        }

        // For repositories other than OrderRepository, or if OrderRepository SQL creation failed,
        // check if we should use in-memory implementation
        if (storageType == StorageTypeConfig.StorageType.MEMORY) {
            // In memory mode, always use in-memory implementation
            // For OrderRepository, we only use in-memory if SQL creation failed above
            logger.database("Creating in-memory " + repositoryInterface.getSimpleName() + 
                           (repositoryInterface == OrderRepository.class ? " (SQL creation failed)" : ""));

            // Get the LoggerFactory service
            LoggerFactory loggerFactory = ServiceLocator.getService(LoggerFactory.class);

            // Try to create instance with LoggerFactory parameter first
            try {
                Constructor<?> constructor = inMemoryImplClass.getDeclaredConstructor(LoggerFactory.class);
                T repository = (T) constructor.newInstance(loggerFactory);
                logger.info("Using in-memory " + repositoryInterface.getSimpleName() + " with LoggerFactory");
                return repository;
            } catch (NoSuchMethodException e) {
                // If no constructor with LoggerFactory, try no-arg constructor
                T repository = inMemoryImplClass.getDeclaredConstructor().newInstance();
                logger.info("Using in-memory " + repositoryInterface.getSimpleName());
                return repository;
            }
        }

        // Try to create SQL implementation first
        T sqlRepository = createSqlRepository(repositoryInterface, sqlImplName, dataSource);
        if (sqlRepository != null) {
            return sqlRepository;
        }

        // Fall back to in-memory implementation
        logger.database("Creating in-memory " + repositoryInterface.getSimpleName());

        // Get the LoggerFactory service
        LoggerFactory loggerFactory = ServiceLocator.getService(LoggerFactory.class);

        // Try to create instance with LoggerFactory parameter first
        try {
            Constructor<?> constructor = inMemoryImplClass.getDeclaredConstructor(LoggerFactory.class);
            T repository = (T) constructor.newInstance(loggerFactory);
            logger.info("Using in-memory " + repositoryInterface.getSimpleName() + " with LoggerFactory");
            return repository;
        } catch (NoSuchMethodException e) {
            // If no constructor with LoggerFactory, try no-arg constructor
            T repository = inMemoryImplClass.getDeclaredConstructor().newInstance();
            logger.info("Using in-memory " + repositoryInterface.getSimpleName());
            return repository;
        }
    }

    /**
     * Helper method to create a SQL repository instance.
     * 
     * @param <T> the repository interface type
     * @param repositoryInterface the repository interface class
     * @param sqlImplName the name of the SQL implementation class
     * @param dataSource the DataSource to pass to the SQL implementation constructor
     * @return the SQL repository instance, or null if it couldn't be created
     */
    @SuppressWarnings("unchecked")
    private static <T> T createSqlRepository(Class<T> repositoryInterface, String sqlImplName, DataSource dataSource) {
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

        return null;
    }
}
