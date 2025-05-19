package com.belman.application.usecase.worker;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.photo.Photo;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoDocumentFactory;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.user.UserBusiness;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Default implementation of the WorkerService interface.
 * This service provides functionality for production workers to manage orders and photos.
 */
public class DefaultWorkerService implements WorkerService {

    private final OrderRepository orderRepository;
    private final PhotoRepository photoRepository;

    /**
     * Creates a new DefaultWorkerService with the specified repositories.
     *
     * @param orderRepository the order repository
     * @param photoRepository the photo repository
     */
    public DefaultWorkerService(OrderRepository orderRepository, PhotoRepository photoRepository) {
        this.orderRepository = orderRepository;
        this.photoRepository = photoRepository;
    }

    @Override
    public Optional<OrderBusiness> getAssignedOrder(UserBusiness worker) {
        // In a real implementation, this would query a worker-order assignment repository
        // For now, we'll just return the first IN_PROGRESS order
        List<OrderBusiness> orders = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == OrderStatus.IN_PROGRESS)
                .collect(Collectors.toList());
        
        return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
    }

    @Override
    public List<PhotoTemplate> getAvailableTemplates(OrderId orderId) {
        // Return all predefined templates
        return Arrays.asList(
                PhotoTemplate.TOP_VIEW_OF_JOINT,
                PhotoTemplate.SIDE_VIEW_OF_WELD,
                PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.BACK_VIEW_OF_ASSEMBLY,
                PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.BOTTOM_VIEW_OF_ASSEMBLY,
                PhotoTemplate.CLOSE_UP_OF_WELD,
                PhotoTemplate.ANGLED_VIEW_OF_JOINT,
                PhotoTemplate.OVERVIEW_OF_ASSEMBLY
        );
    }

    @Override
    public List<PhotoDocument> getCapturedPhotos(OrderId orderId) {
        return photoRepository.findByOrderId(orderId);
    }

    @Override
    public PhotoDocument capturePhoto(OrderId orderId, PhotoTemplate template, Photo photo, UserBusiness takenBy) {
        // Create a new photo document
        PhotoDocument photoDocument = PhotoDocumentFactory.createForOrderWithCurrentTimestamp(
                template, photo, takenBy, orderId);
        
        // Save the photo document
        return photoRepository.save(photoDocument);
    }

    @Override
    public boolean deletePhoto(PhotoId photoId) {
        return photoRepository.deleteById(photoId);
    }

    @Override
    public boolean completeOrder(OrderId orderId, UserBusiness completedBy) {
        Optional<OrderBusiness> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            OrderBusiness order = orderOpt.get();
            
            // Check if the order has all required photos
            if (!hasAllRequiredPhotos(orderId)) {
                return false;
            }
            
            // Complete the order
            order.completeProcessing();
            
            // Save the order
            orderRepository.save(order);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasAllRequiredPhotos(OrderId orderId) {
        return getMissingRequiredTemplates(orderId).isEmpty();
    }

    @Override
    public List<PhotoTemplate> getMissingRequiredTemplates(OrderId orderId) {
        // Get all captured photos for the order
        List<PhotoDocument> capturedPhotos = photoRepository.findByOrderId(orderId);
        
        // Get the templates of the captured photos
        List<PhotoTemplate> capturedTemplates = capturedPhotos.stream()
                .map(PhotoDocument::getTemplate)
                .collect(Collectors.toList());
        
        // Define the required templates
        List<PhotoTemplate> requiredTemplates = Arrays.asList(
                PhotoTemplate.TOP_VIEW_OF_JOINT,
                PhotoTemplate.SIDE_VIEW_OF_WELD,
                PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.BACK_VIEW_OF_ASSEMBLY
        );
        
        // Find the missing required templates
        return requiredTemplates.stream()
                .filter(template -> !capturedTemplates.contains(template))
                .collect(Collectors.toList());
    }
}