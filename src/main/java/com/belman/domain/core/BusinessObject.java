package com.belman.domain.core;

import com.belman.domain.audit.AuditFacade;
import com.belman.domain.audit.event.AuditEvent;
import com.belman.domain.event.BusinessEvent;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Base class for all business objects in the business model.
 * <p>
 * A business object is an entity that acts as the entry point to a group of related components
 * (a cluster of business objects that are treated as a single unit for data changes).
 * Business objects are responsible for enforcing invariants and consistency rules
 * within the group and emitting business events that represent significant changes to
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
     * Registers an audit event to be dispatched immediately.
     * This method is provided for backward compatibility.
     *
     * @param event the audit event to register
     * @throws NullPointerException if the event is null
     * @deprecated Use {@link #registerBusinessEvent(BusinessEvent)} instead
     */
    @Deprecated
    protected void registerAuditEvent(AuditEvent event) {
        registerBusinessEvent(event);
    }

    /**
     * Registers a business event to be dispatched immediately.
     * If the event is an audit event, it is also logged through the AuditFacade.
     *
     * @param event the business event to register
     * @throws NullPointerException if the event is null
     */
    protected void registerBusinessEvent(BusinessEvent event) {
        Objects.requireNonNull(event, "event must not be null");

        // If it's an audit event, log it through the audit facade
        if (event instanceof AuditEvent) {
            if (auditFacade == null) {
                throw new IllegalStateException(
                        "AuditFacade has not been set. Call setAuditFacade during application initialization.");
            }
            auditFacade.logEvent((AuditEvent) event);
        }

        updateLastModifiedAt();
    }

    /**
     * Updates the last modified timestamp of the business object.
     */
    protected void updateLastModifiedAt() {
        this.lastModifiedAt = Instant.now();
    }

    /**
     * Registers multiple audit events to be dispatched immediately.
     * This method is provided for backward compatibility.
     *
     * @param events the collection of audit events to register
     * @throws NullPointerException if the events collection is null or contains null elements
     * @deprecated Use {@link #registerBusinessEvents(Collection)} instead
     */
    @Deprecated
    protected void registerAuditEvents(Collection<AuditEvent> events) {
        registerBusinessEvents(new ArrayList<>(events));
    }

    /**
     * Registers multiple business events to be dispatched immediately.
     * If any of the events are audit events, they are also logged through the AuditFacade.
     *
     * @param events the collection of business events to register
     * @throws NullPointerException if the events collection is null or contains null elements
     */
    protected void registerBusinessEvents(Collection<? extends BusinessEvent> events) {
        Objects.requireNonNull(events, "events collection must not be null");

        // Filter out audit events and log them through the audit facade
        if (auditFacade != null) {
            List<AuditEvent> auditEvents = events.stream()
                    .filter(e -> e instanceof AuditEvent)
                    .map(e -> (AuditEvent) e)
                    .collect(Collectors.toList());

            if (!auditEvents.isEmpty()) {
                auditFacade.logBatch(auditEvents);
            }
        }

        updateLastModifiedAt();
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
     * Computes the hash code based on the business object's identifier.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId());
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
     * Returns the identifier of this business object.
     * The identifier must be unique within the object's type.
     *
     * @return the business object's identifier
     */
    public abstract ID getId();
}
