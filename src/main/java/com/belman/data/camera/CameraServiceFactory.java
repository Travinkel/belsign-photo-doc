package com.belman.data.camera;

import com.belman.data.platform.PlatformUtils;
import com.belman.business.richbe.services.CameraService;
import com.belman.data.service.MockCameraService;

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
            // Use GluonCameraService for mobile platforms
            return new GluonCameraService(getTempDirectory());
        } else {
            // Use MockCameraService for desktop platforms
            return new MockCameraService();
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
