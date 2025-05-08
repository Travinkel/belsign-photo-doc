package com.belman.acceptance.authentication;

import com.belman.acceptance.BaseAcceptanceTest;
import com.belman.business.richbe.user.UserAggregate;
import com.belman.business.richbe.security.AuthenticationService;
import com.belman.business.richbe.security.PasswordHasher;
import com.belman.business.richbe.common.EmailAddress;
import com.belman.business.richbe.security.HashedPassword;
import com.belman.business.richbe.user.UserId;
import com.belman.business.richbe.user.Username;
import com.belman.business.richbe.user.UserRole;
import com.belman.data.persistence.InMemoryUserRepository;
import com.belman.data.security.BCryptPasswordHasher;
import com.belman.data.security.DefaultAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Acceptance tests for user authentication.
 * Verifies that users can log in with valid credentials and are denied access with invalid credentials.
 */
public class UserAuthenticationTest extends BaseAcceptanceTest {

    private AuthenticationService authService;
    private UserAggregate testUser;
    private PasswordHasher passwordHasher;
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_EMAIL = "test@example.com";

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
        testUser = UserAggregate.createNewUser(username, password, email);
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
        Optional<UserAggregate> authenticatedUserOpt = authService.authenticate(TEST_USERNAME, TEST_PASSWORD);

        // Verify that authentication succeeded
        assertTrue(authenticatedUserOpt.isPresent(), "Authentication should succeed with valid credentials");

        UserAggregate authenticatedUser = authenticatedUserOpt.get();
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
        Optional<UserAggregate> authenticatedUserOpt = authService.authenticate(TEST_USERNAME, "wrongpassword");

        // Verify that authentication failed
        assertTrue(authenticatedUserOpt.isEmpty(), "Authentication should fail with invalid password");

        logDebug("Login with invalid password correctly failed");
    }

    @Test
    @DisplayName("User cannot log in with non-existent username")
    void testLoginWithNonExistentUsername() {
        logDebug("Testing login with non-existent username");

        // Attempt to log in with non-existent username
        Optional<UserAggregate> authenticatedUserOpt = authService.authenticate("nonexistentuser", TEST_PASSWORD);

        // Verify that authentication failed
        assertTrue(authenticatedUserOpt.isEmpty(), "Authentication should fail with non-existent username");

        logDebug("Login with non-existent username correctly failed");
    }
}
