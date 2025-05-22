package com.belman.presentation.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

/**
 * Controller for the ConfirmationDialog component.
 * This component provides a reusable confirmation dialog that can be included in any view.
 */
public class ConfirmationDialogController {

    @FXML
    private StackPane confirmationDialog;

    @FXML
    private Label titleLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    private final StringProperty title = new SimpleStringProperty("Confirmation");
    private final StringProperty message = new SimpleStringProperty("Are you sure you want to proceed?");
    private final StringProperty confirmButtonText = new SimpleStringProperty("Confirm");
    private final StringProperty cancelButtonText = new SimpleStringProperty("Cancel");

    private Consumer<Boolean> resultCallback;

    /**
     * Initializes the controller.
     */
    @FXML
    private void initialize() {
        // Bind the visibility of the confirmation dialog to the visible property
        confirmationDialog.visibleProperty().bind(visible);
        
        // Bind the text properties to the labels and buttons
        titleLabel.textProperty().bind(title);
        messageLabel.textProperty().bind(message);
        confirmButton.textProperty().bind(confirmButtonText);
        cancelButton.textProperty().bind(cancelButtonText);
    }

    /**
     * Shows the confirmation dialog with the default title and message.
     *
     * @param resultCallback the callback to be called when the user confirms or cancels
     */
    public void show(Consumer<Boolean> resultCallback) {
        this.resultCallback = resultCallback;
        visible.set(true);
    }

    /**
     * Shows the confirmation dialog with a custom message.
     *
     * @param message the message to display
     * @param resultCallback the callback to be called when the user confirms or cancels
     */
    public void show(String message, Consumer<Boolean> resultCallback) {
        this.message.set(message);
        show(resultCallback);
    }

    /**
     * Shows the confirmation dialog with a custom title and message.
     *
     * @param title the title to display
     * @param message the message to display
     * @param resultCallback the callback to be called when the user confirms or cancels
     */
    public void show(String title, String message, Consumer<Boolean> resultCallback) {
        this.title.set(title);
        this.message.set(message);
        show(resultCallback);
    }

    /**
     * Shows the confirmation dialog with custom title, message, and button texts.
     *
     * @param title the title to display
     * @param message the message to display
     * @param confirmText the text for the confirm button
     * @param cancelText the text for the cancel button
     * @param resultCallback the callback to be called when the user confirms or cancels
     */
    public void show(String title, String message, String confirmText, String cancelText, Consumer<Boolean> resultCallback) {
        this.title.set(title);
        this.message.set(message);
        this.confirmButtonText.set(confirmText);
        this.cancelButtonText.set(cancelText);
        show(resultCallback);
    }

    /**
     * Hides the confirmation dialog.
     */
    public void hide() {
        visible.set(false);
    }

    /**
     * Handles the confirm button click.
     *
     * @param event the action event
     */
    @FXML
    private void handleConfirm(ActionEvent event) {
        if (resultCallback != null) {
            resultCallback.accept(true);
        }
        hide();
    }

    /**
     * Handles the cancel button click.
     *
     * @param event the action event
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        if (resultCallback != null) {
            resultCallback.accept(false);
        }
        hide();
    }

    /**
     * Gets the confirmation dialog StackPane.
     *
     * @return the confirmation dialog StackPane
     */
    public StackPane getConfirmationDialog() {
        return confirmationDialog;
    }

    /**
     * Gets the visible property.
     *
     * @return the visible property
     */
    public BooleanProperty visibleProperty() {
        return visible;
    }

    /**
     * Gets the title property.
     *
     * @return the title property
     */
    public StringProperty titleProperty() {
        return title;
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
     * Gets the confirm button text property.
     *
     * @return the confirm button text property
     */
    public StringProperty confirmButtonTextProperty() {
        return confirmButtonText;
    }

    /**
     * Gets the cancel button text property.
     *
     * @return the cancel button text property
     */
    public StringProperty cancelButtonTextProperty() {
        return cancelButtonText;
    }
}