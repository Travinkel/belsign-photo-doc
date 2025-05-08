package com.belman.business.richbe.order.photo.events;

import com.belman.business.richbe.events.AbstractDomainEvent;
import com.belman.business.richbe.order.photo.PhotoId;
import com.belman.business.richbe.order.OrderId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain event that is published when a photo document is rejected.
 * This event is raised when a photo document transitions from pending to rejected status.
 * Subscribers to this event might include notification services to inform stakeholders,
 * new photo request services to create follow-up tasks, or reporting services to track
 * rejection metrics.
 * <p>
 * This is part of the photo bounded context but exposes information to other contexts.
 */
public final class PhotoRejectedEvent extends AbstractDomainEvent {
    private final PhotoId photoId;
    private final OrderId orderId;
    private final String reason;

    /**
     * Creates a new PhotoRejectedEvent with the specified photo, order IDs, and rejection reason.
     *
     * @param photoId the ID of the photo document that was rejected
     * @param orderId the ID of the order the photo document belongs to
     * @param reason  the reason for rejection (can be null)
     * @throws NullPointerException if photoId or orderId is null
     */
    public PhotoRejectedEvent(PhotoId photoId, OrderId orderId, String reason) {
        super();
        this.photoId = Objects.requireNonNull(photoId, "photoId must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.reason = reason; // reason can be null
    }

    /**
     * Creates a new PhotoRejectedEvent with the specified IDs, event ID, timestamp, and rejection reason.
     * This constructor is primarily used for event reconstitution from storage.
     *
     * @param eventId    the unique identifier for this event
     * @param occurredOn the timestamp when this event occurred
     * @param photoId    the ID of the photo document that was rejected
     * @param orderId    the ID of the order the photo document belongs to
     * @param reason     the reason for rejection (can be null)
     * @throws NullPointerException if eventId, occurredOn, photoId, or orderId is null
     */
    public PhotoRejectedEvent(UUID eventId, Instant occurredOn, PhotoId photoId, OrderId orderId, String reason) {
        super(eventId, occurredOn);
        this.photoId = Objects.requireNonNull(photoId, "photoId must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.reason = reason; // reason can be null
    }

    /**
     * Gets the ID of the photo document that was rejected.
     *
     * @return the photo ID
     */
    public PhotoId getPhotoId() {
        return photoId;
    }

    /**
     * Gets the ID of the order the photo document belongs to.
     *
     * @return the order ID
     */
    public OrderId getOrderId() {
        return orderId;
    }

    /**
     * Gets the reason for rejection.
     *
     * @return the rejection reason, or null if no reason was provided
     */
    public String getReason() {
        return reason;
    }
}