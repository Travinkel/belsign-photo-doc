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
 * Entity representing an order in the system.
 * Contains order details, photos, and metadata about its creation.
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

    public OrderId getId() {
        return id;
    }

    /**
     * @return the business order number, or null if not set
     */
    public OrderNumber getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the business order number for this order.
     * 
     * @param orderNumber the business order number
     * @throws NullPointerException if orderNumber is null
     */
    public void setOrderNumber(OrderNumber orderNumber) {
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
    }

    /**
     * @return the customer who placed the order, or null if not set
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets the customer who placed the order.
     * 
     * @param customer the customer
     * @throws NullPointerException if customer is null
     */
    public void setCustomer(Customer customer) {
        this.customer = Objects.requireNonNull(customer, "customer must not be null");
    }

    /**
     * @return the product description, or null if not set
     */
    public ProductDescription getProductDescription() {
        return productDescription;
    }

    /**
     * Sets the product description.
     * 
     * @param productDescription the product description
     * @throws NullPointerException if productDescription is null
     */
    public void setProductDescription(ProductDescription productDescription) {
        this.productDescription = Objects.requireNonNull(productDescription, "productDescription must not be null");
    }

    /**
     * @return the delivery information, or null if not set
     */
    public DeliveryInformation getDeliveryInformation() {
        return deliveryInformation;
    }

    /**
     * Sets the delivery information.
     * 
     * @param deliveryInformation the delivery information
     * @throws NullPointerException if deliveryInformation is null
     */
    public void setDeliveryInformation(DeliveryInformation deliveryInformation) {
        this.deliveryInformation = Objects.requireNonNull(deliveryInformation, "deliveryInformation must not be null");
    }

    /**
     * @return the current status of the order
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Updates the status of the order.
     * 
     * @param status the new status
     * @throws NullPointerException if status is null
     */
    public void setStatus(OrderStatus status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * @return an unmodifiable view of the photos in this order
     */
    public List<PhotoDocument> getPhotos() {
        return Collections.unmodifiableList(photoDocuments);
    }

    /**
     * Associates a photo with this order and records it in the list.
     * 
     * @param photo the photo to add
     * @throws NullPointerException if photo is null
     */
    public void addPhoto(PhotoDocument photo) {
        Objects.requireNonNull(photo, "photo must not be null");
        photo.assignToOrder(this.id);
        this.photoDocuments.add(photo);
    }

    /**
     * Returns all photos that have been approved.
     */
    public List<PhotoDocument> getApprovedPhotos() {
        return photoDocuments.stream()
                .filter(PhotoDocument::isApproved)
                .toList();
    }

    /**
     * Returns all photos that are still pending review.
     */
    public List<PhotoDocument> getPendingPhotos() {
        return photoDocuments.stream()
                .filter(PhotoDocument::isPending)
                .toList();
    }

    /**
     * @return true if the order is ready for QA review (has photos and is in COMPLETED status)
     */
    public boolean isReadyForQaReview() {
        return status == OrderStatus.COMPLETED && !photoDocuments.isEmpty();
    }

    /**
     * @return true if the order has been approved by QA
     */
    public boolean isApproved() {
        return status == OrderStatus.APPROVED;
    }

    /**
     * @return true if the order has been rejected by QA
     */
    public boolean isRejected() {
        return status == OrderStatus.REJECTED;
    }

    /**
     * @return true if the order has been delivered to the customer
     */
    public boolean isDelivered() {
        return status == OrderStatus.DELIVERED;
    }
}
