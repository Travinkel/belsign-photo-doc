package com.belman.data.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A fallback implementation of DisplayService for desktop platforms.
 * This class uses reflection to create a proxy that implements the DisplayService interface.
 */
public class DesktopDisplayServiceFallback {
    private static final Logger LOGGER = Logger.getLogger(DesktopDisplayServiceFallback.class.getName());

    // Default values for desktop platforms
    private static final float DEFAULT_SCREEN_SCALE = 1.0f;
    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 800;

    /**
     * Creates a new DisplayService proxy that provides default values for desktop platforms.
     * 
     * @return a proxy that implements the DisplayService interface
     */
    public static Object createDisplayServiceProxy() {
        try {
            // Load the DisplayService interface
            Class<?> displayServiceClass = Class.forName("com.gluonhq.attach.display.DisplayService");

            // Create a proxy that implements the DisplayService interface
            return Proxy.newProxyInstance(
                DesktopDisplayServiceFallback.class.getClassLoader(),
                new Class<?>[] { displayServiceClass },
                new DisplayServiceInvocationHandler()
            );
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Failed to load DisplayService class", e);
            throw new RuntimeException("Failed to create DisplayService proxy", e);
        }
    }

    /**
     * Invocation handler for the DisplayService proxy.
     * This class handles method calls on the proxy and returns default values.
     */
    private static class DisplayServiceInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            String methodName = method.getName();
            Class<?> returnType = method.getReturnType();

            // Handle common methods
            if (methodName.equals("toString")) {
                return "DesktopDisplayServiceFallback";
            } else if (methodName.equals("hashCode")) {
                return System.identityHashCode(proxy);
            } else if (methodName.equals("equals")) {
                return proxy == args[0];
            }

            // Handle DisplayService methods
            switch (methodName) {
                case "isPhone":
                case "isTablet":
                case "hasNotch":
                    return false;
                case "isDesktop":
                    return true;
                case "getDensity":
                    return DEFAULT_SCREEN_SCALE;
                case "getScreenWidth":
                    return (float) DEFAULT_WIDTH;
                case "getScreenHeight":
                    return (float) DEFAULT_HEIGHT;
                case "getScreenResolution":
                    return DEFAULT_WIDTH + "x" + DEFAULT_HEIGHT;
                default:
                    // For any other method, return a default value based on the return type
                    if (returnType == boolean.class) {
                        return false;
                    } else if (returnType == int.class) {
                        return 0;
                    } else if (returnType == float.class) {
                        return 0.0f;
                    } else if (returnType == double.class) {
                        return 0.0;
                    } else if (returnType == String.class) {
                        return "";
                    } else if (returnType.isAssignableFrom(java.util.Optional.class)) {
                        return java.util.Optional.empty();
                    } else {
                        return null;
                    }
            }
        }
    }
}
