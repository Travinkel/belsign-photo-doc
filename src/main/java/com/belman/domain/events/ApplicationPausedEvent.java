package com.belman.domain.events;

/**
 * Event that is published when the application is paused.
 * This event is triggered when the application loses focus but is still visible,
 * such as when a dialog is shown over the application.
 */
public class ApplicationPausedEvent extends ApplicationStateEvent {
    
    /**
     * Creates a new ApplicationPausedEvent.
     */
    public ApplicationPausedEvent() {
        super(ApplicationState.PAUSED);
    }
}