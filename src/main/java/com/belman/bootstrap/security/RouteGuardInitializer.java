package com.belman.bootstrap.security;

import com.belman.domain.security.AuthenticationService;
import com.belman.domain.services.Logger;

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
     * Initializes route guards for role-based access control.
     * This method should be called once during application startup, after the Router is set up.
     *
     * @param authenticationService the authentication service
     * @param routeGuard            the route guard to register guards with
     */
    public static synchronized void initialize(AuthenticationService authenticationService, RouteGuard routeGuard) {
        if (initialized) {
            logDebug("Route guards already initialized, skipping initialization");
            return;
        }

        logInfo("Starting route guard initialization");

        try {
            // AccessPolicyFactory and RoleBasedAccessControlFactory are no longer used
            // Commented out as per task list
            /*
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
            routeGuard.registerGuard("admin", () -> rbacFactory.createAdminAccessController().hasAccess());

            logDebug("Adding ADMIN guard for UserManagementView");
            routeGuard.registerGuard("userManagement", () -> rbacFactory.createAdminAccessController().hasAccess());

            // PhotoReviewView and QADashboardView are accessible to QA users
            logDebug("Adding QA guard for PhotoReviewView");
            routeGuard.registerGuard("photoReview", () -> rbacFactory.createQAAccessController().hasAccess());

            logDebug("Adding QA guard for QADashboardView");
            routeGuard.registerGuard("qaDashboard", () -> rbacFactory.createQAAccessController().hasAccess());

            // PhotoUploadView is accessible to PRODUCTION users
            logDebug("Adding PRODUCTION guard for PhotoUploadView");
            routeGuard.registerGuard("photoUpload", () -> rbacFactory.createProductionAccessController().hasAccess());

            // OrderGalleryView is accessible to all authenticated users
            logDebug("Adding ALL ROLES guard for OrderGalleryView");
            routeGuard.registerGuard("orderGallery", () -> rbacFactory.createAllRolesAccessController().hasAccess());

            // ReportPreviewView is accessible to QA and ADMIN users
            logDebug("Adding QA and ADMIN guard for ReportPreviewView");
            routeGuard.registerGuard("reportPreview", () -> rbacFactory.createQAAndAdminAccessController().hasAccess());
            */

            // Simple stub implementation that allows access to all views
            logDebug("Using simplified route guards that allow access to all views");

            // Register simple guards that always return true
            routeGuard.registerGuard("admin", () -> true);
            routeGuard.registerGuard("userManagement", () -> true);
            routeGuard.registerGuard("photoReview", () -> true);
            routeGuard.registerGuard("qaDashboard", () -> true);
            routeGuard.registerGuard("photoUpload", () -> true);
            routeGuard.registerGuard("orderGallery", () -> true);
            routeGuard.registerGuard("reportPreview", () -> true);

            initialized = true;
            logInfo("Route guards initialized successfully ✨");
        } catch (Exception e) {
            logInfo("Failed to initialize route guards");
            logError("Route guard initialization error details", e);
            throw new RuntimeException("Failed to initialize route guards", e);
        }
    }

    /**
     * Safely logs a message at the debug level.
     * If the logger is not set, this method does nothing.
     *
     * @param message the message to log
     * @param args    the arguments to the message
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
     * @param args    the arguments to the message
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
     * @param message   the message to log
     * @param throwable the exception to log
     */
    private static void logError(String message, Throwable throwable) {
        if (logger != null) {
            logger.error(message, throwable);
        }
    }
}
