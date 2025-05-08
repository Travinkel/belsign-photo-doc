package com.belman.business.audit;

import com.belman.business.richbe.events.AuditEvent;
import com.belman.business.richbe.order.photo.PhotoId;
import com.belman.business.richbe.user.UserId;

import java.time.Instant;
import java.util.List;

/**
 * Central facade for all audit logging operations.
 * <p>
 * This facade provides a unified interface for logging audit events across the application.
 * It centralizes audit logic, ensuring consistent handling and formatting of audit data.
 * Business objects don't need to know how audit events are stored or processed;
 * they just emit them through this facade.
 */
public interface AuditFacade {
    
    /**
     * Logs a single audit event.
     *
     * @param event the audit event to log
     */
    void logEvent(AuditEvent event);
    
    /**
     * Logs multiple audit events in a batch.
     *
     * @param events the list of audit events to log
     */
    void logBatch(List<AuditEvent> events);
    
    /**
     * Convenience method for logging a photo approval event.
     *
     * @param photoId the ID of the approved photo
     * @param approverId the ID of the user who approved the photo
     */
    void logPhotoApproved(PhotoId photoId, UserId approverId);
    
    /**
     * Convenience method for logging a photo rejection event.
     *
     * @param photoId the ID of the rejected photo
     * @param rejecterId the ID of the user who rejected the photo
     * @param reason the reason for rejection
     */
    void logPhotoRejected(PhotoId photoId, UserId rejecterId, String reason);
    
    /**
     * Retrieves audit events for a specific entity.
     *
     * @param entityType the type of entity
     * @param entityId the ID of the entity
     * @return a list of audit events for the entity
     */
    List<AuditEvent> getEventsByEntity(String entityType, String entityId);
}