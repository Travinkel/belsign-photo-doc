package com.belman.domain.order.photo;

import com.belman.domain.core.ComponentDataAccessInterface;
import com.belman.domain.order.OrderId;
import com.belman.domain.user.ApprovalStatus;

import java.util.List;

/**
 * Data access interface for PhotoDocument business component.
 * This interface defines the standard operations to be executed on a data access component for photo documents.
 */
public interface PhotoDataAccess extends ComponentDataAccessInterface<PhotoDocument, PhotoId> {

    /**
     * Finds all photo documents for a specific order.
     *
     * @param orderId the order ID to search for
     * @return a list of photo documents for the specified order
     */
    List<PhotoDocument> findByOrderId(OrderId orderId);

    /**
     * Finds all photo documents with a specific approval status.
     *
     * @param status the approval status to search for
     * @return a list of photo documents with the specified approval status
     */
    List<PhotoDocument> findByStatus(ApprovalStatus status);

    /**
     * Finds all photo documents for a specific order with a specific approval status.
     *
     * @param orderId the order ID to search for
     * @param status  the approval status to search for
     * @return a list of photo documents for the specified order with the specified approval status
     */
    List<PhotoDocument> findByOrderIdAndStatus(OrderId orderId, ApprovalStatus status);
}
