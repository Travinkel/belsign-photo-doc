package com.belman.unit.infrastructure;

import com.belman.domain.valueobjects.OrderId;
import com.belman.domain.entities.PhotoDocument;
import com.belman.domain.entities.Report;
import com.belman.domain.valueobjects.Timestamp;
import com.belman.domain.valueobjects.EmailAddress;
import com.belman.domain.valueobjects.HashedPassword;
import com.belman.domain.aggregates.User;
import com.belman.domain.valueobjects.Username;
import com.belman.infrastructure.email.SmtpEmailService;
import com.belman.unit.backbone.util.GluonTestStorageHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GraalVM and Gluon Substrate compatible version of the SmtpEmailServiceTest.
 * This test uses the GluonTestStorageHelper to create temporary files in a
 * platform-agnostic way, making it compatible with both desktop and mobile platforms.
 */
class GluonCompatibleSmtpEmailServiceTest {

    private SmtpEmailService emailService;
    private Report report;
    private EmailAddress recipient;
    private List<File> testFiles;

    @BeforeEach
    void setUp() throws IOException {
        // Create a test email service with test-specific configuration
        emailService = new SmtpEmailService(
            "localhost", // Use localhost instead of a real SMTP server
            25,          // Use a standard port
            "test",      // Use a simple test username
            "test",      // Use a simple test password
            "test@localhost" // Use a test from address
        );

        // Create a test report
        OrderId orderId = OrderId.newId();
        Username username = new Username("test-user");
        HashedPassword hashedPassword= new HashedPassword("test-password");
        EmailAddress email = new EmailAddress("test@localhost");
        User qaUser = new User(username, hashedPassword, email);
        Timestamp now = new Timestamp(Instant.now());
        List<PhotoDocument> approvedPhotos = new ArrayList<>();

        report = new Report(orderId, approvedPhotos, qaUser, now);

        // Create a test recipient
        recipient = new EmailAddress("test-recipient@localhost");

        // Create test files using the GluonTestStorageHelper
        testFiles = new ArrayList<>();
        byte[] pdfContent = "This is a test PDF file".getBytes(StandardCharsets.UTF_8);
        testFiles.add(GluonTestStorageHelper.createTempTestFile("test-report.pdf", pdfContent));
    }

    @AfterEach
    void tearDown() {
        // Clean up test files
        GluonTestStorageHelper.cleanupTempTestFiles(testFiles);
    }

    @Test
    @DisplayName("Send report to a single recipient should succeed")
    void sendReportToSingleRecipientShouldReturnTrue() {
        boolean result = emailService.sendReport(
            report,
            recipient,
            "QC Report for Order " + report.getOrderId(),
            "Please find attached the QC report for your order.",
            Collections.emptyList()
        );

        assertTrue(result, "Email should be sent successfully");
    }

    @Test
    @DisplayName("Send report to multiple recipients should succeed")
    void sendReportToMultipleRecipientsShouldReturnTrue() {
        List<EmailAddress> recipients = new ArrayList<>();
        recipients.add(recipient);
        recipients.add(new EmailAddress("manager@example.com"));

        boolean result = emailService.sendReport(
            report,
            recipients,
            "QC Report for Order " + report.getOrderId(),
            "Please find attached the QC report for your order.",
            Collections.emptyList()
        );

        assertTrue(result, "Email should be sent successfully to multiple recipients");
    }

    @Test
    @DisplayName("Send report with file attachments should succeed")
    void sendReportWithAttachmentsShouldReturnTrue() {
        boolean result = emailService.sendReport(
            report,
            recipient,
            "QC Report for Order " + report.getOrderId(),
            "Please find attached the QC report for your order.",
            testFiles
        );

        assertTrue(result, "Email with attachments should be sent successfully");
    }
}