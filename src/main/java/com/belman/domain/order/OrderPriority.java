package com.belman.domain.order;

/**
 * Enum representing the priority of an order.
 * This is used to indicate the urgency or importance of an order.
 */
public enum OrderPriority {
    /**
     * Low priority order. These orders can be processed when there are no higher priority orders.
     */
    LOW,

    /**
     * Normal priority order. This is the default priority for most orders.
     */
    NORMAL,

    /**
     * High priority order. These orders should be processed before normal and low priority orders.
     */
    HIGH,

    /**
     * Urgent priority order. These orders require immediate attention and should be processed as soon as possible.
     */
    URGENT
}