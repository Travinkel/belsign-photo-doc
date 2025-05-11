package com.belman.bootstrap.platform;

import com.gluonhq.attach.storage.StorageService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to override the default StorageService.create() method.
 * This class uses reflection to replace the default implementation with our fallback implementation.
 */
public class StorageServiceOverride {
    private static final Logger LOGGER = Logger.getLogger(StorageServiceOverride.class.getName());
    private static boolean initialized = false;

    /**
     * Initializes the StorageService override.
     * This method uses reflection to replace the default implementation with our fallback implementation.
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        try {
            // Get our fallback implementation
            StorageService fallbackService = StorageServiceFactory.getStorageService().orElseThrow();

            // Use reflection to get the StorageService class
            Class<?> storageServiceClass = StorageService.class;

            // Try to find a way to override the create() method
            try {
                // Look for a field that might hold the service instance
                Field instanceField = findInstanceField(storageServiceClass);
                if (instanceField != null) {
                    // Make the field accessible
                    instanceField.setAccessible(true);

                    // Set the field to our fallback implementation
                    instanceField.set(null, fallbackService);

                    LOGGER.info("Successfully overrode StorageService instance");
                    initialized = true;
                    return;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to override StorageService instance field", e);
            }

            // If we couldn't find an instance field, try to find a method that might set the service instance
            try {
                // Look for a method that might set the service instance
                Method setInstanceMethod = findSetInstanceMethod(storageServiceClass);
                if (setInstanceMethod != null) {
                    // Make the method accessible
                    setInstanceMethod.setAccessible(true);

                    // Call the method with our fallback implementation
                    setInstanceMethod.invoke(null, fallbackService);

                    LOGGER.info("Successfully overrode StorageService instance via method");
                    initialized = true;
                    return;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to override StorageService instance via method", e);
            }

            LOGGER.warning("Could not find a way to override StorageService.create()");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize StorageService override", e);
        }
    }

    /**
     * Finds a field that might hold the service instance.
     *
     * @param clazz the class to search
     * @return the field, or null if not found
     */
    private static Field findInstanceField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().equals(StorageService.class) ||
                field.getType().equals(Optional.class)) {
                return field;
            }
        }
        return null;
    }

    /**
     * Finds a method that might set the service instance.
     *
     * @param clazz the class to search
     * @return the method, or null if not found
     */
    private static Method findSetInstanceMethod(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getParameterCount() == 1 &&
                (method.getParameterTypes()[0].equals(StorageService.class) ||
                 method.getParameterTypes()[0].equals(Optional.class))) {
                return method;
            }
        }
        return null;
    }
}