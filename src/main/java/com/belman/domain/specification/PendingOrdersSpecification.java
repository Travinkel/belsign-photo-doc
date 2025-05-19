package com.belman.domain.specification;


import com.belman.domain.order.OrderBusiness;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoRepository;

/**
 * Specification that matches orders having any pending (unapproved) photos.
 */
public final class PendingOrdersSpecification implements Specification<OrderBusiness> {

    private final PhotoRepository photoRepository;

    /**
     * Creates a new PendingOrdersSpecification with the specified PhotoRepository.
     *
     * @param photoRepository the photo repository
     */
    public PendingOrdersSpecification(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    @Override
    public boolean isSatisfiedBy(OrderBusiness orderBusiness) {
        return !photoRepository.findByOrderIdAndStatus(orderBusiness.getId(), PhotoDocument.ApprovalStatus.PENDING).isEmpty();
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
