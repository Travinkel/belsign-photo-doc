package com.belman.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Interface for business events in the business model.
 * <p>
 * Business events represent significant occurrences or state changes within the system
 * that need to be tracked for business purposes. This includes audit events, which are
 * a specific type of business event focused on accountability and traceability.
 */
public interface BusinessEvent {

    /**
     * Gets the unique identifier of this business event.
     *
     * @return the event's unique identifier
     */
    UUID getEventId();

    /**
     * Gets the timestamp when this business event occurred.
     *
     * @return the timestamp when the event occurred
     */
    Instant getOccurredOn();

    /**
     * Gets the type of this business event.
     * This is typically used for categorizing and filtering events.
     *
     * @return the event type
     */
    String getEventType();
}