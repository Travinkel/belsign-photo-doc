package com.belman.application.usecase.qa;

import com.belman.domain.order.OrderId;
import com.belman.domain.photo.PhotoAnnotation;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.user.UserBusiness;

import java.util.List;

/**
 * Service for quality assurance.
 * Provides methods for reviewing and approving photos, as well as managing photo annotations.
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

    /**
     * Gets all annotations for a photo.
     *
     * @param photoId the ID of the photo
     * @return a list of annotations for the photo, or an empty list if the photo was not found
     */
    List<PhotoAnnotation> getAnnotations(PhotoId photoId);

    /**
     * Creates a new annotation for a photo.
     *
     * @param photoId the ID of the photo to annotate
     * @param x the x-coordinate (as percentage of image width, 0.0-1.0)
     * @param y the y-coordinate (as percentage of image height, 0.0-1.0)
     * @param text the text content of the annotation
     * @param type the type of annotation
     * @return the created annotation, or null if the photo was not found
     */
    PhotoAnnotation createAnnotation(PhotoId photoId, double x, double y, String text, PhotoAnnotation.AnnotationType type);

    /**
     * Updates an existing annotation.
     *
     * @param photoId the ID of the photo containing the annotation
     * @param annotation the updated annotation
     * @return true if the annotation was updated successfully, false if the photo or annotation was not found
     */
    boolean updateAnnotation(PhotoId photoId, PhotoAnnotation annotation);

    /**
     * Deletes an annotation from a photo.
     *
     * @param photoId the ID of the photo containing the annotation
     * @param annotationId the ID of the annotation to delete
     * @return true if the annotation was deleted successfully, false if the photo or annotation was not found
     */
    boolean deleteAnnotation(PhotoId photoId, String annotationId);
}
