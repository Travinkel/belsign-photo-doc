package com.belman.business.core;

// Using the BaseService from the application.core package
// This class is already in the application.core package, so no import needed
import com.belman.business.richbe.events.DomainEventPublisher;
import com.belman.business.richbe.events.UserLoggedInEvent;
import com.belman.business.richbe.events.UserLoggedOutEvent;
import com.belman.business.richbe.security.AuthenticationService;
import com.belman.business.richbe.user.UserAggregate;

import java.util.Optional;

/**
 * Manages user sessions and provides access to the current user.
 * This is a singleton service that can be accessed from anywhere in the application.
 * <p>
 * This class is part of the Backbone framework and provides session management
 * functionality for the application.
 */
public class SessionManager extends BaseService {
    private static volatile SessionManager instance;
    private UserAggregate currentUser;

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

    public static SessionManager getInstance(AuthenticationService authenticationService) {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager(authenticationService);
                }
            }
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
            throw new IllegalStateException("SessionManager has not been initialized. Call getInstance(AuthenticationService) first.");
        }
        return instance;
    }

    public void setCurrentUser(UserAggregate user) {
        this.currentUser = user;
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
        logInfo("User logged in: {}", event.getUsername());
    }

    /**
     * Handles a UserLoggedOutEvent.
     * 
     * @param event the event to handle
     */
    private void handleUserLoggedOut(UserLoggedOutEvent event) {
        logInfo("User logged out: {}", event.getUsername());
    }

    /**
     * Gets the currently authenticated user.
     * 
     * @return an Optional containing the authenticated UserAggregate if a user is logged in, or empty if no user is logged in
     */
    public Optional<UserAggregate> getCurrentUser() {
        return Optional.ofNullable(currentUser);
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
    public Optional<UserAggregate> login(String username, String password) {
        return authenticationService.authenticate(username, password);
    }
}
