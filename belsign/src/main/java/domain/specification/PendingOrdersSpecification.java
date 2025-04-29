package domain.specification;


import domain.model.order.Order;

/**
 * Specification that matches orders having any pending (unapproved) photos.
 */
public final class PendingOrdersSpecification implements Specification<Order> {

    @Override
    public boolean isSatisfiedBy(Order order) {
        return !order.getPendingPhotos().isEmpty();
    }
}
