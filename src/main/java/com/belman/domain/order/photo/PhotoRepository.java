package com.belman.domain.order.photo;

import com.belman.domain.core.Repository;
import com.belman.domain.order.OrderId;
import com.belman.domain.user.ApprovalStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing photo documents.
 * This interface follows the Repository pattern from Domain-Driven Design and provides a collection-like interface for accessing photo document aggregates.
 */
public interface PhotoRepository extends Repository<PhotoDocument, PhotoId> {

    /**
     * Finds a photo document by ID.
     *
     * @param id the photo ID to search for
     * @return an Optional containing the photo document if found, or empty if not found
     */
    Optional<PhotoDocument> findById(PhotoId id);

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
