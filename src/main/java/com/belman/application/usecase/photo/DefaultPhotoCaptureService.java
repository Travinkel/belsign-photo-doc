package com.belman.application.usecase.photo;

import com.belman.domain.order.OrderId;
import com.belman.domain.photo.Photo;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoDocumentFactory;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserBusiness;

import java.util.List;

/**
 * Default implementation of the PhotoCaptureService interface.
 * This service provides functionality for capturing, retrieving, and deleting photos.
 */
public class DefaultPhotoCaptureService implements PhotoCaptureService {

    private final PhotoRepository photoRepository;
    private final Logger logger;

    /**
     * Creates a new DefaultPhotoCaptureService with the specified repository and logger factory.
     *
     * @param photoRepository the photo repository
     * @param loggerFactory the logger factory
     */
    public DefaultPhotoCaptureService(PhotoRepository photoRepository, LoggerFactory loggerFactory) {
        this.photoRepository = photoRepository;
        this.logger = loggerFactory.getLogger(DefaultPhotoCaptureService.class);
        logger.info("DefaultPhotoCaptureService initialized");
    }

    @Override
    public List<PhotoDocument> getCapturedPhotos(OrderId orderId) {
        logger.debug("Getting captured photos for order ID: {}", orderId.id());
        List<PhotoDocument> photos = photoRepository.findByOrderId(orderId);
        logger.debug("Found {} captured photos for order", photos.size());
        return photos;
    }

    @Override
    public PhotoDocument capturePhoto(OrderId orderId, PhotoTemplate template, Photo photo, UserBusiness takenBy) {
        logger.info("Capturing photo for order ID: {}, template: {}, taken by: {}", 
                   orderId.id(), template.name(), takenBy.getUsername().value());

        // Create a new photo document
        PhotoDocument photoDocument = PhotoDocumentFactory.createForOrderWithCurrentTimestamp(
                template, photo, takenBy, orderId);

        logger.debug("Created photo document with ID: {}", photoDocument.getPhotoId().id());

        // Save the photo document
        PhotoDocument savedPhoto = photoRepository.save(photoDocument);
        logger.info("Saved photo document to repository, ID: {}", savedPhoto.getPhotoId().id());

        return savedPhoto;
    }

    @Override
    public boolean deletePhoto(PhotoId photoId) {
        logger.info("Attempting to delete photo with ID: {}", photoId.id());
        boolean deleted = photoRepository.deleteById(photoId);
        if (deleted) {
            logger.info("Photo deletion successful for photo ID: {}", photoId.id());
        } else {
            logger.warn("Photo deletion failed for photo ID: {}", photoId.id());
        }
        return deleted;
    }
}