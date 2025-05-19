package com.belman.application.usecase.worker;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.Photo;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.user.UserBusiness;

import java.util.List;
import java.util.Optional;

/**
 * Service for production worker operations.
 * Provides methods for managing orders, capturing photos, and completing orders.
 */
public interface WorkerService {
    /**
     * Gets the assigned order for a worker.
     *
     * @param worker the worker
     * @return an Optional containing the assigned order if found, or empty if not found
     */
    Optional<OrderBusiness> getAssignedOrder(UserBusiness worker);

    /**
     * Gets all available photo templates for an order.
     *
     * @param orderId the ID of the order
     * @return a list of available photo templates
     */
    List<PhotoTemplate> getAvailableTemplates(OrderId orderId);

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

    /**
     * Completes an order by marking it as ready for QA review.
     *
     * @param orderId     the ID of the order to complete
     * @param completedBy the user who completed the order
     * @return true if the order was completed, false if the order was not found or cannot be completed
     */
    boolean completeOrder(OrderId orderId, UserBusiness completedBy);

    /**
     * Checks if an order has all required photos.
     *
     * @param orderId the ID of the order
     * @return true if the order has all required photos, false otherwise
     */
    boolean hasAllRequiredPhotos(OrderId orderId);

    /**
     * Gets the missing required templates for an order.
     *
     * @param orderId the ID of the order
     * @return a list of required templates that are missing photos
     */
    List<PhotoTemplate> getMissingRequiredTemplates(OrderId orderId);
}