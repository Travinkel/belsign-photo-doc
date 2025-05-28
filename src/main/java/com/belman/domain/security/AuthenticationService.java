package com.belman.domain.security;

import com.belman.domain.services.Logger;
import com.belman.domain.user.UserBusiness;

import java.util.Optional;

/**
 * Service for authenticating users.
 */
public interface AuthenticationService {
    /**
     * Authenticates a user with the given username and password.
     *
     * @param username the username
     * @param password the password
     * @return an Optional containing the authenticated User if successful, or empty if authentication failed
     */
    Optional<UserBusiness> authenticate(String username, String password);

    /**
     * Gets the currently authenticated user.
     *
     * @return an Optional containing the authenticated User if a user is logged in, or empty if no user is logged in
     */
    Optional<UserBusiness> getCurrentUser();

    /**
     * Logs out the current user.
     */
    void logout();

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    boolean isLoggedIn();

    /**
     * Gets the logger for this service.
     * This method is required to ensure that all implementations have a logger dependency.
     *
     * @return the logger for this service
     */
    Logger getLogger();
}
