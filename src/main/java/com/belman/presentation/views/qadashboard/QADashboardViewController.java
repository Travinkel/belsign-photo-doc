package com.belman.presentation.views.qadashboard;


import com.belman.backbone.core.base.BaseController;
import com.gluonhq.charm.glisten.control.ProgressBar;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Controller for the splash screen view.
 * Handles the animation and initialization of the splash screen.
 */
public class QADashboardViewController extends BaseController<QADashboardViewModel> {
    @FXML
    private Label titleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private ProgressBar loadingProgress;

    @FXML
    private ImageView logoImage;

    private Timeline loadingTimeline;

    @Override
    public void initializeBinding() {
        try {
            // Bind UI components to ViewModel properties
            messageLabel.textProperty().bind(getViewModel().messageProperty());

            // Adjust UI based on platform if needed
            adjustForPlatform();

            // Initialize loading animation
            initializeLoadingAnimation();
        } catch (Exception e) {
            handleInitializationError(e);
        }
    }

    /**
     * Initializes the loading animation with a timeline.
     */
    private void initializeLoadingAnimation() {
        // Make the logo smaller to signify loading
        logoImage.setFitWidth(150);

        // Create a flickering animation for the logo
        Timeline flickerTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(logoImage.opacityProperty(), 1.0)),
            new KeyFrame(Duration.seconds(0.7), new KeyValue(logoImage.opacityProperty(), 0.5)),
            new KeyFrame(Duration.seconds(1.4), new KeyValue(logoImage.opacityProperty(), 1.0))
        );
        flickerTimeline.setCycleCount(Timeline.INDEFINITE);
        flickerTimeline.play();

        // Simulate loading with a timeline animation
        loadingTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(loadingProgress.progressProperty(), 0)),
            new KeyFrame(Duration.seconds(2.5), new KeyValue(loadingProgress.progressProperty(), 1))
        );

        loadingTimeline.setOnFinished(event -> {
            try {
                // Stop the flickering animation
                flickerTimeline.stop();

                // Navigate to the main view after splash screen finishes
                getViewModel().onLoadingComplete();
            } catch (Exception e) {
                handleNavigationError(e);
            }
        });

        // Start the animation
        loadingTimeline.play();
    }

    /**
     * Adjusts UI elements based on the platform (desktop, tablet, smartphone).
     */
    private void adjustForPlatform() {
        try {
            // Check if running on mobile
            if (isRunningOnMobile()) {
                // Adjust UI for mobile if needed
                if (isRunningOnSmartphone()) {
                    // Smartphone-specific adjustments
                    logoImage.setFitWidth(200);
                } else {
                    // Tablet-specific adjustments
                    logoImage.setFitWidth(300);
                }
            }
        } catch (Exception e) {
            // If platform detection fails, use default desktop styling
            System.err.println("Platform detection failed: " + e.getMessage());
        }
    }

    /**
     * Detects if the application is running on a mobile device.
     * @return true if running on Android or iOS, false otherwise
     */
    private boolean isRunningOnMobile() {
        try {
            return com.gluonhq.attach.util.Platform.isAndroid() || 
                   com.gluonhq.attach.util.Platform.isIOS();
        } catch (Exception e) {
            // If Gluon Attach is not available, assume desktop
            return false;
        }
    }

    /**
     * Detects if the application is running on a smartphone (as opposed to a tablet).
     * @return true if running on a smartphone, false otherwise
     */
    private boolean isRunningOnSmartphone() {
        try {
            return com.gluonhq.attach.util.Platform.isAndroid();
        } catch (Exception e) {
            // If detection fails, assume not a smartphone
            return false;
        }
    }

    /**
     * Handles errors that occur during initialization.
     * @param e the exception that occurred
     */
    private void handleInitializationError(Exception e) {
        System.err.println("Error initializing splash screen: " + e.getMessage());
        e.printStackTrace();

        // Update UI to show error
        Platform.runLater(() -> {
            if (messageLabel != null) {
                messageLabel.textProperty().unbind();
                messageLabel.setText("Error initializing application");
            }
        });
    }

    /**
     * Handles errors that occur during navigation.
     * @param e the exception that occurred
     */
    private void handleNavigationError(Exception e) {
        System.err.println("Error navigating from splash screen: " + e.getMessage());
        e.printStackTrace();

        // Update UI to show error
        Platform.runLater(() -> {
            if (messageLabel != null) {
                messageLabel.textProperty().unbind();
                messageLabel.setText("Error loading application");
            }
        });
    }
}
