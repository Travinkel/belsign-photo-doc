package com.belman.presentation.navigation;

import com.belman.common.logging.AuthLoggingService;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRole;
import com.belman.common.session.SessionContext;
import com.belman.presentation.usecases.admin.dashboard.AdminDashboardView;
import com.belman.presentation.usecases.admin.usermanagement.UserManagementView;
import com.belman.presentation.usecases.login.LoginView;
import com.belman.presentation.usecases.qa.dashboard.QADashboardView;
import com.belman.presentation.usecases.worker.assignedorder.AssignedOrderView;

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
     * @throws IllegalArgumentException if sessionContext is null
     */
    public RoleBasedNavigationService(SessionContext sessionContext) {
        if (sessionContext == null) {
            throw new IllegalArgumentException("SessionContext cannot be null");
        }
        this.sessionContext = sessionContext;
        this.logger = Logger.getLogger(RoleBasedNavigationService.class.getName());
    }

    /**
     * Navigates to the appropriate home view based on the user's role.
     * If the user has one or more roles, navigates directly to the role-specific view.
     * For users with multiple roles, prioritizes in order: ADMIN, QA, PRODUCTION.
     * If no user is logged in, navigates to the login view.
     */
    public void navigateToUserHome() {
        AuthLoggingService.logNavigation("RoleBasedNavigationService", "Navigating to user home");

        // Defensive check for null sessionContext
        if (sessionContext == null) {
            AuthLoggingService.logError("RoleBasedNavigationService", "SessionContext is null, cannot navigate to user home");
            logger.severe("SessionContext is null, cannot navigate to user home");
            // Navigate to login view as a fallback
            Router.navigateTo(LoginView.class);
            return;
        }

        Optional<UserBusiness> userOpt = sessionContext.getUser();
        AuthLoggingService.logNavigation("RoleBasedNavigationService", "User from session context: " + (userOpt.isPresent() ? "present" : "not present"));

        if (userOpt.isPresent()) {
            UserBusiness user = userOpt.get();
            AuthLoggingService.logNavigation("RoleBasedNavigationService", "User found: " + user.getUsername().value() + ", ID: " + user.getId().id() + ", Roles: " + user.getRoles());

            // Get the user's roles
            var roles = user.getRoles();
            AuthLoggingService.logNavigation("RoleBasedNavigationService", "User roles: " + roles);

            if (roles.isEmpty()) {
                // Fallback if no specific role is found
                logger.warning("No specific role found for user, using default view");
                AuthLoggingService.logNavigation("RoleBasedNavigationService", "No specific role found for user, using default view (AssignedOrderView)");
                Router.navigateTo(AssignedOrderView.class);
                logger.fine("Navigation to default view completed");
                AuthLoggingService.logNavigation("RoleBasedNavigationService", "Navigation to default view completed");
            } else {
                // Select the highest priority role
                UserRole selectedRole;

                if (roles.contains(UserRole.ADMIN)) {
                    selectedRole = UserRole.ADMIN;
                    logger.fine("User has multiple roles, selecting ADMIN role");
                    AuthLoggingService.logNavigation("RoleBasedNavigationService", "User has multiple roles, selecting ADMIN role");
                } else if (roles.contains(UserRole.QA)) {
                    selectedRole = UserRole.QA;
                    logger.fine("User has multiple roles, selecting QA role");
                    AuthLoggingService.logNavigation("RoleBasedNavigationService", "User has multiple roles, selecting QA role");
                } else {
                    selectedRole = UserRole.PRODUCTION;
                    logger.fine("User has multiple roles, selecting PRODUCTION role");
                    AuthLoggingService.logNavigation("RoleBasedNavigationService", "User has multiple roles, selecting PRODUCTION role");
                }

                // Navigate to the view for the selected role
                AuthLoggingService.logNavigation("RoleBasedNavigationService", "Navigating to role-specific view for role: " + selectedRole);
                navigateToRoleSpecificView(selectedRole);
            }
        } else {
            // If no user is logged in, navigate to login view
            AuthLoggingService.logNavigation("RoleBasedNavigationService", "No user is logged in, navigating to login view");
            navigateToLogin();
        }
    }

    /**
     * Navigates to the appropriate view based on the specified role.
     *
     * @param role the user role
     */
    public void navigateToRoleSpecificView(UserRole role) {
        AuthLoggingService.logNavigation("RoleBasedNavigationService", "Navigating to role-specific view for role: " + role);

        switch (role) {
            case ADMIN:
                logger.fine("Navigating to AdminDashboardView for ADMIN user");
                AuthLoggingService.logNavigation("RoleBasedNavigationService", "Navigating to AdminDashboardView for ADMIN user");
                try {
                    Router.navigateTo(com.belman.presentation.usecases.admin.dashboard.AdminDashboardView.class);
                    logger.fine("Navigation to AdminDashboardView completed");
                    AuthLoggingService.logNavigation("RoleBasedNavigationService", "Navigation to AdminDashboardView completed successfully");
                } catch (Exception e) {
                    AuthLoggingService.logError("RoleBasedNavigationService", "Error navigating to AdminDashboardView: " + e.getMessage());
                }
                break;
            case QA:
                logger.fine("Navigating to QADashboardView for QA user");
                AuthLoggingService.logNavigation("RoleBasedNavigationService", "Navigating to QADashboardView for QA user");
                try {
                    Router.navigateTo(QADashboardView.class);
                    logger.fine("Navigation to QADashboardView completed");
                    AuthLoggingService.logNavigation("RoleBasedNavigationService", "Navigation to QADashboardView completed successfully");
                } catch (Exception e) {
                    AuthLoggingService.logError("RoleBasedNavigationService", "Error navigating to QADashboardView: " + e.getMessage());
                }
                break;
            case PRODUCTION:
                logger.fine("Navigating to AssignedOrderView for PRODUCTION user");
                AuthLoggingService.logNavigation("RoleBasedNavigationService", "Navigating to AssignedOrderView for PRODUCTION user");
                try {
                    Router.navigateTo(AssignedOrderView.class);
                    logger.fine("Navigation to AssignedOrderView completed");
                    AuthLoggingService.logNavigation("RoleBasedNavigationService", "Navigation to AssignedOrderView completed successfully");
                } catch (Exception e) {
                    AuthLoggingService.logError("RoleBasedNavigationService", "Error navigating to AssignedOrderView: " + e.getMessage());
                }
                break;
            default:
                // Fallback if no specific role is found
                logger.warning("Unknown role: " + role + ", using default view");
                AuthLoggingService.logNavigation("RoleBasedNavigationService", "Unknown role: " + role + ", using default view (AssignedOrderView)");
                try {
                    Router.navigateTo(AssignedOrderView.class);
                    logger.fine("Navigation to default view completed");
                    AuthLoggingService.logNavigation("RoleBasedNavigationService", "Navigation to default view completed successfully");
                } catch (Exception e) {
                    AuthLoggingService.logError("RoleBasedNavigationService", "Error navigating to default view: " + e.getMessage());
                }
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
     * Navigates to the assigned order view.
     * This is typically used by production workers.
     */
    public void navigateToAssignedOrder() {
        logger.fine("Navigating to assigned order view");
        Router.navigateTo(AssignedOrderView.class);
        logger.fine("Navigation to assigned order view completed");
    }

    /**
     * Navigates to the photo upload view.
     * This is kept for backward compatibility.
     * @deprecated Use {@link #navigateToAssignedOrder()} instead.
     */
    @Deprecated
    public void navigateToPhotoUpload() {
        logger.fine("Navigating to assigned order view (redirected from photo upload)");
        Router.navigateTo(AssignedOrderView.class);
        logger.fine("Navigation to assigned order view completed");
    }
}
