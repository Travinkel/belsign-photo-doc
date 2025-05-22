package com.belman.presentation.usecases.qa.done;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.report.ReportBusiness;
import com.belman.domain.report.ReportFormat;
import com.belman.domain.report.ReportId;
import com.belman.domain.report.ReportType;
import com.belman.application.usecase.email.EmailService;
import com.belman.application.usecase.report.ReportService;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.qa.dashboard.QADashboardView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

/**
 * ViewModel for the QA done view.
 * Provides data and operations for displaying a completion message after approving or rejecting an order.
 */
public class QADoneViewModel extends BaseViewModel<QADoneViewModel> {
    private final StringProperty orderNumber = new SimpleStringProperty("");
    private final StringProperty completionMessage = new SimpleStringProperty("QA process completed");
    private final BooleanProperty approved = new SimpleBooleanProperty(false);

    @Inject
    private SessionContext sessionContext;

    @Inject
    private EmailService emailService;

    @Inject
    private ReportService reportService;

    @Inject
    private OrderRepository orderRepository;

    private OrderId orderId;
    private ReportId reportId;

    /**
     * Default constructor for use by the ViewLoader.
     */
    public QADoneViewModel() {
        // Default constructor
    }

    @Override
    public void onShow() {
        // Get parameters from the navigation context
        String orderNumberStr = Router.getParameter("orderNumber");
        if (orderNumberStr != null) {
            orderNumber.set(orderNumberStr);

            // Find the order by order number to get its ID
            try {
                orderRepository.findAll().stream()
                        .filter(o -> o.getOrderNumber().toString().equals(orderNumberStr))
                        .findFirst()
                        .ifPresent(order -> orderId = order.getId());
            } catch (Exception e) {
                // Log the error but continue
                System.err.println("Error finding order: " + e.getMessage());
            }
        }

        Boolean approvedValue = Router.getParameter("approved");
        if (approvedValue != null) {
            approved.set(approvedValue);

            // Update completion message based on approval status
            if (approved.get()) {
                completionMessage.set("Order " + orderNumber.get() + " has been approved");

                // Generate a report for approved orders
                if (orderId != null) {
                    try {
                        // Get the current user
                        sessionContext.getUser().ifPresent(user -> {
                            // Generate a report
                            ReportBusiness report = reportService.generateReport(
                                    orderId, 
                                    ReportType.PHOTO_DOCUMENTATION, 
                                    ReportFormat.PDF, 
                                    user);

                            // Store the report ID for later use
                            reportId = report.getId();
                        });
                    } catch (Exception e) {
                        // Log the error but continue
                        System.err.println("Error generating report: " + e.getMessage());
                    }
                }
            } else {
                completionMessage.set("Order " + orderNumber.get() + " has been rejected");
            }
        }
    }

    /**
     * Navigates back to the QA dashboard.
     */
    public void navigateToDashboard() {
        Router.navigateTo(QADashboardView.class);
    }

    /**
     * Gets the order number property.
     *
     * @return the order number property
     */
    public StringProperty orderNumberProperty() {
        return orderNumber;
    }

    /**
     * Gets the completion message property.
     *
     * @return the completion message property
     */
    public StringProperty completionMessageProperty() {
        return completionMessage;
    }

    /**
     * Gets the approved property.
     *
     * @return the approved property
     */
    public BooleanProperty approvedProperty() {
        return approved;
    }

    /**
     * Sends an email with the QC report to the specified recipient.
     *
     * @param recipientEmail the email address of the recipient
     * @param subject        the subject of the email
     * @param message        the message body of the email
     * @param attachReport   whether to attach the QC report
     * @return true if the email was sent successfully, false otherwise
     */
    public boolean sendEmail(String recipientEmail, String subject, String message, boolean attachReport) {
        try {
            // Validate inputs
            if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
                System.err.println("Recipient email is required");
                return false;
            }

            if (subject == null || subject.trim().isEmpty()) {
                System.err.println("Subject is required");
                return false;
            }

            if (message == null || message.trim().isEmpty()) {
                System.err.println("Message is required");
                return false;
            }

            // Create an EmailAddress from the recipient email
            EmailAddress recipient;
            try {
                recipient = new EmailAddress(recipientEmail);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid email address: " + e.getMessage());
                return false;
            }

            // If we're attaching a report, we need a report ID
            if (attachReport && reportId == null) {
                System.err.println("No report available to attach");
                return false;
            }

            // Send the email
            if (attachReport) {
                // Get the report as a PDF
                byte[] reportPdf = reportService.previewReport(orderId, ReportType.PHOTO_DOCUMENTATION, ReportFormat.PDF);

                // Send the email with the report attached
                return emailService.sendEmailWithAttachment(
                        recipient,
                        subject,
                        message,
                        "QC_Report_" + orderNumber.get() + ".pdf",
                        reportPdf,
                        "application/pdf");
            } else {
                // Send a simple email without attachments
                return emailService.sendEmail(recipient, subject, message);
            }
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            return false;
        }
    }
}
