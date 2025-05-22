package com.belman.application.usecase.order;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.user.UserBusiness;

import java.util.Optional;

/**
 * Service for order workflow and status operations.
 * Provides methods for managing order progress and assignments.
 */
public interface OrderProgressService {
    /**
     * Gets the assigned order for a worker.
     *
     * @param worker the worker
     * @return an Optional containing the assigned order if found, or empty if not found
     */
    Optional<OrderBusiness> getAssignedOrder(UserBusiness worker);

    /**
     * Completes an order by marking it as ready for QA review.
     *
     * @param orderId     the ID of the order to complete
     * @param completedBy the user who completed the order
     * @return true if the order was completed, false if the order was not found or cannot be completed
     */
    boolean completeOrder(OrderId orderId, UserBusiness completedBy);
}