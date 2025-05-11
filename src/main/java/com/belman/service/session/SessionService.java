package com.belman.service.session;

import com.belman.domain.user.UserBusiness;

import java.util.Optional;

/**
 * Service interface for session management.
 * This interface defines the methods for managing user sessions.
 */
public interface SessionService {

    /**
     * Logs in a user with the given username and password.
     *
     * @param username the username
     * @param password the password
     * @return an Optional containing the authenticated User if successful, or empty if authentication failed
     */
    Optional<UserBusiness> login(String username, String password);

    /**
     * Logs out the current user.
     */
    void logout();

    /**
     * Gets the currently authenticated user.
     *
     * @return an Optional containing the authenticated User if a user is logged in, or empty if no user is logged in
     */
    Optional<UserBusiness> getCurrentUser();

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    boolean isLoggedIn();

    /**
     * Refreshes the current session.
     */
    void refreshSession();
}