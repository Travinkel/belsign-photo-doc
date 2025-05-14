package com.belman.ui.usecases.authentication.login.flow;

import javax.security.auth.login.LoginException;

/**
 * State for handling PIN code login.
 */
public class PinLoginState implements LoginState {
    @Override
    public void handle(LoginContext context) throws LoginException {
        context.logDebug("PIN login started");

        // In a real implementation, this would validate the PIN code against a database or service
        // For demonstration purposes, we'll just simulate a successful login

        // Set login in progress
        context.setLoginInProgress(true);

        // Simulate PIN code validation
        // In a real implementation, this would call a service to validate the PIN code

        // For now, we'll just transition to the next state
        context.setState(new AttemptLoginState());
    }
}