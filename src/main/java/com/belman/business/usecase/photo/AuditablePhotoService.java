package com.belman.business.usecase.photo;

import com.belman.domain.audit.AuditFacade;
import com.belman.domain.audit.Auditable;
import com.belman.domain.event.AuditableBusinessEvent;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.photo.Photo;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.PhotoId;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserBusiness;
import com.belman.business.base.BaseService;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the PhotoService interface that includes auditing capabilities.
 * <p>
 * This service implements the Auditable interface to indicate that it generates
 * audit events that should be tracked for accountability and traceability purposes.
 * <p>
 * It delegates the actual photo operations to another PhotoService implementation
 * and adds auditing on top of it.
 */
public class AuditablePhotoService extends BaseService implements PhotoService, Auditable {

    private final PhotoService delegateService;
    private final AuditFacade auditFacade;
    private UserBusiness currentUser;
    private String currentAction;
    private String currentEntityType;
    private String currentEntityId;
    private String currentDetails;

    /**
     * Creates a new AuditablePhotoService with the specified delegate service and audit facade.
     *
     * @param delegateService the service to delegate photo operations to
     * @param auditFacade     the facade for logging audit events
     * @param loggerFactory   the factory to create loggers
     */
    public AuditablePhotoService(PhotoService delegateService, AuditFacade auditFacade, LoggerFactory loggerFactory) {
        super(loggerFactory);
        this.delegateService = delegateService;
        this.auditFacade = auditFacade;
    }

    @Override
    public Optional<PhotoDocument> getPhotoById(PhotoId photoId) {
        return delegateService.getPhotoById(photoId);
    }

    @Override
    public List<PhotoDocument> getPhotosByOrderId(OrderId orderId) {
        return delegateService.getPhotosByOrderId(orderId);
    }

    @Override
    public PhotoDocument uploadPhoto(OrderId orderId, Photo photo, UserBusiness uploadedBy) {
        // Create a new PhotoId for this upload
        PhotoId photoId = PhotoId.newId();

        // Set audit context
        this.currentUser = uploadedBy;
        this.currentAction = "UPLOAD_PHOTO";
        this.currentEntityType = "Photo";
        this.currentEntityId = photoId.toString();
        this.currentDetails = "Uploaded photo for order " + orderId.toString();

        // Delegate to the actual service
        PhotoDocument result = delegateService.uploadPhoto(orderId, photo, uploadedBy);

        // Log the audit event
        auditFacade.logBusinessEvent(new PhotoUploadedEvent(
                result.getId(),
                orderId,
                uploadedBy.getId().toString()
        ));

        return result;
    }

    @Override
    public List<PhotoDocument> uploadPhotos(OrderId orderId, List<Photo> photos, UserBusiness uploadedBy) {
        // Set audit context
        this.currentUser = uploadedBy;
        this.currentAction = "UPLOAD_PHOTOS";
        this.currentEntityType = "Order";
        this.currentEntityId = orderId.toString();
        this.currentDetails = "Uploaded " + photos.size() + " photos for order " + orderId;

        // Delegate to the actual service
        List<PhotoDocument> results = delegateService.uploadPhotos(orderId, photos, uploadedBy);

        // Log the audit event
        auditFacade.logBusinessEvent(new PhotosUploadedEvent(
                orderId,
                uploadedBy.getId().toString(),
                photos.size()
        ));

        return results;
    }

    @Override
    public boolean deletePhoto(PhotoId photoId, UserBusiness deletedBy) {
        // Set audit context
        this.currentUser = deletedBy;
        this.currentAction = "DELETE_PHOTO";
        this.currentEntityType = "Photo";
        this.currentEntityId = photoId.toString();
        this.currentDetails = "Deleted photo " + photoId;

        // Delegate to the actual service
        boolean result = delegateService.deletePhoto(photoId, deletedBy);

        // Log the audit event only if the operation was successful
        if (result) {
            auditFacade.logBusinessEvent(new PhotoDeletedEvent(
                    photoId,
                    deletedBy.getId().toString()
            ));
        }

        return result;
    }

    @Override
    public boolean approvePhoto(PhotoId photoId, UserBusiness approvedBy) {
        // Set audit context
        this.currentUser = approvedBy;
        this.currentAction = "APPROVE_PHOTO";
        this.currentEntityType = "Photo";
        this.currentEntityId = photoId.toString();
        this.currentDetails = "Approved photo " + photoId;

        // Delegate to the actual service
        boolean result = delegateService.approvePhoto(photoId, approvedBy);

        // Log the audit event only if the operation was successful
        if (result) {
            auditFacade.logPhotoApproved(photoId, approvedBy.getId());
        }

        return result;
    }

    @Override
    public boolean rejectPhoto(PhotoId photoId, UserBusiness rejectedBy, String reason) {
        // Set audit context
        this.currentUser = rejectedBy;
        this.currentAction = "REJECT_PHOTO";
        this.currentEntityType = "Photo";
        this.currentEntityId = photoId.toString();
        this.currentDetails = "Rejected photo " + photoId + " with reason: " + reason;

        // Delegate to the actual service
        boolean result = delegateService.rejectPhoto(photoId, rejectedBy, reason);

        // Log the audit event only if the operation was successful
        if (result) {
            auditFacade.logPhotoRejected(photoId, rejectedBy.getId(), reason);
        }

        return result;
    }

    @Override
    public boolean addComment(PhotoId photoId, String comment, UserBusiness commentedBy) {
        // Set audit context
        this.currentUser = commentedBy;
        this.currentAction = "ADD_COMMENT";
        this.currentEntityType = "Photo";
        this.currentEntityId = photoId.toString();
        this.currentDetails = "Added comment to photo " + photoId + ": " + comment;

        // Delegate to the actual service
        boolean result = delegateService.addComment(photoId, comment, commentedBy);

        // Log the audit event only if the operation was successful
        if (result) {
            auditFacade.logBusinessEvent(new PhotoCommentAddedEvent(
                    photoId,
                    commentedBy.getId().toString(),
                    comment
            ));
        }

        return result;
    }

    @Override
    public String getAuditEntityType() {
        return currentEntityType;
    }

    @Override
    public String getAuditEntityId() {
        return currentEntityId;
    }

    @Override
    public String getAuditUserId() {
        return currentUser != null ? currentUser.getId().toString() : null;
    }

    @Override
    public String getAuditAction() {
        return currentAction;
    }

    @Override
    public String getAuditDetails() {
        return currentDetails;
    }

    /**
     * Event class for photo upload events.
     */
    private static class PhotoUploadedEvent extends AuditableBusinessEvent {
        public PhotoUploadedEvent(PhotoId photoId, OrderId orderId, String userId) {
            super("Photo", photoId.toString(), userId, "UPLOAD_PHOTO",
                    "Uploaded photo for order " + orderId.toString());
        }
    }

    /**
     * Event class for multiple photos upload events.
     */
    private static class PhotosUploadedEvent extends AuditableBusinessEvent {
        public PhotosUploadedEvent(OrderId orderId, String userId, int count) {
            super("Order", orderId.toString(), userId, "UPLOAD_PHOTOS",
                    "Uploaded " + count + " photos for order " + orderId);
        }
    }

    /**
     * Event class for photo deletion events.
     */
    private static class PhotoDeletedEvent extends AuditableBusinessEvent {
        public PhotoDeletedEvent(PhotoId photoId, String userId) {
            super("Photo", photoId.toString(), userId, "DELETE_PHOTO", "Deleted photo " + photoId);
        }
    }

    /**
     * Event class for photo comment events.
     */
    private static class PhotoCommentAddedEvent extends AuditableBusinessEvent {
        public PhotoCommentAddedEvent(PhotoId photoId, String userId, String comment) {
            super("Photo", photoId.toString(), userId, "ADD_COMMENT",
                    "Added comment to photo " + photoId + ": " + comment);
        }
    }
}
