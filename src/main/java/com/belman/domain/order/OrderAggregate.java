package com.belman.domain.order;

import com.belman.domain.common.Timestamp;
import com.belman.domain.core.AggregateRoot;
import com.belman.domain.customer.CustomerId;
import com.belman.domain.order.events.OrderApprovedEvent;
import com.belman.domain.order.events.OrderCancelledEvent;
import com.belman.domain.order.events.OrderCompletedEvent;
import com.belman.domain.order.events.OrderRejectedEvent;
import com.belman.domain.photo.PhotoDocumentd;
import com.belman.domain.user.UserReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root representing a customer order in the BelSign system.
 * <p>
 * The OrderAggregate aggregate is a central entity in the domain model that represents a customer's
 * order for which photo documentation needs to be collected and managed. It serves as the
 * primary container for photo documents and maintains the lifecycle of an order from creation
 * through completion, approval, and delivery.
 * <p>
 * An OrderAggregate contains:
 * - Basic identification (ID and order number)
 * - Customer information
 * - Product details
 * - Delivery information
 * - Status tracking
 * - Collection of associated photo documents
 * - Metadata about creation
 * <p>
 * The OrderAggregate aggregate enforces business rules related to:
 * - OrderAggregate status transitions
 * - Photo document management
 * - Quality control approval processes
 */
public class OrderAggregate extends AggregateRoot<OrderId> {
    private final OrderId id;
    private OrderNumber orderNumber;
    private CustomerId customerId;
    private ProductDescription productDescription;
    private DeliveryInformation deliveryInformation;
    private OrderStatus status;
    private final UserReference createdBy;
    private final Timestamp createdAt;
    private final List<PhotoDocumentd> photoDocuments = new ArrayList<>();

    /**
     * Creates a new OrderAggregate with the specified ID, creator, and creation time.
     *
     * @param id        the unique identifier for this order
     * @param createdBy the user who created this order
     * @param createdAt the time when this order was created
     */
    public OrderAggregate(OrderId id, UserReference createdBy, Timestamp createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.status = OrderStatus.PENDING;
    }

    /**
     * Creates a new OrderAggregate with the specified ID, order number, creator, and creation time.
     *
     * @param id          the unique identifier for this order
     * @param orderNumber the business order number
     * @param createdBy   the user who created this order
     * @param createdAt   the time when this order was created
     */
    public OrderAggregate(OrderId id, OrderNumber orderNumber, UserReference createdBy, Timestamp createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.status = OrderStatus.PENDING;
    }

    /**
     * Creates a new OrderAggregate with all details.
     *
     * @param id                  the unique identifier for this order
     * @param orderNumber         the business order number
     * @param customerId          the ID of the customer who placed the order
     * @param productDescription  the product description
     * @param deliveryInformation the delivery information
     * @param createdBy           the user who created this order
     * @param createdAt           the time when this order was created
     */
    public OrderAggregate(OrderId id, OrderNumber orderNumber, CustomerId customerId,
                          ProductDescription productDescription, DeliveryInformation deliveryInformation,
                          UserReference createdBy, Timestamp createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
        this.customerId = Objects.requireNonNull(customerId, "customerId must not be null");
        this.productDescription = Objects.requireNonNull(productDescription, "productDescription must not be null");
        this.deliveryInformation = Objects.requireNonNull(deliveryInformation, "deliveryInformation must not be null");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.status = OrderStatus.PENDING;
    }

    /**
     * Returns the unique identifier for this order.
     */
    @Override
    public OrderId getId() {
        return id;
    }

    /**
     * Returns the business order number for this order.
     */
    public OrderNumber getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets or updates the business order number for this order.
     *
     * @param orderNumber the business order number to set
     * @throws NullPointerException if orderNumber is null
     */
    public void setOrderNumber(OrderNumber orderNumber) {
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
    }

    /**
     * Returns the ID of the customer who placed this order.
     */
    public CustomerId getCustomerId() {
        return customerId;
    }

    /**
     * Sets or updates the customer who placed this order.
     *
     * @param customerId the ID of the customer
     * @throws NullPointerException if customerId is null
     */
    public void setCustomerId(CustomerId customerId) {
        this.customerId = Objects.requireNonNull(customerId, "customerId must not be null");
    }

    /**
     * Returns the description of the product being ordered.
     */
    public ProductDescription getProductDescription() {
        return productDescription;
    }

    /**
     * Sets or updates the description of the product being ordered.
     *
     * @param productDescription the product description to set
     * @throws NullPointerException if productDescription is null
     */
    public void setProductDescription(ProductDescription productDescription) {
        this.productDescription = Objects.requireNonNull(productDescription, "productDescription must not be null");
    }

    /**
     * Returns the delivery information for this order.
     */
    public DeliveryInformation getDeliveryInformation() {
        return deliveryInformation;
    }

    /**
     * Sets or updates the delivery information for this order.
     *
     * @param deliveryInformation the delivery information to set
     * @throws NullPointerException if deliveryInformation is null
     */
    public void setDeliveryInformation(DeliveryInformation deliveryInformation) {
        this.deliveryInformation = Objects.requireNonNull(deliveryInformation, "deliveryInformation must not be null");
    }

    /**
     * Returns the current status of this order.
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Updates the status of this order.
     *
     * @param status the new status to set
     * @throws NullPointerException if status is null
     */
    public void setStatus(OrderStatus status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    /**
     * Returns the user who created this order.
     */
    public UserReference getCreatedBy() {
        return createdBy;
    }

    /**
     * Returns the timestamp when this order was created.
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns an unmodifiable view of all photo documents associated with this order.
     */
    public List<PhotoDocumentd> getPhotos() {
        return Collections.unmodifiableList(photoDocuments);
    }

    /**
     * Associates a photo document with this order and adds it to the order's photo collection.
     *
     * @param photo the photo document to add to this order
     * @throws NullPointerException if photo is null
     */
    public void addPhoto(PhotoDocumentd photo) {
        Objects.requireNonNull(photo, "photo must not be null");
        photo.assignToOrder(this.id);
        this.photoDocuments.add(photo);
    }

    /**
     * Returns a filtered list of photo documents that have been approved by QA.
     */
    public List<PhotoDocumentd> getApprovedPhotos() {
        return photoDocuments.stream()
                .filter(PhotoDocumentd::isApproved)
                .toList();
    }

    /**
     * Returns a filtered list of photo documents that are still pending QA review.
     */
    public List<PhotoDocumentd> getPendingPhotos() {
        return photoDocuments.stream()
                .filter(PhotoDocumentd::isPending)
                .toList();
    }

    /**
     * Checks if this order is ready for quality assurance review.
     * An order is ready for QA review when it has at least one photo document
     * and its status is COMPLETED, indicating that production work is finished.
     */
    public boolean isReadyForQaReview() {
        return status == OrderStatus.COMPLETED && !photoDocuments.isEmpty();
    }

    /**
     * Starts processing this order by changing its status to IN_PROGRESS.
     *
     * @throws IllegalStateException if the order is not in PENDING status
     */
    public void startProcessing() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot start processing an order with status: " + status);
        }
        status = OrderStatus.IN_PROGRESS;
    }

    /**
     * Completes processing of this order by changing its status to COMPLETED.
     *
     * @throws IllegalStateException if the order is not in IN_PROGRESS status
     */
    public void completeProcessing() {
        if (status != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete an order with status: " + status);
        }
        status = OrderStatus.COMPLETED;

        // Register domain event
        registerDomainEvent(new OrderCompletedEvent(id, orderNumber, getApprovedPhotos().size()));
    }

    /**
     * Approves this order by changing its status to APPROVED.
     *
     * @throws IllegalStateException if the order is not in COMPLETED status
     */
    public void approve() {
        if (status != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot approve an order with status: " + status);
        }
        status = OrderStatus.APPROVED;

        // Register domain event
        registerDomainEvent(new OrderApprovedEvent(id, orderNumber));
    }

    /**
     * Rejects this order by changing its status to REJECTED.
     *
     * @throws IllegalStateException if the order is not in COMPLETED status
     */
    public void reject() {
        if (status != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot reject an order with status: " + status);
        }
        status = OrderStatus.REJECTED;

        // Register domain event
        registerDomainEvent(new OrderRejectedEvent(id, orderNumber));
    }

    /**
     * Marks this order as delivered by changing its status to DELIVERED.
     *
     * @throws IllegalStateException if the order is not in APPROVED status
     */
    public void deliver() {
        if (status != OrderStatus.APPROVED) {
            throw new IllegalStateException("Cannot deliver an order with status: " + status);
        }
        status = OrderStatus.DELIVERED;
    }

    /**
     * Cancels this order by changing its status to CANCELLED.
     * An order can be cancelled at any stage before delivery.
     *
     * @throws IllegalStateException if the order is already DELIVERED
     */
    public void cancel() {
        if (status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel an order that has already been delivered");
        }

        OrderStatus previousStatus = status;
        status = OrderStatus.CANCELLED;

        // Register domain event
        registerDomainEvent(new OrderCancelledEvent(id, orderNumber, previousStatus));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderAggregate that = (OrderAggregate) o;
        return id.equals(that.id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}