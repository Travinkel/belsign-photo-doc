package com.belman.infrastructure.persistence;

import com.belman.domain.aggregates.Order;
import com.belman.domain.repositories.OrderRepository;
import com.belman.domain.specification.Specification;
import com.belman.domain.valueobjects.OrderId;
import com.belman.domain.valueobjects.OrderNumber;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the OrderRepository interface.
 * This implementation stores orders in memory and is suitable for development and testing.
 * In a production environment, this would be replaced with a database-backed implementation.
 */
public class InMemoryOrderRepository implements OrderRepository {
    private final Map<OrderId, Order> ordersById = new HashMap<>();
    private final Map<OrderNumber, Order> ordersByNumber = new HashMap<>();

    @Override
    public Order findById(OrderId id) {
        return ordersById.get(id);
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(ordersById.values());
    }

    @Override
    public List<Order> findBySpecification(Specification<Order> spec) {
        return ordersById.values().stream()
                .filter(spec::isSatisfiedBy)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Order order) {
        ordersById.put(order.getId(), order);
        ordersByNumber.put(order.getOrderNumber(), order);
    }

    public Optional<Order> findByOrderNumber(OrderNumber orderNumber) {
        return Optional.ofNullable(ordersByNumber.get(orderNumber));
    }
}
