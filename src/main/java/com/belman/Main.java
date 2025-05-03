package com.belman;

import com.belman.backbone.core.util.PlatformUtils;
import com.belman.backbone.core.logging.EmojiLogger;
import com.belman.backbone.core.navigation.Router;
import com.belman.infrastructure.config.ApplicationInitializer;
import com.belman.infrastructure.service.DisplayServiceFactory;
import com.belman.infrastructure.service.GluonInternalClassesFix;
import com.belman.infrastructure.service.StorageServiceFactory;
import com.belman.presentation.views.splash.SplashView;
import com.gluonhq.charm.glisten.application.MobileApplication;
import javafx.scene.Scene;

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
        launch(args);
    }

    public static final String SPLASH_VIEW = SplashView.class.getSimpleName();

    @Override
    public void init() {
        // Initialize Gluon internal classes fixes
        logger.debug("Initializing Gluon internal classes fixes");
        GluonInternalClassesFix.initialize();

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

        // Initialize application services and repositories
        logger.startup("Initializing application services and repositories");
        ApplicationInitializer.initialize();

        // Register the splash view
        logger.debug("Registering splash view");
        addViewFactory(SPLASH_VIEW, SplashView::new);

        // Register the login view
        logger.debug("Registering login view");
        addViewFactory(com.belman.presentation.views.login.LoginView.class.getSimpleName(), 
                       com.belman.presentation.views.login.LoginView::new);

        // Register the main view
        logger.debug("Registering main view");
        addViewFactory(com.belman.presentation.views.main.MainView.class.getSimpleName(), 
                       com.belman.presentation.views.main.MainView::new);

        // Set up the Router
        logger.debug("Setting up Router");
        Router.setMobileApplication(this);

        // Initialize the GluonLifecycleManager
        logger.debug("Initializing GluonLifecycleManager");
        com.belman.backbone.core.lifecycle.GluonLifecycleManager.init(this);
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
                if (PlatformUtils.isRunningOnMobile()) {
                    if (PlatformUtils.isAndroid()) {
                        logger.debug("Applying smartphone styling");
                        scene.getRoot().getStyleClass().add("smartphone");
                    } else {
                        logger.debug("Applying tablet styling");
                        scene.getRoot().getStyleClass().add("tablet");
                    }
                } else {
                    logger.debug("Applying desktop styling");
                    scene.getRoot().getStyleClass().add("desktop");
                }
            }
        } catch (Exception e) {
            logger.error("Platform detection failed: {}", e.getMessage(), e);
        }
    }

    @Override
    public void stop() throws Exception {
        // Shutdown application services and resources
        logger.shutdown("Shutting down application services and resources");
        ApplicationInitializer.shutdown();

        super.stop();
    }
}
