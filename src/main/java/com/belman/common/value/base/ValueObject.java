package com.belman.common.value.base;

/**
 * Marker interface for value objects in the domain model.
 * <p>
 * A value object is an immutable object that represents a descriptive aspect of the domain
 * with no conceptual identity. Value objects are defined by their attributes rather than
 * by a distinct identity.
 * <p>
 * Value objects should:
 * - Be immutable (all fields should be final)
 * - Have structural equality (two value objects are equal if all their attributes are equal)
 * - Have no side effects
 * - Be self-validating (validate their invariants in the constructor)
 */
public interface ValueObject {
    // Marker interface - no methods required
}