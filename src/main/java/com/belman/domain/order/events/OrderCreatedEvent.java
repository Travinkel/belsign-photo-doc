package com.belman.domain.order.events;

import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Audit event that is published when an order is created.
 * This is part of the order bounded context.
 */
public final class OrderCreatedEvent extends BaseAuditEvent {
    private final OrderId orderId;
    private final OrderNumber orderNumber;

    /**
     * Creates a new OrderCreatedEvent.
     *
     * @param orderId     the ID of the order that was created
     * @param orderNumber the order number of the order that was created
     */
    public OrderCreatedEvent(OrderId orderId, OrderNumber orderNumber) {
        super();
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
    }

    /**
     * Constructor for event deserialization/reconstitution.
     *
     * @param eventId     the ID of the event
     * @param occurredOn  the timestamp when the event occurred
     * @param orderId     the ID of the order that was created
     * @param orderNumber the order number of the order that was created
     */
    public OrderCreatedEvent(UUID eventId, Instant occurredOn, OrderId orderId, OrderNumber orderNumber) {
        super(eventId, occurredOn);
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
    }

    /**
     * Gets the ID of the order that was created.
     *
     * @return the order ID
     */
    public OrderId getOrderId() {
        return orderId;
    }

    /**
     * Gets the order number of the order that was created.
     *
     * @return the order number
     */
    public OrderNumber getOrderNumber() {
        return orderNumber;
    }
}
