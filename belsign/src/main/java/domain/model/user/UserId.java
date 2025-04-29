package domain.model.user;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object wrapping a unique User identifier.
 */
public record UserId(UUID id) {
    /**
     * Creates a UserId with the specified UUID.
     * 
     * @param id the UUID for this user ID
     * @throws NullPointerException if id is null
     */
    public UserId {
        Objects.requireNonNull(id, "id must not be null");
    }

    /**
     * Generates a new random UserId.
     */
    public static UserId newId() {
        return new UserId(UUID.randomUUID());
    }

    /**
     * @return the UUID representation of this user ID
     */
    public UUID toUUID() {
        return id;
    }
}