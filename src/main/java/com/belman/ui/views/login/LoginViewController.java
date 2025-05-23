package com.belman.ui.views.login;

import com.belman.ui.base.BaseController;
import com.belman.ui.views.login.flow.DefaultLoginContext;
import com.belman.ui.views.login.flow.PinLoginState;
import com.belman.ui.views.login.flow.CameraScanLoginState;
import com.belman.ui.views.login.flow.StartLoginState;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Random;

/**
 * Controller for the login view.
 */
public class LoginViewController extends BaseController<LoginViewModel> {
    // Authentication method selection buttons
    @FXML
    private Button scanButton;

    @FXML
    private Button pinButton;

    // Note: Username/password form has been removed from the FXML

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

    @FXML
    private Button mockScanSuccessButton;

    @FXML
    private StackPane cameraPreviewContainer;

    @FXML
    private ImageView cameraPreviewImage;

    // Common controls
    @FXML
    private Button loginButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private ProgressIndicator loginProgressIndicator;

    // Timeline for simulating camera preview
    private Timeline cameraPreviewTimeline;

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

        // Bind UI components to ViewModel properties
        errorMessageLabel.textProperty().bind(getViewModel().errorMessageProperty());
        loginProgressIndicator.visibleProperty().bind(getViewModel().loginInProgressProperty());
        loginButton.disableProperty().bind(getViewModel().loginInProgressProperty());

        // Add a listener to the error message property to show/hide the error message label
        getViewModel().errorMessageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                errorMessageLabel.setVisible(true);
            } else {
                errorMessageLabel.setVisible(false);
            }
        });

        // Set up event handlers for common buttons
        loginButton.setOnAction(this::handleLoginButtonAction);
        cancelButton.setOnAction(this::handleCancelButtonAction);

        // Set up key event handlers for Enter key
        pinCodeField.setOnAction(this::handleLoginButtonAction);

        // Set up event handlers for authentication method selection
        scanButton.setOnAction(this::handleScanButtonAction);
        pinButton.setOnAction(this::handlePinButtonAction);
        startScanButton.setOnAction(this::handleStartScanButtonAction);

        // Hide error message label initially
        errorMessageLabel.setVisible(false);
    }

    @Override
    public void initializeBinding() {
        // Call setupBindings to avoid duplication
        setupBindings();
    }

    @Override
    public void onHide() {
        super.onHide();

        // Stop the camera preview timeline when the view is hidden
        if (cameraPreviewTimeline != null) {
            cameraPreviewTimeline.stop();
        }
    }

    /**
     * Handles the login button action.
     *
     * @param event the action event
     */
    private void handleLoginButtonAction(ActionEvent event) {
        // Clear any previous error message
        errorMessageLabel.setVisible(false);

        // Determine which form is visible and call the appropriate login method
        if (pinCodeForm.isVisible()) {
            handlePinCodeLogin();
        } else if (cameraScanForm.isVisible()) {
            // Camera scan is handled by the start scan button
            // This is just a fallback
            handleStartScanButtonAction(event);
        } else {
            // If no form is visible, prompt the user to select an authentication method
            getViewModel().setErrorMessage("Please select an authentication method: Scan Keychain or Use PIN Code");
            errorMessageLabel.setVisible(true);
        }
    }

    /**
     * Handles the cancel button action.
     *
     * @param event the action event
     */
    private void handleCancelButtonAction(ActionEvent event) {
        // Clear any error message
        getViewModel().setErrorMessage("");
        errorMessageLabel.setVisible(false);

        // Call the view model's cancel method
        getViewModel().cancel();

        // Reset the UI to the default state
        showUsernamePasswordForm();
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
     * Handles a PIN button press (0-9).
     * Appends the digit to the PIN code field.
     *
     * @param event the action event
     */
    @FXML
    private void handlePinButtonPressed(ActionEvent event) {
        Button button = (Button) event.getSource();
        String digit = button.getText();

        // Append the digit to the PIN code field
        String currentPin = pinCodeField.getText();
        pinCodeField.setText(currentPin + digit);

        // Clear any error message
        errorMessageLabel.setVisible(false);
    }

    /**
     * Handles the PIN clear button press.
     * Clears the PIN code field.
     *
     * @param event the action event
     */
    @FXML
    private void handlePinClearPressed(ActionEvent event) {
        pinCodeField.setText("");

        // Clear any error message
        errorMessageLabel.setVisible(false);
    }

    /**
     * Handles the PIN backspace button press.
     * Deletes the last digit from the PIN code field.
     *
     * @param event the action event
     */
    @FXML
    private void handlePinBackspacePressed(ActionEvent event) {
        String currentPin = pinCodeField.getText();
        if (!currentPin.isEmpty()) {
            pinCodeField.setText(currentPin.substring(0, currentPin.length() - 1));
        }

        // Clear any error message
        errorMessageLabel.setVisible(false);
    }

    /**
     * Handles the start scan button action.
     *
     * @param event the action event
     */
    private void handleStartScanButtonAction(ActionEvent event) {
        // Show the camera preview and mock scan button
        cameraPreviewContainer.setVisible(true);
        cameraPreviewContainer.setManaged(true);
        mockScanSuccessButton.setVisible(true);
        mockScanSuccessButton.setManaged(true);

        // Hide the start scan button
        startScanButton.setVisible(false);
        startScanButton.setManaged(false);

        // Start the camera preview simulation
        startCameraPreviewSimulation();

        // Add event handler for mock scan success button
        mockScanSuccessButton.setOnAction(this::handleMockScanSuccessAction);
    }

    /**
     * Starts a simulation of the camera preview.
     * This method creates a timeline that updates the camera preview image
     * with random noise to simulate a camera feed.
     */
    private void startCameraPreviewSimulation() {
        // Stop any existing timeline
        if (cameraPreviewTimeline != null) {
            cameraPreviewTimeline.stop();
        }

        // Create a new timeline that updates the camera preview image every 100ms
        cameraPreviewTimeline = new Timeline(
            new KeyFrame(Duration.millis(100), event -> {
                // Generate a random image to simulate camera feed
                // In a real implementation, this would use the CameraService to get a camera frame
                Random random = new Random();
                int width = 300;
                int height = 200;

                // Create a WritableImage and fill it with random noise
                javafx.scene.image.WritableImage image = new javafx.scene.image.WritableImage(width, height);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int gray = random.nextInt(256);
                        javafx.scene.paint.Color color = javafx.scene.paint.Color.rgb(gray, gray, gray);
                        image.getPixelWriter().setColor(x, y, color);
                    }
                }

                // Set the image to the camera preview
                cameraPreviewImage.setImage(image);
            })
        );

        cameraPreviewTimeline.setCycleCount(Timeline.INDEFINITE);
        cameraPreviewTimeline.play();
    }

    /**
     * Handles the mock scan success button action.
     * Simulates a successful scan of a barcode/QR code.
     *
     * @param event the action event
     */
    private void handleMockScanSuccessAction(ActionEvent event) {
        // Stop the camera preview simulation
        if (cameraPreviewTimeline != null) {
            cameraPreviewTimeline.stop();
        }

        // Show the login progress indicator
        loginProgressIndicator.setVisible(true);

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
            errorMessageLabel.setVisible(true);
        }
    }

    /**
     * Shows the PIN code form by default since username/password form has been removed.
     * This method is kept for backward compatibility.
     */
    private void showUsernamePasswordForm() {
        // Username/password form has been removed, so show PIN code form instead
        showPinCodeForm();
    }

    /**
     * Shows the PIN code form and hides the other forms.
     */
    private void showPinCodeForm() {
        // Show PIN code form
        pinCodeForm.setVisible(true);
        pinCodeForm.setManaged(true);
        // Hide camera scan form
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
            errorMessageLabel.setVisible(true);
            return;
        }

        if (!pin.matches("\\d+")) {
            getViewModel().setErrorMessage("PIN code must contain only digits");
            errorMessageLabel.setVisible(true);
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
                errorMessageLabel.setVisible(true);
            }
        } else {
            getViewModel().setErrorMessage("Invalid PIN code");
            errorMessageLabel.setVisible(true);
        }
    }

    /**
     * Shows the camera scan form and hides the other forms.
     */
    private void showCameraScanForm() {
        // Hide PIN code form
        pinCodeForm.setVisible(false);
        pinCodeForm.setManaged(false);
        // Show camera scan form
        cameraScanForm.setVisible(true);
        cameraScanForm.setManaged(true);
    }
}
