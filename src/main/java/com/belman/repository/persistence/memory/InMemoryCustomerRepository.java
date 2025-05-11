package com.belman.repository.persistence.memory;

import com.belman.domain.customer.CustomerAggregate;
import com.belman.domain.customer.CustomerId;
import com.belman.domain.customer.CustomerRepository;
import com.belman.domain.specification.Specification;
import com.belman.repository.logging.EmojiLoggerFactory;
import com.belman.service.base.BaseService;

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

    /**
     * Creates a new InMemoryCustomerRepository.
     */
    public InMemoryCustomerRepository() {
        super(EmojiLoggerFactory.getInstance());
    }

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
