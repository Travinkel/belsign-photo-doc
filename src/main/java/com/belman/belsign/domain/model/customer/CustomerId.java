package com.belman.belsign.domain.model.customer;

import java.util.UUID;
import java.util.Objects;

/**
 * Value object wrapping a unique Customer identifier.
 */
public final class CustomerId {
    private final UUID id;

    public CustomerId(UUID id) {
        this.id = Objects.requireNonNull(id, "id must not be null");
    }

    /**
     * Generates a new random CustomerId.
     */
    public static CustomerId newId() {
        return new CustomerId(UUID.randomUUID());
    }

    public UUID toUUID() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerId customerId = (CustomerId) o;
        return id.equals(customerId.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}