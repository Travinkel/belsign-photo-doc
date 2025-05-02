package com.belman.domain.repositories;

import com.belman.domain.aggregates.Order;
import com.belman.domain.specification.Specification;
import com.belman.domain.valueobjects.OrderId;
import com.belman.domain.valueobjects.OrderNumber;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order aggregate.
 * Integrates the Specification pattern for querying.
 */
public interface OrderRepository {
    Order findById(OrderId id);
    List<Order> findAll();
    List<Order> findBySpecification(Specification<Order> spec);
    void save(Order order);

    /**
     * Finds an order by its order number.
     *
     * @param orderNumber the order number
     * @return an Optional containing the order if found, or empty if not found
     */
    Optional<Order> findByOrderNumber(OrderNumber orderNumber);
}
