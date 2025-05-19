package com.belman.presentation.usecases.admin.dashboard;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRepository;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.admin.usermanagement.UserManagementView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * ViewModel for the Admin dashboard view.
 * Provides data and operations for admin-specific functionality.
 */
public class AdminDashboardViewModel extends BaseViewModel<AdminDashboardViewModel> {
    private final StringProperty welcomeMessage = new SimpleStringProperty("Welcome to Admin Dashboard");
    private final StringProperty errorMessage = new SimpleStringProperty("");

    @Inject
    private UserRepository userRepository;

    @Inject
    private SessionContext sessionContext;

    /**
     * Default constructor for use by the ViewLoader.
     */
    public AdminDashboardViewModel() {
        // Default constructor
    }

    @Override
    public void onShow() {
        // Update welcome message with user name if available
        sessionContext.getUser().ifPresent(user -> {
            welcomeMessage.set("Welcome, " + user.getUsername().value() + "!");
        });
    }

    /**
     * Navigates to the user management view.
     */
    public void navigateToUserManagement() {
        try {
            // Navigate to the user management view
            Router.navigateTo(UserManagementView.class);
        } catch (Exception e) {
            errorMessage.set("Error navigating to user management: " + e.getMessage());
        }
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
    public void logout() {
        try {
            // Log out the user
            if (sessionContext != null) {
                sessionContext.navigateToLogin();
            }
        } catch (Exception e) {
            errorMessage.set("Error logging out: " + e.getMessage());
        }
    }
}