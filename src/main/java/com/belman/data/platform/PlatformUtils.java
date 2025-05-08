package com.belman.data.platform;

/**
 * Utility class for platform detection.
 * Provides methods to check if the application is running on a mobile device.
 */
public class PlatformUtils {

    /**
     * Detects if the application is running on a mobile device.
     * @return true if running on Android or iOS, false otherwise
     * 
     * Note: This method always returns true as the app is now mobile-only.
     */
    public static boolean isRunningOnMobile() {
        // App is now mobile-only, so always return true
        return true;
    }

    /**
     * Detects if the application is running on Android.
     * @return true if running on Android, false otherwise
     * 
     * Note: This method always returns true as the app is now mobile-only and Android-focused.
     */
    public static boolean isAndroid() {
        // App is now mobile-only and Android-focused, so always return true
        return true;
    }

    /**
     * Detects if the application is running on iOS.
     * @return true if running on iOS, false otherwise
     * 
     * Note: This method always returns false as the app is now mobile-only and Android-focused.
     */
    public static boolean isIOS() {
        // App is now mobile-only and Android-focused, so always return false
        return false;
    }

    /**
     * Detects if the application is running on desktop.
     * @return true if running on desktop, false otherwise
     */
    public static boolean isDesktop() {
        return !isRunningOnMobile();
    }
}
