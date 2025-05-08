package com.belman.business.richbe.customer;

/**
 * Enum representing the type of a customer in the BelSign system.
 * <p>
 * Different customer types may have different business rules, pricing
 * structures, or approval workflows for orders and reports.
 */
public enum CustomerType {
    /**
     * Individual person customer.
     * Typically associated with smaller orders and simplified workflows.
     */
    INDIVIDUAL,

    /**
     * Company or organization customer.
     * Often associated with larger orders, more formal documentation,
     * and potentially more complex approval processes.
     */
    COMPANY,

    /**
     * Distributor or reseller customer.
     * Has special privileges and pricing structure as they sell to end customers.
     */
    DISTRIBUTOR,

    /**
     * Internal department customer.
     * Used for internal orders within the organization.
     */
    INTERNAL
}