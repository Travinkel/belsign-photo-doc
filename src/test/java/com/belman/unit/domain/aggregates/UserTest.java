package com.belman.unit.domain.aggregates;

import com.belman.domain.aggregates.User;
import com.belman.domain.aggregates.User.Role;
import com.belman.domain.enums.UserStatus;
import com.belman.domain.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the User aggregate.
 */
class UserTest {

    private UserId userId;
    private Username username;
    private HashedPassword password;
    private EmailAddress email;
    private PersonName name;
    private PhoneNumber phoneNumber;

    @BeforeEach
    void setUp() {
        userId = UserId.newId();
        username = new Username("testuser");
        password = new HashedPassword("hashedpassword123");
        email = new EmailAddress("test@example.com");
        name = new PersonName("John", "Doe");
        phoneNumber = new PhoneNumber("+45 12345678");
    }

    @Test
    void constructor_withValidParameters_shouldCreateUser() {
        // When
        User user = new User(userId, username, password, email);

        // Then
        assertEquals(userId, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(email, user.getEmail());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void constructor_withNullId_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new User(null, username, password, email));
    }

    @Test
    void constructor_withNullUsername_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new User(userId, null, password, email));
    }

    @Test
    void constructor_withNullPassword_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new User(userId, username, null, email));
    }

    @Test
    void constructor_withNullEmail_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new User(userId, username, password, null));
    }

    @Test
    void setName_withValidName_shouldUpdateName() {
        // Given
        User user = new User(userId, username, password, email);

        // When
        user.setName(name);

        // Then
        assertEquals(name, user.getName());
    }

    @Test
    void setName_withNullName_shouldThrowException() {
        // Given
        User user = new User(userId, username, password, email);

        // When/Then
        assertThrows(NullPointerException.class, () -> user.setName(null));
    }

    @Test
    void setEmail_withValidEmail_shouldUpdateEmail() {
        // Given
        User user = new User(userId, username, password, email);
        EmailAddress newEmail = new EmailAddress("new@example.com");

        // When
        user.setEmail(newEmail);

        // Then
        assertEquals(newEmail, user.getEmail());
    }

    @Test
    void setEmail_withNullEmail_shouldThrowException() {
        // Given
        User user = new User(userId, username, password, email);

        // When/Then
        assertThrows(NullPointerException.class, () -> user.setEmail(null));
    }

    @Test
    void setPhoneNumber_withValidPhoneNumber_shouldUpdatePhoneNumber() {
        // Given
        User user = new User(userId, username, password, email);

        // When
        user.setPhoneNumber(phoneNumber);

        // Then
        assertEquals(phoneNumber, user.getPhoneNumber());
    }

    @Test
    void setPhoneNumber_withNullPhoneNumber_shouldAllowNull() {
        // Given
        User user = new User(userId, username, password, email);
        user.setPhoneNumber(phoneNumber);

        // When
        user.setPhoneNumber(null);

        // Then
        assertNull(user.getPhoneNumber());
    }

    @Test
    void setStatus_withValidStatus_shouldUpdateStatus() {
        // Given
        User user = new User(userId, username, password, email);

        // When
        user.setStatus(UserStatus.INACTIVE);

        // Then
        assertEquals(UserStatus.INACTIVE, user.getStatus());
    }

    @Test
    void setStatus_withNullStatus_shouldThrowException() {
        // Given
        User user = new User(userId, username, password, email);

        // When/Then
        assertThrows(NullPointerException.class, () -> user.setStatus(null));
    }

    @Test
    void addRole_withValidRole_shouldAddRole() {
        // Given
        User user = new User(userId, username, password, email);

        // When
        user.addRole(Role.ADMIN);

        // Then
        assertTrue(user.getRoles().contains(Role.ADMIN));
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void addRole_withNullRole_shouldThrowException() {
        // Given
        User user = new User(userId, username, password, email);

        // When/Then
        assertThrows(NullPointerException.class, () -> user.addRole(null));
    }

    @Test
    void removeRole_withExistingRole_shouldRemoveRole() {
        // Given
        User user = new User(userId, username, password, email);
        user.addRole(Role.ADMIN);

        // When
        user.removeRole(Role.ADMIN);

        // Then
        assertFalse(user.getRoles().contains(Role.ADMIN));
        assertEquals(0, user.getRoles().size());
    }

    @Test
    void removeRole_withNonExistingRole_shouldNotChangeRoles() {
        // Given
        User user = new User(userId, username, password, email);
        user.addRole(Role.ADMIN);

        // When
        user.removeRole(Role.QA);

        // Then
        assertTrue(user.getRoles().contains(Role.ADMIN));
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void removeRole_withNullRole_shouldThrowException() {
        // Given
        User user = new User(userId, username, password, email);

        // When/Then
        assertThrows(NullPointerException.class, () -> user.removeRole(null));
    }

    @Test
    void isActive_withActiveStatus_shouldReturnTrue() {
        // Given
        User user = new User(userId, username, password, email);
        user.setStatus(UserStatus.ACTIVE);

        // When/Then
        assertTrue(user.isActive());
        assertFalse(user.isInactive());
        assertFalse(user.isLocked());
        assertFalse(user.isPending());
    }

    @Test
    void isInactive_withInactiveStatus_shouldReturnTrue() {
        // Given
        User user = new User(userId, username, password, email);
        user.setStatus(UserStatus.INACTIVE);

        // When/Then
        assertFalse(user.isActive());
        assertTrue(user.isInactive());
        assertFalse(user.isLocked());
        assertFalse(user.isPending());
    }

    @Test
    void isLocked_withLockedStatus_shouldReturnTrue() {
        // Given
        User user = new User(userId, username, password, email);
        user.setStatus(UserStatus.LOCKED);

        // When/Then
        assertFalse(user.isActive());
        assertFalse(user.isInactive());
        assertTrue(user.isLocked());
        assertFalse(user.isPending());
    }

    @Test
    void isPending_withPendingStatus_shouldReturnTrue() {
        // Given
        User user = new User(userId, username, password, email);
        user.setStatus(UserStatus.PENDING);

        // When/Then
        assertFalse(user.isActive());
        assertFalse(user.isInactive());
        assertFalse(user.isLocked());
        assertTrue(user.isPending());
    }

    @Test
    void activate_shouldSetStatusToActive() {
        // Given
        User user = new User(userId, username, password, email);
        user.setStatus(UserStatus.INACTIVE);

        // When
        user.activate();

        // Then
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertTrue(user.isActive());
    }

    @Test
    void deactivate_shouldSetStatusToInactive() {
        // Given
        User user = new User(userId, username, password, email);

        // When
        user.deactivate();

        // Then
        assertEquals(UserStatus.INACTIVE, user.getStatus());
        assertTrue(user.isInactive());
    }

    @Test
    void lock_shouldSetStatusToLocked() {
        // Given
        User user = new User(userId, username, password, email);

        // When
        user.lock();

        // Then
        assertEquals(UserStatus.LOCKED, user.getStatus());
        assertTrue(user.isLocked());
    }
}