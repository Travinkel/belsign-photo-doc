package com.belman.domain.order;

import com.belman.domain.customer.CustomerId;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing orders.
 * This interface follows the Repository pattern from Domain-Driven Design.
 */
public interface OrderRepository {

    /**
     * Finds an order by ID.
     *
     * @param id the order ID to search for
     * @return an Optional containing the order if found, or empty if not found
     */
    Optional<OrderAggregate> findById(OrderId id);

    /**
     * Finds an order by order number.
     *
     * @param orderNumber the order number to search for
     * @return an Optional containing the order if found, or empty if not found
     */
    Optional<OrderAggregate> findByOrderNumber(OrderNumber orderNumber);

    /**
     * Finds all orders for a specific customer.
     *
     * @param customerId the customer ID to search for
     * @return a list of orders for the specified customer
     */
    List<OrderAggregate> findByCustomerId(CustomerId customerId);

    /**
     * Finds all orders with a specific status.
     *
     * @param status the order status to search for
     * @return a list of orders with the specified status
     */
    List<OrderAggregate> findByStatus(OrderStatus status);

    /**
     * Saves an order.
     * If the order already exists, it will be updated.
     * If the order does not exist, it will be created.
     *
     * @param order the order to save
     */
    void save(OrderAggregate order);

    /**
     * Deletes an order by ID.
     *
     * @param id the ID of the order to delete
     * @return true if the order was deleted, false if the order was not found
     */
    boolean delete(OrderId id);

    /**
     * Gets all orders.
     *
     * @return a list of all orders
     */
    List<OrderAggregate> findAll();
}