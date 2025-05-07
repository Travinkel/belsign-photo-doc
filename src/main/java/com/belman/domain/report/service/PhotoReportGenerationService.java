package com.belman.domain.report.service;

import com.belman.domain.common.validation.ValidationResult;
import com.belman.domain.core.IDomainService;
import com.belman.domain.order.OrderAggregate;
import com.belman.domain.order.ProductDescription;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.service.PhotoValidationService;
import com.belman.domain.report.ReportAggregate;
import com.belman.domain.report.ReportId;
import com.belman.domain.report.ReportStatus;
import com.belman.domain.report.ReportType;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain service responsible for generating photo documentation reports.
 * <p>
 * This service handles the creation of photo documentation reports based on
 * approved photos for an order. It ensures that all required photos are present
 * and properly approved before generating a report.
 */
public class PhotoReportGenerationService implements IDomainService {

    private final PhotoValidationService photoValidationService;
    private final LoggerFactory loggerFactory;

    /**
     * Creates a new PhotoReportGenerationService with the specified dependencies.
     *
     * @param photoValidationService the service for validating photos
     * @param loggerFactory          the factory for creating loggers
     */
    public PhotoReportGenerationService(
            PhotoValidationService photoValidationService,
            LoggerFactory loggerFactory) {
        this.photoValidationService = Objects.requireNonNull(
                photoValidationService, "photoValidationService must not be null");
        this.loggerFactory = Objects.requireNonNull(loggerFactory, "loggerFactory must not be null");
    }

    /**
     * Generates a photo documentation report for an order if all required photos are approved.
     *
     * @param order     the order to generate a report for
     * @param requester the user requesting the report generation
     * @return the generated report, or null if the order is not ready for a report
     */
    public ReportAggregate generatePhotoDocumentationReport(OrderAggregate order, UserReference requester) {
        Objects.requireNonNull(order, "order must not be null");
        Objects.requireNonNull(requester, "requester must not be null");

        List<PhotoDocument> approvedPhotos = order.getApprovedPhotos();

        // Validate that we have enough approved photos
        ValidationResult validationResult = photoValidationService.validateAll(
                approvedPhotos,
                order.getId(),
                order.getProductDescription());

        // If there are validation errors, the report cannot be generated
        if (!validationResult.isValid()) {
            loggerFactory.getLogger(getClass()).warn(
                    "Cannot generate photo documentation report for order {}: {}",
                    order.getOrderNumber(),
                    String.join(", ", validationResult.getErrors()));
            return null;
        }

        // Create the report
        ReportId reportId = new ReportId(UUID.randomUUID().toString());
        ReportAggregate report = new ReportAggregate(
                reportId,
                order.getId(),
                ReportType.PHOTO_DOCUMENTATION,
                ReportStatus.PENDING,
                requester);

        // Add the approved photos to the report
        for (PhotoDocument photo : approvedPhotos) {
            report.addPhotoReference(photo.getPhotoId());
        }

        return report;
    }

    /**
     * Validates whether an order is ready for report generation.
     *
     * @param order the order to validate
     * @return the validation result
     */
    public ValidationResult validateReportReadiness(OrderAggregate order) {
        Objects.requireNonNull(order, "order must not be null");

        List<PhotoDocument> approvedPhotos = order.getApprovedPhotos();

        // If no photos are approved, the order is not ready
        if (approvedPhotos.isEmpty()) {
            return ValidationResult.failure("No approved photos available for report generation");
        }

        // Validate the approved photos against the product requirements
        return photoValidationService.validateAll(
                approvedPhotos,
                order.getId(),
                order.getProductDescription());
    }

    public ValidationResult validatePhotoQuality(List<PhotoDocument> photos, ProductDescription productDescription) {
        Objects.requireNonNull(photos, "photos must not be null");
        Objects.requireNonNull(productDescription, "productDescription must not be null");

        ValidationResult result = new ValidationResult(true, new ArrayList<>());

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
}