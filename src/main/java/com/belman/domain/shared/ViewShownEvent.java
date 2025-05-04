package com.belman.domain.shared;

/**
 * Event that is published when a view is shown.
 * This is a sample domain event that demonstrates how the domain event system can be used.
 * 
 * @deprecated This class is deprecated and will be removed in a future release.
 * Use {@link com.belman.domain.events.ViewShownEvent} instead.
 */
@Deprecated
public class ViewShownEvent extends com.belman.domain.events.ViewShownEvent {

    /**
     * Creates a new ViewShownEvent with the specified view name.
     * 
     * @param viewName the name of the view that was shown
     */
    public ViewShownEvent(String viewName) {
        super(viewName);
    }
}
