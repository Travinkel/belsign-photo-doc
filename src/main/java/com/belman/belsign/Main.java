package com.belman.belsign;

import com.belman.belsign.framework.athomefx.navigation.Router;
import com.belman.belsign.presentation.views.splash.SplashView;
import com.gluonhq.attach.lifecycle.LifecycleService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main application class for BelSign.
 * Handles application initialization and platform detection.
 */
public class Main extends Application {

    private static final String APP_NAME = "BelSign";
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        // Initialize the Router with the primary stage
        Router.setPrimaryStage(primaryStage);

        // Set application title
        primaryStage.setTitle(APP_NAME);

        // Set default size for desktop
        if (!isRunningOnMobile()) {
            primaryStage.setWidth(DEFAULT_WIDTH);
            primaryStage.setHeight(DEFAULT_HEIGHT);
        }

        // Navigate to the splash screen
        Router.navigateTo(SplashView.class);

        // Apply platform-specific styling after the scene is created
        applyPlatformStyling();
    }

    /**
     * Detects if the application is running on a mobile device.
     * @return true if running on Android or iOS, false otherwise
     */
    private boolean isRunningOnMobile() {
        try {
            return Platform.isAndroid() || Platform.isIOS();
        } catch (Exception e) {
            // If Gluon Attach is not available, assume desktop
            return false;
        }
    }

    /**
     * Applies platform-specific styling to the application.
     * Adds CSS classes based on the detected platform.
     */
    private void applyPlatformStyling() {
        try {
            Scene scene = Router.getPrimaryStage().getScene();
            if (scene != null) {
                if (Platform.isAndroid() || Platform.isIOS()) {
                    if (Platform.isAndroid()) {
                        scene.getRoot().getStyleClass().add("smartphone");
                    } else {
                        // iOS is typically tablet
                        scene.getRoot().getStyleClass().add("tablet");
                    }
                } else {
                    // Desktop
                    scene.getRoot().getStyleClass().add("desktop");
                }
            }
        } catch (Exception e) {
            // If platform detection fails, default styling will be used
            System.err.println("Platform detection failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
