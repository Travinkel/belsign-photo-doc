package com.belman.presentation.usecases.admin.dashboard;

import com.belman.presentation.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

/**
 * Controller for the Admin dashboard view.
 * Handles UI interactions for the Admin dashboard screen.
 */
public class AdminDashboardViewController extends BaseController<AdminDashboardViewModel> {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button manageUsersButton;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label errorLabel;

    @Override
    protected void setupBindings() {
        // Bind view model properties to UI elements
        welcomeLabel.textProperty().bind(getViewModel().welcomeMessageProperty());
        errorLabel.textProperty().bind(getViewModel().errorMessageProperty());
    }

    /**
     * Handles the manage users button click.
     */
    @FXML
    private void handleManageUsers() {
        getViewModel().navigateToUserManagement();
    }

    /**
     * Handles the logout button click.
     */
    @FXML
    private void handleLogout() {
        getViewModel().logout();
    }
}