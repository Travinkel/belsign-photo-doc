package com.belman.domain.audit.event;

import com.belman.domain.event.BusinessEvent;

/**
 * Interface for audit events in the business model.
 * <p>
 * Audit events are a specific type of business event that represent significant
 * occurrences or state changes within the system that need to be tracked for
 * accountability and traceability purposes.
 */
public interface AuditEvent extends BusinessEvent {
    // Additional audit-specific methods could be added here if needed
}
