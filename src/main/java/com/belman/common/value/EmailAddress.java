package com.belman.common.value;

import com.belman.common.value.base.DataObject;

import java.util.regex.Pattern;

/**
 * Value object representing an email address.
 * This is in the common package as it's used across multiple bounded contexts.
 */
public record EmailAddress(String value) implements DataObject {
    // Regular expression for validating email addresses
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    /**
     * Creates a new EmailAddress with the specified value.
     *
     * @param value the email address value
     * @throws IllegalArgumentException if the value is null, empty, or not a valid email address
     */
    public EmailAddress {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email address must not be null or blank");
        }

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email address format: " + value);
        }
    }

    /**
     * Returns the domain part of the email address (everything after the @ symbol).
     *
     * @return the domain part of the email address
     */
    public String getDomain() {
        int atIndex = value.indexOf('@');
        return value.substring(atIndex + 1);
    }

    /**
     * Returns the local part of the email address (everything before the @ symbol).
     *
     * @return the local part of the email address
     */
    public String getLocalPart() {
        int atIndex = value.indexOf('@');
        return value.substring(0, atIndex);
    }
}