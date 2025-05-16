package com.belman.ui.navigation;

import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRole;
import com.belman.ui.session.SessionContext;
import com.belman.ui.usecases.admin.usermanagement.UserManagementView;
import com.belman.ui.usecases.authentication.login.LoginView;
import com.belman.ui.usecases.photo.upload.PhotoUploadView;
import com.belman.ui.usecases.qa.dashboard.QADashboardView;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service for role-based navigation in the application.
 * This class encapsulates the logic for navigating to different views based on user roles.
 */
public class RoleBasedNavigationService {
    private final SessionContext sessionContext;
    private final Logger logger;

    /**
     * Creates a new RoleBasedNavigationService with the specified session context.
     *
     * @param sessionContext the session context
     */
    public RoleBasedNavigationService(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
        this.logger = Logger.getLogger(RoleBasedNavigationService.class.getName());
    }

    /**
     * Navigates to the appropriate home view based on the user's role.
     * If the user has just one role, navigates directly to the role-specific view.
     * If the user has multiple roles, navigates to the main view for role selection.
     * If no user is logged in, navigates to the login view.
     */
    public void navigateToUserHome() {
        Optional<UserBusiness> userOpt = sessionContext.getUser();
        if (userOpt.isPresent()) {
            UserBusiness user = userOpt.get();

            // Count the number of roles the user has
            int roleCount = user.getRoles().size();

            if (roleCount == 0) {
                // Fallback if no specific role is found
                logger.warning("No specific role found for user, using default view");
                Router.navigateTo(PhotoUploadView.class);
                logger.fine("Navigation to default view completed");
            } else if (roleCount == 1) {
                // If user has exactly one role, navigate directly to the role-specific view
                UserRole role = user.getRoles().iterator().next();
                navigateToRoleSpecificView(role);
            } else {
                // If user has multiple roles, navigate to the main view for role selection
                logger.fine("User has multiple roles, navigating to MainView for role selection");
                Router.navigateTo(com.belman.ui.usecases.common.main.MainView.class);
                logger.fine("Navigation to MainView completed");
            }
        } else {
            // If no user is logged in, navigate to login view
            navigateToLogin();
        }
    }

    /**
     * Navigates to the appropriate view based on the specified role.
     *
     * @param role the user role
     */
    public void navigateToRoleSpecificView(UserRole role) {
        switch (role) {
            case ADMIN:
                logger.fine("Navigating to UserManagementView for ADMIN user");
                Router.navigateTo(UserManagementView.class);
                logger.fine("Navigation to UserManagementView completed");
                break;
            case QA:
                logger.fine("Navigating to QADashboardView for QA user");
                Router.navigateTo(QADashboardView.class);
                logger.fine("Navigation to QADashboardView completed");
                break;
            case PRODUCTION:
                logger.fine("Navigating to PhotoUploadView for PRODUCTION user");
                Router.navigateTo(PhotoUploadView.class);
                logger.fine("Navigation to PhotoUploadView completed");
                break;
            default:
                // Fallback if no specific role is found
                logger.warning("Unknown role: " + role + ", using default view");
                Router.navigateTo(PhotoUploadView.class);
                logger.fine("Navigation to default view completed");
                break;
        }
    }

    /**
     * Navigates to the login view.
     */
    public void navigateToLogin() {
        logger.fine("Navigating to login view");
        Router.navigateTo(LoginView.class);
        logger.fine("Navigation to login view completed");
    }

    /**
     * Navigates to the user management view.
     * This is typically used for administrative tasks.
     */
    public void navigateToUserManagement() {
        logger.fine("Navigating to user management view");
        Router.navigateTo(UserManagementView.class);
        logger.fine("Navigation to user management view completed");
    }

    /**
     * Navigates to the QA dashboard view.
     * This is typically used by quality assurance personnel.
     */
    public void navigateToQADashboard() {
        logger.fine("Navigating to QA dashboard view");
        Router.navigateTo(QADashboardView.class);
        logger.fine("Navigation to QA dashboard view completed");
    }

    /**
     * Navigates to the photo upload view.
     * This is typically used by production workers.
     */
    public void navigateToPhotoUpload() {
        logger.fine("Navigating to photo upload view");
        Router.navigateTo(PhotoUploadView.class);
        logger.fine("Navigation to photo upload view completed");
    }
}
