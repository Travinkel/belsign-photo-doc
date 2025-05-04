package com.belman.application.api;

import com.belman.application.core.EventManager;
import com.belman.application.core.SessionManager;
import com.belman.application.core.StateManager;
import com.belman.domain.aggregates.User;
import com.belman.domain.services.AuthenticationService;
import com.belman.domain.shared.DomainEvent;
import com.belman.domain.shared.DomainEventHandler;
import com.belman.domain.shared.Property;
import com.belman.domain.shared.StateKey;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Central API for interacting with the application.
 * This class provides methods for state management, event handling, and session management.
 * 
 * @deprecated This class is deprecated and will be removed in a future release.
 * Use {@link com.belman.application.core.StateManager}, {@link com.belman.application.core.EventManager},
 * and {@link com.belman.application.core.SessionManager} instead.
 */
@Deprecated
public class CoreAPI {

    private CoreAPI() {
        // Private constructor to prevent instantiation
    }

    // ===== Session Management =====

    /**
     * Initializes the session manager with the specified authentication service.
     * 
     * @param authenticationService the authentication service
     * @return the session manager instance
     * @deprecated Use {@link com.belman.application.core.SessionManager#getInstance(AuthenticationService)} instead.
     */
    @Deprecated
    public static SessionManager initializeSessionManager(AuthenticationService authenticationService) {
        return SessionManager.getInstance(authenticationService);
    }

    /**
     * Gets the session manager instance.
     * 
     * @return the session manager instance
     * @throws IllegalStateException if the session manager has not been initialized
     * @deprecated Use {@link com.belman.application.core.SessionManager#getInstance()} instead.
     */
    @Deprecated
    public static SessionManager getSessionManager() {
        SessionManager sessionManager = SessionManager.getInstance();
        if (sessionManager == null) {
            throw new IllegalStateException("SessionManager has not been initialized");
        }
        return sessionManager;
    }

    /**
     * Gets the currently authenticated user.
     * 
     * @return an Optional containing the authenticated User if a user is logged in, or empty if no user is logged in
     * @throws IllegalStateException if the session manager has not been initialized
     * @deprecated Use {@link com.belman.application.core.SessionManager#getCurrentUser()} instead.
     */
    @Deprecated
    public static Optional<User> getCurrentUser() {
        return getSessionManager().getCurrentUser();
    }

    /**
     * Checks if a user is currently logged in.
     * 
     * @return true if a user is logged in, false otherwise
     * @throws IllegalStateException if the session manager has not been initialized
     * @deprecated Use {@link com.belman.application.core.SessionManager#isLoggedIn()} instead.
     */
    @Deprecated
    public static boolean isLoggedIn() {
        return getSessionManager().isLoggedIn();
    }

    /**
     * Logs out the current user.
     * 
     * @throws IllegalStateException if the session manager has not been initialized
     * @deprecated Use {@link com.belman.application.core.SessionManager#logout()} instead.
     */
    @Deprecated
    public static void logout() {
        getSessionManager().logout();
    }

    /**
     * Authenticates a user with the given username and password.
     * 
     * @param username the username
     * @param password the password
     * @return an Optional containing the authenticated User if successful, or empty if authentication failed
     * @throws IllegalStateException if the session manager has not been initialized
     * @deprecated Use {@link com.belman.application.core.SessionManager#login(String, String)} instead.
     */
    @Deprecated
    public static Optional<User> login(String username, String password) {
        return getSessionManager().login(username, password);
    }

    // ===== State Management =====

    /**
     * Sets a value in the state store.
     * 
     * @param key the key for the state value
     * @param value the value to store
     * @param <T> the type of the value
     * @deprecated Use {@link com.belman.application.core.StateManager#setState(String, Object)} instead.
     */
    @Deprecated
    public static <T> void setState(String key, T value) {
        StateManager.getInstance().setState(key, value);
    }

    /**
     * Gets a value from the state store.
     * 
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return the value, or null if not found
     * @deprecated Use {@link com.belman.application.core.StateManager#getState(String)} instead.
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static <T> T getState(String key) {
        return StateManager.getInstance().getState(key);
    }

    /**
     * Updates a value in the state store using a function.
     * 
     * @param key the key for the state value
     * @param updater the function to update the value
     * @param <T> the type of the value
     * @deprecated Use {@link com.belman.application.core.StateManager#updateState(String, Function)} instead.
     */
    @Deprecated
    public static <T> void updateState(String key, Function<T, T> updater) {
        StateManager.getInstance().updateState(key, updater);
    }

    /**
     * Registers a listener for changes to a specific state value.
     * 
     * @param key the key for the state value
     * @param owner the owner object (used for unregistering)
     * @param listener the listener to call when the value changes
     * @param <T> the expected type of the value
     * @deprecated Use {@link com.belman.application.core.StateManager#listenToState(String, Object, Consumer)} instead.
     */
    @Deprecated
    public static <T> void listenToState(String key, Object owner, Consumer<T> listener) {
        StateManager.getInstance().listenToState(key, owner, listener);
    }

    /**
     * Unregisters a listener for a specific state value.
     * 
     * @param key the key for the state value
     * @param owner the owner object used when registering
     * @deprecated Use {@link com.belman.application.core.StateManager#unlistenToState(String, Object)} instead.
     */
    @Deprecated
    public static void unlistenToState(String key, Object owner) {
        StateManager.getInstance().unlistenToState(key, owner);
    }

    /**
     * Clears all state values and listeners.
     * This is primarily for testing purposes.
     * 
     * @deprecated Use {@link com.belman.application.core.StateManager#clearState()} instead.
     */
    @Deprecated
    public static void clearState() {
        StateManager.getInstance().clearState();
    }

    /**
     * Sets a value in the state store using a type-safe key.
     * 
     * @param key the type-safe key for the state value
     * @param value the value to store
     * @param <T> the type of the value
     * @deprecated Use {@link com.belman.application.core.StateManager#setState(StateKey, Object)} instead.
     */
    @Deprecated
    public static <T> void setState(StateKey<T> key, T value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        StateManager.getInstance().setState(key, value);
    }

    /**
     * Gets a value from the state store using a type-safe key.
     * 
     * @param key the type-safe key for the state value
     * @param <T> the expected type of the value
     * @return the value, or null if not found
     * @deprecated Use {@link com.belman.application.core.StateManager#getState(StateKey)} instead.
     */
    @Deprecated
    public static <T> T getState(StateKey<T> key) {
        return StateManager.getInstance().getState(key);
    }

    /**
     * Gets a property from the state store using a type-safe key.
     * 
     * @param key the type-safe key for the state value
     * @param <T> the expected type of the value
     * @return the property, or a new property if not found
     * @deprecated Use {@link com.belman.application.core.StateManager#getStateProperty(StateKey)} instead.
     */
    @Deprecated
    public static <T> Property<T> getStateProperty(StateKey<T> key) {
        return StateManager.getInstance().getStateProperty(key);
    }

    /**
     * Updates a value in the state store using a function and a type-safe key.
     * 
     * @param key the type-safe key for the state value
     * @param updater the function to update the value
     * @param <T> the type of the value
     * @deprecated Use {@link com.belman.application.core.StateManager#updateState(StateKey, Function)} instead.
     */
    @Deprecated
    public static <T> void updateState(StateKey<T> key, Function<T, T> updater) {
        StateManager.getInstance().updateState(key, updater);
    }

    /**
     * Registers a listener for changes to a specific state value using a type-safe key.
     * 
     * @param key the type-safe key for the state value
     * @param owner the owner object (used for unregistering)
     * @param listener the listener to call when the value changes
     * @param <T> the expected type of the value
     * @deprecated Use {@link com.belman.application.core.StateManager#listenToState(StateKey, Object, Consumer)} instead.
     */
    @Deprecated
    public static <T> void listenToState(StateKey<T> key, Object owner, Consumer<T> listener) {
        StateManager.getInstance().listenToState(key, owner, listener);
    }

    /**
     * Unregisters a listener for a specific state value using a type-safe key.
     * 
     * @param key the type-safe key for the state value
     * @param owner the owner object used when registering
     * @param <T> the type of the value
     * @deprecated Use {@link com.belman.application.core.StateManager#unlistenToState(StateKey, Object)} instead.
     */
    @Deprecated
    public static <T> void unlistenToState(StateKey<T> key, Object owner) {
        StateManager.getInstance().unlistenToState(key, owner);
    }

    // ===== Event Handling =====

    /**
     * Publishes a domain event.
     * 
     * @param event the event to publish
     * @deprecated Use {@link com.belman.application.core.EventManager#publishEvent(DomainEvent)} instead.
     */
    @Deprecated
    public static void publishEvent(DomainEvent event) {
        EventManager.getInstance().publishEvent(event);
    }

    /**
     * Publishes a domain event asynchronously.
     * 
     * @param event the event to publish
     * @deprecated Use {@link com.belman.application.core.EventManager#publishEventAsync(DomainEvent)} instead.
     */
    @Deprecated
    public static void publishEventAsync(DomainEvent event) {
        EventManager.getInstance().publishEventAsync(event);
    }

    /**
     * Registers a handler for a specific event type.
     * 
     * @param eventType the class of the event type
     * @param handler the handler to register
     * @param <T> the type of event
     * @deprecated Use {@link com.belman.application.core.EventManager#registerEventHandler(Class, DomainEventHandler)} instead.
     */
    @Deprecated
    public static <T extends DomainEvent> void registerEventHandler(Class<T> eventType, DomainEventHandler<T> handler) {
        EventManager.getInstance().registerEventHandler(eventType, handler);
    }

    /**
     * Registers a handler for a specific event type.
     * 
     * @param eventType the class of the event type
     * @param handler the handler to register
     * @param <T> the type of event
     * @deprecated Use {@link com.belman.application.core.EventManager#registerEventHandler(Class, DomainEventHandler)} instead.
     */
    @Deprecated
    public static <T extends DomainEvent> void registerEventHandler(Class<T> eventType, Consumer<T> handler) {
        EventManager.getInstance().registerEventHandler(eventType, handler::accept);
    }

    /**
     * Unregisters a handler for a specific event type.
     * 
     * @param eventType the class of the event type
     * @param handler the handler to unregister
     * @param <T> the type of event
     * @deprecated Use {@link com.belman.application.core.EventManager#unregisterEventHandler(Class, DomainEventHandler)} instead.
     */
    @Deprecated
    public static <T extends DomainEvent> void unregisterEventHandler(Class<T> eventType, DomainEventHandler<T> handler) {
        EventManager.getInstance().unregisterEventHandler(eventType, handler);
    }
}
