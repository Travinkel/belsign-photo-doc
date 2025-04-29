package domain.model.customer;

import java.util.UUID;
import java.util.Objects;

/**
 * Value object wrapping a unique Customer identifier.
 */
public record CustomerId(UUID id) {
    /**
     * Creates a CustomerId with the specified UUID.
     * 
     * @param id the UUID for this customer ID
     * @throws NullPointerException if id is null
     */
    public CustomerId {
        Objects.requireNonNull(id, "id must not be null");
    }

    /**
     * Generates a new random CustomerId.
     */
    public static CustomerId newId() {
        return new CustomerId(UUID.randomUUID());
    }

    /**
     * @return the UUID representation of this customer ID
     */
    public UUID toUUID() {
        return id;
    }
}
