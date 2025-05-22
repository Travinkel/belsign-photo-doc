package com.belman.presentation.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Utility class for loading and using reusable UI components.
 */
public class UIComponentUtils {

    private static final String LOADING_OVERLAY_PATH = "/com/belman/presentation/components/LoadingOverlay.fxml";
    private static final String CONFIRMATION_DIALOG_PATH = "/com/belman/presentation/components/ConfirmationDialog.fxml";
    private static final String BELMAN_FOOTER_PATH = "/com/belman/presentation/components/BelmanFooter.fxml";

    /**
     * Adds a loading overlay to the specified parent pane.
     *
     * @param parent the parent pane to add the loading overlay to
     * @return the loading overlay controller
     * @throws IOException if the loading overlay FXML file cannot be loaded
     */
    public static LoadingOverlayController addLoadingOverlay(Pane parent) throws IOException {
        FXMLLoader loader = new FXMLLoader(UIComponentUtils.class.getResource(LOADING_OVERLAY_PATH));
        Node loadingOverlay = loader.load();
        LoadingOverlayController controller = loader.getController();

        // Add the loading overlay to the parent pane
        parent.getChildren().add(loadingOverlay);

        // Ensure the loading overlay is on top
        loadingOverlay.toFront();

        return controller;
    }

    /**
     * Adds a confirmation dialog to the specified parent pane.
     *
     * @param parent the parent pane to add the confirmation dialog to
     * @return the confirmation dialog controller
     * @throws IOException if the confirmation dialog FXML file cannot be loaded
     */
    public static ConfirmationDialogController addConfirmationDialog(Pane parent) throws IOException {
        FXMLLoader loader = new FXMLLoader(UIComponentUtils.class.getResource(CONFIRMATION_DIALOG_PATH));
        Node confirmationDialog = loader.load();
        ConfirmationDialogController controller = loader.getController();

        // Add the confirmation dialog to the parent pane
        parent.getChildren().add(confirmationDialog);

        // Ensure the confirmation dialog is on top
        confirmationDialog.toFront();

        return controller;
    }

    /**
     * Shows a confirmation dialog with the specified message and callback.
     *
     * @param parent the parent pane to add the confirmation dialog to
     * @param message the message to display
     * @param resultCallback the callback to be called when the user confirms or cancels
     * @throws IOException if the confirmation dialog FXML file cannot be loaded
     */
    public static void showConfirmation(Pane parent, String message, Consumer<Boolean> resultCallback) throws IOException {
        ConfirmationDialogController controller = addConfirmationDialog(parent);
        controller.show(message, resultCallback);
    }

    /**
     * Shows a confirmation dialog with the specified title, message, and callback.
     *
     * @param parent the parent pane to add the confirmation dialog to
     * @param title the title to display
     * @param message the message to display
     * @param resultCallback the callback to be called when the user confirms or cancels
     * @throws IOException if the confirmation dialog FXML file cannot be loaded
     */
    public static void showConfirmation(Pane parent, String title, String message, Consumer<Boolean> resultCallback) throws IOException {
        ConfirmationDialogController controller = addConfirmationDialog(parent);
        controller.show(title, message, resultCallback);
    }

    /**
     * Shows a confirmation dialog with the specified title, message, button texts, and callback.
     *
     * @param parent the parent pane to add the confirmation dialog to
     * @param title the title to display
     * @param message the message to display
     * @param confirmText the text for the confirm button
     * @param cancelText the text for the cancel button
     * @param resultCallback the callback to be called when the user confirms or cancels
     * @throws IOException if the confirmation dialog FXML file cannot be loaded
     */
    public static void showConfirmation(Pane parent, String title, String message, String confirmText, String cancelText, Consumer<Boolean> resultCallback) throws IOException {
        ConfirmationDialogController controller = addConfirmationDialog(parent);
        controller.show(title, message, confirmText, cancelText, resultCallback);
    }

    /**
     * Shows a loading overlay with the specified message.
     *
     * @param parent the parent pane to add the loading overlay to
     * @param message the message to display
     * @return the loading overlay controller
     * @throws IOException if the loading overlay FXML file cannot be loaded
     */
    public static LoadingOverlayController showLoading(Pane parent, String message) throws IOException {
        LoadingOverlayController controller = addLoadingOverlay(parent);
        controller.messageProperty().set(message);
        controller.show();
        return controller;
    }

    /**
     * Shows a loading overlay with the default message.
     *
     * @param parent the parent pane to add the loading overlay to
     * @return the loading overlay controller
     * @throws IOException if the loading overlay FXML file cannot be loaded
     */
    public static LoadingOverlayController showLoading(Pane parent) throws IOException {
        LoadingOverlayController controller = addLoadingOverlay(parent);
        controller.show();
        return controller;
    }

    /**
     * Adds a Belman footer to the specified BorderPane.
     *
     * @param borderPane the BorderPane to add the footer to
     * @return the footer controller
     * @throws IOException if the footer FXML file cannot be loaded
     */
    public static BelmanFooterController addBelmanFooter(BorderPane borderPane) throws IOException {
        FXMLLoader loader = new FXMLLoader(UIComponentUtils.class.getResource(BELMAN_FOOTER_PATH));
        Node footer = loader.load();
        BelmanFooterController controller = loader.getController();

        // Set the footer as the bottom of the BorderPane
        borderPane.setBottom(footer);

        return controller;
    }

    /**
     * Adds a Belman footer to the specified BorderPane and adds status labels.
     *
     * @param borderPane the BorderPane to add the footer to
     * @param statusLabels a map of status label keys and texts
     * @return the footer controller
     * @throws IOException if the footer FXML file cannot be loaded
     */
    public static BelmanFooterController addBelmanFooter(BorderPane borderPane, Map<String, String> statusLabels) throws IOException {
        BelmanFooterController controller = addBelmanFooter(borderPane);

        // Add status labels
        for (Map.Entry<String, String> entry : statusLabels.entrySet()) {
            controller.addStatusLabel(entry.getKey(), entry.getValue());
        }

        return controller;
    }
}
