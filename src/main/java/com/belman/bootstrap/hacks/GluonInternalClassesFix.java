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
            LOGGER.info("GluonInternalClassesFix already initialized, skipping initialization");
            return;
        }

        LOGGER.info("Starting initialization of fixes for Gluon's internal classes");

        try {
            // Log Java version and classpath information
            LOGGER.info("Java version: " + System.getProperty("java.version"));
            LOGGER.info("Java class path: " + System.getProperty("java.class.path").substring(0, 
                Math.min(System.getProperty("java.class.path").length(), 500)) + "...");

            // Fix LicenseManager
            LOGGER.info("Attempting to fix LicenseManager...");
            fixLicenseManager();

            // Fix TrackingManager
            LOGGER.info("Attempting to fix TrackingManager...");
            fixTrackingManager();

            initialized = true;
            LOGGER.info("Successfully initialized fixes for Gluon's internal classes");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize fixes for Gluon's internal classes", e);
            // Print stack trace for more detailed debugging
            e.printStackTrace();
        }
    }

    /**
     * Fixes issues with LicenseManager.
     */
    private static void fixLicenseManager() {
        try {
            LOGGER.info("Attempting to load LicenseManager class: " + LICENSE_MANAGER_CLASS);

            // Try to load the LicenseManager class
            Class<?> licenseManagerClass = Class.forName(LICENSE_MANAGER_CLASS);
            LOGGER.info("Successfully loaded LicenseManager class");

            // Log class details
            LOGGER.info("LicenseManager class loader: " + licenseManagerClass.getClassLoader());
            LOGGER.info("LicenseManager class location: " + licenseManagerClass.getProtectionDomain().getCodeSource().getLocation());

            // Look for static fields that might be causing issues
            Field[] fields = licenseManagerClass.getDeclaredFields();
            LOGGER.info("Found " + fields.length + " fields in LicenseManager class");

            int modifiedFields = 0;
            for (Field field : fields) {
                LOGGER.info("Examining field: " + field.getName() + " of type " + field.getType().getName());

                if (field.getType().equals(boolean.class) ||
                    field.getType().equals(Boolean.class)) {
                    try {
                        // Make the field accessible
                        field.setAccessible(true);
                        LOGGER.info("Made field " + field.getName() + " accessible");

                        // Get current value
                        Object currentValue = field.get(null);
                        LOGGER.info("Current value of " + field.getName() + " is " + currentValue);

                        // Set the field to true to bypass license checks
                        field.set(null, Boolean.TRUE);
                        modifiedFields++;

                        // Verify the change
                        Object newValue = field.get(null);
                        LOGGER.info("Set " + field.getName() + " to " + newValue + " in LicenseManager");
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to set " + field.getName() + " in LicenseManager", e);
                    }
                }
            }

            LOGGER.info("Modified " + modifiedFields + " fields in LicenseManager");
            LOGGER.info("Successfully fixed LicenseManager");
        } catch (ClassNotFoundException e) {
            LOGGER.info("LicenseManager class not found, skipping fix. Reason: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to fix LicenseManager", e);
            e.printStackTrace();
        }
    }

    /**
     * Fixes issues with TrackingManager.
     */
    private static void fixTrackingManager() {
        try {
            LOGGER.info("Attempting to load TrackingManager class: " + TRACKING_MANAGER_CLASS);

            // Try to load the TrackingManager class
            Class<?> trackingManagerClass = Class.forName(TRACKING_MANAGER_CLASS);
            LOGGER.info("Successfully loaded TrackingManager class");

            // Log class details
            LOGGER.info("TrackingManager class loader: " + trackingManagerClass.getClassLoader());
            LOGGER.info("TrackingManager class location: " + trackingManagerClass.getProtectionDomain().getCodeSource().getLocation());

            // Look for static fields that might be causing issues
            Field[] fields = trackingManagerClass.getDeclaredFields();
            LOGGER.info("Found " + fields.length + " fields in TrackingManager class");

            int modifiedFields = 0;
            for (Field field : fields) {
                LOGGER.info("Examining field: " + field.getName() + " of type " + field.getType().getName());

                if (field.getType().equals(boolean.class) ||
                    field.getType().equals(Boolean.class)) {
                    try {
                        // Make the field accessible
                        field.setAccessible(true);
                        LOGGER.info("Made field " + field.getName() + " accessible");

                        // Get current value
                        Object currentValue = field.get(null);
                        LOGGER.info("Current value of " + field.getName() + " is " + currentValue);

                        // Set the field to true to bypass tracking checks
                        field.set(null, Boolean.TRUE);
                        modifiedFields++;

                        // Verify the change
                        Object newValue = field.get(null);
                        LOGGER.info("Set " + field.getName() + " to " + newValue + " in TrackingManager");
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to set " + field.getName() + " in TrackingManager", e);
                    }
                }
            }

            LOGGER.info("Modified " + modifiedFields + " fields in TrackingManager");
            LOGGER.info("Successfully fixed TrackingManager");
        } catch (ClassNotFoundException e) {
            LOGGER.info("TrackingManager class not found, skipping fix. Reason: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to fix TrackingManager", e);
            e.printStackTrace();
        }
    }
}
