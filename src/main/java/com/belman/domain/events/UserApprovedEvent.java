package com.belman.domain.events;

import com.belman.domain.user.ApprovalStatus;
import java.util.UUID;
import java.time.Instant;

public class UserApprovedEvent implements DomainEvent {

    private final UUID eventId;
    private final Instant occurredOn;
    private final String eventType;
    private final ApprovalStatus approvalStatus;

    public UserApprovedEvent(ApprovalStatus approvalStatus) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.eventType = "UserApprovedEvent";
        this.approvalStatus = approvalStatus;
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }
}