package com.belman.service.usecase.photo;

import com.belman.domain.order.OrderId;
import com.belman.domain.order.photo.Photo;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.PhotoId;
import com.belman.domain.user.UserBusiness;

import java.util.List;
import java.util.Optional;

/**
 * Service for photo management.
 * Provides methods for uploading, retrieving, and managing photos.
 */
public interface PhotoService {
    /**
     * Gets a photo by ID.
     *
     * @param photoId the ID of the photo to get
     * @return an Optional containing the photo if found, or empty if not found
     */
    Optional<PhotoDocument> getPhotoById(PhotoId photoId);

    /**
     * Gets all photos for an order.
     *
     * @param orderId the ID of the order
     * @return a list of photos for the order
     */
    List<PhotoDocument> getPhotosByOrderId(OrderId orderId);

    /**
     * Uploads a photo for an order.
     *
     * @param orderId the ID of the order
     * @param photo the photo to upload
     * @param uploadedBy the user who uploaded the photo
     * @return the uploaded photo document
     */
    PhotoDocument uploadPhoto(OrderId orderId, Photo photo, UserBusiness uploadedBy);

    /**
     * Uploads multiple photos for an order.
     *
     * @param orderId the ID of the order
     * @param photos the photos to upload
     * @param uploadedBy the user who uploaded the photos
     * @return a list of uploaded photo documents
     */
    List<PhotoDocument> uploadPhotos(OrderId orderId, List<Photo> photos, UserBusiness uploadedBy);

    /**
     * Deletes a photo.
     *
     * @param photoId the ID of the photo to delete
     * @param deletedBy the user who deleted the photo
     * @return true if the photo was deleted, false if the photo was not found
     */
    boolean deletePhoto(PhotoId photoId, UserBusiness deletedBy);

    /**
     * Approves a photo.
     *
     * @param photoId the ID of the photo to approve
     * @param approvedBy the user who approved the photo
     * @return true if the photo was approved, false if the photo was not found
     */
    boolean approvePhoto(PhotoId photoId, UserBusiness approvedBy);

    /**
     * Rejects a photo.
     *
     * @param photoId the ID of the photo to reject
     * @param rejectedBy the user who rejected the photo
     * @param reason the reason for rejection
     * @return true if the photo was rejected, false if the photo was not found
     */
    boolean rejectPhoto(PhotoId photoId, UserBusiness rejectedBy, String reason);

    /**
     * Adds a comment to a photo.
     *
     * @param photoId the ID of the photo to comment on
     * @param comment the comment to add
     * @param commentedBy the user who added the comment
     * @return true if the comment was added, false if the photo was not found
     */
    boolean addComment(PhotoId photoId, String comment, UserBusiness commentedBy);
}