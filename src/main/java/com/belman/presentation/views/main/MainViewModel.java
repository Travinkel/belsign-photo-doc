package com.belman.presentation.views.main;


import com.belman.application.api.CoreAPI;
import com.belman.presentation.core.BaseViewModel;
import com.belman.infrastructure.EmojiLogger;
import com.belman.presentation.navigation.Router;
import com.belman.domain.aggregates.User;
import com.belman.infrastructure.service.SessionManager;
import com.belman.presentation.views.login.LoginView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Optional;

/**
 * View model for the main view.
 */
public class MainViewModel extends BaseViewModel<MainViewModel> {
    private final EmojiLogger logger = EmojiLogger.getLogger(MainViewModel.class);
    private final StringProperty welcomeMessage = new SimpleStringProperty("Welcome to BelSign!");
    private final StringProperty username = new SimpleStringProperty("");
    private final SessionManager sessionManager;

    /**
     * Creates a new MainViewModel.
     */
    public MainViewModel() {
        // Get the SessionManager instance
        sessionManager = SessionManager.getInstance();
    }

    @Override
    public void onShow() {
        logger.debug("MainViewModel.onShow() called");
        try {
            // Update the app bar title using CoreAPI
            logger.debug("Setting app bar title to 'Main View'");
            CoreAPI.setState("appBarTitle", "Main View");

            logger.debug("Calling updateWelcomeMessage()");
            updateWelcomeMessage();
            logger.debug("updateWelcomeMessage() completed");
        } catch (Exception e) {
            logger.error("Exception in MainViewModel.onShow()", e);
        }
    }

    /**
     * Updates the welcome message based on the current user.
     */
    private void updateWelcomeMessage() {
        logger.debug("Updating welcome message");
        try {
            logger.debug("Getting current user from sessionManager");
            Optional<User> currentUser = sessionManager.getCurrentUser();

            if (currentUser.isPresent()) {
                String name = currentUser.get().getUsername().value();
                logger.debug("Current user found: {}", name);
                welcomeMessage.set("Welcome, " + name + "!");
                username.set(name);
                logger.debug("Welcome message updated for user: {}", name);
            } else {
                logger.warn("No current user found, redirecting to login view");
                welcomeMessage.set("Welcome to BelSign!");
                username.set("");

                // If no user is logged in, redirect to login view
                try {
                    logger.debug("Navigating to LoginView");
                    Router.navigateTo(LoginView.class);
                    logger.debug("Navigation to LoginView completed");
                } catch (Exception e) {
                    logger.error("Failed to navigate to LoginView", e);
                }
            }
        } catch (Exception e) {
            logger.error("Exception while updating welcome message", e);
            // Set a default welcome message in case of error
            welcomeMessage.set("Welcome to BelSign!");
            username.set("");
        }
    }

    /**
     * Logs out the current user and navigates to the login view.
     */
    public void logout() {
        logger.debug("Logging out user");
        try {
            logger.debug("Calling sessionManager.logout()");
            sessionManager.logout();
            logger.debug("User logged out successfully");

            logger.debug("Navigating to LoginView");
            try {
                Router.navigateTo(LoginView.class);
                logger.debug("Navigation to LoginView completed");
            } catch (Exception e) {
                logger.error("Failed to navigate to LoginView", e);
            }
        } catch (Exception e) {
            logger.error("Exception during logout", e);
        }
    }

    /**
     * Gets the welcome message property.
     * 
     * @return the welcome message property
     */
    public StringProperty welcomeMessageProperty() {
        return welcomeMessage;
    }

    /**
     * Gets the welcome message.
     * 
     * @return the welcome message
     */
    public String getWelcomeMessage() {
        return welcomeMessage.get();
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
     * Gets the username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username.get();
    }
}
