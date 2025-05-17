package com.belman.presentation.di;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of the Container interface.
 * This is part of the Dependency Injection pattern.
 */
public class SimpleContainer implements Container {
    private final Map<Class<?>, Object> instances = new HashMap<>();
    private final Map<Class<?>, Provider<?>> providers = new HashMap<>();

    /**
     * Creates a new SimpleContainer.
     */
    public SimpleContainer() {
        // Register this container as a dependency
        register(Container.class, this);
    }

    @Override
    public <T> void register(Class<T> type, T implementation) {
        instances.put(type, implementation);
    }

    @Override
    public <T> void register(Class<T> type, Provider<T> provider) {
        providers.put(type, provider);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> type) {
        // Check if an instance is already registered
        if (instances.containsKey(type)) {
            return (T) instances.get(type);
        }

        // Check if a provider is registered
        if (providers.containsKey(type)) {
            Provider<T> provider = (Provider<T>) providers.get(type);
            T instance = provider.get();

            // Cache the instance for future use
            instances.put(type, instance);

            return instance;
        }

        throw new IllegalArgumentException("No implementation or provider registered for type: " + type.getName());
    }

    @Override
    public <T> boolean contains(Class<T> type) {
        return instances.containsKey(type) || providers.containsKey(type);
    }
}