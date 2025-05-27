package com.belman.domain.report.service;

import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.report.ReportBusiness;
import com.belman.domain.report.ReportId;
import com.belman.domain.report.ReportStatus;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserReference;
import com.belman.application.base.BaseService;

import java.time.Instant;
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
    private final PhotoRepository photoRepository;

    /**
     * Creates a new PhotoReportGenerationService with the specified dependencies.
     *
     * @param loggerFactory    the factory for creating loggers
     * @param photoRepository  the repository for accessing photos
     */
    public PhotoReportGenerationService(
            LoggerFactory loggerFactory,
            PhotoRepository photoRepository) {
        super(loggerFactory);
        this.loggerFactory = Objects.requireNonNull(loggerFactory, "loggerFactory must not be null");
        this.photoRepository = Objects.requireNonNull(photoRepository, "photoRepository must not be null");
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

        List<PhotoDocument> approvedPhotos = photoRepository.findByOrderIdAndStatus(order.getId(), PhotoDocument.ApprovalStatus.APPROVED);


        // Create the report
        ReportId reportId = new ReportId(UUID.randomUUID().toString());

        // Convert UserReference to UserBusiness for the report
        UserBusiness generatedBy = createUserBusinessFromReference(requester);

        // Use the builder to create the report with the correct parameters
        ReportBusiness report = ReportBusiness.builder()
                .id(reportId)
                .orderId(order.getId())
                .approvedPhotos(approvedPhotos)
                .generatedBy(generatedBy) // Use the converted UserBusiness
                .generatedAt(new Timestamp(Instant.now()))
                .status(ReportStatus.PENDING)
                .build();

        return report;
    }

    /**
     * Creates a UserBusiness instance from a UserReference.
     * This is used for testing purposes when we don't have access to a UserRepository.
     *
     * @param reference the UserReference to convert
     * @return a new UserBusiness instance with the same ID and username as the reference
     */
    private UserBusiness createUserBusinessFromReference(UserReference reference) {
        // Create a dummy email address based on the username
        String emailValue = reference.username().value() + "@test.com";
        EmailAddress email = new EmailAddress(emailValue);

        // Create a dummy hashed password (this is a BCrypt hash for "password")
        HashedPassword password = new HashedPassword("$2a$10$ReM2gCw1o9rZz/ctET48N.XCmTxSKFcQvwNaqtjCSZxGr78adkX5u");

        return new UserBusiness.Builder()
                .id(reference.id())
                .username(reference.username())
                .password(password)
                .email(email)
                .build();
    }
}
