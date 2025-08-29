package com.belman.business.session;

/**
 * State representing a session being refreshed.
 * This state is active during session refresh.
 */
public class SessionRefreshingState implements SessionState {

    @Override
    public void handle(SessionContext context) {
        context.logEvent("Refreshing session");
        
        // Refresh the session
        context.refreshSession();
        
        // Check if the session is still valid after refresh
        if (context.isSessionValid()) {
            context.logEvent("Session refreshed successfully");
            
            // If the session is valid, transition to logged in state
            context.getUser().ifPresent(user -> {
                context.setState(new LoggedInState(user));
            });
        } else {
            context.logEvent("Session refresh failed");
            
            // If the session is not valid, transition to session expired state
            context.setState(new SessionExpiredState());
        }
    }

    @Override
    public String getName() {
        return "SessionRefreshing";
    }
}