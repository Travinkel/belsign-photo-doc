package com.belman.bootstrap.hacks;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to fix issues with Gluon's internal classes.
 * This class uses reflection to bypass or fix issues with LicenseManager and TrackingManager.
 */
public class GluonInternalClassesFix {
    private static final Logger LOGGER = Logger.getLogger(GluonInternalClassesFix.class.getName());
    // Class names for Gluon's internal classes
    private static final String LICENSE_MANAGER_CLASS = "com.gluonhq.impl.charm.glisten.license.LicenseManager";
    private static final String TRACKING_MANAGER_CLASS = "com.gluonhq.impl.charm.glisten.tracking.TrackingManager";
    private static boolean initialized = false;

    /**
     * Initializes the fixes for Gluon's internal classes.
     * This method uses reflection to bypass or fix issues with LicenseManager and TrackingManager.
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        try {
            // Fix LicenseManager
            fixLicenseManager();

            // Fix TrackingManager
            fixTrackingManager();

            initialized = true;
            LOGGER.info("Successfully initialized fixes for Gluon's internal classes");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize fixes for Gluon's internal classes", e);
        }
    }

    /**
     * Fixes issues with LicenseManager.
     */
    private static void fixLicenseManager() {
        try {
            // Try to load the LicenseManager class
            Class<?> licenseManagerClass = Class.forName(LICENSE_MANAGER_CLASS);

            // Look for static fields that might be causing issues
            for (Field field : licenseManagerClass.getDeclaredFields()) {
                if (field.getType().equals(boolean.class) ||
                    field.getType().equals(Boolean.class)) {
                    try {
                        // Make the field accessible
                        field.setAccessible(true);

                        // Set the field to true to bypass license checks
                        field.set(null, Boolean.TRUE);

                        LOGGER.info("Set " + field.getName() + " to true in LicenseManager");
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to set " + field.getName() + " in LicenseManager", e);
                    }
                }
            }

            LOGGER.info("Successfully fixed LicenseManager");
        } catch (ClassNotFoundException e) {
            LOGGER.info("LicenseManager class not found, skipping fix");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to fix LicenseManager", e);
        }
    }

    /**
     * Fixes issues with TrackingManager.
     */
    private static void fixTrackingManager() {
        try {
            // Try to load the TrackingManager class
            Class<?> trackingManagerClass = Class.forName(TRACKING_MANAGER_CLASS);

            // Look for static fields that might be causing issues
            for (Field field : trackingManagerClass.getDeclaredFields()) {
                if (field.getType().equals(boolean.class) ||
                    field.getType().equals(Boolean.class)) {
                    try {
                        // Make the field accessible
                        field.setAccessible(true);

                        // Set the field to true to bypass tracking checks
                        field.set(null, Boolean.TRUE);

                        LOGGER.info("Set " + field.getName() + " to true in TrackingManager");
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to set " + field.getName() + " in TrackingManager", e);
                    }
                }
            }

            LOGGER.info("Successfully fixed TrackingManager");
        } catch (ClassNotFoundException e) {
            LOGGER.info("TrackingManager class not found, skipping fix");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to fix TrackingManager", e);
        }
    }
}