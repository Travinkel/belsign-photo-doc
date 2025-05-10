package com.belman.repository.persistence;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderDataAccess;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.specification.AbstractSpecification;
import com.belman.domain.specification.Specification;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementation of the OrderDataAccess interface.
 * This class adapts the InMemoryOrderRepository to the OrderDataAccess interface,
 * allowing the business layer to interact with the data layer through the OrderDataAccess interface.
 */
public class OrderDataAccessAdapter implements OrderDataAccess {
    private final InMemoryOrderRepository repository;

    /**
     * Creates a new OrderDataAccessAdapter with the specified repository.
     *
     * @param repository the repository to adapt
     */
    public OrderDataAccessAdapter(InMemoryOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<OrderBusiness> findById(OrderId id) {
        return repository.findById(id)
                .map(this::convertToBusiness);
    }

    @Override
    public List<OrderBusiness> findAll() {
        return repository.findAll().stream()
                .map(this::convertToBusiness)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderBusiness> findBySpecification(Specification<OrderBusiness> spec) {
        // Create a specification that works with OrderBusiness
        Specification<OrderBusiness> aggregateSpec = new AbstractSpecification<OrderBusiness>() {
            @Override
            public boolean isSatisfiedBy(OrderBusiness aggregate) {
                OrderBusiness business = convertToBusiness(aggregate);
                return spec.isSatisfiedBy(business);
            }
        };

        return repository.findBySpecification(aggregateSpec).stream()
                .map(this::convertToBusiness)
                .collect(Collectors.toList());
    }

    @Override
    public void save(OrderBusiness orderBusiness) {
        OrderBusiness aggregate = convertToAggregate(orderBusiness);
        repository.save(aggregate);
    }

    @Override
    public Optional<OrderBusiness> findByOrderNumber(OrderNumber orderNumber) {
        return repository.findByOrderNumber(orderNumber)
                .map(this::convertToBusiness);
    }

    /**
     * Converts an OrderBusiness to an OrderBusiness.
     *
     * @param aggregate the aggregate to convert
     * @return the converted business object
     */
    private OrderBusiness convertToBusiness(OrderBusiness aggregate) {
        OrderBusiness business = new OrderBusiness(
                aggregate.getId(),
                aggregate.getOrderNumber(),
                aggregate.getCustomerId(),
                aggregate.getProductDescription(),
                aggregate.getDeliveryInformation(),
                aggregate.getCreatedBy(),
                aggregate.getCreatedAt()
        );
        
        // Set status
        business.setStatus(aggregate.getStatus());
        
        // Add photos
        for (var photo : aggregate.getPhotos()) {
            business.addPhoto(photo);
        }
        
        return business;
    }

    /**
     * Converts an OrderBusiness to an OrderBusiness.
     *
     * @param business the business object to convert
     * @return the converted aggregate
     */
    private OrderBusiness convertToAggregate(OrderBusiness business) {
        OrderBusiness aggregate = new OrderBusiness(
                business.getId(),
                business.getOrderNumber(),
                business.getCustomerId(),
                business.getProductDescription(),
                business.getDeliveryInformation(),
                business.getCreatedBy(),
                business.getCreatedAt()
        );
        
        // Set status
        aggregate.setStatus(business.getStatus());
        
        // Add photos
        for (var photo : business.getPhotos()) {
            aggregate.addPhoto(photo);
        }
        
        return aggregate;
    }
}