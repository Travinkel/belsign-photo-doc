package com.belman.domain.event;

import com.belman.domain.audit.Auditable;

import java.time.Instant;
import java.util.UUID;

/**
 * Abstract base class for business events that can be audited.
 * <p>
 * This class extends BaseBusinessEvent and implements the Auditable interface,
 * providing a bridge between the business event system and the audit system.
 * <p>
 * Subclasses should provide implementations for the audit-specific methods
 * to ensure proper integration with the audit system.
 */
public abstract class AuditableBusinessEvent extends BaseBusinessEvent implements Auditable {

    private final String entityType;
    private final String entityId;
    private final String userId;
    private final String action;
    private final String details;

    /**
     * Creates a new auditable business event with the specified audit information.
     *
     * @param entityType the type of entity being audited
     * @param entityId   the ID of the entity being audited
     * @param userId     the ID of the user who performed the action
     * @param action     the action being performed
     * @param details    additional details about the action
     */
    protected AuditableBusinessEvent(String entityType, String entityId, String userId, String action, String details) {
        super();
        this.entityType = entityType;
        this.entityId = entityId;
        this.userId = userId;
        this.action = action;
        this.details = details;
    }

    /**
     * Creates a new auditable business event with the specified event ID, timestamp, and audit information.
     *
     * @param eventId    the unique identifier for this event
     * @param occurredOn the timestamp when this event occurred
     * @param entityType the type of entity being audited
     * @param entityId   the ID of the entity being audited
     * @param userId     the ID of the user who performed the action
     * @param action     the action being performed
     * @param details    additional details about the action
     */
    protected AuditableBusinessEvent(UUID eventId, Instant occurredOn, String entityType, String entityId,
                                     String userId, String action, String details) {
        super(eventId, occurredOn);
        this.entityType = entityType;
        this.entityId = entityId;
        this.userId = userId;
        this.action = action;
        this.details = details;
    }

    @Override
    public String getAuditEntityType() {
        return entityType;
    }

    @Override
    public String getAuditEntityId() {
        return entityId;
    }

    @Override
    public String getAuditUserId() {
        return userId;
    }

    @Override
    public String getAuditAction() {
        return action;
    }

    @Override
    public String getAuditDetails() {
        return details;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "eventId=" + getEventId() +
               ", occurredOn=" + getOccurredOn() +
               ", entityType='" + entityType + '\'' +
               ", entityId='" + entityId + '\'' +
               ", userId='" + userId + '\'' +
               ", action='" + action + '\'' +
               ", details='" + details + '\'' +
               '}';
    }
}