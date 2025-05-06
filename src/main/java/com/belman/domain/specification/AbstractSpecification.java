package com.belman.domain.specification;

/**
 * Abstract base class for all specifications following the specification pattern.
 * Specifications are used to validate business rules against domain objects.
 *
 * @param <T> the type of object that this specification can be applied to
 */
public abstract class AbstractSpecification<T> implements Specification<T> {

    /**
     * Checks if the given candidate satisfies this specification.
     *
     * @param candidate the candidate object to test
     * @return true if the candidate satisfies the specification, otherwise false
     */
    public abstract boolean isSatisfiedBy(T candidate);

    /**
     * Creates a new specification that is the logical AND of this specification and the given one.
     *
     * @param other the other specification to AND with
     * @return a new composite specification
     */
    public Specification<T> and(final Specification<T> other) {
        return new AndSpecification<>(this, other);
    }

    /**
     * Creates a new specification that is the logical OR of this specification and the given one.
     *
     * @param other the other specification to OR with
     * @return a new composite specification
     */
    public Specification<T> or(final Specification<T> other) {
        return new OrSpecification<>(this, other);
    }

    /**
     * Creates a new specification that is the logical NOT of this specification.
     *
     * @return a new negated specification
     */
    public Specification<T> not() {
        return new NotSpecification<>(this);
    }

    /**
     * Inner class representing an AND composite specification.
     *
     * @param <T> the type of object that this specification can be applied to
     */
    private static class AndSpecification<T> extends AbstractSpecification<T> {
        private final Specification<T> left;
        private final Specification<T> right;

        public AndSpecification(final Specification<T> left, final Specification<T> right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean isSatisfiedBy(T candidate) {
            return left.isSatisfiedBy(candidate) && right.isSatisfiedBy(candidate);
        }
    }

    /**
     * Inner class representing an OR composite specification.
     *
     * @param <T> the type of object that this specification can be applied to
     */
    private static class OrSpecification<T> extends AbstractSpecification<T> {
        private final Specification<T> left;
        private final Specification<T> right;

        public OrSpecification(final Specification<T> left, final Specification<T> right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean isSatisfiedBy(T candidate) {
            return left.isSatisfiedBy(candidate) || right.isSatisfiedBy(candidate);
        }
    }

    /**
     * Inner class representing a NOT specification.
     *
     * @param <T> the type of object that this specification can be applied to
     */
    private static class NotSpecification<T> extends AbstractSpecification<T> {
        private final Specification<T> wrapped;

        public NotSpecification(final Specification<T> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public boolean isSatisfiedBy(T candidate) {
            return !wrapped.isSatisfiedBy(candidate);
        }
    }
}