package com.belman.application.usecase.order;

import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.order.OrderStatus;
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

    /**
     * Creates a new DefaultOrderService with the specified OrderRepository.
     *
     * @param orderRepository the order repository
     */
    public DefaultOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Optional<OrderBusiness> getOrderById(OrderId orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public Optional<OrderBusiness> getOrderByNumber(OrderNumber orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Override
    public List<OrderBusiness> getAllOrders() {
        try {
            return orderRepository.findAll();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<OrderBusiness> getOrdersByStatus(OrderStatus status) {
        try {
            OrderStatusSpecification specification = new OrderStatusSpecification(status);
            return orderRepository.findBySpecification(specification);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public OrderBusiness createOrder(OrderNumber orderNumber, UserBusiness createdBy) {
        // Create a new order
        UserReference userRef = new UserReference(createdBy.getId(), createdBy.getUsername());
        Timestamp timestamp = new Timestamp(Instant.now());
        OrderBusiness order = new OrderBusiness(OrderId.newId(), orderNumber, userRef, timestamp);

        // Save the order
        orderRepository.save(order);

        return order;
    }

    @Override
    public boolean updateOrderStatus(OrderId orderId, OrderStatus status, UserBusiness updatedBy) {
        Optional<OrderBusiness> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            OrderBusiness order = orderOpt.get();

            // Update the status
            order.setStatus(status);

            // Save the order
            orderRepository.save(order);
            return true;
        }
        return false;
    }

    @Override
    public boolean cancelOrder(OrderId orderId, UserBusiness cancelledBy) {
        Optional<OrderBusiness> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            OrderBusiness order = orderOpt.get();

            // Cancel the order
            order.cancel();

            // Save the order
            orderRepository.save(order);
            return true;
        }
        return false;
    }

    @Override
    public boolean completeOrder(OrderId orderId, UserBusiness completedBy) {
        Optional<OrderBusiness> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            OrderBusiness order = orderOpt.get();

            // Complete the order
            order.completeProcessing();

            // Save the order
            orderRepository.save(order);
            return true;
        }
        return false;
    }
}
