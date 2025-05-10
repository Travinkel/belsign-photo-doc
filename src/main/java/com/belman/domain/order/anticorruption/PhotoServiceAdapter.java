package com.belman.domain.order.anticorruption;

import com.belman.domain.common.validation.ValidationResult;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.PhotoRepository;
import com.belman.domain.order.photo.service.PhotoValidationService;

import java.util.List;
import java.util.Objects;

/**
 * Anti-corruption layer adapter that mediates between the OrderBusiness and Photo bounded contexts.
 * <p>
 * This adapter provides a clean interface for the OrderBusiness context to interact with the Photo context
 * without direct dependencies. It translates concepts between the two contexts and ensures
 * that changes in the Photo context don't impact the OrderBusiness context directly.
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
     * Checks if an orderBusiness has sufficient approved photos for proceeding with orderBusiness processing.
     *
     * @param orderBusiness the orderBusiness to check
     * @return true if the orderBusiness has sufficient approved photos, false otherwise
     */
    public boolean hasRequiredApprovedPhotos(OrderBusiness orderBusiness) {
        Objects.requireNonNull(orderBusiness, "orderBusiness must not be null");

        ValidationResult result = validateOrderPhotos(orderBusiness);
        return result.getErrors().isEmpty();
    }

    /**
     * Validates that an orderBusiness has all required photos with appropriate quality.
     *
     * @param orderBusiness the orderBusiness to validate photos for
     * @return a validation result containing any errors or warnings
     */
    public ValidationResult validateOrderPhotos(OrderBusiness orderBusiness) {
        Objects.requireNonNull(orderBusiness, "orderBusiness must not be null");

        // Fetch all photos for the orderBusiness
        List<PhotoDocument> photos = photoRepository.findByOrderId(orderBusiness.getId());

        // Validate the photos against product requirements
        return photoValidationService.validateAll(photos, orderBusiness.getId(),
                orderBusiness.getProductDescription());
    }

    /**
     * Returns a list of validation errors for an orderBusiness's photos, if any.
     *
     * @param orderBusiness the orderBusiness to check
     * @return a list of validation error messages, or an empty list if no errors
     */
    public List<String> getPhotoValidationErrors(OrderBusiness orderBusiness) {
        Objects.requireNonNull(orderBusiness, "orderBusiness must not be null");

        ValidationResult result = validateOrderPhotos(orderBusiness);
        return result.getErrors();
    }

    /**
     * Returns a list of validation warnings for an orderBusiness's photos, if any.
     *
     * @param orderBusiness the orderBusiness to check
     * @return a list of validation warning messages, or an empty list if no warnings
     */
    public List<String> getPhotoValidationWarnings(OrderBusiness orderBusiness) {
        Objects.requireNonNull(orderBusiness, "orderBusiness must not be null");

        ValidationResult result = validateOrderPhotos(orderBusiness);
        return result.getErrors();
    }
}