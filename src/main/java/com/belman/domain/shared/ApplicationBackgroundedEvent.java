package com.belman.domain.shared;

/**
 * Event that is published when the application goes to the background.
 * This event is triggered when the application is no longer visible to the user,
 * such as when the user switches to another app or returns to the home screen.
 * 
 * @deprecated This class is deprecated and will be removed in a future release.
 * Use {@link com.belman.domain.events.ApplicationBackgroundedEvent} instead.
 */
@Deprecated
public class ApplicationBackgroundedEvent extends com.belman.domain.events.ApplicationBackgroundedEvent {

    /**
     * Creates a new ApplicationBackgroundedEvent.
     */
    public ApplicationBackgroundedEvent() {
        super();
    }
}
