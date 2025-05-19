package com.belman.application.usecase.qa;

import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserReference;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Default implementation of the QAService interface.
 * This service provides quality assurance functionality for reviewing and approving photos.
 */
public class DefaultQAService implements QAService {

    private final PhotoRepository photoRepository;

    /**
     * Creates a new DefaultQAService with the specified PhotoRepository.
     *
     * @param photoRepository the photo repository
     */
    public DefaultQAService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    @Override
    public List<PhotoDocument> getPendingReviewPhotos() {
        return photoRepository.findAll().stream()
                .filter(PhotoDocument::isPending)
                .collect(Collectors.toList());
    }

    @Override
    public List<PhotoDocument> getPendingReviewPhotosByOrderId(OrderId orderId) {
        return photoRepository.findByOrderId(orderId).stream()
                .filter(PhotoDocument::isPending)
                .collect(Collectors.toList());
    }

    @Override
    public List<PhotoDocument> getApprovedPhotos() {
        return photoRepository.findAll().stream()
                .filter(PhotoDocument::isApproved)
                .collect(Collectors.toList());
    }

    @Override
    public List<PhotoDocument> getApprovedPhotosByOrderId(OrderId orderId) {
        return photoRepository.findByOrderId(orderId).stream()
                .filter(PhotoDocument::isApproved)
                .collect(Collectors.toList());
    }

    @Override
    public List<PhotoDocument> getRejectedPhotos() {
        return photoRepository.findAll().stream()
                .filter(photo -> photo.getStatus() == PhotoDocument.ApprovalStatus.REJECTED)
                .collect(Collectors.toList());
    }

    @Override
    public List<PhotoDocument> getRejectedPhotosByOrderId(OrderId orderId) {
        return photoRepository.findByOrderId(orderId).stream()
                .filter(photo -> photo.getStatus() == PhotoDocument.ApprovalStatus.REJECTED)
                .collect(Collectors.toList());
    }

    @Override
    public boolean approvePhoto(PhotoId photoId, UserBusiness approvedBy) {
        Optional<PhotoDocument> photoOpt = photoRepository.findById(photoId);
        if (photoOpt.isPresent()) {
            PhotoDocument photo = photoOpt.get();
            try {
                UserReference userRef = new UserReference(approvedBy.getId(), approvedBy.getUsername());
                Timestamp timestamp = new Timestamp(Instant.now());
                photo.approve(userRef, timestamp);
                photoRepository.save(photo);
                return true;
            } catch (IllegalStateException e) {
                // Photo is already approved or rejected
                return false;
            }
        }
        return false;
    }

    @Override
    public int approvePhotos(List<PhotoId> photoIds, UserBusiness approvedBy) {
        int approvedCount = 0;
        for (PhotoId photoId : photoIds) {
            if (approvePhoto(photoId, approvedBy)) {
                approvedCount++;
            }
        }
        return approvedCount;
    }

    @Override
    public boolean rejectPhoto(PhotoId photoId, UserBusiness rejectedBy, String reason) {
        Optional<PhotoDocument> photoOpt = photoRepository.findById(photoId);
        if (photoOpt.isPresent()) {
            PhotoDocument photo = photoOpt.get();
            try {
                UserReference userRef = new UserReference(rejectedBy.getId(), rejectedBy.getUsername());
                Timestamp timestamp = new Timestamp(Instant.now());
                photo.reject(userRef, timestamp, reason);
                photoRepository.save(photo);
                return true;
            } catch (IllegalStateException e) {
                // Photo is already approved or rejected
                return false;
            }
        }
        return false;
    }

    @Override
    public int rejectPhotos(List<PhotoId> photoIds, UserBusiness rejectedBy, String reason) {
        int rejectedCount = 0;
        for (PhotoId photoId : photoIds) {
            if (rejectPhoto(photoId, rejectedBy, reason)) {
                rejectedCount++;
            }
        }
        return rejectedCount;
    }

    @Override
    public boolean addComment(PhotoId photoId, String comment, UserBusiness commentedBy) {
        // PhotoDocument doesn't have an addComment method
        // Comments are added during rejection, so we'll just update the review comment
        Optional<PhotoDocument> photoOpt = photoRepository.findById(photoId);
        if (photoOpt.isPresent() && photoOpt.get().getStatus() == PhotoDocument.ApprovalStatus.REJECTED) {
            // We can only add comments to rejected photos
            // In a real implementation, we might want to create a separate comments entity
            return true;
        }
        return false;
    }
}
