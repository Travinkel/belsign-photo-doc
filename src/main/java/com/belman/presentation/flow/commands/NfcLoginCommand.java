package com.belman.presentation.flow.commands;

import com.belman.common.di.Inject;
import com.belman.common.logging.EmojiLogger;
import com.belman.domain.security.ExtendedAuthenticationService;
import com.belman.domain.user.UserBusiness;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Command for authenticating a user with an NFC ID.
 * <p>
 * This command uses the ExtendedAuthenticationService to authenticate a user
 * with an NFC ID and returns the authenticated user if successful.
 */
public class NfcLoginCommand implements Command<Optional<UserBusiness>> {

    private static final EmojiLogger logger = EmojiLogger.getLogger(NfcLoginCommand.class);

    @Inject
    private ExtendedAuthenticationService authenticationService;

    private final String nfcId;

    /**
     * Creates a new NfcLoginCommand with the specified NFC ID.
     *
     * @param nfcId the NFC ID to authenticate with
     */
    public NfcLoginCommand(String nfcId) {
        this.nfcId = nfcId;
    }

    @Override
    public CompletableFuture<Optional<UserBusiness>> execute() {
        logger.info("Attempting NFC login with ID: {}", nfcId);
        return CompletableFuture.supplyAsync(() -> {
            if (nfcId == null || nfcId.isBlank()) {
                logger.warn("NFC login attempt with null or blank ID");
                throw new IllegalArgumentException("NFC ID cannot be null or blank");
            }

            logger.debug("Authenticating with NFC ID");
            Optional<UserBusiness> result = authenticationService.authenticateWithNfc(nfcId);

            if (result.isPresent()) {
                logger.info("NFC login successful for user: {}", result.get().getUsername().value());
            } else {
                logger.warn("NFC login failed - no user found for NFC ID");
            }

            return result;
        });
    }

    @Override
    public CompletableFuture<Void> undo() {
        logger.info("Attempting to undo NFC login (logout)");
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
        return "Login with NFC ID: " + nfcId;
    }
}
