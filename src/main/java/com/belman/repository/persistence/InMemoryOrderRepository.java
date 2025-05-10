package com.belman.repository.persistence;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.specification.Specification;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the OrderRepository interface.
 * This implementation stores orders in memory and is suitable for development and testing.
 * In a production environment, this would be replaced with a database-backed implementation.
 */
public class InMemoryOrderRepository implements OrderRepository {
    private final Map<OrderId, OrderBusiness> ordersById = new HashMap<>();
    private final Map<OrderNumber, OrderBusiness> ordersByNumber = new HashMap<>();

    @Override
    public Optional<OrderBusiness> findById(OrderId id) {
        return Optional.ofNullable(ordersById.get(id));
    }

    @Override
    public List<OrderBusiness> findAll() {
        return new ArrayList<>(ordersById.values());
    }

    @Override
    public List<OrderBusiness> findBySpecification(Specification<OrderBusiness> spec) {
        return ordersById.values().stream()
                .filter(spec::isSatisfiedBy)
                .collect(Collectors.toList());
    }

    @Override
    public void save(OrderBusiness orderBusiness) {
        ordersById.put(orderBusiness.getId(), orderBusiness);
        ordersByNumber.put(orderBusiness.getOrderNumber(), orderBusiness);
    }

    public Optional<OrderBusiness> findByOrderNumber(OrderNumber orderNumber) {
        return Optional.ofNullable(ordersByNumber.get(orderNumber));
    }
}
