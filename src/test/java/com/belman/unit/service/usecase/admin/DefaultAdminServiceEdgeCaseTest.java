package com.belman.unit.service.usecase.admin;

import com.belman.application.usecase.admin.AdminService;
import com.belman.application.usecase.admin.DefaultAdminService;
import com.belman.application.usecase.security.BCryptPasswordHasher;
import com.belman.dataaccess.persistence.memory.InMemoryUserRepository;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.security.PasswordHasher;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserRepository;
import com.belman.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Edge case tests for the DefaultAdminService class.
 * These tests verify that the admin service correctly handles edge cases and error conditions.
 */
public class DefaultAdminServiceEdgeCaseTest {

    private AdminService adminService;
    private UserRepository userRepository;
    private PasswordHasher passwordHasher;
    private LoggerFactory mockLoggerFactory;
    private Logger mockLogger;

    @BeforeEach
    public void setUp() {
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
    public void testCreateUser_ThrowsException_WithInvalidEmailFormat() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            adminService.createUser("user", "password", "John", "Doe", "invalid-email", new UserRole[]{UserRole.ADMIN});
        });
    }

    @Test
    public void testCreateUser_ThrowsException_WithNullUsername() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            adminService.createUser(null, "password", "John", "Doe", "john@example.com", new UserRole[]{UserRole.ADMIN});
        });
    }

    @Test
    public void testCreateUser_ThrowsException_WithNullPassword() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            adminService.createUser("user", null, "John", "Doe", "john@example.com", new UserRole[]{UserRole.ADMIN});
        });
    }

    @Test
    public void testCreateUser_ThrowsException_WithNullEmail() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            adminService.createUser("user", "password", "John", "Doe", null, new UserRole[]{UserRole.ADMIN});
        });
    }

    @Test
    public void testCreateUser_ThrowsException_WithNullRoles() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            adminService.createUser("user", "password", "John", "Doe", "john@example.com", null);
        });
    }

    @Test
    public void testAssignRole_ReturnsFalse_WhenUserAlreadyHasRole() {
        // Arrange
        UserBusiness user = createTestUser("user", "password", "John", "Doe", "john@example.com", 
                new UserRole[]{UserRole.ADMIN});

        // Act
        boolean result = adminService.assignRole(user.getId(), UserRole.ADMIN);

        // Assert
        assertFalse(result);

        // Verify the role was not duplicated
        Optional<UserBusiness> updatedUser = userRepository.findById(user.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(1, updatedUser.get().getRoles().size());
        assertTrue(updatedUser.get().getRoles().contains(UserRole.ADMIN));
    }

    @Test
    public void testRemoveRole_ReturnsFalse_WhenRemovingAdminRole() {
        // Arrange
        UserBusiness user = createTestUser("user", "password", "John", "Doe", "john@example.com", 
                new UserRole[]{UserRole.ADMIN});

        // Act
        boolean result = adminService.removeRole(user.getId(), UserRole.ADMIN);

        // Assert
        assertFalse(result);

        // Verify the role was not removed
        Optional<UserBusiness> updatedUser = userRepository.findById(user.getId());
        assertTrue(updatedUser.isPresent());
        assertTrue(updatedUser.get().getRoles().contains(UserRole.ADMIN));
    }

    @Test
    public void testRemoveRole_ReturnsFalse_WhenRemovingRoleUserDoesNotHave() {
        // Arrange
        UserBusiness user = createTestUser("user", "password", "John", "Doe", "john@example.com", 
                new UserRole[]{UserRole.ADMIN});

        // Act
        boolean result = adminService.removeRole(user.getId(), UserRole.PRODUCTION);

        // Assert
        assertFalse(result);

        // Verify the user's roles are unchanged
        Optional<UserBusiness> updatedUser = userRepository.findById(user.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(1, updatedUser.get().getRoles().size());
        assertTrue(updatedUser.get().getRoles().contains(UserRole.ADMIN));
    }

    @Test
    public void testResetPassword_ReturnsFalse_WithEmptyPassword() {
        // Arrange
        UserBusiness user = createTestUser("user", "password", "John", "Doe", "john@example.com", 
                new UserRole[]{UserRole.ADMIN});

        // Act
        boolean result = adminService.resetPassword(user.getId(), "");

        // Assert
        assertFalse(result);

        // Verify the password was not changed
        Optional<UserBusiness> updatedUser = userRepository.findById(user.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(user.getPassword().value(), updatedUser.get().getPassword().value());
    }

    @Test
    public void testResetPassword_ReturnsTrue_WithShortPassword() {
        // Arrange
        UserBusiness user = createTestUser("user", "password", "John", "Doe", "john@example.com", 
                new UserRole[]{UserRole.ADMIN});

        // Act
        boolean result = adminService.resetPassword(user.getId(), "123");

        // Assert
        assertTrue(result);

        // Verify the password was changed
        Optional<UserBusiness> updatedUser = userRepository.findById(user.getId());
        assertTrue(updatedUser.isPresent());
        assertNotEquals(user.getPassword().value(), updatedUser.get().getPassword().value());
    }

    /**
     * Helper method to create a test user.
     */
    private UserBusiness createTestUser(String username, String password, String firstName, String lastName, String email, UserRole[] roles) {
        return adminService.createUser(username, password, firstName, lastName, email, roles);
    }
}
