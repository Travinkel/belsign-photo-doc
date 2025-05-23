package com.belman.domain.order.events;

import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Audit event that is published when an order is approved by quality assurance.
 * This event signifies that the order has passed all quality checks and is
 * ready to be delivered to the customer.
 */
public class OrderApprovedEvent extends BaseAuditEvent {
    private final OrderId orderId;
    private final OrderNumber orderNumber;

    /**
     * Creates a new OrderApprovedEvent with the specified order ID and order number.
     *
     * @param orderId     the ID of the order that was approved
     * @param orderNumber the business order number
     */
    public OrderApprovedEvent(OrderId orderId, OrderNumber orderNumber) {
        super();
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
    }

    /**
     * Constructor for event deserialization/reconstitution.
     *
     * @param eventId     the ID of the event
     * @param occurredOn  the timestamp when the event occurred
     * @param orderId     the ID of the order that was approved
     * @param orderNumber the business order number
     */
    public OrderApprovedEvent(UUID eventId, Instant occurredOn, OrderId orderId, OrderNumber orderNumber) {
        super(eventId, occurredOn);
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
    }

    /**
     * Returns the ID of the order that was approved.
     *
     * @return the order ID
     */
    public OrderId getOrderId() {
        return orderId;
    }

    /**
     * Returns the business order number of the order that was approved.
     *
     * @return the order number
     */
    public OrderNumber getOrderNumber() {
        return orderNumber;
    }
}
