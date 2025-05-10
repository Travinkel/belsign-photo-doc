package com.belman.ui.views.login;

import com.belman.ui.base.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressIndicator;

/**
 * Controller for the login view.
 */
public class LoginViewController extends BaseController<LoginViewModel> {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private ProgressIndicator loginProgressIndicator;

    @FXML
    private CheckBox rememberMeCheckBox;

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

        usernameField.textProperty().bindBidirectional(getViewModel().usernameProperty());
        passwordField.textProperty().bindBidirectional(getViewModel().passwordProperty());
        errorMessageLabel.textProperty().bind(getViewModel().errorMessageProperty());
        loginProgressIndicator.visibleProperty().bind(getViewModel().loginInProgressProperty());
        loginButton.disableProperty().bind(getViewModel().loginInProgressProperty());
        rememberMeCheckBox.selectedProperty().bindBidirectional(getViewModel().rememberMeProperty());

        // Set up event handlers
        loginButton.setOnAction(this::handleLoginButtonAction);
        cancelButton.setOnAction(this::handleCancelButtonAction);

        // Set up key event handlers for Enter key
        usernameField.setOnAction(this::handleLoginButtonAction);
        passwordField.setOnAction(this::handleLoginButtonAction);
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
        getViewModel().login();
    }

    /**
     * Handles the cancel button action.
     * 
     * @param event the action event
     */
    private void handleCancelButtonAction(ActionEvent event) {
        getViewModel().cancel();
    }
}
