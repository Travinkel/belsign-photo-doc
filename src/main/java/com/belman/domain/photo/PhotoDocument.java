package com.belman.domain.photo;

import com.belman.domain.common.Timestamp;
import com.belman.domain.core.AggregateRoot;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.events.PhotoApprovedEvent;
import com.belman.domain.photo.events.PhotoRejectedEvent;
import com.belman.domain.user.UserAggregate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root entity representing a photo document linked to an order in the BelSign system.
 * <p>
 * The PhotoDocument entity is a core component of the domain model that represents
 * a photograph taken as part of the quality documentation process. Each photo document
 * is associated with a specific order and contains metadata about when it was taken,
 * who took it, and its current approval status.
 * <p>
 * Photo documents go through an approval workflow:
 * 1. Initially created with PENDING status when uploaded by production staff
 * 2. Reviewed by QA personnel who can either APPROVE or REJECT the photo
 * 3. Approved photos are included in quality control reports
 * <p>
 * This entity maintains:
 * - Photo metadata (ID, angle, image path)
 * - Upload information (who uploaded it and when)
 * - Review information (who reviewed it, when, and any comments)
 * - Current approval status
 * - Annotations and markups added to the photo
 */
public class PhotoDocument extends AggregateRoot<PhotoId> {

    private final PhotoId photoId;
    private OrderId orderId;
    private final PhotoAngle angle;
    private final ImagePath imagePath;
    private final Timestamp uploadedAt;
    private final UserReference uploadedBy;
    private ApprovalStatus status;
    private UserReference reviewedBy;
    private Timestamp reviewedAt;
    private String reviewComment;
    private final List<PhotoAnnotation> annotations = new ArrayList<>();

    /**
     * Creates a new PhotoDocument with the specified details.
     * The photo document is initially created with PENDING approval status
     * and must be assigned to an order using {@link #assignToOrder(OrderId)}
     * before it can be used in the system.
     *
     * @param photoId    the unique identifier for this photo document
     * @param angle      the angle at which the photo was taken
     * @param imagePath  the path to the image file
     * @param uploadedBy reference to the user who uploaded this photo
     * @param uploadedAt the timestamp when this photo was uploaded
     * @throws NullPointerException if any parameter is null
     */
    public PhotoDocument(PhotoId photoId, PhotoAngle angle, ImagePath imagePath, UserReference uploadedBy,
                         Timestamp uploadedAt) {
        this.photoId = Objects.requireNonNull(photoId, "photoId must not be null");
        this.angle = Objects.requireNonNull(angle, "angle must not be null");
        this.imagePath = Objects.requireNonNull(imagePath, "imagePath must not be null");
        this.uploadedBy = Objects.requireNonNull(uploadedBy, "uploadedBy must not be null");
        this.uploadedAt = Objects.requireNonNull(uploadedAt, "uploadedAt must not be null");
        this.status = ApprovalStatus.PENDING;
    }

    /**
     * Factory method to create a new PhotoDocument.
     *
     * @param photoId    the unique identifier for this photo document
     * @param angle      the angle at which the photo was taken
     * @param imagePath  the path to the image file
     * @param uploadedBy reference to the user who uploaded this photo
     * @param uploadedAt the timestamp when this photo was uploaded
     * @return a new PhotoDocument instance
     */
    public static PhotoDocument create(PhotoId photoId, PhotoAngle angle, ImagePath imagePath,
                                       UserReference uploadedBy, Timestamp uploadedAt) {
        return new PhotoDocument(photoId, angle, imagePath, uploadedBy, uploadedAt);
    }

    /**
     * Links this photo to the given order ID.
     *
     * @param orderId the order ID to link this photo to
     * @throws NullPointerException if orderId is null
     * @throws IllegalStateException if already assigned to an order
     */
    public void assignToOrder(OrderId orderId) {
        Objects.requireNonNull(orderId, "orderId must not be null");

        if (this.orderId != null) {
            throw new IllegalStateException("Photo is already assigned to order: " + this.orderId);
        }

        this.orderId = orderId;
    }

    /**
     * Marks this photo as approved by the given QA user at the given time.
     * Raises a PhotoApprovedEvent.
     *
     * @param reviewer   reference to the user who reviewed this photo
     * @param reviewedAt the time when this photo was reviewed
     * @throws NullPointerException  if reviewer or reviewedAt is null
     * @throws IllegalStateException if this photo is already approved or rejected
     * @throws IllegalStateException if this photo is not assigned to an order
     */
    public void approve(UserReference reviewer, Timestamp reviewedAt) {
        if (this.status != ApprovalStatus.PENDING) {
            throw new IllegalStateException("Photo is already " + this.status.name().toLowerCase());
        }

        if (this.orderId == null) {
            throw new IllegalStateException("Cannot approve a photo that is not assigned to an order");
        }

        this.status = ApprovalStatus.APPROVED;
        this.reviewedBy = Objects.requireNonNull(reviewer, "reviewer must not be null");
        this.reviewedAt = Objects.requireNonNull(reviewedAt, "reviewedAt must not be null");

        // Raise domain event
        PhotoApprovedEvent event = new PhotoApprovedEvent(this.photoId, this.orderId);
        registerDomainEvent(event);
    }

    /**
     * Marks this photo as rejected by the given QA user at the given time, with an optional reason.
     * Raises a PhotoRejectedEvent.
     *
     * @param reviewer   reference to the user who reviewed this photo
     * @param reviewedAt the time when this photo was reviewed
     * @param reason     the reason for rejection (optional)
     * @throws NullPointerException  if reviewer or reviewedAt is null
     * @throws IllegalStateException if this photo is already approved or rejected
     * @throws IllegalStateException if this photo is not assigned to an order
     */
    public void reject(UserReference reviewer, Timestamp reviewedAt, String reason) {
        if (this.status != ApprovalStatus.PENDING) {
            throw new IllegalStateException("Photo is already " + this.status.name().toLowerCase());
        }

        if (this.orderId == null) {
            throw new IllegalStateException("Cannot reject a photo that is not assigned to an order");
        }

        this.status = ApprovalStatus.REJECTED;
        this.reviewedBy = Objects.requireNonNull(reviewer, "reviewer must not be null");
        this.reviewedAt = Objects.requireNonNull(reviewedAt, "reviewedAt must not be null");
        this.reviewComment = reason; // reason can be null

        // Raise domain event
        PhotoRejectedEvent event = new PhotoRejectedEvent(this.photoId, this.orderId, reason);
        registerDomainEvent(event);
    }

    /**
     * Adds an annotation to this photo.
     *
     * @param annotation the annotation to add
     * @throws NullPointerException if annotation is null
     */
    public void addAnnotation(PhotoAnnotation annotation) {
        Objects.requireNonNull(annotation, "annotation must not be null");
        this.annotations.add(annotation);
    }

    /**
     * Removes an annotation from this photo.
     *
     * @param annotationId the ID of the annotation to remove
     * @return true if the annotation was removed, false if not found
     */
    public boolean removeAnnotation(String annotationId) {
        return this.annotations.removeIf(a -> a.getId().equals(annotationId));
    }

    /**
     * Returns all annotations on this photo.
     *
     * @return an unmodifiable list of annotations
     */
    public List<PhotoAnnotation> getAnnotations() {
        return Collections.unmodifiableList(this.annotations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhotoId getId() {
        return photoId;
    }

    /**
     * Checks if this photo document has been approved by QA.
     *
     * @return true if the photo has been approved, false otherwise
     */
    public boolean isApproved() {
        return this.status == ApprovalStatus.APPROVED;
    }

    /**
     * Checks if this photo document is still pending QA review.
     *
     * @return true if the photo is pending review, false otherwise
     */
    public boolean isPending() {
        return this.status == ApprovalStatus.PENDING;
    }

    /**
     * Checks if this photo document has been rejected by QA.
     *
     * @return true if the photo has been rejected, false otherwise
     */
    public boolean isRejected() {
        return this.status == ApprovalStatus.REJECTED;
    }

    /**
     * Returns the ID of the order this photo document is associated with.
     * May be null if the photo has not yet been assigned to an order.
     *
     * @return the order ID, or null if not assigned to an order
     */
    public OrderId getOrderId() {
        return orderId;
    }

    /**
     * Returns the current approval status of this photo document.
     *
     * @return the approval status (PENDING, APPROVED, or REJECTED)
     * @see ApprovalStatus
     */
    public ApprovalStatus getStatus() {
        return status;
    }

    /**
     * Returns the path to the image file for this photo document.
     *
     * @return the image path
     */
    public ImagePath getImagePath() {
        return imagePath;
    }

    /**
     * Returns the angle at which this photo was taken.
     * The angle may be a named angle (FRONT, BACK, LEFT, RIGHT) or a custom angle in degrees.
     *
     * @return the photo angle
     */
    public PhotoAngle getAngle() {
        return angle;
    }

    /**
     * Returns the reference to the user who uploaded this photo document.
     *
     * @return reference to the user who uploaded this photo
     */
    public UserReference getUploadedBy() {
        return uploadedBy;
    }

    /**
     * Returns the timestamp when this photo document was uploaded.
     *
     * @return the upload timestamp
     */
    public Timestamp getUploadedAt() {
        return uploadedAt;
    }

    /**
     * Returns the unique identifier for this photo document.
     *
     * @return the photo ID
     */
    public PhotoId getPhotoId() {
        return photoId;
    }

    /**
     * Returns reference to the user who reviewed this photo document.
     * Will be null if the photo has not yet been reviewed.
     *
     * @return reference to the user who reviewed this photo, or null if not reviewed
     */
    public UserReference getReviewedBy() {
        return reviewedBy;
    }

    /**
     * Returns the timestamp when this photo document was reviewed.
     * Will be null if the photo has not yet been reviewed.
     *
     * @return the review timestamp, or null if not reviewed
     */
    public Timestamp getReviewedAt() {
        return reviewedAt;
    }

    /**
     * Returns any comments provided during the review of this photo document.
     * Will be null if the photo has not been reviewed or if no comments were provided.
     * Comments are typically provided when a photo is rejected to explain the reason.
     *
     * @return the review comments, or null if none
     */
    public String getReviewComment() {
        return reviewComment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhotoDocument that = (PhotoDocument) o;
        return photoId.equals(that.photoId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(photoId);
    }
}