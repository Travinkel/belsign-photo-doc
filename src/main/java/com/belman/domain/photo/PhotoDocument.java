package com.belman.domain.photo;

import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.common.base.BusinessComponent;
import com.belman.domain.order.OrderId;
import com.belman.domain.report.ReportType;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserReference;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a photo document linked to an order in the BelSign system.
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
 * <p>
 * PhotoDocument entities are always associated with an OrderBusiness business object and
 * are managed through the OrderBusiness's collection of photos.
 */
public class PhotoDocument extends BusinessComponent<PhotoId> {

    private final PhotoTemplate template;
    private final Photo imagePath;
    private final List<PhotoAnnotation> annotations;
    private final UserBusiness uploadedBy;
    private final Timestamp uploadedAt;
    private final PhotoId photoId;
    private final ReportType type;
    private PhotoMetadata metadata;
    private OrderId orderId;
    private ApprovalStatus status;
    private UserReference reviewedBy;
    private Timestamp reviewedAt;
    private String reviewComment;
    private Instant lastModifiedAt;

    private PhotoDocument(Builder builder) {
        this.annotations = new ArrayList<>(builder.annotations);
        this.photoId = Objects.requireNonNull(builder.photoId, "photoId must not be null");
        this.template = Objects.requireNonNull(builder.template, "template must not be null");
        this.imagePath = Objects.requireNonNull(builder.imagePath, "imagePath must not be null");
        this.uploadedBy = Objects.requireNonNull(builder.uploadedBy, "uploadedBy must not be null");
        this.uploadedAt = Objects.requireNonNull(builder.uploadedAt, "uploadedAt must not be null");
        this.status = ApprovalStatus.PENDING;
        this.orderId = builder.orderId;
        this.metadata = builder.metadata;
        this.type = ReportType.PHOTO_DOCUMENTATION;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Links this photo to the given order ID.
     *
     * @param orderId the order ID to link this photo to
     * @throws NullPointerException if orderId is null
     */
    public void assignToOrder(OrderId orderId) {
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        updateLastModifiedAt();
    }

    /**
     * Updates the last modified timestamp of this photo document.
     */
    protected void updateLastModifiedAt() {
        this.lastModifiedAt = Instant.now();
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
        updateLastModifiedAt();
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
        updateLastModifiedAt();
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
    public UserBusiness getUploadedBy() {
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
        return photoId;
    }

    /**
     * Gets the last modified timestamp of this photo document.
     *
     * @return the last modified timestamp
     */
    public Instant getLastModifiedAt() {
        return this.lastModifiedAt;
    }

    /**
     * Returns the list of annotations associated with this photo document.
     *
     * @return an unmodifiable list of photo annotations
     */
    public List<PhotoAnnotation> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    /**
     * Returns the metadata associated with this photo document.
     * This includes technical information about the photo such as resolution,
     * file size, and image format.
     *
     * @return the photo metadata, or null if not set
     */
    public PhotoMetadata getMetadata() {
        return metadata;
    }

    /**
     * Sets the metadata for this photo document.
     * This is typically called after the photo is processed and its
     * technical characteristics are extracted.
     *
     * @param metadata the photo metadata to set
     * @return a list of validation errors, or an empty list if the photo meets all quality standards
     * @throws NullPointerException if metadata is null
     */
    public List<String> setMetadata(PhotoMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata must not be null");
        this.metadata = metadata;
        updateLastModifiedAt();

        // Validate photo quality
        return PhotoQualityValidator.validate(metadata);
    }

    /**
     * Checks if this photo document meets all quality standards based on its metadata.
     * 
     * @return true if the photo meets all quality standards, false if it doesn't or if metadata is not set
     */
    public boolean meetsQualityStandards() {
        return metadata != null && PhotoQualityValidator.isValid(metadata);
    }

    /**
     * Adds a new annotation to this photo document.
     *
     * @param annotation the annotation to add
     * @throws NullPointerException if annotation is null
     * @return true if the annotation was added successfully
     */
    public boolean addAnnotation(PhotoAnnotation annotation) {
        Objects.requireNonNull(annotation, "annotation must not be null");
        boolean added = this.annotations.add(annotation);
        if (added) {
            updateLastModifiedAt();
        }
        return added;
    }

    /**
     * Removes an annotation from this photo document.
     *
     * @param annotationId the ID of the annotation to remove
     * @return true if an annotation was removed, false if no annotation with the given ID was found
     */
    public boolean removeAnnotation(String annotationId) {
        Objects.requireNonNull(annotationId, "annotationId must not be null");
        boolean removed = this.annotations.removeIf(a -> a.getId().equals(annotationId));
        if (removed) {
            updateLastModifiedAt();
        }
        return removed;
    }

    /**
     * Updates an existing annotation with a new one having the same ID.
     *
     * @param updatedAnnotation the updated annotation
     * @throws NullPointerException if updatedAnnotation is null
     * @return true if an annotation was updated, false if no annotation with the given ID was found
     */
    public boolean updateAnnotation(PhotoAnnotation updatedAnnotation) {
        Objects.requireNonNull(updatedAnnotation, "updatedAnnotation must not be null");
        String annotationId = updatedAnnotation.getId();

        for (int i = 0; i < annotations.size(); i++) {
            if (annotations.get(i).getId().equals(annotationId)) {
                annotations.set(i, updatedAnnotation);
                updateLastModifiedAt();
                return true;
            }
        }

        return false;
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
        private UserBusiness uploadedBy;
        private Timestamp uploadedAt;
        private OrderId orderId;
        private PhotoMetadata metadata;
        private List<PhotoAnnotation> annotations = new ArrayList<>();

        private Builder() {
        }

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

        public Builder uploadedBy(UserBusiness uploadedBy) {
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

        public Builder metadata(PhotoMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public PhotoDocument build() {
            return new PhotoDocument(this);
        }
    }
}
