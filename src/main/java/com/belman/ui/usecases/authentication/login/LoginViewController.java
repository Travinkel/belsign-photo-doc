package com.belman.ui.usecases.authentication.login;

import com.belman.bootstrap.camera.CameraServiceFactory;
import com.belman.domain.services.CameraService;
import com.belman.ui.base.BaseController;
import com.belman.ui.usecases.authentication.login.flow.CameraScanLoginState;
import com.belman.ui.usecases.authentication.login.flow.DefaultLoginContext;
import com.belman.ui.usecases.authentication.login.flow.PinLoginState;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Controller for the login view.
 */
public class LoginViewController extends BaseController<LoginViewModel> {


    @FXML private StackPane root;          // holds the whole scene
    @FXML private ImageView backgroundImage;
    @FXML private VBox loginCard;          // the card VBox

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
    public void onHide() {
        super.onHide();

        // Stop the camera preview timeline when the view is hidden
        if (cameraPreviewTimeline != null) {
            cameraPreviewTimeline.stop();
        }
    }

    @Override
    public void initializeBinding() {
        // Call setupBindings to avoid duplication
        setupBindings();


        // background image always fills the window
        backgroundImage.fitWidthProperty().bind(root.widthProperty());
        backgroundImage.fitHeightProperty().bind(root.heightProperty());

        // card: ≤ 450 px, otherwise 90 % of window width
        root.widthProperty().addListener((obs, o, w) ->
                loginCard.setMaxWidth(Math.min(450, w.doubleValue() * 0.90)));

        // buttons stay finger-friendly but shrink on small screens
        Stream.of(scanButton, pinButton, startScanButton,
                        mockScanSuccessButton, cancelButton, loginButton)
                .forEach(b -> b.minWidthProperty()
                        .bind(root.widthProperty().multiply(0.30).subtract(40)));
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
     * Gets a CameraService instance appropriate for the current platform.
     *
     * @return a CameraService instance
     */
    private CameraService getCameraService() {
        return CameraServiceFactory.getCameraService();
    }

    /**
     * Starts the camera preview using the CameraService.
     * If the camera is not available, falls back to a simulation.
     */
    private void startCameraPreviewSimulation() {
        // Stop any existing timeline
        if (cameraPreviewTimeline != null) {
            cameraPreviewTimeline.stop();
        }

        CameraService cameraService = getCameraService();

        if (cameraService.isCameraAvailable()) {
            // Use the actual camera
            // Since we can't get a continuous preview from the CameraService directly,
            // we'll take a photo when the user clicks the scan button
            // For now, just show a placeholder image
            Image placeholderImage = new Image(getClass().getResourceAsStream(
                    "/com/belman/ui/usecases/authentication/login/camera_placeholder.png"));
            if (placeholderImage.isError()) {
                // If the placeholder image can't be loaded, fall back to simulation
                startCameraSimulation();
            } else {
                cameraPreviewImage.setImage(placeholderImage);
            }
        } else {
            // Fall back to simulation if camera is not available
            startCameraSimulation();
        }
    }

    /**
     * Starts a simulation of the camera preview.
     * This method creates a timeline that updates the camera preview image
     * with random noise to simulate a camera feed.
     */
    private void startCameraSimulation() {
        // Create a new timeline that updates the camera preview image every 100ms
        cameraPreviewTimeline = new Timeline(
                new KeyFrame(Duration.millis(100), event -> {
                    // Generate a random image to simulate camera feed
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
     * Takes a photo using the camera service and processes it for login.
     *
     * @param event the action event
     */
    private void handleMockScanSuccessAction(ActionEvent event) {
        // Stop the camera preview simulation
        if (cameraPreviewTimeline != null) {
            cameraPreviewTimeline.stop();
        }

        // Show the login progress indicator by setting the loginInProgress property
        getViewModel().setLoginInProgress(true);

        CameraService cameraService = getCameraService();

        if (cameraService.isCameraAvailable()) {
            // Take a photo using the camera service
            Optional<File> photoFile = cameraService.takePhoto();

            if (photoFile.isPresent()) {
                // Photo was taken successfully
                // In a real implementation, this would process the photo to extract login credentials
                // For now, we'll just use hardcoded credentials
                processPhotoForLogin(photoFile.get());
            } else {
                // Photo taking was cancelled or failed
                getViewModel().setErrorMessage("Photo capture cancelled or failed");
                errorMessageLabel.setVisible(true);
                getViewModel().setLoginInProgress(false);
            }
        } else {
            // Camera is not available, use mock credentials
            mockLogin();
        }
    }

    /**
     * Processes a photo file for login.
     * In a real implementation, this would extract login credentials from the photo.
     * For now, it just uses hardcoded credentials.
     *
     * @param photoFile the photo file to process
     */
    private void processPhotoForLogin(File photoFile) {
        // For demonstration purposes, we'll just set a username and password
        // that would normally be extracted from the photo
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
            getViewModel().setLoginInProgress(false);
        }
    }

    /**
     * Performs a mock login with hardcoded credentials.
     * This is used when the camera is not available.
     */
    private void mockLogin() {
        // For demonstration purposes, we'll just set a username and password
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
            getViewModel().setLoginInProgress(false);
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
     * This method uses the view model's loginWithPin method to authenticate with the PIN code.
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

        // Use the view model's loginWithPin method to authenticate with the PIN code
        getViewModel().loginWithPin(pin);

        // Show error message if there is one
        if (!getViewModel().getErrorMessage().isEmpty()) {
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
