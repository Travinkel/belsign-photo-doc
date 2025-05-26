package com.belman.presentation.usecases.admin.settings;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.session.SessionContext;
import com.belman.domain.security.AuthenticationService;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.base.LogoutCapable;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.admin.dashboard.AdminDashboardView;
import com.belman.presentation.usecases.login.LoginView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * ViewModel for the system settings screen.
 * Provides data and operations for system-wide settings configuration.
 */
public class SystemSettingsViewModel extends BaseViewModel<SystemSettingsViewModel> implements LogoutCapable {
    private final AuthenticationService authenticationService = ServiceLocator.getService(AuthenticationService.class);
    private final StringProperty welcomeMessage = new SimpleStringProperty("System Settings");
    private final StringProperty errorMessage = new SimpleStringProperty("");

    /**
     * Default constructor for use by the ViewLoader.
     */
    public SystemSettingsViewModel() {
        // Default constructor
    }

    @Override
    public void onShow() {
        // Update welcome message with user name if available
        SessionContext.getCurrentUser().ifPresent(user -> {
            welcomeMessage.set("System Settings - " + user.getUsername().value());
        });
    }

    /**
     * Gets the welcome message property.
     *
     * @return the welcome message property
     */
    public StringProperty welcomeMessageProperty() {
        return welcomeMessage;
    }

    /**
     * Gets the error message property.
     *
     * @return the error message property
     */
    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    /**
     * Logs out the current user and navigates to the login view.
     */
    @Override
    public void logout() {
        try {
            // Log out the user
            authenticationService.logout();

            // Clear the session context
            SessionContext.clear();

            // Navigate to the login view
            // Router.navigateTo(LoginView.class); // Commented out for now
        } catch (Exception e) {
            // Log the full error for debugging
            System.err.println("Error logging out: " + e.getMessage());
            e.printStackTrace();

            // Set a user-friendly error message
            errorMessage.set("Unable to log out properly. Please close the application and restart it to ensure you are fully logged out.");

            // Even if logout fails, try to navigate to login screen anyway
            try {
                // Navigate to the login view
                // Router.navigateTo(LoginView.class); // Commented out for now
            } catch (Exception navEx) {
                System.err.println("Failed to navigate to login view after logout error: " + navEx.getMessage());
            }
        }
    }

    /**
     * Navigates back to the admin dashboard.
     */
    public void navigateBack() {
        try {
            Router.navigateTo(com.belman.presentation.usecases.admin.dashboard.AdminDashboardView.class);
        } catch (Exception e) {
            errorMessage.set("Error navigating back: " + e.getMessage());
        }
    }

    /**
     * Saves the system settings.
     * This is a placeholder implementation that shows a message.
     */
    public void saveSettings() {
        try {
            // In a real implementation, this would save the settings to a configuration file or database
            welcomeMessage.set("Settings saved successfully.");
        } catch (Exception e) {
            errorMessage.set("Error saving settings: " + e.getMessage());
        }
    }
}
