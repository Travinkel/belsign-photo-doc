package com.belman.application.usecase.photo;

import com.belman.domain.order.OrderId;
import com.belman.domain.photo.Photo;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.user.UserBusiness;

import java.util.List;

/**
 * Service for photo capture operations.
 * Provides methods for capturing, retrieving, and deleting photos.
 */
public interface PhotoCaptureService {
    /**
     * Gets all captured photos for an order.
     *
     * @param orderId the ID of the order
     * @return a list of captured photos
     */
    List<PhotoDocument> getCapturedPhotos(OrderId orderId);

    /**
     * Captures a photo for a template.
     *
     * @param orderId  the ID of the order
     * @param template the photo template
     * @param photo    the captured photo
     * @param takenBy  the user who took the photo
     * @return the created photo document
     */
    PhotoDocument capturePhoto(OrderId orderId, PhotoTemplate template, Photo photo, UserBusiness takenBy);

    /**
     * Deletes a photo.
     *
     * @param photoId the ID of the photo to delete
     * @return true if the photo was deleted, false if the photo was not found
     */
    boolean deletePhoto(PhotoId photoId);
}