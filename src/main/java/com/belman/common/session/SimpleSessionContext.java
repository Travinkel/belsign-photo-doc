package com.belman.common.session;

import com.belman.common.logging.AuthLoggingService;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.user.UserBusiness;
import com.belman.presentation.navigation.RoleBasedNavigationService;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.authentication.login.LoginView;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * A simple implementation of the SessionContext interface.
 * This class provides a basic session context that uses AuthenticationService directly.
 */
public class SimpleSessionContext implements SessionContext {
    private final AuthenticationService authenticationService;
    private SessionState currentState;
    private UserBusiness currentUser; // Added field to store the current user
    private final Logger logger = Logger.getLogger(SimpleSessionContext.class.getName());

    /**
     * Creates a new SimpleSessionContext with the specified AuthenticationService.
     *
     * @param authenticationService the authentication service to use
     */
    public SimpleSessionContext(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        this.currentState = new SimpleSessionState("LOGGED_OUT");
    }

    @Override
    public Optional<UserBusiness> getUser() {
        AuthLoggingService.logSession("SimpleSessionContext", "Getting user from session");

        // First check if we have a user stored in the currentUser field
        if (currentUser != null) {
            AuthLoggingService.logSession("SimpleSessionContext", "User found in local session storage: " + currentUser.getUsername().value() + ", ID: " + currentUser.getId().id() + ", Roles: " + currentUser.getRoles());
            return Optional.of(currentUser);
        }

        // If no user is stored locally, fall back to authenticationService
        AuthLoggingService.logSession("SimpleSessionContext", "No user found in local session storage, checking authentication service");
        Optional<UserBusiness> user = authenticationService.getCurrentUser();

        if (user.isPresent()) {
            AuthLoggingService.logSession("SimpleSessionContext", "User found in authentication service: " + user.get().getUsername().value() + ", ID: " + user.get().getId().id() + ", Roles: " + user.get().getRoles());
            // Store the user for future use
            this.currentUser = user.get();
            // Update the state to LOGGED_IN
            this.currentState = new SimpleSessionState("LOGGED_IN");
        } else {
            AuthLoggingService.logSession("SimpleSessionContext", "No user found in authentication service");
        }

        return user;
    }

    @Override
    public void setUser(UserBusiness user) {
        if (user != null) {
            AuthLoggingService.logSession("SimpleSessionContext", "Setting user in session: " + user.getUsername().value() + ", ID: " + user.getId().id() + ", Roles: " + user.getRoles());
            // Store the user in the currentUser field
            this.currentUser = user;
            // Also update the state to LOGGED_IN
            this.currentState = new SimpleSessionState("LOGGED_IN");
        } else {
            AuthLoggingService.logSession("SimpleSessionContext", "Clearing user from session (null user)");
            // Clear the currentUser field
            this.currentUser = null;
            // Update the state to LOGGED_OUT
            this.currentState = new SimpleSessionState("LOGGED_OUT");
        }
    }

    @Override
    public SessionState getState() {
        return currentState;
    }

    @Override
    public void setState(SessionState state) {
        this.currentState = state;
    }

    @Override
    public void logEvent(String message) {
        logger.info(message);
    }

    @Override
    public void navigateToUserHome() {
        AuthLoggingService.logNavigation("SimpleSessionContext", "Navigating to user home");

        // Create a RoleBasedNavigationService and use it to navigate
        AuthLoggingService.logNavigation("SimpleSessionContext", "Creating new RoleBasedNavigationService instance");
        RoleBasedNavigationService navigationService = new RoleBasedNavigationService(this);

        try {
            AuthLoggingService.logNavigation("SimpleSessionContext", "Calling navigationService.navigateToUserHome()");
            navigationService.navigateToUserHome();
            AuthLoggingService.logNavigation("SimpleSessionContext", "Navigation to user home completed");
        } catch (Exception e) {
            AuthLoggingService.logError("SimpleSessionContext", "Error navigating to user home: " + e.getMessage());
        }
    }

    @Override
    public void navigateToLogin() {
        AuthLoggingService.logNavigation("SimpleSessionContext", "Navigating to login view");
        try {
            Router.navigateTo(LoginView.class);
            AuthLoggingService.logNavigation("SimpleSessionContext", "Navigation to login view completed");
        } catch (Exception e) {
            AuthLoggingService.logError("SimpleSessionContext", "Error navigating to login view: " + e.getMessage());
        }
    }

    @Override
    public void refreshSession() {
        // This simple implementation doesn't do anything for session refresh
    }

    @Override
    public boolean isSessionValid() {
        AuthLoggingService.logSession("SimpleSessionContext", "Checking if session is valid");
        boolean isValid = authenticationService.isLoggedIn();
        AuthLoggingService.logSession("SimpleSessionContext", "Session is " + (isValid ? "valid" : "invalid"));
        return isValid;
    }
}
