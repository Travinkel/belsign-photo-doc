package com.belman.domain.events;

import java.time.Instant;
import java.util.UUID;

public class UserRejectedEvent implements DomainEvent {

    private final UUID eventId;
    private final Instant occurredOn;
    private final String eventType = "UserRejectedEvent";

    public UserRejectedEvent() {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
    }

    @Override
    public UUID getEventId() {
        return this.eventId;
    }

    @Override
    public Instant getOccurredOn() {
        return this.occurredOn;
    }

    @Override
    public String getEventType() {
        return this.eventType;
    }
}