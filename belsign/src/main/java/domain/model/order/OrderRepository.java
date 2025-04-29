package domain.model.order;

import com.belman.belsign.domain.model.order.Order;
import domain.specification.Specification;


import java.util.List;

/**
 * Repository interface for Order aggregate.
 * Integrates the Specification pattern for querying.
 */
public interface OrderRepository {
    Order findById(OrderId id);
    List<Order> findAll();
    List<Order> findBySpecification(Specification<Order> spec);
    void save(Order order);
}
