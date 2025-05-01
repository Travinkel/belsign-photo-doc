package com.belman.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object wrapping a unique Order identifier.
 */
public record OrderId(UUID id) {
    /**
     * Creates an OrderId with the specified UUID.
     * 
     * @param id the UUID for this order ID
     * @throws NullPointerException if id is null
     */
    public OrderId {
        Objects.requireNonNull(id, "id must not be null");
    }

    /**
     * Generates a new random OrderId.
     */
    public static OrderId newId() {
        return new OrderId(UUID.randomUUID());
    }

    /**
     * @return the UUID representation of this order ID
     */
    public UUID toUUID() {
        return id;
    }
}
