package com.belman.domain.security;

import com.belman.domain.user.UserBusiness;

import java.util.Optional;

/**
 * Extended authentication service interface that adds support for NFC authentication.
 */
public interface ExtendedAuthenticationService extends AuthenticationService {
    /**
     * Authenticates a user with the given NFC ID.
     * This is a simulated NFC authentication for production workers.
     *
     * @param nfcId the NFC ID
     * @return an Optional containing the authenticated User if successful, or empty if authentication failed
     */
    Optional<UserBusiness> authenticateWithNfc(String nfcId);
}
