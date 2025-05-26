package com.belman.application.usecase.worker;

import com.belman.application.usecase.order.OrderProgressService;
import com.belman.application.usecase.photo.PhotoCaptureService;
import com.belman.application.usecase.photo.PhotoTemplateService;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.Photo;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserBusiness;

import java.util.List;
import java.util.Optional;

/**
 * Default implementation of the WorkerService interface.
 * This service delegates to specialized services for different responsibilities.
 */
public class DefaultWorkerService implements WorkerService {

    private final PhotoCaptureService photoCaptureService;
    private final PhotoTemplateService photoTemplateService;
    private final OrderProgressService orderProgressService;
    private final Logger logger;

    /**
     * Creates a new DefaultWorkerService with the specified services and logger factory.
     *
     * @param photoCaptureService the photo capture service
     * @param photoTemplateService the photo template service
     * @param orderProgressService the order progress service
     * @param loggerFactory the logger factory
     */
    public DefaultWorkerService(PhotoCaptureService photoCaptureService, 
                               PhotoTemplateService photoTemplateService,
                               OrderProgressService orderProgressService,
                               LoggerFactory loggerFactory) {
        this.photoCaptureService = photoCaptureService;
        this.photoTemplateService = photoTemplateService;
        this.orderProgressService = orderProgressService;
        this.logger = loggerFactory.getLogger(DefaultWorkerService.class);
        logger.info("DefaultWorkerService initialized");
    }

    @Override
    public Optional<OrderBusiness> getAssignedOrder(UserBusiness worker) {
        logger.debug("Delegating getAssignedOrder to OrderProgressService");
        return orderProgressService.getAssignedOrder(worker);
    }

    @Override
    public List<PhotoTemplate> getAvailableTemplates(OrderId orderId) {
        logger.debug("Delegating getAvailableTemplates to PhotoTemplateService");
        return photoTemplateService.getAvailableTemplates(orderId);
    }

    @Override
    public List<PhotoDocument> getCapturedPhotos(OrderId orderId) {
        logger.debug("Delegating getCapturedPhotos to PhotoCaptureService");
        return photoCaptureService.getCapturedPhotos(orderId);
    }

    @Override
    public PhotoDocument capturePhoto(OrderId orderId, PhotoTemplate template, Photo photo, UserBusiness takenBy) {
        logger.debug("Delegating capturePhoto to PhotoCaptureService");
        return photoCaptureService.capturePhoto(orderId, template, photo, takenBy);
    }

    @Override
    public boolean deletePhoto(PhotoId photoId) {
        logger.debug("Delegating deletePhoto to PhotoCaptureService");
        return photoCaptureService.deletePhoto(photoId);
    }

    @Override
    public boolean completeOrder(OrderId orderId, UserBusiness completedBy) {
        logger.debug("Delegating completeOrder to OrderProgressService");
        return orderProgressService.completeOrder(orderId, completedBy);
    }

    @Override
    public boolean hasAllRequiredPhotos(OrderId orderId) {
        logger.debug("Delegating hasAllRequiredPhotos to PhotoTemplateService");
        return photoTemplateService.hasAllRequiredPhotos(orderId);
    }

    @Override
    public List<PhotoTemplate> getMissingRequiredTemplates(OrderId orderId) {
        logger.debug("Getting templates for order using getAvailableTemplates");
        return photoTemplateService.getAvailableTemplates(orderId);
    }
}
