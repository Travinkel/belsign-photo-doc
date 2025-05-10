package com.belman.domain.user.events;

import com.belman.domain.events.BaseAuditEvent;
import com.belman.domain.user.UserId;
import com.belman.domain.user.Username;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Audit event that is published when a user is created.
 */
public final class UserCreatedEvent extends BaseAuditEvent {
    private final UserId userId;
    private final Username username;

    /**
     * Creates a new UserCreatedEvent.
     *
     * @param userId   the ID of the user that was created
     * @param username the username of the user that was created
     */
    public UserCreatedEvent(UserId userId, Username username) {
        super();
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
    }

    /**
     * Constructor for event deserialization/reconstitution.
     *
     * @param eventId    the ID of the event
     * @param occurredOn the timestamp when the event occurred
     * @param userId     the ID of the user that was created
     * @param username   the username of the user that was created
     */
    public UserCreatedEvent(UUID eventId, Instant occurredOn, UserId userId, Username username) {
        super(eventId, occurredOn);
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
    }

    /**
     * Gets the ID of the user that was created.
     *
     * @return the user ID
     */
    public UserId getUserId() {
        return userId;
    }

    /**
     * Gets the username of the user that was created.
     *
     * @return the username
     */
    public Username getUsername() {
        return username;
    }
}
