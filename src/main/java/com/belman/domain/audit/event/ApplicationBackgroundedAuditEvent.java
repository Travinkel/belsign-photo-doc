package com.belman.domain.audit.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when the application is backgrounded.
 * <p>
 * This event is used to track when the application goes to the background,
 * which is useful for analytics and debugging purposes.
 */
public class ApplicationBackgroundedAuditEvent extends ApplicationStateAuditEvent {

    /**
     * Creates a new ApplicationBackgroundedAuditEvent.
     */
    public ApplicationBackgroundedAuditEvent() {
        super(ApplicationState.BACKGROUND);
    }

    /**
     * Creates a new ApplicationBackgroundedAuditEvent with the specified ID and timestamp.
     *
     * @param eventId    the unique identifier for this event
     * @param occurredOn the timestamp when this event occurred
     */
    public ApplicationBackgroundedAuditEvent(UUID eventId, Instant occurredOn) {
        super(eventId, occurredOn, ApplicationState.BACKGROUND);
    }

    @Override
    public String getEventType() {
        return "APPLICATION_BACKGROUNDED";
    }
}