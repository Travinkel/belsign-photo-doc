package com.belman.belsign.domain.model.order;

import com.belman.belsign.domain.model.order.photodocument.PhotoDocument;
import com.belman.belsign.domain.model.order.photodocument.PhotoId;
import com.belman.belsign.domain.model.shared.Timestamp;
import com.belman.belsign.domain.model.user.User;

import java.util.Objects;
import java.util.UUID;
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
}
