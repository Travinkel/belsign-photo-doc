package com.belman.backbone.core.navigation;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.belman.backbone.core.logging.Logger;
import com.belman.backbone.core.api.CoreAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

/**
 * Router for navigating between views in a Gluon Mobile application.
 * Supports route parameters, nested routes, and route guards.
 */
public class Router {
    private static final Logger logger = Logger.getLogger(Router.class);
    private static MobileApplication mobileApplication;
    private static final Router instance = new Router();
    private static View currentView;
    private static final Map<String, Object> routeParameters = new HashMap<>();
    private static final Map<Class<? extends View>, Supplier<Boolean>> routeGuards = new HashMap<>();
    private static final Stack<Class<? extends View>> navigationHistory = new Stack<>();

    private Router() {
        // Singleton constructor
    }

    /**
     * Gets the singleton instance of the Router.
     * 
     * @return the Router instance
     */
    public static Router getInstance() {
        return instance;
    }

    /**
     * Sets the mobile application for the router.
     * This method should be called once during application startup.
     * 
     * @param application the mobile application
     */
    public static void setMobileApplication(MobileApplication application) {
        mobileApplication = application;
    }

    public static void navigateTo(Class<? extends View> viewClass) {
        navigateTo(viewClass, new HashMap<>());
    }

    public static void navigateTo(Class<? extends View> viewClass, Map<String, Object> parameters) {
        if (viewClass == null) {
            logger.error("View class is null");
            throw new IllegalArgumentException("View class cannot be null");
        }
        if (parameters == null) {
            parameters = new HashMap<>();
        }

        // Store route parameters in CoreAPI state
        CoreAPI.setState("routeParameters", parameters);

        // Check if there's a route guard for this view
        if (routeGuards.containsKey(viewClass)) {
            Supplier<Boolean> guard = routeGuards.get(viewClass);
            if (!guard.get()) {
                logger.warn("Route guard prevented navigation to: {}", viewClass.getSimpleName());
                return;
            }
        }

        try {
            // Set route parameters
            routeParameters.clear();
            if (parameters != null) {
                routeParameters.putAll(parameters);
            }

            View view = viewClass.getDeclaredConstructor().newInstance();

            // Update the current view reference
            currentView = view;

            // Show the new view
            String viewId = viewClass.getSimpleName();

            // Register the view with the mobile application
            try {
                mobileApplication.addViewFactory(viewId, () -> view);
            } catch (IllegalArgumentException e) {
                // View already registered, ignore
                logger.info("View already registered: {}", viewId);
            }

            // Switch to the view
            mobileApplication.switchView(viewId);

            AppBar appBar = MobileApplication.getInstance().getAppBar();
            if (appBar != null) {
                appBar.setTitleText(viewId);
            }

            // Add the view to the navigation history
            navigationHistory.push(viewClass);

            logger.info("Navigated to: {}", viewClass.getSimpleName());
        } catch (Exception e) {
            logger.error("Failed to navigate to: {}", viewClass.getSimpleName(), e);
            throw new RuntimeException("Failed to navigate to: " + viewClass.getSimpleName(), e);
        }
    }

    /**
     * Navigates back to the previous view.
     * 
     * @return true if navigation was successful, false if there is no previous view
     */
    public static boolean navigateBack() {
        if (navigationHistory.size() <= 1) {
            logger.warn("Cannot navigate back: no previous view");
            return false;
        }

        // Remove the current view from the history
        navigationHistory.pop();
        Class<? extends View> previousViewClass = navigationHistory.pop();
        navigateTo(previousViewClass);

        return true;
    }

    public static void addGuard(Class<? extends View> viewClass, Supplier<Boolean> guard) {
        if (viewClass == null || guard == null) {
            throw new IllegalArgumentException("View class and guard cannot be null");
        }

        if (guard == null) {
            logger.error("Guard is null");
            throw new IllegalArgumentException("Guard cannot be null");
        }

        routeGuards.put(viewClass, guard);
        logger.info("Added guard for view: {}", viewClass.getSimpleName());
    }

    /**
     * Removes a route guard for the specified view.
     * 
     * @param viewClass the view class to remove the guard for
     */
    public static void removeGuard(Class<? extends View> viewClass) {
        if (viewClass == null) {
            logger.error("View class is null");
            throw new IllegalArgumentException("View class cannot be null");
        }
        routeGuards.remove(viewClass);
        logger.info("Removed guard for view: {}", viewClass.getSimpleName());
    }

    /**
     * Gets a route parameter.
     * 
     * @param key the parameter key
     * @param <T> the parameter type
     * @return the parameter value, or null if not found
     */
    @SuppressWarnings("unchecked")
    public static <T> T getParameter(String key) {
        Map<String, Object> parameters = CoreAPI.getState("routeParameters");
        if (parameters != null) {
            return (T) parameters.get(key);
        }
        return null;
    }

    public static View getCurrentView() {
        return currentView;
    }

    public static Stack<Class<? extends View>> getNavigationHistory() {
        return new Stack<>() {{
            addAll(navigationHistory);
        }};
    }

    public static void clearNavigationHistory() {
        navigationHistory.clear();
    }
}
