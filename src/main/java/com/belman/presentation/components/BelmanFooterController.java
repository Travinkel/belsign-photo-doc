package com.belman.presentation.components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the BelmanFooter component.
 * This component provides a reusable footer with Belman branding and customizable status labels.
 */
public class BelmanFooterController {

    @FXML
    private HBox statusContainer;

    private final Map<String, Label> statusLabels = new HashMap<>();

    /**
     * Initializes the controller.
     */
    @FXML
    private void initialize() {
        // No initialization needed yet
    }

    /**
     * Adds a status label to the footer.
     *
     * @param key the key to identify the label
     * @param text the initial text for the label
     * @return the created label
     */
    public Label addStatusLabel(String key, String text) {
        Label label = new Label(text);
        label.getStyleClass().add("footer-status-label");
        
        statusContainer.getChildren().add(label);
        statusLabels.put(key, label);
        
        return label;
    }

    /**
     * Updates the text of a status label.
     *
     * @param key the key of the label to update
     * @param text the new text for the label
     * @return true if the label was found and updated, false otherwise
     */
    public boolean updateStatusLabel(String key, String text) {
        Label label = statusLabels.get(key);
        if (label != null) {
            label.setText(text);
            return true;
        }
        return false;
    }

    /**
     * Removes a status label from the footer.
     *
     * @param key the key of the label to remove
     * @return true if the label was found and removed, false otherwise
     */
    public boolean removeStatusLabel(String key) {
        Label label = statusLabels.get(key);
        if (label != null) {
            statusContainer.getChildren().remove(label);
            statusLabels.remove(key);
            return true;
        }
        return false;
    }

    /**
     * Clears all status labels from the footer.
     */
    public void clearStatusLabels() {
        statusContainer.getChildren().clear();
        statusLabels.clear();
    }

    /**
     * Gets the status container.
     *
     * @return the status container
     */
    public HBox getStatusContainer() {
        return statusContainer;
    }
}