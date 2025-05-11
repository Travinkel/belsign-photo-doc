package com.belman.domain.core;

/**
 * Base class for all business components in the business model.
 * <p>
 * A business component is a business object that has a distinct identity that runs through
 * time and different states. Business components are primarily defined by their identity,
 * which remains constant throughout the life of the system.
 * <p>
 * Business components in the business model should be distinguishable from other components
 * by their identity, rather than their attributes.
 *
 * @param <ID> the type of identifier used by this business component
 */
public abstract class BusinessComponent<ID> {

    /**
     * Returns a hash code value for this business component.
     * The hash code is based on the component's ID and type.
     *
     * @return a hash code value for this business component
     */
    @Override
    public int hashCode() {
        return getId() == null ? 0 : getId().hashCode();
    }

    /**
     * Determines if this business component is equal to another object.
     * Two business components are considered equal if they have the same ID and are of the same type.
     *
     * @param obj the object to compare with
     * @return true if the specified object is equal to this business component; false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof BusinessComponent<?> other)) return false;

        // If either business component has a null ID, they cannot be equal
        if (getId() == null || other.getId() == null) return false;

        // Business components are equal if they have the same ID and are of the same type
        return getClass().equals(obj.getClass()) && getId().equals(other.getId());
    }

    /**
     * Returns a string representation of this business component.
     * The string representation includes the component's type and ID.
     *
     * @return a string representation of this business component
     */
    @Override
    public String toString() {
        return String.format("%s[id=%s]", getClass().getSimpleName(), getId());
    }

    /**
     * Returns the identifier of this business component.
     * The identifier must be unique within the component's type.
     *
     * @return the component's identifier
     */
    public abstract ID getId();
}