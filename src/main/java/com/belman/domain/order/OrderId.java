package com.belman.domain.order;

import com.belman.domain.common.base.ValueObject;

import java.util.UUID;

/**
 * Value object representing a unique identifier for an order.
 * This is specific to the order bounded context.
 */
public record OrderId(String id) implements ValueObject {

    /**
     * Creates a new OrderId with the specified ID.
     *
     * @param id the ID value
     * @throws IllegalArgumentException if the ID is null or empty
     */
    public OrderId {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("OrderBusiness ID must not be null or blank");
        }
    }

    /**
     * Creates a new OrderId with a random UUID.
     *
     * @return a new OrderId with a random UUID
     */
    public static OrderId newId() {
        return new OrderId(UUID.randomUUID().toString());
    }
}
