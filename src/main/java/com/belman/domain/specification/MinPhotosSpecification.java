package com.belman.domain.specification;


/**
 * Specification that matches orders with at least a minimum number of photos.
 */
public final class MinPhotosSpecification implements Specification<OrderAggregate> {
    private final int minPhotos;

    public MinPhotosSpecification(int minPhotos) {
        this.minPhotos = minPhotos;
    }

    @Override
    public boolean isSatisfiedBy(OrderAggregate orderAggregate) {
        return orderAggregate.getPhotos().size() >= minPhotos;
    }
}
