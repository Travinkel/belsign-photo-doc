package com.belman.domain.customer.events;

import com.belman.domain.customer.CustomerId;
import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.domain.user.UserId;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Audit event that is published when a customer is updated.
 * This event captures the essential information about the customer update,
 * including which fields were changed.
 */
public class CustomerUpdatedEvent extends BaseAuditEvent {
    private final CustomerId customerId;
    private final UserId updatedBy;
    private final Map<String, String> changedFields;

    /**
     * Creates a new CustomerUpdatedEvent with the specified customer ID, updater, and changed fields.
     *
     * @param customerId    the ID of the updated customer
     * @param updatedBy     the ID of the user who updated the customer
     * @param changedFields a map of field names to their new values
     */
    public CustomerUpdatedEvent(CustomerId customerId, UserId updatedBy, Map<String, String> changedFields) {
        super();
        this.customerId = Objects.requireNonNull(customerId, "customerId must not be null");
        this.updatedBy = Objects.requireNonNull(updatedBy, "updatedBy must not be null");
        this.changedFields = new HashMap<>(Objects.requireNonNull(changedFields, "changedFields must not be null"));
    }

    /**
     * Constructor for event deserialization/reconstitution.
     *
     * @param eventId       the ID of the event
     * @param occurredOn    the timestamp when the event occurred
     * @param customerId    the ID of the updated customer
     * @param updatedBy     the ID of the user who updated the customer
     * @param changedFields a map of field names to their new values
     */
    public CustomerUpdatedEvent(UUID eventId, Instant occurredOn, CustomerId customerId,
                                UserId updatedBy, Map<String, String> changedFields) {
        super(eventId, occurredOn);
        this.customerId = Objects.requireNonNull(customerId, "customerId must not be null");
        this.updatedBy = Objects.requireNonNull(updatedBy, "updatedBy must not be null");
        this.changedFields = new HashMap<>(Objects.requireNonNull(changedFields, "changedFields must not be null"));
    }

    /**
     * Returns the ID of the updated customer.
     *
     * @return the customer ID
     */
    public CustomerId getCustomerId() {
        return customerId;
    }

    /**
     * Returns the ID of the user who updated the customer.
     *
     * @return the updater's user ID
     */
    public UserId getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Returns an unmodifiable map of the fields that were changed in this update.
     * The map keys are field names and the values are the new values.
     *
     * @return the changed fields
     */
    public Map<String, String> getChangedFields() {
        return Map.copyOf(changedFields);
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
     * @return the user ID (updater's ID as string)
     */
    public String getUserId() {
        return updatedBy.toString();
    }

    @Override
    public String getEventType() {
        return "CustomerUpdated";
    }
}
