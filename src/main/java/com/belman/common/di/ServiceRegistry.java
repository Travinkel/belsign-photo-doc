package com.belman.common.di;

/**
 * Interface for service registries that can register and manage services.
 * This interface is used to break cyclic dependencies between packages.
 */
public interface ServiceRegistry {
    
    /**
     * Registers a service instance.
     *
     * @param serviceClass    the service class
     * @param serviceInstance the service instance
     * @throws IllegalArgumentException if the service instance is null
     * @throws ServiceInjectionException if a service is already registered for the given class
     */
    <T> void registerService(Class<T> serviceClass, T serviceInstance);
    
    /**
     * Registers a service instance only if it doesn't already exist.
     *
     * @param serviceClass    the service class
     * @param serviceInstance the service instance
     * @return true if the service was registered, false if it already existed
     * @throws IllegalArgumentException if the service instance is null
     */
    <T> boolean registerServiceIfAbsent(Class<T> serviceClass, T serviceInstance);
    
    /**
     * Clears all registered services.
     */
    void clear();
}