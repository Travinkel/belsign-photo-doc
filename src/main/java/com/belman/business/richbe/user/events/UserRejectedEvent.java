package com.belman.business.richbe.user.events;

import com.belman.business.richbe.events.DomainEvent;
import com.belman.business.richbe.user.UserId;

import java.time.Instant;
import java.util.UUID;

class UserRejectedEvent implements DomainEvent {
    private final UserId userId;
    private final UserId reviewerId;
    private final Instant reviewedAt;
    private final String reason;
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredOn = Instant.now();

    public UserRejectedEvent(UserId userId, UserId reviewerId, Instant reviewedAt, String reason) {
        this.userId = userId;
        this.reviewerId = reviewerId;
        this.reviewedAt = reviewedAt;
        this.reason = reason;
    }

    @Override
    public UUID getEventId() {
        return UUID.randomUUID();
    }

    @Override
    public Instant getOccurredOn() {
        return Instant.now();
    }

    @Override
    public String getEventType() {
        return "UserRejectedEvent";
    }
}
