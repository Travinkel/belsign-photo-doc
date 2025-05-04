package com.belman.application.core;


import com.belman.infrastructure.platform.ServiceInjectionException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ServiceLocator for managing and injecting services.
 */
public class ServiceLocator {

    private static final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    /**
     * Registers a service instance.
     *
     * @param serviceClass    the service class
     * @param serviceInstance the service instance
     */
    public static <T> void registerService(Class<T> serviceClass, T serviceInstance) {
        if (serviceInstance == null) {
            throw new IllegalArgumentException("Service instance cannot be null for: " + serviceClass.getSimpleName());
        }
        if (services.containsKey(serviceClass)) {
            throw new ServiceInjectionException(
                    "Service already registered for: " + serviceClass.getSimpleName()
            );
        }
        services.put(serviceClass, serviceInstance);
    }

    /**
     * Injects services into the target object.
     *
     * @param target the object to inject services into
     */
    public static void injectServices(Object target) {
        // Field injection
        for (Field field : target.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                try {
                    field.setAccessible(true);
                    Object service = services.get(field.getType());
                    if (service == null) {
                        throw new ServiceInjectionException(
                                "No service registered for: " + field.getType().getSimpleName());
                    }
                    field.set(target, service);

                } catch (IllegalAccessException e) {
                    throw new ServiceInjectionException(
                            "Failed to inject service into: " + target.getClass().getSimpleName(),
                            e);
                }
            }
        }

        // Method injection
        for (Method method : target.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Inject.class)) {
                if (method.getParameterCount() > 0) {
                    throw new RuntimeException("Injected methods must have no parameters: " + method.getName());
                }
                try {
                    method.setAccessible(true);
                    method.invoke(target);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new ServiceInjectionException(
                            "Failed to inject services into: " + target.getClass().getSimpleName(),
                            e);
                }
            }
        }

        // Setter injection by convention (setXxx methods for registered services)
        for (Method method : target.getClass().getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.startsWith("set") && methodName.length() > 3 && method.getParameterCount() == 1) {
                Class<?> paramType = method.getParameterTypes()[0];
                Object service = services.get(paramType);
                if (service != null) {
                    try {
                        method.setAccessible(true);
                        method.invoke(target, service);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new ServiceInjectionException(
                                "Failed to inject service into setter: " + methodName,
                                e);
                    }
                }
            }
        }
    }

    /**
     * Retrieves a registered service.
     *
     * @param serviceClass the service class
     * @return the service instance
     */
    public static <T> T getService(Class<T> serviceClass) {
        Object service = services.get(serviceClass);
        if (service == null) {
            throw new ServiceInjectionException(
                    "No service registered for: " + serviceClass.getSimpleName());
        }
        return serviceClass.cast(service);
    }

    public static void clear() {
        services.clear();
    }
}
