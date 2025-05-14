package com.belman.ui.usecases.authentication.login.flow;

import com.belman.domain.user.UserBusiness;
import com.belman.ui.usecases.authentication.login.LoginViewModel;

import javax.security.auth.login.LoginException;
import java.util.Optional;

/**
 * Default implementation of the LoginContext interface.
 */
public class DefaultLoginContext implements LoginContext {
    private final LoginViewModel viewModel;
    private LoginState currentState;

    /**
     * Creates a new DefaultLoginContext with the specified view model.
     *
     * @param viewModel the login view model
     */
    public DefaultLoginContext(LoginViewModel viewModel) {
        this.viewModel = viewModel;
        this.currentState = new StartLoginState();
    }

    /**
     * Handles the current state.
     *
     * @throws LoginException if an error occurs during login
     */
    public void handle() throws LoginException {
        if (currentState != null) {
            currentState.handle(this);
        }
    }

    @Override
    public Optional<UserBusiness> login() {
        // Call the view model's login method
        viewModel.login();

        // Since the view model's login method doesn't return a value,
        // we'll return an empty Optional
        return Optional.empty();
    }

    @Override
    public void setUser(UserBusiness user) {
        // No direct way to set the user in the view model
        // This would be handled by the login method
    }

    @Override
    public void setLoginInProgress(boolean inProgress) {
        viewModel.setLoginInProgress(inProgress);
    }

    @Override
    public void logDebug(String message) {
        // Log debug message
        System.out.println("[DEBUG] " + message);
    }

    @Override
    public void logSuccess(String message) {
        // Log success message
        System.out.println("[SUCCESS] " + message);
    }

    @Override
    public void logFailure(String message) {
        // Log failure message
        System.out.println("[FAILURE] " + message);
    }

    @Override
    public void setState(LoginState state) {
        this.currentState = state;
    }
}