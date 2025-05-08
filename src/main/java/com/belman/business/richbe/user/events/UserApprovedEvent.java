package com.belman.business.richbe.user.events;

import com.belman.business.richbe.events.AuditEvent;
import com.belman.business.richbe.user.UserId;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Audit event that is published when a user is approved by a reviewer.
 */
class UserApprovedEvent implements AuditEvent {
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
