package com.belman.ui.navigation;

import com.belman.common.logging.EmojiLogger;
import com.belman.common.platform.PlatformUtils;
import com.belman.ui.core.*;
import com.belman.ui.transitions.FadeViewTransition;
import com.belman.ui.transitions.SlideDirection;
import com.belman.ui.transitions.SlideViewTransition;
import com.belman.ui.transitions.ViewTransition;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

/**
 * Router for navigating between views in a Gluon Mobile application.
 * Supports route parameters, nested routes, route guards, and view transitions.
 */
public class Router {
    private static final EmojiLogger logger = EmojiLogger.getLogger(Router.class);
    private static final Router instance = new Router();
    private static final Map<String, Object> routeParameters = new HashMap<>();
    private static final Map<Class<? extends View>, Supplier<Boolean>> routeGuards = new HashMap<>();
    private static final Stack<Class<? extends View>> navigationHistory = new Stack<>();
    private static com.gluonhq.charm.glisten.application.MobileApplication mobileApplication;
    private static View currentView;
    // Default transitions
    private static ViewTransition defaultForwardTransition;
    private static ViewTransition defaultBackwardTransition;

    private Router() {
        // Singleton constructor

        // Initialize default transitions based on platform
        if (PlatformUtils.isRunningOnMobile()) {
            // Mobile platforms typically use slide transitions
            defaultForwardTransition = new SlideViewTransition(SlideDirection.RIGHT_TO_LEFT);
            defaultBackwardTransition = new SlideViewTransition(SlideDirection.LEFT_TO_RIGHT);
        } else {
            // Desktop platforms typically use fade transitions
            defaultForwardTransition = new FadeViewTransition();
            defaultBackwardTransition = new FadeViewTransition();
        }
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
    public static void setMobileApplication(com.gluonhq.charm.glisten.application.MobileApplication application) {
        mobileApplication = application;
        logger.startup("Router initialized with MobileApplication");
    }

    /**
     * Gets the default forward transition.
     *
     * @return the default forward transition
     */
    public static ViewTransition getDefaultForwardTransition() {
        return defaultForwardTransition;
    }

    /**
     * Sets the default forward transition.
     * This transition is used when navigating to a new view.
     *
     * @param transition the transition to use
     */
    public static void setDefaultForwardTransition(ViewTransition transition) {
        if (transition == null) {
            throw new IllegalArgumentException("Transition cannot be null");
        }
        defaultForwardTransition = transition;
    }

    /**
     * Gets the default backward transition.
     *
     * @return the default backward transition
     */
    public static ViewTransition getDefaultBackwardTransition() {
        return defaultBackwardTransition;
    }

    /**
     * Sets the default backward transition.
     * This transition is used when navigating back to a previous view.
     *
     * @param transition the transition to use
     */
    public static void setDefaultBackwardTransition(ViewTransition transition) {
        if (transition == null) {
            throw new IllegalArgumentException("Transition cannot be null");
        }
        defaultBackwardTransition = transition;
    }

    /**
     * Navigates to the specified view using the default forward transition.
     *
     * @param viewClass the class of the view to navigate to
     */
    public static void navigateTo(Class<? extends View> viewClass) {
        navigateTo(viewClass, new HashMap<>(), defaultForwardTransition);
    }

    /**
     * Navigates to the specified view with the specified parameters and transition.
     *
     * @param viewClass  the class of the view to navigate to
     * @param parameters the parameters to pass to the view
     * @param transition the transition to use
     */
    public static void navigateTo(Class<? extends View> viewClass, Map<String, Object> parameters,
                                  ViewTransition transition) {
        if (viewClass == null) {
            logger.error("View class is null");
            throw new IllegalArgumentException("View class cannot be null");
        }
        if (parameters == null) {
            parameters = new HashMap<>();
        }

        logger.debug("Navigating to: {} with {} parameters", viewClass.getSimpleName(), parameters.size());

        // Store route parameters in state
        StateManager.getInstance().setState("routeParameters", parameters);

        // Check if there's a route guard for this view
        if (routeGuards.containsKey(viewClass)) {
            logger.debug("Checking route guard for: {}", viewClass.getSimpleName());
            Supplier<Boolean> guard = routeGuards.get(viewClass);
            if (!guard.get()) {
                logger.warn("Route guard prevented navigation to: {}", viewClass.getSimpleName());
                return;
            }
            logger.debug("Route guard allowed navigation to: {}", viewClass.getSimpleName());
        }

        try {
            // Set route parameters
            routeParameters.clear();
            if (parameters != null) {
                routeParameters.putAll(parameters);
            }

            // Get the view ID
            String viewId = viewClass.getSimpleName();

            // We don't need to register the view again, as it should have been registered during app initialization
            logger.debug("Using view ID for navigation: {}", viewId);

            // Add the view to the navigation history
            navigationHistory.push(viewClass);
            logger.debug("Added {} to navigation history. History size: {}", viewId, navigationHistory.size());

            // Switch to the view using MobileApplication
            logger.debug("Switching to view: {}", viewId);
            try {
                System.out.println("[DEBUG_LOG] About to call mobileApplication.switchView(" + viewId + ")");
                mobileApplication.switchView(viewId);
                System.out.println("[DEBUG_LOG] Successfully called mobileApplication.switchView(" + viewId + ")");

                // Update the AppBar
                updateAppBar(viewId);

                logger.success("Navigated to: " + viewClass.getSimpleName());
            } catch (Exception e) {
                System.err.println(
                        "[DEBUG_LOG] Error calling mobileApplication.switchView(" + viewId + "): " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Failed to switch to view: " + viewId, e);
            }
        } catch (Exception e) {
            logger.failure("Failed to navigate to: " + viewClass.getSimpleName());
            logger.error("Navigation error details", e);
            throw new RuntimeException("Failed to navigate to: " + viewClass.getSimpleName(), e);
        }
    }

    /**
     * Updates the app bar title.
     *
     * @param title the new title to set
     */
    public static void updateAppBar(String title) {
        if (mobileApplication != null) {
            AppBar appBar = mobileApplication.getAppBar();
            if (appBar != null) {
                appBar.setTitleText(title);
            } else {
                logger.warn("AppBar is null. Unable to update title.");
            }
        } else {
            logger.warn("MobileApplication is null. Unable to update AppBar title.");
        }
    }

    /**
     * Navigates to the specified view with the specified parameters using the default forward transition.
     *
     * @param viewClass  the class of the view to navigate to
     * @param parameters the parameters to pass to the view
     */
    public static void navigateTo(Class<? extends View> viewClass, Map<String, Object> parameters) {
        navigateTo(viewClass, parameters, defaultForwardTransition);
    }

    /**
     * Navigates back to the previous view using the default backward transition.
     *
     * @return true if navigation was successful, false if there is no previous view
     */
    public static boolean navigateBack() {
        return navigateBack(defaultBackwardTransition);
    }

    /**
     * Navigates back to the previous view using the specified transition.
     *
     * @param transition the transition to use
     * @return true if navigation was successful, false if there is no previous view
     */
    public static boolean navigateBack(ViewTransition transition) {
        logger.debug("Attempting to navigate back. History size: {}", navigationHistory.size());

        if (navigationHistory.size() <= 1) {
            logger.warn("Cannot navigate back: no previous view in history");
            return false;
        }

        // Remove the current view from the history
        Class<? extends View> currentViewClass = navigationHistory.pop();
        logger.debug("Removed current view from history: {}", currentViewClass.getSimpleName());

        Class<? extends View> previousViewClass = navigationHistory.pop();
        logger.debug("Navigating back to previous view: {}", previousViewClass.getSimpleName());

        navigateTo(previousViewClass, transition);
        logger.success("Successfully navigated back to: " + previousViewClass.getSimpleName());

        return true;
    }

    /**
     * Navigates to the specified view with the specified transition.
     *
     * @param viewClass  the class of the view to navigate to
     * @param transition the transition to use
     */
    public static void navigateTo(Class<? extends View> viewClass, ViewTransition transition) {
        navigateTo(viewClass, new HashMap<>(), transition);
    }

    /**
     * Adds a route guard for the specified view.
     *
     * @param viewClass the view class to add the guard for
     * @param guard     the guard function that determines if navigation is allowed
     */
    public static void addGuard(Class<? extends View> viewClass, Supplier<Boolean> guard) {
        if (viewClass == null || guard == null) {
            logger.error("Cannot add guard: view class or guard is null");
            throw new IllegalArgumentException("View class and guard cannot be null");
        }

        logger.debug("Adding guard for view: {}", viewClass.getSimpleName());
        routeGuards.put(viewClass, guard);
        logger.success("Added security guard for view: " + viewClass.getSimpleName());
    }

    /**
     * Removes a route guard for the specified view.
     *
     * @param viewClass the view class to remove the guard for
     */
    public static void removeGuard(Class<? extends View> viewClass) {
        if (viewClass == null) {
            logger.error("Cannot remove guard: view class is null");
            throw new IllegalArgumentException("View class cannot be null");
        }

        logger.debug("Removing guard for view: {}", viewClass.getSimpleName());
        boolean removed = routeGuards.remove(viewClass) != null;

        if (removed) {
            logger.success("Removed security guard from view: " + viewClass.getSimpleName());
        } else {
            logger.debug("No guard found to remove for view: {}", viewClass.getSimpleName());
        }
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
        Map<String, Object> parameters = StateManager.getInstance().getState("routeParameters");
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
