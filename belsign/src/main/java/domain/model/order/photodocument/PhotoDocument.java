package domain.model.order.photodocument;

import domain.model.order.OrderId;
import domain.model.shared.Timestamp;
import domain.model.user.User;

import java.util.Objects;

public class PhotoDocument {

    private OrderId orderId;
    private final PhotoAngle angle;
    private ApprovalStatus status;
    private final ImagePath imagePath;
    private final User uploadedBy;
    private final Timestamp uploadedAt;
    private User reviewedBy;
    private Timestamp reviewedAt;
    private String reviewComment;
    private final PhotoId photoId;

    /**
     * Entity representing a photo document linked to an order.
     * Contains metadata (uploader, timestamps) and approval status.
     */

    public PhotoDocument(PhotoId photoId, PhotoAngle angle, ImagePath imagePath, User uploadedBy, Timestamp uploadedAt) {
        this.photoId = Objects.requireNonNull(photoId);
        this.angle = Objects.requireNonNull(angle);
        this.imagePath = Objects.requireNonNull(imagePath);
        this.uploadedBy = Objects.requireNonNull(uploadedBy);
        this.uploadedAt = Objects.requireNonNull(uploadedAt);
        this.status = ApprovalStatus.PENDING;
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
     * @param reviewer the user who reviewed this photo
     * @param reviewedAt the time when this photo was reviewed
     * @throws NullPointerException if reviewer or reviewedAt is null
     * @throws IllegalStateException if this photo is already approved or rejected
     */
    public void approve(User reviewer, Timestamp reviewedAt) {
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
     * @param reviewer the user who reviewed this photo
     * @param reviewedAt the time when this photo was reviewed
     * @param reason the reason for rejection (optional)
     * @throws NullPointerException if reviewer or reviewedAt is null
     * @throws IllegalStateException if this photo is already approved or rejected
     */
    public void reject(User reviewer, Timestamp reviewedAt, String reason) {
        if (this.status != ApprovalStatus.PENDING) {
            throw new IllegalStateException("Photo is already " + this.status.name().toLowerCase());
        }
        this.status = ApprovalStatus.REJECTED;
        this.reviewedBy = Objects.requireNonNull(reviewer, "reviewer must not be null");
        this.reviewedAt = Objects.requireNonNull(reviewedAt, "reviewedAt must not be null");
        this.reviewComment = reason; // reason can be null
    }

    public boolean isApproved() {
        return this.status == ApprovalStatus.APPROVED;
    }

    public boolean isPending() {
        return this.status == ApprovalStatus.PENDING;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public ImagePath getImagePath() {
        return imagePath;
    }

    public PhotoAngle getAngle() {
        return angle;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public Timestamp getUploadedAt() {
        return uploadedAt;
    }

    public PhotoId getPhotoId() {
        return photoId;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public Timestamp getReviewedAt() {
        return reviewedAt;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    /**
     * Approval status of the photo (default PENDING).
     */
    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
