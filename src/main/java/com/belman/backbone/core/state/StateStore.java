package com.belman.backbone.core.state;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A global state management store for the application.
 * Inspired by Vuex/Pinia from Vue.js, this class provides a centralized
 * store for managing application state with reactive properties.
 */
public class StateStore {
    private static final StateStore instance = new StateStore();

    // The actual state storage
    private final Map<String, Property<Object>> state;

    // Listeners for state changes
    private final Map<String, Map<Object, Consumer<Object>>> listeners;

    // Schemas for state validation
    private final Map<String, StateSchema<?>> schemas;

    // Private constructor for singleton
    private StateStore() {
        this.state = new ConcurrentHashMap<>();
        this.listeners = new ConcurrentHashMap<>();
        this.schemas = new ConcurrentHashMap<>();
    }

    /**
     * Gets the singleton instance of the StateStore.
     * 
     * @return the StateStore instance
     */
    public static StateStore getInstance() {
        return instance;
    }

    /**
     * Sets a value in the state store.
     * 
     * @param key the key for the state value
     * @param value the value to store
     * @param <T> the type of the value
     * @throws IllegalArgumentException if the key is null
     */
    @SuppressWarnings("unchecked")
    public <T> void set(String key, T value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        Property<Object> property = state.computeIfAbsent(key, 
            k -> new Property<>(null));

        Object oldValue = property.get();

        // Only set and notify if the value has changed
        if (oldValue != value && (oldValue == null || !oldValue.equals(value))) {
            property.set(value);

            // Notify listeners
            if (listeners.containsKey(key)) {
                listeners.get(key).values().forEach(listener -> 
                    listener.accept(value));
            }
        }
    }

    /**
     * Gets a value from the state store.
     * 
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return the value, or null if not found
     * @throws IllegalArgumentException if the key is null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        Property<Object> property = state.get(key);
        return property != null ? (T) property.get() : null;
    }

    /**
     * Gets a property from the state store.
     * 
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return the property, or a new property if not found
     * @throws IllegalArgumentException if the key is null
     */
    @SuppressWarnings("unchecked")
    public <T> Property<T> getProperty(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        Property<Object> property = state.computeIfAbsent(key, 
            k -> new Property<>(null));
        return (Property<T>) property;
    }

    /**
     * Registers a listener for changes to a specific state value.
     * 
     * @param key the key for the state value
     * @param owner the owner object (used for unregistering)
     * @param listener the listener to call when the value changes
     * @param <T> the expected type of the value
     * @throws IllegalArgumentException if the key, owner, or listener is null
     */
    @SuppressWarnings("unchecked")
    public <T> void listen(String key, Object owner, Consumer<T> listener) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (owner == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        Map<Object, Consumer<Object>> keyListeners = listeners.computeIfAbsent(key, 
            k -> new ConcurrentHashMap<>());

        keyListeners.put(owner, (Consumer<Object>) listener);

        // Call the listener with the current value if it exists
        Property<Object> property = state.get(key);
        if (property != null) {
            Object value = property.get();
            if (value != null) {
                listener.accept((T) value);
            }
        }
    }

    /**
     * Unregisters a listener for a specific state value.
     * 
     * @param key the key for the state value
     * @param owner the owner object used when registering
     * @throws IllegalArgumentException if the key or owner is null
     */
    public void unlisten(String key, Object owner) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (owner == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }

        if (listeners.containsKey(key)) {
            listeners.get(key).remove(owner);
            if (listeners.get(key).isEmpty()) {
                listeners.remove(key);
            }
        }
    }

    /**
     * Updates a value in the state store using a function.
     * 
     * @param key the key for the state value
     * @param updater the function to update the value
     * @param <T> the type of the value
     * @throws IllegalArgumentException if the key or updater is null
     */
    @SuppressWarnings("unchecked")
    public <T> void update(String key, Function<T, T> updater) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (updater == null) {
            throw new IllegalArgumentException("Updater function cannot be null");
        }

        // Get the property directly to avoid an extra lookup
        Property<Object> property = state.get(key);
        if (property == null) {
            // If the property doesn't exist, create it and set the updated value
            T newValue = updater.apply(null);
            if (newValue != null) {
                set(key, newValue);
            }
        } else {
            // If the property exists, update its value
            T currentValue = (T) property.get();
            T newValue = updater.apply(currentValue);

            // Only set if the value has changed
            if (currentValue != newValue && (currentValue == null || !currentValue.equals(newValue))) {
                property.set(newValue);

                // Notify listeners
                if (listeners.containsKey(key)) {
                    listeners.get(key).values().forEach(listener -> 
                        listener.accept(newValue));
                }
            }
        }
    }

    /**
     * Clears all state values and listeners.
     * This method removes all values from the state store and unregisters all listeners.
     * Use with caution as it will remove all application state.
     * Consider using this method only for testing or when completely resetting the application.
     */
    public void clear() {
        state.clear();
        listeners.clear();
    }

    // --- Type-safe API using StateKey ---

    /**
     * Sets a value in the state store using a type-safe key.
     * 
     * @param key   the type-safe key for the state value
     * @param value the value to store
     * @param <T>   the type of the value
     * @throws IllegalArgumentException if the key is null
     */
    public <T> void setTyped(StateKey<T> key, T value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        set(key.getKey(), value);
    }

    /**
     * Gets a value from the state store using a type-safe key.
     * 
     * @param key the type-safe key for the state value
     * @param <T> the expected type of the value
     * @return the value, or null if not found
     * @throws IllegalArgumentException if the key is null
     */
    @SuppressWarnings("unchecked")
    public <T> T getTyped(StateKey<T> key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        Object value = get(key.getKey());
        if (value == null) {
            return null;
        }

        // Verify the type before casting
        if (key.getType().isInstance(value)) {
            return (T) value;
        } else {
            throw new ClassCastException("Value for key '" + key.getKey() + 
                "' is not of expected type " + key.getType().getName() + 
                ", but is " + value.getClass().getName());
        }
    }

    /**
     * Gets a property from the state store using a type-safe key.
     * 
     * @param key the type-safe key for the state value
     * @param <T> the expected type of the value
     * @return the property, or a new property if not found
     * @throws IllegalArgumentException if the key is null
     */
    @SuppressWarnings("unchecked")
    public <T> Property<T> getPropertyTyped(StateKey<T> key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        return getProperty(key.getKey());
    }

    /**
     * Registers a listener for changes to a specific state value using a type-safe key.
     * 
     * @param key      the type-safe key for the state value
     * @param owner    the owner object (used for unregistering)
     * @param listener the listener to call when the value changes
     * @param <T>      the expected type of the value
     * @throws IllegalArgumentException if the key, owner, or listener is null
     */
    public <T> void listenTyped(StateKey<T> key, Object owner, Consumer<T> listener) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        listen(key.getKey(), owner, listener);
    }

    /**
     * Unregisters a listener for a specific state value using a type-safe key.
     * 
     * @param key   the type-safe key for the state value
     * @param owner the owner object used when registering
     * @param <T>   the type of the value
     * @throws IllegalArgumentException if the key or owner is null
     */
    public <T> void unlistenTyped(StateKey<T> key, Object owner) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        unlisten(key.getKey(), owner);
    }

    /**
     * Updates a value in the state store using a function and a type-safe key.
     * 
     * @param key     the type-safe key for the state value
     * @param updater the function to update the value
     * @param <T>     the type of the value
     * @throws IllegalArgumentException if the key or updater is null
     */
    public <T> void updateTyped(StateKey<T> key, Function<T, T> updater) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        update(key.getKey(), updater);
    }

    // --- Nested Property API ---

    /**
     * Creates a nested property for the specified key.
     * <p>
     * A nested property allows for accessing and updating nested properties of complex objects
     * using dot notation (e.g., "user.address.city").
     *
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return a nested property
     * @throws IllegalArgumentException if the key is null
     */
    @SuppressWarnings("unchecked")
    public <T> NestedProperty<T> getNestedProperty(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        Property<Object> property = state.get(key);
        if (property instanceof NestedProperty) {
            return (NestedProperty<T>) property;
        }

        // Create a new nested property with the current value
        T value = property != null ? (T) property.get() : null;
        NestedProperty<T> nestedProperty = new NestedProperty<>(value);

        // Replace the existing property with the nested property
        state.put(key, (Property<Object>) (Property<?>) nestedProperty);

        return nestedProperty;
    }

    /**
     * Creates a nested property for the specified key.
     * <p>
     * A nested property allows for accessing and updating nested properties of complex objects
     * using dot notation (e.g., "user.address.city").
     *
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return a nested property
     * @throws IllegalArgumentException if the key is null
     */
    @SuppressWarnings("unchecked")
    public <T> NestedProperty<T> getNestedPropertyTyped(StateKey<T> key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        return getNestedProperty(key.getKey());
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
     * @throws IllegalArgumentException if the key or schema is null
     */
    public <T> void registerSchema(String key, StateSchema<T> schema) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (schema == null) {
            throw new IllegalArgumentException("Schema cannot be null");
        }
        schemas.put(key, schema);
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
     * @throws IllegalArgumentException if the key or schema is null
     */
    public <T> void registerSchema(StateKey<T> key, StateSchema<T> schema) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        registerSchema(key.getKey(), schema);
    }

    /**
     * Gets the schema for the specified key.
     *
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return the schema, or null if no schema is registered for the key
     * @throws IllegalArgumentException if the key is null
     */
    @SuppressWarnings("unchecked")
    public <T> StateSchema<T> getSchema(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        return (StateSchema<T>) schemas.get(key);
    }

    /**
     * Gets the schema for the specified key.
     *
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return the schema, or null if no schema is registered for the key
     * @throws IllegalArgumentException if the key is null
     */
    public <T> StateSchema<T> getSchema(StateKey<T> key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        return getSchema(key.getKey());
    }

    /**
     * Validates the state value for the specified key against its registered schema.
     *
     * @param key the key for the state value
     * @return a validation result
     * @throws IllegalArgumentException if the key is null
     * @throws IllegalStateException    if no schema is registered for the key
     */
    public ValidationResult validate(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        StateSchema<?> schema = schemas.get(key);
        if (schema == null) {
            throw new IllegalStateException("No schema registered for key: " + key);
        }

        Object value = get(key);
        return schema.validate(value);
    }

    /**
     * Validates the state value for the specified key against its registered schema.
     *
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return a validation result
     * @throws IllegalArgumentException if the key is null
     * @throws IllegalStateException    if no schema is registered for the key
     */
    public <T> ValidationResult validate(StateKey<T> key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        return validate(key.getKey());
    }

    /**
     * Validates the state value for the specified key against the specified schema.
     *
     * @param key    the key for the state value
     * @param schema the schema to validate against
     * @param <T>    the expected type of the value
     * @return a validation result
     * @throws IllegalArgumentException if the key or schema is null
     */
    public <T> ValidationResult validate(String key, StateSchema<T> schema) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (schema == null) {
            throw new IllegalArgumentException("Schema cannot be null");
        }

        Object value = get(key);
        return schema.validate(value);
    }

    /**
     * Validates the state value for the specified key against the specified schema.
     *
     * @param key    the key for the state value
     * @param schema the schema to validate against
     * @param <T>    the expected type of the value
     * @return a validation result
     * @throws IllegalArgumentException if the key or schema is null
     */
    public <T> ValidationResult validate(StateKey<T> key, StateSchema<T> schema) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        return validate(key.getKey(), schema);
    }

    /**
     * Validates all state values against their registered schemas.
     *
     * @return a map of validation results, keyed by state key
     */
    public Map<String, ValidationResult> validateAll() {
        Map<String, ValidationResult> results = new HashMap<>();
        for (String key : schemas.keySet()) {
            results.put(key, validate(key));
        }
        return results;
    }
}
