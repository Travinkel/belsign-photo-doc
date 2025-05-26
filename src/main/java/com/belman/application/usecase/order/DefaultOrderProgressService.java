package com.belman.application.usecase.order;

import com.belman.application.usecase.photo.PhotoTemplateService;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserBusiness;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Default implementation of the OrderProgressService interface.
 * This service provides functionality for managing order workflow and status.
 */
public class DefaultOrderProgressService implements OrderProgressService {

    private final OrderRepository orderRepository;
    private final PhotoTemplateService photoTemplateService;
    private final Logger logger;

    /**
     * Creates a new DefaultOrderProgressService with the specified repository, service, and logger factory.
     *
     * @param orderRepository the order repository
     * @param photoTemplateService the photo template service
     * @param loggerFactory the logger factory
     */
    public DefaultOrderProgressService(OrderRepository orderRepository, 
                                      PhotoTemplateService photoTemplateService,
                                      LoggerFactory loggerFactory) {
        this.orderRepository = orderRepository;
        this.photoTemplateService = photoTemplateService;
        this.logger = loggerFactory.getLogger(DefaultOrderProgressService.class);
        logger.info("DefaultOrderProgressService initialized");
    }

    @Override
    public Optional<OrderBusiness> getAssignedOrder(UserBusiness worker) {
        logger.debug("Getting assigned order for worker: {}, ID: {}", 
                    worker.getUsername().value(), worker.getId().id());

        // Fetch all orders from repository
        logger.debug("Fetching all orders from repository");
        List<OrderBusiness> allOrders = orderRepository.findAll();
        logger.debug("Found {} total orders", allOrders.size());

        // Filter orders that are assigned to this worker
        logger.debug("Filtering for orders assigned to worker");
        List<OrderBusiness> assignedOrders = allOrders.stream()
                .filter(order -> {
                    if (order.getAssignedTo() == null) {
                        return false;
                    }

                    String orderAssignedToId = order.getAssignedTo().id().id();
                    String workerId = worker.getId().id();

                    // Compare case-insensitive to handle UUID case differences
                    boolean isAssigned = orderAssignedToId.equalsIgnoreCase(workerId);

                    logger.trace("Order {} assigned to: {}, matches worker: {}", 
                                order.getId().id(), 
                                orderAssignedToId, 
                                isAssigned);

                    return isAssigned;
                })
                .collect(Collectors.toList());

        logger.debug("Found {} orders assigned to worker", assignedOrders.size());

        if (assignedOrders.isEmpty()) {
            logger.debug("No assigned orders found for worker");
            return Optional.empty();
        } else {
            OrderBusiness assignedOrder = assignedOrders.get(0);
            String orderNumber = assignedOrder.getOrderNumber() != null ? assignedOrder.getOrderNumber().value() : "null";
            logger.debug("Returning first assigned order: {}, Number: {}", 
                        assignedOrder.getId().id(), orderNumber);
            return Optional.of(assignedOrder);
        }
    }

    @Override
    public boolean completeOrder(OrderId orderId, UserBusiness completedBy) {
        logger.info("Attempting to complete order with ID: {} by user: {}", 
                   orderId.id(), completedBy.getUsername().value());

        Optional<OrderBusiness> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            OrderBusiness order = orderOpt.get();
            logger.debug("Found order with number: {}, status: {}", 
                        (order.getOrderNumber() != null ? order.getOrderNumber().value() : "null"), 
                        order.getStatus());

            // Check if the order has all required photos using the PhotoTemplateService
            boolean hasAllPhotos = photoTemplateService.hasAllRequiredPhotos(orderId);
            logger.debug("Order has all required photos: {}", hasAllPhotos);

            if (!hasAllPhotos) {
                logger.warn("Cannot complete order - missing required photos");
                return false;
            }

            // Complete the order
            try {
                logger.debug("Changing order status to COMPLETED");
                order.completeProcessing();

                // Save the order
                logger.debug("Saving updated order status");
                orderRepository.save(order);
                logger.info("Order successfully completed: {}", orderId.id());
                return true;
            } catch (Exception e) {
                logger.error("Error completing order: {}", e.getMessage(), e);
                return false;
            }
        } else {
            logger.warn("Order not found with ID: {}", orderId.id());
            return false;
        }
    }
}
