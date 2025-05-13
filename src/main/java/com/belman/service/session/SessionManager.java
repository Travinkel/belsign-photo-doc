package com.belman.service.session;

import com.belman.domain.events.DomainEvents;
import com.belman.domain.events.UserLoggedInEvent;
import com.belman.domain.events.UserLoggedOutEvent;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.user.UserBusiness;
import com.belman.common.logging.EmojiLoggerFactory;
import com.belman.service.base.BaseService;

import java.util.Optional;

/**
 * Manages user sessions and provides access to the current user.
 * This is a singleton service that can be accessed from anywhere in the application.
 */
public class SessionManager extends BaseService {
    private static SessionManager instance;
    private final AuthenticationService authenticationService;

    /**
     * Creates a new SessionManager with the specified AuthenticationService.
     *
     * @param authenticationService the authentication service
     */
    private SessionManager(AuthenticationService authenticationService) {
        super(EmojiLoggerFactory.getInstance());
        this.authenticationService = authenticationService;

        // Register event handlers
        registerEventHandlers();
    }

    /**
     * Registers event handlers for session-related events.
     */
    private void registerEventHandlers() {
        // TODO: Fix event handling mechanism for BusinessEvent objects
        // Handle user login events
        // DomainEvents.on(UserLoggedInEvent.class, this::handleUserLoggedIn);

        // Handle user logout events
        // DomainEvents.on(UserLoggedOutEvent.class, this::handleUserLoggedOut);
    }

    /**
     * Handles a UserLoggedInEvent.
     *
     * @param event the event to handle
     */
    private void handleUserLoggedIn(UserLoggedInEvent event) {
        logInfo("User logged in: {}", event.getUsername().value());
    }

    /**
     * Handles a UserLoggedOutEvent.
     *
     * @param event the event to handle
     */
    private void handleUserLoggedOut(UserLoggedOutEvent event) {
        logInfo("User logged out: {}", event.getUsername().value());
    }

    /**
     * Gets the singleton instance of the SessionManager.
     *
     * @param authenticationService the authentication service to use (only used if the instance doesn't exist yet)
     * @return the SessionManager instance
     */
    public static synchronized SessionManager getInstance(AuthenticationService authenticationService) {
        if (instance == null) {
            if (authenticationService == null) {
                throw new IllegalArgumentException("AuthenticationService cannot be null");
            }
            instance = new SessionManager(authenticationService);
        }
        return instance;
    }

    /**
     * Gets the singleton instance of the SessionManager.
     *
     * @return the SessionManager instance, or null if it hasn't been initialized yet
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            System.err.println("WARNING: SessionManager.getInstance() called before initialization");
        }
        return instance;
    }

    /**
     * Gets the currently authenticated user.
     *
     * @return an Optional containing the authenticated User if a user is logged in, or empty if no user is logged in
     */
    public Optional<UserBusiness> getCurrentUser() {
        return authenticationService.getCurrentUser();
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return authenticationService.isLoggedIn();
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        authenticationService.logout();
    }

    /**
     * Authenticates a user with the given username and password.
     *
     * @param username the username
     * @param password the password
     * @return an Optional containing the authenticated User if successful, or empty if authentication failed
     */
    public Optional<UserBusiness> login(String username, String password) {
        if (authenticationService == null) {
            logError("AuthenticationService is null. Cannot perform login.");
            return Optional.empty();
        }

        try {
            logInfo("Attempting to authenticate user: {}", username);
            Optional<UserBusiness> user = authenticationService.authenticate(username, password);

            if (user.isPresent()) {
                logInfo("Authentication successful for user: {}", username);
            } else {
                logWarn("Authentication failed for user: {}", username);
            }

            return user;
        } catch (Exception e) {
            logError("Error during authentication: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}
