package com.belman.bootstrap.platform;

import com.belman.common.platform.PlatformUtils;
import com.gluonhq.attach.display.DisplayService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Dimension2D;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory for creating DisplayService instances based on the platform.
 * This class provides a fallback implementation for desktop platforms.
 * 
 * IMPORTANT: DisplayService is primarily designed for mobile platforms.
 * When running on desktop, many features will not be available or will return default values.
 * 
 * Desktop limitations:
 * - Screen dimensions will be fixed default values
 * - Platform detection (isTablet, isPhone) will return fixed values
 * - DPI and resolution information will be estimates
 * 
 * Always check if you're running on a mobile platform before using platform-specific features:
 * 
 * <pre>
 * if (PlatformUtils.isRunningOnMobile()) {
 *     // Use mobile-specific features
 * } else {
 *     // Use desktop fallback
 * }
 * </pre>
 */
public class DisplayServiceFactory {
    private static final Logger LOGGER = Logger.getLogger(DisplayServiceFactory.class.getName());
    private static DisplayService desktopFallbackInstance;

    // Default values for desktop fallback
    private static final double DEFAULT_SCREEN_WIDTH = 360.0;  // Default width for mobile-like display
    private static final double DEFAULT_SCREEN_HEIGHT = 640.0; // Default height for mobile-like display
    private static final double DEFAULT_DPI = 160.0;           // Default DPI for standard density

    /**
     * Gets a DisplayService instance appropriate for the current platform.
     * For mobile platforms, it returns the default Gluon implementation.
     * For desktop platforms, it returns a fallback implementation.
     *
     * @return an Optional containing a DisplayService instance, or empty if no implementation is available
     */
    public static Optional<DisplayService> getDisplayService() {
        if (PlatformUtils.isRunningOnMobile()) {
            // Use the default Gluon implementation for mobile platforms
            return DisplayService.create();
        } else {
            // Use our fallback implementation for desktop platforms
            try {
                DisplayService proxy = getDesktopFallbackInstance();
                return Optional.of(proxy);
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
     * @return the desktop fallback instance
     */
    private static synchronized DisplayService getDesktopFallbackInstance() {
        if (desktopFallbackInstance == null) {
            try {
                // Create a dynamic proxy that implements the DisplayService interface
                desktopFallbackInstance = (DisplayService) Proxy.newProxyInstance(
                    DisplayService.class.getClassLoader(),
                    new Class<?>[] { DisplayService.class },
                    new DisplayServiceInvocationHandler()
                );
                LOGGER.info("Created desktop fallback instance of DisplayService using dynamic proxy");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to create desktop fallback instance of DisplayService", e);
                throw new RuntimeException("Failed to create desktop fallback instance of DisplayService", e);
            }
        }
        return desktopFallbackInstance;
    }

    /**
     * Invocation handler for the DisplayService proxy.
     * This class handles method calls to the DisplayService interface and provides default implementations.
     */
    private static class DisplayServiceInvocationHandler implements InvocationHandler {
        private final BooleanProperty notchProperty = new SimpleBooleanProperty(false);

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();

            // Handle specific methods with default implementations
            switch (methodName) {
                case "getDefaultDimensions":
                    return new Dimension2D(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);
                case "getScreenResolution":
                    return new Dimension2D(DEFAULT_DPI, DEFAULT_DPI);
                case "hasNotch":
                    return false;
                case "isScreenRound":
                    return false;
                case "getScreenWidth":
                    return DEFAULT_SCREEN_WIDTH;
                case "getScreenHeight":
                    return DEFAULT_SCREEN_HEIGHT;
                case "getScreenScale":
                    return 1.0f;
                case "isTablet":
                    return false;
                case "isPhone":
                    return true;
                case "isDesktop":
                    return false;
                case "getDpi":
                    return DEFAULT_DPI;
                case "notchProperty":
                    // Return the notchProperty - this will be handled by the proxy
                    return notchProperty;
                case "toString":
                    return "DisplayService Desktop Fallback (Dynamic Proxy)";
                default:
                    // For any other methods, return null or default values
                    Class<?> returnType = method.getReturnType();
                    if (returnType.equals(boolean.class)) {
                        return false;
                    } else if (returnType.equals(int.class)) {
                        return 0;
                    } else if (returnType.equals(double.class)) {
                        return 0.0;
                    } else if (returnType.equals(float.class)) {
                        return 0.0f;
                    } else {
                        return null;
                    }
            }
        }
    }
}
