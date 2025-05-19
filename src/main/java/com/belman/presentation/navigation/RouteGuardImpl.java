package com.belman.presentation.navigation;

import com.belman.domain.services.Logger;
import com.belman.bootstrap.security.RouteGuard;
import com.belman.presentation.usecases.admin.dashboard.AdminDashboardView;
import com.belman.presentation.usecases.admin.usermanagement.UserManagementView;
import com.belman.presentation.usecases.qa.dashboard.QADashboardView;
import com.belman.presentation.usecases.qa.review.PhotoReviewView;
import com.belman.presentation.usecases.worker.assignedorder.AssignedOrderView;
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

    static {
        // Initialize the route map
        ROUTE_MAP.put("admin", AdminDashboardView.class);
        ROUTE_MAP.put("userManagement", UserManagementView.class);
        ROUTE_MAP.put("photoReview", PhotoReviewView.class);
        ROUTE_MAP.put("qaDashboard", QADashboardView.class);
        ROUTE_MAP.put("assignedOrder", AssignedOrderView.class); // Updated route name to match the view name
        ROUTE_MAP.put("photoUpload", AssignedOrderView.class); // Keep old route name for backward compatibility
        // Removed orderGallery and reportPreview as they are not used in the current application
    }

    private final Logger logger;

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
