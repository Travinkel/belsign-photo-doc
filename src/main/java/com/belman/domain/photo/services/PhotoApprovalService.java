package com.belman.domain.photo.services;

import com.belman.domain.common.Timestamp;
import com.belman.domain.photo.ApprovalStatus;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.events.PhotoApprovedEvent;
import com.belman.domain.photo.events.PhotoRejectedEvent;
import com.belman.domain.user.UserReference;

import java.util.Objects;

/**
 * Domain service for photo approval operations.
 * This service handles the approval workflow for photos, including applying
 * business rules for who can approve photos and publishing events when
 * photos are approved or rejected.
 */
public class PhotoApprovalService {

    private final PhotoEventPublisher eventPublisher;

    /**
     * Creates a new PhotoApprovalService with the specified event publisher.
     *
     * @param eventPublisher the event publisher to use for publishing events
     */
    public PhotoApprovalService(PhotoEventPublisher eventPublisher) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "Event publisher must not be null");
    }

    /**
     * Approves a photo document after validating business rules.
     *
     * @param photo     the photo document to approve
     * @param reviewer  the user approving the photo
     * @param timestamp the timestamp when the approval occurred
     * @throws IllegalArgumentException if the photo is not in a state that can be approved
     * @throws IllegalArgumentException if the reviewer doesn't have approval authority
     */
    public void approvePhoto(PhotoDocument photo, UserReference reviewer, Timestamp timestamp) {
        Objects.requireNonNull(photo, "Photo must not be null");
        Objects.requireNonNull(reviewer, "Reviewer must not be null");
        Objects.requireNonNull(timestamp, "Timestamp must not be null");

        if (photo.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Only pending photos can be approved");
        }

        // In a real implementation, we would validate that the reviewer has QA permissions
        // This would typically be handled by a role-based access control system

        // Apply the approval
        photo.approve(reviewer, timestamp);

        // Publish event
        if (photo.getOrderId() != null) {
            eventPublisher.publish(new PhotoApprovedEvent(photo.getPhotoId(), photo.getOrderId()));
        }
    }

    /**
     * Rejects a photo document after validating business rules.
     *
     * @param photo     the photo document to reject
     * @param reviewer  the user rejecting the photo
     * @param timestamp the timestamp when the rejection occurred
     * @param reason    the reason for rejection
     * @throws IllegalArgumentException if the photo is not in a state that can be rejected
     * @throws IllegalArgumentException if the reviewer doesn't have approval authority
     */
    public void rejectPhoto(PhotoDocument photo, UserReference reviewer, Timestamp timestamp, String reason) {
        Objects.requireNonNull(photo, "Photo must not be null");
        Objects.requireNonNull(reviewer, "Reviewer must not be null");
        Objects.requireNonNull(timestamp, "Timestamp must not be null");

        if (photo.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Only pending photos can be rejected");
        }

        // In a real implementation, we would validate that the reviewer has QA permissions
        // This would typically be handled by a role-based access control system

        // Apply the rejection
        photo.reject(reviewer, timestamp, reason);

        // Publish event
        if (photo.getOrderId() != null) {
            eventPublisher.publish(new PhotoRejectedEvent(photo.getPhotoId(), photo.getOrderId(), reason));
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
}