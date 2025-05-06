package com.belman.domain.customer;

import com.belman.domain.common.EmailAddress;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing customers.
 * This interface follows the Repository pattern from Domain-Driven Design.
 */
public interface CustomerRepository {

    /**
     * Finds a customer by ID.
     *
     * @param id the customer ID to search for
     * @return an Optional containing the customer if found, or empty if not found
     */
    Optional<CustomerAggregate> findById(CustomerId id);

    /**
     * Finds a customer by company name (case-insensitive, partial match).
     *
     * @param companyName the company name to search for
     * @return a list of customers matching the company name
     */
    List<CustomerAggregate> findByCompanyName(String companyName);

    /**
     * Finds a customer by contact email.
     *
     * @param email the contact email to search for
     * @return an Optional containing the customer if found, or empty if not found
     */
    Optional<CustomerAggregate> findByContactEmail(EmailAddress email);

    /**
     * Saves a customer.
     * If the customer already exists, it will be updated.
     * If the customer does not exist, it will be created.
     *
     * @param customer the customer to save
     */
    void save(CustomerAggregate customer);

    /**
     * Deletes a customer by ID.
     *
     * @param id the ID of the customer to delete
     * @return true if the customer was deleted, false if the customer was not found
     */
    boolean delete(CustomerId id);

    /**
     * Gets all customers.
     *
     * @return a list of all customers
     */
    List<CustomerAggregate> findAll();

    /**
     * Gets all active customers.
     *
     * @return a list of all active customers
     */
    List<CustomerAggregate> findAllActive();

    /**
     * Gets all customers of a specific type.
     *
     * @param type the customer type to filter by
     * @return a list of customers of the specified type
     */
    List<CustomerAggregate> findByType(CustomerType type);
}