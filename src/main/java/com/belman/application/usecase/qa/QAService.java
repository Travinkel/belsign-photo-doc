package com.belman.application.usecase.qa;

import com.belman.domain.order.OrderId;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.PhotoId;
import com.belman.domain.user.UserBusiness;

import java.util.List;

/**
 * Service for quality assurance.
 * Provides methods for reviewing and approving photos.
 */
public interface QAService {
    /**
     * Gets all photos pending review.
     *
     * @return a list of photos pending review
     */
    List<PhotoDocument> getPendingReviewPhotos();

    /**
     * Gets all photos pending review for an order.
     *
     * @param orderId the ID of the order
     * @return a list of photos pending review for the order
     */
    List<PhotoDocument> getPendingReviewPhotosByOrderId(OrderId orderId);

    /**
     * Gets all approved photos.
     *
     * @return a list of approved photos
     */
    List<PhotoDocument> getApprovedPhotos();

    /**
     * Gets all approved photos for an order.
     *
     * @param orderId the ID of the order
     * @return a list of approved photos for the order
     */
    List<PhotoDocument> getApprovedPhotosByOrderId(OrderId orderId);

    /**
     * Gets all rejected photos.
     *
     * @return a list of rejected photos
     */
    List<PhotoDocument> getRejectedPhotos();

    /**
     * Gets all rejected photos for an order.
     *
     * @param orderId the ID of the order
     * @return a list of rejected photos for the order
     */
    List<PhotoDocument> getRejectedPhotosByOrderId(OrderId orderId);

    /**
     * Approves a photo.
     *
     * @param photoId    the ID of the photo to approve
     * @param approvedBy the user who approved the photo
     * @return true if the photo was approved, false if the photo was not found
     */
    boolean approvePhoto(PhotoId photoId, UserBusiness approvedBy);

    /**
     * Approves multiple photos.
     *
     * @param photoIds   the IDs of the photos to approve
     * @param approvedBy the user who approved the photos
     * @return the number of photos that were approved
     */
    int approvePhotos(List<PhotoId> photoIds, UserBusiness approvedBy);

    /**
     * Rejects a photo.
     *
     * @param photoId    the ID of the photo to reject
     * @param rejectedBy the user who rejected the photo
     * @param reason     the reason for rejection
     * @return true if the photo was rejected, false if the photo was not found
     */
    boolean rejectPhoto(PhotoId photoId, UserBusiness rejectedBy, String reason);

    /**
     * Rejects multiple photos.
     *
     * @param photoIds   the IDs of the photos to reject
     * @param rejectedBy the user who rejected the photos
     * @param reason     the reason for rejection
     * @return the number of photos that were rejected
     */
    int rejectPhotos(List<PhotoId> photoIds, UserBusiness rejectedBy, String reason);

    /**
     * Adds a comment to a photo.
     *
     * @param photoId     the ID of the photo to comment on
     * @param comment     the comment to add
     * @param commentedBy the user who added the comment
     * @return true if the comment was added, false if the photo was not found
     */
    boolean addComment(PhotoId photoId, String comment, UserBusiness commentedBy);
}