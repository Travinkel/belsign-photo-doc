package com.belman.infrastructure.persistence;

import com.belman.application.core.BaseService;
import com.belman.domain.customer.CustomerAggregate;
import com.belman.domain.customer.CustomerRepository;
import com.belman.domain.specification.Specification;
import com.belman.domain.valueobjects.CustomerId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the CustomerRepository interface.
 * This implementation is used as a fallback when a database is not available.
 */
public class InMemoryCustomerRepository extends BaseService implements CustomerRepository {
    private final Map<CustomerId, CustomerAggregate> customers = new HashMap<>();

    @Override
    public CustomerAggregate findById(CustomerId id) {
        return customers.get(id);
    }

    @Override
    public List<CustomerAggregate> findAll() {
        return new ArrayList<>(customers.values());
    }

    @Override
    public List<CustomerAggregate> findBySpecification(Specification<CustomerAggregate> spec) {
        return customers.values().stream()
                .filter(spec::isSatisfiedBy)
                .collect(Collectors.toList());
    }

    @Override
    public void save(CustomerAggregate customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        customers.put(customer.getId(), customer);
    }

    @Override
    public void delete(CustomerAggregate customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        customers.remove(customer.getId());
    }
}
