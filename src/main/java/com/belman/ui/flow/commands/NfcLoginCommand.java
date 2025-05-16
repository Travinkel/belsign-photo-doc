package com.belman.ui.flow.commands;

import com.belman.common.di.Inject;
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
        return CompletableFuture.supplyAsync(() -> {
            if (nfcId == null || nfcId.isBlank()) {
                throw new IllegalArgumentException("NFC ID cannot be null or blank");
            }
            
            return authenticationService.authenticateWithNfc(nfcId);
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
        return "Login with NFC ID: " + nfcId;
    }
}