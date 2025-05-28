package com.belman.unit.application.usecase.admin;

import com.belman.application.usecase.admin.AdminService;
import com.belman.application.usecase.admin.DefaultAdminService;
import com.belman.application.usecase.security.BCryptPasswordHasher;
import com.belman.dataaccess.persistence.memory.InMemoryUserRepository;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.PersonName;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.security.PasswordHasher;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserRepository;
import com.belman.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DefaultAdminService class.
 * These tests verify that the admin service correctly manages users.
 */
public class DefaultAdminServiceTest {

    private AdminService adminService;
    private UserRepository userRepository;
    private PasswordHasher passwordHasher;
    private LoggerFactory mockLoggerFactory;
    private Logger mockLogger;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create mock logger and logger factory
        mockLogger = mock(Logger.class);
        mockLoggerFactory = mock(LoggerFactory.class);
        when(mockLoggerFactory.getLogger(any())).thenReturn(mockLogger);

        userRepository = new InMemoryUserRepository(false); // Don't create default users
        passwordHasher = new BCryptPasswordHasher();

        // Create DefaultAdminService with mock logger factory
        adminService = new DefaultAdminService(userRepository, passwordHasher, mockLoggerFactory);
    }

    @Test
    public void testGetAllUsers_ReturnsEmptyList_WhenNoUsersExist() {
        // Act
        List<UserBusiness> users = adminService.getAllUsers();

        // Assert
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    public void testGetAllUsers_ReturnsAllUsers_WhenUsersExist() {
        // Arrange
        createTestUser("user1", "password", "John", "Doe", "john@example.com", new UserRole[]{UserRole.ADMIN});
        createTestUser("user2", "password", "Jane", "Doe", "jane@example.com", new UserRole[]{UserRole.PRODUCTION});

        // Act
        List<UserBusiness> users = adminService.getAllUsers();

        // Assert
        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    public void testCreateUser_CreatesUser_WithValidData() {
        // Arrange
        String username = "testuser";
        String password = "password";
        String firstName = "Test";
        String lastName = "User";
        String email = "test@example.com";
        UserRole[] roles = {UserRole.ADMIN};

        // Act
        UserBusiness user = adminService.createUser(username, password, firstName, lastName, email, roles);

        // Assert
        assertNotNull(user);
        assertEquals(username, user.getUsername().value());
        assertEquals(firstName, user.getName().firstName());
        assertEquals(lastName, user.getName().lastName());
        assertEquals(email, user.getEmail().value());
        assertTrue(user.getRoles().contains(UserRole.ADMIN));
    }

    @Test
    public void testCreateUser_ThrowsException_WithDuplicateUsername() {
        // Arrange
        createTestUser("duplicate", "password", "John", "Doe", "john@example.com", new UserRole[]{UserRole.ADMIN});

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            adminService.createUser("duplicate", "password", "Jane", "Doe", "jane@example.com", new UserRole[]{UserRole.ADMIN});
        });
    }

    @Test
    public void testCreateUser_ThrowsException_WithEmptyUsername() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            adminService.createUser("", "password", "John", "Doe", "john@example.com", new UserRole[]{UserRole.ADMIN});
        });
    }

    @Test
    public void testCreateUser_ThrowsException_WithEmptyPassword() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            adminService.createUser("user", "", "John", "Doe", "john@example.com", new UserRole[]{UserRole.ADMIN});
        });
    }

    @Test
    public void testCreateUser_ThrowsException_WithShortPassword() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            adminService.createUser("user", "123", "John", "Doe", "john@example.com", new UserRole[]{UserRole.ADMIN});
        });
    }

    @Test
    public void testCreateUser_ThrowsException_WithNoRoles() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            adminService.createUser("user", "password", "John", "Doe", "john@example.com", new UserRole[]{});
        });
    }

    @Test
    public void testDeleteUser_DeletesUser_WhenUserExists() {
        // Arrange
        UserBusiness user = createTestUser("user", "password", "John", "Doe", "john@example.com", new UserRole[]{UserRole.ADMIN});

        // Act
        boolean result = adminService.deleteUser(user.getId());

        // Assert
        assertTrue(result);
        assertEquals(0, adminService.getAllUsers().size());
    }

    @Test
    public void testDeleteUser_ReturnsFalse_WhenUserDoesNotExist() {
        // Act
        boolean result = adminService.deleteUser(UserId.newId());

        // Assert
        assertFalse(result);
    }

    @Test
    public void testAssignRole_AddsRole_WhenUserExists() {
        // Arrange
        UserBusiness user = createTestUser("user", "password", "John", "Doe", "john@example.com", new UserRole[]{UserRole.ADMIN});

        // Act
        boolean result = adminService.assignRole(user.getId(), UserRole.PRODUCTION);

        // Assert
        assertTrue(result);

        // Verify the role was added
        Optional<UserBusiness> updatedUser = userRepository.findById(user.getId());
        assertTrue(updatedUser.isPresent());
        assertTrue(updatedUser.get().getRoles().contains(UserRole.PRODUCTION));
    }

    @Test
    public void testAssignRole_ReturnsFalse_WhenUserDoesNotExist() {
        // Act
        boolean result = adminService.assignRole(UserId.newId(), UserRole.ADMIN);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testRemoveRole_RemovesRole_WhenUserExists() {
        // Arrange
        UserBusiness user = createTestUser("user", "password", "John", "Doe", "john@example.com", 
                new UserRole[]{UserRole.ADMIN, UserRole.PRODUCTION});

        // Act
        boolean result = adminService.removeRole(user.getId(), UserRole.PRODUCTION);

        // Assert
        assertTrue(result);

        // Verify the role was removed
        Optional<UserBusiness> updatedUser = userRepository.findById(user.getId());
        assertTrue(updatedUser.isPresent());
        assertFalse(updatedUser.get().getRoles().contains(UserRole.PRODUCTION));
        assertTrue(updatedUser.get().getRoles().contains(UserRole.ADMIN));
    }

    @Test
    public void testRemoveRole_ReturnsFalse_WhenUserDoesNotExist() {
        // Act
        boolean result = adminService.removeRole(UserId.newId(), UserRole.ADMIN);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testResetPassword_ChangesPassword_WhenUserExists() {
        // Arrange
        UserBusiness user = createTestUser("user", "oldpassword", "John", "Doe", "john@example.com", new UserRole[]{UserRole.ADMIN});
        String newPassword = "newpassword";

        // Act
        boolean result = adminService.resetPassword(user.getId(), newPassword);

        // Assert
        assertTrue(result);

        // Verify the password was changed
        Optional<UserBusiness> updatedUser = userRepository.findById(user.getId());
        assertTrue(updatedUser.isPresent());

        // We can't directly check the password, but we can verify it's different
        assertNotEquals(user.getPassword().value(), updatedUser.get().getPassword().value());
    }

    @Test
    public void testResetPassword_ReturnsFalse_WhenUserDoesNotExist() {
        // Act
        boolean result = adminService.resetPassword(UserId.newId(), "newpassword");

        // Assert
        assertFalse(result);
    }

    /**
     * Helper method to create a test user.
     */
    private UserBusiness createTestUser(String username, String password, String firstName, String lastName, String email, UserRole[] roles) {
        return adminService.createUser(username, password, firstName, lastName, email, roles);
    }
}
