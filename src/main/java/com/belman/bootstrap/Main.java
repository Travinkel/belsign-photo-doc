package com.belman.bootstrap;

import com.belman.bootstrap.config.ApplicationBootstrapper;
import com.belman.bootstrap.di.ServiceLocator;
import com.belman.bootstrap.di.ServiceRegistry;
import com.belman.bootstrap.hacks.GluonInternalClassesFix;
import com.belman.bootstrap.lifecycle.LifecycleManager;
import com.belman.bootstrap.platform.DisplayServiceFactory;
import com.belman.bootstrap.platform.StorageServiceFactory;
import com.belman.bootstrap.security.RouteGuardInitializer;
import com.belman.common.logging.EmojiLogger;
import com.belman.common.platform.PlatformUtils;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.repository.logging.EmojiLoggerAdapter;
import com.belman.ui.core.GluonFacade;
import com.belman.ui.navigation.RouteGuardImpl;
import com.belman.ui.navigation.Router;
import com.belman.ui.views.splash.SplashView;
import javafx.application.Application;
import javafx.scene.Scene;
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

    /**
     * Static initializer to set system properties.
     */
    static {
        // Set system properties for Gluon
        // This disables licensing and tracking features in Glisten safely
        // It's officially supported for students and works fine with native-image too
        System.setProperty("com.gluonhq.license.disable", "true");

        // Set platform to Desktop for consistent behavior
        if (System.getProperty("javafx.platform") == null) {
            System.setProperty("javafx.platform", "Desktop");
        }
    }

    private final GluonFacade app = GluonFacade.initialize();

    /**
     * Main entry point for the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("com.gluonhq.license.disable", "true");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        init();
        app.start(primaryStage);
        postInit(app.getScene());
    }

    @Override
    public void init() {
        // Initialize Gluon internal classes fixes (DAL)
        logger.debug("Initializing Gluon internal classes fixes");
        GluonInternalClassesFix.initialize();

        // Bootstrap the application (DAL)
        logger.startup("Bootstrapping the application");
        ApplicationBootstrapper.initialize();

        // Initialize service fallbacks for desktop platforms (DAL)
        if (!PlatformUtils.isRunningOnMobile()) {
            logger.file("Initializing desktop storage service fallback");
            StorageServiceFactory.getStorageService();

            logger.file("Initializing desktop display service fallback");
            DisplayServiceFactory.getDisplayService();

            logger.info("Using com.gluonhq.license.disable=true to disable Gluon licensing checks");
        }

        // Register views (GUI)
        registerViews();

        // Set up the Router (GUI)
        logger.debug("Setting up Router");
        Router.setMobileApplication(app);

        // Initialize route guards for role-based access control (BLL + GUI)
        initializeRouteGuards();

        // Initialize the LifecycleManager and ServiceRegistry (BLL)
        logger.debug("Initializing LifecycleManager and ServiceRegistry");
        LoggerFactory loggerFactory = ServiceLocator.getService(LoggerFactory.class);
        ServiceRegistry.setLogger(loggerFactory);
        LifecycleManager.init(this, loggerFactory);
    }

    /**
     * Registers all views with the application.
     * This is part of the GUI layer bootstrapping.
     */
    private void registerViews() {
        // Register the splash view
        logger.debug("Registering splash view");
        app.addViewFactory(SPLASH_VIEW, SplashView::new);

        // Register the login view
        logger.debug("Registering login view");
        app.addViewFactory(com.belman.ui.views.login.LoginView.class.getSimpleName(),
                com.belman.ui.views.login.LoginView::new);

        // Register the admin view
        logger.debug("Registering admin view");
        app.addViewFactory(com.belman.ui.views.admin.AdminView.class.getSimpleName(),
                com.belman.ui.views.admin.AdminView::new);

        // Register the order gallery view
        logger.debug("Registering order gallery view");
        app.addViewFactory(com.belman.ui.views.ordergallery.OrderGalleryView.class.getSimpleName(),
                com.belman.ui.views.ordergallery.OrderGalleryView::new);

        // Register the photo review view
        logger.debug("Registering photo review view");
        app.addViewFactory(com.belman.ui.views.photoreview.PhotoReviewView.class.getSimpleName(),
                com.belman.ui.views.photoreview.PhotoReviewView::new);

        // Register the photo upload view
        logger.debug("Registering photo upload view");
        app.addViewFactory(com.belman.ui.views.photoupload.PhotoUploadView.class.getSimpleName(),
                com.belman.ui.views.photoupload.PhotoUploadView::new);

        // Register the QA dashboard view
        logger.debug("Registering QA dashboard view");
        app.addViewFactory(com.belman.ui.views.qadashboard.QADashboardView.class.getSimpleName(),
                com.belman.ui.views.qadashboard.QADashboardView::new);

        // Register the report preview view
        logger.debug("Registering report preview view");
        app.addViewFactory(com.belman.ui.views.reportpreview.ReportPreviewView.class.getSimpleName(),
                com.belman.ui.views.reportpreview.ReportPreviewView::new);

        // Register the user management view
        logger.debug("Registering user management view");
        app.addViewFactory(com.belman.ui.views.usermanagement.UserManagementView.class.getSimpleName(),
                com.belman.ui.views.usermanagement.UserManagementView::new);
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

    /**
     * Performs post-initialization tasks.
     *
     * @param scene the JavaFX scene
     */
    public void postInit(Scene scene) {
        // Apply platform-specific styling (GUI)
        logger.debug("Applying platform-specific styling");
        applyPlatformStyling(scene);

        // Load CSS (GUI)
        loadCss(scene);

        // Show the splash view (GUI)
        logger.info("Showing splash view");
        app.switchView(SPLASH_VIEW);
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

    @Override
    public void stop() throws Exception {
        // Shutdown the application (DAL)
        logger.shutdown("Shutting down the application");
        ApplicationBootstrapper.shutdown();

        super.stop();
    }
}