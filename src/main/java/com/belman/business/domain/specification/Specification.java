package com.belman.business.domain.specification;

/**
 * Interface defining the Specification pattern.
 * A specification is a predicate that determines if an object meets certain criteria.
 *
 * @param <T> the type of object that this specification can be applied to
 */
public interface Specification<T> {

    /**
     * Checks if the given candidate satisfies this specification.
     *
     * @param candidate the candidate object to test
     * @return true if the candidate satisfies the specification, otherwise false
     */
    boolean isSatisfiedBy(T candidate);

    /**
     * Creates a new specification that is the logical AND of this specification and the given one.
     *
     * @param other the other specification to AND with
     * @return a new composite specification
     */
    Specification<T> and(Specification<T> other);

    /**
     * Creates a new specification that is the logical OR of this specification and the given one.
     *
     * @param other the other specification to OR with
     * @return a new composite specification
     */
    Specification<T> or(Specification<T> other);

    /**
     * Creates a new specification that is the logical NOT of this specification.
     *
     * @return a new negated specification
     */
    Specification<T> not();
}