package com.belman.domain.customer;

import com.belman.domain.specification.Specification;

import java.util.List;
import java.util.Optional;

/**
 * Data access interface for Customer business object.
 * Integrates the Specification pattern for querying.
 */
public interface CustomerDataAccess {
    /**
     * Finds a customer by its ID.
     *
     * @param id the customer ID to search for
     * @return an Optional containing the customer if found, or empty if not found
     */
    Optional<CustomerBusiness> findById(CustomerId id);

    /**
     * Finds all customers.
     *
     * @return a list of all customers
     */
    List<CustomerBusiness> findAll();

    /**
     * Saves a customer (creates or updates).
     *
     * @param customer the customer to save
     * @return the saved customer
     */
    CustomerBusiness save(CustomerBusiness customer);

    /**
     * Deletes a customer.
     *
     * @param customer the customer to delete
     */
    void delete(CustomerBusiness customer);

    /**
     * Deletes a customer by its ID.
     *
     * @param id the ID of the customer to delete
     * @return true if the customer was deleted, false if the customer was not found
     */
    boolean deleteById(CustomerId id);

    /**
     * Checks if a customer with the given ID exists.
     *
     * @param id the ID to check
     * @return true if a customer with the given ID exists, false otherwise
     */
    boolean existsById(CustomerId id);

    /**
     * Counts the number of customers.
     *
     * @return the number of customers
     */
    long count();
    /**
     * Finds customers that satisfy the given specification.
     *
     * @param spec the specification to filter customers
     * @return a list of customers that satisfy the specification
     */
    List<CustomerBusiness> findBySpecification(Specification<CustomerBusiness> spec);
}
