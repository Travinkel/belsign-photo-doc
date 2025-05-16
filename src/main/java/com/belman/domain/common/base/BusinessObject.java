package com.belman.domain.common.base;



import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Base class for all business objects in the business model.
 * <p>
 * A business object is an entity that acts as the entry point to a group of related components
 * (a cluster of business objects that are treated as a single unit for data changes).
 * Business objects are responsible for enforcing invariants and consistency rules
 * within the group and emitting business events that represent significant changes to
 * the object's state.
 *
 * @param <ID> the type of identifier used by this business object
 */
public abstract class BusinessObject<ID> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Instant lastModifiedAt;

    /**
     * Updates the last modified timestamp of the business object.
     */
    protected void updateLastModifiedAt() {
        this.lastModifiedAt = Instant.now();
    }


    /**
     * Gets the last modified timestamp of the business object.
     *
     * @return the last modified timestamp
     */
    public Instant getLastModifiedAt() {
        return this.lastModifiedAt;
    }

    /**
     * Computes the hash code based on the business object's identifier.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    /**
     * Checks equality based on the business object's identifier.
     *
     * @param obj the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BusinessObject<?> that = (BusinessObject<?>) obj;
        return Objects.equals(getId(), that.getId());
    }

    /**
     * Returns the identifier of this business object.
     * The identifier must be unique within the object's type.
     *
     * @return the business object's identifier
     */
    public abstract ID getId();
}
