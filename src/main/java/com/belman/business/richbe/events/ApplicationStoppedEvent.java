package com.belman.business.richbe.events;

/**
 * Event that is published when the application is stopped.
 * This event is triggered when the application is being terminated,
 * such as when the user closes the app or the system kills the process.
 */
public class ApplicationStoppedEvent extends ApplicationStateEvent {
    
    /**
     * Creates a new ApplicationStoppedEvent.
     */
    public ApplicationStoppedEvent() {
        super(ApplicationState.STOPPING);
    }
}