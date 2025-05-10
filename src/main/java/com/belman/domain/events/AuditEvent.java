package com.belman.domain.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Interface for audit events in the business model.
 * <p>
 * Audit events represent significant occurrences or state changes within the system
 * that need to be tracked for accountability and traceability purposes.
 */
public interface AuditEvent {

    /**
     * Gets the unique identifier of this audit event.
     *
     * @return the event's unique identifier
     */
    UUID getEventId();

    /**
     * Gets the timestamp when this audit event occurred.
     *
     * @return the timestamp when the event occurred
     */
    Instant getOccurredOn();

    /**
     * Gets the type of this audit event.
     * This is typically used for categorizing and filtering events.
     *
     * @return the event type
     */
    String getEventType();
}