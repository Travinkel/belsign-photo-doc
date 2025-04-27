package com.belman.belsign.domain.model.photo;

import java.util.UUID;
import java.time.LocalDateTime;

public class PhotoDocument {

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }

    private final UUID photoId;
    private final UUID orderId;
    private final UUID uploaderUserId;
    private final String filePath;
    private final PhotoAngle angle;
    private Status status;
    private final LocalDateTime uploadedAt;
    private UUID approvedByUserId;
    private LocalDateTime approvedAt;

    public PhotoDocument(UUID photoId, UUID orderId, UUID uploaderUserId, String filePath, PhotoAngle angle, Status status,
                         LocalDateTime uploadedAt) {
        this.photoId = photoId;
        this.orderId = orderId;
        this.uploaderUserId = uploaderUserId;
        this.filePath = filePath;
        this.angle = angle;
        this.status = status;
        this.uploadedAt = uploadedAt;
    }

    // -- Getters

    public UUID getPhotoId() {
        return photoId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getUploaderUserId() {
        return uploaderUserId;
    }

    public String getFilePath() {
        return filePath;
    }

    public PhotoAngle getAngle() {
        return angle;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public UUID getApprovedByUserId() {
        return approvedByUserId;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    // -- Business Methods

    public void approve(UUID approverUserId) {
        if (this.status != Status.PENDING) {
            throw new IllegalStateException("Only pending photos can be approved.");
        }
        this.status = Status.APPROVED;
        this.approvedByUserId = approverUserId;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject(UUID approverUserId) {
        if (this.status != Status.PENDING) {
            throw new IllegalStateException("Only pending photos can be rejected.");
        }
        this.status = Status.REJECTED;
        this.approvedByUserId = approverUserId;
        this.approvedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "PhotoDocument{" +
               "photoId=" + photoId +
               ", orderId=" + orderId +
               ", uploaderUserId=" + uploaderUserId +
               ", filePath='" + filePath + '\'' +
               ", angle='" + angle + '\'' +
               ", status=" + status +
               ", uploadedAt=" + uploadedAt +
               ", approvedByUserId=" + approvedByUserId +
               ", approvedAt=" + approvedAt +
               '}';
    }
}
