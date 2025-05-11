package com.belman.ui.views.login;

import com.belman.ui.base.BaseController;
import com.belman.ui.views.login.flow.DefaultLoginContext;
import com.belman.ui.views.login.flow.PinLoginState;
import com.belman.ui.views.login.flow.CameraScanLoginState;
import com.belman.ui.views.login.flow.StartLoginState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 * Controller for the login view.
 */
public class LoginViewController extends BaseController<LoginViewModel> {
    // Authentication method selection buttons
    @FXML
    private Button scanButton;

    @FXML
    private Button pinButton;

    // Username/password form
    @FXML
    private VBox usernamePasswordForm;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheckBox;

    // PIN code form
    @FXML
    private VBox pinCodeForm;

    @FXML
    private TextField pinCodeField;

    // Camera scan form
    @FXML
    private VBox cameraScanForm;

    @FXML
    private Button startScanButton;

    // Common controls
    @FXML
    private Button loginButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private ProgressIndicator loginProgressIndicator;

    @Override
    protected void setupBindings() {
        // Bind UI components to ViewModel properties
        if (getViewModel() == null) {
            System.err.println("Error in LoginViewController.setupBindings(): ViewModel is null");
            // Create a temporary view model if it's null - this is just a safety measure
            LoginViewModel tempViewModel = new LoginViewModel();
            setViewModel(tempViewModel);
            System.out.println("Created temporary LoginViewModel to avoid NullPointerException");
        }

        // Bind username/password form fields
        usernameField.textProperty().bindBidirectional(getViewModel().usernameProperty());
        passwordField.textProperty().bindBidirectional(getViewModel().passwordProperty());
        errorMessageLabel.textProperty().bind(getViewModel().errorMessageProperty());
        loginProgressIndicator.visibleProperty().bind(getViewModel().loginInProgressProperty());
        loginButton.disableProperty().bind(getViewModel().loginInProgressProperty());
        rememberMeCheckBox.selectedProperty().bindBidirectional(getViewModel().rememberMeProperty());

        // Set up event handlers for common buttons
        loginButton.setOnAction(this::handleLoginButtonAction);
        cancelButton.setOnAction(this::handleCancelButtonAction);

        // Set up key event handlers for Enter key
        usernameField.setOnAction(this::handleLoginButtonAction);
        passwordField.setOnAction(this::handleLoginButtonAction);
        pinCodeField.setOnAction(this::handleLoginButtonAction);

        // Set up event handlers for authentication method selection
        scanButton.setOnAction(this::handleScanButtonAction);
        pinButton.setOnAction(this::handlePinButtonAction);
        startScanButton.setOnAction(this::handleStartScanButtonAction);

        // Initially show username/password form
        showUsernamePasswordForm();
    }

    @Override
    public void initializeBinding() {
        // Call setupBindings to avoid duplication
        setupBindings();
    }

    /**
     * Handles the login button action.
     *
     * @param event the action event
     */
    private void handleLoginButtonAction(ActionEvent event) {
        // Determine which form is visible and call the appropriate login method
        if (pinCodeForm.isVisible()) {
            handlePinCodeLogin();
        } else if (cameraScanForm.isVisible()) {
            // Camera scan is handled by the start scan button
            // This is just a fallback
            handleStartScanButtonAction(event);
        } else {
            // Default to username/password login using the state pattern
            try {
                // Create a login context with the view model
                DefaultLoginContext context = new DefaultLoginContext(getViewModel());

                // Set the initial state to StartLoginState (default)
                context.setState(new StartLoginState());

                // Handle the login flow
                context.handle();
            } catch (Exception e) {
                getViewModel().setErrorMessage("Login failed: " + e.getMessage());
            }
        }
    }

    /**
     * Handles the cancel button action.
     *
     * @param event the action event
     */
    private void handleCancelButtonAction(ActionEvent event) {
        getViewModel().cancel();
    }

    /**
     * Handles the scan button action.
     *
     * @param event the action event
     */
    private void handleScanButtonAction(ActionEvent event) {
        showCameraScanForm();
    }

    /**
     * Handles the PIN button action.
     *
     * @param event the action event
     */
    private void handlePinButtonAction(ActionEvent event) {
        showPinCodeForm();
    }

    /**
     * Handles the start scan button action.
     *
     * @param event the action event
     */
    private void handleStartScanButtonAction(ActionEvent event) {
        // This would normally start the camera and scan for a barcode/QR code
        // For now, we'll just simulate a successful scan after a delay
        loginProgressIndicator.setVisible(true);

        // In a real implementation, this would use the CameraService to take a photo
        // and then process it to extract the barcode/QR code

        // For demonstration purposes, we'll just set a username and password
        // that would normally be extracted from the barcode/QR code
        getViewModel().setUsername("scanner");
        getViewModel().setPassword("scanner123");

        // Use the state pattern to handle the login flow
        try {
            // Create a login context with the view model
            DefaultLoginContext context = new DefaultLoginContext(getViewModel());

            // Set the initial state to CameraScanLoginState
            context.setState(new CameraScanLoginState());

            // Handle the login flow
            context.handle();
        } catch (Exception e) {
            getViewModel().setErrorMessage("Login failed: " + e.getMessage());
        }
    }

    /**
     * Shows the username/password form and hides the other forms.
     */
    private void showUsernamePasswordForm() {
        usernamePasswordForm.setVisible(true);
        usernamePasswordForm.setManaged(true);
        pinCodeForm.setVisible(false);
        pinCodeForm.setManaged(false);
        cameraScanForm.setVisible(false);
        cameraScanForm.setManaged(false);
    }

    /**
     * Shows the PIN code form and hides the other forms.
     */
    private void showPinCodeForm() {
        usernamePasswordForm.setVisible(false);
        usernamePasswordForm.setManaged(false);
        pinCodeForm.setVisible(true);
        pinCodeForm.setManaged(true);
        cameraScanForm.setVisible(false);
        cameraScanForm.setManaged(false);

        // Focus the PIN code field
        pinCodeField.requestFocus();
    }

    /**
     * Handles PIN code login.
     * This method converts the PIN code to a username and password
     * that can be used with the existing login method.
     */
    private void handlePinCodeLogin() {
        String pin = pinCodeField.getText();

        // Validate PIN code
        if (pin == null || pin.isBlank()) {
            getViewModel().setErrorMessage("PIN code is required");
            return;
        }

        if (!pin.matches("\\d+")) {
            getViewModel().setErrorMessage("PIN code must contain only digits");
            return;
        }

        // For demonstration purposes, we'll use a simple mapping:
        // PIN code "1234" maps to username "pin_user" and password "pin_pass"
        // In a real implementation, this would validate against a database or service
        if (pin.equals("1234")) {
            getViewModel().setUsername("pin_user");
            getViewModel().setPassword("pin_pass");

            // Use the state pattern to handle the login flow
            try {
                // Create a login context with the view model
                DefaultLoginContext context = new DefaultLoginContext(getViewModel());

                // Set the initial state to PinLoginState
                context.setState(new PinLoginState());

                // Handle the login flow
                context.handle();
            } catch (Exception e) {
                getViewModel().setErrorMessage("Login failed: " + e.getMessage());
            }
        } else {
            getViewModel().setErrorMessage("Invalid PIN code");
        }
    }

    /**
     * Shows the camera scan form and hides the other forms.
     */
    private void showCameraScanForm() {
        usernamePasswordForm.setVisible(false);
        usernamePasswordForm.setManaged(false);
        pinCodeForm.setVisible(false);
        pinCodeForm.setManaged(false);
        cameraScanForm.setVisible(true);
        cameraScanForm.setManaged(true);
    }
}
