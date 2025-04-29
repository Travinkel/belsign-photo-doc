package com.belman.belsign.domain.model.order;

/**
 * Enum representing the status of an order.
 */
public enum OrderStatus {
    /**
     * Order has been created but not yet started.
     */
    PENDING,
    
    /**
     * Order is currently being processed.
     */
    IN_PROGRESS,
    
    /**
     * Order has been completed but not yet approved.
     */
    COMPLETED,
    
    /**
     * Order has been approved by QA.
     */
    APPROVED,
    
    /**
     * Order has been rejected by QA and needs rework.
     */
    REJECTED,
    
    /**
     * Order has been delivered to the customer.
     */
    DELIVERED,
    
    /**
     * Order has been cancelled.
     */
    CANCELLED
}