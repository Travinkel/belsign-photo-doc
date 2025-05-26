package com.belman.bootstrap;

import com.belman.bootstrap.config.ApplicationBootstrapper;
import com.belman.bootstrap.config.StorageTypeConfig;
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
        try {
            logger.info("Starting BelSign Photo Documentation application");

            // Log Java version and environment
            logger.debug("Java version: " + System.getProperty("java.version"));
            logger.debug("JavaFX version: " + System.getProperty("javafx.version"));
            logger.debug("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));

            // Initialize storage type configuration
            logger.debug("Initializing storage type configuration...");
            StorageTypeConfig.initialize();
            logger.debug("Storage type configuration initialized successfully");

            // Launch JavaFX application
            logger.debug("Launching JavaFX application...");
            Application.launch(args);
        } catch (Exception e) {
            logger.error("Error in main method", e);
            throw new RuntimeException("Failed to start application", e);
        }
    }

    @Override
    public void init() throws Exception {
        try {
            logger.debug("Starting JavaFX init() method");
            super.init();

            // Initialize application
            logger.debug("Initializing Application");
        } catch (Exception e) {
            logger.error("Error initializing Application", e);
            throw new RuntimeException("Error initializing Application", e);
        }

        // Initialize Gluon internal classes fixes (DAL)
        try {
            logger.debug("Initializing Gluon internal classes fixes");
            GluonInternalClassesFix.initialize();
            logger.debug("Gluon internal classes fixes initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing Gluon internal classes fixes", e);
            // Continue despite error, as this might not be critical
        }

        // Bootstrap the application (DAL)
        try {
            logger.startup("Bootstrapping the application");
            logger.debug("Current storage type: " + StorageTypeConfig.getStorageType());
            ApplicationBootstrapper.initialize();
            logger.debug("Application bootstrapping completed successfully");
        } catch (Exception e) {
            logger.error("Error bootstrapping application", e);
            throw new RuntimeException("Error bootstrapping application", e);
        }

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
            // Set stage style to TRANSPARENT for borderless window
            primaryStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

            // Create the scene with the root pane - using larger dimensions for tablet-like appearance
            Scene scene = new Scene(rootPane, 1024, 768);
            primaryStage.setScene(scene);
            primaryStage.setTitle("BelSign Photo Documentation");

            // Make the scene background transparent to achieve borderless effect
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

            // Initialize ViewStackManager with the root pane
            ViewStackManager.initWithRootPane(rootPane);

            // Apply platform-specific styling (GUI)
            logger.debug("Applying platform-specific styling");
            applyPlatformStyling(scene);

            // Load CSS (GUI)
            loadCss(scene);

            // Center the stage on screen
            primaryStage.centerOnScreen();

            // Set stage to maximized for tablet-like fullscreen experience
            primaryStage.setMaximized(true);

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
                // Apply tablet styling for desktop to make it tablet-like
                logger.debug("Applying tablet styling for desktop");
                scene.getRoot().getStyleClass().add("tablet");
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
        // Load modular CSS files following industry standards for CSS organization
        // This follows the recommended structure: base, components, layouts, views, utilities

        // Load base.css (core styles and variables)
        var baseCss = getClass().getResource("/com/belman/assets/styles/base.css");
        logger.debug("Loading base.css from: " + baseCss);
        if (baseCss != null) {
            scene.getStylesheets().add(baseCss.toExternalForm());
            logger.info("✅ Loaded base.css from: " + baseCss);
        } else {
            logger.error("❌ Could not find base.css at /com/belman/styles/base.css");
            throw new RuntimeException("Required CSS file base.css not found");
        }

        // Load components.css (reusable UI component styles)
        var componentsCss = getClass().getResource("/com/belman/assets/styles/components.css");
        logger.debug("Loading components.css from: " + componentsCss);
        if (componentsCss != null) {
            scene.getStylesheets().add(componentsCss.toExternalForm());
            logger.info("✅ Loaded components.css from: " + componentsCss);
        } else {
            logger.error("❌ Could not find components.css at /com/belman/styles/components.css");
            throw new RuntimeException("Required CSS file components.css not found");
        }

        // Load layouts.css (layout patterns and containers)
        var layoutsCss = getClass().getResource("/com/belman/assets/styles/layouts.css");
        logger.debug("Loading layouts.css from: " + layoutsCss);
        if (layoutsCss != null) {
            scene.getStylesheets().add(layoutsCss.toExternalForm());
            logger.info("✅ Loaded layouts.css from: " + layoutsCss);
        } else {
            logger.error("❌ Could not find layouts.css at /com/belman/styles/layouts.css");
            throw new RuntimeException("Required CSS file layouts.css not found");
        }

        // Load views.css (view-specific styles)
        var viewsCss = getClass().getResource("/com/belman/assets/styles/views.css");
        logger.debug("Loading views.css from: " + viewsCss);
        if (viewsCss != null) {
            scene.getStylesheets().add(viewsCss.toExternalForm());
            logger.info("✅ Loaded views.css from: " + viewsCss);
        } else {
            logger.error("❌ Could not find views.css at /com/belman/styles/views.css");
            throw new RuntimeException("Required CSS file views.css not found");
        }

        // Load utilities.css (helper classes)
        var utilitiesCss = getClass().getResource("/com/belman/assets/styles/utilities.css");
        logger.debug("Loading utilities.css from: " + utilitiesCss);
        if (utilitiesCss != null) {
            scene.getStylesheets().add(utilitiesCss.toExternalForm());
            logger.info("✅ Loaded utilities.css from: " + utilitiesCss);
        } else {
            logger.error("❌ Could not find utilities.css at /com/belman/styles/utilities.css");
            throw new RuntimeException("Required CSS file utilities.css not found");
        }

        // PhotoCubeView styles have been integrated into views.css

        // All required modular CSS files have been loaded successfully
        logger.info("✅ All modular CSS files loaded successfully");
    }
}
