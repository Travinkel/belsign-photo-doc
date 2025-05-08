package com.belman.business.richbe.order.photo.service;

import com.belman.business.richbe.common.validation.ValidationResult;
import com.belman.business.richbe.core.IDomainService;
import com.belman.business.richbe.order.OrderId;
import com.belman.business.richbe.order.ProductDescription;
import com.belman.business.richbe.order.photo.PhotoDocument;
import com.belman.business.richbe.order.photo.PhotoTemplate;
import com.belman.business.richbe.order.photo.policy.IPhotoQualityService;
import com.belman.business.richbe.services.LoggerFactory;

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

    private final IPhotoQualityService photoQualityService;
    private final LoggerFactory loggerFactory;

    /**
     * Creates a new PhotoValidationService with the specified dependencies.
     *
     * @param photoQualityService the service defining photo quality requirements
     * @param loggerFactory       the factory for creating loggers
     */
    public PhotoValidationService(IPhotoQualityService photoQualityService, LoggerFactory loggerFactory) {
        this.photoQualityService = Objects.requireNonNull(photoQualityService, "photoQualityService must not be null");
        this.loggerFactory = Objects.requireNonNull(loggerFactory, "loggerFactory must not be null");
    }

    public ValidationResult validateRequiredTemplates(List<PhotoDocument> photos, ProductDescription productDescription) {
        Objects.requireNonNull(photos, "photos must not be null");
        Objects.requireNonNull(productDescription, "productDescription must not be null");

        ValidationResult result = new ValidationResult();
        Set<PhotoTemplate> requiredTemplates = photoQualityService.getRequiredTemplates(productDescription);

        // Extract all templates from the provided photos
        Set<PhotoTemplate> providedTemplates = photos.stream()
                .map(PhotoDocument::getTemplate)
                .collect(Collectors.toSet());

        // Find missing templates
        Set<PhotoTemplate> missingTemplates = new HashSet<>(requiredTemplates);
        missingTemplates.removeAll(providedTemplates);

        if (!missingTemplates.isEmpty()) {
            for (PhotoTemplate missingTemplate : missingTemplates) {
                result.addError(String.format("Missing required photo for template: %s", missingTemplate));
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

        int requiredCount = photoQualityService.getMinimumPhotoCount(productDescription);
        if (photos.size() < requiredCount) {
            result.addError(String.format("Insufficient photos. Required: %d, Provided: %d", requiredCount, photos.size()));
        }

        // Validate each individual photo
        for (PhotoDocument photo : photos) {
            if (photoQualityService.requiresAnnotations(productDescription) && photo.getAnnotations().isEmpty()) {
                result.addWarning(String.format("Photo %s should have annotations for measurements", photo.getPhotoId()));
            }

            // Add more quality checks as needed
        }

        return result;
    }

    public ValidationResult validateAll(List<PhotoDocument> photos, OrderId orderId, ProductDescription productDescription) {
        Objects.requireNonNull(photos, "photos must not be null");
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(productDescription, "productDescription must not be null");

        ValidationResult result = new ValidationResult();

        // Validate all photos belong to the correct order
        if (photos.stream().anyMatch(p -> !orderId.equals(p.getOrderId()))) {
            result.addError("One or more photos do not belong to the specified order");
        }

        result.combine(validateRequiredTemplates(photos, productDescription));
        result.combine(validatePhotoQuality(photos, productDescription));

        return result;
    }
}
