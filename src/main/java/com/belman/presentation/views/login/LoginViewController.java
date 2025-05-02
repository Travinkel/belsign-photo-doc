package com.belman.presentation.views.login;

import com.belman.backbone.core.base.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private Label errorMessageLabel;
    
    @FXML
    private ProgressIndicator loginProgressIndicator;
    
    @Override
    public void initializeBinding() {
        // Bind UI components to ViewModel properties
        usernameField.textProperty().bindBidirectional(getViewModel().usernameProperty());
        passwordField.textProperty().bindBidirectional(getViewModel().passwordProperty());
        errorMessageLabel.textProperty().bind(getViewModel().errorMessageProperty());
        loginProgressIndicator.visibleProperty().bind(getViewModel().loginInProgressProperty());
        loginButton.disableProperty().bind(getViewModel().loginInProgressProperty());
        
        // Set up event handlers
        loginButton.setOnAction(this::handleLoginButtonAction);
        
        // Set up key event handlers for Enter key
        usernameField.setOnAction(this::handleLoginButtonAction);
        passwordField.setOnAction(this::handleLoginButtonAction);
    }
    
    /**
     * Handles the login button action.
     * 
     * @param event the action event
     */
    private void handleLoginButtonAction(ActionEvent event) {
        getViewModel().login();
    }
}