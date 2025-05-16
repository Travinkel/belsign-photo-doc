package com.belman.domain.audit.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when the application is stopped.
 * <p>
 * This event is used to track when the application is shutting down,
 * which is useful for analytics and debugging purposes.
 */
public class ApplicationStoppedAuditEvent extends ApplicationStateAuditEvent {

    /**
     * Creates a new ApplicationStoppedAuditEvent.
     */
    public ApplicationStoppedAuditEvent() {
        super(ApplicationState.STOPPING);
    }

    /**
     * Creates a new ApplicationStoppedAuditEvent with the specified ID and timestamp.
     *
     * @param eventId    the unique identifier for this event
     * @param occurredOn the timestamp when this event occurred
     */
    public ApplicationStoppedAuditEvent(UUID eventId, Instant occurredOn) {
        super(eventId, occurredOn, ApplicationState.STOPPING);
    }

    @Override
    public String getEventType() {
        return "APPLICATION_STOPPED";
    }
}