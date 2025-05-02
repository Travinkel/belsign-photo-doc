package com.belman.domain.valueobjects;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Value object representing a business order number in the BelSign system.
 * 
 * The order number follows a specific format: XX/XX-XXXXXX-XXXXXXXX, where:
 * - The first part (XX) represents the department code (1-2 digits)
 * - The second part (XX) represents the year (2 digits)
 * - The third part (XXXXXX) represents the customer code (6 digits)
 * - The fourth part (XXXXXXXX) represents the sequential order number (8 digits)
 * 
 * For example: "01/23-456789-12345678"
 * 
 * This format ensures that each order has a unique, structured identifier that
 * provides information about its origin and chronology.
 */
public record OrderNumber(String value) {
    private static final Pattern VALID_PATTERN = Pattern.compile("(\\d{1,2})/(\\d{2})-(\\d{6})-(\\d{8})");

    /**
     * Creates an OrderNumber with the specified value.
     * 
     * @param value the order number string
     * @throws NullPointerException if value is null
     * @throws IllegalArgumentException if value doesn't match the required pattern
     */
    public OrderNumber {
        Objects.requireNonNull(value, "OrderNumber must not be null");
        if (!VALID_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("OrderNumber must match the pattern 'XX/XX-XXXXXX-XXXXXXXX'");
        }
    }

    /**
     * Creates an OrderNumber from its component parts.
     * 
     * @param departmentCode the department code (1-2 digits)
     * @param year the year (2 digits)
     * @param customerCode the customer code (6 digits)
     * @param sequentialNumber the sequential order number (8 digits)
     * @return a new OrderNumber
     * @throws IllegalArgumentException if any component doesn't match the required format
     */
    public static OrderNumber of(int departmentCode, int year, int customerCode, int sequentialNumber) {
        if (departmentCode < 1 || departmentCode > 99) {
            throw new IllegalArgumentException("Department code must be between 1 and 99");
        }
        if (year < 0 || year > 99) {
            throw new IllegalArgumentException("Year must be between 0 and 99");
        }
        if (customerCode < 0 || customerCode > 999999) {
            throw new IllegalArgumentException("Customer code must be between 0 and 999999");
        }
        if (sequentialNumber < 0 || sequentialNumber > 99999999) {
            throw new IllegalArgumentException("Sequential number must be between 0 and 99999999");
        }

        String formattedValue = String.format("%02d/%02d-%06d-%08d", 
            departmentCode, year, customerCode, sequentialNumber);
        return new OrderNumber(formattedValue);
    }

    /**
     * Returns the department code part of this order number.
     * 
     * @return the department code (1-2 digits)
     */
    public int getDepartmentCode() {
        Matcher matcher = VALID_PATTERN.matcher(value);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0; // Should never happen due to validation in constructor
    }

    /**
     * Returns the year part of this order number.
     * 
     * @return the year (2 digits)
     */
    public int getYear() {
        Matcher matcher = VALID_PATTERN.matcher(value);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(2));
        }
        return 0; // Should never happen due to validation in constructor
    }

    /**
     * Returns the customer code part of this order number.
     * 
     * @return the customer code (6 digits)
     */
    public int getCustomerCode() {
        Matcher matcher = VALID_PATTERN.matcher(value);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(3));
        }
        return 0; // Should never happen due to validation in constructor
    }

    /**
     * Returns the sequential number part of this order number.
     * 
     * @return the sequential number (8 digits)
     */
    public int getSequentialNumber() {
        Matcher matcher = VALID_PATTERN.matcher(value);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(4));
        }
        return 0; // Should never happen due to validation in constructor
    }

    @Override
    public String toString() {
        return value;
    }
}
