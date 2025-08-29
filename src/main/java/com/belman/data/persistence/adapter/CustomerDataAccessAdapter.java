package com.belman.data.persistence.adapter;

import com.belman.domain.customer.CustomerAggregate;
import com.belman.domain.customer.CustomerBusiness;
import com.belman.domain.customer.CustomerDataAccess;
import com.belman.domain.customer.CustomerId;
import com.belman.domain.specification.AbstractSpecification;
import com.belman.domain.specification.Specification;
import com.belman.data.persistence.memory.InMemoryCustomerRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementation of the CustomerDataAccess interface.
 * This class adapts the InMemoryCustomerRepository to the CustomerDataAccess interface,
 * allowing the business layer to interact with the data layer through the CustomerDataAccess interface.
 */
public class CustomerDataAccessAdapter implements CustomerDataAccess {
    private final InMemoryCustomerRepository repository;

    /**
     * Creates a new CustomerDataAccessAdapter with the specified repository.
     *
     * @param repository the repository to adapt
     */
    public CustomerDataAccessAdapter(InMemoryCustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<CustomerBusiness> findById(CustomerId id) {
        CustomerAggregate aggregate = repository.findById(id);
        return Optional.ofNullable(aggregate).map(this::convertToBusiness);
    }

    @Override
    public CustomerBusiness save(CustomerBusiness customer) {
        CustomerAggregate aggregate = convertToAggregate(customer);
        repository.save(aggregate);
        return customer; // Return the saved business object
    }

    @Override
    public void delete(CustomerBusiness customer) {
        CustomerAggregate aggregate = convertToAggregate(customer);
        repository.delete(aggregate);
    }

    @Override
    public boolean deleteById(CustomerId id) {
        CustomerAggregate aggregate = repository.findById(id);
        if (aggregate != null) {
            repository.delete(aggregate);
            return true;
        }
        return false;
    }

    @Override
    public List<CustomerBusiness> findAll() {
        return repository.findAll().stream()
                .map(this::convertToBusiness)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(CustomerId id) {
        return repository.findById(id) != null;
    }

    @Override
    public long count() {
        return repository.findAll().size();
    }

    /**
     * Converts a CustomerBusiness to a CustomerAggregate.
     *
     * @param business the business object to convert
     * @return the converted aggregate
     */
    private CustomerAggregate convertToAggregate(CustomerBusiness business) {
        if (business.isIndividual()) {
            return CustomerAggregate.individual(
                    business.getId(),
                    business.getPersonName(),
                    business.getEmail(),
                    business.getPhoneNumber()
            );
        } else {
            return CustomerAggregate.company(
                    business.getId(),
                    business.getCompany(),
                    business.getEmail(),
                    business.getPhoneNumber()
            );
        }
    }

    /**
     * Converts a CustomerAggregate to a CustomerBusiness.
     *
     * @param aggregate the aggregate to convert
     * @return the converted business object
     */
    private CustomerBusiness convertToBusiness(CustomerAggregate aggregate) {
        if (aggregate.isIndividual()) {
            return CustomerBusiness.individual(
                    aggregate.getId(),
                    aggregate.getPersonName(),
                    aggregate.getEmail(),
                    aggregate.getPhoneNumber()
            );
        } else {
            return CustomerBusiness.company(
                    aggregate.getId(),
                    aggregate.getCompany(),
                    aggregate.getEmail(),
                    aggregate.getPhoneNumber()
            );
        }
    }

    @Override
    public List<CustomerBusiness> findBySpecification(Specification<CustomerBusiness> spec) {
        // Create a specification that works with CustomerAggregate
        Specification<CustomerAggregate> aggregateSpec = new AbstractSpecification<CustomerAggregate>() {
            @Override
            public boolean isSatisfiedBy(CustomerAggregate aggregate) {
                CustomerBusiness business = convertToBusiness(aggregate);
                return spec.isSatisfiedBy(business);
            }
        };

        return repository.findBySpecification(aggregateSpec).stream()
                .map(this::convertToBusiness)
                .collect(Collectors.toList());
    }
}
