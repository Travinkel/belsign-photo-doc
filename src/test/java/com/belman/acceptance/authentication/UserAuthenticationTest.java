package com.belman.acceptance.authentication;

import com.belman.acceptance.BaseAcceptanceTest;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.security.PasswordHasher;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserRole;
import com.belman.domain.user.Username;
import com.belman.repository.persistence.memory.InMemoryUserRepository;
import com.belman.service.usecase.security.BCryptPasswordHasher;
import com.belman.service.usecase.security.DefaultAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Acceptance tests for user authentication.
 * Verifies that users can log in with valid credentials and are denied access with invalid credentials.
 */
public class UserAuthenticationTest extends BaseAcceptanceTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_EMAIL = "test@example.com";
    private AuthenticationService authService;
    private UserBusiness testUser;
    private PasswordHasher passwordHasher;

    @BeforeEach
    void setup() {
        logDebug("Setting up authentication test");

        // Create a repository and add a test user
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        authService = new DefaultAuthenticationService(userRepository);
        passwordHasher = new BCryptPasswordHasher();

        // Create a test user for authentication tests
        UserId userId = UserId.newId();
        Username username = new Username(TEST_USERNAME);
        HashedPassword password = HashedPassword.fromPlainText(TEST_PASSWORD, passwordHasher);
        EmailAddress email = new EmailAddress(TEST_EMAIL);

        // Create a new user using the builder pattern
        testUser = UserBusiness.createNewUser(username, password, email);
        testUser.addRole(UserRole.PRODUCTION);

        // Save the test user to the repository
        userRepository.save(testUser);
        logDebug("Test user created: " + testUser.getUsername().value());
    }

    @Test
    @DisplayName("User can log in with valid credentials")
    void testLoginWithValidCredentials() {
        logDebug("Testing login with valid credentials");

        // Attempt to log in with valid credentials
        Optional<UserBusiness> authenticatedUserOpt = authService.authenticate(TEST_USERNAME, TEST_PASSWORD);

        // Verify that authentication succeeded
        assertTrue(authenticatedUserOpt.isPresent(), "Authentication should succeed with valid credentials");

        UserBusiness authenticatedUser = authenticatedUserOpt.get();
        assertEquals(TEST_USERNAME, authenticatedUser.getUsername().value(),
                "Authenticated user should have the expected username");
        assertEquals(TEST_EMAIL, authenticatedUser.getEmail().value(),
                "Authenticated user should have the expected email");
        assertTrue(authenticatedUser.getRoles().contains(UserRole.PRODUCTION),
                "Authenticated user should have the PRODUCTION role");

        logDebug("Login with valid credentials successful");
    }

    @Test
    @DisplayName("User cannot log in with invalid password")
    void testLoginWithInvalidPassword() {
        logDebug("Testing login with invalid password");

        // Attempt to log in with invalid password
        Optional<UserBusiness> authenticatedUserOpt = authService.authenticate(TEST_USERNAME, "wrongpassword");

        // Verify that authentication failed
        assertTrue(authenticatedUserOpt.isEmpty(), "Authentication should fail with invalid password");

        logDebug("Login with invalid password correctly failed");
    }

    @Test
    @DisplayName("User cannot log in with non-existent username")
    void testLoginWithNonExistentUsername() {
        logDebug("Testing login with non-existent username");

        // Attempt to log in with non-existent username
        Optional<UserBusiness> authenticatedUserOpt = authService.authenticate("nonexistentuser", TEST_PASSWORD);

        // Verify that authentication failed
        assertTrue(authenticatedUserOpt.isEmpty(), "Authentication should fail with non-existent username");

        logDebug("Login with non-existent username correctly failed");
    }
}
