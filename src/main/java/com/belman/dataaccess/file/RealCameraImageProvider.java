package com.belman.dataaccess.file;

import com.belman.application.usecase.photo.CameraImageProvider;
import com.belman.application.usecase.photo.CameraService;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.services.LoggerFactory;

import java.io.File;
import java.util.Optional;

/**
 * Real implementation of the CameraImageProvider interface.
 * This implementation delegates to the actual CameraService for real camera operations.
 */
public class RealCameraImageProvider implements CameraImageProvider {

    private final CameraService cameraService;
    private final LoggerFactory loggerFactory;

    /**
     * Creates a new RealCameraImageProvider.
     *
     * @param cameraService the camera service to delegate to
     * @param loggerFactory the logger factory
     */
    public RealCameraImageProvider(CameraService cameraService, LoggerFactory loggerFactory) {
        this.cameraService = cameraService;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Optional<File> takePhoto(PhotoTemplate template, String orderId) {
        logInfo("Taking real photo for template: " + template.name() + ", order: " + orderId);
        return cameraService.takePhoto();
    }

    @Override
    public Optional<File> selectPhoto(PhotoTemplate template, String orderId) {
        logInfo("Selecting real photo for template: " + template.name() + ", order: " + orderId);
        return cameraService.selectPhoto();
    }

    @Override
    public boolean isCameraAvailable() {
        return cameraService.isCameraAvailable();
    }

    @Override
    public boolean isGalleryAvailable() {
        return cameraService.isGalleryAvailable();
    }

    @Override
    public boolean isMockProvider() {
        // This is a real implementation
        return false;
    }

    /**
     * Logs an info message.
     *
     * @param message the info message
     */
    private void logInfo(String message) {
        if (loggerFactory != null) {
            loggerFactory.getLogger(this.getClass()).info(message);
        } else {
            System.out.println(message);
        }
    }
}