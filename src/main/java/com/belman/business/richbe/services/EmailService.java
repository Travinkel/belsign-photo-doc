package com.belman.business.richbe.services;


import com.belman.business.richbe.common.EmailAddress;
import com.belman.business.richbe.report.ReportAggregate;

import java.io.File;
import java.util.List;

/**
 * Service for sending emails with QC reports to customers.
 */
public interface EmailService {
    /**
     * Sends a QC reportAggregate to the specified email addresses.
     * 
     * @param reportAggregate the reportAggregate to send
     * @param recipients the email addresses of the recipients
     * @param subject the email subject
     * @param message the email message
     * @param attachments optional file attachments (e.g., PDF reportAggregate)
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendReport(ReportAggregate reportAggregate, List<EmailAddress> recipients, String subject, String message, List<File> attachments);
    
    /**
     * Sends a QC reportAggregate to the specified email address.
     * 
     * @param reportAggregate the reportAggregate to send
     * @param recipient the email address of the recipient
     * @param subject the email subject
     * @param message the email message
     * @param attachments optional file attachments (e.g., PDF reportAggregate)
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendReport(ReportAggregate reportAggregate, EmailAddress recipient, String subject, String message, List<File> attachments);
}