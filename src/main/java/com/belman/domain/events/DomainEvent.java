package com.belman.domain.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Base interface for all domain events in the system.
 * <p>
 * Domain events represent significant occurrences within the domain model that other
 * parts of the application might be interested in. They are immutable records of
 * something that happened in the domain.
 */
public interface DomainEvent {

    /**
     * Returns the unique identifier for this event.
     *
     * @return the event ID
     */
    UUID getEventId();

    /**
     * Returns the timestamp when this event occurred.
     *
     * @return the event timestamp
     */
    Instant getOccurredOn();

    /**
     * Returns the type of this event.
     *
     * @return the event type
     */
    String getEventType();
}