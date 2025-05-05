package com.belman.domain.shared;

/**
 * Event that is published when a view is hidden.
 * This event is fired when a view is no longer visible to the user.
 * 
 * @deprecated This class is deprecated and will be removed in a future release.
 * Use {@link com.belman.domain.events.ViewHiddenEvent} instead.
 */
@Deprecated
public class ViewHiddenEvent extends com.belman.domain.events.ViewHiddenEvent {

    /**
     * Creates a new ViewHiddenEvent with the specified view name.
     * 
     * @param viewName the name of the view that was hidden
     */
    public ViewHiddenEvent(String viewName) {
        super(viewName);
    }
}
