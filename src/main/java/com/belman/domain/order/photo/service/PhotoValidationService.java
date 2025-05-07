package com.belman.domain.order.photo.service;

import com.belman.domain.common.validation.ValidationResult;
import com.belman.domain.core.IDomainService;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.ProductDescription;
import com.belman.domain.order.photo.PhotoAngle;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.policy.PhotoQualityPolicy;
import com.belman.domain.services.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Domain service responsible for validating photos according to business rules.
 * <p>
 * This service enforces quality standards and ensures that the required photos
 * are present for different product types. It encapsulates complex validation
 * logic that spans across multiple entities or aggregates.
 */
public class PhotoValidationService implements IDomainService {

    private final PhotoQualityPolicy photoQualityPolicy;
    private final LoggerFactory loggerFactory;

    /**
     * Creates a new PhotoValidationService with the specified dependencies.
     *
     * @param photoQualityPolicy the policy defining photo quality requirements
     * @param loggerFactory      the factory for creating loggers
     */
    public PhotoValidationService(PhotoQualityPolicy photoQualityPolicy, LoggerFactory loggerFactory) {
        this.photoQualityPolicy = Objects.requireNonNull(photoQualityPolicy, "photoQualityPolicy must not be null");
        this.loggerFactory = Objects.requireNonNull(loggerFactory, "loggerFactory must not be null");
    }

    /**
     * Validates that all required angles for a product have corresponding photos.
     *
     * @param photos             the photos to validate
     * @param productDescription the product description determining the required angles
     * @return a result containing validation messages
     */
    public ValidationResult validateRequiredAngles(List<PhotoDocument> photos, ProductDescription productDescription) {
        Objects.requireNonNull(photos, "photos must not be null");
        Objects.requireNonNull(productDescription, "productDescription must not be null");

        ValidationResult result = new ValidationResult(new ArrayList<>(), new ArrayList<>());
        Set<PhotoAngle> requiredAngles = photoQualityPolicy.getRequiredAngles(productDescription);

        // Extract all angles from the provided photos
        Set<PhotoAngle> providedAngles = photos.stream()
                .map(PhotoDocument::getAngle)
                .collect(Collectors.toSet());

        // Find missing angles
        Set<PhotoAngle> missingAngles = new HashSet<>(requiredAngles);
        missingAngles.removeAll(providedAngles);

        if (!missingAngles.isEmpty()) {
            for (PhotoAngle missingAngle : missingAngles) {
                result.addError(String.format("Missing required photo from angle: %s", missingAngle));
            }
        }

        return result;
    }

    /**
     * Validates that the photos for an order meet all quality requirements.
     *
     * @param photos             the photos to validate
     * @param productDescription the product description determining the requirements
     * @return a result containing validation messages
     */
    public ValidationResult validatePhotoQuality(List<PhotoDocument> photos, ProductDescription productDescription) {
        Objects.requireNonNull(photos, "photos must not be null");
        Objects.requireNonNull(productDescription, "productDescription must not be null");

        ValidationResult result = new ValidationResult();

        // Validate minimum photo count
        int requiredCount = photoQualityPolicy.getMinimumPhotoCount(productDescription);
        if (photos.size() < requiredCount) {
            result.addError(String.format("Insufficient photos. Required: %d, Provided: %d",
                    requiredCount, photos.size()));
        }

        // Validate each individual photo
        for (PhotoDocument photo : photos) {
            // Check if photo has annotations when required
            if (photoQualityPolicy.requiresAnnotations(productDescription) && photo.getAnnotations().isEmpty()) {
                result.addWarning(String.format("Photo %s should have annotations for measurements",
                        photo.getPhotoId()));
            }

            // Add more quality checks as needed
        }

        return result;
    }

    /**
     * Validates all photos for an order against all business rules.
     *
     * @param photos             the photos to validate
     * @param orderId            the ID of the order the photos belong to
     * @param productDescription the product description determining the requirements
     * @return a result containing validation messages
     */
    public ValidationResult validateAll(List<PhotoDocument> photos, OrderId orderId,
                                        ProductDescription productDescription) {
        Objects.requireNonNull(photos, "photos must not be null");
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(productDescription, "productDescription must not be null");

        ValidationResult result = new ValidationResult();

        // Validate all photos belong to the correct order
        if (photos.stream().anyMatch(p -> !orderId.equals(p.getOrderId()))) {
            result.addError("One or more photos do not belong to the specified order");
        }

        // Combine results from all validations
        result.combine(validateRequiredAngles(photos, productDescription));
        result.combine(validatePhotoQuality(photos, productDescription));

        return result;
    }
}