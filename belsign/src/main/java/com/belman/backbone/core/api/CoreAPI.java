package com.belman.backbone.core.api;

import com.belman.backbone.core.base.BaseViewModel;
import com.belman.backbone.core.di.ServiceLocator;
import com.belman.backbone.core.events.DomainEvent;
import com.belman.backbone.core.events.DomainEventHandler;
import com.belman.backbone.core.events.DomainEventPublisher;
import com.belman.backbone.core.state.StateStore;
import com.belman.backbone.core.util.ViewLoader;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Central API for interacting with the AtHomeFX core module.
 * Provides access to dependency injection, event handling, state management, and view loading.
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
}