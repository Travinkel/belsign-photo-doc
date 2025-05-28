package com.belman.bootstrap.di;

import com.belman.common.di.ServiceProvider;

/**
 * Default implementation of ServiceProvider that delegates to ServiceLocator.
 * This class is used to break cyclic dependencies between packages.
 */
public class DefaultServiceProvider implements ServiceProvider {

    private static final DefaultServiceProvider INSTANCE = new DefaultServiceProvider();

    /**
     * Private constructor to prevent instantiation.
     */
    private DefaultServiceProvider() {
        // Singleton
    }

    /**
     * Gets the singleton instance of DefaultServiceProvider.
     *
     * @return the singleton instance
     */
    public static DefaultServiceProvider getInstance() {
        return INSTANCE;
    }

    @Override
    public <T> T getService(Class<T> serviceClass) {
        return ServiceLocator.getService(serviceClass);
    }
}