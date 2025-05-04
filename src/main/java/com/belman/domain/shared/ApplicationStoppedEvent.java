package com.belman.domain.shared;

/**
 * Event that is published when the application is stopped.
 * This event is triggered when the application is being terminated,
 * such as when the user closes the app or the system kills the process.
 * 
 * @deprecated This class is deprecated and will be removed in a future release.
 * Use {@link com.belman.domain.events.ApplicationStoppedEvent} instead.
 */
@Deprecated
public class ApplicationStoppedEvent extends com.belman.domain.events.ApplicationStoppedEvent {

    /**
     * Creates a new ApplicationStoppedEvent.
     */
    public ApplicationStoppedEvent() {
        super();
    }
}
