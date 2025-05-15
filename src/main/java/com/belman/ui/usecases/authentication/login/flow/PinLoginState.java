package com.belman.ui.usecases.authentication.login.flow;

import com.belman.domain.user.UserBusiness;

import javax.security.auth.login.LoginException;
import java.util.Optional;

/**
 * State for handling PIN code login.
 */
public class PinLoginState implements LoginState {
    @Override
    public void handle(LoginContext context) throws LoginException {
        context.logDebug("PIN login started");

        // Set login in progress
        context.setLoginInProgress(true);

        // Get the PIN code from the view model
        // In a real implementation, this would be passed to the state
        // For now, we'll use a hardcoded PIN code
        String pinCode = "1234";

        // Call the loginWithPin method on the context
        Optional<UserBusiness> userOpt = context.loginWithPin(pinCode);

        if (userOpt.isPresent()) {
            // Login successful
            context.setUser(userOpt.get());
            context.logSuccess("PIN login successful");
            context.setState(new HandlePreferencesState());
        } else {
            // Login failed
            context.logFailure("PIN login failed");
            context.setState(new HandleLoginFailureState());
        }
    }
}
