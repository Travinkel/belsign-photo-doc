package com.belman.domain.events;

import com.belman.domain.user.UserAggregate;
import com.belman.domain.user.Username;
import com.belman.domain.user.Username;

/**
 * Event that is published when a user logs out.
 */
public class UserLoggedOutEvent extends AbstractDomainEvent {
    private final Username username;

    /**
     * Creates a new UserLoggedOutEvent with the specified user.
     *
     * @param user the user who logged out
     */
    public UserLoggedOutEvent(UserAggregate user) {
        super();
        this.username = user.getUsername();
    }

    /**
     * Gets the username of the user who logged out.
     *
     * @return the username
     */
    public Username getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return String.format("UserLoggedOutEvent[username=%s, timestamp=%s, id=%s]",
                username.value(), getEventId());
    }
}