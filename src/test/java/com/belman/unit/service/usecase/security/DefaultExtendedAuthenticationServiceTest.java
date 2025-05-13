package com.belman.unit.service.usecase.security;

import com.belman.domain.common.EmailAddress;
import com.belman.domain.security.ExtendedAuthenticationService;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.security.PasswordHasher;
import com.belman.domain.user.*;
import com.belman.repository.persistence.memory.InMemoryUserRepository;
import com.belman.service.usecase.security.BCryptPasswordHasher;
import com.belman.service.usecase.security.DefaultExtendedAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the DefaultExtendedAuthenticationService class.
 * These tests verify that PIN code and QR code authentication work correctly.
 */
public class DefaultExtendedAuthenticationServiceTest {

    private ExtendedAuthenticationService authService;
    private InMemoryUserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Create an in-memory user repository
        userRepository = new InMemoryUserRepository();

        // Create the authentication service
        authService = new DefaultExtendedAuthenticationService(userRepository);

        // Set up test users
        setupTestUsers();
    }

    /**
     * Sets up test users with PIN codes and QR code hashes.
     */
    private void setupTestUsers() {
        // Create a hashed password directly
        HashedPassword hashedPassword = new HashedPassword("$2a$10$h.dl5J86rGH7I8bD9bZeZe");

        // Create a user with PIN code
        UserBusiness pinUser = UserBusiness.createNewUser(
                new Username("pin_user"),
                hashedPassword,
                new EmailAddress("pin_user@example.com")
        );
        pinUser.addRole(UserRole.PRODUCTION);
        pinUser.setApprovalState(ApprovalState.createApproved());
        userRepository.save(pinUser);
        userRepository.addPinCodeMapping("1234", pinUser.getId());

        // Create a user with QR code
        UserBusiness qrUser = UserBusiness.createNewUser(
                new Username("qr_user"),
                hashedPassword,
                new EmailAddress("qr_user@example.com")
        );
        qrUser.addRole(UserRole.QA);
        qrUser.setApprovalState(ApprovalState.createApproved());
        userRepository.save(qrUser);
        userRepository.addQrCodeHashMapping("test-qr-code-hash", qrUser.getId());

        System.out.println("[DEBUG_LOG] Test users created successfully");
    }

    @Test
    void testAuthenticateWithPin_ValidPin_ReturnsUser() {
        // Arrange
        String validPin = "1234";

        // Act
        Optional<UserBusiness> result = authService.authenticateWithPin(validPin);

        // Assert
        assertTrue(result.isPresent(), "Should return a user for valid PIN");
        assertEquals("pin_user", result.get().getUsername().value(), "Should return the correct user");
        System.out.println("[DEBUG_LOG] Successfully authenticated with PIN: " + validPin);
    }

    @Test
    void testAuthenticateWithPin_InvalidPin_ReturnsEmpty() {
        // Arrange
        String invalidPin = "9999";

        // Act
        Optional<UserBusiness> result = authService.authenticateWithPin(invalidPin);

        // Assert
        assertTrue(result.isEmpty(), "Should return empty for invalid PIN");
        System.out.println("[DEBUG_LOG] Correctly rejected invalid PIN: " + invalidPin);
    }

    @Test
    void testAuthenticateWithQrCode_ValidQrCode_ReturnsUser() {
        // Arrange
        String validQrCode = "test-qr-code-hash";

        // Act
        Optional<UserBusiness> result = authService.authenticateWithQrCode(validQrCode);

        // Assert
        assertTrue(result.isPresent(), "Should return a user for valid QR code");
        assertEquals("qr_user", result.get().getUsername().value(), "Should return the correct user");
        System.out.println("[DEBUG_LOG] Successfully authenticated with QR code: " + validQrCode);
    }

    @Test
    void testAuthenticateWithQrCode_InvalidQrCode_ReturnsEmpty() {
        // Arrange
        String invalidQrCode = "invalid-qr-code-hash";

        // Act
        Optional<UserBusiness> result = authService.authenticateWithQrCode(invalidQrCode);

        // Assert
        assertTrue(result.isEmpty(), "Should return empty for invalid QR code");
        System.out.println("[DEBUG_LOG] Correctly rejected invalid QR code: " + invalidQrCode);
    }
}
