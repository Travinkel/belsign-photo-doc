package com.belman.unit.be.rbac;

import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.exceptions.AccessDeniedException;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserRole;
import com.belman.domain.user.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the role-based access control components.
 */
public class RoleBasedAccessControlTest {

    @Mock
    private AuthenticationService authenticationService;

    private AccessPolicyFactory accessPolicyFactory;
    private RoleBasedAccessControlFactory rbacFactory;
    private UserBusiness adminUser;
    private UserBusiness qaUser;
    private UserBusiness productionUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create the factories
        accessPolicyFactory = new AccessPolicyFactory();
        rbacFactory = new RoleBasedAccessControlFactory(authenticationService, accessPolicyFactory);

        // Create test users with different roles
        adminUser = new UserBusiness.Builder()
                .id(UserId.newId())
                .username(new Username("admin"))
                .password(new HashedPassword("admin-password"))
                .email(new EmailAddress("admin@example.com"))
                .addRole(UserRole.ADMIN)
                .build();

        qaUser = new UserBusiness.Builder()
                .id(UserId.newId())
                .username(new Username("qa"))
                .password(new HashedPassword("qa-password"))
                .email(new EmailAddress("qa@example.com"))
                .addRole(UserRole.QA)
                .build();

        productionUser = new UserBusiness.Builder()
                .id(UserId.newId())
                .username(new Username("production"))
                .password(new HashedPassword("production-password"))
                .email(new EmailAddress("production@example.com"))
                .addRole(UserRole.PRODUCTION)
                .build();
    }

    @Test
    @DisplayName("AccessPolicy should grant access to users with allowed roles")
    void accessPolicy_withAllowedRole_shouldGrantAccess() {
        // Arrange
        AccessPolicy adminPolicy = accessPolicyFactory.createAdminOnlyPolicy();
        AccessPolicy qaPolicy = accessPolicyFactory.createQAOnlyPolicy();
        AccessPolicy productionPolicy = accessPolicyFactory.createProductionOnlyPolicy();

        // Act & Assert
        assertTrue(adminPolicy.hasAccess(adminUser), "Admin user should have access with admin policy");
        assertFalse(adminPolicy.hasAccess(qaUser), "QA user should not have access with admin policy");
        assertFalse(adminPolicy.hasAccess(productionUser), "Production user should not have access with admin policy");

        assertTrue(qaPolicy.hasAccess(qaUser), "QA user should have access with QA policy");
        assertFalse(qaPolicy.hasAccess(adminUser), "Admin user should not have access with QA policy");
        assertFalse(qaPolicy.hasAccess(productionUser), "Production user should not have access with QA policy");

        assertTrue(productionPolicy.hasAccess(productionUser),
                "Production user should have access with production policy");
        assertFalse(productionPolicy.hasAccess(adminUser), "Admin user should not have access with production policy");
        assertFalse(productionPolicy.hasAccess(qaUser), "QA user should not have access with production policy");
    }

    @Test
    @DisplayName("AccessPolicy should grant access to users with any of the allowed roles")
    void accessPolicy_withMultipleAllowedRoles_shouldGrantAccess() {
        // Arrange
        AccessPolicy qaAndAdminPolicy = accessPolicyFactory.createQAAndAdminPolicy();
        AccessPolicy productionAndQAPolicy = accessPolicyFactory.createProductionAndQAPolicy();
        AccessPolicy allRolesPolicy = accessPolicyFactory.createAllRolesPolicy();

        // Act & Assert
        assertTrue(qaAndAdminPolicy.hasAccess(adminUser), "Admin user should have access with QA and admin policy");
        assertTrue(qaAndAdminPolicy.hasAccess(qaUser), "QA user should have access with QA and admin policy");
        assertFalse(qaAndAdminPolicy.hasAccess(productionUser),
                "Production user should not have access with QA and admin policy");

        assertTrue(productionAndQAPolicy.hasAccess(productionUser),
                "Production user should have access with production and QA policy");
        assertTrue(productionAndQAPolicy.hasAccess(qaUser), "QA user should have access with production and QA policy");
        assertFalse(productionAndQAPolicy.hasAccess(adminUser),
                "Admin user should not have access with production and QA policy");

        assertTrue(allRolesPolicy.hasAccess(adminUser), "Admin user should have access with all roles policy");
        assertTrue(allRolesPolicy.hasAccess(qaUser), "QA user should have access with all roles policy");
        assertTrue(allRolesPolicy.hasAccess(productionUser),
                "Production user should have access with all roles policy");
    }

    @Test
    @DisplayName("RoleBasedAccessController should check access for current user")
    void roleBasedAccessController_withCurrentUser_shouldCheckAccess() {
        // Arrange
        RoleBasedAccessController adminController = rbacFactory.createAdminAccessController();
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(adminUser));

        // Act & Assert
        assertTrue(adminController.hasAccess(), "Admin controller should grant access to admin user");

        // Change current user to QA
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(qaUser));
        assertFalse(adminController.hasAccess(), "Admin controller should deny access to QA user");

        // Change current user to production
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(productionUser));
        assertFalse(adminController.hasAccess(), "Admin controller should deny access to production user");
    }

    @Test
    @DisplayName("RoleBasedAccessController should throw exception when access is denied")
    void roleBasedAccessController_withDeniedAccess_shouldThrowException() {
        // Arrange
        RoleBasedAccessController adminController = rbacFactory.createAdminAccessController();
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(qaUser));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> adminController.checkAccess(),
                "checkAccess should throw AccessDeniedException when access is denied");

        assertThrows(AccessDeniedException.class, () -> adminController.checkAccess(qaUser),
                "checkAccess(user) should throw AccessDeniedException when access is denied");
    }

    @Test
    @DisplayName("RoleBasedAccessControlFactory should create controllers with correct policies")
    void roleBasedAccessControlFactory_shouldCreateControllersWithCorrectPolicies() {
        // Arrange & Act
        RoleBasedAccessController adminController = rbacFactory.createAdminAccessController();
        RoleBasedAccessController qaController = rbacFactory.createQAAccessController();
        RoleBasedAccessController productionController = rbacFactory.createProductionAccessController();

        // Assert
        assertTrue(adminController.hasAccess(adminUser), "Admin controller should grant access to admin user");
        assertFalse(adminController.hasAccess(qaUser), "Admin controller should deny access to QA user");
        assertFalse(adminController.hasAccess(productionUser),
                "Admin controller should deny access to production user");

        assertTrue(qaController.hasAccess(qaUser), "QA controller should grant access to QA user");
        assertFalse(qaController.hasAccess(adminUser), "QA controller should deny access to admin user");
        assertFalse(qaController.hasAccess(productionUser), "QA controller should deny access to production user");

        assertTrue(productionController.hasAccess(productionUser),
                "Production controller should grant access to production user");
        assertFalse(productionController.hasAccess(adminUser),
                "Production controller should deny access to admin user");
        assertFalse(productionController.hasAccess(qaUser), "Production controller should deny access to QA user");
    }
}
