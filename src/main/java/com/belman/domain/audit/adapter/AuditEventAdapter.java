package com.belman.domain.audit.adapter;

import com.belman.domain.audit.event.AuditEvent;
import com.belman.domain.audit.BusinessEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Adapter class that allows an AuditEvent to be used as a BusinessEvent.
 * <p>
 * This adapter implements the BusinessEvent interface and wraps an AuditEvent,
 * delegating the method calls to the wrapped AuditEvent. This allows AuditEvents
 * to be published through the BusinessEventPublisher.
 */
public class AuditEventAdapter implements BusinessEvent {
    private final AuditEvent auditEvent;

    /**
     * Creates a new AuditEventAdapter that wraps the specified AuditEvent.
     *
     * @param auditEvent the AuditEvent to wrap
     */
    public AuditEventAdapter(AuditEvent auditEvent) {
        if (auditEvent == null) {
            throw new IllegalArgumentException("AuditEvent cannot be null");
        }
        this.auditEvent = auditEvent;
    }

    /**
     * Gets the wrapped AuditEvent.
     *
     * @return the wrapped AuditEvent
     */
    public AuditEvent getAuditEvent() {
        return auditEvent;
    }

    @Override
    public UUID getEventId() {
        return auditEvent.getEventId();
    }

    @Override
    public Instant getOccurredOn() {
        return auditEvent.getOccurredOn();
    }

    @Override
    public String getEventType() {
        return auditEvent.getEventType();
    }

    @Override
    public String toString() {
        return "AuditEventAdapter{" +
               "auditEvent=" + auditEvent +
               '}';
    }
}