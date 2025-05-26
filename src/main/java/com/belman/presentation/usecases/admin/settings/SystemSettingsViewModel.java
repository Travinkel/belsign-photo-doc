package com.belman.presentation.usecases.admin.settings;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.user.UserBusiness;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.admin.dashboard.AdminDashboardView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * ViewModel for the system settings view.
 * Provides data and operations for system settings functionality.
 */
public class SystemSettingsViewModel extends BaseViewModel<SystemSettingsViewModel> {
    private final AuthenticationService authenticationService = ServiceLocator.getService(AuthenticationService.class);
    private final StringProperty welcomeMessage = new SimpleStringProperty("System Settings");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    
    @Inject
    private SessionContext sessionContext;
    
    /**
     * Default constructor for use by the ViewLoader.
     */
    public SystemSettingsViewModel() {
        // Default constructor
    }
    
    @Override
    public void onShow() {
        // Update welcome message with user name if available
        sessionContext.getUser().ifPresent(user -> {
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
     * Navigates back to the admin dashboard.
     */
    public void navigateBack() {
        try {
            Router.navigateTo(AdminDashboardView.class);
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