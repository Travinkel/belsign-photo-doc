package com.belman.service.usecase.photo;

import java.io.File;
import java.util.Optional;

/**
 * Service for accessing the device camera and photo gallery.
 * This interface abstracts the platform-specific implementation details
 * for camera access and photo selection.
 */
public interface CameraService {

    /**
     * Takes a photo using the device camera.
     *
     * @return an Optional containing the photo file if successful, or empty if the operation was cancelled
     */
    Optional<File> takePhoto();

    /**
     * Selects a photo from the device gallery.
     *
     * @return an Optional containing the selected photo file if successful, or empty if the operation was cancelled
     */
    Optional<File> selectPhoto();

    /**
     * Checks if the camera is available on the current device.
     *
     * @return true if the camera is available, false otherwise
     */
    boolean isCameraAvailable();

    /**
     * Checks if the photo gallery is available on the current device.
     *
     * @return true if the photo gallery is available, false otherwise
     */
    boolean isGalleryAvailable();
}