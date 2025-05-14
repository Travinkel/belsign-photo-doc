package com.belman.ui.views.login.flow;

import javax.security.auth.login.LoginException;

/**
 * State for handling login failures.
 */
public class HandleLoginFailureState implements LoginState {
    @Override
    public void handle(LoginContext context) throws LoginException {
        context.logDebug("Handling login failure");

        // In a real implementation, this might increment a failed login counter
        // or implement a lockout mechanism

        context.logFailure("Login failed");

        // Clear login in progress
        context.setLoginInProgress(false);
    }
}