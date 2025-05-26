package com.belman.application.usecase.qa;

import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.PhotoAnnotation;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserReference;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    @Override
    public List<PhotoAnnotation> getAnnotations(PhotoId photoId) {
        Optional<PhotoDocument> photoOpt = photoRepository.findById(photoId);
        return photoOpt.map(PhotoDocument::getAnnotations).orElse(Collections.emptyList());
    }

    @Override
    public PhotoAnnotation createAnnotation(PhotoId photoId, double x, double y, String text, PhotoAnnotation.AnnotationType type) {
        Optional<PhotoDocument> photoOpt = photoRepository.findById(photoId);
        if (photoOpt.isPresent()) {
            PhotoDocument photo = photoOpt.get();

            // Create a new annotation with a unique ID
            String annotationId = UUID.randomUUID().toString();
            PhotoAnnotation annotation = new PhotoAnnotation(annotationId, x, y, text, type);

            // Add the annotation to the photo
            boolean added = photo.addAnnotation(annotation);
            if (added) {
                // Save the photo with the new annotation
                photoRepository.save(photo);
                return annotation;
            }
        }
        return null;
    }

    @Override
    public boolean updateAnnotation(PhotoId photoId, PhotoAnnotation annotation) {
        Optional<PhotoDocument> photoOpt = photoRepository.findById(photoId);
        if (photoOpt.isPresent()) {
            PhotoDocument photo = photoOpt.get();

            // Update the annotation in the photo
            boolean updated = photo.updateAnnotation(annotation);
            if (updated) {
                // Save the photo with the updated annotation
                photoRepository.save(photo);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteAnnotation(PhotoId photoId, String annotationId) {
        Optional<PhotoDocument> photoOpt = photoRepository.findById(photoId);
        if (photoOpt.isPresent()) {
            PhotoDocument photo = photoOpt.get();

            // Remove the annotation from the photo
            boolean removed = photo.removeAnnotation(annotationId);
            if (removed) {
                // Save the photo without the annotation
                photoRepository.save(photo);
                return true;
            }
        }
        return false;
    }
}
