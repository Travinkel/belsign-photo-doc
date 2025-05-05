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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmtpEmailServiceTest {

    private SmtpEmailService emailService;
    private Report report;
    private EmailAddress recipient;

    @BeforeEach
    void setUp() {
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
    }

    @Test
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

    @TempDir
    Path tempDir;

    private List<File> tempFiles = new ArrayList<>();

    @AfterEach
    void tearDown() {
        // Clean up any temporary files
        for (File file : tempFiles) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Test
    void sendReportWithAttachmentsShouldReturnTrue() throws IOException {
        // Create a temporary file for testing
        Path tempFile = tempDir.resolve("test-report.pdf");
        Files.write(tempFile, "This is a test PDF file".getBytes());
        File attachmentFile = tempFile.toFile();
        tempFiles.add(attachmentFile); // Add to list for cleanup

        List<File> attachments = new ArrayList<>();
        attachments.add(attachmentFile);

        boolean result = emailService.sendReport(
            report,
            recipient,
            "QC Report for Order " + report.getOrderId(),
            "Please find attached the QC report for your order.",
            attachments
        );

        assertTrue(result, "Email with attachments should be sent successfully");
        assertTrue(attachmentFile.exists(), "Temporary file should still exist");
    }
}
