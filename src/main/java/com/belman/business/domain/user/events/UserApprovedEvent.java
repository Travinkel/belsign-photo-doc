package com.belman.business.domain.user.events;

import com.belman.business.domain.events.DomainEvent;
import com.belman.business.domain.user.UserId;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

class UserApprovedEvent implements DomainEvent {
    private final UserId userId;
    private final UserId reviewerId;
    private final ZonedDateTime reviewedAt;

    public UserApprovedEvent(UserId userId, UserId reviewerId, ZonedDateTime reviewedAt) {
        this.userId = userId;
        this.reviewerId = reviewerId;
        this.reviewedAt = reviewedAt;
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
        return "UserApprovedEvent";
    }
}
