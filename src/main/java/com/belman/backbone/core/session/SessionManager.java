package com.belman.backbone.core.session;

import com.belman.backbone.core.base.BaseService;
import com.belman.backbone.core.events.DomainEvent;
import com.belman.backbone.core.events.DomainEventPublisher;
import com.belman.domain.aggregates.User;
import com.belman.domain.events.UserLoggedInEvent;
import com.belman.domain.events.UserLoggedOutEvent;
import com.belman.domain.services.AuthenticationService;

import java.util.Optional;

/**
 * Manages user sessions and provides access to the current user.
 * This is a singleton service that can be accessed from anywhere in the application.
 * <p>
 * This class is part of the Backbone framework and provides session management
 * functionality for the application.
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
        this.authenticationService = authenticationService;

        // Register event handlers
        registerEventHandlers();
    }

    /**
     * Gets the singleton instance of the SessionManager.
     * 
     * @param authenticationService the authentication service to use (only used if the instance doesn't exist yet)
     * @return the SessionManager instance
     */
    public static synchronized SessionManager getInstance(AuthenticationService authenticationService) {
        if (instance == null) {
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
        return instance;
    }

    /**
     * Registers event handlers for session-related events.
     */
    private void registerEventHandlers() {
        // Handle user login events
        DomainEventPublisher.getInstance().register(UserLoggedInEvent.class, this::handleUserLoggedIn);

        // Handle user logout events
        DomainEventPublisher.getInstance().register(UserLoggedOutEvent.class, this::handleUserLoggedOut);
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
     * Gets the currently authenticated user.
     * 
     * @return an Optional containing the authenticated User if a user is logged in, or empty if no user is logged in
     */
    public Optional<User> getCurrentUser() {
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
    public Optional<User> login(String username, String password) {
        return authenticationService.authenticate(username, password);
    }
}