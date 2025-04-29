package com.belman.belsign.domain.model.customer;

import java.util.regex.Pattern;

/**
 * Value object representing a phone number.
 */
public record PhoneNumber(String value) {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\s()-]{8,20}$");
    
    /**
     * Creates a PhoneNumber with the specified value.
     * 
     * @param value the phone number string
     * @throws IllegalArgumentException if the phone number is invalid
     */
    public PhoneNumber {
        if (value == null || !PHONE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }
    
    /**
     * @return a formatted version of the phone number
     */
    public String getFormatted() {
        // Simple formatting - could be enhanced for different country formats
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}