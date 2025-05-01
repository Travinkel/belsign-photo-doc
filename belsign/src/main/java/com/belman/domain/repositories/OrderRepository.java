package com.belman.domain.repositories;

import com.belman.domain.aggregates.Order;
import com.belman.domain.specification.Specification;
import com.belman.domain.valueobjects.OrderId;


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
