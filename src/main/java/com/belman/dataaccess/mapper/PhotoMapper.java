package com.belman.dataaccess.mapper;

import com.belman.domain.order.OrderId;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.user.ApprovalStatus;

import java.util.List;

/**
 * Interface for mapping between PhotoDocument entities and database records.
 *
 * @param <D> the type of the database record
 */
public interface PhotoMapper<D> extends EntityMapper<PhotoDocument, D> {

    /**
     * Maps a database record to a PhotoId.
     *
     * @param record the database record
     * @return the PhotoId
     */
    PhotoId toPhotoId(D record);

    /**
     * Maps a database record to an OrderId.
     *
     * @param record the database record
     * @return the OrderId
     */
    OrderId toOrderId(D record);

    /**
     * Finds database records by order ID.
     *
     * @param orderId the order ID
     * @return a list of database records for the specified order
     */
    List<D> findByOrderId(OrderId orderId);

    /**
     * Finds database records by approval status.
     *
     * @param status the approval status
     * @return a list of database records with the specified approval status
     */
    List<D> findByStatus(ApprovalStatus status);

    /**
     * Finds database records by order ID and approval status.
     *
     * @param orderId the order ID
     * @param status  the approval status
     * @return a list of database records for the specified order with the specified approval status
     */
    List<D> findByOrderIdAndStatus(OrderId orderId, ApprovalStatus status);
}