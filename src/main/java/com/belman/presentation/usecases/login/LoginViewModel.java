package com.belman.presentation.usecases.login;

import com.belman.common.di.Inject;
import com.belman.common.logging.AuthLoggingService;
import com.belman.common.session.SessionContext;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.security.ExtendedAuthenticationService;
import com.belman.domain.user.UserBusiness;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.RoleBasedNavigationService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Optional;

/**
 * ViewModel for the login screen.
 * This class handles the business logic for user authentication.
 */
public class LoginViewModel extends BaseViewModel<LoginViewModel> {
    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final BooleanProperty loginSuccessful = new SimpleBooleanProperty(false);
    private final BooleanProperty devMode = new SimpleBooleanProperty(true); // Set to true for school project

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private ExtendedAuthenticationService extendedAuthenticationService;

    @Inject
    private RoleBasedNavigationService navigationService;

    /**
     * Gets the username property.
     *
     * @return the username property
     */
    public StringProperty usernameProperty() {
        return username;
    }

    /**
     * Gets the password property.
     *
     * @return the password property
     */
    public StringProperty passwordProperty() {
        return password;
    }

    /**
     * Gets the error message property.
     *
     * @return the error message property
     */
    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    /**
     * Gets the loading property.
     *
     * @return the loading property
     */
    public BooleanProperty loadingProperty() {
        return loading;
    }

    /**
     * Gets the login successful property.
     *
     * @return the login successful property
     */
    public BooleanProperty loginSuccessfulProperty() {
        return loginSuccessful;
    }

    /**
     * Gets the development mode property.
     * This property controls whether development features are visible.
     *
     * @return the development mode property
     */
    public BooleanProperty devModeProperty() {
        return devMode;
    }

    /**
     * Attempts to log in with the provided username and password.
     */
    public void login() {
        AuthLoggingService.logAuth("LoginViewModel", "Login attempt with username: " + username.get());

        // Clear any previous error message
        errorMessage.set("");

        // Validate input
        if (username.get().isEmpty() || password.get().isEmpty()) {
            errorMessage.set("Username and password are required");
            AuthLoggingService.logAuth("LoginViewModel", "Login failed: Username or password is empty");
            return;
        }

        // Set loading state
        loading.set(true);
        AuthLoggingService.logAuth("LoginViewModel", "Authenticating user: " + username.get());

        try {
            // Attempt to authenticate
            AuthLoggingService.logAuth("LoginViewModel", "Calling authenticationService.authenticate for user: " + username.get());
            Optional<UserBusiness> user = authenticationService.authenticate(username.get(), password.get());

            if (user.isPresent()) {
                // Authentication successful
                loginSuccessful.set(true);
                AuthLoggingService.logAuth("LoginViewModel", "Authentication successful for user: " + username.get() + ", ID: " + user.get().getId().id() + ", Roles: " + user.get().getRoles());

                // Set the current user in the session context
                AuthLoggingService.logSession("LoginViewModel", "Setting current user in SessionContext: " + username.get() + ", ID: " + user.get().getId().id());
                SessionContext.setCurrentUser(user.get());

                // Navigate to the appropriate view based on user role
                AuthLoggingService.logNavigation("LoginViewModel", "Navigating to user home for user: " + username.get() + ", Roles: " + user.get().getRoles());

                // Check if navigationService is null
                if (navigationService == null) {
                    AuthLoggingService.logError("LoginViewModel", "Navigation service is null, cannot navigate to user home");
                    errorMessage.set("System error: Navigation service unavailable");
                    return;
                }

                navigationService.navigateToUserHome();
                AuthLoggingService.logNavigation("LoginViewModel", "Navigation to user home completed");
            } else {
                // Authentication failed
                errorMessage.set("Invalid username or password");
                loginSuccessful.set(false);
                AuthLoggingService.logAuth("LoginViewModel", "Authentication failed: Invalid username or password for user: " + username.get());
            }
        } catch (Exception e) {
            // Handle authentication errors
            errorMessage.set("Authentication error: " + e.getMessage());
            loginSuccessful.set(false);
            AuthLoggingService.logError("LoginViewModel", "Authentication error: " + e.getMessage());
        } finally {
            // Reset loading state
            loading.set(false);
        }
    }

    /**
     * Attempts to log in with NFC.
     *
     * @param nfcId the NFC ID
     */
    public void loginWithNfc(String nfcId) {
        AuthLoggingService.logAuth("LoginViewModel", "NFC login attempt with ID: " + nfcId);

        // Clear any previous error message
        errorMessage.set("");

        // Validate input
        if (nfcId == null || nfcId.isEmpty()) {
            errorMessage.set("NFC ID is required");
            AuthLoggingService.logAuth("LoginViewModel", "NFC login failed: NFC ID is empty");
            return;
        }

        // Set loading state
        loading.set(true);
        AuthLoggingService.logAuth("LoginViewModel", "Authenticating with NFC ID: " + nfcId);

        try {
            // Attempt to authenticate with NFC
            AuthLoggingService.logAuth("LoginViewModel", "Calling extendedAuthenticationService.authenticateWithNfc with ID: " + nfcId);
            Optional<UserBusiness> user = extendedAuthenticationService.authenticateWithNfc(nfcId);

            if (user.isPresent()) {
                // Authentication successful
                loginSuccessful.set(true);
                AuthLoggingService.logAuth("LoginViewModel", "NFC authentication successful for user: " + user.get().getUsername().value() + ", ID: " + user.get().getId().id() + ", Roles: " + user.get().getRoles());

                // Set the current user in the session context
                AuthLoggingService.logSession("LoginViewModel", "Setting current user in SessionContext from NFC: " + user.get().getUsername().value() + ", ID: " + user.get().getId().id());
                SessionContext.setCurrentUser(user.get());

                // Navigate to the appropriate view based on user role
                AuthLoggingService.logNavigation("LoginViewModel", "Navigating to user home for NFC user: " + user.get().getUsername().value() + ", Roles: " + user.get().getRoles());

                // Check if navigationService is null
                if (navigationService == null) {
                    AuthLoggingService.logError("LoginViewModel", "Navigation service is null, cannot navigate to user home for NFC user");
                    errorMessage.set("System error: Navigation service unavailable");
                    return;
                }

                navigationService.navigateToUserHome();
                AuthLoggingService.logNavigation("LoginViewModel", "Navigation to user home completed for NFC user");
            } else {
                // Authentication failed
                errorMessage.set("Invalid NFC ID");
                loginSuccessful.set(false);
                AuthLoggingService.logAuth("LoginViewModel", "NFC authentication failed: Invalid NFC ID: " + nfcId);
            }
        } catch (Exception e) {
            // Handle authentication errors
            errorMessage.set("NFC authentication error: " + e.getMessage());
            loginSuccessful.set(false);
            AuthLoggingService.logError("LoginViewModel", "NFC authentication error: " + e.getMessage());
        } finally {
            // Reset loading state
            loading.set(false);
        }
    }

    @Override
    public void onShow() {
        // No need to update the app bar title as it's handled in the view
    }
}