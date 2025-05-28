package com.belman.bootstrap.di;

import com.belman.common.di.DependencyContainer;
import com.belman.common.di.ServiceProviderFactory;
import com.belman.common.logging.EmojiLogger;

/**
 * Implementation of the DependencyContainer interface that delegates to the ServiceLocator.
 * This class is used to break cyclic dependencies between packages.
 */
public class DependencyContainerImpl implements DependencyContainer {

    private static final EmojiLogger logger = EmojiLogger.getLogger(DependencyContainerImpl.class);
    private static final DependencyContainerImpl INSTANCE = new DependencyContainerImpl();

    static {
        try {
            // Register this instance with the ServiceProviderFactory
            ServiceProviderFactory.setInstance(INSTANCE);
            logger.info("DependencyContainerImpl registered with ServiceProviderFactory");
        } catch (Exception e) {
            logger.error("Failed to register DependencyContainerImpl with ServiceProviderFactory", e);
        }
    }

    private DependencyContainerImpl() {
        // Private constructor to prevent instantiation
    }

    /**
     * Gets the singleton instance of the DependencyContainerImpl.
     *
     * @return the singleton instance
     */
    public static DependencyContainerImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public <T> T getService(Class<T> serviceClass) {
        return ServiceLocator.getService(serviceClass);
    }

    @Override
    public <T> void registerService(Class<T> serviceClass, T serviceInstance) {
        ServiceLocator.registerService(serviceClass, serviceInstance);
    }

    @Override
    public <T> boolean registerServiceIfAbsent(Class<T> serviceClass, T serviceInstance) {
        return ServiceLocator.registerServiceIfAbsent(serviceClass, serviceInstance);
    }

    @Override
    public void injectServices(Object target) {
        ServiceLocator.injectServices(target);
    }

    @Override
    public void clear() {
        ServiceLocator.clear();
    }
}
