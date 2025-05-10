package com.belman.domain.audit.event;

import com.belman.domain.event.BaseBusinessEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Abstract base class for all audit events.
 * <p>
 * Extends BaseBusinessEvent and implements AuditEvent, providing common functionality
 * for all audit events. This class serves as the base for all audit-specific events
 * in the system.
 */
public abstract class BaseAuditEvent extends BaseBusinessEvent implements AuditEvent {

    /**
     * Creates a new audit event with an auto-generated ID and the current timestamp.
     */
    protected BaseAuditEvent() {
        super();
    }

    /**
     * Creates a new audit event with the specified ID and timestamp.
     *
     * @param eventId    the unique identifier for this event
     * @param occurredOn the timestamp when this event occurred
     */
    protected BaseAuditEvent(UUID eventId, Instant occurredOn) {
        super(eventId, occurredOn);
    }

    // No need to override getEventId(), getOccurredOn(), or getEventType()
    // as they are inherited from BaseBusinessEvent
}
