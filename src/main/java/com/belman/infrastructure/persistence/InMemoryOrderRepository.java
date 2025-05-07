package com.belman.infrastructure.persistence;

import com.belman.domain.order.OrderRepository;
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
    private final Map<OrderId, OrderAggregate> ordersById = new HashMap<>();
    private final Map<OrderNumber, OrderAggregate> ordersByNumber = new HashMap<>();

    @Override
    public OrderAggregate findById(OrderId id) {
        return ordersById.get(id);
    }

    @Override
    public List<OrderAggregate> findAll() {
        return new ArrayList<>(ordersById.values());
    }

    @Override
    public List<OrderAggregate> findBySpecification(Specification<OrderAggregate> spec) {
        return ordersById.values().stream()
                .filter(spec::isSatisfiedBy)
                .collect(Collectors.toList());
    }

    @Override
    public void save(OrderAggregate orderAggregate) {
        ordersById.put(orderAggregate.getId(), orderAggregate);
        ordersByNumber.put(orderAggregate.getOrderNumber(), orderAggregate);
    }

    public Optional<OrderAggregate> findByOrderNumber(OrderNumber orderNumber) {
        return Optional.ofNullable(ordersByNumber.get(orderNumber));
    }
}
