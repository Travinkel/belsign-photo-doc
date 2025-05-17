package com.belman.common.session;

import com.belman.domain.security.AuthenticationService;
import com.belman.domain.user.UserBusiness;
import com.belman.presentation.navigation.RoleBasedNavigationService;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.archive.authentication.login.LoginView;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * A simple implementation of the SessionContext interface.
 * This class provides a basic session context that uses AuthenticationService directly.
 */
public class SimpleSessionContext implements SessionContext {
    private final AuthenticationService authenticationService;
    private SessionState currentState;
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
        return authenticationService.getCurrentUser();
    }

    @Override
    public void setUser(UserBusiness user) {
        // This simple implementation doesn't store the user directly
        // as it relies on the AuthenticationService
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
        // Create a RoleBasedNavigationService and use it to navigate
        RoleBasedNavigationService navigationService = new RoleBasedNavigationService(this);
        navigationService.navigateToUserHome();
    }

    @Override
    public void navigateToLogin() {
        Router.navigateTo(LoginView.class);
    }

    @Override
    public void refreshSession() {
        // This simple implementation doesn't do anything for session refresh
    }

    @Override
    public boolean isSessionValid() {
        return authenticationService.isLoggedIn();
    }
}