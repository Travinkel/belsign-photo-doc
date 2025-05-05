package com.belman.domain.aggregates;

import com.belman.domain.entities.Customer;
import com.belman.domain.entities.PhotoDocument;
import com.belman.domain.valueobjects.OrderNumber;
import com.belman.domain.enums.OrderStatus;
import com.belman.domain.valueobjects.ProductDescription;
import com.belman.domain.valueobjects.Timestamp;
import com.belman.domain.valueobjects.DeliveryInformation;
import com.belman.domain.valueobjects.OrderId;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Aggregate root representing a customer order in the BelSign system.
 * 
 * The Order aggregate is a central entity in the domain model that represents a customer's
 * order for which photo documentation needs to be collected and managed. It serves as the
 * primary container for photo documents and maintains the lifecycle of an order from creation
 * through completion, approval, and delivery.
 * 
 * An Order contains:
 * - Basic identification (ID and order number)
 * - Customer information
 * - Product details
 * - Delivery information
 * - Status tracking
 * - Collection of associated photo documents
 * - Metadata about creation
 * 
 * The Order aggregate enforces business rules related to:
 * - Order status transitions
 * - Photo document management
 * - Quality control approval processes
 * 
 * Orders go through a lifecycle represented by their status:
 * - PENDING: Newly created orders
 * - IN_PROGRESS: Orders being worked on
 * - COMPLETED: Orders with completed production
 * - APPROVED: Orders that have passed QA review
 * - REJECTED: Orders that have failed QA review
 * - DELIVERED: Orders that have been delivered to the customer
 * 
 * This aggregate is responsible for maintaining the integrity of the order and its
 * associated photo documents throughout its lifecycle.
 */
public class Order {
    private final OrderId id;
    private OrderNumber orderNumber;
    private Customer customer;
    private ProductDescription productDescription;
    private DeliveryInformation deliveryInformation;
    private OrderStatus status;
    private final User createdBy;
    private final Timestamp createdAt;
    private final List<PhotoDocument> photoDocuments = new ArrayList<>();

    /**
     * Creates a new Order with the specified ID, creator, and creation time.
     * 
     * @param id the unique identifier for this order
     * @param createdBy the user who created this order
     * @param createdAt the time when this order was created
     */
    public Order(OrderId id, User createdBy, Timestamp createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.status = OrderStatus.PENDING;
    }

    /**
     * Creates a new Order with the specified ID, order number, creator, and creation time.
     * 
     * @param id the unique identifier for this order
     * @param orderNumber the business order number
     * @param createdBy the user who created this order
     * @param createdAt the time when this order was created
     */
    public Order(OrderId id, OrderNumber orderNumber, User createdBy, Timestamp createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.status = OrderStatus.PENDING;
    }

    /**
     * Creates a new Order with all details.
     * 
     * @param id the unique identifier for this order
     * @param orderNumber the business order number
     * @param customer the customer who placed the order
     * @param productDescription the product description
     * @param deliveryInformation the delivery information
     * @param createdBy the user who created this order
     * @param createdAt the time when this order was created
     */
    public Order(OrderId id, OrderNumber orderNumber, Customer customer, 
                ProductDescription productDescription, DeliveryInformation deliveryInformation,
                User createdBy, Timestamp createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
        this.customer = Objects.requireNonNull(customer, "customer must not be null");
        this.productDescription = Objects.requireNonNull(productDescription, "productDescription must not be null");
        this.deliveryInformation = Objects.requireNonNull(deliveryInformation, "deliveryInformation must not be null");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.status = OrderStatus.PENDING;
    }

    /**
     * Returns the unique identifier for this order.
     * This ID is immutable and serves as the primary identifier for the order in the system.
     * 
     * @return the order's unique identifier
     */
    public OrderId getId() {
        return id;
    }

    /**
     * Returns the business order number for this order.
     * The order number is a human-readable identifier used in business processes
     * and customer communications. It follows a specific format (e.g., "01/23-456789-12345678").
     * 
     * @return the business order number, or null if not set
     * @see OrderNumber
     */
    public OrderNumber getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets or updates the business order number for this order.
     * The order number is a human-readable identifier used in business processes
     * and customer communications.
     * 
     * @param orderNumber the business order number to set
     * @throws NullPointerException if orderNumber is null
     * @see OrderNumber
     */
    public void setOrderNumber(OrderNumber orderNumber) {
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
    }

    /**
     * Returns the customer who placed this order.
     * The customer entity contains contact information and other details
     * about the individual or company that placed the order.
     * 
     * @return the customer who placed the order, or null if not set
     * @see Customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets or updates the customer who placed this order.
     * This method associates the order with a specific customer entity
     * containing contact information and other customer details.
     * 
     * @param customer the customer to associate with this order
     * @throws NullPointerException if customer is null
     * @see Customer
     */
    public void setCustomer(Customer customer) {
        this.customer = Objects.requireNonNull(customer, "customer must not be null");
    }

    /**
     * Returns the description of the product being ordered.
     * The product description contains details about the item being manufactured,
     * including its name, specifications, and other relevant information.
     * 
     * @return the product description, or null if not set
     * @see ProductDescription
     */
    public ProductDescription getProductDescription() {
        return productDescription;
    }

    /**
     * Sets or updates the description of the product being ordered.
     * This method associates the order with specific product details
     * that describe what is being manufactured.
     * 
     * @param productDescription the product description to set
     * @throws NullPointerException if productDescription is null
     * @see ProductDescription
     */
    public void setProductDescription(ProductDescription productDescription) {
        this.productDescription = Objects.requireNonNull(productDescription, "productDescription must not be null");
    }

    /**
     * Returns the delivery information for this order.
     * The delivery information contains details about where and when the order
     * should be delivered, including address, estimated delivery time, and any
     * special instructions.
     * 
     * @return the delivery information, or null if not set
     * @see DeliveryInformation
     */
    public DeliveryInformation getDeliveryInformation() {
        return deliveryInformation;
    }

    /**
     * Sets or updates the delivery information for this order.
     * This method associates the order with specific delivery details
     * that describe where and when the order should be delivered.
     * 
     * @param deliveryInformation the delivery information to set
     * @throws NullPointerException if deliveryInformation is null
     * @see DeliveryInformation
     */
    public void setDeliveryInformation(DeliveryInformation deliveryInformation) {
        this.deliveryInformation = Objects.requireNonNull(deliveryInformation, "deliveryInformation must not be null");
    }

    /**
     * Returns the current status of this order.
     * The status represents the order's position in its lifecycle and determines
     * what operations can be performed on it.
     * 
     * @return the current status of the order (PENDING, IN_PROGRESS, COMPLETED, APPROVED, REJECTED, or DELIVERED)
     * @see OrderStatus
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Updates the status of this order.
     * Changing an order's status affects what operations can be performed on it
     * and may trigger business processes (e.g., QA review, customer notification).
     * 
     * @param status the new status to set
     * @throws NullPointerException if status is null
     * @see OrderStatus
     */
    public void setStatus(OrderStatus status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    /**
     * Returns the user who created this order.
     * This information is immutable and provides an audit trail of who initiated the order.
     * 
     * @return the user who created this order
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Returns the timestamp when this order was created.
     * This information is immutable and provides an audit trail of when the order was initiated.
     * 
     * @return the timestamp when this order was created
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns an unmodifiable view of all photo documents associated with this order.
     * The returned list cannot be modified, providing protection against
     * accidental modification of the order's photo collection.
     * 
     * @return an unmodifiable view of the photos in this order
     * @see Collections#unmodifiableList(List)
     */
    public List<PhotoDocument> getPhotos() {
        return Collections.unmodifiableList(photoDocuments);
    }

    /**
     * Associates a photo document with this order and adds it to the order's photo collection.
     * This method also updates the photo document to reference this order by calling
     * {@link PhotoDocument#assignToOrder(OrderId)}.
     * 
     * @param photo the photo document to add to this order
     * @throws NullPointerException if photo is null
     * @see PhotoDocument#assignToOrder(OrderId)
     */
    public void addPhoto(PhotoDocument photo) {
        Objects.requireNonNull(photo, "photo must not be null");
        photo.assignToOrder(this.id);
        this.photoDocuments.add(photo);
    }

    /**
     * Returns a filtered list of photo documents that have been approved by QA.
     * Approved photos have passed quality review and are ready for inclusion in reports.
     * 
     * @return a list containing only the approved photos for this order
     * @see PhotoDocument#isApproved()
     */
    public List<PhotoDocument> getApprovedPhotos() {
        return photoDocuments.stream()
                .filter(PhotoDocument::isApproved)
                .toList();
    }

    /**
     * Returns a filtered list of photo documents that are still pending QA review.
     * Pending photos have been uploaded but not yet approved or rejected.
     * 
     * @return a list containing only the pending photos for this order
     * @see PhotoDocument#isPending()
     */
    public List<PhotoDocument> getPendingPhotos() {
        return photoDocuments.stream()
                .filter(PhotoDocument::isPending)
                .toList();
    }

    /**
     * Checks if this order is ready for quality assurance review.
     * An order is ready for QA review when it has at least one photo document
     * and its status is COMPLETED, indicating that production work is finished.
     * 
     * @return true if the order is ready for QA review, false otherwise
     */
    public boolean isReadyForQaReview() {
        return status == OrderStatus.COMPLETED && !photoDocuments.isEmpty();
    }

    /**
     * Checks if this order has been approved by quality assurance.
     * An approved order has passed QA review and its photo documentation
     * meets the required quality standards.
     * 
     * @return true if the order has been approved by QA, false otherwise
     */
    public boolean isApproved() {
        return status == OrderStatus.APPROVED;
    }

    /**
     * Checks if this order has been rejected by quality assurance.
     * A rejected order has failed QA review and requires corrections
     * before it can be approved.
     * 
     * @return true if the order has been rejected by QA, false otherwise
     */
    public boolean isRejected() {
        return status == OrderStatus.REJECTED;
    }

    /**
     * Checks if this order has been delivered to the customer.
     * A delivered order has completed its lifecycle in the system
     * and its documentation has been sent to the customer.
     * 
     * @return true if the order has been delivered to the customer, false otherwise
     */
    public boolean isDelivered() {
        return status == OrderStatus.DELIVERED;
    }
}
