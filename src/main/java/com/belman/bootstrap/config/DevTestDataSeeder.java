package com.belman.bootstrap.config;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.logging.EmojiLogger;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.photo.PhotoTemplateRepository;
import com.belman.domain.report.ReportRepository;
import com.belman.domain.user.UserRepository;

/**
 * Seeds test data for development and testing environments.
 * This class provides methods to seed test data for all InMemoryRepositories,
 * ensuring that the application has data to work with in development and testing environments.
 */
public class DevTestDataSeeder {

    private static final EmojiLogger logger = EmojiLogger.getLogger(DevTestDataSeeder.class);

    /**
     * Seeds test data for all repositories.
     * This method should be called when the application boots in dev/test mode.
     * It will only seed data if the application is running in memory mode.
     */
    public static void seedData() {
        // Only seed data if we're in memory mode
        if (!StorageTypeConfig.isMemoryMode()) {
            logger.info("Not in memory mode, skipping test data seeding");
            return;
        }

        logger.info("🌱 Seeding test data for in-memory repositories");

        try {
            // Get all repositories from ServiceLocator
            UserRepository userRepository = ServiceLocator.getService(UserRepository.class);
            OrderRepository orderRepository = ServiceLocator.getService(OrderRepository.class);
            PhotoRepository photoRepository = ServiceLocator.getService(PhotoRepository.class);
            PhotoTemplateRepository photoTemplateRepository = ServiceLocator.getService(PhotoTemplateRepository.class);
            ReportRepository reportRepository = ServiceLocator.getService(ReportRepository.class);

            System.out.println("OrderRepo in SEEDER: " + orderRepository.hashCode());

            // Check if the OrderRepository already has data
            if (!orderRepository.findAll().isEmpty()) {
                logger.info("OrderRepository already contains data, skipping seeding");
                return;
            }

            // Call the existing seedTestData method in RepositoryInitializer
            RepositoryInitializer.seedTestData(
                userRepository,
                orderRepository,
                photoRepository,
                photoTemplateRepository,
                reportRepository
            );

            logger.success("✅ Test data seeded successfully");
        } catch (Exception e) {
            logger.error("❌ Error seeding test data", e);
        }
    }

    /**
     * Seeds test data for all repositories, regardless of storage mode.
     * This method can be called to force seeding of test data even if the application
     * is not running in memory mode. Use with caution in non-development environments.
     */
    public static void forceSeedData() {
        logger.info("🌱 Force seeding test data for repositories (ignoring storage mode)");

        try {
            // Get all repositories from ServiceLocator
            UserRepository userRepository = ServiceLocator.getService(UserRepository.class);
            OrderRepository orderRepository = ServiceLocator.getService(OrderRepository.class);
            PhotoRepository photoRepository = ServiceLocator.getService(PhotoRepository.class);
            PhotoTemplateRepository photoTemplateRepository = ServiceLocator.getService(PhotoTemplateRepository.class);
            ReportRepository reportRepository = ServiceLocator.getService(ReportRepository.class);

            System.out.println("OrderRepo in FORCE SEEDER: " + orderRepository.hashCode());

            // Check if the OrderRepository already has data
            if (!orderRepository.findAll().isEmpty()) {
                logger.info("OrderRepository already contains data, skipping force seeding");
                return;
            }

            // Call the existing seedTestData method in RepositoryInitializer
            RepositoryInitializer.seedTestData(
                userRepository,
                orderRepository,
                photoRepository,
                photoTemplateRepository,
                reportRepository
            );

            logger.success("✅ Test data force-seeded successfully");
        } catch (Exception e) {
            logger.error("❌ Error force-seeding test data", e);
        }
    }
}
