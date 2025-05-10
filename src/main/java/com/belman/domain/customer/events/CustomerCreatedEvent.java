package com.belman.domain.customer.events;

import com.belman.domain.customer.CustomerId;
import com.belman.domain.customer.CustomerType;
import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.domain.user.UserId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Audit event that is published when a new customer is created.
 * This event captures the essential information about the customer creation.
 */
public class CustomerCreatedEvent extends BaseAuditEvent {
    private final CustomerId customerId;
    private final CustomerType customerType;
    private final UserId createdBy;

    /**
     * Creates a new CustomerCreatedEvent with the specified customer ID, type, and creator.
     *
     * @param customerId   the ID of the created customer
     * @param customerType the type of the created customer (INDIVIDUAL, COMPANY, etc.)
     * @param createdBy    the ID of the user who created the customer
     */
    public CustomerCreatedEvent(CustomerId customerId, CustomerType customerType, UserId createdBy) {
        super();
        this.customerId = Objects.requireNonNull(customerId, "customerId must not be null");
        this.customerType = Objects.requireNonNull(customerType, "customerType must not be null");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
    }

    /**
     * Constructor for event deserialization/reconstitution.
     *
     * @param eventId      the ID of the event
     * @param occurredOn   the timestamp when the event occurred
     * @param customerId   the ID of the created customer
     * @param customerType the type of the created customer
     * @param createdBy    the ID of the user who created the customer
     */
    public CustomerCreatedEvent(UUID eventId, Instant occurredOn, CustomerId customerId,
                                CustomerType customerType, UserId createdBy) {
        super(eventId, occurredOn);
        this.customerId = Objects.requireNonNull(customerId, "customerId must not be null");
        this.customerType = Objects.requireNonNull(customerType, "customerType must not be null");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
    }

    /**
     * Returns the ID of the created customer.
     *
     * @return the customer ID
     */
    public CustomerId getCustomerId() {
        return customerId;
    }

    /**
     * Returns the type of the created customer.
     *
     * @return the customer type
     */
    public CustomerType getCustomerType() {
        return customerType;
    }

    /**
     * Returns the ID of the user who created the customer.
     *
     * @return the creator's user ID
     */
    public UserId getCreatedBy() {
        return createdBy;
    }

    /**
     * Returns the entity type for this event.
     * This is used for filtering events by entity type.
     *
     * @return the entity type ("Customer")
     */
    public String getEntityType() {
        return "Customer";
    }

    /**
     * Returns the entity ID for this event.
     * This is used for filtering events by entity ID.
     *
     * @return the entity ID (customer ID as string)
     */
    public String getEntityId() {
        return customerId.toString();
    }

    /**
     * Returns the user ID for this event.
     * This is used for filtering events by user ID.
     *
     * @return the user ID (creator's ID as string)
     */
    public String getUserId() {
        return createdBy.toString();
    }

    @Override
    public String getEventType() {
        return "CustomerCreated";
    }
}
