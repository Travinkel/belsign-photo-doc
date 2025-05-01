package com.belman.unit.infrastructure;


import com.belman.domain.valueobjects.OrderId;
import com.belman.domain.entities.PhotoDocument;
import com.belman.domain.entities.Report;
import com.belman.domain.valueobjects.Timestamp;
import com.belman.domain.valueobjects.EmailAddress;
import com.belman.domain.valueobjects.HashedPassword;
import com.belman.domain.aggregates.User;
import com.belman.domain.valueobjects.Username;
import com.belman.infrastructure.service.SmtpEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
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
        // Create a test email service
        emailService = new SmtpEmailService(
            "smtp.belman.dk",
            587,
            "test@belman.dk",
            "password",
            "noreply@belman.dk"
        );
        
        // Create a test report
        OrderId orderId = OrderId.newId();
        Username username = new Username("qa1");
        HashedPassword hashedPassword= new HashedPassword("hashedpassword");
        EmailAddress email = new EmailAddress("qa1@belman.dk");
        User qaUser = new User(username, hashedPassword, email);
        Timestamp now = new Timestamp(Instant.now());
        List<PhotoDocument> approvedPhotos = new ArrayList<>();
        
        report = new Report(orderId, approvedPhotos, qaUser, now);
        
        // Create a test recipient
        recipient = new EmailAddress("customer@example.com");
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
    
    @Test
    void sendReportWithAttachmentsShouldReturnTrue() {
        List<File> attachments = new ArrayList<>();
        attachments.add(new File("test-report.pdf"));
        
        boolean result = emailService.sendReport(
            report,
            recipient,
            "QC Report for Order " + report.getOrderId(),
            "Please find attached the QC report for your order.",
            attachments
        );
        
        assertTrue(result, "Email with attachments should be sent successfully");
    }
}