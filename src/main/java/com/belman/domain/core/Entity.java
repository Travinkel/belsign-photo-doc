package com.belman.domain.core;

/**
 * Base class for all entities in the domain model.
 * <p>
 * An entity is a domain object that has a distinct identity that runs through
 * time and different states. Entities are primarily defined by their identity,
 * which remains constant throughout the life of the system.
 * <p>
 * Entities in the domain model should be distinguishable from other entities
 * by their identity, rather than their attributes.
 *
 * @param <ID> the type of identifier used by this entity
 */
public abstract class Entity<ID> {

    /**
     * Returns a hash code value for this entity.
     * The hash code is based on the entity's ID and type.
     *
     * @return a hash code value for this entity
     */
    @Override
    public int hashCode() {
        return getId() == null ? 0 : getId().hashCode();
    }

    /**
     * Determines if this entity is equal to another object.
     * Two entities are considered equal if they have the same ID and are of the same type.
     *
     * @param obj the object to compare with
     * @return true if the specified object is equal to this entity; false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Entity<?> other)) return false;

        // If either entity has a null ID, they cannot be equal
        if (getId() == null || other.getId() == null) return false;

        // Entities are equal if they have the same ID and are of the same type
        return getClass().equals(obj.getClass()) && getId().equals(other.getId());
    }

    /**
     * Returns the identifier of this entity.
     * The identifier must be unique within the entity's type.
     *
     * @return the entity's identifier
     */
    public abstract ID getId();

    /**
     * Returns a string representation of this entity.
     * The string representation includes the entity's type and ID.
     *
     * @return a string representation of this entity
     */
    @Override
    public String toString() {
        return String.format("%s[id=%s]", getClass().getSimpleName(), getId());
    }
}