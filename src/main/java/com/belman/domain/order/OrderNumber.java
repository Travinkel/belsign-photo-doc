package com.belman.domain.order;

import com.belman.domain.common.base.ValueObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.security.SecureRandom;

/**
 * Value object representing an order number in the BelSign system.
 * OrderBusiness numbers follow a specific format: "ORD-XX-YYMMDD-ZZZ-NNNN"
 * where:
 * - XX is a project identifier
 * - YYMMDD is the date in year-month-day format
 * - ZZZ is a project code
 * - NNNN is a sequence number
 * <p>
 * This is specific to the order bounded context.
 */
public record OrderNumber(String value) implements ValueObject {

    /**
     * Creates a new OrderNumber with the specified value.
     *
     * @param value the order number value
     * @throws IllegalArgumentException if the value is null or empty
     */
    public OrderNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrderBusiness number must not be null or blank");
        }

        // Only accept the new format (ORD-XX-YYMMDD-ZZZ-NNNN)
        if (!value.matches("ORD-\\d{2}-\\d{6}-[A-Z]{3}-\\d{4}")) {
            throw new IllegalArgumentException("Invalid order number format: " + value +
                                               ". Expected format: ORD-XX-YYMMDD-ZZZ-NNNN");
        }
    }

    /**
     * Generates a new order number based on the current date and a project code.
     *
     * @param projectCode the project code (typically a 3-letter code)
     * @return a new OrderNumber
     */
    public static OrderNumber generate(String projectCode) {
        // Format: ORD-XX-YYMMDD-ZZZ-NNNN
        LocalDate now = LocalDate.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyMMdd"));

        // Default project identifier to 78 as per the example
        String projectId = "78";

        // Ensure project code is uppercase and exactly 3 letters
        String formattedProjectCode = projectCode.toUpperCase();
        if (formattedProjectCode.length() != 3) {
            formattedProjectCode = String.format("%-3s", formattedProjectCode).replace(' ', 'X').substring(0, 3);
        }

        // Generate a random 4-digit sequence number
        // In a real system, this would be managed by a sequence generator
        SecureRandom random = new SecureRandom();
        String sequence = String.format("%04d", random.nextInt(10000));

        return new OrderNumber("ORD-" + projectId + "-" + dateStr + "-" + formattedProjectCode + "-" + sequence);
    }

    /**
     * Extracts the project identifier portion of the order number.
     *
     * @return the project identifier portion (e.g., "78")
     */
    public String getProjectIdentifier() {
        return value.substring(4, 6);
    }

    /**
     * Extracts the date portion of the order number.
     *
     * @return the date portion (e.g., "230625")
     */
    public String getDateCode() {
        return value.substring(7, 13);
    }

    /**
     * Extracts the project code portion of the order number.
     *
     * @return the project code portion (e.g., "PIP")
     */
    public String getProjectCode() {
        return value.substring(14, 17);
    }

    /**
     * Extracts the sequence number portion of the order number.
     *
     * @return the sequence number portion (e.g., "0003")
     */
    public String getSequenceNumber() {
        return value.substring(18);
    }
}
