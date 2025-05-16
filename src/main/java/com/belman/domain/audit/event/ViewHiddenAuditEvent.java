package com.belman.domain.audit.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when a view is hidden.
 * <p>
 * This event is used to track when views are hidden from the user,
 * which is useful for analytics and debugging purposes.
 */
public class ViewHiddenAuditEvent extends BaseAuditEvent {
    private final String viewName;

    /**
     * Creates a new ViewHiddenAuditEvent with the specified view name.
     *
     * @param viewName the name of the view that was hidden
     */
    public ViewHiddenAuditEvent(String viewName) {
        super();
        this.viewName = viewName;
    }

    /**
     * Creates a new ViewHiddenAuditEvent with the specified ID, timestamp, and view name.
     *
     * @param eventId    the unique identifier for this event
     * @param occurredOn the timestamp when this event occurred
     * @param viewName   the name of the view that was hidden
     */
    public ViewHiddenAuditEvent(UUID eventId, Instant occurredOn, String viewName) {
        super(eventId, occurredOn);
        this.viewName = viewName;
    }

    /**
     * Gets the name of the view that was hidden.
     *
     * @return the view name
     */
    public String getViewName() {
        return viewName;
    }

    @Override
    public String getEventType() {
        return "VIEW_HIDDEN";
    }
}