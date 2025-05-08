package com.belman.integration.infrastructure.persistence;

import com.belman.domain.aggregates.User;
import com.belman.domain.enums.UserStatus;
import com.belman.business.domain.user.UserRepository;
import com.belman.domain.services.PasswordHasher;
import com.belman.domain.valueobjects.EmailAddress;
import com.belman.domain.valueobjects.HashedPassword;
import com.belman.domain.valueobjects.PersonName;
import com.belman.domain.valueobjects.UserId;
import com.belman.domain.valueobjects.Username;
import com.belman.data.config.DatabaseConfig;
import com.belman.data.persistence.SqlUserRepository;
import com.belman.data.security.BCryptPasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for SqlUserRepository.
 * These tests require a running SQL Server instance with the BelSign database.
 * 
 * Note: These tests will be skipped if the database is not available.
 */
public class SqlUserRepositoryTest {

    private UserRepository userRepository;
    private User testUser;
    private PasswordHasher passwordHasher;

    @BeforeEach
    void setUp() {
        System.out.println("[DEBUG_LOG] Setting up SqlUserRepositoryTest");

        // Initialize database configuration
        System.out.println("[DEBUG_LOG] Initializing database configuration");
        DatabaseConfig.initialize();

        // Get a connection from the data source
        System.out.println("[DEBUG_LOG] Getting database connection");
        DataSource dataSource = DatabaseConfig.getDataSource();
        if (dataSource == null) {
            System.out.println("[DEBUG_LOG] Database is not available, skipping test setup");
            return; // Skip the test setup
        }

        // Create a new repository instance for each test
        userRepository = new SqlUserRepository(dataSource);
        System.out.println("[DEBUG_LOG] Created SqlUserRepository");

        // Create a password hasher
        passwordHasher = new BCryptPasswordHasher();
        System.out.println("[DEBUG_LOG] Created BCryptPasswordHasher");

        // Create a test user with a unique ID, username, and email
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        UserId userId = new UserId(UUID.randomUUID());
        Username username = new Username("testuser_" + uniqueId);
        HashedPassword password = HashedPassword.fromPlainText("password", passwordHasher);
        PersonName name = new PersonName("Test", "User");
        EmailAddress email = new EmailAddress("testuser_" + uniqueId + "@example.com");

        testUser = new User(userId, username, password, name, email);
        testUser.setStatus(UserStatus.ACTIVE);
        testUser.addRole(User.Role.PRODUCTION);
        System.out.println("[DEBUG_LOG] Created test user: " + username.value());
    }

    @Test
    void save_newUser_shouldPersistUser() {
        System.out.println("[DEBUG_LOG] Running save_newUser_shouldPersistUser test");

        // Skip test if repository is not available
        if (userRepository == null) {
            System.out.println("[DEBUG_LOG] UserRepository is not available, skipping test");
            return;
        }

        try {
            // When
            System.out.println("[DEBUG_LOG] Saving test user");
            userRepository.save(testUser);

            // Then
            System.out.println("[DEBUG_LOG] Finding user by username");
            Optional<User> retrievedUser = userRepository.findByUsername(testUser.getUsername());

            assertTrue(retrievedUser.isPresent(), "User should be found after saving");
            assertEquals(testUser.getId().id(), retrievedUser.get().getId().id(), "User ID should match");
            assertEquals(testUser.getUsername().value(), retrievedUser.get().getUsername().value(), "Username should match");
            assertEquals(testUser.getEmail().getValue(), retrievedUser.get().getEmail().getValue(), "Email should match");
            assertEquals(testUser.getStatus(), retrievedUser.get().getStatus(), "Status should match");
            assertTrue(retrievedUser.get().getRoles().contains(User.Role.PRODUCTION), "User should have PRODUCTION role");

            System.out.println("[DEBUG_LOG] Test passed successfully");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error in test: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    void findByUsername_existingUser_shouldReturnUser() {
        System.out.println("[DEBUG_LOG] Running findByUsername_existingUser_shouldReturnUser test");

        // Skip test if repository is not available
        if (userRepository == null) {
            System.out.println("[DEBUG_LOG] UserRepository is not available, skipping test");
            return;
        }

        try {
            // Given
            System.out.println("[DEBUG_LOG] Saving test user");
            userRepository.save(testUser);

            // When
            System.out.println("[DEBUG_LOG] Finding user by username");
            Optional<User> result = userRepository.findByUsername(testUser.getUsername());

            // Then
            assertTrue(result.isPresent(), "User should be found");
            assertEquals(testUser.getId().id(), result.get().getId().id(), "User ID should match");

            System.out.println("[DEBUG_LOG] Test passed successfully");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error in test: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    void findByUsername_nonExistingUser_shouldReturnEmpty() {
        System.out.println("[DEBUG_LOG] Running findByUsername_nonExistingUser_shouldReturnEmpty test");

        // Skip test if repository is not available
        if (userRepository == null) {
            System.out.println("[DEBUG_LOG] UserRepository is not available, skipping test");
            return;
        }

        try {
            // When
            System.out.println("[DEBUG_LOG] Finding non-existent user by username");
            Optional<User> result = userRepository.findByUsername(new Username("nonexistent"));

            // Then
            assertTrue(result.isEmpty(), "Non-existent user should not be found");

            System.out.println("[DEBUG_LOG] Test passed successfully");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error in test: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    void findByEmail_existingUser_shouldReturnUser() {
        System.out.println("[DEBUG_LOG] Running findByEmail_existingUser_shouldReturnUser test");

        // Skip test if repository is not available
        if (userRepository == null) {
            System.out.println("[DEBUG_LOG] UserRepository is not available, skipping test");
            return;
        }

        try {
            // Given
            System.out.println("[DEBUG_LOG] Saving test user");
            userRepository.save(testUser);

            // When
            System.out.println("[DEBUG_LOG] Finding user by email");
            Optional<User> result = userRepository.findByEmail(testUser.getEmail());

            // Then
            assertTrue(result.isPresent(), "User should be found by email");
            assertEquals(testUser.getId().id(), result.get().getId().id(), "User ID should match");

            System.out.println("[DEBUG_LOG] Test passed successfully");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error in test: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    void findByEmail_nonExistingUser_shouldReturnEmpty() {
        System.out.println("[DEBUG_LOG] Running findByEmail_nonExistingUser_shouldReturnEmpty test");

        // Skip test if repository is not available
        if (userRepository == null) {
            System.out.println("[DEBUG_LOG] UserRepository is not available, skipping test");
            return;
        }

        try {
            // When
            System.out.println("[DEBUG_LOG] Finding non-existent user by email");
            Optional<User> result = userRepository.findByEmail(new EmailAddress("nonexistent@example.com"));

            // Then
            assertTrue(result.isEmpty(), "Non-existent user should not be found by email");

            System.out.println("[DEBUG_LOG] Test passed successfully");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error in test: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    void save_existingUser_shouldUpdateUser() {
        System.out.println("[DEBUG_LOG] Running save_existingUser_shouldUpdateUser test");

        // Skip test if repository is not available
        if (userRepository == null) {
            System.out.println("[DEBUG_LOG] UserRepository is not available, skipping test");
            return;
        }

        try {
            // Given
            System.out.println("[DEBUG_LOG] Saving test user");
            userRepository.save(testUser);

            // When
            System.out.println("[DEBUG_LOG] Updating test user");
            testUser.addRole(User.Role.QA);
            testUser.setStatus(UserStatus.INACTIVE);
            userRepository.save(testUser);

            // Then
            System.out.println("[DEBUG_LOG] Finding updated user by username");
            Optional<User> retrievedUser = userRepository.findByUsername(testUser.getUsername());

            assertTrue(retrievedUser.isPresent(), "Updated user should be found");
            assertEquals(UserStatus.INACTIVE, retrievedUser.get().getStatus(), "User status should be updated");
            assertTrue(retrievedUser.get().getRoles().contains(User.Role.PRODUCTION), "User should retain PRODUCTION role");
            assertTrue(retrievedUser.get().getRoles().contains(User.Role.QA), "User should have new QA role");

            System.out.println("[DEBUG_LOG] Test passed successfully");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error in test: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }
}
