package domain.model.order.photodocument;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object wrapping a unique Photo identifier.
 */
public record PhotoId(UUID value) {
    /**
     * Creates a PhotoId with the specified UUID.
     * 
     * @param value the UUID for this photo ID
     * @throws NullPointerException if value is null
     */
    public PhotoId {
        Objects.requireNonNull(value, "value must not be null");
    }

    /**
     * Generates a new random PhotoId.
     */
    public static PhotoId newId() {
        return new PhotoId(UUID.randomUUID());
    }
}
