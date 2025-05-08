package com.belman.business.richbe.core;

import com.belman.business.richbe.events.AuditEvent;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Base class for all business objects in the business model.
 * <p>
 * A business object is an entity that acts as the entry point to a group of related components
 * (a cluster of business objects that are treated as a single unit for data changes).
 * Business objects are responsible for enforcing invariants and consistency rules
 * within the group and emitting audit events that represent significant changes to
 * the object's state.
 *
 * @param <ID> the type of identifier used by this business object
 */
public abstract class BusinessObject<ID> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<AuditEvent> auditEvents = new ArrayList<>();
    private Instant lastModifiedAt;

    /**
     * Returns the identifier of this business object.
     * The identifier must be unique within the object's type.
     *
     * @return the business object's identifier
     */
    public abstract ID getId();

    /**
     * Registers an audit event to be dispatched when the business object is persisted.
     *
     * @param event the audit event to register
     * @throws NullPointerException if the event is null
     */
    protected void registerAuditEvent(AuditEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        this.auditEvents.add(event);
        updateLastModifiedAt();
    }

    /**
     * Registers multiple audit events to be dispatched when the business object is persisted.
     *
     * @param events the collection of audit events to register
     * @throws NullPointerException if the events collection is null or contains null elements
     */
    protected void registerAuditEvents(Collection<AuditEvent> events) {
        Objects.requireNonNull(events, "events collection must not be null");
        events.forEach(this::registerAuditEvent);
    }

    /**
     * Returns and clears all audit events that were registered with this business object.
     * This method is typically called by a data access interface when saving the business object.
     *
     * @return an unmodifiable list of audit events
     */
    public List<AuditEvent> pullAuditEvents() {
        List<AuditEvent> events = new ArrayList<>(this.auditEvents);
        this.auditEvents.clear();
        return Collections.unmodifiableList(events);
    }

    /**
     * Returns all registered audit events without clearing the list.
     * This method is useful for testing or inspection purposes.
     *
     * @return an unmodifiable list of audit events
     */
    public List<AuditEvent> getRegisteredAuditEvents() {
        return Collections.unmodifiableList(this.auditEvents);
    }

    /**
     * Checks if there are any registered audit events.
     *
     * @return true if there are registered audit events, false otherwise
     */
    public boolean hasAuditEvents() {
        return !this.auditEvents.isEmpty();
    }

    /**
     * Clears the list of audit events without dispatching them.
     * This is useful in situations where you want to discard events, such as when rolling back a transaction.
     */
    protected void clearAuditEvents() {
        this.auditEvents.clear();
    }

    /**
     * Updates the last modified timestamp of the business object.
     */
    protected void updateLastModifiedAt() {
        this.lastModifiedAt = Instant.now();
    }

    /**
     * Gets the last modified timestamp of the business object.
     *
     * @return the last modified timestamp
     */
    public Instant getLastModifiedAt() {
        return this.lastModifiedAt;
    }

    /**
     * Checks equality based on the business object's identifier.
     *
     * @param obj the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BusinessObject<?> that = (BusinessObject<?>) obj;
        return Objects.equals(getId(), that.getId());
    }

    /**
     * Computes the hash code based on the business object's identifier.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
