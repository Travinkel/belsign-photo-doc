package com.belman.bootstrap.config;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.bootstrap.di.ServiceRegistry;
import com.belman.bootstrap.persistence.DatabaseConfig;
import com.belman.bootstrap.persistence.SqliteDatabaseConfig;
import com.belman.bootstrap.config.DevModeConfig;
import com.belman.common.logging.EmojiLogger;
import com.belman.common.session.SessionContext;
import com.belman.common.session.SimpleSessionContext;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.report.ReportRepository;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.security.PasswordHasher;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRole;
import com.belman.application.usecase.photo.PhotoService;
import com.belman.presentation.navigation.RoleBasedNavigationService;
import com.belman.domain.user.UserRepository;
import com.belman.application.usecase.order.DefaultOrderService;
import com.belman.application.usecase.order.OrderService;
import com.belman.application.usecase.photo.DefaultPhotoService;
import com.belman.application.usecase.qa.DefaultQAService;
import com.belman.application.usecase.qa.QAService;
import com.belman.application.usecase.report.DefaultReportService;
import com.belman.application.usecase.report.PDFExportService;
import com.belman.application.usecase.report.ReportService;
import com.belman.application.usecase.security.BCryptPasswordHasher;
import com.belman.application.usecase.security.DefaultAuthenticationService;
import com.belman.application.usecase.user.DefaultUserService;
import com.belman.application.usecase.user.UserService;
import com.belman.application.usecase.worker.DefaultWorkerService;
import com.belman.application.usecase.worker.WorkerService;
import com.belman.application.usecase.photo.CameraService;
import com.belman.application.usecase.order.OrderIntakeService;
import com.belman.dataaccess.file.CameraServiceFactory;
import com.belman.dataaccess.provider.MockFolderOrderProvider;
import com.belman.dataaccess.provider.OrderProvider;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/**
 * Initializes the application's services and repositories.
 * This class is responsible for setting up the application's dependencies
 * and ensuring they are properly initialized before use.
 */
public class ApplicationInitializer {

    private static final EmojiLogger logger = EmojiLogger.getLogger(ApplicationInitializer.class);
    // Photo storage directory
    private static final String PHOTO_STORAGE_DIRECTORY = "camera";
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
                    logger.warn("SQLite database not available, falling back to in-memory repositories. SQL Server offline and SQLite not configured.");
                }
            }

            // Initialize repositories based on dev mode
            Object[] repositories;
            if (DevModeConfig.isDevMode()) {
                logger.info("🛠️ Development mode enabled - using hybrid repository configuration");
                repositories = RepositoryInitializer.initializeDevModeRepositories(dataSource);
            } else {
                repositories = RepositoryInitializer.initializeRepositories(dataSource);
            }

            userRepository = (UserRepository) repositories[0];
            orderRepository = (OrderRepository) repositories[1];
            PhotoRepository photoRepository = (PhotoRepository) repositories[2];
            ReportRepository reportRepository = (ReportRepository) repositories[3];

            // Initialize PhotoService
            logger.database("Creating DefaultPhotoService");
            PhotoService photoService = new DefaultPhotoService(photoRepository, PHOTO_STORAGE_DIRECTORY);
            ServiceRegistry.registerService(photoService);
            logger.success("Using DefaultPhotoService");

            // Initialize QAService
            logger.database("Creating DefaultQAService");
            QAService qaService = new DefaultQAService(photoRepository);
            ServiceRegistry.registerService(qaService);
            logger.success("Using DefaultQAService");

            // Initialize WorkerService
            logger.database("Creating DefaultWorkerService");
            WorkerService workerService = new DefaultWorkerService(orderRepository, photoRepository);
            ServiceRegistry.registerService(workerService);
            logger.success("Using DefaultWorkerService");

            // Initialize OrderService
            logger.database("Creating DefaultOrderService");
            OrderService orderService = new DefaultOrderService(orderRepository);
            ServiceRegistry.registerService(orderService);
            logger.success("Using DefaultOrderService");

            // Initialize UserService
            logger.database("Creating DefaultUserService");
            PasswordHasher passwordHasher = new BCryptPasswordHasher();
            UserService userService = new DefaultUserService(userRepository, passwordHasher);
            ServiceRegistry.registerService(userService);
            logger.success("Using DefaultUserService");

            // Initialize PDFExportService
            logger.database("Creating PDFExportService");
            PDFExportService pdfExportService = new PDFExportService(orderRepository);
            ServiceRegistry.registerService(pdfExportService);
            logger.success("Using PDFExportService");

            // Initialize ReportService
            logger.database("Creating DefaultReportService");
            ReportService reportService = new DefaultReportService(reportRepository, orderRepository, photoRepository, pdfExportService);
            ServiceRegistry.registerService(reportService);
            logger.success("Using DefaultReportService");

            // Initialize CameraService
            logger.database("Creating CameraServiceFactory");
            CameraServiceFactory cameraServiceFactory = new CameraServiceFactory(ServiceLocator.getService(LoggerFactory.class), PHOTO_STORAGE_DIRECTORY, true);
            logger.success("Using CameraServiceFactory");

            // Create and register MockCameraService
            logger.database("Creating MockCameraService");
            CameraService cameraService = cameraServiceFactory.createMockCameraService();
            ServiceRegistry.registerService(cameraService);
            logger.success("Using MockCameraService");

            // Initialize MockFolderOrderProvider
            logger.database("Creating MockFolderOrderProvider");
            OrderProvider orderProvider = new MockFolderOrderProvider(ServiceLocator.getService(LoggerFactory.class));
            ServiceRegistry.registerService(orderProvider);
            logger.success("Using MockFolderOrderProvider");

            // Initialize OrderIntakeService
            logger.database("Creating OrderIntakeService");
            // Get a default user for creating orders
            UserBusiness defaultUser = userRepository.findAll().stream()
                    .filter(user -> user.getRoles().contains(UserRole.ADMIN))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No admin user found for OrderIntakeService"));
            OrderIntakeService orderIntakeService = new OrderIntakeService(
                    ServiceLocator.getService(LoggerFactory.class),
                    orderProvider,
                    orderService,
                    defaultUser);
            ServiceRegistry.registerService(orderIntakeService);
            logger.success("Using OrderIntakeService");

            // Start the OrderIntakeService
            logger.database("Starting OrderIntakeService");
            orderIntakeService.start(5, 30, TimeUnit.SECONDS);
            logger.success("OrderIntakeService started");

            // Create services
            logger.debug("Creating extended authentication service");
            AuthenticationService authenticationService = new DefaultAuthenticationService(userRepository);
            // Register the AuthenticationService with the ServiceRegistry
            ServiceRegistry.registerService(authenticationService);
            logger.success("Extended authentication service created successfully");

            // Initialize a simple SessionContext
            logger.debug("Initializing simple SessionContext");
            SessionContext sessionContext = new SimpleSessionContext(authenticationService);
            // Register the SessionContext with the ServiceRegistry
            ServiceRegistry.registerService(sessionContext);
            logger.success("Simple SessionContext initialized successfully");

            // Initialize RoleBasedNavigationService
            logger.debug("Initializing RoleBasedNavigationService");
            RoleBasedNavigationService navigationService = new RoleBasedNavigationService(sessionContext);
            // Register the RoleBasedNavigationService with the ServiceRegistry
            ServiceRegistry.registerService(navigationService);
            logger.success("RoleBasedNavigationService initialized successfully");

            // AccessPolicyFactory and RoleBasedAccessControlFactory are no longer used
            // Commented out as per task list
            /*
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
            */

            // Log the state of critical services if in dev mode
            if (DevModeConfig.isDevMode()) {
                logCriticalServicesState();
            }

            initialized = true;
            logger.startup("Application initialized successfully ✨");
        } catch (Exception e) {
            logger.failure("Failed to initialize application");
            logger.error("Initialization error details", e);
            throw new RuntimeException("Failed to initialize application", e);
        }
    }

    /**
     * Logs the state of critical services.
     * This method is called during application startup in development mode.
     */
    private static void logCriticalServicesState() {
        logger.info("📊 Logging state of critical services for development mode");

        try {
            // Check AuthenticationService
            AuthenticationService authService = ServiceLocator.getService(AuthenticationService.class);
            logger.info("🔑 AuthenticationService: {}", authService.getClass().getSimpleName());

            // Check SessionContext
            SessionContext sessionContext = ServiceLocator.getService(SessionContext.class);
            logger.info("👤 SessionContext: {}", sessionContext.getClass().getSimpleName());

            // Check RoleBasedNavigationService
            RoleBasedNavigationService navigationService = ServiceLocator.getService(RoleBasedNavigationService.class);
            logger.info("🧭 RoleBasedNavigationService: {}", navigationService.getClass().getSimpleName());

            // Check OrderService
            OrderService orderService = ServiceLocator.getService(OrderService.class);
            logger.info("📋 OrderService: {}", orderService.getClass().getSimpleName());

            // Check PhotoService
            PhotoService photoService = ServiceLocator.getService(PhotoService.class);
            logger.info("📷 PhotoService: {}", photoService.getClass().getSimpleName());

            logger.success("✅ All critical services are available");
        } catch (Exception e) {
            logger.error("❌ Error checking critical services", e);
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
