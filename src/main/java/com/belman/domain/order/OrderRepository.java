package com.belman.domain.order;

import com.belman.domain.specification.Specification;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OrderAggregate aggregate.
 * Integrates the Specification pattern for querying.
 */
public interface OrderRepository {
    Optional<OrderAggregate> findById(OrderId id);
    List<OrderAggregate> findAll();
    List<OrderAggregate> findBySpecification(Specification<OrderAggregate> spec);
    void save(OrderAggregate orderAggregate);

    /**
     * Finds an order by its order number.
     *
     * @param orderNumber the order number
     * @return an Optional containing the order if found, or empty if not found
     */
    Optional<OrderAggregate> findByOrderNumber(OrderNumber orderNumber);
}
