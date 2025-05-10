package com.belman.domain.order;

/**
 * Enum representing the status of an order throughout its lifecycle.
 * This is core to the order bounded context.
 */
public enum OrderStatus {
    /**
     * OrderBusiness has been created but not yet started.
     */
    PENDING,

    /**
     * OrderBusiness is currently being processed.
     */
    IN_PROGRESS,

    /**
     * OrderBusiness has been completed but not yet approved.
     */
    COMPLETED,

    /**
     * OrderBusiness has been approved by QA.
     */
    APPROVED,

    /**
     * OrderBusiness has been rejected by QA and needs rework.
     */
    REJECTED,

    /**
     * OrderBusiness has been delivered to the customer.
     */
    DELIVERED,

    /**
     * OrderBusiness has been cancelled.
     */
    CANCELLED
}