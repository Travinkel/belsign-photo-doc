package com.belman.bootstrap.config;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.bootstrap.di.ServiceRegistry;
import com.belman.bootstrap.config.StorageTypeManager;
import com.belman.common.logging.EmojiLogger;
import com.belman.common.session.SessionContext;
import com.belman.common.session.SimpleSessionContext;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.photo.PhotoTemplateRepository;
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
import com.belman.application.usecase.photo.PhotoCaptureService;
import com.belman.application.usecase.photo.DefaultPhotoCaptureService;
import com.belman.application.usecase.photo.PhotoTemplateService;
import com.belman.application.usecase.photo.DefaultPhotoTemplateService;
import com.belman.application.usecase.order.OrderProgressService;
import com.belman.application.usecase.order.DefaultOrderProgressService;
import com.belman.application.usecase.photo.CameraService;
import com.belman.dataaccess.file.CameraServiceFactory;

import javax.sql.DataSource;

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
            // Initialize the storage type manager to determine which database to use
            logger.database("Initializing storage type manager");
            StorageTypeManager.initialize();
            DataSource dataSource = StorageTypeManager.getActiveDataSource();

            if (dataSource != null) {
                logger.success("Database connection pool initialized successfully");
            } else {
                logger.info("Using in-memory repositories");
            }

            // Create repositories
            UserRepository userRepository;
            OrderRepository orderRepository;
            logger.debug("Creating repositories");

            // Initialize repositories
            Object[] repositories;
            repositories = RepositoryInitializer.initializeRepositories(dataSource);

            userRepository = (UserRepository) repositories[0];
            orderRepository = (OrderRepository) repositories[1];
            PhotoRepository photoRepository = (PhotoRepository) repositories[2];
            PhotoTemplateRepository photoTemplateRepository = (PhotoTemplateRepository) repositories[3];
            ReportRepository reportRepository = (ReportRepository) repositories[4];

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

            // Initialize PhotoCaptureService
            logger.database("Creating DefaultPhotoCaptureService");
            PhotoCaptureService photoCaptureService = new DefaultPhotoCaptureService(photoRepository, ServiceLocator.getService(LoggerFactory.class));
            ServiceRegistry.registerService(photoCaptureService);
            logger.success("Using DefaultPhotoCaptureService");

            // Initialize PhotoTemplateService
            logger.database("Creating DefaultPhotoTemplateService");
            PhotoTemplateService photoTemplateService = new DefaultPhotoTemplateService(orderRepository, photoRepository, photoTemplateRepository, userRepository, ServiceLocator.getService(LoggerFactory.class));
            ServiceRegistry.registerService(photoTemplateService);
            logger.success("Using DefaultPhotoTemplateService");

            // Initialize OrderProgressService
            logger.database("Creating DefaultOrderProgressService");
            OrderProgressService orderProgressService = new DefaultOrderProgressService(orderRepository, photoTemplateService, ServiceLocator.getService(LoggerFactory.class));
            ServiceRegistry.registerService(orderProgressService);
            logger.success("Using DefaultOrderProgressService");

            // Initialize WorkerService
            logger.database("Creating DefaultWorkerService");
            WorkerService workerService = new DefaultWorkerService(photoCaptureService, photoTemplateService, orderProgressService, ServiceLocator.getService(LoggerFactory.class));
            ServiceRegistry.registerService(workerService);
            logger.success("Using DefaultWorkerService");

            // Initialize OrderService
            logger.database("Creating DefaultOrderService");
            OrderService orderService = new DefaultOrderService(orderRepository, ServiceLocator.getService(LoggerFactory.class));
            ServiceRegistry.registerService(orderService);
            logger.success("Using DefaultOrderService");

            // Initialize UserService
            logger.database("Creating DefaultUserService");
            PasswordHasher passwordHasher = new BCryptPasswordHasher();
            UserService userService = new DefaultUserService(userRepository, passwordHasher, ServiceLocator.getService(LoggerFactory.class));
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
            CameraService cameraService;

            // Check if we're running in memory mode
            if (StorageTypeConfig.isMemoryMode()) {
                // In memory mode, use InMemoryCameraService to avoid searching mock/camera folder
                logger.database("Creating InMemoryCameraService for memory mode");
                cameraService = new com.belman.application.usecase.photo.InMemoryCameraService();
                ServiceRegistry.registerService(cameraService);
                logger.success("Using InMemoryCameraService (no mock/camera folder search)");
            } else {
                // In other modes, use the regular CameraServiceFactory
                logger.database("Creating CameraServiceFactory");
                CameraServiceFactory cameraServiceFactory = new CameraServiceFactory(ServiceLocator.getService(LoggerFactory.class), PHOTO_STORAGE_DIRECTORY, true);
                logger.success("Using CameraServiceFactory");

                // Create and register MockCameraService
                logger.database("Creating MockCameraService");
                cameraService = cameraServiceFactory.createMockCameraService();
                ServiceRegistry.registerService(cameraService);
                logger.success("Using MockCameraService");
            }

            // Log which repositories are active
            logger.database("Active repositories:");
            logger.database("- UserRepository: " + userRepository.getClass().getSimpleName());
            logger.database("- OrderRepository: " + orderRepository.getClass().getSimpleName());
            logger.database("- PhotoRepository: " + photoRepository.getClass().getSimpleName());
            logger.database("- PhotoTemplateRepository: " + photoTemplateRepository.getClass().getSimpleName());
            logger.database("- ReportRepository: " + reportRepository.getClass().getSimpleName());

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

            // Initialize PhotoCube managers
            logger.debug("Initializing PhotoCube managers");
            PhotoCubeManagersInitializer.initialize();
            logger.success("PhotoCube managers initialized successfully");

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
            // Shutdown storage type manager
            logger.database("Shutting down storage type manager");
            StorageTypeManager.shutdown();
            logger.success("Storage type manager shut down successfully");

            initialized = false;
            logger.shutdown("Application shut down successfully 👋");
        } catch (Exception e) {
            logger.failure("Failed to shut down application properly");
            logger.error("Shutdown error details", e);
        }
    }
}
