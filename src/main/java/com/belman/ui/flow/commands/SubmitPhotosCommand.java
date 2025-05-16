package com.belman.ui.flow.commands;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.services.PhotoService;
import com.belman.domain.user.UserBusiness;
import com.belman.service.usecase.order.OrderService;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Command for submitting photos for an order, marking the photo session as complete.
 * <p>
 * This command updates the order status to COMPLETED, indicating that all required
 * photos have been taken and the order is ready for QA review.
 */
public class SubmitPhotosCommand implements Command<Boolean> {
    
    @Inject
    private OrderService orderService;
    
    @Inject
    private PhotoService photoService;
    
    @Inject
    private SessionContext sessionContext;
    
    private final OrderId orderId;
    private OrderStatus previousStatus;
    
    /**
     * Creates a new SubmitPhotosCommand for the specified order.
     *
     * @param orderId the ID of the order to submit photos for
     */
    public SubmitPhotosCommand(OrderId orderId) {
        this.orderId = orderId;
    }
    
    @Override
    public CompletableFuture<Boolean> execute() {
        return CompletableFuture.supplyAsync(() -> {
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID cannot be null");
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
            previousStatus = orderOpt.get().getStatus();
            
            // Complete the order
            return orderService.completeOrder(orderId, userOpt.get());
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
        });
    }
    
    @Override
    public boolean canUndo() {
        return previousStatus != null;
    }
    
    @Override
    public String getDescription() {
        return "Submit photos for order: " + orderId.id();
    }
}