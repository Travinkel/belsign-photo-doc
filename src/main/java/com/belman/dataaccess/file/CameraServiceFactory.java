package com.belman.dataaccess.file;

import com.belman.application.usecase.photo.CameraService;
import com.belman.application.usecase.photo.GluonCameraService;
import com.belman.common.platform.PlatformUtils;
import com.belman.domain.services.LoggerFactory;

/**
 * Factory for creating CameraService instances.
 * This factory creates the appropriate CameraService implementation based on the runtime environment.
 */
public class CameraServiceFactory {

    private final LoggerFactory loggerFactory;
    private final String tempDirectory;
    private final boolean forceMock;

    /**
     * Creates a new CameraServiceFactory with the specified dependencies.
     *
     * @param loggerFactory  the logger factory
     * @param tempDirectory  the directory to store temporary files
     * @param forceMock      whether to force the use of the mock camera service
     */
    public CameraServiceFactory(LoggerFactory loggerFactory, String tempDirectory, boolean forceMock) {
        this.loggerFactory = loggerFactory;
        this.tempDirectory = tempDirectory;
        this.forceMock = forceMock;
    }

    /**
     * Creates a new CameraServiceFactory with the specified dependencies.
     *
     * @param loggerFactory  the logger factory
     * @param tempDirectory  the directory to store temporary files
     */
    public CameraServiceFactory(LoggerFactory loggerFactory, String tempDirectory) {
        this(loggerFactory, tempDirectory, false);
    }

    /**
     * Creates a CameraService instance.
     * If running on a mobile device and not forcing mock, a GluonCameraService is created.
     * Otherwise, a MockCameraService is created.
     *
     * @return a CameraService instance
     */
    public CameraService createCameraService() {
        if (PlatformUtils.isRunningOnMobile() && !forceMock) {
            loggerFactory.getLogger(CameraServiceFactory.class).info("Creating GluonCameraService");
            return new GluonCameraService(tempDirectory);
        } else {
            loggerFactory.getLogger(CameraServiceFactory.class).info("Creating MockCameraService");
            return new MockCameraService(loggerFactory);
        }
    }

    /**
     * Creates a mock CameraService instance.
     *
     * @return a MockCameraService instance
     */
    public CameraService createMockCameraService() {
        loggerFactory.getLogger(CameraServiceFactory.class).info("Creating MockCameraService");
        return new MockCameraService(loggerFactory);
    }

    /**
     * Creates a Gluon CameraService instance.
     *
     * @return a GluonCameraService instance
     */
    public CameraService createGluonCameraService() {
        loggerFactory.getLogger(CameraServiceFactory.class).info("Creating GluonCameraService");
        return new GluonCameraService(tempDirectory);
    }
}
