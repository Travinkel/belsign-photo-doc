package com.belman.domain.audit;

import com.belman.domain.audit.event.AuditEvent;
import com.belman.domain.event.AuditableBusinessEvent;
import com.belman.domain.order.photo.PhotoId;
import com.belman.domain.user.UserId;

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
     * @param photoId    the ID of the approved photo
     * @param approverId the ID of the user who approved the photo
     */
    void logPhotoApproved(PhotoId photoId, UserId approverId);

    /**
     * Convenience method for logging a photo rejection event.
     *
     * @param photoId    the ID of the rejected photo
     * @param rejecterId the ID of the user who rejected the photo
     * @param reason     the reason for rejection
     */
    void logPhotoRejected(PhotoId photoId, UserId rejecterId, String reason);

    /**
     * Retrieves audit events for a specific entity.
     *
     * @param entityType the type of entity
     * @param entityId   the ID of the entity
     * @return a list of audit events for the entity
     */
    List<AuditEvent> getEventsByEntity(String entityType, String entityId);

    /**
     * Logs an auditable business event.
     * <p>
     * This method converts the auditable business event to an audit event
     * and logs it using the {@link #logEvent(AuditEvent)} method.
     *
     * @param event the auditable business event to log
     */
    void logBusinessEvent(AuditableBusinessEvent event);

    /**
     * Logs multiple auditable business events in a batch.
     * <p>
     * This method converts the auditable business events to audit events
     * and logs them using the {@link #logBatch(List)} method.
     *
     * @param events the list of auditable business events to log
     */
    void logBusinessEvents(List<AuditableBusinessEvent> events);

    /**
     * Retrieves audit events for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of audit events for the user
     */
    List<AuditEvent> getEventsByUser(String userId);

    /**
     * Retrieves audit events of a specific type.
     *
     * @param eventType the type of event
     * @return a list of audit events of the specified type
     */
    List<AuditEvent> getEventsByType(String eventType);
}
