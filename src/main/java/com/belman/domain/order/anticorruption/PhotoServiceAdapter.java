package com.belman.domain.order.anticorruption;

import com.belman.domain.order.OrderAggregate;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.photo.service.PhotoValidationService;
import com.belman.domain.photo.service.PhotoValidationService.ValidationResult;

import java.util.List;
import java.util.Objects;

/**
 * Anti-corruption layer adapter that mediates between the Order and Photo bounded contexts.
 * <p>
 * This adapter provides a clean interface for the Order context to interact with the Photo context
 * without direct dependencies. It translates concepts between the two contexts and ensures
 * that changes in the Photo context don't impact the Order context directly.
 * <p>
 * Following DDD principles, this adapter prevents conceptual leakage between bounded contexts
 * and maintains the integrity of the domain model boundaries.
 */
public class PhotoServiceAdapter {

    private final PhotoRepository photoRepository;
    private final PhotoValidationService photoValidationService;

    /**
     * Creates a new PhotoServiceAdapter with the specified dependencies.
     *
     * @param photoRepository        the repository for photo documents
     * @param photoValidationService the service for validating photos
     */
    public PhotoServiceAdapter(PhotoRepository photoRepository, PhotoValidationService photoValidationService) {
        this.photoRepository = Objects.requireNonNull(photoRepository, "photoRepository must not be null");
        this.photoValidationService = Objects.requireNonNull(photoValidationService,
                "photoValidationService must not be null");
    }

    /**
     * Gets all photos associated with an order.
     *
     * @param orderId the ID of the order
     * @return a list of photo documents associated with the order
     */
    public List<PhotoDocument> getPhotosForOrder(OrderId orderId) {
        Objects.requireNonNull(orderId, "orderId must not be null");
        return photoRepository.findByOrderId(orderId);
    }

    /**
     * Validates that an order has all required photos with appropriate quality.
     *
     * @param order the order to validate photos for
     * @return a validation result containing any errors or warnings
     */
    public ValidationResult validateOrderPhotos(OrderAggregate order) {
        Objects.requireNonNull(order, "order must not be null");

        // Fetch all photos for the order
        List<PhotoDocument> photos = photoRepository.findByOrderId(order.getId());

        // Validate the photos against product requirements
        return photoValidationService.validateAll(photos, order.getId(), order.getProductDescription());
    }

    /**
     * Checks if an order has sufficient approved photos for proceeding with order processing.
     *
     * @param order the order to check
     * @return true if the order has sufficient approved photos, false otherwise
     */
    public boolean hasRequiredApprovedPhotos(OrderAggregate order) {
        Objects.requireNonNull(order, "order must not be null");

        ValidationResult result = validateOrderPhotos(order);
        return !result.hasErrors();
    }

    /**
     * Returns a list of validation errors for an order's photos, if any.
     *
     * @param order the order to check
     * @return a list of validation error messages, or an empty list if no errors
     */
    public List<String> getPhotoValidationErrors(OrderAggregate order) {
        Objects.requireNonNull(order, "order must not be null");

        ValidationResult result = validateOrderPhotos(order);
        return result.getErrors();
    }

    /**
     * Returns a list of validation warnings for an order's photos, if any.
     *
     * @param order the order to check
     * @return a list of validation warning messages, or an empty list if no warnings
     */
    public List<String> getPhotoValidationWarnings(OrderAggregate order) {
        Objects.requireNonNull(order, "order must not be null");

        ValidationResult result = validateOrderPhotos(order);
        return result.getWarnings();
    }
}