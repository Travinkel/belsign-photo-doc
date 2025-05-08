package com.belman.data.service;

import com.belman.data.platform.PlatformUtils;
import com.gluonhq.attach.storage.StorageService;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory for creating StorageService instances based on the platform.
 * This class provides a fallback implementation for desktop platforms.
 */
public class StorageServiceFactory {
    private static final Logger LOGGER = Logger.getLogger(StorageServiceFactory.class.getName());
    private static StorageService desktopFallbackInstance;

    /**
     * Gets a StorageService instance appropriate for the current platform.
     * For mobile platforms, it returns the default Gluon implementation.
     * For desktop platforms, it returns a fallback implementation.
     * 
     * @return an Optional containing a StorageService instance, or empty if no implementation is available
     */
    public static Optional<StorageService> getStorageService() {
        if (PlatformUtils.isRunningOnMobile()) {
            // Use the default Gluon implementation for mobile platforms
            return StorageService.create();
        } else {
            // Use our fallback implementation for desktop platforms
            return Optional.of(getDesktopFallbackInstance());
        }
    }

    /**
     * Gets the desktop fallback instance of StorageService.
     * This method creates the instance if it doesn't exist yet.
     * 
     * @return the desktop fallback instance
     */
    private static synchronized StorageService getDesktopFallbackInstance() {
        if (desktopFallbackInstance == null) {
            try {
                desktopFallbackInstance = new DesktopStorageServiceFallback();
                LOGGER.info("Created desktop fallback instance of StorageService");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to create desktop fallback instance of StorageService", e);
                throw new RuntimeException("Failed to create desktop fallback instance of StorageService", e);
            }
        }
        return desktopFallbackInstance;
    }
}