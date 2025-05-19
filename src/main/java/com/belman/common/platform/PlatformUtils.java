package com.belman.common.platform;

/**
 * Utility class for platform detection.
 * Provides methods to check if the application is running on a mobile device.
 */
public class PlatformUtils {

    /**
     * Detects if the application is running on Android.
     *
     * @return true if running on Android, false otherwise
     * <p>
     * Note: For development on desktop, this returns false.
     */
    public static boolean isAndroid() {
        // Check if we're running on desktop for development
        String os = System.getProperty("os.name").toLowerCase();
        return !(os.contains("win") || os.contains("mac") || os.contains("linux"));
    }

    /**
     * Detects if the application is running on iOS.
     *
     * @return true if running on iOS, false otherwise
     * <p>
     * Note: This method always returns false as the app is now mobile-only and Android-focused.
     */
    public static boolean isIOS() {
        // App is now mobile-only and Android-focused, so always return false
        return false;
    }

    /**
     * Detects if the application is running on desktop.
     *
     * @return true if running on desktop, false otherwise
     */
    public static boolean isDesktop() {
        return !isRunningOnMobile();
    }

    /**
     * Detects if the application is running on a mobile device.
     *
     * @return true if running on Android or iOS, false otherwise
     * <p>
     * Note: For development on desktop, this returns false to enable desktop fallbacks.
     */
    public static boolean isRunningOnMobile() {
        // Check if we're running on desktop for development
        String os = System.getProperty("os.name").toLowerCase();
        return !(os.contains("win") || os.contains("mac") || os.contains("linux"));
    }
}
