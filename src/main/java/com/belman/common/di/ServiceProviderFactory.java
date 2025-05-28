package com.belman.common.di;

import java.lang.reflect.Method;

/**
 * Factory for creating ServiceProvider instances.
 * This class is used to break cyclic dependencies between packages.
 */
public class ServiceProviderFactory {

    private static ServiceProvider instance;

    /**
     * Private constructor to prevent instantiation.
     */
    private ServiceProviderFactory() {
        // Utility class
    }

    /**
     * Sets the ServiceProvider instance.
     * This method should be called during application initialization.
     *
     * @param serviceProvider the ServiceProvider instance
     */
    public static void setInstance(ServiceProvider serviceProvider) {
        if (serviceProvider == null) {
            throw new IllegalArgumentException("ServiceProvider instance cannot be null");
        }
        instance = serviceProvider;
    }

    /**
     * Gets the ServiceProvider instance.
     *
     * @return the ServiceProvider instance
     * @throws IllegalStateException if the ServiceProvider instance has not been set
     */
    public static ServiceProvider getInstance() {
        if (instance == null) {
            // The DependencyContainerImpl will register itself with this factory
            // during its static initialization, so we just need to check again
            // after a small delay to see if it has been registered
            try {
                Thread.sleep(100); // Give the DependencyContainerImpl time to register
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (instance == null) {
                throw new IllegalStateException("ServiceProvider instance has not been set. Call setInstance() first.");
            }
        }
        return instance;
    }
}
