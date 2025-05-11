package com.belman.domain.order;

import com.belman.domain.core.Repository;
import com.belman.domain.specification.Specification;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OrderBusiness business object.
 * Integrates the Specification pattern for querying.
 */
public interface OrderRepository extends Repository<OrderBusiness, OrderId> {
    /**
     * Finds orders that satisfy the given specification.
     *
     * @param spec the specification to filter orders
     * @return a list of orders that satisfy the specification
     */
    List<OrderBusiness> findBySpecification(Specification<OrderBusiness> spec);

    /**
     * Finds an order by its order number.
     *
     * @param orderNumber the order number
     * @return an Optional containing the order if found, or empty if not found
     */
    Optional<OrderBusiness> findByOrderNumber(OrderNumber orderNumber);
}