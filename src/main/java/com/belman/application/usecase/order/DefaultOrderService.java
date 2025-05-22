package com.belman.application.usecase.order;

import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.specification.OrderStatusSpecification;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserReference;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of the OrderService interface.
 * This service provides order management functionality.
 */
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final Logger logger;

    /**
     * Creates a new DefaultOrderService with the specified OrderRepository and LoggerFactory.
     *
     * @param orderRepository the order repository
     * @param loggerFactory the logger factory
     */
    public DefaultOrderService(OrderRepository orderRepository, LoggerFactory loggerFactory) {
        this.orderRepository = orderRepository;
        this.logger = loggerFactory.getLogger(DefaultOrderService.class);
        logger.info("DefaultOrderService initialized");
    }

    @Override
    public Optional<OrderBusiness> getOrderById(OrderId orderId) {
        logger.debug("Getting order by ID: {}", orderId);
        Optional<OrderBusiness> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            logger.debug("Found order with ID: {}", orderId);
        } else {
            logger.debug("Order not found with ID: {}", orderId);
        }
        return order;
    }

    @Override
    public Optional<OrderBusiness> getOrderByNumber(OrderNumber orderNumber) {
        logger.debug("Getting order by number: {}", orderNumber);
        Optional<OrderBusiness> order = orderRepository.findByOrderNumber(orderNumber);
        if (order.isPresent()) {
            logger.debug("Found order with number: {}", orderNumber);
        } else {
            logger.debug("Order not found with number: {}", orderNumber);
        }
        return order;
    }

    @Override
    public List<OrderBusiness> getAllOrders() {
        logger.debug("Getting all orders");
        try {
            List<OrderBusiness> orders = orderRepository.findAll();
            logger.debug("Retrieved {} orders", orders.size());
            return orders;
        } catch (Exception e) {
            logger.error("Error retrieving all orders", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<OrderBusiness> getOrdersByStatus(OrderStatus status) {
        logger.debug("Getting orders by status: {}", status);
        try {
            OrderStatusSpecification specification = new OrderStatusSpecification(status);
            List<OrderBusiness> orders = orderRepository.findBySpecification(specification);
            logger.debug("Retrieved {} orders with status {}", orders.size(), status);
            return orders;
        } catch (Exception e) {
            logger.error("Error retrieving orders with status: {}", status, e);
            return new ArrayList<>();
        }
    }

    @Override
    public OrderBusiness createOrder(OrderNumber orderNumber, UserBusiness createdBy) {
        logger.info("Creating new order with number: {} by user: {}", orderNumber, createdBy.getUsername());

        // Create a new order using the factory method
        OrderBusiness order = OrderBusiness.createNew(orderNumber, createdBy);

        // Save the order
        OrderBusiness savedOrder = orderRepository.save(order);
        logger.info("Created order with ID: {} and number: {}", savedOrder.getId(), orderNumber);

        return savedOrder;
    }

    @Override
    public boolean updateOrderStatus(OrderId orderId, OrderStatus status, UserBusiness updatedBy) {
        logger.info("Updating order status: Order ID: {}, New status: {}, Updated by: {}", 
                orderId, status, updatedBy.getUsername());

        Optional<OrderBusiness> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            OrderBusiness order = orderOpt.get();
            OrderStatus oldStatus = order.getStatus();

            // Update the status
            order.setStatus(status);

            // Save the order
            orderRepository.save(order);
            logger.info("Order status updated: Order ID: {}, Old status: {}, New status: {}", 
                    orderId, oldStatus, status);
            return true;
        } else {
            logger.warn("Failed to update order status: Order not found with ID: {}", orderId);
            return false;
        }
    }

    @Override
    public boolean cancelOrder(OrderId orderId, UserBusiness cancelledBy) {
        logger.info("Cancelling order: Order ID: {}, Cancelled by: {}", 
                orderId, cancelledBy.getUsername());

        Optional<OrderBusiness> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            OrderBusiness order = orderOpt.get();

            // Cancel the order
            order.cancel();

            // Save the order
            orderRepository.save(order);
            logger.info("Order cancelled: Order ID: {}, Order number: {}", 
                    orderId, order.getOrderNumber());
            return true;
        } else {
            logger.warn("Failed to cancel order: Order not found with ID: {}", orderId);
            return false;
        }
    }

    @Override
    public boolean completeOrder(OrderId orderId, UserBusiness completedBy) {
        logger.info("Completing order: Order ID: {}, Completed by: {}", 
                orderId, completedBy.getUsername());

        Optional<OrderBusiness> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            OrderBusiness order = orderOpt.get();

            // Complete the order
            order.completeProcessing();

            // Save the order
            orderRepository.save(order);
            logger.info("Order completed: Order ID: {}, Order number: {}", 
                    orderId, order.getOrderNumber());
            return true;
        } else {
            logger.warn("Failed to complete order: Order not found with ID: {}", orderId);
            return false;
        }
    }

    @Override
    public OrderBusiness saveOrder(OrderBusiness order) {
        logger.debug("Saving order: ID: {}, Number: {}, Status: {}", 
                order.getId(), order.getOrderNumber(), order.getStatus());

        OrderBusiness savedOrder = orderRepository.save(order);
        logger.debug("Order saved successfully: ID: {}", savedOrder.getId());
        return savedOrder;
    }
}
