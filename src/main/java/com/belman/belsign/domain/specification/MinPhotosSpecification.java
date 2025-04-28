package com.belman.belsign.domain.specification;

import com.belman.belsign.domain.model.order.Order;

/**
 * Specification that matches orders with at least a minimum number of photos.
 */
public final class MinPhotosSpecification implements Specification<Order> {
    private final int minPhotos;

    public MinPhotosSpecification(int minPhotos) {
        this.minPhotos = minPhotos;
    }

    @Override
    public boolean isSatisfiedBy(Order order) {
        return order.getPhotos().size() >= minPhotos;
    }
}
