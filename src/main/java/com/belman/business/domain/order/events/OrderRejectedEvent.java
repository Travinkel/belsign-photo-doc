package com.belman.business.domain.order.events;

import com.belman.business.domain.events.AbstractDomainEvent;
import com.belman.business.domain.order.OrderId;
import com.belman.business.domain.order.OrderNumber;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain event that is published when an order is rejected by quality assurance.
 * This event signifies that the order has failed quality checks and needs to be
 * reworked before it can be approved.
 */
public class OrderRejectedEvent extends AbstractDomainEvent {
    private final OrderId orderId;
    private final OrderNumber orderNumber;

    /**
     * Creates a new OrderRejectedEvent with the specified order ID and order number.
     *
     * @param orderId     the ID of the order that was rejected
     * @param orderNumber the business order number
     */
    public OrderRejectedEvent(OrderId orderId, OrderNumber orderNumber) {
        super();
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
    }

    /**
     * Constructor for event deserialization/reconstitution.
     *
     * @param eventId     the ID of the event
     * @param occurredOn  the timestamp when the event occurred
     * @param orderId     the ID of the order that was rejected
     * @param orderNumber the business order number
     */
    public OrderRejectedEvent(UUID eventId, Instant occurredOn, OrderId orderId, OrderNumber orderNumber) {
        super(eventId, occurredOn);
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
    }

    /**
     * Returns the ID of the order that was rejected.
     *
     * @return the order ID
     */
    public OrderId getOrderId() {
        return orderId;
    }

    /**
     * Returns the business order number of the order that was rejected.
     *
     * @return the order number
     */
    public OrderNumber getOrderNumber() {
        return orderNumber;
    }
}