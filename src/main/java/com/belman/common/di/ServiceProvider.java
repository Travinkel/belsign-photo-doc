package com.belman.common.di;

/**
 * Interface for service providers that can retrieve registered services.
 * This interface is used to break cyclic dependencies between packages.
 */
public interface ServiceProvider {
    
    /**
     * Retrieves a registered service.
     *
     * @param serviceClass the service class
     * @return the service instance
     * @throws ServiceInjectionException if no service is registered for the given class
     */
    <T> T getService(Class<T> serviceClass);
}