package com.belman.presentation.usecases.qa.done;

import com.belman.presentation.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller for the QA done view.
 * Handles UI interactions for displaying a completion message after approving or rejecting an order.
 */
public class QADoneViewController extends BaseController<QADoneViewModel> {

    @FXML
    private Label orderNumberLabel;

    @FXML
    private Label completionMessageLabel;

    @FXML
    private Button backToDashboardButton;

    @FXML
    private VBox approvedContainer;

    @FXML
    private VBox rejectedContainer;

    @Override
    protected void setupBindings() {
        // Bind view model properties to UI elements
        orderNumberLabel.textProperty().bind(getViewModel().orderNumberProperty());
        completionMessageLabel.textProperty().bind(getViewModel().completionMessageProperty());

        // Show/hide containers based on approval status
        approvedContainer.visibleProperty().bind(getViewModel().approvedProperty());
        approvedContainer.managedProperty().bind(getViewModel().approvedProperty());
        rejectedContainer.visibleProperty().bind(getViewModel().approvedProperty().not());
        rejectedContainer.managedProperty().bind(getViewModel().approvedProperty().not());
    }

    /**
     * Handles the back to dashboard button click.
     */
    @FXML
    private void handleBackToDashboard() {
        getViewModel().navigateToDashboard();
    }
}