package com.belman.infrastructure.service;

import com.belman.backbone.core.util.PlatformUtils;
import com.belman.domain.services.CameraService;

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
        // For now, we'll use the MockCameraService for all platforms
        // In a future implementation, we would use a platform-specific implementation
        // such as GluonCameraService for mobile platforms
        
        if (PlatformUtils.isRunningOnMobile()) {
            // TODO: Return a mobile-specific implementation when dependencies are sorted out
            // return new GluonCameraService(getTempDirectory());
            return new MockCameraService();
        } else {
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