package com.belman.infrastructure.email;



import com.belman.domain.report.ReportAggregate;
import com.belman.domain.valueobjects.EmailAddress;
import com.belman.domain.services.EmailService;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of EmailService that sends emails using SMTP.
 */
public class SmtpEmailService implements EmailService {
    private static final Logger LOGGER = Logger.getLogger(SmtpEmailService.class.getName());
    
    private final String smtpHost;
    private final int smtpPort;
    private final String username;
    private final String password;
    private final String fromAddress;
    
    /**
     * Creates a new SmtpEmailService with the specified SMTP configuration.
     * 
     * @param smtpHost the SMTP server host
     * @param smtpPort the SMTP server port
     * @param username the SMTP server username
     * @param password the SMTP server password
     * @param fromAddress the email address to send from
     */
    public SmtpEmailService(String smtpHost, int smtpPort, String username, String password, String fromAddress) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.username = username;
        this.password = password;
        this.fromAddress = fromAddress;
    }
    
    @Override
    public boolean sendReport(ReportAggregate reportAggregate, List<EmailAddress> recipients, String subject, String message, List<File> attachments) {
        try {
            // In a real implementation, this would use JavaMail API to send the email
            LOGGER.info("Sending reportAggregate " + reportAggregate.getOrderId() + " to " + recipients.size() + " recipients");
            
            // Log the email details for debugging
            LOGGER.info("Subject: " + subject);
            LOGGER.info("Message: " + message);
            LOGGER.info("Attachments: " + (attachments != null ? attachments.size() : 0));
            
            // Simulate sending the email
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send email", e);
            return false;
        }
    }
    
    @Override
    public boolean sendReport(ReportAggregate reportAggregate, EmailAddress recipient, String subject, String message, List<File> attachments) {
        return sendReport(reportAggregate, Collections.singletonList(recipient), subject, message, attachments);
    }
}