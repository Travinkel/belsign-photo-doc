package com.belman.domain.user.events;

import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.Username;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when a user logs out.
 */
public class UserLoggedOutAuditEvent extends BaseAuditEvent {
    private final UserId userId;
    private final Username username;
    private final String sessionId;

    /**
     * Creates a new UserLoggedOutAuditEvent with the specified user and session ID.
     *
     * @param user      the user who logged out
     * @param sessionId the ID of the session that was terminated
     */
    public UserLoggedOutAuditEvent(UserBusiness user, String sessionId) {
        super(UUID.randomUUID(), Instant.now());
        this.userId = user.getId();
        this.username = user.getUsername();
        this.sessionId = sessionId;
    }

    /**
     * Gets the ID of the user who logged out.
     *
     * @return the user ID
     */
    public UserId getUserId() {
        return userId;
    }

    /**
     * Gets the username of the user who logged out.
     *
     * @return the username
     */
    public Username getUsername() {
        return username;
    }

    /**
     * Gets the ID of the session that was terminated.
     *
     * @return the session ID
     */
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String getEventType() {
        return "UserLoggedOut";
    }

    @Override
    public String toString() {
        return String.format("UserLoggedOutAuditEvent[userId=%s, username=%s, sessionId=%s, timestamp=%s, id=%s]",
                userId, username.value(), sessionId, getOccurredOn(), getEventId());
    }
}