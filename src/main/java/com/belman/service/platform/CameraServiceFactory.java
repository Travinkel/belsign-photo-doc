package com.belman.service.platform;

import com.belman.common.platform.PlatformUtils;
import com.belman.service.camera.impl.GluonCameraServiceAdapter;

/**
 * Factory for creating CameraService instances based on the platform.
 */
public class CameraServiceFactory {

    /**
     * Gets a CameraService instance appropriate for the current platform.
     *
     * @return a CameraService instance
     */
    public static CameraService getCameraService() {
        if (PlatformUtils.isRunningOnMobile()) {
            // Use GluonCameraServiceAdapter for mobile platforms
            return new GluonCameraServiceAdapter(getTempDirectory());
        } else {
            // Use MockCameraServiceAdapter for desktop platforms
            return new MockCameraServiceAdapter();
        }
    }

    /**
     * Gets the temporary directory for storing camera files.
     *
     * @return the temporary directory
     */
    private static String getTempDirectory() {
        // Use the system temporary directory
        return System.getProperty("java.io.tmpdir") + "/belsign/camera";
    }
}
