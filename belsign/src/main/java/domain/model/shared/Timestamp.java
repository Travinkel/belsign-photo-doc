package domain.model.shared;

import java.time.Instant;

/**
 * Value object wrapping a point in time.
 */
public final class Timestamp {
    private final Instant instant;

    public Timestamp(Instant instant) {
        this.instant = instant;
    }

    /**
     * Creates a Timestamp representing the current moment.
     */
    public static Timestamp now() {
        return new Timestamp(Instant.now());
    }

    public Instant toInstant() {
        return instant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timestamp timestamp = (Timestamp) o;
        return timestamp.instant.equals(this.instant);
    }

    @Override
    public int hashCode() {
        return instant.hashCode();
    }
}
