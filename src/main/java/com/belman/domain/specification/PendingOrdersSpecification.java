package com.belman.domain.specification;


import com.belman.domain.order.OrderBusiness;

/**
 * Specification that matches orders having any pending (unapproved) photos.
 */
public final class PendingOrdersSpecification implements Specification<OrderBusiness> {

    @Override
    public boolean isSatisfiedBy(OrderBusiness orderBusiness) {
        return !orderBusiness.getPendingPhotos().isEmpty();
    }

    @Override
    public Specification<OrderBusiness> and(Specification<OrderBusiness> other) {
        return null;
    }

    @Override
    public Specification<OrderBusiness> or(Specification<OrderBusiness> other) {
        return null;
    }

    @Override
    public Specification<OrderBusiness> not() {
        return null;
    }
}
