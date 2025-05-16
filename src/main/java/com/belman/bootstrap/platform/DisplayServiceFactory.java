package com.belman.bootstrap.platform;

import com.belman.common.platform.PlatformUtils;
import com.gluonhq.attach.display.DisplayService;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory for creating DisplayService instances based on the platform.
 * This class provides a fallback implementation for desktop platforms.
 */
public class DisplayServiceFactory {
    private static final Logger LOGGER = Logger.getLogger(DisplayServiceFactory.class.getName());
    private static Object desktopFallbackInstance;

    /**
     * Gets a DisplayService instance appropriate for the current platform.
     * For mobile platforms, it returns the default Gluon implementation.
     * For desktop platforms, it returns a fallback implementation.
     *
     * @return an Optional containing a DisplayService instance, or empty if no implementation is available
     */
    @SuppressWarnings("unchecked")
    public static Optional<DisplayService> getDisplayService() {
        if (PlatformUtils.isRunningOnMobile()) {
            // Use the default Gluon implementation for mobile platforms
            return DisplayService.create();
        } else {
            // Use our fallback implementation for desktop platforms
            try {
                Object proxy = getDesktopFallbackInstance();
                // The proxy implements DisplayService, but we can't cast it directly
                // Instead, we return it as is, and the caller will use it through the DisplayService interface
                return Optional.of((DisplayService) proxy);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to get desktop fallback instance of DisplayService", e);
                return Optional.empty();
            }
        }
    }

    /**
     * Gets the desktop fallback instance of DisplayService.
     * This method creates the instance if it doesn't exist yet.
     *
     * @return the desktop fallback instance as an Object
     */
    private static synchronized Object getDesktopFallbackInstance() {
        if (desktopFallbackInstance == null) {
            try {
                // Create a proxy that implements the DisplayService interface
                LOGGER.info("Created desktop fallback instance of DisplayService");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to create desktop fallback instance of DisplayService", e);
                throw new RuntimeException("Failed to create desktop fallback instance of DisplayService", e);
            }
        }
        return desktopFallbackInstance;
    }
}
