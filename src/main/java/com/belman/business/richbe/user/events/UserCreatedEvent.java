package com.belman.business.richbe.user.events;

import com.belman.business.richbe.user.UserId;
import com.belman.business.richbe.user.Username;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain event that is published when a user is created.
 */
public final class UserCreatedEvent {
    private final String eventId;
    private final Instant timestamp;
    private final UserId userId;
    private final Username username;

    /**
     * Creates a new UserCreatedEvent.
     *
     * @param userId   the ID of the user that was created
     * @param username the username of the user that was created
     */
    public UserCreatedEvent(UserId userId, Username username) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
    }

    /**
     * Gets the unique identifier of this event.
     *
     * @return the event ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Gets the timestamp when this event occurred.
     *
     * @return the timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
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