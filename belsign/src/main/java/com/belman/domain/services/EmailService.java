package com.belman.domain.services;


import com.belman.domain.entities.Report;
import com.belman.domain.valueobjects.EmailAddress;

import java.io.File;
import java.util.List;

/**
 * Service for sending emails with QC reports to customers.
 */
public interface EmailService {
    /**
     * Sends a QC report to the specified email addresses.
     * 
     * @param report the report to send
     * @param recipients the email addresses of the recipients
     * @param subject the email subject
     * @param message the email message
     * @param attachments optional file attachments (e.g., PDF report)
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendReport(Report report, List<EmailAddress> recipients, String subject, String message, List<File> attachments);
    
    /**
     * Sends a QC report to the specified email address.
     * 
     * @param report the report to send
     * @param recipient the email address of the recipient
     * @param subject the email subject
     * @param message the email message
     * @param attachments optional file attachments (e.g., PDF report)
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendReport(Report report, EmailAddress recipient, String subject, String message, List<File> attachments);
}