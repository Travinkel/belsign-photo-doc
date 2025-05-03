package com.belman.infrastructure.config;

import com.belman.domain.rbac.AccessPolicyFactory;
import com.belman.domain.rbac.RoleBasedAccessControlFactory;
import com.belman.domain.services.AuthenticationService;
import com.belman.infrastructure.EmojiLogger;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.views.admin.AdminView;
import com.belman.presentation.views.login.LoginView;
import com.belman.presentation.views.ordergallery.OrderGalleryView;
import com.belman.presentation.views.photoreview.PhotoReviewView;
import com.belman.presentation.views.photoupload.PhotoUploadView;
import com.belman.presentation.views.qadashboard.QADashboardView;
import com.belman.presentation.views.reportpreview.ReportPreviewView;
import com.belman.presentation.views.splash.SplashView;
import com.belman.presentation.views.usermanagement.UserManagementView;

/**
 * Initializes route guards for role-based access control.
 * This class adds guards to the Router to restrict access to views based on user roles.
 */
public class RouteGuardInitializer {

    private static final EmojiLogger logger = EmojiLogger.getLogger(RouteGuardInitializer.class);
    private static boolean initialized = false;

    /**
     * Initializes route guards for role-based access control.
     * This method should be called once during application startup, after the Router is set up.
     * 
     * @param authenticationService the authentication service
     */
    public static synchronized void initialize(AuthenticationService authenticationService) {
        if (initialized) {
            logger.debug("Route guards already initialized, skipping initialization");
            return;
        }

        logger.startup("Starting route guard initialization");

        try {
            // Create access policy factory
            AccessPolicyFactory accessPolicyFactory = new AccessPolicyFactory();
            
            // Create role-based access control factory
            RoleBasedAccessControlFactory rbacFactory = new RoleBasedAccessControlFactory(
                authenticationService, accessPolicyFactory);
            
            // Add guards for views
            
            // SplashView and LoginView are accessible to all users (no guard needed)
            logger.debug("No guard needed for SplashView and LoginView");
            
            // AdminView and UserManagementView are accessible only to ADMIN users
            logger.debug("Adding ADMIN guard for AdminView");
            Router.addGuard(AdminView.class, rbacFactory.createAdminAccessController()::hasAccess);
            
            logger.debug("Adding ADMIN guard for UserManagementView");
            Router.addGuard(UserManagementView.class, rbacFactory.createAdminAccessController()::hasAccess);
            
            // PhotoReviewView and QADashboardView are accessible to QA users
            logger.debug("Adding QA guard for PhotoReviewView");
            Router.addGuard(PhotoReviewView.class, rbacFactory.createQAAccessController()::hasAccess);
            
            logger.debug("Adding QA guard for QADashboardView");
            Router.addGuard(QADashboardView.class, rbacFactory.createQAAccessController()::hasAccess);
            
            // PhotoUploadView is accessible to PRODUCTION users
            logger.debug("Adding PRODUCTION guard for PhotoUploadView");
            Router.addGuard(PhotoUploadView.class, rbacFactory.createProductionAccessController()::hasAccess);
            
            // OrderGalleryView is accessible to all authenticated users
            logger.debug("Adding ALL ROLES guard for OrderGalleryView");
            Router.addGuard(OrderGalleryView.class, rbacFactory.createAllRolesAccessController()::hasAccess);
            
            // ReportPreviewView is accessible to QA and ADMIN users
            logger.debug("Adding QA and ADMIN guard for ReportPreviewView");
            Router.addGuard(ReportPreviewView.class, rbacFactory.createQAAndAdminAccessController()::hasAccess);
            
            initialized = true;
            logger.startup("Route guards initialized successfully ✨");
        } catch (Exception e) {
            logger.failure("Failed to initialize route guards");
            logger.error("Route guard initialization error details", e);
            throw new RuntimeException("Failed to initialize route guards", e);
        }
    }
}