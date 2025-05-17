package com.belman.presentation.flow.commands;

import com.belman.common.di.Inject;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.user.UserBusiness;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Command for authenticating a user with username and password.
 * <p>
 * This command uses the AuthenticationService to authenticate a user
 * and returns the authenticated user if successful.
 */
public class LoginCommand implements Command<Optional<UserBusiness>> {
    
    @Inject
    private AuthenticationService authenticationService;
    
    private final String username;
    private final String password;
    
    /**
     * Creates a new LoginCommand with the specified username and password.
     *
     * @param username the username to authenticate with
     * @param password the password to authenticate with
     */
    public LoginCommand(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    @Override
    public CompletableFuture<Optional<UserBusiness>> execute() {
        return CompletableFuture.supplyAsync(() -> {
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException("Username cannot be null or blank");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("Password cannot be null or blank");
            }
            
            return authenticationService.authenticate(username, password);
        });
    }
    
    @Override
    public CompletableFuture<Void> undo() {
        return CompletableFuture.runAsync(() -> {
            // Logout the user if they were logged in
            if (authenticationService.isLoggedIn()) {
                authenticationService.logout();
            }
        });
    }
    
    @Override
    public boolean canUndo() {
        return true;
    }
    
    @Override
    public String getDescription() {
        return "Login with username: " + username;
    }
}