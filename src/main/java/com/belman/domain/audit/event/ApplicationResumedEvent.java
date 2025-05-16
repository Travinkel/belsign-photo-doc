package com.belman.domain.audit.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when the application is resumed.
 * <p>
 * This event represents the transition to the ACTIVE state from a paused
 * or background state and captures information about the application resumption process.
 */
public class ApplicationResumedEvent extends ApplicationStateEvent {
    
    /**
     * Creates a new ApplicationResumedEvent with default values,
     * assuming the application was previously in the PAUSED state.
     */
    public ApplicationResumedEvent() {
        super(
                ApplicationState.PAUSED,
                ApplicationState.ACTIVE,
                "system",
                "Application resumed by user or system",
                "foreground activation",
                "application lifecycle"
        );
    }
    
    /**
     * Creates a new ApplicationResumedEvent with the specified previous state.
     *
     * @param previousState the state before resuming (typically PAUSED or BACKGROUND)
     */
    public ApplicationResumedEvent(ApplicationState previousState) {
        super(
                previousState,
                ApplicationState.ACTIVE,
                "system",
                "Application resumed by user or system",
                "foreground activation",
                "application lifecycle"
        );
    }
    
    /**
     * Creates a new ApplicationResumedEvent with the specified previous state, initiator, and reason.
     *
     * @param previousState the state before resuming (typically PAUSED or BACKGROUND)
     * @param initiator     the entity that initiated the application resumption
     * @param reason        the reason for resuming the application
     */
    public ApplicationResumedEvent(ApplicationState previousState, String initiator, String reason) {
        super(
                previousState,
                ApplicationState.ACTIVE,
                initiator,
                reason,
                "foreground activation",
                "application lifecycle"
        );
    }
    
    /**
     * Creates a new ApplicationResumedEvent with the specified ID, timestamp, previous state, initiator, and reason.
     *
     * @param eventId       the unique identifier for this event
     * @param occurredOn    the timestamp when this event occurred
     * @param previousState the state before resuming (typically PAUSED or BACKGROUND)
     * @param initiator     the entity that initiated the application resumption
     * @param reason        the reason for resuming the application
     */
    public ApplicationResumedEvent(UUID eventId, Instant occurredOn, ApplicationState previousState, String initiator, String reason) {
        super(
                eventId,
                occurredOn,
                previousState,
                ApplicationState.ACTIVE,
                initiator,
                reason,
                "foreground activation",
                "application lifecycle"
        );
    }
    
    @Override
    public String getEventType() {
        return "APPLICATION_RESUMED";
    }
}