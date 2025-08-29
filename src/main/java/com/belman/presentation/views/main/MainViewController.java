package com.belman.presentation.views.main;


import com.belman.presentation.base.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller for the main view.
 */
public class MainViewController extends BaseController<MainViewModel> {
    @FXML
    private Label welcomeLabel;

    @FXML
    private Button logoutButton;
    
    @FXML
    private Button adminButton;
    
    @FXML
    private Button qaButton;
    
    @FXML
    private Button productionButton;

    @Override
    protected void setupBindings() {
        // Bind UI components to ViewModel properties if they exist
        // This is important for tests where the FXML might not have these elements
        if (welcomeLabel != null) {
            welcomeLabel.textProperty().bind(getViewModel().welcomeMessageProperty());
        }

        // Set up event handlers if the buttons exist
        if (logoutButton != null) {
            logoutButton.setOnAction(this::handleLogoutButtonAction);
        }
        
        // Set up role selection buttons
        if (adminButton != null) {
            adminButton.setOnAction(this::handleAdminButtonAction);
            // Disable the button if the user doesn't have the admin role
            adminButton.disableProperty().bind(getViewModel().adminRoleAvailableProperty().not());
        }
        
        if (qaButton != null) {
            qaButton.setOnAction(this::handleQAButtonAction);
            // Disable the button if the user doesn't have the QA role
            qaButton.disableProperty().bind(getViewModel().qaRoleAvailableProperty().not());
        }
        
        if (productionButton != null) {
            productionButton.setOnAction(this::handleProductionButtonAction);
            // Disable the button if the user doesn't have the production role
            productionButton.disableProperty().bind(getViewModel().productionRoleAvailableProperty().not());
        }
    }

    @Override
    public void initializeBinding() {
        // Call setupBindings to avoid duplication
        setupBindings();
    }

    /**
     * Handles the logout button action.
     *
     * @param event the action event
     */
    private void handleLogoutButtonAction(ActionEvent event) {
        getViewModel().logout();
    }
    
    /**
     * Handles the admin button action.
     *
     * @param event the action event
     */
    private void handleAdminButtonAction(ActionEvent event) {
        getViewModel().navigateToAdminView();
    }
    
    /**
     * Handles the QA button action.
     *
     * @param event the action event
     */
    private void handleQAButtonAction(ActionEvent event) {
        getViewModel().navigateToQAView();
    }
    
    /**
     * Handles the production button action.
     *
     * @param event the action event
     */
    private void handleProductionButtonAction(ActionEvent event) {
        getViewModel().navigateToProductionView();
    }
}