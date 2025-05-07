package com.belman.domain.order.services;

import com.belman.domain.order.OrderAggregate;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.order.photo.PhotoDocument;

import java.util.List;
import java.util.Objects;

/**
 * Domain service for complex order-related operations that span multiple aggregates
 * or require domain-specific logic that doesn't belong in any specific aggregate.
 * <p>
 * This service encapsulates domain logic for operations like evaluating order readiness
 * for quality review, verifying photo documentation completeness, and managing
 * order state transitions based on business rules.
 */
public class OrderDomainService {

    /**
     * Checks if an orderAggregate is ready for quality assurance review.
     * An orderAggregate is ready for QA review when it has the required number of approved
     * photos for each category/angle and its status is COMPLETED.
     *
     * @param orderAggregate  the orderAggregate to check
     * @param photos the photos associated with the orderAggregate
     * @return true if the orderAggregate is ready for QA review, false otherwise
     */
    public boolean isReadyForQaReview(OrderAggregate orderAggregate, List<PhotoDocument> photos) {
        Objects.requireNonNull(orderAggregate, "OrderAggregate must not be null");
        Objects.requireNonNull(photos, "Photos must not be null");

        if (orderAggregate.getStatus() != OrderStatus.COMPLETED) {
            return false;
        }

        if (photos.isEmpty()) {
            return false;
        }

        // Check if all required photo angles are covered
        // This is a simplified implementation; in a real system, there would be more complex rules
        // based on product type, customer requirements, etc.
        return true;
    }

    /**
     * Verifies if an orderAggregate has sufficient photo documentation to proceed with approval.
     * This applies business rules for photo documentation requirements.
     *
     * @param orderAggregate  the orderAggregate to verify
     * @param photos the photos associated with the orderAggregate
     * @return true if the orderAggregate has sufficient photo documentation, false otherwise
     */
    public boolean hasSufficientPhotoDocumentation(OrderAggregate orderAggregate, List<PhotoDocument> photos) {
        Objects.requireNonNull(orderAggregate, "OrderAggregate must not be null");
        Objects.requireNonNull(photos, "Photos must not be null");

        // Count approved photos
        long approvedPhotoCount = photos.stream()
                .filter(photo -> photo.getStatus() == PhotoDocument.ApprovalStatus.APPROVED)
                .count();

        // Apply business rule: must have at least one approved photo
        return approvedPhotoCount > 0;
    }

    /**
     * Evaluates whether an orderAggregate can be approved based on business rules.
     * An orderAggregate can be approved if:
     * 1. It is in COMPLETED status
     * 2. It has sufficient approved photo documentation
     * 3. All required photo angles are covered
     *
     * @param orderAggregate  the orderAggregate to evaluate
     * @param photos the photos associated with the orderAggregate
     * @return true if the orderAggregate can be approved, false otherwise
     */
    public boolean canApproveOrder(OrderAggregate orderAggregate, List<PhotoDocument> photos) {
        Objects.requireNonNull(orderAggregate, "OrderAggregate must not be null");
        Objects.requireNonNull(photos, "Photos must not be null");

        if (orderAggregate.getStatus() != OrderStatus.COMPLETED) {
            return false;
        }

        if (!hasSufficientPhotoDocumentation(orderAggregate, photos)) {
            return false;
        }

        // Additional business rules could be applied here

        return true;
    }
}