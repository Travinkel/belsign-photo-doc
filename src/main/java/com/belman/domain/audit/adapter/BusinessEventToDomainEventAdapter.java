package com.belman.domain.audit.adapter;

import com.belman.domain.audit.BusinessEvent;
import com.belman.domain.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Adapter class that allows a BusinessEvent to be used as a DomainEvent.
 * <p>
 * This adapter implements the DomainEvent interface and wraps a BusinessEvent,
 * delegating the method calls to the wrapped BusinessEvent. This allows BusinessEvents
 * to be handled by DomainEventHandlers.
 */
public class BusinessEventToDomainEventAdapter implements DomainEvent {
    private final BusinessEvent businessEvent;

    /**
     * Creates a new BusinessEventToDomainEventAdapter that wraps the specified BusinessEvent.
     *
     * @param businessEvent the BusinessEvent to wrap
     */
    public BusinessEventToDomainEventAdapter(BusinessEvent businessEvent) {
        if (businessEvent == null) {
            throw new IllegalArgumentException("BusinessEvent cannot be null");
        }
        this.businessEvent = businessEvent;
    }

    /**
     * Gets the wrapped BusinessEvent.
     *
     * @return the wrapped BusinessEvent
     */
    public BusinessEvent getBusinessEvent() {
        return businessEvent;
    }

    @Override
    public UUID getEventId() {
        return businessEvent.getEventId();
    }

    @Override
    public Instant getOccurredOn() {
        return businessEvent.getOccurredOn();
    }

    @Override
    public String getEventType() {
        return businessEvent.getEventType();
    }

    @Override
    public String toString() {
        return "BusinessEventToDomainEventAdapter{" +
               "businessEvent=" + businessEvent +
               '}';
    }
}