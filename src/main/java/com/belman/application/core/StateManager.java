package com.belman.application.core;

import com.belman.domain.shared.NestedProperty;
import com.belman.domain.shared.Property;
import com.belman.domain.shared.StateKey;
import com.belman.domain.shared.StateSchema;
import com.belman.domain.shared.StateStore;
import com.belman.domain.shared.ValidationResult;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Manager for application state.
 * Provides methods for setting, getting, and listening to state changes.
 * This class is a wrapper around the StateStore class and provides a more convenient API.
 */
public class StateManager {
    private static final StateManager instance = new StateManager();

    private StateManager() {
        // Private constructor to enforce singleton pattern
    }

    /**
     * Gets the singleton instance of the StateManager.
     * 
     * @return the StateManager instance
     */
    public static StateManager getInstance() {
        return instance;
    }

    /**
     * Sets a value in the state store.
     * 
     * @param key the key for the state value
     * @param value the value to store
     * @param <T> the type of the value
     */
    public <T> void setState(String key, T value) {
        StateStore.getInstance().set(key, value);
    }

    /**
     * Gets a value from the state store.
     * 
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return the value, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T> T getState(String key) {
        return StateStore.getInstance().get(key);
    }

    /**
     * Updates a value in the state store using a function.
     * 
     * @param key the key for the state value
     * @param updater the function to update the value
     * @param <T> the type of the value
     */
    public <T> void updateState(String key, Function<T, T> updater) {
        StateStore.getInstance().update(key, updater);
    }

    /**
     * Registers a listener for changes to a specific state value.
     * 
     * @param key the key for the state value
     * @param owner the owner object (used for unregistering)
     * @param listener the listener to call when the value changes
     * @param <T> the expected type of the value
     */
    public <T> void listenToState(String key, Object owner, Consumer<T> listener) {
        StateStore.getInstance().listen(key, owner, listener);
    }

    /**
     * Unregisters a listener for a specific state value.
     * 
     * @param key the key for the state value
     * @param owner the owner object used when registering
     */
    public void unlistenToState(String key, Object owner) {
        StateStore.getInstance().unlisten(key, owner);
    }

    /**
     * Clears all state values and listeners.
     * This is primarily for testing purposes.
     */
    public void clearState() {
        StateStore.getInstance().clear();
    }

    /**
     * Sets a value in the state store using a type-safe key.
     * 
     * @param key the type-safe key for the state value
     * @param value the value to store
     * @param <T> the type of the value
     */
    public <T> void setState(StateKey<T> key, T value) {
        StateStore.getInstance().setTyped(key, value);
    }

    /**
     * Gets a value from the state store using a type-safe key.
     * 
     * @param key the type-safe key for the state value
     * @param <T> the expected type of the value
     * @return the value, or null if not found
     */
    public <T> T getState(StateKey<T> key) {
        return StateStore.getInstance().getTyped(key);
    }

    /**
     * Gets a property from the state store using a type-safe key.
     * 
     * @param key the type-safe key for the state value
     * @param <T> the expected type of the value
     * @return the property, or a new property if not found
     */
    public <T> Property<T> getStateProperty(StateKey<T> key) {
        return StateStore.getInstance().getPropertyTyped(key);
    }

    /**
     * Updates a value in the state store using a function and a type-safe key.
     * 
     * @param key the type-safe key for the state value
     * @param updater the function to update the value
     * @param <T> the type of the value
     */
    public <T> void updateState(StateKey<T> key, Function<T, T> updater) {
        StateStore.getInstance().updateTyped(key, updater);
    }

    /**
     * Registers a listener for changes to a specific state value using a type-safe key.
     * 
     * @param key the type-safe key for the state value
     * @param owner the owner object (used for unregistering)
     * @param listener the listener to call when the value changes
     * @param <T> the expected type of the value
     */
    public <T> void listenToState(StateKey<T> key, Object owner, Consumer<T> listener) {
        StateStore.getInstance().listenTyped(key, owner, listener);
    }

    /**
     * Unregisters a listener for a specific state value using a type-safe key.
     * 
     * @param key the type-safe key for the state value
     * @param owner the owner object used when registering
     * @param <T> the type of the value
     */
    public <T> void unlistenToState(StateKey<T> key, Object owner) {
        StateStore.getInstance().unlistenTyped(key, owner);
    }

    /**
     * Gets a nested property for the specified key.
     * 
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return a nested property
     */
    public <T> NestedProperty<T> getNestedProperty(String key) {
        return StateStore.getInstance().getNestedProperty(key);
    }

    /**
     * Gets a nested property for the specified key.
     * 
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return a nested property
     */
    public <T> NestedProperty<T> getNestedProperty(StateKey<T> key) {
        return StateStore.getInstance().getNestedPropertyTyped(key);
    }

    /**
     * Registers a schema for the specified key.
     * 
     * @param key the key for the state value
     * @param schema the schema for the state value
     * @param <T> the expected type of the value
     */
    public <T> void registerSchema(String key, StateSchema<T> schema) {
        StateStore.getInstance().registerSchema(key, schema);
    }

    /**
     * Registers a schema for the specified key.
     * 
     * @param key the key for the state value
     * @param schema the schema for the state value
     * @param <T> the expected type of the value
     */
    public <T> void registerSchema(StateKey<T> key, StateSchema<T> schema) {
        StateStore.getInstance().registerSchema(key, schema);
    }

    /**
     * Validates the state value for the specified key against its registered schema.
     * 
     * @param key the key for the state value
     * @return a validation result
     */
    public ValidationResult validate(String key) {
        return StateStore.getInstance().validate(key);
    }

    /**
     * Validates the state value for the specified key against its registered schema.
     * 
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return a validation result
     */
    public <T> ValidationResult validate(StateKey<T> key) {
        return StateStore.getInstance().validate(key);
    }

    /**
     * Validates all state values against their registered schemas.
     * 
     * @return a map of validation results, keyed by state key
     */
    public Map<String, ValidationResult> validateAll() {
        return StateStore.getInstance().validateAll();
    }

    static {
        // Listen to changes in the appBarTitle state and update the app bar dynamically
        getInstance().listenToState("appBarTitle", StateManager.class, title -> {
            if (title != null) {
                try {
                    // Try to get the MobileApplication instance
                    Class<?> mobileAppClass = Class.forName("com.gluonhq.charm.glisten.application.MobileApplication");
                    Object mobileApp = mobileAppClass.getMethod("getInstance").invoke(null);

                    if (mobileApp != null) {
                        // Try to get the AppBar
                        Object appBar = mobileAppClass.getMethod("getAppBar").invoke(mobileApp);

                        if (appBar != null) {
                            // Set the title text
                            appBar.getClass().getMethod("setTitleText", String.class).invoke(appBar, title.toString());
                        }
                    }
                } catch (Exception e) {
                    // Ignore exceptions - this is just a convenience feature
                }
            }
        });
    }
}
