package com.belman.domain.order.events;

import com.belman.domain.events.AbstractDomainEvent;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain event that is published when an order's processing is completed.
 * This event signifies that production work is finished and the order is
 * ready for quality assurance review.
 */
public class OrderCompletedEvent extends AbstractDomainEvent {
    private final OrderId orderId;
    private final OrderNumber orderNumber;
    private final int photoCount;

    /**
     * Creates a new OrderCompletedEvent with the specified order ID and order number.
     *
     * @param orderId     the ID of the order that was completed
     * @param orderNumber the business order number
     * @param photoCount  the number of approved photos associated with the order
     */
    public OrderCompletedEvent(OrderId orderId, OrderNumber orderNumber, int photoCount) {
        super();
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
        this.photoCount = photoCount;
    }

    /**
     * Constructor for event deserialization/reconstitution.
     *
     * @param eventId     the ID of the event
     * @param occurredOn  the timestamp when the event occurred
     * @param orderId     the ID of the order that was completed
     * @param orderNumber the business order number
     * @param photoCount  the number of approved photos associated with the order
     */
    public OrderCompletedEvent(UUID eventId, Instant occurredOn, OrderId orderId, OrderNumber orderNumber,
                               int photoCount) {
        super(eventId, occurredOn);
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
        this.photoCount = photoCount;
    }

    /**
     * Returns the ID of the order that was completed.
     *
     * @return the order ID
     */
    public OrderId getOrderId() {
        return orderId;
    }

    /**
     * Returns the business order number of the order that was completed.
     *
     * @return the order number
     */
    public OrderNumber getOrderNumber() {
        return orderNumber;
    }

    /**
     * Returns the number of approved photos associated with the order when it was completed.
     *
     * @return the number of approved photos
     */
    public int getPhotoCount() {
        return photoCount;
    }
}