package com.belman.domain.event.adapter;

import com.belman.domain.event.BusinessEvent;
import com.belman.domain.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Adapter class that allows a DomainEvent to be used as a BusinessEvent.
 * <p>
 * This adapter implements the BusinessEvent interface and wraps a DomainEvent,
 * delegating the method calls to the wrapped DomainEvent. This allows DomainEvents
 * to be published through the BusinessEventPublisher.
 */
public class DomainEventAdapter implements BusinessEvent {
    private final DomainEvent domainEvent;

    /**
     * Creates a new DomainEventAdapter that wraps the specified DomainEvent.
     *
     * @param domainEvent the DomainEvent to wrap
     */
    public DomainEventAdapter(DomainEvent domainEvent) {
        if (domainEvent == null) {
            throw new IllegalArgumentException("DomainEvent cannot be null");
        }
        this.domainEvent = domainEvent;
    }

    /**
     * Gets the wrapped DomainEvent.
     *
     * @return the wrapped DomainEvent
     */
    public DomainEvent getDomainEvent() {
        return domainEvent;
    }

    @Override
    public UUID getEventId() {
        return domainEvent.getEventId();
    }

    @Override
    public Instant getOccurredOn() {
        return domainEvent.getOccurredOn();
    }

    @Override
    public String getEventType() {
        return domainEvent.getEventType();
    }

    @Override
    public String toString() {
        return "DomainEventAdapter{" +
               "domainEvent=" + domainEvent +
               '}';
    }
}