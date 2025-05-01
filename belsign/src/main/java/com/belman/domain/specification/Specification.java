package com.belman.domain.specification;

/**
 * Generic specification interface for filtering or querying domain entities.
 * This is a sealed interface to control which classes can implement it.
 */
public sealed interface Specification<T> permits MinPhotosSpecification, PendingOrdersSpecification {
    /**
     * Determines if the given entity satisfies the specification.
     */
    boolean isSatisfiedBy(T entity);
}
