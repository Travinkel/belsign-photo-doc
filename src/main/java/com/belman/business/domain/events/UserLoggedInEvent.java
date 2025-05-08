package com.belman.business.domain.events;


import com.belman.business.domain.user.UserAggregate;
import com.belman.business.domain.user.Username;

/**
 * Event that is published when a user logs in.
 */
public class UserLoggedInEvent extends AbstractDomainEvent {
    private final Username username;
    
    /**
     * Creates a new UserLoggedInEvent with the specified user.
     * 
     * @param user the user who logged in
     */
    public UserLoggedInEvent(UserAggregate user) {
        super();
        this.username = user.getUsername();
    }
    
    /**
     * Gets the username of the user who logged in.
     * 
     * @return the username
     */
    public Username getUsername() {
        return username;
    }
    
    @Override
    public String toString() {
        return String.format("UserLoggedInEvent[username=%s, timestamp=%s, id=%s]",
                username.value(), getEventId());
    }
}