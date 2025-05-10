package com.belman.domain.customer;

import com.belman.domain.specification.Specification;

import java.util.List;

/**
 * Repository interface for Customer aggregate.
 * Integrates the Specification pattern for querying.
 */
public interface CustomerRepository {
    /**
     * Finds a customer by their unique ID.
     *
     * @param id the customer ID to search for
     * @return the customer with the specified ID, or null if not found
     */
    CustomerAggregate findById(CustomerId id);

    /**
     * Finds all customers in the repository.
     *
     * @return a list of all customers
     */
    List<CustomerAggregate> findAll();

    /**
     * Finds customers that satisfy the given specification.
     *
     * @param spec the specification to filter customers
     * @return a list of customers that satisfy the specification
     */
    List<CustomerAggregate> findBySpecification(Specification<CustomerAggregate> spec);

    /**
     * Saves a customer to the repository.
     * If the customer already exists, it will be updated.
     *
     * @param customer the customer to save
     */
    void save(CustomerAggregate customer);

    /**
     * Deletes a customer from the repository.
     *
     * @param customer the customer to delete
     */
    void delete(CustomerAggregate customer);
}