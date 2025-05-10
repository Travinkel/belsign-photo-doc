package com.belman.domain.order.photo.services;

import com.belman.domain.common.Timestamp;
import com.belman.domain.core.BusinessService;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.events.PhotoApprovedEvent;
import com.belman.domain.order.photo.events.PhotoRejectedEvent;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserReference;

import java.util.Objects;

/**
 * Business service for photo approval operations.
 * This service handles the approval workflow for photos, including applying
 * business rules for who can approve photos and publishing events when
 * photos are approved or rejected.
 */
public class PhotoApprovalService extends BusinessService {

    private final PhotoEventPublisher eventPublisher;
    private final LoggerFactory loggerFactory;

    /**
     * Creates a new PhotoApprovalService with the specified event publisher and logger factory.
     *
     * @param eventPublisher the event publisher to use for publishing events
     * @param loggerFactory  the logger factory to use for creating loggers
     */
    public PhotoApprovalService(PhotoEventPublisher eventPublisher, LoggerFactory loggerFactory) {
        super();
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "Event publisher must not be null");
        this.loggerFactory = Objects.requireNonNull(loggerFactory, "Logger factory must not be null");
    }

    @Override
    protected LoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    /**
     * Approves a photo document after validating business rules.
     *
     * @param photo     the photo document to approve
     * @param reviewer  the user approving the photo
     * @param timestamp the timestamp when the approval occurred
     * @throws PhotoApprovalException if the photo is not in a state that can be approved
     * @throws PhotoApprovalException if the reviewer doesn't have approval authority
     */
    public void approvePhoto(PhotoDocument photo, UserReference reviewer, Timestamp timestamp) {
        validatePhotoAndReviewer(photo, reviewer, timestamp);

        if (photo.getStatus() != PhotoDocument.ApprovalStatus.PENDING) {
            throw new PhotoApprovalException("Only photos in the pending state can be approved.");
        }

        // TODO: Implement QA permission check
        // if (!hasQAPermissions(reviewer)) {
        //     throw new PhotoApprovalException("Reviewer does not have QA permissions.");
        // }

        // Apply the approval
        photo.approve(reviewer, timestamp);

        // Publish event
        if (photo.getOrderId() != null) {
            eventPublisher.publish(new PhotoApprovedEvent(photo.getPhotoId(), photo.getOrderId()));
            logInfo("Photo approved: " + photo.getPhotoId());
        }
    }

    private void validatePhotoAndReviewer(PhotoDocument photo, UserReference reviewer, Timestamp timestamp) {
        Objects.requireNonNull(photo, "Photo must not be null");
        Objects.requireNonNull(reviewer, "Reviewer must not be null");
        Objects.requireNonNull(timestamp, "Timestamp must not be null");
    }

    /**
     * Rejects a photo document after validating business rules.
     *
     * @param photo     the photo document to reject
     * @param reviewer  the user rejecting the photo
     * @param timestamp the timestamp when the rejection occurred
     * @param reason    the reason for rejection
     * @throws PhotoApprovalException if the photo is not in a state that can be rejected
     * @throws PhotoApprovalException if the reviewer doesn't have approval authority
     */
    public void rejectPhoto(PhotoDocument photo, UserReference reviewer, Timestamp timestamp, String reason) {
        validatePhotoAndReviewer(photo, reviewer, timestamp);
        Objects.requireNonNull(reason, "Rejection reason must not be null");

        if (reason.trim().isEmpty()) {
            throw new PhotoApprovalException("Rejection reason cannot be empty.");
        }

        if (photo.getStatus() != PhotoDocument.ApprovalStatus.PENDING) {
            throw new PhotoApprovalException("Only photos in the pending state can be rejected.");
        }

        // TODO: Implement QA permission check
        // if (!hasQAPermissions(reviewer)) {
        //     throw new PhotoApprovalException("Reviewer does not have QA permissions.");
        // }

        photo.reject(reviewer, timestamp, reason);

        if (photo.getOrderId() != null) {
            eventPublisher.publish(new PhotoRejectedEvent(photo.getPhotoId(), photo.getOrderId(), reason));
            logInfo("Photo rejected: " + photo.getPhotoId());
        }
    }

    /**
     * Interface for publishing photo-related events.
     */
    public interface PhotoEventPublisher {

        /**
         * Publishes a photo-related event.
         *
         * @param event the event to publish
         */
        void publish(Object event);
    }

    public static class PhotoApprovalException extends RuntimeException {
        public PhotoApprovalException(String message) {
            super(message);
        }
    }
}
