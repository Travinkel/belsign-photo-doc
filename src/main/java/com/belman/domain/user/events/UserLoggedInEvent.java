package com.belman.domain.user.events;

import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.Username;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when a user logs in.
 */
public class UserLoggedInEvent extends BaseAuditEvent {
    private final Username username;
    private final UserId userId;

    /**
     * Creates a new UserLoggedInEvent with the specified user.
     *
     * @param user the user who logged in
     */
    public UserLoggedInEvent(UserBusiness user) {
        super();
        this.username = user.getUsername();
        this.userId = user.getId();
    }

    /**
     * Constructor for event deserialization/reconstitution.
     *
     * @param eventId    the ID of the event
     * @param occurredOn the timestamp when the event occurred
     * @param username   the username of the user who logged in
     * @param userId     the ID of the user who logged in
     */
    public UserLoggedInEvent(UUID eventId, Instant occurredOn, Username username, UserId userId) {
        super(eventId, occurredOn);
        this.username = username;
        this.userId = userId;
    }

    /**
     * Gets the username of the user who logged in.
     *
     * @return the username
     */
    public Username getUsername() {
        return username;
    }

    /**
     * Gets the ID of the user who logged in.
     *
     * @return the user ID
     */
    public UserId getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return String.format("UserLoggedInEvent[username=%s, userId=%s, timestamp=%s, id=%s]",
                username.value(), userId, getOccurredOn(), getEventId());
    }
}
