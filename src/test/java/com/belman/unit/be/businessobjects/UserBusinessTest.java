package com.belman.unit.be.businessobjects;

import com.belman.domain.audit.AuditFacade;
import com.belman.domain.audit.event.AuditEvent;
import com.belman.domain.common.EmailAddress;
import com.belman.domain.common.PersonName;
import com.belman.domain.common.PhoneNumber;
import com.belman.domain.core.BusinessObject;
import com.belman.domain.order.photo.PhotoId;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.security.PasswordHasher;
import com.belman.domain.user.ApprovalState;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserRole;
import com.belman.domain.user.UserStatus;
import com.belman.domain.user.Username;
import com.belman.repository.security.BCryptPasswordHasher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UserBusiness business object.
 */
class UserBusinessTest {

    private UserId userId;
    private Username username;
    private HashedPassword password;
    private EmailAddress email;
    private PersonName name;
    private PhoneNumber phoneNumber;
    private UserBusiness adminUser;
    private PasswordHasher passwordHasher;

    @Mock
    private AuditFacade auditFacade;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Set the AuditFacade on the BusinessObject class
        BusinessObject.setAuditFacade(auditFacade);

        // Initialize password hasher
        passwordHasher = new BCryptPasswordHasher();

        // Create test data
        userId = UserId.newId();
        username = new Username("testuser");
        password = HashedPassword.fromPlainText("password123", passwordHasher);
        email = new EmailAddress("user@example.com");
        name = new PersonName("Test", "User");
        phoneNumber = new PhoneNumber("+1234567890");

        // Create an admin user for approval/rejection tests
        UserId adminId = UserId.newId();
        Username adminUsername = new Username("admin");
        HashedPassword adminPassword = HashedPassword.fromPlainText("admin123", passwordHasher);
        EmailAddress adminEmail = new EmailAddress("admin@example.com");

        adminUser = new UserBusiness.Builder()
                .id(adminId)
                .username(adminUsername)
                .password(adminPassword)
                .email(adminEmail)
                .addRole(UserRole.ADMIN)
                .build();
    }

    @AfterEach
    void tearDown() {
        // Reset the AuditFacade to avoid affecting other tests
        BusinessObject.setAuditFacade(null);
    }

    @Test
    void createNewUser_withValidParameters_shouldCreateUser() {
        // When
        UserBusiness user = UserBusiness.createNewUser(username, password, email);

        // Then
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(email, user.getEmail());
        assertEquals(UserStatus.PENDING, user.getStatus());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void reconstitute_withValidParameters_shouldReconstituteUser() {
        // Given
        ApprovalState approvalState = ApprovalState.createApprovedState(adminUser, Instant.now());
        Set<UserRole> roles = Set.of(UserRole.PRODUCTION);

        // When
        UserBusiness user = UserBusiness.reconstitute(
                userId, username, password, name, email, phoneNumber, approvalState, roles);

        // Then
        assertEquals(userId, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(phoneNumber, user.getPhoneNumber());
        assertEquals(approvalState, user.getApprovalState());
        assertEquals(roles, user.getRoles());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    void builder_withValidParameters_shouldBuildUser() {
        // When
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .addRole(UserRole.PRODUCTION)
                .build();

        // Then
        assertEquals(userId, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(phoneNumber, user.getPhoneNumber());
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(UserRole.PRODUCTION));
        assertEquals(UserStatus.PENDING, user.getStatus());
    }

    @Test
    void builder_withNullId_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new UserBusiness.Builder()
                .username(username)
                .password(password)
                .email(email)
                .build());
    }

    @Test
    void builder_withNullUsername_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new UserBusiness.Builder()
                .id(userId)
                .password(password)
                .email(email)
                .build());
    }

    @Test
    void builder_withNullPassword_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .email(email)
                .build());
    }

    @Test
    void builder_withNullEmail_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .build());
    }

    @Test
    void setPassword_withValidPassword_shouldUpdatePassword() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .build();

        // When
        HashedPassword newPassword = HashedPassword.fromPlainText("newpassword123", passwordHasher);
        user.setPassword(newPassword);

        // Then
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    void setPassword_withNullPassword_shouldThrowException() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .build();

        // When/Then
        assertThrows(NullPointerException.class, () -> user.setPassword(null));
    }

    @Test
    void setEmail_withValidEmail_shouldUpdateEmail() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .build();

        // When
        EmailAddress newEmail = new EmailAddress("newemail@example.com");
        user.setEmail(newEmail);

        // Then
        assertEquals(newEmail, user.getEmail());
    }

    @Test
    void setEmail_withNullEmail_shouldThrowException() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .build();

        // When/Then
        assertThrows(NullPointerException.class, () -> user.setEmail(null));
    }

    @Test
    void setName_withValidName_shouldUpdateName() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .build();

        // When
        PersonName newName = new PersonName("New", "Name");
        user.setName(newName);

        // Then
        assertEquals(newName, user.getName());
    }

    @Test
    void setName_withNullName_shouldAllowNullName() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .name(name)
                .build();

        // When
        user.setName(null);

        // Then
        assertNull(user.getName());
    }

    @Test
    void setPhoneNumber_withValidPhoneNumber_shouldUpdatePhoneNumber() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .build();

        // When
        PhoneNumber newPhoneNumber = new PhoneNumber("+9876543210");
        user.setPhoneNumber(newPhoneNumber);

        // Then
        assertEquals(newPhoneNumber, user.getPhoneNumber());
    }

    @Test
    void setPhoneNumber_withNullPhoneNumber_shouldAllowNullPhoneNumber() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .phoneNumber(phoneNumber)
                .build();

        // When
        user.setPhoneNumber(null);

        // Then
        assertNull(user.getPhoneNumber());
    }

    @Test
    void approve_withValidReviewer_shouldApproveUser() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .build();

        // When
        Instant reviewTime = Instant.now();
        user.approve(adminUser, reviewTime);

        // Then
        assertTrue(user.getApprovalState().isApproved());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    void approve_withNullReviewer_shouldThrowException() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .build();

        // When/Then
        assertThrows(NullPointerException.class, () -> user.approve(null, Instant.now()));
    }

    @Test
    void approve_withNullReviewedAt_shouldThrowException() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .build();

        // When/Then
        assertThrows(NullPointerException.class, () -> user.approve(adminUser, null));
    }

    @Test
    void reject_withValidReviewer_shouldRejectUser() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .build();

        // When
        Instant reviewTime = Instant.now();
        String reason = "Failed background check";
        user.reject(adminUser, reviewTime, reason);

        // Then
        assertTrue(user.getApprovalState().isRejected());
        assertEquals(UserStatus.INACTIVE, user.getStatus());
    }

    @Test
    void reject_withNullReviewer_shouldThrowException() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .build();

        // When/Then
        assertThrows(NullPointerException.class, () -> user.reject(null, Instant.now(), "reason"));
    }

    @Test
    void reject_withNullReviewedAt_shouldThrowException() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .build();

        // When/Then
        assertThrows(NullPointerException.class, () -> user.reject(adminUser, null, "reason"));
    }

    @Test
    void addRole_withValidRole_shouldAddRole() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .build();

        // When
        user.addRole(UserRole.PRODUCTION);

        // Then
        assertTrue(user.getRoles().contains(UserRole.PRODUCTION));
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void addRole_withNullRole_shouldThrowException() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .build();

        // When/Then
        assertThrows(NullPointerException.class, () -> user.addRole(null));
    }

    @Test
    void addRole_withDuplicateAdminRole_shouldThrowException() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .addRole(UserRole.ADMIN)
                .build();

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> user.addRole(UserRole.ADMIN));
    }

    @Test
    void removeRole_withExistingRole_shouldRemoveRole() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .addRole(UserRole.PRODUCTION)
                .build();

        // When
        user.removeRole(UserRole.PRODUCTION);

        // Then
        assertFalse(user.getRoles().contains(UserRole.PRODUCTION));
        assertEquals(0, user.getRoles().size());
    }

    @Test
    void removeRole_withAdminRole_shouldThrowException() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .addRole(UserRole.ADMIN)
                .build();

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> user.removeRole(UserRole.ADMIN));
    }

    @Test
    void removeRole_withNonExistingRole_shouldThrowException() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .build();

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> user.removeRole(UserRole.PRODUCTION));
    }

    @Test
    void getStatus_withPendingApprovalState_shouldReturnPendingStatus() {
        // Given
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .build();

        // When/Then
        assertEquals(UserStatus.PENDING, user.getStatus());
    }

    @Test
    void getStatus_withApprovedApprovalState_shouldReturnActiveStatus() {
        // Given
        ApprovalState approvedState = ApprovalState.createApprovedState(adminUser, Instant.now());
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .approvalState(approvedState)
                .build();

        // When/Then
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    void getStatus_withRejectedApprovalState_shouldReturnInactiveStatus() {
        // Given
        ApprovalState rejectedState = ApprovalState.createRejectedState("Failed background check");
        UserBusiness user = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .password(password)
                .email(email)
                .approvalState(rejectedState)
                .build();

        // When/Then
        assertEquals(UserStatus.INACTIVE, user.getStatus());
    }
}
