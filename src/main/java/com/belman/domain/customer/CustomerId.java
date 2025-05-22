package com.belman.domain.customer;

import com.belman.domain.common.base.ValueObject;

import java.util.UUID;

/**
 * Value object representing a unique identifier for a customer.
 * This is specific to the customer bounded context.
 */
public record CustomerId(String id) implements ValueObject {

    /**
     * Creates a new CustomerId with the specified ID.
     * The ID is normalized to lowercase to ensure consistent comparison across all storage backends.
     *
     * @param id the ID value
     * @throws IllegalArgumentException if the ID is null or empty
     */
    public CustomerId {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Customer ID must not be null or blank");
        }
        // Normalize UUID to lowercase for consistent comparison
        id = id.toLowerCase();
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
