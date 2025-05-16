package com.belman.domain.audit.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when a view is shown.
 * <p>
 * This event is used to track when views are displayed to the user,
 * which is useful for analytics and debugging purposes.
 */
public class ViewShownAuditEvent extends BaseAuditEvent {
    private final String viewName;

    /**
     * Creates a new ViewShownAuditEvent with the specified view name.
     *
     * @param viewName the name of the view that was shown
     */
    public ViewShownAuditEvent(String viewName) {
        super();
        this.viewName = viewName;
    }

    /**
     * Creates a new ViewShownAuditEvent with the specified ID, timestamp, and view name.
     *
     * @param eventId    the unique identifier for this event
     * @param occurredOn the timestamp when this event occurred
     * @param viewName   the name of the view that was shown
     */
    public ViewShownAuditEvent(UUID eventId, Instant occurredOn, String viewName) {
        super(eventId, occurredOn);
        this.viewName = viewName;
    }

    /**
     * Gets the name of the view that was shown.
     *
     * @return the view name
     */
    public String getViewName() {
        return viewName;
    }

    @Override
    public String getEventType() {
        return "VIEW_SHOWN";
    }
}