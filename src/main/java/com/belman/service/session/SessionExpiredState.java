package com.belman.service.session;

/**
 * State representing an expired session.
 * This state is active when a session has timed out.
 */
public class SessionExpiredState implements SessionState {

    @Override
    public void handle(SessionContext context) {
        context.logEvent("Session has expired");
        
        // Clear the current user
        context.setUser(null);
        
        // Transition to logged out state
        context.setState(new LoggedOutState());
    }

    @Override
    public String getName() {
        return "SessionExpired";
    }
}