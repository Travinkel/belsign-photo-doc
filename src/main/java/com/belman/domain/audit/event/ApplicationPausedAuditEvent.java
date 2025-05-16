package com.belman.domain.audit.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when the application is paused.
 * <p>
 * This event is used to track when the application is paused but still visible,
 * which is useful for analytics and debugging purposes.
 */
public class ApplicationPausedAuditEvent extends ApplicationStateAuditEvent {

    /**
     * Creates a new ApplicationPausedAuditEvent.
     */
    public ApplicationPausedAuditEvent() {
        super(ApplicationState.PAUSED);
    }

    /**
     * Creates a new ApplicationPausedAuditEvent with the specified ID and timestamp.
     *
     * @param eventId    the unique identifier for this event
     * @param occurredOn the timestamp when this event occurred
     */
    public ApplicationPausedAuditEvent(UUID eventId, Instant occurredOn) {
        super(eventId, occurredOn, ApplicationState.PAUSED);
    }

    @Override
    public String getEventType() {
        return "APPLICATION_PAUSED";
    }
}