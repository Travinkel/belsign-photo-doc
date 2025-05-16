package com.belman.ui.session;

import com.belman.domain.user.UserBusiness;

/**
 * State representing a logged in session.
 * This state is active when a user is successfully authenticated.
 */
public class LoggedInState implements SessionState {
    private final UserBusiness user;

    /**
     * Creates a new LoggedInState with the specified user.
     *
     * @param user the authenticated user
     */
    public LoggedInState(UserBusiness user) {
        this.user = user;
    }

    @Override
    public void handle(SessionContext context) {
        // Nothing to do in the logged in state
        // This state is active when a user is successfully authenticated
        context.logEvent("Session is in logged in state for user: " + user.getUsername().value());
    }

    @Override
    public String getName() {
        return "LoggedIn";
    }

    /**
     * Gets the authenticated user.
     *
     * @return the authenticated user
     */
    public UserBusiness getUser() {
        return user;
    }
}