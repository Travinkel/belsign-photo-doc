package com.belman.unit.domain.services;

import com.belman.domain.aggregates.User;
import com.belman.domain.repositories.UserRepository;
import com.belman.domain.services.AuthenticationService;
import com.belman.domain.valueobjects.EmailAddress;
import com.belman.domain.valueobjects.HashedPassword;
import com.belman.domain.valueobjects.PersonName;
import com.belman.domain.valueobjects.UserId;
import com.belman.domain.valueobjects.Username;
import com.belman.infrastructure.persistence.InMemoryUserRepository;
import com.belman.infrastructure.service.DefaultAuthenticationService;
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
    private User testUser;

    @BeforeEach
    void setUp() {
        // Create a fresh repository for each test
        userRepository = new InMemoryUserRepository();
        
        // Create the authentication service with the repository
        authenticationService = new DefaultAuthenticationService(userRepository);
        
        // Create a test user
        testUser = new User(
            UserId.newId(),
            new Username("testuser"),
            HashedPassword.fromPlainText("testpassword"),
            new PersonName("Test", "User"),
            new EmailAddress("test@example.com")
        );
        
        // Save the test user to the repository
        userRepository.save(testUser);
    }

    @Test
    void authenticate_withValidCredentials_shouldReturnUser() {
        // Act
        Optional<User> result = authenticationService.authenticate("testuser", "testpassword");
        
        // Assert
        assertTrue(result.isPresent(), "Authentication should succeed with valid credentials");
        assertEquals(testUser.getUsername().value(), result.get().getUsername().value(), "Username should match");
    }

    @Test
    void authenticate_withInvalidUsername_shouldReturnEmpty() {
        // Act
        Optional<User> result = authenticationService.authenticate("nonexistentuser", "testpassword");
        
        // Assert
        assertFalse(result.isPresent(), "Authentication should fail with invalid username");
    }

    @Test
    void authenticate_withInvalidPassword_shouldReturnEmpty() {
        // Act
        Optional<User> result = authenticationService.authenticate("testuser", "wrongpassword");
        
        // Assert
        assertFalse(result.isPresent(), "Authentication should fail with invalid password");
    }

    @Test
    void authenticate_withNullUsername_shouldReturnEmpty() {
        // Act
        Optional<User> result = authenticationService.authenticate(null, "testpassword");
        
        // Assert
        assertFalse(result.isPresent(), "Authentication should fail with null username");
    }

    @Test
    void authenticate_withNullPassword_shouldReturnEmpty() {
        // Act
        Optional<User> result = authenticationService.authenticate("testuser", null);
        
        // Assert
        assertFalse(result.isPresent(), "Authentication should fail with null password");
    }

    @Test
    void getCurrentUser_afterSuccessfulAuthentication_shouldReturnUser() {
        // Arrange
        authenticationService.authenticate("testuser", "testpassword");
        
        // Act
        Optional<User> result = authenticationService.getCurrentUser();
        
        // Assert
        assertTrue(result.isPresent(), "Current user should be present after successful authentication");
        assertEquals(testUser.getUsername().value(), result.get().getUsername().value(), "Username should match");
    }

    @Test
    void getCurrentUser_beforeAuthentication_shouldReturnEmpty() {
        // Act
        Optional<User> result = authenticationService.getCurrentUser();
        
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
}