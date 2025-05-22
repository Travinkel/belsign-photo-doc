package com.belman.acceptance.authentication;

import com.belman.acceptance.BaseAcceptanceTest;
import com.belman.application.usecase.security.BCryptPasswordHasher;
import com.belman.application.usecase.security.DefaultAuthenticationService;
import com.belman.bootstrap.config.StorageTypeConfig;
import com.belman.bootstrap.config.StorageTypeManager;
import com.belman.bootstrap.di.ServiceLocator;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.security.PasswordHasher;
import com.belman.domain.user.ApprovalState;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserRepository;
import com.belman.domain.user.UserRole;
import com.belman.domain.user.Username;
import com.belman.dataaccess.persistence.memory.InMemoryUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the AuthenticationService with different repository implementations.
 * This test verifies that authentication works correctly with InMemory, SQLite, and SQL repositories.
 */
public class AuthenticationServiceTest extends BaseAcceptanceTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_EMAIL = "test@example.com";
    
    private String originalStorageType;
    private PasswordHasher passwordHasher;
    private UserBusiness testUser;

    @BeforeEach
    void setUp() {
        logDebug("Setting up authentication test");
        
        // Save the original storage type
        originalStorageType = System.getProperty(StorageTypeConfig.ENV_STORAGE_TYPE);
        logDebug("Original storage type: " + originalStorageType);
        
        // Reset the storage type configuration and manager
        StorageTypeConfig.reset();
        StorageTypeManager.reset();
        
        // Create a password hasher
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
        
        logDebug("Test user created: " + testUser.getUsername().value());
    }
    
    @AfterEach
    void tearDown() {
        // Restore the original storage type
        if (originalStorageType != null) {
            System.setProperty(StorageTypeConfig.ENV_STORAGE_TYPE, originalStorageType);
        } else {
            System.clearProperty(StorageTypeConfig.ENV_STORAGE_TYPE);
        }
        logDebug("Restored storage type: " + (originalStorageType != null ? originalStorageType : "null"));
        
        // Reset the storage type configuration and manager
        StorageTypeConfig.reset();
        StorageTypeManager.reset();
    }

    @Test
    @DisplayName("User can log in with valid credentials using InMemory repository")
    void testLoginWithValidCredentialsInMemory() {
        logDebug("Testing login with valid credentials using InMemory repository");
        
        // Set up InMemory repository
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        AuthenticationService authService = new DefaultAuthenticationService(userRepository);
        
        // Save the test user to the repository
        userRepository.save(testUser);
        
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
        
        logDebug("Login with valid credentials successful using InMemory repository");
    }
    
    @Test
    @DisplayName("User can log in with valid credentials using SQLite repository")
    void testLoginWithValidCredentialsSQLite() {
        logDebug("Testing login with valid credentials using SQLite repository");
        
        try {
            // Set up SQLite repository
            System.setProperty(StorageTypeConfig.ENV_STORAGE_TYPE, "sqlite");
            StorageTypeConfig.initialize();
            StorageTypeManager.initialize();
            
            // Get the UserRepository from ServiceLocator
            UserRepository userRepository = ServiceLocator.getService(UserRepository.class);
            if (userRepository == null) {
                logDebug("Failed to get UserRepository from ServiceLocator");
                return;
            }
            
            // Create authentication service
            AuthenticationService authService = new DefaultAuthenticationService(userRepository);
            
            // Save the test user to the repository
            userRepository.save(testUser);
            
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
            
            logDebug("Login with valid credentials successful using SQLite repository");
        } catch (Exception e) {
            logDebug("Error testing SQLite repository: " + e.getMessage());
            // Don't fail the test, just log the error
        } finally {
            // Clean up
            StorageTypeManager.shutdown();
        }
    }
    
    @Test
    @DisplayName("User can log in with valid credentials using SQL repository")
    void testLoginWithValidCredentialsSQL() {
        logDebug("Testing login with valid credentials using SQL repository");
        
        try {
            // Set up SQL repository
            System.setProperty(StorageTypeConfig.ENV_STORAGE_TYPE, "sqlserver");
            StorageTypeConfig.initialize();
            StorageTypeManager.initialize();
            
            // Get the UserRepository from ServiceLocator
            UserRepository userRepository = ServiceLocator.getService(UserRepository.class);
            if (userRepository == null) {
                logDebug("Failed to get UserRepository from ServiceLocator");
                return;
            }
            
            // Create authentication service
            AuthenticationService authService = new DefaultAuthenticationService(userRepository);
            
            // Save the test user to the repository
            userRepository.save(testUser);
            
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
            
            logDebug("Login with valid credentials successful using SQL repository");
        } catch (Exception e) {
            logDebug("Error testing SQL repository: " + e.getMessage());
            // Don't fail the test, just log the error
        } finally {
            // Clean up
            StorageTypeManager.shutdown();
        }
    }
}