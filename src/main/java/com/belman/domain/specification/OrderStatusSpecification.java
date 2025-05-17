package com.belman.domain.specification;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderStatus;

/**
 * Specification that matches orders with a specific status.
 */
public final class OrderStatusSpecification extends AbstractSpecification<OrderBusiness> {
    private final OrderStatus status;

    /**
     * Creates a new OrderStatusSpecification with the specified status.
     *
     * @param status the status to match
     */
    public OrderStatusSpecification(OrderStatus status) {
        this.status = status;
    }

    @Override
    public boolean isSatisfiedBy(OrderBusiness orderBusiness) {
        return orderBusiness.getStatus() == status;
    }
}