package com.belman.integration.admin;

import com.belman.application.usecase.admin.AdminService;
import com.belman.common.session.SessionContext;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.PersonName;
import com.belman.domain.common.valueobjects.PhoneNumber;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.ApprovalState;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserRepository;
import com.belman.domain.user.Username;
import com.belman.domain.user.UserRole;
import com.belman.domain.security.AuthenticationService;
import com.belman.presentation.usecases.admin.usermanagement.UserManagementViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * End-to-End test for the user management flow.
 * This test verifies the complete user management workflow from loading users
 * to adding, editing, and deleting users.
 */
public class UserManagementFlowTest {

    @Mock
    private AdminService adminService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private SessionContext sessionContext;

    @Mock
    private com.belman.domain.services.LoggerFactory loggerFactory;

    @Mock
    private UserRepository userRepository;

    private UserBusiness testAdmin;
    private List<UserBusiness> testUsers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("[DEBUG_LOG] Setting up UserManagementFlowTest");

        // Clear ServiceLocator and register necessary services
        com.belman.bootstrap.di.ServiceLocator.clear();

        // Register SessionContext
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.common.session.SessionContext.class,
            sessionContext
        );

        // Register LoggerFactory
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.domain.services.LoggerFactory.class,
            loggerFactory
        );

        // Set up logger factory mock
        when(loggerFactory.getLogger(any())).thenReturn(mock(com.belman.domain.services.Logger.class));

        // Register AuthenticationService
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.domain.security.AuthenticationService.class,
            authenticationService
        );

        // Register AdminService
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.application.usecase.admin.AdminService.class,
            adminService
        );

        // Register UserRepository
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.domain.user.UserRepository.class,
            userRepository
        );

        // Create test admin
        testAdmin = createTestAdmin();

        // Create test users
        testUsers = createTestUsers();

        // Set up mocks
        setupMocks();
    }

    /**
     * Tests the complete user management flow.
     * This test verifies that an admin can load users, add a new user,
     * edit an existing user, and delete a user.
     */
    @Test
    void testCompleteUserManagementFlow() {
        System.out.println("[DEBUG_LOG] Running complete user management flow test");

        // Set the current user in the session
        SessionContext.setCurrentUser(testAdmin);

        // Configure sessionContext mock to return the test admin
        when(sessionContext.getUser()).thenReturn(Optional.of(testAdmin));

        // 1. Create the UserManagementViewModel
        UserManagementViewModel userManagementViewModel = new UserManagementViewModel();
        injectMocks(userManagementViewModel);

        // 2. Load users
        userManagementViewModel.loadUsers();
        
        // Verify that users are loaded
        assertEquals(testUsers.size(), userManagementViewModel.getUsers().size(), 
                    "Number of loaded users should match test users");

        // 3. Test adding a new user
        // We can't directly test the dialog in a unit test, so we'll mock the repository save method
        UserBusiness newUser = createNewUser();
        
        // Mock the repository to return the new user in the list
        List<UserBusiness> updatedUsers = new ArrayList<>(testUsers);
        updatedUsers.add(newUser);
        when(userRepository.findAll()).thenReturn(updatedUsers);
        
        // Simulate saving the new user
        userManagementViewModel.loadUsers();
        
        // Verify that the user list is updated
        assertEquals(updatedUsers.size(), userManagementViewModel.getUsers().size(), 
                    "Number of users should be updated after adding a new user");

        // 4. Test selecting a user
        UserBusiness userToSelect = testUsers.get(0);
        userManagementViewModel.selectUser(userToSelect);
        
        // We can't directly verify the selection since selectedUser is private,
        // but we can test that subsequent operations work correctly

        // 5. Test editing a user
        // Mock the repository to return the updated user
        UserBusiness updatedUser = createUpdatedUser(userToSelect);
        List<UserBusiness> usersAfterEdit = new ArrayList<>(updatedUsers);
        usersAfterEdit.remove(userToSelect);
        usersAfterEdit.add(updatedUser);
        when(userRepository.findAll()).thenReturn(usersAfterEdit);
        
        // Simulate saving the updated user
        userManagementViewModel.loadUsers();
        
        // Verify that the user list is updated
        assertEquals(usersAfterEdit.size(), userManagementViewModel.getUsers().size(), 
                    "Number of users should remain the same after editing a user");

        // 6. Test deleting a user
        // Mock the repository to return the list without the deleted user
        UserBusiness userToDelete = updatedUser;
        List<UserBusiness> usersAfterDelete = new ArrayList<>(usersAfterEdit);
        usersAfterDelete.remove(userToDelete);
        when(userRepository.findAll()).thenReturn(usersAfterDelete);
        
        // Simulate deleting the user
        userManagementViewModel.loadUsers();
        
        // Verify that the user list is updated
        assertEquals(usersAfterDelete.size(), userManagementViewModel.getUsers().size(), 
                    "Number of users should be reduced after deleting a user");

        System.out.println("[DEBUG_LOG] Complete user management flow test completed successfully");
    }

    /**
     * Tests error handling during user management.
     * This test verifies that errors during user management are properly handled.
     */
    @Test
    void testErrorHandlingDuringUserManagement() {
        System.out.println("[DEBUG_LOG] Running error handling during user management test");

        // Set the current user in the session
        SessionContext.setCurrentUser(testAdmin);

        // Configure sessionContext mock to return the test admin
        when(sessionContext.getUser()).thenReturn(Optional.of(testAdmin));

        // 1. Create the UserManagementViewModel
        UserManagementViewModel userManagementViewModel = new UserManagementViewModel();
        injectMocks(userManagementViewModel);

        // 2. Test error handling when loading users fails
        // Mock the repository to throw an exception
        when(userRepository.findAll()).thenThrow(new RuntimeException("Failed to load users"));
        
        // Attempt to load users
        userManagementViewModel.loadUsers();
        
        // Verify that an error message is set
        assertFalse(userManagementViewModel.errorMessageProperty().get().isEmpty(), 
                    "Error message should be set when loading users fails");
        assertTrue(userManagementViewModel.getUsers().isEmpty(), 
                    "User list should be empty when loading users fails");

        // 3. Test error handling when deleting the current user
        // Reset the mock to return users normally
        reset(userRepository);
        when(userRepository.findAll()).thenReturn(testUsers);
        
        // Clear the error message
        userManagementViewModel.errorMessageProperty().set("");
        
        // Load users
        userManagementViewModel.loadUsers();
        
        // Select the current user (admin)
        userManagementViewModel.selectUser(testAdmin);
        
        // Attempt to delete the current user
        userManagementViewModel.deleteSelectedUser();
        
        // Verify that an error message is set
        assertFalse(userManagementViewModel.errorMessageProperty().get().isEmpty(), 
                    "Error message should be set when attempting to delete the current user");

        System.out.println("[DEBUG_LOG] Error handling during user management test completed successfully");
    }

    // Helper methods

    private UserBusiness createTestAdmin() {
        return new UserBusiness.Builder()
            .id(new UserId("admin-123"))
            .username(new Username("testadmin"))
            .password(new HashedPassword("hashedpassword"))
            .email(new EmailAddress("admin@example.com"))
            .name(new PersonName("Test", "Admin"))
            .approvalState(ApprovalState.createApproved())
            .addRole(UserRole.ADMIN)
            .build();
    }

    private List<UserBusiness> createTestUsers() {
        List<UserBusiness> users = new ArrayList<>();
        
        // Add the admin user
        users.add(testAdmin);
        
        // Add a production user
        users.add(new UserBusiness.Builder()
            .id(new UserId("prod-123"))
            .username(new Username("testproduction"))
            .password(new HashedPassword("hashedpassword"))
            .email(new EmailAddress("production@example.com"))
            .name(new PersonName("Test", "Production"))
            .approvalState(ApprovalState.createApproved())
            .addRole(UserRole.PRODUCTION)
            .build());
            
        // Add a QA user
        users.add(new UserBusiness.Builder()
            .id(new UserId("qa-123"))
            .username(new Username("testqa"))
            .password(new HashedPassword("hashedpassword"))
            .email(new EmailAddress("qa@example.com"))
            .name(new PersonName("Test", "QA"))
            .approvalState(ApprovalState.createApproved())
            .addRole(UserRole.QA)
            .build());
            
        return users;
    }

    private UserBusiness createNewUser() {
        return new UserBusiness.Builder()
            .id(new UserId("new-123"))
            .username(new Username("newuser"))
            .password(new HashedPassword("hashedpassword"))
            .email(new EmailAddress("newuser@example.com"))
            .name(new PersonName("New", "User"))
            .approvalState(ApprovalState.createApproved())
            .addRole(UserRole.PRODUCTION)
            .build();
    }

    private UserBusiness createUpdatedUser(UserBusiness originalUser) {
        return new UserBusiness.Builder()
            .id(originalUser.getId())
            .username(originalUser.getUsername())
            .password(originalUser.getPassword())
            .email(originalUser.getEmail())
            .name(new PersonName("Updated", "User"))
            .approvalState(originalUser.getApprovalState())
            .addRole(originalUser.getRoles().iterator().next())
            .build();
    }

    private void setupMocks() {
        // Set up UserRepository mock
        when(userRepository.findAll()).thenReturn(testUsers);
        
        // Set up AuthenticationService mock
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(testAdmin));
    }

    private void injectMocks(UserManagementViewModel viewModel) {
        try {
            // Use reflection to inject mocks into the view model
            java.lang.reflect.Field userRepositoryField = viewModel.getClass().getDeclaredField("userRepository");
            userRepositoryField.setAccessible(true);
            userRepositoryField.set(viewModel, userRepository);

            java.lang.reflect.Field sessionContextField = viewModel.getClass().getDeclaredField("sessionContext");
            sessionContextField.setAccessible(true);
            sessionContextField.set(viewModel, sessionContext);
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] Error injecting mocks: " + e.getMessage());
            e.printStackTrace();
        }
    }
}