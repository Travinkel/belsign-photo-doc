package com.belman.domain.audit;

import com.belman.domain.audit.event.AuditEvent;

import java.util.List;

/**
 * Repository interface for storing and retrieving audit events.
 * <p>
 * This interface defines the contract for persisting audit events and retrieving them
 * for reporting or analysis purposes. Implementations of this interface handle the
 * details of how audit events are stored (database, file, remote service, etc.).
 */
public interface AuditRepository {

    /**
     * Stores a single audit event.
     *
     * @param event the audit event to store
     */
    void store(AuditEvent event);

    /**
     * Stores multiple audit events in a batch operation.
     * Implementations may optimize this for better performance compared to
     * storing events individually.
     *
     * @param events the list of audit events to store
     */
    void storeAll(List<AuditEvent> events);

    /**
     * Retrieves audit events for a specific entity.
     *
     * @param entityType the type of entity
     * @param entityId   the ID of the entity
     * @return a list of audit events for the entity
     */
    List<AuditEvent> getEventsByEntity(String entityType, String entityId);

    /**
     * Retrieves audit events of a specific type.
     *
     * @param eventType the type of events to retrieve
     * @return a list of audit events of the specified type
     */
    List<AuditEvent> getEventsByType(String eventType);

    /**
     * Retrieves audit events created by a specific user.
     *
     * @param userId the ID of the user who created the events
     * @return a list of audit events created by the user
     */
    List<AuditEvent> getEventsByUser(String userId);
}
