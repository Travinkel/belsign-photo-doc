package com.belman.integration.service;

import com.belman.application.usecase.admin.AdminService;
import com.belman.application.usecase.admin.DefaultAdminService;
import com.belman.application.usecase.security.BCryptPasswordHasher;
import com.belman.dataaccess.persistence.memory.InMemoryUserRepository;
import com.belman.domain.security.PasswordHasher;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserRepository;
import com.belman.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the interaction between AdminService and UserRepository.
 * These tests verify that the AdminService correctly interacts with the UserRepository
 * when creating, retrieving, and deleting users.
 */
public class AdminServiceUserRepositoryIntegrationTest {

    private AdminService adminService;
    private UserRepository userRepository;
    private PasswordHasher passwordHasher;
    private LoggerFactory mockLoggerFactory;
    private Logger mockLogger;

    @BeforeEach
    public void setUp() {
        // Create a real UserRepository with no default users
        userRepository = new InMemoryUserRepository(false);

        // Create a real PasswordHasher
        passwordHasher = new BCryptPasswordHasher();

        // Create mock logger and logger factory
        mockLogger = mock(Logger.class);
        mockLoggerFactory = mock(LoggerFactory.class);
        when(mockLoggerFactory.getLogger(any())).thenReturn(mockLogger);

        // Create the AdminService with the real dependencies and mock logger factory
        adminService = new DefaultAdminService(userRepository, passwordHasher, mockLoggerFactory);
    }

    @Test
    public void testCreateUser_UserIsStoredInRepository() {
        // Arrange
        String username = "testuser";
        String password = "password";
        String firstName = "Test";
        String lastName = "User";
        String email = "test@example.com";
        UserRole[] roles = {UserRole.ADMIN};

        // Act
        UserBusiness createdUser = adminService.createUser(username, password, firstName, lastName, email, roles);

        // Assert
        // Verify that the user was created successfully
        assertNotNull(createdUser);
        assertEquals(username, createdUser.getUsername().value());

        // Verify that the user is stored in the repository
        Optional<UserBusiness> retrievedUser = userRepository.findById(createdUser.getId());
        assertTrue(retrievedUser.isPresent());
        assertEquals(username, retrievedUser.get().getUsername().value());
        assertEquals(email, retrievedUser.get().getEmail().value());
        assertTrue(retrievedUser.get().getRoles().contains(UserRole.ADMIN));
    }

    @Test
    public void testDeleteUser_UserIsRemovedFromRepository() {
        // Arrange
        String username = "testuser";
        String password = "password";
        String firstName = "Test";
        String lastName = "User";
        String email = "test@example.com";
        UserRole[] roles = {UserRole.ADMIN};

        UserBusiness createdUser = adminService.createUser(username, password, firstName, lastName, email, roles);
        UserId userId = createdUser.getId();

        // Verify that the user exists in the repository
        assertTrue(userRepository.findById(userId).isPresent());

        // Act
        boolean result = adminService.deleteUser(userId);

        // Assert
        // Verify that the deletion was successful
        assertTrue(result);

        // Verify that the user is no longer in the repository
        assertFalse(userRepository.findById(userId).isPresent());
    }

    @Test
    public void testGetAllUsers_ReturnsAllUsersFromRepository() {
        // Arrange
        // Create several users
        adminService.createUser("user1", "password", "John", "Doe", "john@example.com", new UserRole[]{UserRole.ADMIN});
        adminService.createUser("user2", "password", "Jane", "Doe", "jane@example.com", new UserRole[]{UserRole.PRODUCTION});
        adminService.createUser("user3", "password", "Bob", "Smith", "bob@example.com", new UserRole[]{UserRole.QA});

        // Act
        List<UserBusiness> users = adminService.getAllUsers();

        // Assert
        // Verify that all users are returned
        assertEquals(3, users.size());

        // Verify that the repository contains the same number of users
        assertEquals(3, userRepository.findAll().size());
    }

    @Test
    public void testAssignRole_RoleIsAddedToUserInRepository() {
        // Arrange
        String username = "testuser";
        String password = "password";
        String firstName = "Test";
        String lastName = "User";
        String email = "test@example.com";
        UserRole[] roles = {UserRole.ADMIN};

        UserBusiness createdUser = adminService.createUser(username, password, firstName, lastName, email, roles);
        UserId userId = createdUser.getId();

        // Verify that the user has only the ADMIN role
        assertEquals(1, userRepository.findById(userId).get().getRoles().size());
        assertTrue(userRepository.findById(userId).get().getRoles().contains(UserRole.ADMIN));

        // Act
        boolean result = adminService.assignRole(userId, UserRole.PRODUCTION);

        // Assert
        // Verify that the role assignment was successful
        assertTrue(result);

        // Verify that the user now has both roles in the repository
        Optional<UserBusiness> updatedUser = userRepository.findById(userId);
        assertTrue(updatedUser.isPresent());
        assertEquals(2, updatedUser.get().getRoles().size());
        assertTrue(updatedUser.get().getRoles().contains(UserRole.ADMIN));
        assertTrue(updatedUser.get().getRoles().contains(UserRole.PRODUCTION));
    }

    @Test
    public void testRemoveRole_RoleIsRemovedFromUserInRepository() {
        // Arrange
        String username = "testuser";
        String password = "password";
        String firstName = "Test";
        String lastName = "User";
        String email = "test@example.com";
        UserRole[] roles = {UserRole.ADMIN, UserRole.PRODUCTION};

        UserBusiness createdUser = adminService.createUser(username, password, firstName, lastName, email, roles);
        UserId userId = createdUser.getId();

        // Verify that the user has both roles
        assertEquals(2, userRepository.findById(userId).get().getRoles().size());
        assertTrue(userRepository.findById(userId).get().getRoles().contains(UserRole.ADMIN));
        assertTrue(userRepository.findById(userId).get().getRoles().contains(UserRole.PRODUCTION));

        // Act
        boolean result = adminService.removeRole(userId, UserRole.PRODUCTION);

        // Assert
        // Verify that the role removal was successful
        assertTrue(result);

        // Verify that the user now has only the ADMIN role in the repository
        Optional<UserBusiness> updatedUser = userRepository.findById(userId);
        assertTrue(updatedUser.isPresent());
        assertEquals(1, updatedUser.get().getRoles().size());
        assertTrue(updatedUser.get().getRoles().contains(UserRole.ADMIN));
        assertFalse(updatedUser.get().getRoles().contains(UserRole.PRODUCTION));
    }

    @Test
    public void testResetPassword_PasswordIsUpdatedInRepository() {
        // Arrange
        String username = "testuser";
        String password = "oldpassword";
        String firstName = "Test";
        String lastName = "User";
        String email = "test@example.com";
        UserRole[] roles = {UserRole.ADMIN};

        UserBusiness createdUser = adminService.createUser(username, password, firstName, lastName, email, roles);
        UserId userId = createdUser.getId();

        // Store the original password hash for comparison
        String originalPasswordHash = userRepository.findById(userId).get().getPassword().value();

        // Act
        boolean result = adminService.resetPassword(userId, "newpassword");

        // Assert
        // Verify that the password reset was successful
        assertTrue(result);

        // Verify that the password hash has changed in the repository
        Optional<UserBusiness> updatedUser = userRepository.findById(userId);
        assertTrue(updatedUser.isPresent());
        assertNotEquals(originalPasswordHash, updatedUser.get().getPassword().value());
    }
}
