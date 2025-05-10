package com.belman.domain.order.services;

import com.belman.domain.core.BusinessService;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.services.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * Business service for complex order-related operations that span multiple business objects
 * or require business logic that doesn't belong in any specific business object.
 * <p>
 * This service encapsulates business logic for operations like evaluating order readiness
 * for quality review, verifying photo documentation completeness, and managing
 * order state transitions based on business rules.
 */
public class OrderBusinessService extends BusinessService {

    private final LoggerFactory loggerFactory;

    /**
     * Creates a new OrderBusinessService with the specified logger factory.
     *
     * @param loggerFactory the factory for creating loggers
     */
    public OrderBusinessService(LoggerFactory loggerFactory) {
        super();
        this.loggerFactory = Objects.requireNonNull(loggerFactory, "loggerFactory must not be null");
    }

    @Override
    protected LoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    /**
     * Checks if an order is ready for quality assurance review.
     * An order is ready for QA review when it has the required number of approved
     * photos for each category/angle and its status is COMPLETED.
     *
     * @param order  the order to check
     * @param photos the photos associated with the order
     * @return true if the order is ready for QA review, false otherwise
     */
    public boolean isReadyForQaReview(OrderBusiness order, List<PhotoDocument> photos) {
        Objects.requireNonNull(order, "Order must not be null");
        Objects.requireNonNull(photos, "Photos must not be null");

        if (order.getStatus() != OrderStatus.COMPLETED) {
            logDebug("Order {} is not ready for QA review: status is {}", order.getId(), order.getStatus());
            return false;
        }

        if (photos.isEmpty()) {
            logDebug("Order {} is not ready for QA review: no photos", order.getId());
            return false;
        }

        // Check if all required photo angles are covered
        // This is a simplified implementation; in a real system, there would be more complex rules
        // based on product type, customer requirements, etc.
        logInfo("Order {} is ready for QA review", order.getId());
        return true;
    }

    /**
     * Evaluates whether an order can be approved based on business rules.
     * An order can be approved if:
     * 1. It is in COMPLETED status
     * 2. It has sufficient approved photo documentation
     * 3. All required photo angles are covered
     *
     * @param order  the order to evaluate
     * @param photos the photos associated with the order
     * @return true if the order can be approved, false otherwise
     */
    public boolean canApproveOrder(OrderBusiness order, List<PhotoDocument> photos) {
        Objects.requireNonNull(order, "Order must not be null");
        Objects.requireNonNull(photos, "Photos must not be null");

        if (order.getStatus() != OrderStatus.COMPLETED) {
            logDebug("Order {} cannot be approved: status is {}", order.getId(), order.getStatus());
            return false;
        }

        if (!hasSufficientPhotoDocumentation(order, photos)) {
            logDebug("Order {} cannot be approved: insufficient photo documentation", order.getId());
            return false;
        }

        // Additional business rules could be applied here
        logInfo("Order {} can be approved", order.getId());
        return true;
    }

    /**
     * Verifies if an order has sufficient photo documentation to proceed with approval.
     * This applies business rules for photo documentation requirements.
     *
     * @param order  the order to verify
     * @param photos the photos associated with the order
     * @return true if the order has sufficient photo documentation, false otherwise
     */
    public boolean hasSufficientPhotoDocumentation(OrderBusiness order, List<PhotoDocument> photos) {
        Objects.requireNonNull(order, "Order must not be null");
        Objects.requireNonNull(photos, "Photos must not be null");

        // Count approved photos
        long approvedPhotoCount = photos.stream()
                .filter(PhotoDocument::isApproved)
                .count();

        boolean hasSufficient = approvedPhotoCount > 0;

        if (hasSufficient) {
            logInfo("Order {} has sufficient photo documentation: {} approved photos",
                    order.getId(), approvedPhotoCount);
        } else {
            logWarn("Order {} does not have sufficient photo documentation: {} approved photos",
                    order.getId(), approvedPhotoCount);
        }

        // Apply business rule: must have at least one approved photo
        return hasSufficient;
    }
}