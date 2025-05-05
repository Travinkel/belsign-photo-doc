package com.belman.domain.specification;


import com.belman.domain.aggregates.Order;

/**
 * Specification that matches orders having any pending (unapproved) photos.
 */
public final class PendingOrdersSpecification implements Specification<Order> {

    @Override
    public boolean isSatisfiedBy(Order order) {
        return !order.getPendingPhotos().isEmpty();
    }
}
