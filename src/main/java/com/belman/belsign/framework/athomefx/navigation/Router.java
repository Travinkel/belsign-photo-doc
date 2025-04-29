package com.belman.belsign.framework.athomefx.navigation;

import com.belman.belsign.framework.athomefx.core.BaseController;
import com.belman.belsign.framework.athomefx.core.BaseView;
import com.belman.belsign.framework.athomefx.core.BaseViewModel;
import com.belman.belsign.framework.athomefx.lifecycle.ViewLifecycle;
import com.belman.belsign.framework.athomefx.logging.Logger;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

/**
 * Router for navigating between views.
 * Supports route parameters, nested routes, and route guards.
 */
public class Router {
    private static final Logger logger = Logger.getLogger(Router.class);
    private static Stage primaryStage;
    private static final Router instance = new Router();
    private static BaseView<?> currentView;
    private static final Map<String, Object> routeParameters = new HashMap<>();
    private static final Map<Class<? extends BaseView<?>>, Supplier<Boolean>> routeGuards = new HashMap<>();
    private static final Stack<Class<? extends BaseView<?>>> navigationHistory = new Stack<>();
    private static StackPane rootPane;

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
     * Sets the primary stage for the application and initializes the root pane.
     * This method should be called once during application startup.
     * 
     * @param stage the primary stage
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;

        // Initialize the root pane if it doesn't exist
        if (rootPane == null) {
            rootPane = new StackPane();
            Scene scene = new Scene(rootPane);
            primaryStage.setScene(scene);
        }
    }

    /**
     * Navigates to the specified view.
     * 
     * @param viewClass the view class to navigate to
     */
    public static void navigateTo(Class<? extends BaseView<?>> viewClass) {
        navigateTo(viewClass, new HashMap<>());
    }

    /**
     * Navigates to the specified view with the specified parameters.
     * 
     * @param viewClass the view class to navigate to
     * @param parameters the parameters to pass to the view
     */
    public static void navigateTo(Class<? extends BaseView<?>> viewClass, Map<String, Object> parameters) {
        if (viewClass == null) {
            logger.error("View class is null");
            throw new IllegalArgumentException("View class cannot be null");
        }

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

            // Create the view
            BaseView<?> view = viewClass.getDeclaredConstructor().newInstance();

            // Hide the current view
            if (currentView != null) {
                currentView.onHide();
            }

            // Set the new view as the content of the root pane
            rootPane.getChildren().setAll(view.getRoot());
            primaryStage.show();

            // Show the new view
            view.onShow();
            currentView = view;

            // Add the view to the navigation history
            navigationHistory.push(viewClass);

            logger.info("Navigated to: {}", viewClass.getSimpleName());
        } catch (Exception e) {
            logger.error("Failed to navigate to: {}", viewClass.getSimpleName(), e);
            throw new RuntimeException("Failed to navigate to: " + viewClass.getSimpleName(), e);
        }
    }

    /**
     * Navigates to a nested view.
     * The parent view will be loaded first, then the child view will be loaded into a container in the parent view.
     * 
     * @param parentViewClass the parent view class
     * @param childViewClass the child view class
     */
    public static void navigateToNested(Class<? extends BaseView<?>> parentViewClass, Class<? extends BaseView<?>> childViewClass) {
        navigateToNested(parentViewClass, childViewClass, new HashMap<>());
    }

    /**
     * Navigates to a nested view with the specified parameters.
     * The parent view will be loaded first, then the child view will be loaded into a container in the parent view.
     * 
     * @param parentViewClass the parent view class
     * @param childViewClass the child view class
     * @param parameters the parameters to pass to the views
     */
    public static void navigateToNested(Class<? extends BaseView<?>> parentViewClass, Class<? extends BaseView<?>> childViewClass, Map<String, Object> parameters) {
        if (parentViewClass == null || childViewClass == null) {
            logger.error("Parent or child view class is null");
            throw new IllegalArgumentException("Parent and child view classes cannot be null");
        }

        // Navigate to the parent view
        navigateTo(parentViewClass, parameters);

        // Add the child view class to the parameters
        Map<String, Object> childParameters = new HashMap<>(parameters);
        childParameters.put("childViewClass", childViewClass);

        // The parent view is responsible for loading the child view
        logger.info("Navigated to nested view: {} -> {}", parentViewClass.getSimpleName(), childViewClass.getSimpleName());
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

        // Navigate to the previous view
        Class<? extends BaseView<?>> previousViewClass = navigationHistory.pop();
        navigateTo(previousViewClass);

        return true;
    }

    /**
     * Adds a route guard for the specified view.
     * The guard is a function that returns true if navigation to the view is allowed, false otherwise.
     * 
     * @param viewClass the view class to guard
     * @param guard the guard function
     */
    public static void addGuard(Class<? extends BaseView<?>> viewClass, Supplier<Boolean> guard) {
        if (viewClass == null) {
            logger.error("View class is null");
            throw new IllegalArgumentException("View class cannot be null");
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
    public static void removeGuard(Class<? extends BaseView<?>> viewClass) {
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
        return (T) routeParameters.get(key);
    }

    /**
     * Gets the current view.
     * 
     * @return the current view
     */
    public static BaseView<?> getCurrentView() {
        return currentView;
    }

    /**
     * Gets the navigation history.
     * 
     * @return the navigation history
     */
    public static Stack<Class<? extends BaseView<?>>> getNavigationHistory() {
        return new Stack<Class<? extends BaseView<?>>>() {{
            addAll(navigationHistory);
        }};
    }

    /**
     * Clears the navigation history.
     */
    public static void clearNavigationHistory() {
        navigationHistory.clear();
        logger.info("Cleared navigation history");
    }
}
