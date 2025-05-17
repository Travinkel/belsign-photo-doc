package com.belman.presentation.usecases.archive.authentication.logout;

import com.belman.common.logging.EmojiLogger;
import com.belman.presentation.base.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

/**
 * Controller for the logout view.
 */
public class LogoutViewController extends BaseController<LogoutViewModel> {
    private static final EmojiLogger logger = EmojiLogger.getLogger(LogoutViewController.class);

    @FXML
    private Button logoutButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private ProgressIndicator logoutProgressIndicator;

    /**
     * Creates a new LogoutViewController.
     */
    public LogoutViewController() {
        super();
        logger.debug("LogoutViewController constructor called");
    }

    @Override
    protected void setupBindings() {
        if (getViewModel() == null) {
            logger.error("Error in LogoutViewController.setupBindings(): ViewModel is null");
            // Create a temporary view model if it's null - this is just a safety measure
            LogoutViewModel tempViewModel = new LogoutViewModel();
            setViewModel(tempViewModel);
            logger.debug("Created temporary LogoutViewModel to avoid NullPointerException");
        }

        // Bind UI components to ViewModel properties
        errorMessageLabel.textProperty().bind(getViewModel().errorMessageProperty());
        logoutProgressIndicator.visibleProperty().bind(getViewModel().logoutInProgressProperty());
        logoutButton.disableProperty().bind(getViewModel().logoutInProgressProperty());

        // Add a listener to the error message property to show/hide the error message label
        getViewModel().errorMessageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                errorMessageLabel.setVisible(true);
            } else {
                errorMessageLabel.setVisible(false);
            }
        });

        // Set up event handlers for buttons
        logoutButton.setOnAction(this::handleLogoutButtonAction);
        cancelButton.setOnAction(this::handleCancelButtonAction);

        // Hide error message label initially
        errorMessageLabel.setVisible(false);
    }

    /**
     * Handles the logout button action.
     *
     * @param event the action event
     */
    private void handleLogoutButtonAction(ActionEvent event) {
        // Clear any previous error message
        errorMessageLabel.setVisible(false);

        // Call the view model's logout method
        getViewModel().logout();
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
    }
}
