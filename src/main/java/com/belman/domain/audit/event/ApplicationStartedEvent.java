package com.belman.domain.audit.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when the application is started.
 * <p>
 * This event represents the transition to the STARTING state and captures
 * information about the application startup process.
 */
public class ApplicationStartedEvent extends ApplicationStateEvent {
    
    /**
     * Creates a new ApplicationStartedEvent with default values.
     */
    public ApplicationStartedEvent() {
        super(
                ApplicationState.STOPPING, // Assuming the application was previously stopped
                ApplicationState.STARTING,
                "system",
                "Application initialization",
                "system startup",
                "application bootstrap"
        );
    }
    
    /**
     * Creates a new ApplicationStartedEvent with the specified initiator and reason.
     *
     * @param initiator the entity that initiated the application start
     * @param reason    the reason for starting the application
     */
    public ApplicationStartedEvent(String initiator, String reason) {
        super(
                ApplicationState.STOPPING,
                ApplicationState.STARTING,
                initiator,
                reason,
                "system startup",
                "application bootstrap"
        );
    }
    
    /**
     * Creates a new ApplicationStartedEvent with the specified ID, timestamp, initiator, and reason.
     *
     * @param eventId    the unique identifier for this event
     * @param occurredOn the timestamp when this event occurred
     * @param initiator  the entity that initiated the application start
     * @param reason     the reason for starting the application
     */
    public ApplicationStartedEvent(UUID eventId, Instant occurredOn, String initiator, String reason) {
        super(
                eventId,
                occurredOn,
                ApplicationState.STOPPING,
                ApplicationState.STARTING,
                initiator,
                reason,
                "system startup",
                "application bootstrap"
        );
    }
    
    @Override
    public String getEventType() {
        return "APPLICATION_STARTED";
    }
}