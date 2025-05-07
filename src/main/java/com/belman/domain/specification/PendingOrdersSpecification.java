package com.belman.domain.specification;


/**
 * Specification that matches orders having any pending (unapproved) photos.
 */
public final class PendingOrdersSpecification implements Specification<OrderAggregate> {

    @Override
    public boolean isSatisfiedBy(OrderAggregate orderAggregate) {
        return !orderAggregate.getPendingPhotos().isEmpty();
    }
}
