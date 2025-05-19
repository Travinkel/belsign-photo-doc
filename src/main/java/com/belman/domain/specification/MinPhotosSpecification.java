package com.belman.domain.specification;


import com.belman.domain.order.OrderBusiness;
import com.belman.domain.photo.PhotoRepository;

/**
 * Specification that matches orders with at least a minimum number of photos.
 */
public final class MinPhotosSpecification implements Specification<OrderBusiness> {
    private final int minPhotos;
    private final PhotoRepository photoRepository;

    /**
     * Creates a new MinPhotosSpecification with the specified minimum number of photos and PhotoRepository.
     *
     * @param minPhotos the minimum number of photos
     * @param photoRepository the photo repository
     */
    public MinPhotosSpecification(int minPhotos, PhotoRepository photoRepository) {
        this.minPhotos = minPhotos;
        this.photoRepository = photoRepository;
    }

    @Override
    public boolean isSatisfiedBy(OrderBusiness orderBusiness) {
        return photoRepository.findByOrderId(orderBusiness.getId()).size() >= minPhotos;
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
