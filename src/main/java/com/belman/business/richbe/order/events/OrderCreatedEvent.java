package com.belman.business.richbe.order.events;

import com.belman.business.richbe.order.OrderId;
import com.belman.business.richbe.order.OrderNumber;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain event that is published when an order is created.
 * This is part of the order bounded context.
 */
public final class OrderCreatedEvent {
    private final String eventId;
    private final Instant timestamp;
    private final OrderId orderId;
    private final OrderNumber orderNumber;

    /**
     * Creates a new OrderCreatedEvent.
     *
     * @param orderId     the ID of the order that was created
     * @param orderNumber the order number of the order that was created
     */
    public OrderCreatedEvent(OrderId orderId, OrderNumber orderNumber) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
    }

    /**
     * Gets the unique identifier of this event.
     *
     * @return the event ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Gets the timestamp when this event occurred.
     *
     * @return the timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
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