package com.belman.domain.audit.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when the application is started.
 * <p>
 * This event is used to track when the application starts up,
 * which is useful for analytics and debugging purposes.
 */
public class ApplicationStartedAuditEvent extends ApplicationStateAuditEvent {

    /**
     * Creates a new ApplicationStartedAuditEvent.
     */
    public ApplicationStartedAuditEvent() {
        super(ApplicationState.STARTING);
    }

    /**
     * Creates a new ApplicationStartedAuditEvent with the specified ID and timestamp.
     *
     * @param eventId    the unique identifier for this event
     * @param occurredOn the timestamp when this event occurred
     */
    public ApplicationStartedAuditEvent(UUID eventId, Instant occurredOn) {
        super(eventId, occurredOn, ApplicationState.STARTING);
    }

    @Override
    public String getEventType() {
        return "APPLICATION_STARTED";
    }
}