package com.belman.business.domain.events;

/**
 * Event that is published when the application is resumed.
 * This event is triggered when the application regains focus after being paused,
 * such as when returning to the application from a dialog or another app.
 */
public class ApplicationResumedEvent extends ApplicationStateEvent {
    
    /**
     * Creates a new ApplicationResumedEvent.
     */
    public ApplicationResumedEvent() {
        super(ApplicationState.ACTIVE);
    }
}