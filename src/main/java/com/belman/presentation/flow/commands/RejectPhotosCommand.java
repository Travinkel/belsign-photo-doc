package com.belman.presentation.flow.commands;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.PhotoId;
import com.belman.domain.services.PhotoService;
import com.belman.domain.user.UserBusiness;
import com.belman.application.usecase.order.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Command for rejecting photos for an order with optional comments.
 * <p>
 * This command rejects a set of photos for an order and updates the order status
 * to REJECTED if necessary.
 */
public class RejectPhotosCommand implements Command<Boolean> {
    
    @Inject
    private OrderService orderService;
    
    @Inject
    private PhotoService photoService;
    
    @Inject
    private SessionContext sessionContext;
    
    private final OrderId orderId;
    private final Map<PhotoId, String> photoIdsWithComments;
    private OrderStatus previousStatus;
    
    /**
     * Creates a new RejectPhotosCommand for the specified order and photos.
     *
     * @param orderId            the ID of the order to reject photos for
     * @param photoIdsWithComments a map of photo IDs to rejection comments
     */
    public RejectPhotosCommand(OrderId orderId, Map<PhotoId, String> photoIdsWithComments) {
        this.orderId = orderId;
        this.photoIdsWithComments = photoIdsWithComments != null ? photoIdsWithComments : new HashMap<>();
    }
    
    /**
     * Creates a new RejectPhotosCommand for the specified order and photos with a single comment.
     *
     * @param orderId  the ID of the order to reject photos for
     * @param photoIds the IDs of the photos to reject
     * @param comment  the rejection comment to apply to all photos
     */
    public RejectPhotosCommand(OrderId orderId, List<PhotoId> photoIds, String comment) {
        this.orderId = orderId;
        this.photoIdsWithComments = new HashMap<>();
        if (photoIds != null) {
            for (PhotoId photoId : photoIds) {
                this.photoIdsWithComments.put(photoId, comment);
            }
        }
    }
    
    @Override
    public CompletableFuture<Boolean> execute() {
        return CompletableFuture.supplyAsync(() -> {
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID cannot be null");
            }
            if (photoIdsWithComments.isEmpty()) {
                throw new IllegalArgumentException("Photo IDs cannot be empty");
            }
            
            // Get the current user
            Optional<UserBusiness> userOpt = sessionContext.getUser();
            if (userOpt.isEmpty()) {
                throw new IllegalStateException("No user is logged in");
            }
            
            // Get the order
            Optional<OrderBusiness> orderOpt = orderService.getOrderById(orderId);
            if (orderOpt.isEmpty()) {
                return false;
            }
            
            // Store the previous status for undo
            OrderBusiness order = orderOpt.get();
            previousStatus = order.getStatus();
            
            // Reject each photo
            boolean allRejected = true;
            for (Map.Entry<PhotoId, String> entry : photoIdsWithComments.entrySet()) {
                PhotoId photoId = entry.getKey();
                String comment = entry.getValue();
                
                PhotoDocument photo = photoService.getPhotoById(photoId);
                if (photo == null) {
                    allRejected = false;
                    continue;
                }
                
                // In a real application, we would call a method to reject the photo with the comment
                // For now, we'll just assume the photo is rejected
            }
            
            // If any photos were rejected and the order is in COMPLETED status, reject the order
            if (allRejected && previousStatus == OrderStatus.COMPLETED) {
                return orderService.updateOrderStatus(orderId, OrderStatus.REJECTED, userOpt.get());
            }
            
            return allRejected;
        });
    }
    
    @Override
    public CompletableFuture<Void> undo() {
        if (!canUndo()) {
            return CompletableFuture.failedFuture(
                    new UnsupportedOperationException("Cannot undo: previous status is unknown"));
        }
        
        return CompletableFuture.runAsync(() -> {
            // Get the current user
            Optional<UserBusiness> userOpt = sessionContext.getUser();
            if (userOpt.isEmpty()) {
                throw new IllegalStateException("No user is logged in");
            }
            
            // Restore the previous status
            boolean updated = orderService.updateOrderStatus(orderId, previousStatus, userOpt.get());
            if (!updated) {
                throw new RuntimeException("Failed to restore previous status for order: " + orderId.id());
            }
            
            // In a real application, we would also need to unreject each photo
        });
    }
    
    @Override
    public boolean canUndo() {
        return previousStatus != null;
    }
    
    @Override
    public String getDescription() {
        return "Reject photos for order: " + orderId.id();
    }
}