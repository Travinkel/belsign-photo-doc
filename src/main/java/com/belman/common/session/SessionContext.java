package com.belman.common.session;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.domain.user.UserBusiness;

import java.util.Optional;

/**
 * Interface for the session context used in the application.
 * This interface defines the methods that can be called on the session context
 * during the user session.
 */
public interface SessionContext {

    /**
     * Gets the current user.
     *
     * @return an Optional containing the current user if one is set, or empty if no user is set
     */
    Optional<UserBusiness> getUser();

    /**
     * Sets the current user.
     *
     * @param user the user to set
     */
    void setUser(UserBusiness user);

    /**
     * Gets the current session state.
     *
     * @return the current session state
     */
    SessionState getState();

    /**
     * Sets the current session state.
     *
     * @param state the state to set
     */
    void setState(SessionState state);

    /**
     * Logs a message related to the session.
     *
     * @param message the message to log
     */
    void logEvent(String message);

    /**
     * Navigates to the appropriate home view based on the user's role.
     */
    void navigateToUserHome();

    /**
     * Navigates to the login view.
     */
    void navigateToLogin();

    /**
     * Refreshes the current session.
     */
    void refreshSession();

    /**
     * Checks if the current session is valid.
     *
     * @return true if the session is valid, false otherwise
     */
    boolean isSessionValid();

    /**
     * Static method to set the current user in the session.
     * This is a simplified way to manage the session.
     *
     * @param user the user to set
     */
    static void setCurrentUser(UserBusiness user) {
        SessionContext context = ServiceLocator.getService(SessionContext.class);
        if (context != null) {
            context.setUser(user);
        }
    }

    /**
     * Static method to get the current user from the session.
     * This is a simplified way to access the current user.
     *
     * @return an Optional containing the current user if one is set, or empty if no user is set
     */
    static Optional<UserBusiness> getCurrentUser() {
        SessionContext context = ServiceLocator.getService(SessionContext.class);
        if (context != null) {
            return context.getUser();
        }
        return Optional.empty();
    }

    /**
     * Static method to clear the current session.
     * This is a simplified way to log out the current user.
     */
    static void clear() {
        SessionContext context = ServiceLocator.getService(SessionContext.class);
        if (context != null) {
            context.setUser(null);
        }
    }
}
