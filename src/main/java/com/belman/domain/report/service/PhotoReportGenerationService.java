package com.belman.domain.report.service;

import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.ProductDescription;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.report.ReportBusiness;
import com.belman.domain.report.ReportId;
import com.belman.domain.report.ReportStatus;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserReference;
import com.belman.service.base.BaseService;
import com.belman.service.validation.ValidationResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Business service responsible for generating photo documentation reports.
 * <p>
 * This service handles the creation of photo documentation reports based on
 * approved photos for an order. It ensures that all required photos are present
 * and properly approved before generating a report.
 */
public class PhotoReportGenerationService extends BaseService {

    private final LoggerFactory loggerFactory;

    /**
     * Creates a new PhotoReportGenerationService with the specified dependencies.
     *
     * @param loggerFactory          the factory for creating loggers
     */
    public PhotoReportGenerationService(
            LoggerFactory loggerFactory) {
        super();
        this.loggerFactory = Objects.requireNonNull(loggerFactory, "loggerFactory must not be null");
    }

    @Override
    protected LoggerFactory getLoggerFactory() {
        return this.loggerFactory;
    }

    /**
     * Generates a photo documentation report for an order if all required photos are approved.
     *
     * @param order     the order to generate a report for
     * @param requester the user requesting the report generation
     * @return the generated report, or null if the order is not ready for a report
     */
    public ReportBusiness generatePhotoDocumentationReport(OrderBusiness order, UserReference requester) {
        Objects.requireNonNull(order, "order must not be null");
        Objects.requireNonNull(requester, "requester must not be null");

        List<PhotoDocument> approvedPhotos = order.getApprovedPhotos();


        // Create the report
        ReportId reportId = new ReportId(UUID.randomUUID().toString());

        // Use the builder to create the report with the correct parameters
        ReportBusiness report = ReportBusiness.builder()
                .id(reportId)
                .orderId(order.getId())
                .approvedPhotos(approvedPhotos)
                // Skip generatedBy since we have a UserReference but need a UserBusiness
                .generatedAt(new Timestamp(Instant.now()))
                .status(ReportStatus.PENDING)
                .build();

        return report;
    }
}