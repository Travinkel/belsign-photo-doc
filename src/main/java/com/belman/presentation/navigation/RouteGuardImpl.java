package com.belman.presentation.navigation;

import com.belman.application.routing.RouteGuard;
import com.belman.presentation.views.admin.AdminView;
import com.belman.presentation.views.ordergallery.OrderGalleryView;
import com.belman.presentation.views.photoreview.PhotoReviewView;
import com.belman.presentation.views.photoupload.PhotoUploadView;
import com.belman.presentation.views.qadashboard.QADashboardView;
import com.belman.presentation.views.reportpreview.ReportPreviewView;
import com.belman.presentation.views.usermanagement.UserManagementView;
import com.belman.domain.services.Logger;
import com.gluonhq.charm.glisten.mvc.View;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Implementation of the RouteGuard interface that uses the Router to register guards.
 * This class maps route names to view classes and registers guards with the Router.
 */
public class RouteGuardImpl implements RouteGuard {

    private static final Map<String, Class<? extends View>> ROUTE_MAP = new HashMap<>();
    private final Logger logger;

    static {
        // Initialize the route map
        ROUTE_MAP.put("admin", AdminView.class);
        ROUTE_MAP.put("userManagement", UserManagementView.class);
        ROUTE_MAP.put("photoReview", PhotoReviewView.class);
        ROUTE_MAP.put("qaDashboard", QADashboardView.class);
        ROUTE_MAP.put("photoUpload", PhotoUploadView.class);
        ROUTE_MAP.put("orderGallery", OrderGalleryView.class);
        ROUTE_MAP.put("reportPreview", ReportPreviewView.class);
    }

    /**
     * Constructs a new RouteGuardImpl with the specified logger.
     * 
     * @param logger the logger to use
     */
    public RouteGuardImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void registerGuard(String routeName, Supplier<Boolean> guardCondition) {
        if (routeName == null || guardCondition == null) {
            throw new IllegalArgumentException("Route name and guard condition cannot be null");
        }

        Class<? extends View> viewClass = ROUTE_MAP.get(routeName);
        if (viewClass == null) {
            logger.warn("Unknown route name: {}", routeName);
            return;
        }

        logger.debug("Registering guard for route: {}", routeName);
        Router.addGuard(viewClass, guardCondition);
    }
}
