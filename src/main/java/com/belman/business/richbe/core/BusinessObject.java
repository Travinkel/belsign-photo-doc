package com.belman.business.richbe.core;

import com.belman.business.audit.AuditFacade;
import com.belman.business.richbe.events.AuditEvent;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
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

    private static AuditFacade auditFacade;
    private Instant lastModifiedAt;

    /**
     * Sets the audit facade to be used by all business objects.
     * This method should be called during application initialization.
     *
     * @param facade the audit facade to use
     */
    public static void setAuditFacade(AuditFacade facade) {
        auditFacade = Objects.requireNonNull(facade, "auditFacade must not be null");
    }

    /**
     * Returns the identifier of this business object.
     * The identifier must be unique within the object's type.
     *
     * @return the business object's identifier
     */
    public abstract ID getId();

    /**
     * Registers an audit event to be dispatched immediately.
     * The event is logged through the AuditFacade.
     *
     * @param event the audit event to register
     * @throws NullPointerException if the event is null
     */
    protected void registerAuditEvent(AuditEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        if (auditFacade == null) {
            throw new IllegalStateException("AuditFacade has not been set. Call setAuditFacade during application initialization.");
        }
        auditFacade.logEvent(event);
        updateLastModifiedAt();
    }

    /**
     * Registers multiple audit events to be dispatched immediately.
     * The events are logged through the AuditFacade.
     *
     * @param events the collection of audit events to register
     * @throws NullPointerException if the events collection is null or contains null elements
     */
    protected void registerAuditEvents(Collection<AuditEvent> events) {
        Objects.requireNonNull(events, "events collection must not be null");
        if (auditFacade == null) {
            throw new IllegalStateException("AuditFacade has not been set. Call setAuditFacade during application initialization.");
        }
        auditFacade.logBatch(events.stream().toList());
        updateLastModifiedAt();
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
