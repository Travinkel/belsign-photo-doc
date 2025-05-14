package com.belman.ui.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Window;

import java.util.Optional;

/**
 * Utility class for working with dialogs.
 */
public final class DialogUtils {
    /**
     * Private constructor to prevent instantiation.
     */
    private DialogUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Shows an information dialog.
     *
     * @param title   the dialog title
     * @param header  the dialog header
     * @param content the dialog content
     */
    public static void showInformation(String title, String header, String content) {
        showAlert(Alert.AlertType.INFORMATION, title, header, content);
    }

    /**
     * Shows an alert dialog.
     *
     * @param alertType the alert type
     * @param title     the dialog title
     * @param header    the dialog header
     * @param content   the dialog content
     */
    private static void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = createAlert(alertType, title, header, content);
        alert.showAndWait();
    }

    /**
     * Creates an alert dialog.
     *
     * @param alertType the alert type
     * @param title     the dialog title
     * @param header    the dialog header
     * @param content   the dialog content
     * @return the created alert
     */
    private static Alert createAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        configureDialog(alert, title, header, content);
        return alert;
    }

    /**
     * Configures a dialog.
     *
     * @param dialog  the dialog to configure
     * @param title   the dialog title
     * @param header  the dialog header
     * @param content the dialog content
     */
    private static void configureDialog(Dialog<?> dialog, String title, String header, String content) {
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        // Make the dialog resizable
        dialog.setResizable(true);

        // Center the dialog on the screen
        Window window = dialog.getDialogPane().getScene().getWindow();
        if (window != null) {
            window.centerOnScreen();
        }
    }

    /**
     * Shows a warning dialog.
     *
     * @param title   the dialog title
     * @param header  the dialog header
     * @param content the dialog content
     */
    public static void showWarning(String title, String header, String content) {
        showAlert(Alert.AlertType.WARNING, title, header, content);
    }

    /**
     * Shows an error dialog.
     *
     * @param title   the dialog title
     * @param header  the dialog header
     * @param content the dialog content
     */
    public static void showError(String title, String header, String content) {
        showAlert(Alert.AlertType.ERROR, title, header, content);
    }

    /**
     * Shows a confirmation dialog.
     *
     * @param title   the dialog title
     * @param header  the dialog header
     * @param content the dialog content
     * @return true if the user clicked OK, false otherwise
     */
    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = createAlert(Alert.AlertType.CONFIRMATION, title, header, content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Shows a text input dialog.
     *
     * @param title        the dialog title
     * @param header       the dialog header
     * @param content      the dialog content
     * @param defaultValue the default value
     * @return the entered text, or an empty Optional if the dialog was cancelled
     */
    public static Optional<String> showTextInput(String title, String header, String content, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        configureDialog(dialog, title, header, content);
        return dialog.showAndWait();
    }
}