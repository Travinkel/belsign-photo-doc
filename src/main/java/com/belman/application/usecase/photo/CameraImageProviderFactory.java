package com.belman.application.usecase.photo;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.dataaccess.file.MockCameraImageProvider;
import com.belman.dataaccess.file.RealCameraImageProvider;
import com.belman.domain.services.LoggerFactory;

/**
 * Factory for creating CameraImageProvider instances based on the environment.
 */
public class CameraImageProviderFactory {

    private static final String MOCK_CAMERA_PROPERTY = "mock.camera";
    private static CameraImageProvider instance;

    /**
     * Gets a CameraImageProvider instance based on the environment.
     * If the "mock.camera" system property is set to "true", a MockCameraImageProvider is returned.
     * Otherwise, a RealCameraImageProvider is returned.
     *
     * @return a CameraImageProvider instance
     */
    public static CameraImageProvider getInstance() {
        if (instance == null) {
            // Check if we're in mock mode
            boolean mockMode = Boolean.parseBoolean(System.getProperty(MOCK_CAMERA_PROPERTY, "false"));
            
            // Get dependencies
            CameraService cameraService = ServiceLocator.getService(CameraService.class);
            LoggerFactory loggerFactory = ServiceLocator.getService(LoggerFactory.class);
            
            if (mockMode) {
                // Create a mock provider
                instance = new MockCameraImageProvider(loggerFactory);
            } else {
                // Create a real provider
                instance = new RealCameraImageProvider(cameraService, loggerFactory);
            }
        }
        
        return instance;
    }

    /**
     * Checks if the application is running in mock camera mode.
     *
     * @return true if in mock camera mode, false otherwise
     */
    public static boolean isInMockMode() {
        return Boolean.parseBoolean(System.getProperty(MOCK_CAMERA_PROPERTY, "false"));
    }

    /**
     * Resets the singleton instance, forcing a new instance to be created on the next call to getInstance().
     * This is primarily useful for testing.
     */
    public static void reset() {
        instance = null;
    }
}