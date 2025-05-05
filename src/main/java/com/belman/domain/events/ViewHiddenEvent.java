package com.belman.domain.events;

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

    @Override
    public String toString() {
        return String.format("ViewHiddenEvent[viewName=%s, timestamp=%s, id=%s]",
                viewName, getTimestamp(), getEventId());
    }
}