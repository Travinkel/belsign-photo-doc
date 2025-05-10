package com.belman.domain.user.events;

import com.belman.domain.events.BaseAuditEvent;
import com.belman.domain.user.UserId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Audit event that is published when a user is rejected by a reviewer.
 */
public class UserRejectedEvent extends BaseAuditEvent {
    private final UserId userId;
    private final UserId reviewerId;
    private final Instant reviewedAt;
    private final String reason;

    /**
     * Creates a new UserRejectedEvent with the specified user ID, reviewer ID, reviewed timestamp, and reason.
     *
     * @param userId     the ID of the user who was rejected
     * @param reviewerId the ID of the user who rejected the user
     * @param reviewedAt the timestamp when the user was rejected
     * @param reason     the reason for rejection
     */
    public UserRejectedEvent(UserId userId, UserId reviewerId, Instant reviewedAt, String reason) {
        super();
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.reviewerId = Objects.requireNonNull(reviewerId, "reviewerId must not be null");
        this.reviewedAt = Objects.requireNonNull(reviewedAt, "reviewedAt must not be null");
        this.reason = reason; // reason can be null
    }

    /**
     * Constructor for event deserialization/reconstitution.
     *
     * @param eventId    the ID of the event
     * @param occurredOn the timestamp when the event occurred
     * @param userId     the ID of the user who was rejected
     * @param reviewerId the ID of the user who rejected the user
     * @param reviewedAt the timestamp when the user was rejected
     * @param reason     the reason for rejection
     */
    public UserRejectedEvent(UUID eventId, Instant occurredOn, UserId userId, UserId reviewerId, Instant reviewedAt,
                             String reason) {
        super(eventId, occurredOn);
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.reviewerId = Objects.requireNonNull(reviewerId, "reviewerId must not be null");
        this.reviewedAt = Objects.requireNonNull(reviewedAt, "reviewedAt must not be null");
        this.reason = reason; // reason can be null
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
     * @return the reviewer ID
     */
    public UserId getReviewerId() {
        return reviewerId;
    }

    /**
     * Gets the timestamp when the user was rejected.
     *
     * @return the reviewed timestamp
     */
    public Instant getReviewedAt() {
        return reviewedAt;
    }

    /**
     * Gets the reason for rejection.
     *
     * @return the reason, or null if no reason was provided
     */
    public String getReason() {
        return reason;
    }
}
