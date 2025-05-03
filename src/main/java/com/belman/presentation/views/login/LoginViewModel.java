package com.belman.presentation.views.login;

import com.belman.backbone.core.api.CoreAPI;
import com.belman.backbone.core.base.BaseViewModel;
import com.belman.backbone.core.logging.EmojiLogger;
import com.belman.backbone.core.navigation.Router;
import com.belman.domain.aggregates.User;
import com.belman.domain.services.AuthenticationService;
import com.belman.infrastructure.service.DefaultAuthenticationService;
import com.belman.infrastructure.service.SessionManager;
import com.belman.presentation.views.main.MainView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Optional;

/**
 * ViewModel for the login view.
 */
public class LoginViewModel extends BaseViewModel<LoginViewModel> {
    private final EmojiLogger logger = EmojiLogger.getLogger(LoginViewModel.class);
    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty loginInProgress = new SimpleBooleanProperty(false);

    private final SessionManager sessionManager;

    /**
     * Creates a new LoginViewModel.
     */
    public LoginViewModel() {
        // Get the SessionManager instance
        sessionManager = SessionManager.getInstance();
    }

    @Override
    public void onShow() {
        // No need to update the app bar title as we want to hide the app bar
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
            Optional<User> userOpt = sessionManager.login(username.get(), password.get());

            if (userOpt.isPresent()) {
                // Login successful, navigate to main view
                logger.success("Login successful for user: " + username.get());
                logger.debug("Navigating to MainView");
                try {
                    Router.navigateTo(MainView.class);
                    logger.debug("Navigation to MainView completed");
                } catch (Exception e) {
                    logger.error("Failed to navigate to MainView", e);
                    errorMessage.set("Navigation error: " + e.getMessage());
                }
            } else {
                // Login failed
                logger.warn("Login failed: Invalid credentials for user: {}", username.get());
                errorMessage.set("Invalid username or password");
            }
        } catch (Exception e) {
            // Handle any exceptions
            logger.error("Exception during login for user: {}", username.get(), e);
            errorMessage.set("An error occurred: " + e.getMessage());
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
     * Gets the error message property.
     * 
     * @return the error message property
     */
    public StringProperty errorMessageProperty() {
        return errorMessage;
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
}
