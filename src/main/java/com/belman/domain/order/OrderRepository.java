package com.belman.domain.order;

import com.belman.domain.specification.Specification;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OrderBusiness aggregate.
 * Integrates the Specification pattern for querying.
 */
public interface OrderRepository {
    Optional<OrderBusiness> findById(OrderId id);

    List<OrderBusiness> findAll();

    List<OrderBusiness> findBySpecification(Specification<OrderBusiness> spec);

    void save(OrderBusiness orderBusiness);

    /**
     * Finds an order by its order number.
     *
     * @param orderNumber the order number
     * @return an Optional containing the order if found, or empty if not found
     */
    Optional<OrderBusiness> findByOrderNumber(OrderNumber orderNumber);
}
