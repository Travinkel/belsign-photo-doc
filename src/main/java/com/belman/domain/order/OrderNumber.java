package com.belman.domain.order;

import com.belman.domain.common.base.ValueObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Value object representing an order number in the BelSign system.
 * OrderBusiness numbers follow a specific format: "MM/YY-CUSTOMER-SEQUENCE"
 * where:
 * - MM/YY is the month and year when the order was created
 * - CUSTOMER is a customer-specific identifier (often numeric)
 * - SEQUENCE is a unique sequence number for that month
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

        // Only accept the new format (MM/YY-CUSTOMER-SEQUENCE)
        if (!value.matches("\\d{2}/\\d{2}-\\d{6}-\\d{8}")) {
            throw new IllegalArgumentException("Invalid order number format: " + value +
                                               ". Expected format: MM/YY-CUSTOMER-SEQUENCE");
        }
    }

    /**
     * Generates a new order number based on the current date and a customer identifier.
     *
     * @param customerId the customer identifier (typically a 6-digit number)
     * @return a new OrderNumber
     */
    public static OrderNumber generate(String customerId) {
        // Format: MM/YY-CUSTOMER-SEQUENCE
        LocalDate now = LocalDate.now();
        String monthYear = now.format(DateTimeFormatter.ofPattern("MM/yy"));

        // Ensure customerId is padded to 6 digits
        String paddedCustomerId = String.format("%06d", Integer.parseInt(customerId));

        // Generate a random 8-digit sequence number
        // In a real system, this would be managed by a sequence generator
        Random random = new Random();
        String sequence = String.format("%08d", random.nextInt(100000000));

        return new OrderNumber(monthYear + "-" + paddedCustomerId + "-" + sequence);
    }

    /**
     * Extracts the month and year portion of the order number.
     *
     * @return the month and year portion (e.g., "01/23")
     */
    public String getMonthYear() {
        return value.substring(0, 5);
    }

    /**
     * Extracts the customer identifier portion of the order number.
     *
     * @return the customer identifier portion (e.g., "123456")
     */
    public String getCustomerIdentifier() {
        return value.substring(6, 12);
    }

    /**
     * Extracts the sequence number portion of the order number.
     *
     * @return the sequence number portion (e.g., "12345678")
     */
    public String getSequenceNumber() {
        return value.substring(13);
    }
}
