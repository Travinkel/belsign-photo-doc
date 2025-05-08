package com.belman.unit.domain.services;

import com.belman.business.richbe.user.UserAggregate;
import com.belman.business.richbe.user.UserStatus;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AuthenticationService.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationServiceTest {

    private UserRepository userRepository;
    private AuthenticationService authenticationService;
    private UserAggregate testUser;
    private PasswordHasher passwordHasher;

    @BeforeEach
    void setUp() {
        // Create a fresh repository for each test
        userRepository = new InMemoryUserRepository();

        // Create the authentication service with the repository
        authenticationService = new DefaultAuthenticationService(userRepository);

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

    @Test
    void authenticate_withValidCredentials_shouldReturnUser() {
        // Act
        Optional<UserAggregate> result = authenticationService.authenticate("testuser", "testpassword");

        // Assert
        assertTrue(result.isPresent(), "Authentication should succeed with valid credentials");
        assertEquals(testUser.getUsername().value(), result.get().getUsername().value(), "Username should match");
    }

    @Test
    void authenticate_withInvalidUsername_shouldReturnEmpty() {
        // Act
        Optional<UserAggregate> result = authenticationService.authenticate("nonexistentuser", "testpassword");

        // Assert
        assertFalse(result.isPresent(), "Authentication should fail with invalid username");
    }

    @Test
    void authenticate_withInvalidPassword_shouldReturnEmpty() {
        // Act
        Optional<UserAggregate> result = authenticationService.authenticate("testuser", "wrongpassword");

        // Assert
        assertFalse(result.isPresent(), "Authentication should fail with invalid password");
    }

    @Test
    void authenticate_withNullUsername_shouldReturnEmpty() {
        // Act
        Optional<UserAggregate> result = authenticationService.authenticate(null, "testpassword");

        // Assert
        assertFalse(result.isPresent(), "Authentication should fail with null username");
    }

    @Test
    void authenticate_withNullPassword_shouldReturnEmpty() {
        // Act
        Optional<UserAggregate> result = authenticationService.authenticate("testuser", null);

        // Assert
        assertFalse(result.isPresent(), "Authentication should fail with null password");
    }

    @Test
    void getCurrentUser_afterSuccessfulAuthentication_shouldReturnUser() {
        // Arrange
        authenticationService.authenticate("testuser", "testpassword");

        // Act
        Optional<UserAggregate> result = authenticationService.getCurrentUser();

        // Assert
        assertTrue(result.isPresent(), "Current user should be present after successful authentication");
        assertEquals(testUser.getUsername().value(), result.get().getUsername().value(), "Username should match");
    }

    @Test
    void getCurrentUser_beforeAuthentication_shouldReturnEmpty() {
        // Act
        Optional<UserAggregate> result = authenticationService.getCurrentUser();

        // Assert
        assertFalse(result.isPresent(), "Current user should not be present before authentication");
    }

    @Test
    void isLoggedIn_afterSuccessfulAuthentication_shouldReturnTrue() {
        // Arrange
        authenticationService.authenticate("testuser", "testpassword");

        // Act
        boolean result = authenticationService.isLoggedIn();

        // Assert
        assertTrue(result, "User should be logged in after successful authentication");
    }

    @Test
    void isLoggedIn_beforeAuthentication_shouldReturnFalse() {
        // Act
        boolean result = authenticationService.isLoggedIn();

        // Assert
        assertFalse(result, "User should not be logged in before authentication");
    }

    @Test
    void logout_afterSuccessfulAuthentication_shouldClearCurrentUser() {
        // Arrange
        authenticationService.authenticate("testuser", "testpassword");

        // Act
        authenticationService.logout();

        // Assert
        assertFalse(authenticationService.isLoggedIn(), "User should not be logged in after logout");
        assertFalse(authenticationService.getCurrentUser().isPresent(), "Current user should not be present after logout");
    }

    @Test
    void authenticate_withMultipleFailedAttempts_shouldLockUserAccount() {
        // Create a new user for this test
        UserAggregate lockableUser = new UserAggregate.Builder()
            .id(UserId.newId())
            .username(new Username("lockableuser"))
            .password(HashedPassword.fromPlainText("password123", passwordHasher))
            .name(new PersonName("Lockable", "User"))
            .email(new EmailAddress("lockable@example.com"))
            .build();
        userRepository.save(lockableUser);

        // Attempt to authenticate with wrong password multiple times
        for (int i = 0; i < 10; i++) { // More than MAX_FAILED_ATTEMPTS (5)
            Optional<UserAggregate> result = authenticationService.authenticate("lockableuser", "wrongpassword");
            assertFalse(result.isPresent(), "Authentication should fail with wrong password");
        }

        // Verify that the user is locked in the database
        UserAggregate user = userRepository.findByUsername(new Username("lockableuser")).orElseThrow();
        // Note: UserAggregate might not have isLocked method, using getStatus() == UserStatus.LOCKED instead
        assertEquals(UserStatus.LOCKED, user.getStatus(), "User should be locked after too many failed attempts");

        // Now try with correct password - should still fail because the account is locked
        Optional<UserAggregate> result = authenticationService.authenticate("lockableuser", "password123");
        assertFalse(result.isPresent(), "Authentication should fail when account is locked, even with correct password");
    }
}
