package com.belman.domain.order.photo.events;

import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.photo.PhotoId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Audit event that is published when a photo document is approved.
 * This event is raised when a photo document transitions from pending to approved status.
 * Subscribers to this event might include notification services to inform stakeholders,
 * reporting services to update completion metrics, or ordering services that wait for
 * photo approval before proceeding.
 */
public final class PhotoApprovedEvent extends BaseAuditEvent {
    private final PhotoId photoId;
    private final OrderId orderId;

    /**
     * Creates a new PhotoApprovedEvent with the specified photo and order IDs.
     *
     * @param photoId the ID of the photo document that was approved
     * @param orderId the ID of the order the photo document belongs to
     * @throws NullPointerException if photoId or orderId is null
     */
    public PhotoApprovedEvent(PhotoId photoId, OrderId orderId) {
        super();
        this.photoId = Objects.requireNonNull(photoId, "photoId must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
    }

    /**
     * Creates a new PhotoApprovedEvent with the specified IDs, event ID, and timestamp.
     * This constructor is primarily used for event reconstitution from storage.
     *
     * @param eventId    the unique identifier for this event
     * @param occurredOn the timestamp when this event occurred
     * @param photoId    the ID of the photo document that was approved
     * @param orderId    the ID of the order the photo document belongs to
     * @throws NullPointerException if any parameter is null
     */
    public PhotoApprovedEvent(UUID eventId, Instant occurredOn, PhotoId photoId, OrderId orderId) {
        super(eventId, occurredOn);
        this.photoId = Objects.requireNonNull(photoId, "photoId must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
    }

    /**
     * Gets the ID of the photo document that was approved.
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
}
