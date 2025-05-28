package com.belman.common.di;

/**
 * Combined interface for dependency injection functionality.
 * This interface extends ServiceProvider, ServiceRegistry, and ServiceInjector
 * to provide a single interface for all dependency injection functionality.
 * This interface is used to break cyclic dependencies between packages.
 */
public interface DependencyContainer extends ServiceProvider, ServiceRegistry, ServiceInjector {
    // No additional methods needed, as all functionality is inherited from the extended interfaces
}