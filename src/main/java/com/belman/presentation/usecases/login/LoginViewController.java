package com.belman.presentation.usecases.login;

import com.belman.presentation.base.BaseController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for the login view.
 * Handles user interactions with the login screen.
 */
public class LoginViewController extends BaseController<LoginViewModel> {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button nfcButton;

    @FXML
    private Button devHelperButton;

    @FXML
    private Label errorLabel;

    @FXML
    private VBox loadingIndicator;

    @Override
    protected void setupBindings() {
        try {
            // Bind UI components to ViewModel properties
            if (usernameField != null) {
                usernameField.textProperty().bindBidirectional(getViewModel().usernameProperty());
            }

            if (passwordField != null) {
                passwordField.textProperty().bindBidirectional(getViewModel().passwordProperty());
            }

            if (errorLabel != null) {
                errorLabel.textProperty().bind(getViewModel().errorMessageProperty());
                errorLabel.visibleProperty().bind(getViewModel().errorMessageProperty().isNotEmpty());
            }

            if (loadingIndicator != null) {
                loadingIndicator.visibleProperty().bind(getViewModel().loadingProperty());
            }

            // Set up event handlers
            setupEventHandlers();
        } catch (Exception e) {
            handleInitializationError(e);
        }
    }

    /**
     * Sets up event handlers for UI components.
     */
    private void setupEventHandlers() {
        if (loginButton != null) {
            loginButton.setOnAction(event -> getViewModel().login());
        }

        if (nfcButton != null) {
            nfcButton.setOnAction(event -> simulateNfcScan());
        }

        // Set up development helper button
        if (devHelperButton != null) {
            devHelperButton.setOnAction(event -> showUserSelectionDialog());
            // Bind visibility to devMode property
            devHelperButton.visibleProperty().bind(getViewModel().devModeProperty());
        }

        // Add Enter key handler for password field
        if (passwordField != null) {
            passwordField.setOnAction(event -> getViewModel().login());
        }
    }

    /**
     * Simulates an NFC scan for testing purposes.
     * In a real application, this would use the NFC hardware.
     */
    private void simulateNfcScan() {
        // Simulate NFC scan with a test NFC ID
        getViewModel().loginWithNfc("nfc123456");
    }

    /**
     * Handles errors that occur during initialization.
     *
     * @param e the exception that occurred
     */
    private void handleInitializationError(Exception e) {
        System.err.println("Error initializing login screen: " + e.getMessage());
        e.printStackTrace();

        // Update UI to show error
        Platform.runLater(() -> {
            if (errorLabel != null) {
                errorLabel.setText("Error initializing login screen");
                errorLabel.setVisible(true);
            }
        });
    }

    /**
     * Sets the username and password fields for development purposes.
     * This method is used by the UserSelectionDialog to auto-fill credentials.
     *
     * @param username the username to set
     * @param password the password to set
     */
    public void setCredentials(String username, String password) {
        if (usernameField != null) {
            usernameField.setText(username);
        }

        if (passwordField != null) {
            passwordField.setText(password);
        }
    }

    /**
     * Shows the user selection dialog for development purposes.
     * This dialog allows developers to select a user to auto-fill the login credentials.
     */
    private void showUserSelectionDialog() {
        try {
            // Load the FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/belman/presentation/usecases/login/UserSelectionDialog.fxml"));
            Parent root = loader.load();

            // Get the controller and set this controller as the login view controller
            UserSelectionDialogController controller = loader.getController();
            controller.setLoginViewController(this);

            // Create and show the dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Available Users");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(usernameField.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.show();
        } catch (Exception e) {
            System.err.println("Error showing user selection dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void initializeBinding() {
        // Call setupBindings to avoid duplication
        setupBindings();
    }

    /**
     * Handles the close button action.
     * Closes the application when the close button is clicked.
     */
    @FXML
    private void handleCloseButtonAction() {
        // Get the stage from any UI component
        Stage stage = (Stage) usernameField.getScene().getWindow();
        // Close the stage
        stage.close();
    }
}
