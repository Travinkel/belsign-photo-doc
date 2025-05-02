package com.belman;

import com.belman.backbone.core.util.PlatformUtils;
import com.belman.backbone.core.navigation.Router;
import com.belman.infrastructure.config.ApplicationInitializer;
import com.belman.presentation.views.splash.SplashView;
import com.gluonhq.charm.glisten.application.MobileApplication;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for BelSign.
 * Handles application initialization and platform detection.
 * This class is used for desktop (JavaFX) platforms.
 * For mobile platforms, see GluonMain.
 */
public class Main extends MobileApplication {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * Main entry point for the application.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static final String SPLASH_VIEW = SplashView.class.getSimpleName();

    @Override
    public void init() {
        // Initialize application services and repositories
        ApplicationInitializer.initialize();

        // Register the splash view
        addViewFactory(SPLASH_VIEW, SplashView::new);

        // Set up the Router
        Router.setMobileApplication(this);

        // Initialize the GluonLifecycleManager
        com.belman.backbone.core.lifecycle.GluonLifecycleManager.init(this);
    }

    @Override
    public void postInit(Scene scene) {
        // Apply platform-specific styling
        applyPlatformStyling(scene);

        // Show the splash view
        switchView(SPLASH_VIEW);
    }

    private void applyPlatformStyling(Scene scene) {
        try {
            if (scene != null) {
                if (PlatformUtils.isRunningOnMobile()) {
                    if (PlatformUtils.isAndroid()) {
                        scene.getRoot().getStyleClass().add("smartphone");
                    } else {
                        scene.getRoot().getStyleClass().add("tablet");
                    }
                } else {
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
        ApplicationInitializer.shutdown();

        super.stop();
    }
}
