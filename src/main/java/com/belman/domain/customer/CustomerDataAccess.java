package com.belman.domain.customer;

import com.belman.domain.core.DataAccessInterface;
import com.belman.domain.specification.Specification;

import java.util.List;

/**
 * Data access interface for Customer business object.
 * Integrates the Specification pattern for querying.
 */
public interface CustomerDataAccess extends DataAccessInterface<CustomerBusiness, CustomerId> {
    /**
     * Finds customers that satisfy the given specification.
     *
     * @param spec the specification to filter customers
     * @return a list of customers that satisfy the specification
     */
    List<CustomerBusiness> findBySpecification(Specification<CustomerBusiness> spec);
}
