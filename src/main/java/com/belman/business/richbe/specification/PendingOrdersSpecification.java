package com.belman.business.richbe.specification;


import com.belman.business.richbe.order.OrderAggregate;

/**
 * Specification that matches orders having any pending (unapproved) photos.
 */
public final class PendingOrdersSpecification implements Specification<OrderAggregate> {

    @Override
    public boolean isSatisfiedBy(OrderAggregate orderAggregate) {
        return !orderAggregate.getPendingPhotos().isEmpty();
    }

    @Override
    public Specification<OrderAggregate> and(Specification<OrderAggregate> other) {
        return null;
    }

    @Override
    public Specification<OrderAggregate> or(Specification<OrderAggregate> other) {
        return null;
    }

    @Override
    public Specification<OrderAggregate> not() {
        return null;
    }
}
