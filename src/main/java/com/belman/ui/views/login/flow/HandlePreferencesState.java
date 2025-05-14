package com.belman.ui.views.login.flow;

import javax.security.auth.login.LoginException;

/**
 * State for handling user preferences after a successful login.
 */
public class HandlePreferencesState implements LoginState {
    @Override
    public void handle(LoginContext context) throws LoginException {
        context.logDebug("Handling user preferences");

        // In a real implementation, this would save user preferences
        // For demonstration purposes, we'll just log a message

        context.logSuccess("Login completed successfully");

        // Clear login in progress
        context.setLoginInProgress(false);
    }
}