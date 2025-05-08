package com.belman.business.richbe.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A platform-agnostic property class that can be used to store values
 * and notify listeners when the value changes.
 *
 * @param <T> the type of the value stored in this property
 */
public class Property<T> {
    private T value;
    private final List<Consumer<T>> listeners = new ArrayList<>();

    /**
     * Creates a new property with a null initial value.
     */
    public Property() {
        this(null);
    }

    /**
     * Creates a new property with the specified initial value.
     *
     * @param initialValue the initial value
     */
    public Property(T initialValue) {
        this.value = initialValue;
    }

    /**
     * Gets the current value of this property.
     *
     * @return the current value
     */
    public T get() {
        return value;
    }

    /**
     * Sets the value of this property and notifies all listeners.
     *
     * @param newValue the new value
     */
    public void set(T newValue) {
        if (value != newValue && (value == null || !value.equals(newValue))) {
            this.value = newValue;
            notifyListeners();
        }
    }

    /**
     * Adds a listener that will be notified when the value changes.
     *
     * @param listener the listener to add
     */
    public void addListener(Consumer<T> listener) {
        if (listener != null) {
            listeners.add(listener);
            if (value != null) {
                listener.accept(value);
            }
        }
    }

    /**
     * Removes a listener.
     *
     * @param listener the listener to remove
     */
    public void removeListener(Consumer<T> listener) {
        listeners.remove(listener);
    }

    /**
     * Removes all listeners.
     */
    public void clearListeners() {
        listeners.clear();
    }

    /**
     * Notifies all listeners with the current value.
     */
    private void notifyListeners() {
        for (Consumer<T> listener : listeners) {
            listener.accept(value);
        }
    }
}