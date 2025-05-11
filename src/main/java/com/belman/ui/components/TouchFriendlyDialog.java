package com.belman.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * A touch-friendly dialog component that provides a more mobile-friendly
 * alternative to the standard JavaFX Alert dialog.
 */
public class TouchFriendlyDialog {

    private final Stage dialogStage;
    private final Label titleLabel;
    private final Label messageLabel;
    private final Button primaryButton;
    private final Button secondaryButton;
    private final VBox contentBox;
    private final HBox buttonBox;
    private boolean result = false;

    /**
     * Creates a new touch-friendly dialog with the specified title, message, and type.
     *
     * @param title   the dialog title
     * @param message the dialog message
     * @param type    the dialog type (INFORMATION, ERROR, WARNING, CONFIRMATION)
     */
    public TouchFriendlyDialog(String title, String message, DialogType type) {
        // Create the dialog stage
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setResizable(false);

        // Create the title label
        titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setPadding(new Insets(15));

        // Set the title background color based on the dialog type
        String titleBgColor;
        switch (type) {
            case ERROR:
                titleBgColor = "#d32f2f"; // Red
                break;
            case WARNING:
                titleBgColor = "#ffa000"; // Amber
                break;
            case CONFIRMATION:
                titleBgColor = "#004b88"; // Blue
                break;
            case INFORMATION:
            default:
                titleBgColor = "#338d71"; // Green
                break;
        }
        titleLabel.setStyle("-fx-background-color: " + titleBgColor + "; -fx-text-fill: white;");

        // Create the message label
        messageLabel = new Label(message);
        messageLabel.setFont(Font.font("System", 16));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setPadding(new Insets(20));

        // Create the content box
        contentBox = new VBox();
        contentBox.getChildren().add(messageLabel);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-background-color: white;");

        // Create the buttons
        primaryButton = new Button("OK");
        primaryButton.setMinWidth(120);
        primaryButton.setMinHeight(48);
        primaryButton.setFont(Font.font("System", 16));
        primaryButton.setOnAction(e -> {
            result = true;
            dialogStage.close();
        });

        secondaryButton = new Button("Cancel");
        secondaryButton.setMinWidth(120);
        secondaryButton.setMinHeight(48);
        secondaryButton.setFont(Font.font("System", 16));
        secondaryButton.setOnAction(e -> {
            result = false;
            dialogStage.close();
        });

        // Create the button box
        buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(15));
        buttonBox.setStyle("-fx-background-color: #f2f2f2;");

        // Add buttons based on dialog type
        if (type == DialogType.CONFIRMATION) {
            buttonBox.getChildren().addAll(secondaryButton, primaryButton);
        } else {
            buttonBox.getChildren().add(primaryButton);
        }

        // Create the main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(titleLabel);
        mainLayout.setCenter(contentBox);
        mainLayout.setBottom(buttonBox);
        mainLayout.setMinWidth(350);
        mainLayout.setMinHeight(200);

        // Create the scene
        Scene scene = new Scene(mainLayout);
        scene.setFill(Color.TRANSPARENT);
        dialogStage.setScene(scene);
    }

    /**
     * Shows an information dialog with the specified title and message.
     *
     * @param title   the dialog title
     * @param message the dialog message
     */
    public static void showInformation(String title, String message) {
        TouchFriendlyDialog dialog = new TouchFriendlyDialog(title, message, DialogType.INFORMATION);
        dialog.showAndWait();
    }

    /**
     * Shows the dialog and waits for the user to close it.
     *
     * @return true if the primary button was clicked, false otherwise
     */
    public boolean showAndWait() {
        dialogStage.showAndWait();
        return result;
    }

    /**
     * Shows an error dialog with the specified title and message.
     *
     * @param title   the dialog title
     * @param message the dialog message
     */
    public static void showError(String title, String message) {
        TouchFriendlyDialog dialog = new TouchFriendlyDialog(title, message, DialogType.ERROR);
        dialog.showAndWait();
    }

    /**
     * Shows a warning dialog with the specified title and message.
     *
     * @param title   the dialog title
     * @param message the dialog message
     */
    public static void showWarning(String title, String message) {
        TouchFriendlyDialog dialog = new TouchFriendlyDialog(title, message, DialogType.WARNING);
        dialog.showAndWait();
    }

    /**
     * Shows a confirmation dialog with the specified title and message.
     *
     * @param title   the dialog title
     * @param message the dialog message
     * @return true if the user confirmed, false otherwise
     */
    public static boolean showConfirmation(String title, String message) {
        TouchFriendlyDialog dialog = new TouchFriendlyDialog(title, message, DialogType.CONFIRMATION);
        return dialog.showAndWait();
    }

    public enum DialogType {
        INFORMATION, ERROR, WARNING, CONFIRMATION
    }
}