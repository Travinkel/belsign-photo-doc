package com.belman.backbone.core.api;

import com.belman.backbone.core.base.BaseViewModel;
import com.belman.backbone.core.di.ServiceLocator;
import com.belman.backbone.core.events.DomainEvent;
import com.belman.backbone.core.events.DomainEventHandler;
import com.belman.backbone.core.events.DomainEventPublisher;
import com.belman.backbone.core.session.SessionManager;
import com.belman.backbone.core.state.NestedProperty;
import com.belman.backbone.core.state.Property;
import com.belman.backbone.core.state.StateKey;
import com.belman.backbone.core.state.StateSchema;
import com.belman.backbone.core.state.StateStore;
import com.belman.backbone.core.state.ValidationResult;
import com.belman.backbone.core.util.ViewLoader;
import com.belman.domain.aggregates.User;
import com.belman.domain.services.AuthenticationService;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Central API for interacting with the Backbone core module.
 * Provides access to dependency injection, event handling, state management, session management, and view loading.
 */
public class CoreAPI {

    // --- Dependency Injection API ---

    /**
     * Registers a service instance with the ServiceLocator.
     *
     * @param serviceClass    the service class
     * @param serviceInstance the service instance
     * @param <T>             the type of the service
     */
    public static <T> void registerService(Class<T> serviceClass, T serviceInstance) {
        ServiceLocator.registerService(serviceClass, serviceInstance);
    }

    /**
     * Retrieves a service instance from the ServiceLocator.
     *
     * @param serviceClass the service class
     * @param <T>          the type of the service
     * @return the service instance
     */
    public static <T> T getService(Class<T> serviceClass) {
        return ServiceLocator.getService(serviceClass);
    }

    /**
     * Injects services into the target object.
     *
     * @param target the object to inject services into
     */
    public static void injectServices(Object target) {
        ServiceLocator.injectServices(target);
    }

    /**
     * Clears all registered services.
     * This is primarily for testing purposes.
     */
    public static void clearServices() {
        ServiceLocator.clear();
    }

    // --- Event Handling API ---

    /**
     * Registers a handler for a specific event type.
     *
     * @param eventType the class of the event type
     * @param handler   the handler to register
     * @param <T>       the type of event
     */
    public static <T extends DomainEvent> void registerEventHandler(Class<T> eventType, DomainEventHandler<T> handler) {
        DomainEventPublisher.getInstance().register(eventType, handler);
    }

    /**
     * Unregisters a handler for a specific event type.
     *
     * @param eventType the class of the event type
     * @param handler   the handler to unregister
     * @param <T>       the type of event
     */
    public static <T extends DomainEvent> void unregisterEventHandler(Class<T> eventType, DomainEventHandler<T> handler) {
        DomainEventPublisher.getInstance().unregister(eventType, handler);
    }

    /**
     * Publishes an event to all registered handlers.
     *
     * @param event the event to publish
     * @param <T>   the type of event
     */
    public static <T extends DomainEvent> void publishEvent(T event) {
        DomainEventPublisher.getInstance().publish(event);
    }

    /**
     * Publishes an event asynchronously to all registered handlers.
     *
     * @param event the event to publish
     * @param <T>   the type of event
     */
    public static <T extends DomainEvent> void publishEventAsync(T event) {
        DomainEventPublisher.getInstance().publishAsync(event);
    }

    // --- State Management API ---

    /**
     * Sets a value in the state store.
     *
     * @param key   the key for the state value
     * @param value the value to store
     * @param <T>   the type of the value
     */
    public static <T> void setState(String key, T value) {
        StateStore.getInstance().set(key, value);
    }

    /**
     * Gets a value from the state store.
     *
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return the value, or null if not found
     */
    public static <T> T getState(String key) {
        return StateStore.getInstance().get(key);
    }

    /**
     * Updates a value in the state store using a function.
     *
     * @param key     the key for the state value
     * @param updater the function to update the value
     * @param <T>     the type of the value
     */
    public static <T> void updateState(String key, Function<T, T> updater) {
        StateStore.getInstance().update(key, updater);
    }

    /**
     * Registers a listener for changes to a specific state value.
     *
     * @param key      the key for the state value
     * @param owner    the owner object (used for unregistering)
     * @param listener the listener to call when the value changes
     * @param <T>      the expected type of the value
     */
    public static <T> void listenToState(String key, Object owner, Consumer<T> listener) {
        StateStore.getInstance().listen(key, owner, listener);
    }

    /**
     * Unregisters a listener for a specific state value.
     *
     * @param key   the key for the state value
     * @param owner the owner object used when registering
     */
    public static void unlistenToState(String key, Object owner) {
        StateStore.getInstance().unlisten(key, owner);
    }

    /**
     * Clears all state values and listeners.
     * This is primarily for testing purposes.
     */
    public static void clearState() {
        StateStore.getInstance().clear();
    }

    // --- View Management API ---

    /**
     * Loads a view and its associated controller and view model.
     * This is a platform-agnostic method that delegates to the appropriate ViewLoader implementation.
     *
     * @param viewClass the view class to load
     * @param <T>       the view model type
     * @param <P>       the parent type (e.g., Parent in JavaFX)
     * @return a record containing the loaded components
     */
    public static <T extends BaseViewModel<?>, P> ViewLoader.LoadedComponents<T, P> loadView(Class<?> viewClass) {
        return ViewLoader.load(viewClass);
    }

    // --- Session Management API ---

    /**
     * Initializes the SessionManager with the specified AuthenticationService.
     * This method should be called during application startup.
     *
     * @param authenticationService the authentication service to use
     * @return the SessionManager instance
     */
    public static SessionManager initializeSessionManager(AuthenticationService authenticationService) {
        return SessionManager.getInstance(authenticationService);
    }

    /**
     * Gets the SessionManager instance.
     *
     * @return the SessionManager instance, or null if it hasn't been initialized yet
     */
    public static SessionManager getSessionManager() {
        return SessionManager.getInstance();
    }

    /**
     * Gets the currently authenticated user.
     *
     * @return an Optional containing the authenticated User if a user is logged in, or empty if no user is logged in
     * @throws IllegalStateException if the SessionManager hasn't been initialized
     */
    public static Optional<User> getCurrentUser() {
        SessionManager sessionManager = getSessionManager();
        if (sessionManager == null) {
            throw new IllegalStateException("SessionManager has not been initialized");
        }
        return sessionManager.getCurrentUser();
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     * @throws IllegalStateException if the SessionManager hasn't been initialized
     */
    public static boolean isLoggedIn() {
        SessionManager sessionManager = getSessionManager();
        if (sessionManager == null) {
            throw new IllegalStateException("SessionManager has not been initialized");
        }
        return sessionManager.isLoggedIn();
    }

    /**
     * Logs out the current user.
     *
     * @throws IllegalStateException if the SessionManager hasn't been initialized
     */
    public static void logout() {
        SessionManager sessionManager = getSessionManager();
        if (sessionManager == null) {
            throw new IllegalStateException("SessionManager has not been initialized");
        }
        sessionManager.logout();
    }

    /**
     * Authenticates a user with the given username and password.
     *
     * @param username the username
     * @param password the password
     * @return an Optional containing the authenticated User if successful, or empty if authentication failed
     * @throws IllegalStateException if the SessionManager hasn't been initialized
     */
    public static Optional<User> login(String username, String password) {
        SessionManager sessionManager = getSessionManager();
        if (sessionManager == null) {
            throw new IllegalStateException("SessionManager has not been initialized");
        }
        return sessionManager.login(username, password);
    }

    // --- Type-safe State Management API ---

    /**
     * Sets a value in the state store using a type-safe key.
     *
     * @param key   the type-safe key for the state value
     * @param value the value to store
     * @param <T>   the type of the value
     */
    public static <T> void setState(StateKey<T> key, T value) {
        StateStore.getInstance().setTyped(key, value);
    }

    /**
     * Gets a value from the state store using a type-safe key.
     *
     * @param key the type-safe key for the state value
     * @param <T> the expected type of the value
     * @return the value, or null if not found
     */
    public static <T> T getState(StateKey<T> key) {
        return StateStore.getInstance().getTyped(key);
    }

    /**
     * Gets a property from the state store using a type-safe key.
     *
     * @param key the type-safe key for the state value
     * @param <T> the expected type of the value
     * @return the property, or a new property if not found
     */
    public static <T> Property<T> getStateProperty(StateKey<T> key) {
        return StateStore.getInstance().getPropertyTyped(key);
    }

    /**
     * Updates a value in the state store using a function and a type-safe key.
     *
     * @param key     the type-safe key for the state value
     * @param updater the function to update the value
     * @param <T>     the type of the value
     */
    public static <T> void updateState(StateKey<T> key, Function<T, T> updater) {
        StateStore.getInstance().updateTyped(key, updater);
    }

    /**
     * Registers a listener for changes to a specific state value using a type-safe key.
     *
     * @param key      the type-safe key for the state value
     * @param owner    the owner object (used for unregistering)
     * @param listener the listener to call when the value changes
     * @param <T>      the expected type of the value
     */
    public static <T> void listenToState(StateKey<T> key, Object owner, Consumer<T> listener) {
        StateStore.getInstance().listenTyped(key, owner, listener);
    }

    /**
     * Unregisters a listener for a specific state value using a type-safe key.
     *
     * @param key   the type-safe key for the state value
     * @param owner the owner object used when registering
     * @param <T>   the type of the value
     */
    public static <T> void unlistenToState(StateKey<T> key, Object owner) {
        StateStore.getInstance().unlistenTyped(key, owner);
    }

    // --- Nested Property API ---

    /**
     * Gets a nested property for the specified key.
     * <p>
     * A nested property allows for accessing and updating nested properties of complex objects
     * using dot notation (e.g., "user.address.city").
     *
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return a nested property
     */
    public static <T> NestedProperty<T> getNestedProperty(String key) {
        return StateStore.getInstance().getNestedProperty(key);
    }

    /**
     * Gets a nested property for the specified key.
     * <p>
     * A nested property allows for accessing and updating nested properties of complex objects
     * using dot notation (e.g., "user.address.city").
     *
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return a nested property
     */
    public static <T> NestedProperty<T> getNestedProperty(StateKey<T> key) {
        return StateStore.getInstance().getNestedPropertyTyped(key);
    }

    /**
     * Gets a nested value from a state object.
     * <p>
     * This method allows for accessing nested properties of complex objects
     * using dot notation (e.g., "user.address.city").
     *
     * @param key  the key for the state value
     * @param path the path to the nested property
     * @param <T>  the expected type of the state value
     * @param <V>  the expected type of the nested value
     * @return the nested value, or null if not found
     */
    public static <T, V> V getNestedValue(String key, String path) {
        NestedProperty<T> property = getNestedProperty(key);
        return property.getNestedValue(path);
    }

    /**
     * Gets a nested value from a state object.
     * <p>
     * This method allows for accessing nested properties of complex objects
     * using dot notation (e.g., "user.address.city").
     *
     * @param key  the key for the state value
     * @param path the path to the nested property
     * @param <T>  the expected type of the state value
     * @param <V>  the expected type of the nested value
     * @return the nested value, or null if not found
     */
    public static <T, V> V getNestedValue(StateKey<T> key, String path) {
        NestedProperty<T> property = getNestedProperty(key);
        return property.getNestedValue(path);
    }

    /**
     * Sets a nested value in a state object.
     * <p>
     * This method allows for updating nested properties of complex objects
     * using dot notation (e.g., "user.address.city").
     *
     * @param key   the key for the state value
     * @param path  the path to the nested property
     * @param value the value to set
     * @param <T>   the expected type of the state value
     * @param <V>   the type of the nested value
     */
    public static <T, V> void setNestedValue(String key, String path, V value) {
        NestedProperty<T> property = getNestedProperty(key);
        property.setNestedValue(path, value);
    }

    /**
     * Sets a nested value in a state object.
     * <p>
     * This method allows for updating nested properties of complex objects
     * using dot notation (e.g., "user.address.city").
     *
     * @param key   the key for the state value
     * @param path  the path to the nested property
     * @param value the value to set
     * @param <T>   the expected type of the state value
     * @param <V>   the type of the nested value
     */
    public static <T, V> void setNestedValue(StateKey<T> key, String path, V value) {
        NestedProperty<T> property = getNestedProperty(key);
        property.setNestedValue(path, value);
    }

    /**
     * Adds a listener for changes to a nested property.
     * <p>
     * This method allows for listening to changes to nested properties of complex objects
     * using dot notation (e.g., "user.address.city").
     *
     * @param key      the key for the state value
     * @param path     the path to the nested property
     * @param owner    the owner object (used for unregistering)
     * @param listener the listener to call when the value changes
     * @param <T>      the expected type of the state value
     * @param <V>      the expected type of the nested value
     */
    public static <T, V> void listenToNestedState(String key, String path, Object owner, Consumer<V> listener) {
        NestedProperty<T> property = getNestedProperty(key);
        property.addNestedListener(path, listener);
    }

    /**
     * Adds a listener for changes to a nested property.
     * <p>
     * This method allows for listening to changes to nested properties of complex objects
     * using dot notation (e.g., "user.address.city").
     *
     * @param key      the key for the state value
     * @param path     the path to the nested property
     * @param owner    the owner object (used for unregistering)
     * @param listener the listener to call when the value changes
     * @param <T>      the expected type of the state value
     * @param <V>      the expected type of the nested value
     */
    public static <T, V> void listenToNestedState(StateKey<T> key, String path, Object owner, Consumer<V> listener) {
        NestedProperty<T> property = getNestedProperty(key);
        property.addNestedListener(path, listener);
    }

    // --- Schema API ---

    /**
     * Registers a schema for the specified key.
     * <p>
     * A schema defines validation rules for state values and ensures that state values
     * conform to a specific schema.
     *
     * @param key    the key for the state value
     * @param schema the schema for the state value
     * @param <T>    the expected type of the value
     */
    public static <T> void registerSchema(String key, StateSchema<T> schema) {
        StateStore.getInstance().registerSchema(key, schema);
    }

    /**
     * Registers a schema for the specified key.
     * <p>
     * A schema defines validation rules for state values and ensures that state values
     * conform to a specific schema.
     *
     * @param key    the key for the state value
     * @param schema the schema for the state value
     * @param <T>    the expected type of the value
     */
    public static <T> void registerSchema(StateKey<T> key, StateSchema<T> schema) {
        StateStore.getInstance().registerSchema(key, schema);
    }

    /**
     * Gets the schema for the specified key.
     *
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return the schema, or null if no schema is registered for the key
     */
    public static <T> StateSchema<T> getSchema(String key) {
        return StateStore.getInstance().getSchema(key);
    }

    /**
     * Gets the schema for the specified key.
     *
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return the schema, or null if no schema is registered for the key
     */
    public static <T> StateSchema<T> getSchema(StateKey<T> key) {
        return StateStore.getInstance().getSchema(key);
    }

    /**
     * Validates the state value for the specified key against its registered schema.
     *
     * @param key the key for the state value
     * @return a validation result
     */
    public static ValidationResult validate(String key) {
        return StateStore.getInstance().validate(key);
    }

    /**
     * Validates the state value for the specified key against its registered schema.
     *
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return a validation result
     */
    public static <T> ValidationResult validate(StateKey<T> key) {
        return StateStore.getInstance().validate(key);
    }

    /**
     * Validates the state value for the specified key against the specified schema.
     *
     * @param key    the key for the state value
     * @param schema the schema to validate against
     * @param <T>    the expected type of the value
     * @return a validation result
     */
    public static <T> ValidationResult validate(String key, StateSchema<T> schema) {
        return StateStore.getInstance().validate(key, schema);
    }

    /**
     * Validates the state value for the specified key against the specified schema.
     *
     * @param key    the key for the state value
     * @param schema the schema to validate against
     * @param <T>    the expected type of the value
     * @return a validation result
     */
    public static <T> ValidationResult validate(StateKey<T> key, StateSchema<T> schema) {
        return StateStore.getInstance().validate(key, schema);
    }

    /**
     * Validates all state values against their registered schemas.
     *
     * @return a map of validation results, keyed by state key
     */
    public static Map<String, ValidationResult> validateAll() {
        return StateStore.getInstance().validateAll();
    }
}
