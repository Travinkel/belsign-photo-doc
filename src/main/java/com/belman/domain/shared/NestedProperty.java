package com.belman.domain.shared;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A property that supports nested properties for complex objects.
 * <p>
 * This class extends the Property class to support complex objects with nested properties.
 * It allows for accessing and updating nested properties using dot notation (e.g., "user.address.city").
 *
 * @param <T> the type of the root object
 */
public class NestedProperty<T> extends Property<T> {

    /**
     * Creates a new nested property with a null initial value.
     */
    public NestedProperty() {
        super();
    }

    /**
     * Creates a new nested property with the specified initial value.
     *
     * @param initialValue the initial value
     */
    public NestedProperty(T initialValue) {
        super(initialValue);
    }

    /**
     * Sets a nested property value using dot notation.
     *
     * @param path  the path to the nested property (e.g., "user.address.city")
     * @param value the value to set
     * @param <V>   the type of the nested property
     * @throws IllegalArgumentException if the path is null or empty
     * @throws IllegalStateException    if the parent object is null or not a map or bean
     */
    public <V> void setNestedValue(String path, V value) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        T rootValue = get();
        if (rootValue == null) {
            throw new IllegalStateException("Cannot set nested property on null object");
        }

        String[] parts = path.split("\\.");
        Object parent = rootValue;

        // Navigate to the parent object
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            Object child = getPropertyValue(parent, part);

            if (child == null) {
                // Try to create a new object if the parent is a map
                if (parent instanceof Map) {
                    child = new java.util.HashMap<>();
                    ((Map<String, Object>) parent).put(part, child);
                } else {
                    throw new IllegalStateException(
                            "Cannot set nested property: parent object is null at path: " + part);
                }
            }

            parent = child;
        }

        // Set the value on the parent object
        String lastPart = parts[parts.length - 1];
        setPropertyValue(parent, lastPart, value);

        // Notify listeners of the root object change
        set(rootValue);
    }

    /**
     * Gets a property value from an object.
     *
     * @param obj          the object to get the property from
     * @param propertyName the name of the property
     * @return the property value, or null if not found
     */
    @SuppressWarnings("unchecked")
    private Object getPropertyValue(Object obj, String propertyName) {
        if (obj == null) {
            return null;
        }

        // Handle maps
        if (obj instanceof Map) {
            return ((Map<String, Object>) obj).get(propertyName);
        }

        // Handle lists and arrays with numeric indices
        if (obj instanceof List && propertyName.matches("\\d+")) {
            int index = Integer.parseInt(propertyName);
            List<Object> list = (List<Object>) obj;
            if (index >= 0 && index < list.size()) {
                return list.get(index);
            }
            return null;
        }

        // Handle JavaBean properties using reflection
        try {
            // Try to find a getter method
            String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            java.lang.reflect.Method getter = obj.getClass().getMethod(getterName);
            return getter.invoke(obj);
        } catch (Exception e) {
            try {
                // Try to find a boolean getter method (isXxx)
                String isGetterName = "is" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
                java.lang.reflect.Method isGetter = obj.getClass().getMethod(isGetterName);
                return isGetter.invoke(obj);
            } catch (Exception e2) {
                try {
                    // Try to access the field directly
                    java.lang.reflect.Field field = obj.getClass().getDeclaredField(propertyName);
                    field.setAccessible(true);
                    return field.get(obj);
                } catch (Exception e3) {
                    // Property not found
                    return null;
                }
            }
        }
    }

    /**
     * Sets a property value on an object.
     *
     * @param obj          the object to set the property on
     * @param propertyName the name of the property
     * @param value        the value to set
     */
    @SuppressWarnings("unchecked")
    private void setPropertyValue(Object obj, String propertyName, Object value) {
        if (obj == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }

        // Handle maps
        if (obj instanceof Map) {
            ((Map<String, Object>) obj).put(propertyName, value);
            return;
        }

        // Handle lists and arrays with numeric indices
        if (obj instanceof List && propertyName.matches("\\d+")) {
            int index = Integer.parseInt(propertyName);
            List<Object> list = (List<Object>) obj;

            // Ensure the list is large enough
            while (list.size() <= index) {
                list.add(null);
            }

            list.set(index, value);
            return;
        }

        // Handle JavaBean properties using reflection
        try {
            // Try to find a setter method
            String setterName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            for (java.lang.reflect.Method method : obj.getClass().getMethods()) {
                if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                    method.invoke(obj, value);
                    return;
                }
            }

            // Try to access the field directly
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot set property: " + propertyName, e);
        }
    }

    /**
     * Adds a listener for changes to a nested property.
     *
     * @param path     the path to the nested property (e.g., "user.address.city")
     * @param listener the listener to add
     * @param <V>      the expected type of the nested property
     * @throws IllegalArgumentException if the path is null or empty
     */
    @SuppressWarnings("unchecked")
    public <V> void addNestedListener(String path, Consumer<V> listener) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        // Add a listener to the root property
        addListener(rootValue -> {
            // Get the nested value and notify the listener
            V nestedValue = getNestedValue(path);
            listener.accept(nestedValue);
        });
    }

    /**
     * Gets a nested property value using dot notation.
     *
     * @param path the path to the nested property (e.g., "user.address.city")
     * @param <V>  the expected type of the nested property
     * @return the nested property value, or null if not found
     * @throws IllegalArgumentException if the path is null or empty
     */
    @SuppressWarnings("unchecked")
    public <V> V getNestedValue(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        Object value = get();
        if (value == null) {
            return null;
        }

        String[] parts = path.split("\\.");
        for (String part : parts) {
            value = getPropertyValue(value, part);
            if (value == null) {
                return null;
            }
        }

        return (V) value;
    }
}