package com.belman.domain.customer;

import java.util.UUID;

/**
 * Value object representing a unique identifier for a customer.
 * This is specific to the customer bounded context.
 */
public record CustomerId(String id) {

    /**
     * Creates a new CustomerId with the specified ID.
     *
     * @param id the ID value
     * @throws IllegalArgumentException if the ID is null or empty
     */
    public CustomerId {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Customer ID must not be null or blank");
        }
    }

    /**
     * Creates a new CustomerId with a random UUID.
     *
     * @return a new CustomerId with a random UUID
     */
    public static CustomerId newId() {
        return new CustomerId(UUID.randomUUID().toString());
    }
}