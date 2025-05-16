package com.belman.domain.audit.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when the application is moved to the background.
 * <p>
 * This event represents the transition to the BACKGROUND state from an active
 * or paused state and captures information about the application backgrounding process.
 * <p>
 * The background state typically occurs when the application is no longer visible
 * to the user, such as when another application is brought to the foreground or
 * when the device is locked.
 */
public class ApplicationBackgroundedEvent extends ApplicationStateEvent {
    
    /**
     * Creates a new ApplicationBackgroundedEvent with default values,
     * assuming the application was previously in the PAUSED state.
     */
    public ApplicationBackgroundedEvent() {
        super(
                ApplicationState.PAUSED,
                ApplicationState.BACKGROUND,
                "system",
                "Application moved to background by user or system",
                "visibility loss",
                "application lifecycle"
        );
    }
    
    /**
     * Creates a new ApplicationBackgroundedEvent with the specified previous state.
     *
     * @param previousState the state before backgrounding (typically ACTIVE or PAUSED)
     */
    public ApplicationBackgroundedEvent(ApplicationState previousState) {
        super(
                previousState,
                ApplicationState.BACKGROUND,
                "system",
                "Application moved to background by user or system",
                "visibility loss",
                "application lifecycle"
        );
    }
    
    /**
     * Creates a new ApplicationBackgroundedEvent with the specified previous state, initiator, and reason.
     *
     * @param previousState the state before backgrounding (typically ACTIVE or PAUSED)
     * @param initiator     the entity that initiated the application backgrounding
     * @param reason        the reason for backgrounding the application
     */
    public ApplicationBackgroundedEvent(ApplicationState previousState, String initiator, String reason) {
        super(
                previousState,
                ApplicationState.BACKGROUND,
                initiator,
                reason,
                "visibility loss",
                "application lifecycle"
        );
    }
    
    /**
     * Creates a new ApplicationBackgroundedEvent with the specified ID, timestamp, previous state, initiator, and reason.
     *
     * @param eventId       the unique identifier for this event
     * @param occurredOn    the timestamp when this event occurred
     * @param previousState the state before backgrounding (typically ACTIVE or PAUSED)
     * @param initiator     the entity that initiated the application backgrounding
     * @param reason        the reason for backgrounding the application
     */
    public ApplicationBackgroundedEvent(UUID eventId, Instant occurredOn, ApplicationState previousState, String initiator, String reason) {
        super(
                eventId,
                occurredOn,
                previousState,
                ApplicationState.BACKGROUND,
                initiator,
                reason,
                "visibility loss",
                "application lifecycle"
        );
    }
    
    @Override
    public String getEventType() {
        return "APPLICATION_BACKGROUNDED";
    }
}