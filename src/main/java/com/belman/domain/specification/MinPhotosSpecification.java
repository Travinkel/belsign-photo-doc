package com.belman.domain.specification;


import com.belman.domain.aggregates.Order;

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
