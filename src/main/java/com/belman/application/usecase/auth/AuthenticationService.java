package com.belman.application.usecase.auth;

import com.belman.domain.user.UserBusiness;

import java.util.Optional;

/**
 * Service for user authentication.
 * Provides methods for logging in, logging out, and checking authentication status.
 */
public interface AuthenticationService {
    /**
     * Authenticates a user with the given username and password.
     *
     * @param username the username
     * @param password the password
     * @return an Optional containing the authenticated user if successful, or empty if authentication failed
     */
    Optional<UserBusiness> authenticate(String username, String password);

    /**
     * Gets the currently authenticated user.
     *
     * @return an Optional containing the authenticated user if a user is logged in, or empty if no user is logged in
     */
    Optional<UserBusiness> getCurrentUser();

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    boolean isLoggedIn();

    /**
     * Logs out the current user.
     */
    void logout();
}