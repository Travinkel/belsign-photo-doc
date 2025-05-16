package com.belman.ui.session;

/**
 * State representing a logged out session.
 * This is the initial state for the session context.
 */
public class LoggedOutState implements SessionState {

    @Override
    public void handle(SessionContext context) {
        // Nothing to do in the logged out state
        // This is the initial state, so we just wait for a login attempt
        context.logEvent("Session is in logged out state");
    }

    @Override
    public String getName() {
        return "LoggedOut";
    }
}