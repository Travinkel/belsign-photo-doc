package com.belman.belsign.domain.model.order.photodocument;

import com.belman.belsign.domain.model.order.ApprovalStatus;
import com.belman.belsign.domain.model.shared.Timestamp;
import com.belman.belsign.domain.model.user.Username;

import java.util.Objects;

public class PhotoDocument {

    private final PhotoId id;
    private final ImagePath path;
    private final Timestamp createdAt;
    private final Username uploader;
    private ApprovalStatus approvalStatus;

    public PhotoDocument(PhotoId id, ImagePath path, Timestamp createdAt, Username uploader) {
        this.id = Objects.requireNonNull(id, "PhotoId must not be null");
        this.path = Objects.requireNonNull(path, "ImagePath must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Timestamp must not be null");
        this.uploader = Objects.requireNonNull(uploader, "Uploader must not be null");
        this.approvalStatus = ApprovalStatus.PENDING;
    }

    public PhotoId getId() {
        return id;
    }

    public ImagePath getPath() {
        return path;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Username getUploader() {
        return uploader;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void approve() {
        approvalStatus = ApprovalStatus.APPROVED;
    }

    public void reject() {
        approvalStatus = ApprovalStatus.REJECTED;
    }
}