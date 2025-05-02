package com.belman.presentation.views.main;


import com.belman.backbone.core.base.BaseViewModel;
import com.belman.backbone.core.navigation.Router;
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
        // Initialize any resources or data needed for the main view
        updateWelcomeMessage();
    }

    /**
     * Updates the welcome message based on the current user.
     */
    private void updateWelcomeMessage() {
        Optional<User> currentUser = sessionManager.getCurrentUser();
        if (currentUser.isPresent()) {
            String name = currentUser.get().getUsername().value();
            welcomeMessage.set("Welcome, " + name + "!");
            username.set(name);
        } else {
            welcomeMessage.set("Welcome to BelSign!");
            username.set("");

            // If no user is logged in, redirect to login view
            Router.navigateTo(LoginView.class);
        }
    }

    /**
     * Logs out the current user and navigates to the login view.
     */
    public void logout() {
        sessionManager.logout();
        Router.navigateTo(LoginView.class);
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
