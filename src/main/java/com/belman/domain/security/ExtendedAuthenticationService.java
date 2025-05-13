package com.belman.domain.security;

import com.belman.domain.user.UserBusiness;

import java.util.Optional;

/**
 * Extended authentication service interface that adds support for PIN code and QR code authentication.
 */
public interface ExtendedAuthenticationService extends AuthenticationService {
    /**
     * Authenticates a user with the given PIN code.
     *
     * @param pinCode the PIN code
     * @return an Optional containing the authenticated User if successful, or empty if authentication failed
     */
    Optional<UserBusiness> authenticateWithPin(String pinCode);

    /**
     * Authenticates a user with the given QR code hash.
     *
     * @param qrCodeHash the QR code hash
     * @return an Optional containing the authenticated User if successful, or empty if authentication failed
     */
    Optional<UserBusiness> authenticateWithQrCode(String qrCodeHash);
}