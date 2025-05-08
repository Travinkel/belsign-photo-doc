package com.belman.data.bootstrap;

import com.belman.business.core.LifecycleManager;
import com.belman.business.domain.security.AuthenticationService;
import com.belman.business.domain.services.LoggerFactory;
import com.belman.business.core.ServiceLocator;
import com.belman.business.core.ServiceRegistry;
import com.belman.business.domain.services.Logger;
import com.belman.data.platform.PlatformUtils;
import com.belman.data.logging.EmojiLogger;
import com.belman.data.logging.EmojiLoggerAdapter;
import com.belman.data.config.RouteGuardInitializer;
import com.belman.data.service.DisplayServiceFactory;
import com.belman.data.service.GluonInternalClassesFix;
import com.belman.data.service.StorageServiceFactory;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.navigation.RouteGuardImpl;
import com.belman.presentation.views.splash.SplashView;
import com.gluonhq.charm.glisten.application.MobileApplication;
import javafx.scene.Scene;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;

/**
 * Main application class for BelSign.
 * Handles application initialization and platform detection.
 * This class is used for desktop (JavaFX) platforms.
 * For mobile platforms, see GluonMain.
 */
public class Main extends MobileApplication {

    private static final EmojiLogger logger = EmojiLogger.getLogger(Main.class);

    /**
     * Main entry point for the application.
     * @param args command line arguments
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

    public static void main(String[] args) {
        System.setProperty("com.gluonhq.license.disable", "true");
        BCryptGenerator();
        launch(args);
    }

    public static final String SPLASH_VIEW = SplashView.class.getSimpleName();

    @Override
    public void init() {
        // Initialize Gluon internal classes fixes
        logger.debug("Initializing Gluon internal classes fixes");
        GluonInternalClassesFix.initialize();

        // Bootstrap the application
        logger.startup("Bootstrapping the application");
        ApplicationBootstrapper.initialize();

        // Initialize service fallbacks for desktop platforms
        if (!PlatformUtils.isRunningOnMobile()) {
            logger.file("Initializing desktop storage service fallback");
            StorageServiceFactory.getStorageService();

            logger.file("Initializing desktop display service fallback");
            DisplayServiceFactory.getDisplayService();

            // Note: We no longer need to use reflection hacks to fix Gluon licensing issues
            // Instead, we set the com.gluonhq.license.disable system property in the static initializer
            logger.info("Using com.gluonhq.license.disable=true to disable Gluon licensing checks");
        }


        // Register the splash view
        logger.debug("Registering splash view");
        addViewFactory(SPLASH_VIEW, SplashView::new);

        // Register the login view
        logger.debug("Registering login view");
        addViewFactory(com.belman.presentation.views.login.LoginView.class.getSimpleName(),
                com.belman.presentation.views.login.LoginView::new);

        // Main view has been removed as it doesn't offer value
        // Users are redirected to role-specific views after login

        // Register the admin view
        logger.debug("Registering admin view");
        addViewFactory(com.belman.presentation.views.admin.AdminView.class.getSimpleName(),
                com.belman.presentation.views.admin.AdminView::new);

        // Register the order gallery view
        logger.debug("Registering order gallery view");
        addViewFactory(com.belman.presentation.views.ordergallery.OrderGalleryView.class.getSimpleName(),
                com.belman.presentation.views.ordergallery.OrderGalleryView::new);

        // Register the photo review view
        logger.debug("Registering photo review view");
        addViewFactory(com.belman.presentation.views.photoreview.PhotoReviewView.class.getSimpleName(),
                com.belman.presentation.views.photoreview.PhotoReviewView::new);

        // Register the photo upload view
        logger.debug("Registering photo upload view");
        addViewFactory(com.belman.presentation.views.photoupload.PhotoUploadView.class.getSimpleName(),
                com.belman.presentation.views.photoupload.PhotoUploadView::new);

        // Register the QA dashboard view
        logger.debug("Registering QA dashboard view");
        addViewFactory(com.belman.presentation.views.qadashboard.QADashboardView.class.getSimpleName(),
                com.belman.presentation.views.qadashboard.QADashboardView::new);

        // Register the report preview view
        logger.debug("Registering report preview view");
        addViewFactory(com.belman.presentation.views.reportpreview.ReportPreviewView.class.getSimpleName(),
                com.belman.presentation.views.reportpreview.ReportPreviewView::new);

        // Register the user management view
        logger.debug("Registering user management view");
        addViewFactory(com.belman.presentation.views.usermanagement.UserManagementView.class.getSimpleName(),
                com.belman.presentation.views.usermanagement.UserManagementView::new);

        // Set up the Router
        logger.debug("Setting up Router");
        Router.setMobileApplication(this);

        // Initialize route guards for role-based access control
        logger.debug("Initialmvn izing route guards");
        AuthenticationService authService = ServiceLocator.getService(AuthenticationService.class);
        Logger domainLogger = EmojiLoggerAdapter.getLogger(Main.class);
        RouteGuardImpl routeGuard = new RouteGuardImpl(domainLogger);
        RouteGuardInitializer.initialize(authService, routeGuard);
        logger.success("Route guards initialized successfully");

        // Initialize the LifecycleManager and ServiceRegistry
        logger.debug("Initializing LifecycleManager and ServiceRegistry");
        LoggerFactory loggerFactory = ServiceLocator.getService(LoggerFactory.class);
        ServiceRegistry.setLogger(loggerFactory);
        LifecycleManager.init(this, loggerFactory);
    }

    @Override
    public void postInit(Scene scene) {
        // Apply platform-specific styling
        logger.debug("Applying platform-specific styling");
        applyPlatformStyling(scene);

        var css = getClass().getResource("/com/belman/styles/app.css");
        logger.debug("Loading app.css from: " + css);
        URL cssUrl = getClass().getResource("/com/belman/styles/app.css");
        logger.debug("cssUrl = " + cssUrl);
        logger.debug("Classpath root = " + getClass().getResource("/"));

        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
            logger.info("✅ Loaded app.css from: " + css);
        } else {
            logger.warn("⚠️ Could not find app.css at /com/belman/styles/app.css");
        }

        // Show the splash view
        logger.info("Showing splash view");
        switchView(SPLASH_VIEW);
    }

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
        // Shutdown the application
        logger.shutdown("Shutting down the application");
        ApplicationBootstrapper.shutdown();

        super.stop();
    }

    protected static void BCryptGenerator() {
        String hashed = BCrypt.hashpw("password123", BCrypt.gensalt());
        System.out.println(hashed);
    }
}
