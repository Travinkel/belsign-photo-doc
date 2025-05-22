package com.belman.application.usecase.photo;

import com.belman.domain.photo.PhotoTemplate;

import java.io.File;
import java.util.Optional;

/**
 * Interface for providing camera images, either from a real camera or a mock source.
 * This abstraction allows for different implementations based on the environment.
 */
public interface CameraImageProvider {

    /**
     * Takes a photo for the specified template.
     *
     * @param template the photo template to capture
     * @param orderId the ID of the order (can be used for organizing mock images)
     * @return an Optional containing the image file if successful, or empty if failed
     */
    Optional<File> takePhoto(PhotoTemplate template, String orderId);

    /**
     * Selects a photo from the gallery for the specified template.
     *
     * @param template the photo template to select for
     * @param orderId the ID of the order (can be used for organizing mock images)
     * @return an Optional containing the image file if successful, or empty if failed
     */
    Optional<File> selectPhoto(PhotoTemplate template, String orderId);

    /**
     * Checks if the camera is available.
     *
     * @return true if the camera is available, false otherwise
     */
    boolean isCameraAvailable();

    /**
     * Checks if the gallery is available.
     *
     * @return true if the gallery is available, false otherwise
     */
    boolean isGalleryAvailable();

    /**
     * Checks if this provider is a mock implementation.
     *
     * @return true if this is a mock implementation, false otherwise
     */
    boolean isMockProvider();
}