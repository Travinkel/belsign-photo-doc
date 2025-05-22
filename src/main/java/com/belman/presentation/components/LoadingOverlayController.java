package com.belman.presentation.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

/**
 * Controller for the LoadingOverlay component.
 * This component provides a reusable loading overlay that can be included in any view.
 */
public class LoadingOverlayController {

    @FXML
    private StackPane loadingOverlay;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label messageLabel;

    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final StringProperty message = new SimpleStringProperty("Loading...");

    /**
     * Initializes the controller.
     */
    @FXML
    private void initialize() {
        // Bind the visibility of the loading overlay to the loading property
        loadingOverlay.visibleProperty().bind(loading);
        
        // Bind the text of the message label to the message property
        messageLabel.textProperty().bind(message);
    }

    /**
     * Shows the loading overlay with the default message.
     */
    public void show() {
        loading.set(true);
    }

    /**
     * Shows the loading overlay with a custom message.
     *
     * @param message the message to display
     */
    public void show(String message) {
        this.message.set(message);
        loading.set(true);
    }

    /**
     * Hides the loading overlay.
     */
    public void hide() {
        loading.set(false);
    }

    /**
     * Gets the loading property.
     *
     * @return the loading property
     */
    public BooleanProperty loadingProperty() {
        return loading;
    }

    /**
     * Gets the message property.
     *
     * @return the message property
     */
    public StringProperty messageProperty() {
        return message;
    }

    /**
     * Sets the progress value of the progress bar.
     * A value of -1.0 indicates an indeterminate progress bar.
     *
     * @param progress the progress value (between 0.0 and 1.0, or -1.0 for indeterminate)
     */
    public void setProgress(double progress) {
        progressBar.setProgress(progress);
    }

    /**
     * Gets the loading overlay StackPane.
     *
     * @return the loading overlay StackPane
     */
    public StackPane getLoadingOverlay() {
        return loadingOverlay;
    }
}