package com.belman.domain.shared;

import java.time.Instant;
import java.util.UUID;

/**
 * Abstract base class for domain events that provides common functionality.
 * Concrete event classes should extend this class and provide their specific data.
 * 
 * @deprecated This class is deprecated and will be removed in a future release.
 * Use {@link com.belman.domain.events.AbstractDomainEvent} instead.
 */
@Deprecated
public abstract class AbstractDomainEvent extends com.belman.domain.events.AbstractDomainEvent implements DomainEvent {

    /**
     * Creates a new domain event with a random ID and the current timestamp.
     * The event type is derived from the class name.
     */
    protected AbstractDomainEvent() {
        super();
    }

    /**
     * Creates a new domain event with the specified ID, timestamp, and event type.
     * 
     * @param eventId the event ID
     * @param timestamp the event timestamp
     * @param eventType the event type
     */
    protected AbstractDomainEvent(UUID eventId, Instant timestamp, String eventType) {
        super(eventId, timestamp, eventType);
    }
}
