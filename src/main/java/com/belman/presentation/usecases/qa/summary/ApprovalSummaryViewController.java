package com.belman.presentation.usecases.qa.summary;

import com.belman.presentation.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * Controller for the approval summary view.
 * Handles UI interactions for displaying the result of the QA review process.
 */
public class ApprovalSummaryViewController extends BaseController<ApprovalSummaryViewModel> {

    @FXML
    private Label orderNumberLabel;

    @FXML
    private Label summaryMessageLabel;

    @FXML
    private TextArea commentTextArea;

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
        summaryMessageLabel.textProperty().bind(getViewModel().summaryMessageProperty());
        commentTextArea.textProperty().bind(getViewModel().commentProperty());

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