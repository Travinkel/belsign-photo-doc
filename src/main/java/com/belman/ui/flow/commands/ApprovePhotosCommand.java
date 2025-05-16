package com.belman.ui.flow.commands;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.PhotoId;
import com.belman.domain.services.PhotoService;
import com.belman.domain.user.UserBusiness;
import com.belman.service.usecase.order.OrderService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Command for approving photos for an order.
 * <p>
 * This command approves a set of photos for an order and updates the order status
 * to APPROVED if all required photos have been approved.
 */
public class ApprovePhotosCommand implements Command<Boolean> {
    
    @Inject
    private OrderService orderService;
    
    @Inject
    private PhotoService photoService;
    
    @Inject
    private SessionContext sessionContext;
    
    private final OrderId orderId;
    private final List<PhotoId> photoIds;
    private OrderStatus previousStatus;
    
    /**
     * Creates a new ApprovePhotosCommand for the specified order and photos.
     *
     * @param orderId  the ID of the order to approve photos for
     * @param photoIds the IDs of the photos to approve
     */
    public ApprovePhotosCommand(OrderId orderId, List<PhotoId> photoIds) {
        this.orderId = orderId;
        this.photoIds = photoIds;
    }
    
    @Override
    public CompletableFuture<Boolean> execute() {
        return CompletableFuture.supplyAsync(() -> {
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID cannot be null");
            }
            if (photoIds == null || photoIds.isEmpty()) {
                throw new IllegalArgumentException("Photo IDs cannot be null or empty");
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
            
            // Approve each photo
            boolean allApproved = true;
            for (PhotoId photoId : photoIds) {
                PhotoDocument photo = photoService.getPhotoById(photoId);
                if (photo == null) {
                    allApproved = false;
                    continue;
                }
                
                // In a real application, we would call a method to approve the photo
                // For now, we'll just assume the photo is approved
            }
            
            // If all photos were approved and the order is in COMPLETED status, approve the order
            if (allApproved && previousStatus == OrderStatus.COMPLETED) {
                return orderService.updateOrderStatus(orderId, OrderStatus.APPROVED, userOpt.get());
            }
            
            return allApproved;
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
            
            // In a real application, we would also need to unapprove each photo
        });
    }
    
    @Override
    public boolean canUndo() {
        return previousStatus != null;
    }
    
    @Override
    public String getDescription() {
        return "Approve photos for order: " + orderId.id();
    }
}