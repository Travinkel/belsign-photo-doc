package com.belman.bootstrap.di;


import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.application.base.BaseService;

import java.util.Arrays;
import java.util.Collection;

/**
 * Registry for services.
 * Provides methods for registering services with the ServiceLocator.
 */
public class ServiceRegistry {
    private static Logger logger;

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
            logger.debug("Registering service: {}", serviceClass.getName());
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

        // Register the service under all interfaces it implements
        for (Class<?> interfaceClass : serviceClass.getInterfaces()) {
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
