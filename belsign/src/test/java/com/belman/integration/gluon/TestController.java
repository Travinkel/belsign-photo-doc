package com.belman.integration.gluon;

import com.belman.backbone.core.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Test controller for integration testing.
 */
public class TestController extends BaseController<TestViewModel> {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Label errorLabel;
    
    private boolean bindingInitialized = false;
    
    @Override
    public void initializeBinding() {
        bindingInitialized = true;
        
        // Bind the view model properties to the UI controls
        if (getViewModel() != null) {
            // Example bindings
            loginButton.setOnAction(e -> getViewModel().login());
            cancelButton.setOnAction(e -> getViewModel().cancel());
        }
    }
    
    public boolean isBindingInitialized() {
        return bindingInitialized;
    }
}