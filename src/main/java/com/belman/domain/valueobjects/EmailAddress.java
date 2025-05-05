package com.belman.domain.valueobjects;

import java.util.regex.Pattern;

/**
 * Value object representing an email address in the BelSign system.
 * 
 * This value object encapsulates an email address and ensures it is valid
 * according to RFC 5322 standards. It provides validation to ensure that
 * only properly formatted email addresses are accepted.
 * 
 * Email addresses are used throughout the system for user identification
 * and communication, particularly for sending QC reports to customers.
 */
public record EmailAddress(String value) {
    // RFC 5322 compliant email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    );

    /**
     * Creates an EmailAddress with the specified value.
     * 
     * @param value the email address string
     * @throws IllegalArgumentException if the email address is null or invalid
     */
    public EmailAddress {
        if (value == null) {
            throw new IllegalArgumentException("Email address cannot be null");
        }

        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            throw new IllegalArgumentException("Email address cannot be empty");
        }

        if (!EMAIL_PATTERN.matcher(trimmedValue).matches()) {
            throw new IllegalArgumentException("Invalid email address format: " + value);
        }
    }

    /**
     * Returns the local part of the email address (the part before the @ symbol).
     * 
     * @return the local part of the email address
     */
    public String getLocalPart() {
        int atIndex = value.indexOf('@');
        return atIndex > 0 ? value.substring(0, atIndex) : "";
    }

    /**
     * Returns the domain part of the email address (the part after the @ symbol).
     * 
     * @return the domain part of the email address
     */
    public String getDomainPart() {
        int atIndex = value.indexOf('@');
        return atIndex > 0 && atIndex < value.length() - 1 ? value.substring(atIndex + 1) : "";
    }

    /**
     * @return the email address string
     * @deprecated Use value() instead
     */
    @Deprecated
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
