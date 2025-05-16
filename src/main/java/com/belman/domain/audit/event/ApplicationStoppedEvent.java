package com.belman.domain.audit.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when the application is stopped.
 * <p>
 * This event represents the transition to the STOPPING state from any other
 * state and captures information about the application shutdown process.
 * <p>
 * The stopping state occurs when the application is shutting down, either
 * due to user action or system shutdown.
 */
public class ApplicationStoppedEvent extends ApplicationStateEvent {
    
    /**
     * Creates a new ApplicationStoppedEvent with default values,
     * assuming the application was previously in the ACTIVE state.
     */
    public ApplicationStoppedEvent() {
        super(
                ApplicationState.ACTIVE,
                ApplicationState.STOPPING,
                "system",
                "Application shutdown requested by user or system",
                "shutdown process",
                "application lifecycle"
        );
    }
    
    /**
     * Creates a new ApplicationStoppedEvent with the specified previous state.
     *
     * @param previousState the state before stopping (can be any state)
     */
    public ApplicationStoppedEvent(ApplicationState previousState) {
        super(
                previousState,
                ApplicationState.STOPPING,
                "system",
                "Application shutdown requested by user or system",
                "shutdown process",
                "application lifecycle"
        );
    }
    
    /**
     * Creates a new ApplicationStoppedEvent with the specified previous state, initiator, and reason.
     *
     * @param previousState the state before stopping (can be any state)
     * @param initiator     the entity that initiated the application shutdown
     * @param reason        the reason for stopping the application
     */
    public ApplicationStoppedEvent(ApplicationState previousState, String initiator, String reason) {
        super(
                previousState,
                ApplicationState.STOPPING,
                initiator,
                reason,
                "shutdown process",
                "application lifecycle"
        );
    }
    
    /**
     * Creates a new ApplicationStoppedEvent with the specified ID, timestamp, previous state, initiator, and reason.
     *
     * @param eventId       the unique identifier for this event
     * @param occurredOn    the timestamp when this event occurred
     * @param previousState the state before stopping (can be any state)
     * @param initiator     the entity that initiated the application shutdown
     * @param reason        the reason for stopping the application
     */
    public ApplicationStoppedEvent(UUID eventId, Instant occurredOn, ApplicationState previousState, String initiator, String reason) {
        super(
                eventId,
                occurredOn,
                previousState,
                ApplicationState.STOPPING,
                initiator,
                reason,
                "shutdown process",
                "application lifecycle"
        );
    }
    
    @Override
    public String getEventType() {
        return "APPLICATION_STOPPED";
    }
}