package com.belman.business.domain.order.photo;



import com.belman.business.domain.common.Timestamp;
import com.belman.business.domain.core.Aggregate;
import com.belman.business.domain.order.OrderId;
import com.belman.business.domain.report.ReportType;
import com.belman.business.domain.user.UserAggregate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a photo document linked to an order in the BelSign system.
 *
 * The PhotoDocument entity is a core component of the domain model that represents
 * a photograph taken as part of the quality documentation process. Each photo document
 * is associated with a specific order and contains metadata about when it was taken,
 * who took it, and its current approval status.
 *
 * Photo documents go through an approval workflow:
 * 1. Initially created with PENDING status when uploaded by production staff
 * 2. Reviewed by QA personnel who can either APPROVE or REJECT the photo
 * 3. Approved photos are included in quality control reports
 *
 * This entity maintains:
 * - Photo metadata (ID, angle, image path)
 * - Upload information (who uploaded it and when)
 * - Review information (who reviewed it, when, and any comments)
 * - Current approval status
 *
 * PhotoDocument entities are always associated with an OrderAggregate aggregate and
 * are managed through the OrderAggregate's collection of photos.
 */
public class PhotoDocument extends Aggregate<PhotoId> {

    private OrderId orderId;
    private final PhotoTemplate template;
    private ApprovalStatus status;
    private final Photo imagePath;
    private ReportType type;
    private final List<PhotoAnnotation> annotations;

    private final UserAggregate uploadedBy;
    private final Timestamp uploadedAt;
    private UserReference reviewedBy;
    private Timestamp reviewedAt;
    private String reviewComment;
    private final PhotoId photoId;

    private PhotoDocument(Builder builder) {
        this.annotations = new ArrayList<>(builder.annotations);
        this.photoId = Objects.requireNonNull(builder.photoId, "photoId must not be null");
        this.template = Objects.requireNonNull(builder.template, "template must not be null");
        this.imagePath = Objects.requireNonNull(builder.imagePath, "imagePath must not be null");
        this.uploadedBy = Objects.requireNonNull(builder.uploadedBy, "uploadedBy must not be null");
        this.uploadedAt = Objects.requireNonNull(builder.uploadedAt, "uploadedAt must not be null");
        this.status = ApprovalStatus.PENDING;
        this.orderId = builder.orderId;
        this.type = ReportType.PHOTO_DOCUMENTATION;
    }

    /**
     * Links this photo to the given order ID.
     *
     * @param orderId the order ID to link this photo to
     * @throws NullPointerException if orderId is null
     */
    public void assignToOrder(OrderId orderId) {
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
    }

    /**
     * Marks this photo as approved by the given QA user at the given time.
     *
     * @param reviewer   the user who reviewed this photo
     * @param reviewedAt the time when this photo was reviewed
     * @throws NullPointerException  if reviewer or reviewedAt is null
     * @throws IllegalStateException if this photo is already approved or rejected
     */
    public void approve(UserReference reviewer, Timestamp reviewedAt) {
        if (this.status != ApprovalStatus.PENDING) {
            throw new IllegalStateException("Photo is already " + this.status.name().toLowerCase());
        }
        this.status = ApprovalStatus.APPROVED;
        this.reviewedBy = Objects.requireNonNull(reviewer, "reviewer must not be null");
        this.reviewedAt = Objects.requireNonNull(reviewedAt, "reviewedAt must not be null");
    }

    /**
     * Marks this photo as rejected by the given QA user at the given time, with an optional reason.
     *
     * @param reviewer   the user who reviewed this photo
     * @param reviewedAt the time when this photo was reviewed
     * @param reason     the reason for rejection (optional)
     * @throws NullPointerException  if reviewer or reviewedAt is null
     * @throws IllegalStateException if this photo is already approved or rejected
     */
    public void reject(UserReference reviewer, Timestamp reviewedAt, String reason) {
        if (this.status != ApprovalStatus.PENDING) {
            throw new IllegalStateException("Photo is already " + this.status.name().toLowerCase());
        }
        this.status = ApprovalStatus.REJECTED;
        this.reviewedBy = Objects.requireNonNull(reviewer, "reviewer must not be null");
        this.reviewedAt = Objects.requireNonNull(reviewedAt, "reviewedAt must not be null");
        this.reviewComment = reason; // reason can be null
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
     * Returns the ID of the order this photo document is associated with.
     * May be null if the photo has not yet been assigned to an order.
     *
     * @return the order ID, or null if not assigned to an order
     */
    public OrderId getOrderId() {
        return orderId;
    }

    public ReportType getType() {
        return type;
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
    public Photo getImagePath() {
        return imagePath;
    }

    /**
     * Returns the photo template for this photo document.
     *
     * @return the photo template
     */
    public PhotoTemplate getTemplate() {
        return template;
    }

    /**
     * Returns the user who uploaded this photo document.
     *
     * @return the user who uploaded this photo
     */
    public UserAggregate getUploadedBy() {
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
     * Returns the user who reviewed this photo document.
     * Will be null if the photo has not yet been reviewed.
     *
     * @return the user who reviewed this photo, or null if not reviewed
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

    @Override
    public PhotoId getId() {
        return null;
    }

    /**
     * Returns the list of annotations associated with this photo document.
     *
     * @return an unmodifiable list of photo annotations
     */
    public List<PhotoAnnotation> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    public static Builder builder() {
        return new Builder();
    }

        /**
         * Represents the approval status of a photo document in the quality control process.
         * <p>
         * The approval status tracks the photo document's position in the QA workflow:
         * <ul>
         *   <li>PENDING: Initial state when a photo is uploaded but not yet reviewed</li>
         *   <li>APPROVED: The photo has been reviewed and approved by QA personnel</li>
         *   <li>REJECTED: The photo has been reviewed and rejected by QA personnel</li>
         * </ul>
         * <p>
         * Only approved photos are included in quality control reports sent to customers.
         * Rejected photos typically include comments explaining why they were rejected.
         */
    public enum ApprovalStatus {
        /**
         * Initial state when a photo is uploaded but not yet reviewed by QA.
         * Photos in this state are awaiting quality assessment.
         */
        PENDING,

        /**
         * The photo has been reviewed and approved by QA personnel.
         * Photos in this state meet quality standards and can be included in reports.
         */
        APPROVED,

        /**
         * The photo has been reviewed and rejected by QA personnel.
         * Photos in this state do not meet quality standards and need to be retaken.
         */
        REJECTED
    }

    public static class Builder {
        private PhotoId photoId;
        private PhotoTemplate template;
        private Photo imagePath;
        private UserAggregate uploadedBy;
        private Timestamp uploadedAt;
        private OrderId orderId;
        private List<PhotoAnnotation> annotations = new ArrayList<>();

        private Builder() {}

        public Builder annotations(List<PhotoAnnotation> annotations) {
            this.annotations = new ArrayList<>(annotations);
            return this;
        }

        public Builder addAnnotation(PhotoAnnotation annotation) {
            this.annotations.add(annotation);
            return this;
        }

        public Builder photoId(PhotoId photoId) {
            this.photoId = photoId;
            return this;
        }

        public Builder template(PhotoTemplate template) {
            this.template = template;
            return this;
        }

        public Builder imagePath(Photo imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder uploadedBy(UserAggregate uploadedBy) {
            this.uploadedBy = uploadedBy;
            return this;
        }

        public Builder uploadedAt(Timestamp uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public Builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public PhotoDocument build() {
            return new PhotoDocument(this);
        }
    }
}
