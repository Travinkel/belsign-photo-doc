package com.belman.service.usecase.email;

import com.belman.domain.common.valueobjects.EmailAddress;

import java.util.List;

/**
 * Service for sending emails.
 * Provides methods for sending emails with attachments.
 */
public interface EmailService {
    /**
     * Sends an email to a single recipient.
     *
     * @param to      the recipient's email address
     * @param subject the subject of the email
     * @param body    the body of the email
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendEmail(EmailAddress to, String subject, String body);

    /**
     * Sends an email to multiple recipients.
     *
     * @param to      the recipients' email addresses
     * @param subject the subject of the email
     * @param body    the body of the email
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendEmail(List<EmailAddress> to, String subject, String body);

    /**
     * Sends an email with an attachment to a single recipient.
     *
     * @param to                 the recipient's email address
     * @param subject            the subject of the email
     * @param body               the body of the email
     * @param attachmentName     the name of the attachment
     * @param attachmentData     the data of the attachment
     * @param attachmentMimeType the MIME type of the attachment
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendEmailWithAttachment(EmailAddress to, String subject, String body,
                                    String attachmentName, byte[] attachmentData, String attachmentMimeType);

    /**
     * Sends an email with an attachment to multiple recipients.
     *
     * @param to                 the recipients' email addresses
     * @param subject            the subject of the email
     * @param body               the body of the email
     * @param attachmentName     the name of the attachment
     * @param attachmentData     the data of the attachment
     * @param attachmentMimeType the MIME type of the attachment
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendEmailWithAttachment(List<EmailAddress> to, String subject, String body,
                                    String attachmentName, byte[] attachmentData, String attachmentMimeType);

    /**
     * Sends an email with multiple attachments to a single recipient.
     *
     * @param to                  the recipient's email address
     * @param subject             the subject of the email
     * @param body                the body of the email
     * @param attachmentNames     the names of the attachments
     * @param attachmentData      the data of the attachments
     * @param attachmentMimeTypes the MIME types of the attachments
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendEmailWithAttachments(EmailAddress to, String subject, String body,
                                     List<String> attachmentNames, List<byte[]> attachmentData,
                                     List<String> attachmentMimeTypes);

    /**
     * Sends an email with multiple attachments to multiple recipients.
     *
     * @param to                  the recipients' email addresses
     * @param subject             the subject of the email
     * @param body                the body of the email
     * @param attachmentNames     the names of the attachments
     * @param attachmentData      the data of the attachments
     * @param attachmentMimeTypes the MIME types of the attachments
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendEmailWithAttachments(List<EmailAddress> to, String subject, String body,
                                     List<String> attachmentNames, List<byte[]> attachmentData,
                                     List<String> attachmentMimeTypes);
}