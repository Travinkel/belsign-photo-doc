package com.belman.unit.presentation.usecases.admin.dashboard;

import com.belman.common.session.SessionContext;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.user.UserRepository;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.admin.dashboard.AdminDashboardViewModel;
import com.belman.presentation.usecases.admin.usermanagement.UserManagementView;
import com.belman.presentation.usecases.login.LoginView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AdminDashboardViewModel class.
 * These tests verify that the ViewModel correctly updates the welcome message,
 * handles navigation, and manages error messages.
 */
@ExtendWith(MockitoExtension.class)
public class AdminDashboardViewModelTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionContext sessionContext;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AdminDashboardViewModel viewModel;

    @BeforeEach
    public void setUp() {
        // No setup needed
    }

    @Test
    public void testOnShow_DoesNotUpdateWelcomeMessage_WhenUserIsNotPresent() {
        // Arrange
        when(sessionContext.getUser()).thenReturn(Optional.empty());
        String originalMessage = viewModel.welcomeMessageProperty().get();

        // Act
        viewModel.onShow();

        // Assert
        assertEquals(originalMessage, viewModel.welcomeMessageProperty().get());
    }

    @Test
    public void testNavigateToUserManagement_NavigatesToUserManagementView() {
        // Arrange
        try (MockedStatic<Router> mockedRouter = mockStatic(Router.class)) {
            // Act
            viewModel.navigateToUserManagement();

            // Assert
            mockedRouter.verify(() -> Router.navigateTo(UserManagementView.class));
            assertEquals("", viewModel.errorMessageProperty().get());
        }
    }

    @Test
    public void testNavigateToUserManagement_SetsErrorMessage_WhenNavigationFails() {
        // Arrange
        try (MockedStatic<Router> mockedRouter = mockStatic(Router.class)) {
            mockedRouter.when(() -> Router.navigateTo(UserManagementView.class))
                    .thenThrow(new RuntimeException("Navigation error"));

            // Act
            viewModel.navigateToUserManagement();

            // Assert
            assertEquals("Error navigating to user management: Navigation error", 
                    viewModel.errorMessageProperty().get());
        }
    }

    @Test
    public void testLogout_NavigatesToLogin() {
        // Arrange
        try (MockedStatic<Router> mockedRouter = mockStatic(Router.class);
             MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {

            // Act
            viewModel.logout();

            // Assert
            verify(authenticationService).logout();
            mockedSessionContext.verify(() -> SessionContext.clear());
            mockedRouter.verify(() -> Router.navigateTo(LoginView.class));
            assertEquals("", viewModel.errorMessageProperty().get());
        }
    }

    @Test
    public void testLogout_SetsErrorMessage_WhenLogoutFails() {
        // Arrange
        doThrow(new RuntimeException("Logout error")).when(authenticationService).logout();

        try (MockedStatic<Router> mockedRouter = mockStatic(Router.class)) {
            // Act
            viewModel.logout();

            // Assert
            assertEquals("Unable to log out properly. Please close the application and restart it to ensure you are fully logged out.", 
                    viewModel.errorMessageProperty().get());
        }
    }

    @Test
    public void testLogout_HandlesNullAuthenticationService() {
        // Arrange
        // Create a new ViewModel with null AuthenticationService
        AdminDashboardViewModel viewModelWithNullAuth = new AdminDashboardViewModel();

        // Use reflection to set the userRepository field
        try {
            Field field = AdminDashboardViewModel.class.getDeclaredField("userRepository");
            field.setAccessible(true);
            field.set(viewModelWithNullAuth, userRepository);

            // Set sessionContext
            Field sessionField = AdminDashboardViewModel.class.getDeclaredField("sessionContext");
            sessionField.setAccessible(true);
            sessionField.set(viewModelWithNullAuth, sessionContext);

            // Leave authenticationService as null
        } catch (Exception e) {
            // Ignore
        }

        try (MockedStatic<Router> mockedRouter = mockStatic(Router.class)) {
            // Act
            viewModelWithNullAuth.logout();

            // Assert
            // Should handle the null authenticationService gracefully
            assertEquals("Unable to log out properly. Please close the application and restart it to ensure you are fully logged out.", 
                    viewModelWithNullAuth.errorMessageProperty().get());
        }
    }
}
