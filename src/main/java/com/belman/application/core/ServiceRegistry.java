package com.belman.application.core;


import com.belman.presentation.core.BaseService;
import com.belman.infrastructure.EmojiLogger;

import java.util.Arrays;
import java.util.Collection;

/**
 * Registry for services.
 * Provides methods for registering services with the ServiceLocator.
 */
public class ServiceRegistry {
    private static final EmojiLogger logger = EmojiLogger.getLogger(ServiceRegistry.class);

    /**
     * Registers all the specified services with the ServiceLocator.
     * 
     * @param services the services to register
     */
    public static void registerAll(Object... services) {
        if (services == null || services.length == 0) {
            logger.warn("No services provided to registerAll");
            return;
        }

        registerAll(Arrays.asList(services));
    }

    /**
     * Registers all the specified services with the ServiceLocator.
     * 
     * @param services the services to register
     */
    public static void registerAll(Collection<?> services) {
        if (services == null || services.isEmpty()) {
            logger.warn("No services provided to registerAll");
            return;
        }

        logger.info("Registering {} services", services.size());

        for (Object service : services) {
            registerService(service);
        }
    }

    /**
     * Registers a service with the ServiceLocator.
     * The service is registered under its class and all interfaces it implements.
     * 
     * @param service the service to register
     */
    @SuppressWarnings("unchecked")
    public static void registerService(Object service) {
        if (service == null) {
            logger.warn("Null service provided to registerService");
            return;
        }

        Class<?> serviceClass = service.getClass();
        logger.debug("Registering service: {}", serviceClass.getName());

        // Register the service under its class
        try {
            ServiceLocator.registerService((Class<Object>) serviceClass, service);
            logger.debug("Registered service under class: {}", serviceClass.getName());
        } catch (Exception e) {
            logger.warn("Failed to register service under class: {}", serviceClass.getName(), e);
        }

        // Register the service under all interfaces it implements
        for (Class<?> interfaceClass : serviceClass.getInterfaces()) {
            try {
                ServiceLocator.registerService((Class<Object>) interfaceClass, service);
                logger.debug("Registered service under interface: {}", interfaceClass.getName());
            } catch (Exception e) {
                logger.warn("Failed to register service under interface: {}", interfaceClass.getName(), e);
            }
        }

        // If the service is a BaseService, inject services into it
        if (service instanceof BaseService) {
            try {
                ServiceLocator.injectServices(service);
                logger.debug("Injected services into: {}", serviceClass.getName());
            } catch (Exception e) {
                logger.warn("Failed to inject services into: {}", serviceClass.getName(), e);
            }
        }
    }

    /**
     * Unregisters all services from the ServiceLocator.
     */
    public static void unregisterAll() {
        logger.info("Unregistering all services");
        ServiceLocator.clear();
    }
}
