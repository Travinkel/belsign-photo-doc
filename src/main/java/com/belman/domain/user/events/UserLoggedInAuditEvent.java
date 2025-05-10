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
public class UserLoggedInAuditEvent extends BaseAuditEvent {
    private final UserId userId;
    private final Username username;
    private final String ipAddress;

    /**
     * Creates a new UserLoggedInAuditEvent with the specified user and IP address.
     *
     * @param user      the user who logged in
     * @param ipAddress the IP address from which the user logged in
     */
    public UserLoggedInAuditEvent(UserBusiness user, String ipAddress) {
        super(UUID.randomUUID(), Instant.now());
        this.userId = user.getId();
        this.username = user.getUsername();
        this.ipAddress = ipAddress;
    }

    /**
     * Gets the ID of the user who logged in.
     *
     * @return the user ID
     */
    public UserId getUserId() {
        return userId;
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
     * Gets the IP address from which the user logged in.
     *
     * @return the IP address
     */
    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public String getEventType() {
        return "UserLoggedIn";
    }

    @Override
    public String toString() {
        return String.format("UserLoggedInAuditEvent[userId=%s, username=%s, ipAddress=%s, timestamp=%s, id=%s]",
                userId, username.value(), ipAddress, getOccurredOn(), getEventId());
    }
}