package com.belman.presentation.usecases.qa.done;

import com.belman.presentation.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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

    @FXML
    private TextField recipientField;

    @FXML
    private TextField subjectField;

    @FXML
    private TextArea messageField;

    @FXML
    private CheckBox attachReportCheckBox;

    @FXML
    private Button sendEmailButton;

    @FXML
    private Label emailStatusLabel;

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

        // Set up default values for email fields
        String orderNumber = getViewModel().orderNumberProperty().get();
        subjectField.setText("QC Report for Order " + orderNumber);
        messageField.setText("Please find attached the Quality Control report for order " + orderNumber + ".\n\nIf you have any questions, please don't hesitate to contact us.\n\nBest regards,\nBelman QA Team");

        // Only enable email functionality for approved orders
        boolean isApproved = getViewModel().approvedProperty().get();
        recipientField.setDisable(!isApproved);
        subjectField.setDisable(!isApproved);
        messageField.setDisable(!isApproved);
        attachReportCheckBox.setDisable(!isApproved);
        sendEmailButton.setDisable(!isApproved);
    }

    /**
     * Handles the back to dashboard button click.
     */
    @FXML
    private void handleBackToDashboard() {
        getViewModel().navigateToDashboard();
    }

    /**
     * Handles the send email button click.
     */
    @FXML
    private void handleSendEmail() {
        // Get the email details from the UI
        String recipient = recipientField.getText();
        String subject = subjectField.getText();
        String message = messageField.getText();
        boolean attachReport = attachReportCheckBox.isSelected();

        // Call the view model to send the email
        boolean success = getViewModel().sendEmail(recipient, subject, message, attachReport);

        // Update the UI based on the result
        if (success) {
            emailStatusLabel.setText("Email sent successfully");
            emailStatusLabel.setStyle("-fx-text-fill: green;");
        } else {
            emailStatusLabel.setText("Failed to send email");
            emailStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
