package com.belman.domain.audit;

import com.belman.domain.audit.event.AuditEvent;
import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.domain.event.AuditableBusinessEvent;
import com.belman.domain.order.photo.PhotoId;
import com.belman.domain.services.Logger;
import com.belman.domain.user.UserId;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Default implementation of the AuditFacade interface.
 * <p>
 * This class provides a centralized implementation for logging audit events
 * across the application. It delegates the storage of audit events to an
 * AuditRepository implementation.
 */
public class DefaultAuditFacade implements AuditFacade {

    private final AuditRepository auditRepository;
    private final Logger logger;

    /**
     * Creates a new DefaultAuditFacade with the specified repository and logger.
     *
     * @param auditRepository the repository for storing audit events
     * @param logger          the logger for logging audit operations
     */
    public DefaultAuditFacade(AuditRepository auditRepository, Logger logger) {
        this.auditRepository = Objects.requireNonNull(auditRepository, "auditRepository must not be null");
        this.logger = Objects.requireNonNull(logger, "logger must not be null");
    }

    @Override
    public void logEvent(AuditEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        logger.debug("Logging audit event: {}", event.getEventType());
        auditRepository.store(event);
    }

    @Override
    public void logBatch(List<AuditEvent> events) {
        Objects.requireNonNull(events, "events must not be null");
        if (events.isEmpty()) {
            logger.debug("No audit events to log in batch");
            return;
        }

        logger.debug("Logging batch of {} audit events", events.size());
        auditRepository.storeAll(events);
    }

    @Override
    public void logPhotoApproved(PhotoId photoId, UserId approverId) {
        Objects.requireNonNull(photoId, "photoId must not be null");
        Objects.requireNonNull(approverId, "approverId must not be null");

        logger.debug("Logging photo approval: photoId={}, approverId={}", photoId, approverId);

        // Create and store a photo approved audit event
        PhotoApprovedAuditEvent event = new PhotoApprovedAuditEvent(photoId, approverId);
        auditRepository.store(event);
    }

    @Override
    public void logPhotoRejected(PhotoId photoId, UserId rejecterId, String reason) {
        Objects.requireNonNull(photoId, "photoId must not be null");
        Objects.requireNonNull(rejecterId, "rejecterId must not be null");

        logger.debug("Logging photo rejection: photoId={}, rejecterId={}, reason={}",
                photoId, rejecterId, reason);

        // Create and store a photo rejected audit event
        PhotoRejectedAuditEvent event = new PhotoRejectedAuditEvent(photoId, rejecterId, reason);
        auditRepository.store(event);
    }

    @Override
    public List<AuditEvent> getEventsByEntity(String entityType, String entityId) {
        Objects.requireNonNull(entityType, "entityType must not be null");
        Objects.requireNonNull(entityId, "entityId must not be null");

        logger.debug("Retrieving audit events for entity: type={}, id={}", entityType, entityId);
        return auditRepository.getEventsByEntity(entityType, entityId);
    }

    @Override
    public void logBusinessEvent(AuditableBusinessEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        logger.debug("Logging business event: {}", event.getEventType());

        // Create and store a business audit event
        BusinessAuditEvent auditEvent = new BusinessAuditEvent(
                event.getEventId(),
                event.getOccurredOn(),
                event.getAuditEntityType(),
                event.getAuditEntityId(),
                event.getAuditUserId(),
                event.getAuditAction(),
                event.getAuditDetails()
        );

        auditRepository.store(auditEvent);
    }

    @Override
    public void logBusinessEvents(List<AuditableBusinessEvent> events) {
        Objects.requireNonNull(events, "events must not be null");
        if (events.isEmpty()) {
            logger.debug("No business events to log in batch");
            return;
        }

        logger.debug("Logging batch of {} business events", events.size());

        // Convert business events to audit events and collect them
        List<BusinessAuditEvent> businessAuditEvents = events.stream()
                .map(event -> new BusinessAuditEvent(
                        event.getEventId(),
                        event.getOccurredOn(),
                        event.getAuditEntityType(),
                        event.getAuditEntityId(),
                        event.getAuditUserId(),
                        event.getAuditAction(),
                        event.getAuditDetails()
                ))
                .toList();

        // Convert to List<AuditEvent>
        List<AuditEvent> auditEvents = new java.util.ArrayList<>(businessAuditEvents);

        auditRepository.storeAll(auditEvents);
    }

    @Override
    public List<AuditEvent> getEventsByUser(String userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        logger.debug("Retrieving audit events for user: {}", userId);
        return auditRepository.getEventsByUser(userId);
    }

    @Override
    public List<AuditEvent> getEventsByType(String eventType) {
        Objects.requireNonNull(eventType, "eventType must not be null");

        logger.debug("Retrieving audit events of type: {}", eventType);
        return auditRepository.getEventsByType(eventType);
    }

    /**
     * Inner class representing a photo approved audit event.
     */
    private static class PhotoApprovedAuditEvent extends BaseAuditEvent {
        private final PhotoId photoId;
        private final UserId approverId;

        public PhotoApprovedAuditEvent(PhotoId photoId, UserId approverId) {
            super(UUID.randomUUID(), Instant.now());
            this.photoId = photoId;
            this.approverId = approverId;
        }

        @Override
        public String getEventType() {
            return "PhotoApproved";
        }

        public PhotoId getPhotoId() {
            return photoId;
        }

        public UserId getApproverId() {
            return approverId;
        }
    }

    /**
     * Inner class representing a photo rejected audit event.
     */
    private static class PhotoRejectedAuditEvent extends BaseAuditEvent {
        private final PhotoId photoId;
        private final UserId rejecterId;
        private final String reason;

        public PhotoRejectedAuditEvent(PhotoId photoId, UserId rejecterId, String reason) {
            super(UUID.randomUUID(), Instant.now());
            this.photoId = photoId;
            this.rejecterId = rejecterId;
            this.reason = reason;
        }

        @Override
        public String getEventType() {
            return "PhotoRejected";
        }

        public PhotoId getPhotoId() {
            return photoId;
        }

        public UserId getRejecterId() {
            return rejecterId;
        }

        public String getReason() {
            return reason;
        }
    }

    /**
     * Inner class representing a business audit event.
     * <p>
     * This class is used to convert AuditableBusinessEvent instances to AuditEvent instances
     * for storage in the audit repository.
     */
    private static class BusinessAuditEvent extends BaseAuditEvent {
        private final String entityType;
        private final String entityId;
        private final String userId;
        private final String action;
        private final String details;

        public BusinessAuditEvent(UUID eventId, Instant occurredOn, String entityType, String entityId, String userId,
                                  String action, String details) {
            super(eventId, occurredOn);
            this.entityType = entityType;
            this.entityId = entityId;
            this.userId = userId;
            this.action = action;
            this.details = details;
        }

        @Override
        public String getEventType() {
            return action;
        }

        public String getEntityType() {
            return entityType;
        }

        public String getEntityId() {
            return entityId;
        }

        public String getUserId() {
            return userId;
        }

        public String getAction() {
            return action;
        }

        public String getDetails() {
            return details;
        }
    }
}
