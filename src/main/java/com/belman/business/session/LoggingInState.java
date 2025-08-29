package com.belman.business.session;

import com.belman.domain.user.UserBusiness;

import java.util.Optional;

/**
 * State representing a session in the process of logging in.
 * This state is active during the login process.
 */
public class LoggingInState implements SessionState {
    private final String username;
    private final String password;

    /**
     * Creates a new LoggingInState with the specified username and password.
     *
     * @param username the username
     * @param password the password
     */
    public LoggingInState(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void handle(SessionContext context) {
        context.logEvent("Attempting to log in user: " + username);

        // Get the SessionService from the context
        // We need to cast to DefaultSessionContext to access the getSessionService method
        if (context instanceof DefaultSessionContext) {
            DefaultSessionContext defaultContext = (DefaultSessionContext) context;
            SessionService sessionService = defaultContext.getSessionService();

            // Attempt to log in
            Optional<UserBusiness> userOpt = sessionService.login(username, password);

            if (userOpt.isPresent()) {
                // Login successful
                UserBusiness user = userOpt.get();
                context.setUser(user);
                context.logEvent("Login successful for user: " + username);
                context.setState(new LoggedInState(user));
            } else {
                // Login failed
                context.logEvent("Login failed for user: " + username);
                context.setState(new LoggedOutState());
            }
        } else {
            // If the context is not a DefaultSessionContext, we can't proceed
            context.logEvent("Error: SessionContext is not a DefaultSessionContext");
            context.setState(new LoggedOutState());
        }
    }

    @Override
    public String getName() {
        return "LoggingIn";
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }
}
