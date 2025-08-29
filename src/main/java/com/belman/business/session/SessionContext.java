package com.belman.business.session;

import com.belman.domain.user.UserBusiness;

import java.util.Optional;

/**
 * Interface for the session context used in the application.
 * This interface defines the methods that can be called on the session context
 * during the user session.
 */
public interface SessionContext {

    /**
     * Sets the current user.
     *
     * @param user the user to set
     */
    void setUser(UserBusiness user);

    /**
     * Gets the current user.
     *
     * @return an Optional containing the current user if one is set, or empty if no user is set
     */
    Optional<UserBusiness> getUser();

    /**
     * Sets the current session state.
     *
     * @param state the state to set
     */
    void setState(SessionState state);

    /**
     * Gets the current session state.
     *
     * @return the current session state
     */
    SessionState getState();

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
}