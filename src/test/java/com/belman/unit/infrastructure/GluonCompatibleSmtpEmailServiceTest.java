package com.belman.unit.infrastructure;

import com.belman.domain.common.EmailAddress;
import com.belman.domain.common.Timestamp;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.report.ReportAggregate;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.Username;
import com.belman.repository.email.SmtpEmailService;
import com.belman.unit.backbone.util.GluonTestStorageHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GraalVM and Gluon Substrate compatible version of the SmtpEmailServiceTest.
 * This test uses the GluonTestStorageHelper to create temporary files in a
 * platform-agnostic way, making it compatible with both desktop and mobile platforms.
 */
class GluonCompatibleSmtpEmailServiceTest {

    private SmtpEmailService emailService;
    private ReportAggregate reportAggregate;
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

        // Create a test reportAggregate
        OrderId orderId = OrderId.newId();
        Username username = new Username("test-user");
        HashedPassword hashedPassword = new HashedPassword("test-password");
        EmailAddress email = new EmailAddress("test@localhost");
        UserBusiness qaUser = new UserBusiness.Builder()
                .id(UserId.newId())
                .username(username)
                .password(hashedPassword)
                .email(email)
                .build();
        Timestamp now = new Timestamp(Instant.now());
        List<PhotoDocument> approvedPhotos = new ArrayList<>();

        reportAggregate = new ReportAggregate(orderId, approvedPhotos, qaUser, now);

        // Create a test recipient
        recipient = new EmailAddress("test-recipient@localhost");

        // Create test files using the GluonTestStorageHelper
        testFiles = new ArrayList<>();
        byte[] pdfContent = "This is a test PDF file".getBytes(StandardCharsets.UTF_8);
        testFiles.add(GluonTestStorageHelper.createTempTestFile("test-reportAggregate.pdf", pdfContent));
    }

    @AfterEach
    void tearDown() {
        // Clean up test files
        GluonTestStorageHelper.cleanupTempTestFiles(testFiles);
    }

    @Test
    @DisplayName("Send reportAggregate to a single recipient should succeed")
    void sendReportToSingleRecipientShouldReturnTrue() {
        boolean result = emailService.sendReport(
                reportAggregate,
                recipient,
                "QC ReportAggregate for OrderBusiness " + reportAggregate.getOrderId(),
                "Please find attached the QC reportAggregate for your order.",
                Collections.emptyList()
        );

        assertTrue(result, "Email should be sent successfully");
    }

    @Test
    @DisplayName("Send reportAggregate to multiple recipients should succeed")
    void sendReportToMultipleRecipientsShouldReturnTrue() {
        List<EmailAddress> recipients = new ArrayList<>();
        recipients.add(recipient);
        recipients.add(new EmailAddress("manager@example.com"));

        boolean result = emailService.sendReport(
                reportAggregate,
                recipients,
                "QC ReportAggregate for OrderBusiness " + reportAggregate.getOrderId(),
                "Please find attached the QC reportAggregate for your order.",
                Collections.emptyList()
        );

        assertTrue(result, "Email should be sent successfully to multiple recipients");
    }

    @Test
    @DisplayName("Send reportAggregate with file attachments should succeed")
    void sendReportWithAttachmentsShouldReturnTrue() {
        boolean result = emailService.sendReport(
                reportAggregate,
                recipient,
                "QC ReportAggregate for OrderBusiness " + reportAggregate.getOrderId(),
                "Please find attached the QC reportAggregate for your order.",
                testFiles
        );

        assertTrue(result, "Email with attachments should be sent successfully");
    }
}
