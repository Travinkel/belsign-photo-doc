package com.belman.domain.specification;


import com.belman.domain.order.OrderBusiness;

/**
 * Specification that matches orders with at least a minimum number of photos.
 */
public final class MinPhotosSpecification implements Specification<OrderBusiness> {
    private final int minPhotos;

    public MinPhotosSpecification(int minPhotos) {
        this.minPhotos = minPhotos;
    }

    @Override
    public boolean isSatisfiedBy(OrderBusiness orderBusiness) {
        return orderBusiness.getPhotos().size() >= minPhotos;
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
