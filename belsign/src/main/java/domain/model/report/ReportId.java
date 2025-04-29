package domain.model.report;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object wrapping a unique Report identifier.
 */
public record ReportId(UUID id) {
    /**
     * Creates a ReportId with the specified UUID.
     * 
     * @param id the UUID for this report ID
     * @throws NullPointerException if id is null
     */
    public ReportId {
        Objects.requireNonNull(id, "id must not be null");
    }

    /**
     * Generates a new random ReportId.
     */
    public static ReportId newId() {
        return new ReportId(UUID.randomUUID());
    }

    /**
     * @return the UUID representation of this report ID
     */
    public UUID toUUID() {
        return id;
    }
}