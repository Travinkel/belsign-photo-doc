package com.belman.domain.audit.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when the application is paused.
 * <p>
 * This event represents the transition to the PAUSED state from an active
 * state and captures information about the application pause process.
 * <p>
 * The paused state typically occurs when the application is still visible
 * but not in the foreground, such as when a dialog is shown over it.
 */
public class ApplicationPausedEvent extends ApplicationStateEvent {
    
    /**
     * Creates a new ApplicationPausedEvent with default values,
     * assuming the application was previously in the ACTIVE state.
     */
    public ApplicationPausedEvent() {
        super(
                ApplicationState.ACTIVE,
                ApplicationState.PAUSED,
                "system",
                "Application paused by user or system",
                "focus loss",
                "application lifecycle"
        );
    }
    
    /**
     * Creates a new ApplicationPausedEvent with the specified initiator and reason.
     *
     * @param initiator the entity that initiated the application pause
     * @param reason    the reason for pausing the application
     */
    public ApplicationPausedEvent(String initiator, String reason) {
        super(
                ApplicationState.ACTIVE,
                ApplicationState.PAUSED,
                initiator,
                reason,
                "focus loss",
                "application lifecycle"
        );
    }
    
    /**
     * Creates a new ApplicationPausedEvent with the specified ID, timestamp, initiator, and reason.
     *
     * @param eventId    the unique identifier for this event
     * @param occurredOn the timestamp when this event occurred
     * @param initiator  the entity that initiated the application pause
     * @param reason     the reason for pausing the application
     */
    public ApplicationPausedEvent(UUID eventId, Instant occurredOn, String initiator, String reason) {
        super(
                eventId,
                occurredOn,
                ApplicationState.ACTIVE,
                ApplicationState.PAUSED,
                initiator,
                reason,
                "focus loss",
                "application lifecycle"
        );
    }
    
    @Override
    public String getEventType() {
        return "APPLICATION_PAUSED";
    }
}