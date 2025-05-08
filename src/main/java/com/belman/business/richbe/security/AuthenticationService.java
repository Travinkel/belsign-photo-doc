package com.belman.business.richbe.security;

import com.belman.business.richbe.user.UserAggregate;
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
    Optional<UserAggregate> authenticate(String username, String password);

    /**
     * Gets the currently authenticated user.
     *
     * @return an Optional containing the authenticated User if a user is logged in, or empty if no user is logged in
     */
    Optional<UserAggregate> getCurrentUser();

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
}