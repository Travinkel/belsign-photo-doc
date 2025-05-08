package com.belman.business.domain.report.service;

import com.belman.business.domain.common.Timestamp;
import com.belman.business.domain.common.validation.ValidationResult;
import com.belman.business.domain.core.IDomainService;
import com.belman.business.domain.order.OrderAggregate;
import com.belman.business.domain.order.ProductDescription;
import com.belman.business.domain.order.photo.PhotoDocument;
import com.belman.business.domain.order.photo.policy.IPhotoQualityService;
import com.belman.business.domain.order.photo.service.PhotoValidationService;
import com.belman.business.domain.report.ReportAggregate;
import com.belman.business.domain.report.ReportId;
import com.belman.business.domain.report.ReportStatus;
import com.belman.business.domain.report.ReportType;
import com.belman.business.domain.services.LoggerFactory;
import com.belman.business.domain.user.UserAggregate;
import com.belman.business.domain.user.UserReference;

import java.time.Instant;

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
    private final IPhotoQualityService photoQualityPolicy;

    /**
     * Creates a new PhotoReportGenerationService with the specified dependencies.
     *
     * @param photoValidationService the service for validating photos
     * @param loggerFactory          the factory for creating loggers
     * @param photoQualityPolicy     the policy for photo quality requirements
     */
    public PhotoReportGenerationService(
            PhotoValidationService photoValidationService,
            LoggerFactory loggerFactory,
            IPhotoQualityService photoQualityPolicy) {
        this.photoValidationService = Objects.requireNonNull(
                photoValidationService, "photoValidationService must not be null");
        this.loggerFactory = Objects.requireNonNull(loggerFactory, "loggerFactory must not be null");
        this.photoQualityPolicy = Objects.requireNonNull(photoQualityPolicy, "photoQualityPolicy must not be null");
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

        // Use the builder to create the report with the correct parameters
        ReportAggregate report = ReportAggregate.builder()
                .id(reportId)
                .orderId(order.getId())
                .approvedPhotos(approvedPhotos)
                // Skip generatedBy since we have a UserReference but need a UserAggregate
                .generatedAt(new Timestamp(Instant.now()))
                .status(ReportStatus.PENDING)
                .build();

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
        int requiredCount = this.photoQualityPolicy.getMinimumPhotoCount(productDescription);
        if (photos.size() < requiredCount) {
            result.addError(String.format("Insufficient photos. Required: %d, Provided: %d",
                    requiredCount, photos.size()));
        }

        // Validate each individual photo
        for (PhotoDocument photo : photos) {
            // Check if photo has annotations when required
            if (this.photoQualityPolicy.requiresAnnotations(productDescription) && photo.getAnnotations().isEmpty()) {
                result.addWarning(String.format("Photo %s should have annotations for measurements",
                        photo.getPhotoId()));
            }

            // Add more quality checks as needed
        }

        return result;
    }
}
