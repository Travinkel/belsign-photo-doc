package com.belman.unit.infrastructure.service;

import com.belman.business.richbe.user.UserAggregate;
import com.belman.business.richbe.user.UserRepository;
import com.belman.business.richbe.security.AuthenticationService;
import com.belman.business.richbe.security.PasswordHasher;
import com.belman.business.richbe.common.EmailAddress;
import com.belman.business.richbe.security.HashedPassword;
import com.belman.business.richbe.common.PersonName;
import com.belman.business.richbe.user.UserId;
import com.belman.business.richbe.user.Username;
import com.belman.data.persistence.InMemoryUserRepository;
import com.belman.data.security.BCryptPasswordHasher;
import com.belman.data.security.DefaultAuthenticationService;
import com.belman.business.core.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SessionManager.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SessionManagerTest {

    private UserRepository userRepository;
    private AuthenticationService authenticationService;
    private SessionManager sessionManager;
    private UserAggregate testUser;
    private PasswordHasher passwordHasher;

    @BeforeEach
    void setUp() {
        // Create a fresh repository for each test
        userRepository = new InMemoryUserRepository();

        // Create the authentication service with the repository
        authenticationService = new DefaultAuthenticationService(userRepository);

        // Create the session manager with the authentication service
        sessionManager = SessionManager.getInstance(authenticationService);

        // Create a password hasher
        passwordHasher = new BCryptPasswordHasher();

        // Create a test user
        testUser = new UserAggregate.Builder()
            .id(UserId.newId())
            .username(new Username("testuser"))
            .password(HashedPassword.fromPlainText("testpassword", passwordHasher))
            .name(new PersonName("Test", "User"))
            .email(new EmailAddress("test@example.com"))
            .build();

        // Save the test user to the repository
        userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        // Log out to reset the session state
        sessionManager.logout();
    }

    @Test
    void login_withValidCredentials_shouldReturnUser() {
        // Act
        Optional<UserAggregate> result = sessionManager.login("testuser", "testpassword");

        // Assert
        assertTrue(result.isPresent(), "Login should succeed with valid credentials");
        assertEquals(testUser.getUsername().value(), result.get().getUsername().value(), "Username should match");
    }

    @Test
    void login_withInvalidCredentials_shouldReturnEmpty() {
        // Act
        Optional<UserAggregate> result = sessionManager.login("testuser", "wrongpassword");

        // Assert
        assertFalse(result.isPresent(), "Login should fail with invalid credentials");
    }

    @Test
    void getCurrentUser_afterSuccessfulLogin_shouldReturnUser() {
        // Arrange
        sessionManager.login("testuser", "testpassword");

        // Act
        Optional<UserAggregate> result = sessionManager.getCurrentUser();

        // Assert
        assertTrue(result.isPresent(), "Current user should be present after successful login");
        assertEquals(testUser.getUsername().value(), result.get().getUsername().value(), "Username should match");
    }

    @Test
    void getCurrentUser_beforeLogin_shouldReturnEmpty() {
        // Act
        Optional<UserAggregate> result = sessionManager.getCurrentUser();

        // Assert
        assertFalse(result.isPresent(), "Current user should not be present before login");
    }

    @Test
    void isLoggedIn_afterSuccessfulLogin_shouldReturnTrue() {
        // Arrange
        sessionManager.login("testuser", "testpassword");

        // Act
        boolean result = sessionManager.isLoggedIn();

        // Assert
        assertTrue(result, "User should be logged in after successful login");
    }

    @Test
    void isLoggedIn_beforeLogin_shouldReturnFalse() {
        // Act
        boolean result = sessionManager.isLoggedIn();

        // Assert
        assertFalse(result, "User should not be logged in before login");
    }

    @Test
    void logout_afterSuccessfulLogin_shouldClearCurrentUser() {
        // Arrange
        sessionManager.login("testuser", "testpassword");

        // Act
        sessionManager.logout();

        // Assert
        assertFalse(sessionManager.isLoggedIn(), "User should not be logged in after logout");
        assertFalse(sessionManager.getCurrentUser().isPresent(), "Current user should not be present after logout");
    }

    @Test
    void getInstance_shouldReturnSameInstance() {
        // Act
        SessionManager instance1 = SessionManager.getInstance();
        SessionManager instance2 = SessionManager.getInstance();

        // Assert
        assertSame(instance1, instance2, "getInstance should return the same instance");
    }

    @Test
    void getInstance_withAuthenticationService_shouldInitializeIfNeeded() {
        // Arrange
        AuthenticationService newAuthService = new DefaultAuthenticationService(userRepository);

        // Act
        SessionManager instance = SessionManager.getInstance(newAuthService);

        // Assert
        assertNotNull(instance, "getInstance should return a non-null instance");
        // We can't directly test that it was initialized with the new auth service,
        // but we can test that it's functional
        Optional<UserAggregate> result = instance.login("testuser", "testpassword");
        assertTrue(result.isPresent(), "Login should succeed with valid credentials");
    }
}
