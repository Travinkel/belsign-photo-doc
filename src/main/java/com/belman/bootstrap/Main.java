package com.belman.bootstrap;

import com.belman.bootstrap.config.ApplicationBootstrapper;
import com.belman.bootstrap.config.DevModeConfig;
import com.belman.bootstrap.di.ServiceLocator;
import com.belman.bootstrap.di.ServiceRegistry;
import com.belman.bootstrap.hacks.GluonInternalClassesFix;
import com.belman.bootstrap.lifecycle.LifecycleManager;
import com.belman.bootstrap.platform.DisplayServiceFactory;
import com.belman.bootstrap.platform.StorageServiceFactory;
import com.belman.bootstrap.security.RouteGuardInitializer;
import com.belman.common.logging.EmojiLogger;
import com.belman.common.logging.EmojiLoggerAdapter;
import com.belman.common.platform.PlatformUtils;
import com.belman.common.session.SessionContext;
import com.belman.common.session.SimpleSessionContext;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.application.error.ErrorHandler;
import com.belman.presentation.error.UIErrorHandlerAdapter;
import com.belman.presentation.navigation.RouteGuardImpl;
import com.belman.presentation.core.ViewRegistry;
import com.belman.presentation.core.ViewStackManager;
import com.belman.presentation.navigation.RoleBasedNavigationService;
import com.belman.presentation.usecases.splash.SplashView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Main application class for BelSign.
 * Handles application initialization and platform detection.
 * This class is the entry point for the application and coordinates bootstrapping
 * across the three layers (BLL, DAL, GUI).
 */
public class Main extends Application {

    public static final String SPLASH_VIEW = SplashView.class.getSimpleName();
    private static final EmojiLogger logger = EmojiLogger.getLogger(Main.class);
    private static final StackPane rootPane = new StackPane();

    /**
     * Static initializer to set system properties.
     */
    static {
        // Set platform to Desktop for consistent behavior
        if (System.getProperty("javafx.platform") == null) {
            System.setProperty("javafx.platform", "Desktop");
        }
    }

    /**
     * Main entry point for the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Check for dev mode flag in command line arguments or system properties
        boolean devMode = false;
        for (String arg : args) {
            if (arg.equals("--dev")) {
                devMode = true;
                break;
            }
        }

        // Also check system property
        if (!devMode && "true".equals(System.getProperty("belsign.devMode"))) {
            devMode = true;
        }

        // Initialize dev mode configuration
        DevModeConfig.initialize(devMode);

        // Generate a hashed password for testing (this should be removed in production)
        if (DevModeConfig.isDevMode()) {
            String password = "pass1234";
            String hashed = BCrypt.hashpw(password, BCrypt.gensalt(10));
            System.out.println("Hashed password: " + hashed);
        }

        Application.launch(args);
    }

    @Override
    public void init() throws Exception {
        try {
            super.init();

            // Initialize application
            logger.debug("Initializing Application");
        } catch (Exception e) {
            logger.error("Error initializing Application", e);
            throw new RuntimeException("Error initializing Application", e);
        }

        // Initialize Gluon internal classes fixes (DAL)
        logger.debug("Initializing Gluon internal classes fixes");
        GluonInternalClassesFix.initialize();

        // Bootstrap the application (DAL)
        logger.startup("Bootstrapping the application");
        ApplicationBootstrapper.initialize();

        // Initialize error handling (BLL)
        logger.debug("Initializing error handling");
        // Register the error handler with ServiceLocator instead of using ErrorHandlerFactory
        ServiceLocator.registerService(ErrorHandler.class, UIErrorHandlerAdapter.createWithDefaultErrorHandler());
        logger.success("Error handling initialized successfully");

        // Initialize service fallbacks for desktop platforms (DAL)
        if (!PlatformUtils.isRunningOnMobile()) {
            logger.file("Initializing desktop storage service fallback");
            StorageServiceFactory.getStorageService();

            logger.file("Initializing desktop display service fallback");
            DisplayServiceFactory.getDisplayService();

            logger.info("Using com.gluonhq.license.disable=true to disable Gluon licensing checks");
        }

        // Set up the ViewStackManager (GUI)
        logger.debug("Setting up ViewStackManager");

        // Create a simple SessionContext
        AuthenticationService authService = ServiceLocator.getService(AuthenticationService.class);
        SessionContext sessionContext = new SimpleSessionContext(authService);

        // Create a RoleBasedNavigationService with the SessionContext
        RoleBasedNavigationService navigationService = new RoleBasedNavigationService(sessionContext);

        // Get the ViewRegistry instance
        ViewRegistry viewRegistry = ViewRegistry.getInstance();

        // Initialize the ViewStackManager
        ViewStackManager.initialize(navigationService, viewRegistry);

        // Set up the Router (GUI)
        logger.debug("Setting up Router");
        // Router.setMobileApplication(this) - removed as part of MobileApplication cleanup

        // Initialize route guards for role-based access control (BLL + GUI)
        initializeRouteGuards();

        // Initialize the LifecycleManager and ServiceRegistry (BLL)
        logger.debug("Initializing LifecycleManager and ServiceRegistry");
        LoggerFactory loggerFactory = ServiceLocator.getService(LoggerFactory.class);
        ServiceRegistry.setLogger(loggerFactory);
        // Pass this Application instance to LifecycleManager
        LifecycleManager.init(this, loggerFactory);
    }

    /**
     * Initializes route guards for role-based access control.
     * This combines BLL and GUI layer bootstrapping.
     */
    private void initializeRouteGuards() {
        logger.debug("Initializing route guards");
        AuthenticationService authService = ServiceLocator.getService(AuthenticationService.class);
        Logger domainLogger = EmojiLoggerAdapter.getLogger(Main.class);
        RouteGuardImpl routeGuard = new RouteGuardImpl(domainLogger);
        RouteGuardInitializer.initialize(authService, routeGuard);
        logger.success("Route guards initialized successfully");
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Create the scene with the root pane
            Scene scene = new Scene(rootPane, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("BelSign Photo Documentation");

            // Initialize ViewStackManager with the root pane
            ViewStackManager.initWithRootPane(rootPane);

            // Apply platform-specific styling (GUI)
            logger.debug("Applying platform-specific styling");
            applyPlatformStyling(scene);

            // Load CSS (GUI)
            loadCss(scene);

            // Show the stage
            primaryStage.show();

            // Show the splash view (GUI)
            logger.info("Showing splash view");
            ViewStackManager.getInstance().navigateTo("SplashView");
        } catch (Exception e) {
            logger.error("Error starting application", e);
            throw new RuntimeException("Error starting application", e);
        }
    }

    @Override
    public void stop() throws Exception {
        // Shutdown the application (DAL)
        logger.shutdown("Shutting down the application");
        ApplicationBootstrapper.shutdown();

        super.stop();
    }

    /**
     * Applies platform-specific styling to the scene.
     *
     * @param scene the JavaFX scene
     */
    private void applyPlatformStyling(Scene scene) {
        try {
            if (scene != null) {
                // App is now mobile-only and Android-focused, so always apply smartphone styling
                logger.debug("Applying smartphone styling (mobile-only mode)");
                scene.getRoot().getStyleClass().add("smartphone");
            }
        } catch (Exception e) {
            logger.error("Platform styling failed: {}", e.getMessage(), e);
        }
    }

    /**
     * Loads CSS for the application.
     *
     * @param scene the JavaFX scene
     */
    private void loadCss(Scene scene) {
        var css = getClass().getResource("/com/belman/styles/app.css");
        logger.debug("Loading app.css from: " + css);

        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
            logger.info("✅ Loaded app.css from: " + css);
        } else {
            logger.warn("⚠️ Could not find app.css at /com/belman/styles/app.css");
        }
    }
}
