package com.belman.common.di;

/**
 * Interface for service injectors that can inject dependencies into objects.
 * This interface is used to break cyclic dependencies between packages.
 */
public interface ServiceInjector {
    
    /**
     * Injects services into the target object.
     *
     * @param target the object to inject services into
     * @throws ServiceInjectionException if injection fails
     */
    void injectServices(Object target);
}