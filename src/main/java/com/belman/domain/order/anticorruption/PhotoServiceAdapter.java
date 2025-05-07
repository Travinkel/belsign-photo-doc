package com.belman.domain.order.anticorruption;

import com.belman.domain.order.OrderAggregate;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.PhotoDocumentd;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.photo.service.PhotoValidationService;
import com.belman.domain.photo.service.PhotoValidationService.ValidationResult;

import java.util.List;
import java.util.Objects;

/**
 * Anti-corruption layer adapter that mediates between the OrderAggregate and Photo bounded contexts.
 * <p>
 * This adapter provides a clean interface for the OrderAggregate context to interact with the Photo context
 * without direct dependencies. It translates concepts between the two contexts and ensures
 * that changes in the Photo context don't impact the OrderAggregate context directly.
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
    public List<PhotoDocumentd> getPhotosForOrder(OrderId orderId) {
        Objects.requireNonNull(orderId, "orderId must not be null");
        return photoRepository.findByOrderId(orderId);
    }

    /**
     * Validates that an orderAggregate has all required photos with appropriate quality.
     *
     * @param orderAggregate the orderAggregate to validate photos for
     * @return a validation result containing any errors or warnings
     */
    public ValidationResult validateOrderPhotos(OrderAggregate orderAggregate) {
        Objects.requireNonNull(orderAggregate, "orderAggregate must not be null");

        // Fetch all photos for the orderAggregate
        List<PhotoDocumentd> photos = photoRepository.findByOrderId(orderAggregate.getId());

        // Validate the photos against product requirements
        return photoValidationService.validateAll(photos, orderAggregate.getId(), orderAggregate.getProductDescription());
    }

    /**
     * Checks if an orderAggregate has sufficient approved photos for proceeding with orderAggregate processing.
     *
     * @param orderAggregate the orderAggregate to check
     * @return true if the orderAggregate has sufficient approved photos, false otherwise
     */
    public boolean hasRequiredApprovedPhotos(OrderAggregate orderAggregate) {
        Objects.requireNonNull(orderAggregate, "orderAggregate must not be null");

        ValidationResult result = validateOrderPhotos(orderAggregate);
        return !result.hasErrors();
    }

    /**
     * Returns a list of validation errors for an orderAggregate's photos, if any.
     *
     * @param orderAggregate the orderAggregate to check
     * @return a list of validation error messages, or an empty list if no errors
     */
    public List<String> getPhotoValidationErrors(OrderAggregate orderAggregate) {
        Objects.requireNonNull(orderAggregate, "orderAggregate must not be null");

        ValidationResult result = validateOrderPhotos(orderAggregate);
        return result.getErrors();
    }

    /**
     * Returns a list of validation warnings for an orderAggregate's photos, if any.
     *
     * @param orderAggregate the orderAggregate to check
     * @return a list of validation warning messages, or an empty list if no warnings
     */
    public List<String> getPhotoValidationWarnings(OrderAggregate orderAggregate) {
        Objects.requireNonNull(orderAggregate, "orderAggregate must not be null");

        ValidationResult result = validateOrderPhotos(orderAggregate);
        return result.getWarnings();
    }
}