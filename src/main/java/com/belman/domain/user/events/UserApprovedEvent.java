package com.belman.domain.user.events;

import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.domain.user.UserId;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Audit event that is published when a user is approved by a reviewer.
 */
public class UserApprovedEvent extends BaseAuditEvent {
    private final UserId userId;
    private final UserId reviewerId;
    private final ZonedDateTime reviewedAt;

    /**
     * Creates a new UserApprovedEvent with the specified user ID, reviewer ID, and reviewed timestamp.
     *
     * @param userId     the ID of the user who was approved
     * @param reviewerId the ID of the user who approved the user
     * @param reviewedAt the timestamp when the user was approved
     */
    public UserApprovedEvent(UserId userId, UserId reviewerId, ZonedDateTime reviewedAt) {
        super();
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.reviewerId = Objects.requireNonNull(reviewerId, "reviewerId must not be null");
        this.reviewedAt = Objects.requireNonNull(reviewedAt, "reviewedAt must not be null");
    }

    /**
     * Constructor for event deserialization/reconstitution.
     *
     * @param eventId    the ID of the event
     * @param occurredOn the timestamp when the event occurred
     * @param userId     the ID of the user who was approved
     * @param reviewerId the ID of the user who approved the user
     * @param reviewedAt the timestamp when the user was approved
     */
    public UserApprovedEvent(UUID eventId, Instant occurredOn, UserId userId, UserId reviewerId,
                             ZonedDateTime reviewedAt) {
        super(eventId, occurredOn);
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.reviewerId = Objects.requireNonNull(reviewerId, "reviewerId must not be null");
        this.reviewedAt = Objects.requireNonNull(reviewedAt, "reviewedAt must not be null");
    }

    /**
     * Gets the ID of the user who was approved.
     *
     * @return the user ID
     */
    public UserId getUserId() {
        return userId;
    }

    /**
     * Gets the ID of the user who approved the user.
     *
     * @return the reviewer ID
     */
    public UserId getReviewerId() {
        return reviewerId;
    }

    /**
     * Gets the timestamp when the user was approved.
     *
     * @return the reviewed timestamp
     */
    public ZonedDateTime getReviewedAt() {
        return reviewedAt;
    }
}
