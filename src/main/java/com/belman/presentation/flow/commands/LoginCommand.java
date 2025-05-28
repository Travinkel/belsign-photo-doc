package com.belman.presentation.flow.commands;

import com.belman.common.di.Inject;
import com.belman.common.logging.EmojiLogger;
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

    private static final EmojiLogger logger = EmojiLogger.getLogger(LoginCommand.class);

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
        logger.info("Attempting login for user: {}", username);
        return CompletableFuture.supplyAsync(() -> {
            if (username == null || username.isBlank()) {
                logger.warn("Login attempt with null or blank username");
                throw new IllegalArgumentException("Username cannot be null or blank");
            }
            if (password == null || password.isBlank()) {
                logger.warn("Login attempt with null or blank password for user: {}", username);
                throw new IllegalArgumentException("Password cannot be null or blank");
            }

            logger.debug("Authenticating user: {}", username);
            Optional<UserBusiness> result = authenticationService.authenticate(username, password);

            if (result.isPresent()) {
                logger.info("Login successful for user: {}", username);
            } else {
                logger.warn("Login failed for user: {}", username);
            }

            return result;
        });
    }

    @Override
    public CompletableFuture<Void> undo() {
        logger.info("Attempting to undo login (logout) for user: {}", username);
        return CompletableFuture.runAsync(() -> {
            // Logout the user if they were logged in
            if (authenticationService.isLoggedIn()) {
                logger.debug("User is logged in, performing logout");
                authenticationService.logout();
                logger.info("Logout successful");
            } else {
                logger.debug("No user is currently logged in, nothing to undo");
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
