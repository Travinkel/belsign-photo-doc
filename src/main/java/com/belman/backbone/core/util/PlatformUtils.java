package com.belman.backbone.core.util;

/**
 * Utility class for platform detection.
 * Provides methods to check if the application is running on a mobile device.
 */
public class PlatformUtils {

    /**
     * Detects if the application is running on a mobile device.
     * @return true if running on Android or iOS, false otherwise
     */
    public static boolean isRunningOnMobile() {
        try {
            // Try to use Gluon Attach's Platform class if available
            Class<?> platformClass = Class.forName("com.gluonhq.attach.util.Platform");
            
            // Check if running on Android
            boolean isAndroid = (boolean) platformClass.getMethod("isAndroid").invoke(null);
            
            // Check if running on iOS
            boolean isIOS = (boolean) platformClass.getMethod("isIOS").invoke(null);
            
            return isAndroid || isIOS;
        } catch (Exception e) {
            // If Gluon Attach is not available or an error occurs, assume desktop
            return false;
        }
    }
    
    /**
     * Detects if the application is running on Android.
     * @return true if running on Android, false otherwise
     */
    public static boolean isAndroid() {
        try {
            // Try to use Gluon Attach's Platform class if available
            Class<?> platformClass = Class.forName("com.gluonhq.attach.util.Platform");
            
            // Check if running on Android
            return (boolean) platformClass.getMethod("isAndroid").invoke(null);
        } catch (Exception e) {
            // If Gluon Attach is not available or an error occurs, assume not Android
            return false;
        }
    }
    
    /**
     * Detects if the application is running on iOS.
     * @return true if running on iOS, false otherwise
     */
    public static boolean isIOS() {
        try {
            // Try to use Gluon Attach's Platform class if available
            Class<?> platformClass = Class.forName("com.gluonhq.attach.util.Platform");
            
            // Check if running on iOS
            return (boolean) platformClass.getMethod("isIOS").invoke(null);
        } catch (Exception e) {
            // If Gluon Attach is not available or an error occurs, assume not iOS
            return false;
        }
    }
    
    /**
     * Detects if the application is running on desktop.
     * @return true if running on desktop, false otherwise
     */
    public static boolean isDesktop() {
        return !isRunningOnMobile();
    }
}