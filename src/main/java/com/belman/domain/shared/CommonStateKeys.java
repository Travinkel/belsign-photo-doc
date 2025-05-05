package com.belman.domain.shared;

import com.belman.domain.aggregates.User;

/**
 * Predefined state keys for common application state.
 * <p>
 * This class provides a centralized place for defining and accessing common state keys,
 * making the code more maintainable and reducing the risk of key name collisions.
 * <p>
 * Using these predefined keys instead of creating new ones helps ensure consistency
 * across the application and makes it easier to track state usage.
 */
public final class CommonStateKeys {

    // Prevent instantiation
    private CommonStateKeys() {
    }

    // --- User and Authentication ---

    /**
     * Key for the current user.
     * <p>
     * This key is used to store the currently authenticated user.
     */
    public static final StateKey<User> CURRENT_USER =
        StateKey.of("currentUser", com.belman.domain.aggregates.User.class);

    /**
     * Key for the authentication token.
     * <p>
     * This key is used to store the authentication token for the current user.
     */
    public static final StateKey<String> AUTH_TOKEN = 
        StateKey.of("authToken", String.class);

    /**
     * Key for the login status.
     * <p>
     * This key is used to store whether a user is currently logged in.
     */
    public static final StateKey<Boolean> IS_LOGGED_IN = 
        StateKey.of("isLoggedIn", Boolean.class);

    // --- Navigation ---

    /**
     * Key for the current view.
     * <p>
     * This key is used to store the class of the currently displayed view.
     */
    @SuppressWarnings("unchecked")
    public static final StateKey<Class<?>> CURRENT_VIEW = 
        StateKey.of("currentView", (Class<Class<?>>) (Class<?>) Class.class);

    /**
     * Key for the route parameters.
     * <p>
     * This key is used to store the parameters passed to the current view.
     */
    @SuppressWarnings("unchecked")
    public static final StateKey<java.util.Map<String, Object>> ROUTE_PARAMETERS = 
        StateKey.of("routeParameters", (Class<java.util.Map<String, Object>>) (Class<?>) java.util.Map.class);

    /**
     * Key for the navigation history.
     * <p>
     * This key is used to store the navigation history stack.
     */
    @SuppressWarnings("unchecked")
    public static final StateKey<java.util.Stack<Class<?>>> NAVIGATION_HISTORY = 
        StateKey.of("navigationHistory", (Class<java.util.Stack<Class<?>>>) (Class<?>) java.util.Stack.class);

    // --- Application State ---

    /**
     * Key for the application theme.
     * <p>
     * This key is used to store the current application theme (e.g., "light", "dark").
     */
    public static final StateKey<String> THEME = 
        StateKey.of("theme", String.class);

    /**
     * Key for the application language.
     * <p>
     * This key is used to store the current application language.
     */
    public static final StateKey<java.util.Locale> LANGUAGE = 
        StateKey.of("language", java.util.Locale.class);

    /**
     * Key for the application settings.
     * <p>
     * This key is used to store the application settings.
     */
    @SuppressWarnings("unchecked")
    public static final StateKey<java.util.Map<String, Object>> SETTINGS = 
        StateKey.of("settings", (Class<java.util.Map<String, Object>>) (Class<?>) java.util.Map.class);

    // --- UI State ---

    /**
     * Key for the loading state.
     * <p>
     * This key is used to store whether the application is currently loading data.
     */
    public static final StateKey<Boolean> IS_LOADING = 
        StateKey.of("isLoading", Boolean.class);

    /**
     * Key for the error state.
     * <p>
     * This key is used to store the current error message, if any.
     */
    public static final StateKey<String> ERROR_MESSAGE = 
        StateKey.of("errorMessage", String.class);

    /**
     * Key for the success state.
     * <p>
     * This key is used to store the current success message, if any.
     */
    public static final StateKey<String> SUCCESS_MESSAGE = 
        StateKey.of("successMessage", String.class);

    /**
     * Key for the dialog state.
     * <p>
     * This key is used to store whether a dialog is currently open.
     */
    public static final StateKey<Boolean> IS_DIALOG_OPEN = 
        StateKey.of("isDialogOpen", Boolean.class);

    /**
     * Key for the dialog data.
     * <p>
     * This key is used to store the data for the currently open dialog.
     */
    public static final StateKey<Object> DIALOG_DATA = 
        StateKey.of("dialogData", Object.class);
}
