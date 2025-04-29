package domain.specification;


import com.belman.belsign.domain.model.order.Order;
import com.belman.belsign.domain.specification.Specification;

/**
 * Specification that matches orders having any pending (unapproved) photos.
 */
public final class PendingOrdersSpecification implements Specification<Order> {

    @Override
    public boolean isSatisfiedBy(Order order) {
        return !order.getPendingPhotos().isEmpty();
    }
}
