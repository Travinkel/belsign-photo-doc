package com.belman.dataaccess.persistence.memory;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.photo.PhotoTemplateRepository;
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
    public OrderBusiness save(OrderBusiness orderBusiness) {
        ordersById.put(orderBusiness.getId(), orderBusiness);
        if (orderBusiness.getOrderNumber() != null) {
            ordersByNumber.put(orderBusiness.getOrderNumber(), orderBusiness);
        }

        // Ensure templates are associated with this order
        // This will trigger the auto-association logic in InMemoryPhotoTemplateRepository.findByOrderId
        try {
            PhotoTemplateRepository photoTemplateRepository = ServiceLocator.getService(PhotoTemplateRepository.class);
            if (photoTemplateRepository != null) {
                // Just calling findByOrderId will trigger the auto-association logic if no templates are found
                photoTemplateRepository.findByOrderId(orderBusiness.getId());
                System.out.println("[DEBUG_LOG] InMemoryOrderRepository: Ensured templates are associated with order: " + orderBusiness.getId().id());
            }
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] InMemoryOrderRepository: Error ensuring templates for order: " + e.getMessage());
        }

        return orderBusiness;
    }

    @Override
    public void delete(OrderBusiness orderBusiness) {
        if (orderBusiness != null) {
            deleteById(orderBusiness.getId());
        }
    }

    @Override
    public boolean deleteById(OrderId id) {
        OrderBusiness orderBusiness = ordersById.get(id);
        if (orderBusiness != null) {
            ordersById.remove(id);
            if (orderBusiness.getOrderNumber() != null) {
                ordersByNumber.remove(orderBusiness.getOrderNumber());
            }
            return true;
        }
        return false;
    }

    @Override
    public List<OrderBusiness> findAll() {
        return new ArrayList<>(ordersById.values());
    }

    @Override
    public boolean existsById(OrderId id) {
        return ordersById.containsKey(id);
    }

    @Override
    public long count() {
        return ordersById.size();
    }

    @Override
    public List<OrderBusiness> findBySpecification(Specification<OrderBusiness> spec) {
        return ordersById.values().stream()
                .filter(spec::isSatisfiedBy)
                .collect(Collectors.toList());
    }

    public Optional<OrderBusiness> findByOrderNumber(OrderNumber orderNumber) {
        return Optional.ofNullable(ordersByNumber.get(orderNumber));
    }
}
