package com.belman.domain.shared;

/**
 * Event that is published when the application is started.
 * This event is triggered during application initialization.
 */
public class ApplicationStartedEvent extends ApplicationStateEvent {
    
    /**
     * Creates a new ApplicationStartedEvent.
     */
    public ApplicationStartedEvent() {
        super(ApplicationState.STARTING);
    }
}