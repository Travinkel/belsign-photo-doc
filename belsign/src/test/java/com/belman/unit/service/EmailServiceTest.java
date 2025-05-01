package com.belman.unit.service;


import com.belman.domain.valueobjects.OrderId;
import com.belman.domain.entities.PhotoDocument;
import com.belman.domain.entities.Report;
import com.belman.domain.valueobjects.Timestamp;
import com.belman.domain.valueobjects.EmailAddress;
import com.belman.domain.valueobjects.HashedPassword;
import com.belman.domain.aggregates.User;
import com.belman.domain.valueobjects.Username;
import com.belman.domain.services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    private Report report;
    private User user;
    private EmailAddress recipient;
    private List<EmailAddress> recipients;
    private String subject;
    private String message;
    private List<File> attachments;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        // Create test data
        OrderId orderId = new OrderId(UUID.randomUUID());
        user = new User(new Username("testuser"), new HashedPassword("hashedPassword123"), new EmailAddress("test@example.com"));
        Timestamp timestamp = Timestamp.now();
        List<PhotoDocument> approvedPhotos = new ArrayList<>();

        // Create a report
        report = new Report(orderId, approvedPhotos, user, timestamp);

        // Create email data
        recipient = new EmailAddress("customer@example.com");
        recipients = Arrays.asList(
                recipient,
                new EmailAddress("manager@example.com")
        );
        subject = "QC Report for Order " + orderId.toUUID();
        message = "Please find attached the QC report for your order.";
        attachments = new ArrayList<>();

        // Create a mock implementation of EmailService
        emailService = new EmailService() {
            @Override
            public boolean sendReport(Report report, List<EmailAddress> recipients, String subject, String message, List<File> attachments) {
                // In a real implementation, this would send an email
                // For testing, we just verify the parameters and return success
                return report != null && recipients != null && !recipients.isEmpty() && subject != null && message != null;
            }

            @Override
            public boolean sendReport(Report report, EmailAddress recipient, String subject, String message, List<File> attachments) {
                // In a real implementation, this would send an email
                // For testing, we just verify the parameters and return success
                return report != null && recipient != null && subject != null && message != null;
            }
        };
    }

    @Test
    void sendReportToMultipleRecipientsShouldReturnTrue() {
        // Send a report to multiple recipients
        boolean result = emailService.sendReport(report, recipients, subject, message, attachments);

        // Verify the result
        assertTrue(result);
    }

    @Test
    void sendReportToSingleRecipientShouldReturnTrue() {
        // Send a report to a single recipient
        boolean result = emailService.sendReport(report, recipient, subject, message, attachments);

        // Verify the result
        assertTrue(result);
    }

    @Test
    void sendReportWithNullReportShouldReturnFalse() {
        // Send a null report
        boolean result = emailService.sendReport(null, recipient, subject, message, attachments);

        // Verify the result
        assertFalse(result);
    }

    @Test
    void sendReportWithNullRecipientShouldReturnFalse() {
        // Send a report to a null recipient
        boolean result = emailService.sendReport(report, (EmailAddress) null, subject, message, attachments);

        // Verify the result
        assertFalse(result);
    }

    @Test
    void sendReportWithEmptyRecipientsShouldReturnFalse() {
        // Send a report to an empty list of recipients
        boolean result = emailService.sendReport(report, new ArrayList<>(), subject, message, attachments);

        // Verify the result
        assertFalse(result);
    }

    @Test
    void sendReportWithNullSubjectShouldReturnFalse() {
        // Send a report with a null subject
        boolean result = emailService.sendReport(report, recipient, null, message, attachments);

        // Verify the result
        assertFalse(result);
    }

    @Test
    void sendReportWithNullMessageShouldReturnFalse() {
        // Send a report with a null message
        boolean result = emailService.sendReport(report, recipient, subject, null, attachments);

        // Verify the result
        assertFalse(result);
    }

    @Test
    void sendReportWithNullAttachmentsShouldReturnTrue() {
        // Send a report with null attachments (attachments are optional)
        boolean result = emailService.sendReport(report, recipient, subject, message, null);

        // Verify the result
        assertTrue(result);
    }
}
