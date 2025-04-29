package com.belman.belsign.framework.athomefx.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Base interface for all domain events in the application.
 * Domain events represent something that happened in the domain that domain experts care about.
 */
public interface DomainEvent {
    
    /**
     * Gets the unique identifier for this event.
     * 
     * @return the event ID
     */
    UUID getEventId();
    
    /**
     * Gets the timestamp when this event occurred.
     * 
     * @return the event timestamp
     */
    Instant getTimestamp();
    
    /**
     * Gets the type of this event.
     * 
     * @return the event type
     */
    String getEventType();
}