package com.belman.ui.views.login;

import com.belman.common.logging.EmojiLogger;
import com.belman.domain.user.UserBusiness;
import com.belman.service.session.*;
import com.belman.ui.base.BaseViewModel;
import com.belman.ui.navigation.RoleBasedNavigationService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Optional;
import java.util.prefs.Preferences;

/**
 * ViewModel for the login view.
 */
public class LoginViewModel extends BaseViewModel<LoginViewModel> {
    // Constants for preferences
    private static final String PREF_USERNAME = "username";
    private static final String PREF_REMEMBER_ME = "rememberMe";
    private final EmojiLogger logger = EmojiLogger.getLogger(LoginViewModel.class);
    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty loginInProgress = new SimpleBooleanProperty(false);
    private final BooleanProperty rememberMe = new SimpleBooleanProperty(false);
    private final SessionManager sessionManager;
    private final RoleBasedNavigationService navigationService;
    private final Preferences preferences = Preferences.userNodeForPackage(LoginViewModel.class);

    /**
     * Creates a new LoginViewModel with the default SessionManager and a new RoleBasedNavigationService.
     */
    public LoginViewModel() {
        // Get the SessionManager instance
        sessionManager = SessionManager.getInstance();

        // Create a SessionService
        SessionService sessionService = new DefaultSessionService(sessionManager);

        // Create a SessionContext
        SessionContext sessionContext = new DefaultSessionContext(sessionService);

        // Create a RoleBasedNavigationService with the SessionContext
        navigationService = new RoleBasedNavigationService(sessionContext);
    }

    /**
     * Creates a new LoginViewModel with the specified SessionManager and RoleBasedNavigationService.
     *
     * @param sessionManager    the session manager
     * @param navigationService the role-based navigation service
     */
    public LoginViewModel(SessionManager sessionManager, RoleBasedNavigationService navigationService) {
        this.sessionManager = sessionManager;
        this.navigationService = navigationService;
    }

    @Override
    public void onShow() {
        // No need to update the app bar title as we want to hide the app bar

        // Load saved username and "Remember Me" preference
        boolean savedRememberMe = preferences.getBoolean(PREF_REMEMBER_ME, false);
        rememberMe.set(savedRememberMe);

        if (savedRememberMe) {
            String savedUsername = preferences.get(PREF_USERNAME, "");
            username.set(savedUsername);
        }
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
     * Gets the error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage.get();
    }

    /**
     * Sets the error message.
     *
     * @param errorMessage the error message to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage.set(errorMessage);
    }

    /**
     * Attempts to log in with the current username and password.
     */
    public void login() {
        // Clear any previous error message
        errorMessage.set("");

        logger.debug("Login attempt started for user: {}", username.get());

        // Check if username and password are provided
        if (username.get() == null || username.get().isBlank()) {
            logger.warn("Login failed: Username is required");
            errorMessage.set("Username is required");
            return;
        }

        if (password.get() == null || password.get().isBlank()) {
            logger.warn("Login failed: Password is required for user: {}", username.get());
            errorMessage.set("Password is required");
            return;
        }

        // Set login in progress
        loginInProgress.set(true);
        logger.debug("Login in progress for user: {}", username.get());

        try {
            // Attempt to log in
            logger.debug("Calling sessionManager.login for user: {}", username.get());
            Optional<UserBusiness> userOpt = sessionManager.login(username.get(), password.get());

            if (userOpt.isPresent()) {
                // Login successful, navigate to role-specific view
                UserBusiness user = userOpt.get();
                logger.success("Login successful for user: " + username.get());

                // Save username and "Remember Me" preference if "Remember Me" is checked
                if (rememberMe.get()) {
                    preferences.put(PREF_USERNAME, username.get());
                    preferences.putBoolean(PREF_REMEMBER_ME, true);
                    logger.debug("Saved username and 'Remember Me' preference");
                } else {
                    // Clear saved username and "Remember Me" preference
                    preferences.remove(PREF_USERNAME);
                    preferences.putBoolean(PREF_REMEMBER_ME, false);
                    logger.debug("Cleared saved username and 'Remember Me' preference");
                }

                try {
                    // Use the RoleBasedNavigationService to navigate to the appropriate view based on user role
                    logger.debug("Navigating to user home view");
                    navigationService.navigateToUserHome();
                    logger.debug("Navigation to user home view completed");
                } catch (Exception e) {
                    logger.error("Failed to navigate to role-specific view", e);
                    errorMessage.set("Navigation error: " + e.getMessage());
                }
            } else {
                // Provide more specific error messages based on the username
                String usernameStr = username.get();

                // Check if the username looks valid (simple heuristic)
                if (usernameStr.length() < 3 || !usernameStr.matches("[a-zA-Z0-9_]+")) {
                    // Username is likely invalid
                    logger.warn("Login failed: User not found: {}", usernameStr);
                    errorMessage.set("User not found. Please check your username.");
                } else {
                    // Username looks valid, so password is likely incorrect or account is locked
                    logger.warn("Login failed: Invalid password or account locked for user: {}", usernameStr);
                    errorMessage.set(
                            "Login failed. This could be due to an incorrect password or your account may be locked due to too many failed attempts. Please try again later or contact an administrator.");
                }
            }
        } catch (Exception e) {
            // Handle any exceptions
            logger.error("Exception during login for user: {}", username.get(), e);
            errorMessage.set("An error occurred during login. Please try again later.");
        } finally {
            // Clear login in progress
            loginInProgress.set(false);
            logger.debug("Login process completed for user: {}", username.get());
        }
    }

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
     * Gets the login in progress property.
     *
     * @return the login in progress property
     */
    public BooleanProperty loginInProgressProperty() {
        return loginInProgress;
    }

    /**
     * Gets the remember me property.
     *
     * @return the remember me property
     */
    public BooleanProperty rememberMeProperty() {
        return rememberMe;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username.get();
    }

    /**
     * Sets the username.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username.set(username);
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password.get();
    }

    /**
     * Sets the password.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password.set(password);
    }

    /**
     * Checks if login is in progress.
     *
     * @return true if login is in progress, false otherwise
     */
    public boolean isLoginInProgress() {
        return loginInProgress.get();
    }

    /**
     * Sets whether login is in progress.
     *
     * @param loginInProgress true if login is in progress, false otherwise
     */
    public void setLoginInProgress(boolean loginInProgress) {
        this.loginInProgress.set(loginInProgress);
    }

    /**
     * Cancels the login process and clears the form.
     * This is especially important for the admin role.
     */
    public void cancel() {
        logger.debug("Login cancelled");

        // Clear the form
        username.set("");
        password.set("");
        errorMessage.set("");

        // Ensure login is not in progress
        loginInProgress.set(false);
    }

    /**
     * Attempts to log in with the given PIN code.
     * 
     * @param pinCode the PIN code to use for authentication
     */
    public void loginWithPin(String pinCode) {
        // Clear any previous error message
        errorMessage.set("");

        logger.debug("PIN login attempt started");

        // Check if PIN code is provided
        if (pinCode == null || pinCode.isBlank()) {
            logger.warn("PIN login failed: PIN code is required");
            errorMessage.set("PIN code is required");
            return;
        }

        // Set login in progress
        loginInProgress.set(true);
        logger.debug("PIN login in progress");

        try {
            // Attempt to log in with PIN
            logger.debug("Calling sessionManager.loginWithPin");
            Optional<UserBusiness> userOpt = sessionManager.loginWithPin(pinCode);

            if (userOpt.isPresent()) {
                // Login successful, navigate to role-specific view
                UserBusiness user = userOpt.get();
                logger.success("PIN login successful");

                try {
                    // Use the RoleBasedNavigationService to navigate to the appropriate view based on user role
                    logger.debug("Navigating to user home view");
                    navigationService.navigateToUserHome();
                    logger.debug("Navigation to user home view completed");
                } catch (Exception e) {
                    logger.error("Failed to navigate to role-specific view", e);
                    errorMessage.set("Navigation error: " + e.getMessage());
                }
            } else {
                // PIN login failed
                logger.warn("PIN login failed: Invalid PIN code");
                errorMessage.set("Invalid PIN code. Please try again.");
            }
        } catch (Exception e) {
            // Handle any exceptions
            logger.error("Exception during PIN login", e);
            errorMessage.set("An error occurred during login. Please try again later.");
        } finally {
            // Clear login in progress
            loginInProgress.set(false);
            logger.debug("PIN login process completed");
        }
    }
}
