package com.belman.business.richbe.common.base;

/**
 * Marker interface for data objects in the business model.
 * <p>
 * A data object is an immutable object that represents a descriptive aspect of the business
 * with no conceptual identity. Data objects are defined by their attributes rather than
 * by a distinct identity.
 * <p>
 * Data objects should:
 * - Be immutable (all fields should be final)
 * - Have structural equality (two data objects are equal if all their attributes are equal)
 * - Have no side effects
 * - Be self-validating (validate their invariants in the constructor)
 */
public interface DataObject {
    // Marker interface - no methods required
}