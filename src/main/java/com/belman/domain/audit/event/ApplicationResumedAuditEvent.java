package com.belman.domain.audit.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when the application is resumed.
 * <p>
 * This event is used to track when the application comes to the foreground,
 * which is useful for analytics and debugging purposes.
 */
public class ApplicationResumedAuditEvent extends ApplicationStateAuditEvent {

    /**
     * Creates a new ApplicationResumedAuditEvent.
     */
    public ApplicationResumedAuditEvent() {
        super(ApplicationState.ACTIVE);
    }

    /**
     * Creates a new ApplicationResumedAuditEvent with the specified ID and timestamp.
     *
     * @param eventId    the unique identifier for this event
     * @param occurredOn the timestamp when this event occurred
     */
    public ApplicationResumedAuditEvent(UUID eventId, Instant occurredOn) {
        super(eventId, occurredOn, ApplicationState.ACTIVE);
    }

    @Override
    public String getEventType() {
        return "APPLICATION_RESUMED";
    }
}