package com.belman.acceptance.authentication;

import com.belman.acceptance.BaseAcceptanceTest;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.security.PasswordHasher;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.ApprovalState;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserRole;
import com.belman.domain.user.Username;
import com.belman.dataaccess.persistence.memory.InMemoryUserRepository;
import com.belman.application.usecase.security.BCryptPasswordHasher;
import com.belman.application.usecase.security.DefaultAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Acceptance test for production_worker authentication.
 * Verifies that the production_worker user can log in with the correct password.
 */
public class ProductionWorkerAuthenticationTest extends BaseAcceptanceTest {

    private static final String TEST_USERNAME = "production_worker";
    private static final String TEST_PASSWORD = "pass1234";
    private static final String TEST_EMAIL = "production_worker@example.com";
    private AuthenticationService authService;
    private UserBusiness testUser;
    private PasswordHasher passwordHasher;

    @BeforeEach
    void setup() {
        logDebug("Setting up production_worker authentication test");

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
        testUser.setApprovalState(ApprovalState.createApproved());

        // Save the test user to the repository
        userRepository.save(testUser);
        logDebug("Test user created: " + testUser.getUsername().value());
    }

    @Test
    @DisplayName("Production worker can log in with valid credentials")
    void testProductionWorkerLogin() {
        logDebug("Testing login for production_worker with password pass1234");

        // Attempt to authenticate with production_worker credentials
        Optional<UserBusiness> userOpt = authService.authenticate(TEST_USERNAME, TEST_PASSWORD);

        // Verify that authentication succeeded
        assertTrue(userOpt.isPresent(), "Authentication should succeed for production_worker with password pass1234");

        UserBusiness authenticatedUser = userOpt.get();
        assertEquals(TEST_USERNAME, authenticatedUser.getUsername().value(),
                "Authenticated user should have the expected username");
        assertEquals(TEST_EMAIL, authenticatedUser.getEmail().value(),
                "Authenticated user should have the expected email");
        assertTrue(authenticatedUser.getRoles().contains(UserRole.PRODUCTION),
                "Authenticated user should have the PRODUCTION role");

        logDebug("Successfully authenticated production_worker user: " + authenticatedUser.getUsername().value());
        logDebug("User ID: " + authenticatedUser.getId().id());
        logDebug("User roles: " + authenticatedUser.getRoles());
    }
}
