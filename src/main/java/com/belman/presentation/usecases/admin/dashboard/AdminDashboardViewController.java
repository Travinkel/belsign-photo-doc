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
    private Button backupButton;

    @FXML
    private Button exportButton;

    @FXML
    private Button settingsButton;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label errorLabel;

    // Statistics labels
    @FXML
    private Label totalUsersLabel;

    @FXML
    private Label activeOrdersLabel;

    @FXML
    private Label completedOrdersLabel;

    @FXML
    private Label pendingReviewsLabel;

    @Override
    protected void setupBindings() {
        // Bind view model properties to UI elements
        welcomeLabel.textProperty().bind(getViewModel().welcomeMessageProperty());
        errorLabel.textProperty().bind(getViewModel().errorMessageProperty());

        // Bind statistics properties to labels
        totalUsersLabel.textProperty().bind(getViewModel().totalUsersProperty().asString());
        activeOrdersLabel.textProperty().bind(getViewModel().activeOrdersProperty().asString());
        completedOrdersLabel.textProperty().bind(getViewModel().completedOrdersProperty().asString());
        pendingReviewsLabel.textProperty().bind(getViewModel().pendingReviewsProperty().asString());
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

    /**
     * Handles the backup database button click.
     */
    @FXML
    private void handleBackupDatabase() {
        getViewModel().backupDatabase();
    }

    /**
     * Handles the export reports button click.
     */
    @FXML
    private void handleExportReports() {
        getViewModel().exportReports();
    }

    /**
     * Handles the system settings button click.
     */
    @FXML
    private void handleSystemSettings() {
        getViewModel().openSystemSettings();
    }
}
