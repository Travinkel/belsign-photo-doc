package com.belman.bootstrap.config;

import com.belman.bootstrap.di.ServiceRegistry;
import com.belman.common.logging.EmojiLogger;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.user.UserRepository;
import com.belman.repository.persistence.memory.InMemoryOrderRepository;
import com.belman.repository.persistence.memory.InMemoryUserRepository;

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
     * @return an array containing the initialized repositories in the order [userRepository, orderRepository]
     */
    public static Object[] initializeRepositories(DataSource dataSource) {
        UserRepository userRepository;
        OrderRepository orderRepository;

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
            } catch (Exception e) {
                // Fall back to in-memory repositories if there's an error
                logger.warn("Failed to initialize repositories, falling back to in-memory repositories", e);
                userRepository = initializeInMemoryUserRepository();
                orderRepository = initializeInMemoryOrderRepository();
            }
        } else {
            // Fall back to in-memory repositories if database is not available
            logger.warn("Database is not available, falling back to in-memory repositories");
            userRepository = initializeInMemoryUserRepository();
            orderRepository = initializeInMemoryOrderRepository();
        }

        return new Object[]{userRepository, orderRepository};
    }

    /**
     * Initializes an in-memory user repository.
     *
     * @return the initialized in-memory user repository
     */
    private static UserRepository initializeInMemoryUserRepository() {
        logger.database("Creating in-memory UserRepository as fallback");
        UserRepository userRepository = new InMemoryUserRepository();
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
            String sqlImplClassName = "com.belman.repository.persistence.sql." + sqlImplName;

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