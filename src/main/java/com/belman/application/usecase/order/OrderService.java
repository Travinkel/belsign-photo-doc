package com.belman.application.usecase.order;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.user.UserBusiness;

import java.util.List;
import java.util.Optional;

/**
 * Service for order management.
 * Provides methods for creating, updating, and retrieving orders.
 */
public interface OrderService {
    /**
     * Gets an order by ID.
     *
     * @param orderId the ID of the order to get
     * @return an Optional containing the order if found, or empty if not found
     */
    Optional<OrderBusiness> getOrderById(OrderId orderId);

    /**
     * Gets an order by order number.
     *
     * @param orderNumber the order number of the order to get
     * @return an Optional containing the order if found, or empty if not found
     */
    Optional<OrderBusiness> getOrderByNumber(OrderNumber orderNumber);

    /**
     * Gets all orders.
     *
     * @return a list of all orders
     */
    List<OrderBusiness> getAllOrders();

    /**
     * Gets all orders with the specified status.
     *
     * @param status the status to filter by
     * @return a list of orders with the specified status
     */
    List<OrderBusiness> getOrdersByStatus(OrderStatus status);

    /**
     * Creates a new order with the specified order number.
     *
     * @param orderNumber the order number
     * @param createdBy   the user who created the order
     * @return the created order
     */
    OrderBusiness createOrder(OrderNumber orderNumber, UserBusiness createdBy);

    /**
     * Updates an order's status.
     *
     * @param orderId   the ID of the order to update
     * @param status    the new status
     * @param updatedBy the user who updated the order
     * @return true if the order was updated, false if the order was not found
     */
    boolean updateOrderStatus(OrderId orderId, OrderStatus status, UserBusiness updatedBy);

    /**
     * Cancels an order.
     *
     * @param orderId     the ID of the order to cancel
     * @param cancelledBy the user who cancelled the order
     * @return true if the order was cancelled, false if the order was not found
     */
    boolean cancelOrder(OrderId orderId, UserBusiness cancelledBy);

    /**
     * Completes an order.
     *
     * @param orderId     the ID of the order to complete
     * @param completedBy the user who completed the order
     * @return true if the order was completed, false if the order was not found or cannot be completed
     */
    boolean completeOrder(OrderId orderId, UserBusiness completedBy);
}