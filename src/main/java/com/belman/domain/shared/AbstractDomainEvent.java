package com.belman.domain.shared;

import java.time.Instant;
import java.util.UUID;

/**
 * Abstract base class for domain events that provides common functionality.
 * Concrete event classes should extend this class and provide their specific data.
 */
public abstract class AbstractDomainEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant timestamp;
    private final String eventType;
    
    /**
     * Creates a new domain event with a random ID and the current timestamp.
     * The event type is derived from the class name.
     */
    protected AbstractDomainEvent() {
        this.eventId = UUID.randomUUID();
        this.timestamp = Instant.now();
        this.eventType = this.getClass().getSimpleName();
    }
    
    /**
     * Creates a new domain event with the specified ID, timestamp, and event type.
     * 
     * @param eventId the event ID
     * @param timestamp the event timestamp
     * @param eventType the event type
     */
    protected AbstractDomainEvent(UUID eventId, Instant timestamp, String eventType) {
        if (eventId == null || timestamp == null || eventType == null) {
            throw new IllegalArgumentException("EventId, timestamp, and eventType cannot be null");
        }
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.eventType = eventType;
    }
    
    @Override
    public UUID getEventId() {
        return eventId;
    }
    
    @Override
    public Instant getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String getEventType() {
        return eventType;
    }
    
    @Override
    public String toString() {
        return String.format("Event[type=%s, timestamp=%s, id=%s]", eventType, timestamp, eventId);
    }
}
