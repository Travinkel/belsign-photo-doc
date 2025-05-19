package com.belman.bootstrap.di;


import com.belman.bootstrap.config.DevModeConfig;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.application.base.BaseService;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Registry for services.
 * Provides methods for registering services with the ServiceLocator.
 */
public class ServiceRegistry {
    private static Logger logger;

    /**
     * Gets all interfaces implemented by a class, including those implemented by its superinterfaces.
     *
     * @param clazz the class to get interfaces for
     * @return a set of all interfaces implemented by the class
     */
    private static Set<Class<?>> getAllInterfaces(Class<?> clazz) {
        Set<Class<?>> interfaces = new HashSet<>();

        // Add direct interfaces
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            interfaces.add(interfaceClass);

            // Add interfaces implemented by this interface
            interfaces.addAll(getAllInterfaces(interfaceClass));
        }

        // Add interfaces from superclass
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && !superclass.equals(Object.class)) {
            interfaces.addAll(getAllInterfaces(superclass));
        }

        return interfaces;
    }

    /**
     * Sets the logger for this class.
     * This method should be called before using any methods in this class.
     *
     * @param loggerFactory the factory to create loggers
     */
    public static void setLogger(LoggerFactory loggerFactory) {
        if (loggerFactory == null) {
            throw new IllegalArgumentException("LoggerFactory cannot be null");
        }
        logger = loggerFactory.getLogger(ServiceRegistry.class);
    }

    /**
     * Registers all the specified services with the ServiceLocator.
     *
     * @param services the services to register
     */
    public static void registerAll(Object... services) {
        checkLogger();
        if (services == null || services.length == 0) {
            if (logger != null) {
                logger.warn("No services provided to registerAll");
            }
            return;
        }

        registerAll(Arrays.asList(services));
    }

    /**
     * Checks if the logger has been initialized.
     * If not, logs a warning to System.err.
     */
    private static void checkLogger() {
        if (logger == null) {
            System.err.println("Warning: ServiceRegistry logger not initialized. Call setLogger() first.");
        }
    }

    /**
     * Registers all the specified services with the ServiceLocator.
     *
     * @param services the services to register
     */
    public static void registerAll(Collection<?> services) {
        checkLogger();
        if (services == null || services.isEmpty()) {
            if (logger != null) {
                logger.warn("No services provided to registerAll");
            }
            return;
        }

        if (logger != null) {
            logger.info("Registering {} services", services.size());
        }

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
        checkLogger();
        if (service == null) {
            if (logger != null) {
                logger.warn("Null service provided to registerService");
            }
            return;
        }

        Class<?> serviceClass = service.getClass();
        if (logger != null) {
            if (DevModeConfig.isDevMode()) {
                // More detailed logging in dev mode
                logger.info("🔌 Registering service: {}", serviceClass.getName());
            } else {
                logger.debug("Registering service: {}", serviceClass.getName());
            }
        }

        // Register the service under its class
        try {
            ServiceLocator.registerService((Class<Object>) serviceClass, service);
            if (logger != null) {
                logger.debug("Registered service under class: {}", serviceClass.getName());
            }
        } catch (Exception e) {
            if (logger != null) {
                logger.warn("Failed to register service under class: {}", serviceClass.getName(), e);
            }
        }

        // Register the service under all interfaces it implements, including those implemented by its superinterfaces
        for (Class<?> interfaceClass : getAllInterfaces(serviceClass)) {
            // Skip registration for Repository interface to avoid conflicts with multiple repository implementations
            if (interfaceClass.getName().equals("com.belman.domain.common.base.Repository")) {
                if (logger != null) {
                    logger.debug("Skipping registration for Repository interface to avoid conflicts");
                }
                continue;
            }

            try {
                ServiceLocator.registerService((Class<Object>) interfaceClass, service);
                if (logger != null) {
                    logger.debug("Registered service under interface: {}", interfaceClass.getName());
                }
            } catch (Exception e) {
                if (logger != null) {
                    logger.warn("Failed to register service under interface: {}", interfaceClass.getName(), e);
                }
            }
        }

        // If the service is a BaseService, inject services into it
        if (service instanceof BaseService) {
            try {
                ServiceLocator.injectServices(service);
                if (logger != null) {
                    logger.debug("Injected services into: {}", serviceClass.getName());
                }
            } catch (Exception e) {
                if (logger != null) {
                    logger.warn("Failed to inject services into: {}", serviceClass.getName(), e);
                }
            }
        }
    }

    /**
     * Unregisters all services from the ServiceLocator.
     */
    public static void unregisterAll() {
        checkLogger();
        if (logger != null) {
            logger.info("Unregistering all services");
        }
        ServiceLocator.clear();
    }
}
