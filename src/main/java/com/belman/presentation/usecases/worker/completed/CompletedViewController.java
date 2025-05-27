package com.belman.presentation.usecases.worker.completed;

import com.belman.presentation.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller for the CompletedView.
 * Handles UI interactions for displaying a completion message after submitting photos.
 */
public class CompletedViewController extends BaseController<CompletedViewModel> {

    @FXML
    private Label orderNumberLabel;

    @FXML
    private Label completionMessageLabel;

    @FXML
    private Button newOrderButton;

    @FXML
    private Button logoutButton;

    @Override
    protected void setupBindings() {
        // Bind view model properties to UI elements using safelyBind
        safelyBind(orderNumberLabel, getViewModel().orderNumberProperty());
        safelyBind(completionMessageLabel, getViewModel().completionMessageProperty());
    }

    /**
     * Handles the new order button click.
     */
    @FXML
    private void handleNewOrderClick() {
        getViewModel().goToAssignedOrder();
    }

    /**
     * Handles the logout button click.
     */
    @FXML
    private void handleLogoutClick() {
        getViewModel().logout();
    }
}
