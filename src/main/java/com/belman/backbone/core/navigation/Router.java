package com.belman.backbone.core.navigation;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.belman.backbone.core.logging.Logger;
import com.belman.backbone.core.api.CoreAPI;
import com.belman.backbone.core.transition.SlideDirection;
import com.belman.backbone.core.transition.SlideViewTransition;
import com.belman.backbone.core.transition.ViewTransition;
import com.belman.backbone.core.util.PlatformUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

/**
 * Router for navigating between views in a Gluon Mobile application.
 * Supports route parameters, nested routes, route guards, and view transitions.
 */
public class Router {
    private static final Logger logger = Logger.getLogger(Router.class);
    private static MobileApplication mobileApplication;
    private static final Router instance = new Router();
    private static View currentView;
    private static final Map<String, Object> routeParameters = new HashMap<>();
    private static final Map<Class<? extends View>, Supplier<Boolean>> routeGuards = new HashMap<>();
    private static final Stack<Class<? extends View>> navigationHistory = new Stack<>();

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
            defaultForwardTransition = new com.belman.backbone.core.transition.FadeViewTransition();
            defaultBackwardTransition = new com.belman.backbone.core.transition.FadeViewTransition();
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
    public static void setMobileApplication(MobileApplication application) {
        mobileApplication = application;
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
     * Gets the default forward transition.
     * 
     * @return the default forward transition
     */
    public static ViewTransition getDefaultForwardTransition() {
        return defaultForwardTransition;
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
     * Gets the default backward transition.
     * 
     * @return the default backward transition
     */
    public static ViewTransition getDefaultBackwardTransition() {
        return defaultBackwardTransition;
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
     * Navigates to the specified view with the specified parameters using the default forward transition.
     *
     * @param viewClass  the class of the view to navigate to
     * @param parameters the parameters to pass to the view
     */
    public static void navigateTo(Class<? extends View> viewClass, Map<String, Object> parameters) {
        navigateTo(viewClass, parameters, defaultForwardTransition);
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
     * Navigates to the specified view with the specified parameters and transition.
     *
     * @param viewClass  the class of the view to navigate to
     * @param parameters the parameters to pass to the view
     * @param transition the transition to use
     */
    public static void navigateTo(Class<? extends View> viewClass, Map<String, Object> parameters, ViewTransition transition) {
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

            View newView = viewClass.getDeclaredConstructor().newInstance();
            View oldView = currentView;

            // Register the view with the mobile application
            String viewId = viewClass.getSimpleName();
            try {
                mobileApplication.addViewFactory(viewId, () -> newView);
            } catch (IllegalArgumentException e) {
                // View already registered, ignore
                logger.info("View already registered: {}", viewId);
            }

            // Add the view to the navigation history
            navigationHistory.push(viewClass);

            // If no transition is specified, use the default forward transition
            ViewTransition effectiveTransition = transition != null ? transition : defaultForwardTransition;

            if (effectiveTransition != null) {
                // Perform the transition
                effectiveTransition.performTransition(oldView, newView, () -> {
                    // Update the current view reference
                    currentView = newView;

                    // Update the app bar
                    AppBar appBar = MobileApplication.getInstance().getAppBar();
                    if (appBar != null) {
                        appBar.setTitleText(viewId);
                    }

                    logger.info("Navigated to: {}", viewClass.getSimpleName());
                });
            } else {
                // No transition, just switch to the view
                mobileApplication.switchView(viewId);

                // Update the current view reference
                currentView = newView;

                // Update the app bar
                AppBar appBar = MobileApplication.getInstance().getAppBar();
                if (appBar != null) {
                    appBar.setTitleText(viewId);
                }

                logger.info("Navigated to: {}", viewClass.getSimpleName());
            }
        } catch (Exception e) {
            logger.error("Failed to navigate to: {}", viewClass.getSimpleName(), e);
            throw new RuntimeException("Failed to navigate to: " + viewClass.getSimpleName(), e);
        }
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
        if (navigationHistory.size() <= 1) {
            logger.warn("Cannot navigate back: no previous view");
            return false;
        }

        // Remove the current view from the history
        navigationHistory.pop();
        Class<? extends View> previousViewClass = navigationHistory.pop();
        navigateTo(previousViewClass, transition);

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
