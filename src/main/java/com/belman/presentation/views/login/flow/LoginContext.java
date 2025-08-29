package com.belman.presentation.views.login.flow;

import com.belman.domain.user.UserBusiness;

import java.util.Optional;

/**
 * Interface for the login context used in the login flow.
 * This interface defines the methods that can be called on the login context
 * during the login process.
 */
public interface LoginContext {

    /**
     * Attempts to log in with the current username and password.
     *
     * @return an Optional containing the authenticated User if successful, or empty if authentication failed
     */
    Optional<UserBusiness> login();

    /**
     * Sets the user that has been authenticated.
     *
     * @param user the authenticated user
     */
    void setUser(UserBusiness user);

    /**
     * Sets whether login is in progress.
     *
     * @param inProgress true if login is in progress, false otherwise
     */
    void setLoginInProgress(boolean inProgress);

    /**
     * Logs a debug message.
     *
     * @param message the debug message
     */
    void logDebug(String message);

    /**
     * Logs a success message.
     *
     * @param message the success message
     */
    void logSuccess(String message);

    /**
     * Logs a failure message.
     *
     * @param message the failure message
     */
    void logFailure(String message);

    /**
     * Sets the current state of the login flow.
     *
     * @param state the new state
     */
    void setState(LoginState state);
}