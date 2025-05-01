package com.belman;

import com.belman.backbone.core.util.PlatformUtils;
import com.belman.presentation.views.splash.SplashView;
import com.gluonhq.charm.glisten.application.MobileApplication;
import javafx.scene.Scene;

/**
 * Main application class for BelSign.
 * Handles application initialization and platform detection.
 * This class is used for desktop (JavaFX) platforms.
 * For mobile platforms, see GluonMain.
 */
public class Main extends MobileApplication {

    public static final String SPLASH_VIEW = SplashView.class.getSimpleName();

    @Override
    public void init() {
        // Register the splash view
        addViewFactory(SPLASH_VIEW, SplashView::new);
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
            System.err.println("Platform detection failed: " + e.getMessage());
        }
    }
}
