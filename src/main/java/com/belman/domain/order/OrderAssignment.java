package com.belman.domain.order;

import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.common.base.ValueObject;
import com.belman.domain.user.UserReference;

import java.util.Objects;

/**
 * Value object representing an assignment of an order to a worker.
 * This class tracks who assigned the order, who it was assigned to, and when.
 */
public final class OrderAssignment implements ValueObject {

    private final OrderId orderId;
    private final UserReference assignedTo;
    private final UserReference assignedBy;
    private final Timestamp assignedAt;
    private final String notes;

    /**
     * Creates a new OrderAssignment with the specified details.
     *
     * @param orderId    the ID of the order being assigned
     * @param assignedTo the user the order is assigned to
     * @param assignedBy the user who made the assignment
     * @param assignedAt the time when the assignment was made
     * @param notes      optional notes about the assignment (can be null)
     */
    public OrderAssignment(OrderId orderId, UserReference assignedTo, UserReference assignedBy, 
                          Timestamp assignedAt, String notes) {
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.assignedTo = Objects.requireNonNull(assignedTo, "assignedTo must not be null");
        this.assignedBy = Objects.requireNonNull(assignedBy, "assignedBy must not be null");
        this.assignedAt = Objects.requireNonNull(assignedAt, "assignedAt must not be null");
        this.notes = notes; // notes can be null
    }

    /**
     * Returns the ID of the order being assigned.
     *
     * @return the order ID
     */
    public OrderId getOrderId() {
        return orderId;
    }

    /**
     * Returns the user the order is assigned to.
     *
     * @return the assigned user
     */
    public UserReference getAssignedTo() {
        return assignedTo;
    }

    /**
     * Returns the user who made the assignment.
     *
     * @return the user who assigned the order
     */
    public UserReference getAssignedBy() {
        return assignedBy;
    }

    /**
     * Returns the time when the assignment was made.
     *
     * @return the assignment timestamp
     */
    public Timestamp getAssignedAt() {
        return assignedAt;
    }

    /**
     * Returns any notes about the assignment.
     *
     * @return the assignment notes, or null if none
     */
    public String getNotes() {
        return notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderAssignment that = (OrderAssignment) o;
        return orderId.equals(that.orderId) &&
               assignedTo.equals(that.assignedTo) &&
               assignedBy.equals(that.assignedBy) &&
               assignedAt.equals(that.assignedAt) &&
               Objects.equals(notes, that.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, assignedTo, assignedBy, assignedAt, notes);
    }

    @Override
    public String toString() {
        return "OrderAssignment{" +
               "orderId=" + orderId +
               ", assignedTo=" + assignedTo +
               ", assignedBy=" + assignedBy +
               ", assignedAt=" + assignedAt +
               ", notes='" + notes + '\'' +
               '}';
    }
}