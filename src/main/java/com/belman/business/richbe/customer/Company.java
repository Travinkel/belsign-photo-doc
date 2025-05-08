package com.belman.business.richbe.customer;

/**
 * Value object representing a company in the customer context.
 */
public record Company(String name, String address, String vatNumber) {

    /**
     * Creates a new Company with the specified details.
     *
     * @param name      the company name
     * @param address   the company address
     * @param vatNumber the company VAT number (optional, can be null)
     * @throws IllegalArgumentException if name or address is null or blank
     */
    public Company {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Company name must not be null or blank");
        }

        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Company address must not be null or blank");
        }

        // VAT number is optional, can be null
    }
}