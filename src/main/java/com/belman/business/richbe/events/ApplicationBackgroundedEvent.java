package com.belman.business.richbe.events;

/**
 * Event that is published when the application goes to the background.
 * This event is triggered when the application is no longer visible to the user,
 * such as when the user switches to another app or returns to the home screen.
 */
public class ApplicationBackgroundedEvent extends ApplicationStateEvent {
    
    /**
     * Creates a new ApplicationBackgroundedEvent.
     */
    public ApplicationBackgroundedEvent() {
        super(ApplicationState.BACKGROUND);
    }
}