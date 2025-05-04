package com.belman.infrastructure.config;

import com.belman.domain.rbac.AccessPolicyFactory;
import com.belman.domain.rbac.RoleBasedAccessControlFactory;
import com.belman.domain.services.AuthenticationService;
import com.belman.domain.services.Logger;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.views.admin.AdminView;
import com.belman.presentation.views.ordergallery.OrderGalleryView;
import com.belman.presentation.views.photoreview.PhotoReviewView;
import com.belman.presentation.views.photoupload.PhotoUploadView;
import com.belman.presentation.views.qadashboard.QADashboardView;
import com.belman.presentation.views.reportpreview.ReportPreviewView;
import com.belman.presentation.views.usermanagement.UserManagementView;

/**
 * Initializes route guards for role-based access control.
 * This class adds guards to the Router to restrict access to views based on user roles.
 */
public class RouteGuardInitializer {

    private static Logger logger;
    private static boolean initialized = false;

    /**
     * Sets the logger for this class.
     * This method should be called before using any methods in this class.
     * 
     * @param loggerInstance the logger to use
     */
    public static void setLogger(Logger loggerInstance) {
        logger = loggerInstance;
    }

    /**
     * Safely logs a message at the debug level.
     * If the logger is not set, this method does nothing.
     * 
     * @param message the message to log
     * @param args the arguments to the message
     */
    private static void logDebug(String message, Object... args) {
        if (logger != null) {
            logger.debug(message, args);
        }
    }

    /**
     * Safely logs a message at the info level.
     * If the logger is not set, this method does nothing.
     * 
     * @param message the message to log
     * @param args the arguments to the message
     */
    private static void logInfo(String message, Object... args) {
        if (logger != null) {
            logger.info(message, args);
        }
    }

    /**
     * Safely logs a message at the error level.
     * If the logger is not set, this method does nothing.
     * 
     * @param message the message to log
     * @param throwable the exception to log
     */
    private static void logError(String message, Throwable throwable) {
        if (logger != null) {
            logger.error(message, throwable);
        }
    }

    /**
     * Initializes route guards for role-based access control.
     * This method should be called once during application startup, after the Router is set up.
     * 
     * @param authenticationService the authentication service
     */
    public static synchronized void initialize(AuthenticationService authenticationService) {
        if (initialized) {
            logDebug("Route guards already initialized, skipping initialization");
            return;
        }

        logInfo("Starting route guard initialization");

        try {
            // Create access policy factory
            AccessPolicyFactory accessPolicyFactory = new AccessPolicyFactory();

            // Create role-based access control factory
            RoleBasedAccessControlFactory rbacFactory = new RoleBasedAccessControlFactory(
                authenticationService, accessPolicyFactory);

            // Add guards for views

            // SplashView and LoginView are accessible to all users (no guard needed)
            logDebug("No guard needed for SplashView and LoginView");

            // AdminView and UserManagementView are accessible only to ADMIN users
            logDebug("Adding ADMIN guard for AdminView");
            Router.addGuard(AdminView.class, rbacFactory.createAdminAccessController()::hasAccess);

            logDebug("Adding ADMIN guard for UserManagementView");
            Router.addGuard(UserManagementView.class, rbacFactory.createAdminAccessController()::hasAccess);

            // PhotoReviewView and QADashboardView are accessible to QA users
            logDebug("Adding QA guard for PhotoReviewView");
            Router.addGuard(PhotoReviewView.class, rbacFactory.createQAAccessController()::hasAccess);

            logDebug("Adding QA guard for QADashboardView");
            Router.addGuard(QADashboardView.class, rbacFactory.createQAAccessController()::hasAccess);

            // PhotoUploadView is accessible to PRODUCTION users
            logDebug("Adding PRODUCTION guard for PhotoUploadView");
            Router.addGuard(PhotoUploadView.class, rbacFactory.createProductionAccessController()::hasAccess);

            // OrderGalleryView is accessible to all authenticated users
            logDebug("Adding ALL ROLES guard for OrderGalleryView");
            Router.addGuard(OrderGalleryView.class, rbacFactory.createAllRolesAccessController()::hasAccess);

            // ReportPreviewView is accessible to QA and ADMIN users
            logDebug("Adding QA and ADMIN guard for ReportPreviewView");
            Router.addGuard(ReportPreviewView.class, rbacFactory.createQAAndAdminAccessController()::hasAccess);

            initialized = true;
            logInfo("Route guards initialized successfully ✨");
        } catch (Exception e) {
            logInfo("Failed to initialize route guards");
            logError("Route guard initialization error details", e);
            throw new RuntimeException("Failed to initialize route guards", e);
        }
    }
}
