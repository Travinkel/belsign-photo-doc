package com.belman.domain.order.events;

import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderStatus;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Audit event that is published when an order is cancelled.
 * This event signifies that the order has been cancelled and will not
 * be processed further.
 */
public class OrderCancelledEvent extends BaseAuditEvent {
    private final OrderId orderId;
    private final OrderNumber orderNumber;
    private final OrderStatus previousStatus;

    /**
     * Creates a new OrderCancelledEvent with the specified order ID, order number, and previous status.
     *
     * @param orderId        the ID of the order that was cancelled
     * @param orderNumber    the business order number
     * @param previousStatus the status of the order before cancellation
     */
    public OrderCancelledEvent(OrderId orderId, OrderNumber orderNumber, OrderStatus previousStatus) {
        super();
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
        this.previousStatus = Objects.requireNonNull(previousStatus, "previousStatus must not be null");
    }

    /**
     * Constructor for event deserialization/reconstitution.
     *
     * @param eventId        the ID of the event
     * @param occurredOn     the timestamp when the event occurred
     * @param orderId        the ID of the order that was cancelled
     * @param orderNumber    the business order number
     * @param previousStatus the status of the order before cancellation
     */
    public OrderCancelledEvent(UUID eventId, Instant occurredOn, OrderId orderId,
                               OrderNumber orderNumber, OrderStatus previousStatus) {
        super(eventId, occurredOn);
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
        this.previousStatus = Objects.requireNonNull(previousStatus, "previousStatus must not be null");
    }

    /**
     * Returns the ID of the order that was cancelled.
     *
     * @return the order ID
     */
    public OrderId getOrderId() {
        return orderId;
    }

    /**
     * Returns the business order number of the order that was cancelled.
     *
     * @return the order number
     */
    public OrderNumber getOrderNumber() {
        return orderNumber;
    }

    /**
     * Returns the status of the order before it was cancelled.
     *
     * @return the previous status
     */
    public OrderStatus getPreviousStatus() {
        return previousStatus;
    }
}
