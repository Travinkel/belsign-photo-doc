package com.belman.domain.common.valueobjects;

import com.belman.domain.common.base.ValueObject;

import java.util.regex.Pattern;

/**
 * Value object representing a phone number.
 * This is in the common package as it's used across multiple bounded contexts.
 */
public record PhoneNumber(String value) implements ValueObject {
    // Regular expression for validating phone numbers
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[0-9\\-\\s]{8,15}$"
    );

    /**
     * Creates a new PhoneNumber with the specified value.
     *
     * @param value the phone number value
     * @throws IllegalArgumentException if the value is null, empty, or not a valid phone number
     */
    public PhoneNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Phone number must not be null or blank");
        }

        if (!PHONE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid phone number format: " + value);
        }
    }

    /**
     * Returns the phone number with all non-digit characters removed.
     *
     * @return the normalized phone number
     */
    public String getNormalized() {
        return value.replaceAll("[^0-9]", "");
    }
}
