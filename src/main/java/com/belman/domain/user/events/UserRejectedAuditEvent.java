package com.belman.domain.user.events;

import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.domain.user.UserId;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when a user is rejected.
 */
public class UserRejectedAuditEvent extends BaseAuditEvent {

    private final UserId userId;
    private final UserId rejecterId;
    private final String reason;

    /**
     * Creates a new UserRejectedAuditEvent with the specified user ID, rejecter ID, and reason.
     *
     * @param userId     the ID of the user who was rejected
     * @param rejecterId the ID of the user who rejected the user
     * @param reason     the reason for rejection
     */
    public UserRejectedAuditEvent(UserId userId, UserId rejecterId, String reason) {
        super(UUID.randomUUID(), Instant.now());
        this.userId = userId;
        this.rejecterId = rejecterId;
        this.reason = reason;
    }

    /**
     * Gets the ID of the user who was rejected.
     *
     * @return the user ID
     */
    public UserId getUserId() {
        return userId;
    }

    /**
     * Gets the ID of the user who rejected the user.
     *
     * @return the rejecter ID
     */
    public UserId getRejecterId() {
        return rejecterId;
    }

    /**
     * Gets the reason for rejection.
     *
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    @Override
    public String getEventType() {
        return "UserRejected";
    }
}