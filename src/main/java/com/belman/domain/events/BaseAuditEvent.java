package com.belman.domain.events;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base class for all audit events.
 * <p>
 * Provides common functionality and properties for all audit events,
 * ensuring that every event has a unique identifier and timestamp.
 */
public abstract class BaseAuditEvent implements AuditEvent {

    private final UUID eventId;
    private final Instant occurredOn;

    /**
     * Creates a new audit event with an auto-generated ID and the current timestamp.
     */
    protected BaseAuditEvent() {
        this(UUID.randomUUID(), Instant.now());
    }

    /**
     * Creates a new audit event with the specified ID and timestamp.
     *
     * @param eventId    the unique identifier for this event
     * @param occurredOn the timestamp when this event occurred
     */
    protected BaseAuditEvent(UUID eventId, Instant occurredOn) {
        this.eventId = Objects.requireNonNull(eventId, "eventId must not be null");
        this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID getEventId() {
        return eventId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }

    /**
     * {@inheritDoc}
     * Default implementation returns the simple name of the implementing class.
     */
    @Override
    public String getEventType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseAuditEvent that = (BaseAuditEvent) o;
        return eventId.equals(that.eventId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "eventId=" + eventId +
               ", occurredOn=" + occurredOn +
               '}';
    }
}