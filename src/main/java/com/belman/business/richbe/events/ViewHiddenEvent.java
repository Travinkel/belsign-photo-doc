package com.belman.business.richbe.events;

import java.time.Instant;

/**
 * Event that is published when a view is hidden.
 * This event is fired when a view is no longer visible to the user.
 */
public class ViewHiddenEvent extends AbstractDomainEvent {
    private final String viewName;

    /**
     * Creates a new ViewHiddenEvent with the specified view name.
     * 
     * @param viewName the name of the view that was hidden
     */
    public ViewHiddenEvent(String viewName) {
        super();
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

    /**
     * Gets the timestamp when this event occurred.
     * 
     * @return the timestamp
     */
    public Instant getTimestamp() {
        return getOccurredOn();
    }

    @Override
    public String toString() {
        return String.format("ViewHiddenEvent[viewName=%s, timestamp=%s, id=%s]",
                viewName, getOccurredOn(), getEventId());
    }
}
