package com.belman.belsign.framework.athomefx.state;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * A global state management store for the application.
 * Inspired by Vuex/Pinia from Vue.js, this class provides a centralized
 * store for managing application state with reactive properties.
 */
public class StateStore {
    private static final StateStore instance = new StateStore();
    
    // The actual state storage
    private final ObservableMap<String, ObjectProperty<Object>> state;
    
    // Listeners for state changes
    private final Map<String, Map<Object, Consumer<Object>>> listeners;
    
    // Private constructor for singleton
    private StateStore() {
        this.state = FXCollections.observableHashMap();
        this.listeners = new ConcurrentHashMap<>();
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
     */
    @SuppressWarnings("unchecked")
    public <T> void set(String key, T value) {
        ObjectProperty<Object> property = state.computeIfAbsent(key, 
            k -> new SimpleObjectProperty<>(null));
        
        Object oldValue = property.get();
        property.set(value);
        
        // Notify listeners
        if (listeners.containsKey(key)) {
            listeners.get(key).values().forEach(listener -> 
                listener.accept(value));
        }
    }
    
    /**
     * Gets a value from the state store.
     * 
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return the value, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        ObjectProperty<Object> property = state.get(key);
        return property != null ? (T) property.get() : null;
    }
    
    /**
     * Gets a property from the state store that can be bound to UI elements.
     * 
     * @param key the key for the state value
     * @param <T> the expected type of the value
     * @return the property, or a new property if not found
     */
    @SuppressWarnings("unchecked")
    public <T> ObjectProperty<T> getProperty(String key) {
        ObjectProperty<Object> property = state.computeIfAbsent(key, 
            k -> new SimpleObjectProperty<>(null));
        return (ObjectProperty<T>) property;
    }
    
    /**
     * Registers a listener for changes to a specific state value.
     * 
     * @param key the key for the state value
     * @param owner the owner object (used for unregistering)
     * @param listener the listener to call when the value changes
     * @param <T> the expected type of the value
     */
    @SuppressWarnings("unchecked")
    public <T> void listen(String key, Object owner, Consumer<T> listener) {
        Map<Object, Consumer<Object>> keyListeners = listeners.computeIfAbsent(key, 
            k -> new ConcurrentHashMap<>());
        
        keyListeners.put(owner, (Consumer<Object>) listener);
        
        // Call the listener with the current value if it exists
        ObjectProperty<Object> property = state.get(key);
        if (property != null && property.get() != null) {
            listener.accept((T) property.get());
        }
    }
    
    /**
     * Unregisters a listener for a specific state value.
     * 
     * @param key the key for the state value
     * @param owner the owner object used when registering
     */
    public void unlisten(String key, Object owner) {
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
     */
    @SuppressWarnings("unchecked")
    public <T> void update(String key, Function<T, T> updater) {
        T currentValue = get(key);
        T newValue = updater.apply(currentValue);
        set(key, newValue);
    }
    
    /**
     * Clears all state values.
     */
    public void clear() {
        state.clear();
        listeners.clear();
    }
}