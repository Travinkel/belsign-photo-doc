package com.belman.business.domain.order;

/**
 * Enum representing the status of an order throughout its lifecycle.
 * This is core to the order bounded context.
 */
public enum OrderStatus {
    /**
     * OrderAggregate has been created but not yet started.
     */
    PENDING,

    /**
     * OrderAggregate is currently being processed.
     */
    IN_PROGRESS,

    /**
     * OrderAggregate has been completed but not yet approved.
     */
    COMPLETED,

    /**
     * OrderAggregate has been approved by QA.
     */
    APPROVED,

    /**
     * OrderAggregate has been rejected by QA and needs rework.
     */
    REJECTED,

    /**
     * OrderAggregate has been delivered to the customer.
     */
    DELIVERED,

    /**
     * OrderAggregate has been cancelled.
     */
    CANCELLED
}